package mops.hhu.de.rheinjug1.praxis.adapters.web;

import static mops.hhu.de.rheinjug1.praxis.TestHelper.sampleEntwickelbarReceipt;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import mops.hhu.de.rheinjug1.praxis.domain.receipt.services.ReceiptPrintService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
public class InputHandlerTests {

  private static final String KEINE_DATEI = "Keine Datei";
  private static final String FALSCHE_VERANSTALTUNG = "Falsche Veranstaltung";
  private static final String WRONG_RECEIPT_TYPE_WASNT_RECOGNIZED =
      "wrong Receipt Type wasnt recognized";
  private static final String BAD_FILE_WASNT_RECOGNIZED = "bad File wasnt recognized";
  private static final String VALIDE = "Valide";
  private static final String VALID_FILE_WASNT_RECOGNIZED = "valid File wasnt recognized";

  @Autowired InputHandler handler;
  @Autowired ReceiptPrintService receiptPrintService;

  private MultipartFile validEntwickelbarFile;
  private final MultipartFile invalidFile = new MockMultipartFile("invalidFile", "".getBytes());

  @BeforeEach
  void init() throws IOException, SignatureException, InvalidKeyException {
    final File file = receiptPrintService.printReceiptAndGetFile(sampleEntwickelbarReceipt());
    validEntwickelbarFile = new MockMultipartFile("validFile", new FileInputStream(file));
  }

  @AfterEach
  public void resetHandler() {
    handler.resetSignatures();
  }

  @Test
  public void isFirstUploadMessageFalscheVeranstaultung() {
    handler.setFirstRheinjugReceipt(validEntwickelbarFile);
    assertEquals(
        WRONG_RECEIPT_TYPE_WASNT_RECOGNIZED,
        FALSCHE_VERANSTALTUNG,
        handler.getFirstRheinjugReceiptUploadMessage());
  }

  @Test
  public void isFirstUploadMessageKeineDatei() {
    handler.setFirstRheinjugReceipt(invalidFile);
    assertEquals(
        BAD_FILE_WASNT_RECOGNIZED, KEINE_DATEI, handler.getFirstRheinjugReceiptUploadMessage());
  }

  @Test
  public void isSecondUploadMessageKeineDatei() {
    handler.setSecondRheinjugReceipt(validEntwickelbarFile);
    assertEquals(
        WRONG_RECEIPT_TYPE_WASNT_RECOGNIZED,
        FALSCHE_VERANSTALTUNG,
        handler.getSecondRheinjugReceiptUploadMessage());
  }

  @Test
  public void isSeccondUploadMessageKeineDatei() {
    handler.setSecondRheinjugReceipt(invalidFile);
    assertEquals(
        BAD_FILE_WASNT_RECOGNIZED, KEINE_DATEI, handler.getSecondRheinjugReceiptUploadMessage());
  }

  @Test
  public void isThirdUploadMessageFalscheVeranstaultung() {
    handler.setThirdRheinjugReceipt(validEntwickelbarFile);
    assertEquals(
        WRONG_RECEIPT_TYPE_WASNT_RECOGNIZED,
        FALSCHE_VERANSTALTUNG,
        handler.getThirdRheinjugReceiptUploadMessage());
  }

  @Test
  public void isThirdUploadMessageKeineDatei() {
    handler.setThirdRheinjugReceipt(invalidFile);
    assertEquals(
        BAD_FILE_WASNT_RECOGNIZED, KEINE_DATEI, handler.getThirdRheinjugReceiptUploadMessage());
  }

  @Test
  public void isEntwickelbarUploadMessageKeineDatei() {
    handler.setEntwickelbarReceipt(invalidFile);
    assertEquals(
        BAD_FILE_WASNT_RECOGNIZED, KEINE_DATEI, handler.getEntwickelbarReceiptUploadMessage());
  }

  @Test
  public void isEntwickelbarUploadMessageValide() {
    handler.setEntwickelbarReceipt(validEntwickelbarFile);
    assertEquals(
        VALID_FILE_WASNT_RECOGNIZED, VALIDE, handler.getEntwickelbarReceiptUploadMessage());
  }
}
