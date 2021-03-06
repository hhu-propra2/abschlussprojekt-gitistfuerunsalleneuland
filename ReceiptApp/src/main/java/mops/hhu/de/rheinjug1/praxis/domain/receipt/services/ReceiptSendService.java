package mops.hhu.de.rheinjug1.praxis.domain.receipt.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.mail.MessagingException;
import lombok.AllArgsConstructor;
import mops.hhu.de.rheinjug1.praxis.domain.receipt.entities.Receipt;
import mops.hhu.de.rheinjug1.praxis.domain.receipt.interfaces.MailService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ReceiptSendService {

  private final MailService mailService;
  private final ReceiptPrintService receiptPrintService;

  public void sendReceipt(final Receipt receipt, final String to)
      throws MessagingException, IOException {

    final String path = receiptPrintService.printReceiptAndReturnPath(receipt);
    final String mailText = Files.readString(Path.of("src/main/resources/mail/quittung.txt"));
    final String mailSubject = "Deine Quittung für " + receipt.getMeetupTitle();

    final String fileName =
        String.format(
            "%s%d-Quittung.yml", receipt.getMeetupType().getLabel(), receipt.getMeetupId());

    mailService.sendMailWithAttachment(to, mailSubject, mailText, path, fileName);
  }
}
