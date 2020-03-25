 package mops.hhu.de.rheinjug1.praxis.services;

 import java.io.ByteArrayInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.Scanner;
 import mops.hhu.de.rheinjug1.praxis.domain.Receipt;
import mops.hhu.de.rheinjug1.praxis.exceptions.Base64Exception;
import mops.hhu.de.rheinjug1.praxis.interfaces.ReceiptReaderInterface;
 import org.bouncycastle.util.encoders.Base64;
 import org.springframework.stereotype.Service;
 import org.springframework.web.multipart.MultipartFile;
 import org.yaml.snakeyaml.Yaml;
 import org.yaml.snakeyaml.constructor.Constructor;

// wirft Fehler, FileReaderService nicht

 @Service
 public class YamlReceiptReader implements ReceiptReaderInterface {

  @Override
  public Receipt read(final MultipartFile base64ReceiptFile) throws IOException, Base64Exception {
    if (base64ReceiptFile == null) {
      return null;
    }
    boolean decoded = false;
    try (InputStream input = deCryptBase64(base64ReceiptFile); ) {
    	decoded = true;
      final Constructor constructor = new Constructor(Receipt.class);
      final Yaml yaml = new Yaml(constructor);
      final Receipt receipt = (Receipt) yaml.load(input);
      if (receipt == null || "".equals(receipt.getSignature())) {
        throw new IOException();
      } else {
        return receipt;
      }
    }
    finally { 
    	if (!decoded) {
    	   throw new Base64Exception("Error while decoding with Base64");
    	}
    }
  }

  private InputStream deCryptBase64(final MultipartFile base64ReceiptFile) throws IOException, Base64Exception {
    String receiptString = "";
    try (InputStream input = base64ReceiptFile.getInputStream();
        Scanner scanner = new Scanner(input).useDelimiter("\\A"); ) {
      receiptString = scanner.hasNext() ? scanner.next() : "";
    }
    final byte[] receiptBytes = Base64.decode(receiptString);
      return new ByteArrayInputStream(receiptBytes);
  }
 }
