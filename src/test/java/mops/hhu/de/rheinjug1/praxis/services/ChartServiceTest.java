package mops.hhu.de.rheinjug1.praxis.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;
import mops.hhu.de.rheinjug1.praxis.database.entities.Event;
import mops.hhu.de.rheinjug1.praxis.database.repositories.EventRepository;
import mops.hhu.de.rheinjug1.praxis.database.repositories.SignatureRepository;
import mops.hhu.de.rheinjug1.praxis.enums.MeetupType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ChartServiceTest {

  private final List<Event> sampleData = new LinkedList<>();
  private MeetupService meetupServiceMock;
  private ChartService chartService;
  private EventRepository eventRepository;
  private SignatureRepository signatureRepository;

  @BeforeEach
  void init() {
    final String time = "12:30 - 12.03.2020";
    sampleData.add(Event.builder().id(1).zonedDateTime(time).meetupType(MeetupType.RHEINJUG).build());
    sampleData.add(Event.builder().id(2).zonedDateTime(time).meetupType(MeetupType.RHEINJUG).build());
    sampleData.add(Event.builder().id(3).zonedDateTime(time).meetupType(MeetupType.ENTWICKELBAR).build());
    this.meetupServiceMock = mock(MeetupService.class);
    this.eventRepository = spy(EventRepository.class);
    this.signatureRepository = mock(SignatureRepository.class);
    this.chartService =
        new ChartService(
            meetupServiceMock, eventRepository, signatureRepository, new TimeFormatService());
  }

  @Test
  void testNumberOfDataPoints() {
    // Arrange
    when(meetupServiceMock.getLastXEvents(Mockito.anyInt())).thenReturn(sampleData);
    // Act
    final int numberOfTalks = chartService.getXEventsChart(2).getTalksLength();
    // Assert
    assertThat(numberOfTalks).isEqualTo(sampleData.size());
  }
}