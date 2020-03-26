package mops.hhu.de.rheinjug1.praxis.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.IOException;
import mops.hhu.de.rheinjug1.praxis.domain.Receipt;
import mops.hhu.de.rheinjug1.praxis.enums.MeetupType;
import org.bouncycastle.util.encoders.DecoderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
public class FileReaderServiceTests {

  private final MultipartFile validFile =
      new MockMultipartFile(
          "validFile",
          "ZW1haWw6IFRlc3RFbWFpbAptZWV0dXBJZDogMQptZWV0dXBUaXRsZTogVGl0ZWwKbWVldHVwVHlwZTogRU5UV0lDS0VMQkFSCm5hbWU6IFRlc3ROYW1lCnNpZ25hdHVyZTogT0VVSWM1NjU0ZXV0Cg=="
              .getBytes());

  private final MultipartFile invalidFile =
      new MockMultipartFile(
          "invalidFile",
          "ZW1haWw6IFRlc3RFbWFpbAptZWV0dXBJZDogMQptZWV0dXBUaXRsZTogVGl0ZWwKbWVldHVwVHlwZTogRU5UV0lDS0VMQkFSCm5hbWU6IFRc3ROYW1lCnNpZ25hdHVyZTogT0VVSWM1NjU0ZXV0Cg=="
              .getBytes());

  private final Receipt receipt = new Receipt();

  @Autowired private ReceiptReaderService receiptReader;

  @BeforeEach
  public void setReceipt() {
    receipt.setEmail("TestEmail");
    receipt.setName("TestName");
    receipt.setMeetupId((long) 1);
    receipt.setMeetupTitle("Titel");
    receipt.setMeetupType(MeetupType.ENTWICKELBAR);
    receipt.setSignature("OEUIc5654eut");
  }

  @Test
  public void validFileGenereatesCorrectReceipt() throws IOException {
    assertThat(receipt).isEqualTo(receiptReader.read(validFile));
  }

  @Test
  public void invalidFileDoesNotBecomeReceipt() throws IOException {
    assertThatExceptionOfType(DecoderException.class)
        .isThrownBy(
            () -> {
              receiptReader.read(invalidFile);
            });
  }

  @Test
  public void emptyFileNotReadable() throws IOException {
    final MockMultipartFile emptyFile = null;
    assertThatExceptionOfType(IOException.class)
        .isThrownBy(
            () -> {
              receiptReader.read(emptyFile);
            });
  }
}