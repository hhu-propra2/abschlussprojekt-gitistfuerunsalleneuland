package mops.hhu.de.rheinjug1.praxis.adapters.meetup.dto;

import java.time.ZoneId;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings({"PMD.FieldNamingConventions", "PMD.TooManyFields"})
@NoArgsConstructor
@Setter
public class GroupResponseDTO {
  private long created;
  private String name;
  private long id;
  private String join_mode;
  private double lat;
  private double lon;
  private String urlname;
  private String who;
  private String localized_location;
  private String state;
  private String country;
  private String region;
  private String timezone;

  public ZoneId getZoneId() {
    return ZoneId.of(this.timezone);
  }
}
