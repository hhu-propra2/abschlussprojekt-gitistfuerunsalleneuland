package mops.hhu.de.rheinjug1.praxis.adapters.meetup;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import mops.hhu.de.rheinjug1.praxis.domain.Account;
import mops.hhu.de.rheinjug1.praxis.domain.event.Event;
import mops.hhu.de.rheinjug1.praxis.domain.event.EventNotFoundException;
import mops.hhu.de.rheinjug1.praxis.domain.event.EventRepository;
import mops.hhu.de.rheinjug1.praxis.domain.event.MeetupService;
import mops.hhu.de.rheinjug1.praxis.domain.submission.SubmissionRepository;
import mops.hhu.de.rheinjug1.praxis.domain.submission.eventinfo.SubmissionEventInfo;
import mops.hhu.de.rheinjug1.praxis.domain.submission.eventinfo.SubmissionEventInfoDomainRepository;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@SuppressWarnings({
  "PMD.UnusedPrivateField",
  "PMD.UnusedPrivateMethod",
  "PMD.AvoidDuplicateLiterals"
})
@EnableScheduling
@Service
@AllArgsConstructor
public class MeetupServiceImpl implements MeetupService {

  private final MeetupClient meetupClient;
  private final EventRepository eventRepository;
  final JdbcAggregateTemplate jdbcAggregateTemplate;
  private final SubmissionEventInfoDomainRepository submissionEventInfoDomainRepository;
  final SubmissionRepository submissionRepository;

  @PostConstruct
  private void initDatabase() {
    update();
  }

  @Override
  @Scheduled(cron = "0 0 8 * * ?")
  public void update() {
    final List<Event> meetupEvents = meetupClient.getAllEventsIfAvailable();
    final List<Event> allEvents = eventRepository.findAll();
    updateExistingEvents(meetupEvents, allEvents);
    insertNonExistingEvents(meetupEvents, allEvents);
  }

  @Override
  public List<SubmissionEventInfo> getAllEventsWithInfosByUser(final Account account) {
    return submissionEventInfoDomainRepository.getAllEventsWithInfosByUser(account);
  }

  @Override
  public int getSubmissionCount(final Event event) {
    return submissionRepository.countSubmissionByMeetupId(event.getId());
  }

  @Override
  public List<Event> getEventsByStatus(final String status) {
    switch (status) {
      case "upcoming":
        return filterEventsByStatus("upcoming");
      case "past":
        return filterEventsByStatus("past");
      case "all":
        return eventRepository.findAll();
      default:
        return new ArrayList<>();
    }
  }

  @Override
  public Event getEventIfExistent(final Long meetUpId) throws EventNotFoundException {
    final Optional<Event> eventOptional = eventRepository.findById(meetUpId);

    if (eventOptional.isEmpty()) {
      throw new EventNotFoundException(meetUpId);
    }

    return eventOptional.get();
  }

  private void updateExistingEvents(final List<Event> meetupEvents, final List<Event> allEvents) {
    meetupEvents.stream().filter(allEvents::contains).forEach(eventRepository::save);
  }

  private void insertNonExistingEvents(
      final List<Event> meetupEvents, final List<Event> allEvents) {
    meetupEvents.stream()
        .filter(i -> !allEvents.contains(i))
        .forEach(jdbcAggregateTemplate::insert);
  }

  private List<Event> filterEventsByStatus(final String status) {
    return eventRepository.findAll().stream()
        .filter(i -> i.getStatus().equals(status))
        .collect(Collectors.toList());
  }
}
