package mops.hhu.de.rheinjug1.praxis.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Receipt {

  private String name;
  private String email;
  private long keycloakId;
  private long meetupId;
  private String meetupTitle;
  private String meetupType; // sollte ein enum sein
  private String signature;

  public Receipt cloneThisReceipt() {
    return new Receipt(name, email, keycloakId, meetupId, meetupTitle, meetupType, signature);
  }
}
