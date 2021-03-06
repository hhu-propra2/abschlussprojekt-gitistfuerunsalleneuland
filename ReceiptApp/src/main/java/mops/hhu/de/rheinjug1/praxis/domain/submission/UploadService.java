package mops.hhu.de.rheinjug1.praxis.domain.submission;

import io.minio.errors.MinioException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import mops.hhu.de.rheinjug1.praxis.domain.Account;
import mops.hhu.de.rheinjug1.praxis.domain.event.Event;
import mops.hhu.de.rheinjug1.praxis.domain.event.EventNotFoundException;
import mops.hhu.de.rheinjug1.praxis.domain.submission.exception.DuplicateSubmissionException;
import org.springframework.web.multipart.MultipartFile;
import org.xmlpull.v1.XmlPullParserException;

public interface UploadService {
  void uploadAndSaveSubmission(Long meetupId, MultipartFile file, Account account)
      throws MinioException, XmlPullParserException, NoSuchAlgorithmException, InvalidKeyException,
          IOException, InterruptedException;

  Event checkUploadableAndReturnEvent(Long meetupId, Account account)
      throws EventNotFoundException, DuplicateSubmissionException;

  void checkUploadable(Long meetupId, String email)
      throws DuplicateSubmissionException, EventNotFoundException;

  void uploadAndSaveSubmission(Long meetupId, MultipartFile file, String name, String email)
      throws MinioException, XmlPullParserException, NoSuchAlgorithmException, InvalidKeyException,
          IOException;
}
