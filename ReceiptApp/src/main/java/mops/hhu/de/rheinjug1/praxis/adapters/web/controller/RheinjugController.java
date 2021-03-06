package mops.hhu.de.rheinjug1.praxis.adapters.web.controller;

import static java.util.stream.Collectors.toList;
import static mops.hhu.de.rheinjug1.praxis.adapters.auth.RolesHelper.ORGA;
import static mops.hhu.de.rheinjug1.praxis.adapters.auth.RolesHelper.STUDENTIN;
import static mops.hhu.de.rheinjug1.praxis.adapters.web.thymeleaf.ThymeleafAttributesHelper.ACCOUNT_ATTRIBUTE;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import mops.hhu.de.rheinjug1.praxis.adapters.web.thymeleaf.TemplateEvent;
import mops.hhu.de.rheinjug1.praxis.adapters.web.thymeleaf.TemplateEventInfo;
import mops.hhu.de.rheinjug1.praxis.domain.Account;
import mops.hhu.de.rheinjug1.praxis.domain.AccountFactory;
import mops.hhu.de.rheinjug1.praxis.domain.TimeFormatService;
import mops.hhu.de.rheinjug1.praxis.domain.chart.ChartService;
import mops.hhu.de.rheinjug1.praxis.domain.event.MeetupService;
import mops.hhu.de.rheinjug1.praxis.domain.submission.eventinfo.SubmissionEventInfoDomainRepository;
import mops.hhu.de.rheinjug1.praxis.enums.MeetupType;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
public class RheinjugController {

  private final Counter authenticatedAccess;
  private final Counter publicAccess;
  private final MeetupService meetupService;
  private final ChartService chartService;
  private final AccountFactory accountFactory;
  private final SubmissionEventInfoDomainRepository submissionEventInfoDomainRepository;
  private final TimeFormatService timeFormatService;

  @Autowired
  public RheinjugController(
      final MeterRegistry registry,
      final MeetupService meetupService,
      final ChartService chartService,
      final AccountFactory accountFactory,
      final SubmissionEventInfoDomainRepository submissionEventInfoDomainRepository,
      final TimeFormatService timeFormatService) {
    authenticatedAccess = registry.counter("access.authenticated");
    publicAccess = registry.counter("access.public");
    this.meetupService = meetupService;
    this.chartService = chartService;
    this.accountFactory = accountFactory;
    this.submissionEventInfoDomainRepository = submissionEventInfoDomainRepository;
    this.timeFormatService = timeFormatService;
  }

  @GetMapping("/")
  @Secured({"ROLE_orga", "ROLE_studentin"})
  public String home(final KeycloakAuthenticationToken token, final Model model) {
    if (token != null) {
      final Account account = accountFactory.createFromPrincipal(token);
      model.addAttribute(ACCOUNT_ATTRIBUTE, account);
    }
    final List<TemplateEvent> upcoming =
        meetupService.getEventsByStatus("upcoming").stream()
            .map(event -> new TemplateEvent(event, timeFormatService))
            .collect(toList());
    model.addAttribute("events", upcoming);
    publicAccess.increment();
    return "home";
  }

  @GetMapping("/user/events")
  @Secured(value = STUDENTIN)
  public String showAllEvents(final KeycloakAuthenticationToken token, final Model model) {

    final Account account = accountFactory.createFromPrincipal(token);
    model.addAttribute(ACCOUNT_ATTRIBUTE, account);

    final List<TemplateEventInfo> submissionEventInfos =
        submissionEventInfoDomainRepository.getAllEventsWithInfosByUserSorted(account).stream()
            .map(eventInfo -> new TemplateEventInfo(eventInfo, timeFormatService))
            .collect(toList());

    model.addAttribute("eventsWithInfos", submissionEventInfos);
    publicAccess.increment();
    return "/user/allEventsWithUpload";
  }

  @GetMapping("/admin/events")
  @Secured(value = ORGA)
  public String showAllPastEvents(final KeycloakAuthenticationToken token, final Model model) {

    final Account account = accountFactory.createFromPrincipal(token);
    model.addAttribute(ACCOUNT_ATTRIBUTE, account);

    final List<TemplateEvent> events =
        meetupService.getEventsByStatus("past").stream()
            .map(e -> new TemplateEvent(e, timeFormatService))
            .collect(toList());

    model.addAttribute("events", events);
    publicAccess.increment();
    return "/admin/pastEventsWithUpload";
  }

  @GetMapping("/logout")
  public String logout(final HttpServletRequest request) throws ServletException {
    request.logout();
    return "redirect:/";
  }

  @GetMapping("/statistics")
  @Secured(value = ORGA)
  public String getStatistics(
      final KeycloakAuthenticationToken token,
      final Model model,
      @RequestParam(name = "points", required = false) final Optional<String> datapoints) {
    if (token != null) {
      model.addAttribute(ACCOUNT_ATTRIBUTE, accountFactory.createFromPrincipal(token));
    }
    model.addAttribute("chart", chartService.getXEventsChart(datapoints));
    model.addAttribute(
        "numberEntwickelbarReceipts",
        String.valueOf(chartService.getNumberOfReceiptsByMeetupType(MeetupType.ENTWICKELBAR)));
    model.addAttribute(
        "numberRheinjugReceipts",
        String.valueOf(chartService.getNumberOfReceiptsByMeetupType(MeetupType.RHEINJUG)));
    return "admin/statistics";
  }

  @GetMapping({"/update/{page}", "/update"})
  public String update(@PathVariable(required = false) final String page) {
    meetupService.update();
    return page == null ? "redirect:/" : "redirect:/" + page;
  }
}
