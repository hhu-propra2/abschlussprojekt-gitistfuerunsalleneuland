package mops.hhu.de.rheinjug1.praxis.domain.receipt;

import java.io.File;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

@Service
@RequiredArgsConstructor
public class ReceiptPrintService {

  private final FileHandler fileHandler;
  private final Encoder encoder;

  public String printReceiptAndReturnPath(final Receipt receipt) throws IOException {
    final String path = File.createTempFile("receipt", ".tmp").getAbsolutePath();

    final String yml = getYamlString(receipt);
    fileHandler.write(path, encoder.encode(yml));
    return path;
  }

  private String getYamlString(final Receipt receipt) {
    final Yaml yaml = new Yaml();
    return yaml.dumpAsMap(receipt.toDTO());
  }
}
