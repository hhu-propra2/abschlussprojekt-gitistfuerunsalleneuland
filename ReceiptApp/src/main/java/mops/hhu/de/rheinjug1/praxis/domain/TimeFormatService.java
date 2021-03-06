package mops.hhu.de.rheinjug1.praxis.domain;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import mops.hhu.de.rheinjug1.praxis.domain.event.Event;
import mops.hhu.de.rheinjug1.praxis.domain.submission.eventinfo.SubmissionEventInfo;
import org.joda.time.LocalDateTime;

public interface TimeFormatService {
  String getDatabaseDateTimePattern();

  String format(Duration duration);

  String toLocalEventTimeString(ZonedDateTime utcTime, ZoneId zoneId);

  boolean isInTheFuture(SubmissionEventInfo submissionEventInfo);

  boolean isUploadPeriodExpired(SubmissionEventInfo submissionEventInfo);

  boolean isInUploadPeriod(SubmissionEventInfo submissionEventInfo);

  String getGermanDateString(SubmissionEventInfo submissionEventInfo);

  String getGermanDateString(Event event);

  String getGermanDateTimeString(Event event);

  String getGermanTimeString(Event event);

  String extractDate(final String zonedDateTime);

  LocalDateTime getLocalDateTime(String dateTimeString);

  String getKeepAcceptedSubmissionsExpirationDate();

  LocalDate getLocalDate(String localDateString);
}
