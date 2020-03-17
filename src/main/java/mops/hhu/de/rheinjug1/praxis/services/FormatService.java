package mops.hhu.de.rheinjug1.praxis.services;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class FormatService {

  public static String dateTimeFormat = "HH:mm - dd.MM.yyyy";

  public String format(final Duration duration) {
    return "" + duration.toHoursPart() + ":" + duration.toMinutesPart();
  }

  private ZonedDateTime toLocalEventTime(final ZonedDateTime utcTime, final ZoneId zoneId) {
    return utcTime.toOffsetDateTime().atZoneSameInstant(zoneId);
  }

  public String toLocalEventTimeString(final ZonedDateTime utcTime, final ZoneId zoneId) {
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);
    return toLocalEventTime(utcTime, zoneId).format(formatter);
  }

  public static String getDateTimePattern() {
    return dateTimeFormat;
  }
}