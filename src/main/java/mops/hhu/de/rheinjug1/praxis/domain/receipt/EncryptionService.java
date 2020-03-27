package mops.hhu.de.rheinjug1.praxis.domain.receipt;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import mops.hhu.de.rheinjug1.praxis.enums.MeetupType;

public interface EncryptionService {
  String sign(MeetupType meetupType, long meetupId, String name, String email)
      throws NoSuchAlgorithmException, IOException, InvalidKeyException, KeyStoreException,
          UnrecoverableEntryException, CertificateException, SignatureException;
}