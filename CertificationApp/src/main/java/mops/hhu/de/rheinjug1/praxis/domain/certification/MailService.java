package mops.hhu.de.rheinjug1.praxis.domain.certification;

import javax.mail.MessagingException;

public interface MailService {
  void sendMailWithAttachment(
      String to, String subject, String text, String pathToAttachment, String filename)
      throws MessagingException;
}
