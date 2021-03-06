package mops.hhu.de.rheinjug1.praxis.adapters.minio;

import io.minio.MinioClient;
import io.minio.errors.MinioException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.xmlpull.v1.XmlPullParserException;

@SpringBootTest
public class MinIoUploadServiceTest {

  static MinioClient minioClient;

  @Autowired private MinIoUploadService uploadService;

  private static String bucketname = "rheinjug";

  @BeforeAll
  static void createMinioClient()
      throws MinioException, InvalidKeyException, NoSuchAlgorithmException, IOException,
          XmlPullParserException {
    minioClient = new MinioClient("http://localhost:9000/", "minio", "minio123");
    if (!minioClient.bucketExists(bucketname)) {
      minioClient.makeBucket(bucketname);
    }
  }

  @Test
  void uploadTestfile() throws Exception {
    final MultipartFile testMultipartFile =
        new MockMultipartFile(
            "test.txt", new FileInputStream(new File("src/test/resources/test.txt")));
    uploadService.transferMultipartFileToMinIo(testMultipartFile, "testfile");
    minioClient.statObject(bucketname, "testfile");
  }

  @AfterAll
  static void deleteTestfile()
      throws MinioException, InvalidKeyException, NoSuchAlgorithmException, IOException,
          XmlPullParserException {
    minioClient.removeObject(bucketname, "testfile");
  }
}
