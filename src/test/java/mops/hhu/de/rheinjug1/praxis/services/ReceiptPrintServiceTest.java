package mops.hhu.de.rheinjug1.praxis.services;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import mops.hhu.de.rheinjug1.praxis.enums.MeetupType;
import mops.hhu.de.rheinjug1.praxis.models.Receipt;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReceiptPrintServiceTest {

  File file;
  Path path;

  @BeforeEach
  void createFile() {
    file = new File("src/main/resources/Titel-Quittung.txt");
    path = Paths.get("src/main/resources/Titel-Quittung.txt");
  }

  @Test
  void receiptGetsPrintedToFileInResources() throws IOException {
    final ReceiptPrintService receiptPrintService = new ReceiptPrintService();
    final Receipt receipt =
        new Receipt("Name", 1L, "Titel", MeetupType.ENTWICKELBAR, "OEUIc5654eut");

    receiptPrintService.printReceipt(receipt);

    final List<String> expected =
        Arrays.asList(
            "Name: Name",
            "Veranstaltungs-ID: 1",
            "Titel: Titel",
            "Typ: Entwickelbar",
            "OEUIc5654eut");
    final List<String> actual = Files.readAllLines(path);

    assertThat(actual).isEqualTo(expected);
  }

  @AfterEach
  void deleteFile() {
    file.delete();
  }
}