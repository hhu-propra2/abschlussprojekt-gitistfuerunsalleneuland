package mops.hhu.de.rheinjug1.praxis.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;

import mops.hhu.de.rheinjug1.praxis.domain.Account;
import mops.hhu.de.rheinjug1.praxis.domain.RheinjugCertificationData;
import mops.hhu.de.rheinjug1.praxis.domain.FormUserData;
import mops.hhu.de.rheinjug1.praxis.domain.InputHandler;
import mops.hhu.de.rheinjug1.praxis.interfaces.ReceiptReaderInterface;
import mops.hhu.de.rheinjug1.praxis.interfaces.ReceiptVerificationInterface;
import mops.hhu.de.rheinjug1.praxis.services.CertificationService;
import mops.hhu.de.rheinjug1.praxis.services.VerificationService;
import mops.hhu.de.rheinjug1.praxis.services.YamlReceiptReader;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static mops.hhu.de.rheinjug1.praxis.thymeleaf.ThymeleafAttributesHelper.*;

@Controller
@RequestMapping("/rheinjug1")
@SuppressWarnings({
  "PMD.UnusedPrivateField",
  "PMD.SingularField",
  "PMD.UnusedImports",
  "PMD.AvoidDuplicateLiterals"
})
public class CertificationController {

  private final Counter authenticatedAccess;
  private final CertificationService certificationService = new CertificationService();
  private final ReceiptReaderInterface fileReaderService = new YamlReceiptReader();
  private final ReceiptVerificationInterface verificationService = new VerificationService();

  public CertificationController(final MeterRegistry registry) {
    authenticatedAccess = registry.counter("access.authenticated");
  }

  private Account createAccountFromPrincipal(final KeycloakAuthenticationToken token) {
    final KeycloakPrincipal principal = (KeycloakPrincipal) token.getPrincipal();
    return new Account(
        principal.getName(),
        principal.getKeycloakSecurityContext().getIdToken().getEmail(),
        null,
        token.getAccount().getRoles());
  }

  @GetMapping("/")
  @Secured({"ROLE_studentin", "ROLE_orga"})
  public String home(final KeycloakAuthenticationToken token, final Model model) {
    if (token != null) {
      final Account account = createAccountFromPrincipal(token);
      model.addAttribute(ACCOUNT_ATTRIBUTE, account);
    }
    model.addAttribute(FORM_USER_DATA_ATTRIBUTE, new FormUserData());
    model.addAttribute(
        INPUT_ATTRIBUTE, new InputHandler(/*fileReaderService, verificationService*/ ));
    authenticatedAccess.increment();
    return "home";
  }

  @PostMapping("/")
  @Secured({"ROLE_student", "ROLE_orga"})
  public String submitReceipt(
      final KeycloakAuthenticationToken token,
      final Model model,
      final InputHandler inputHandler,
      final FormUserData formUserData)
          throws InvalidKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException,
          UnrecoverableEntryException, SignatureException, IOException, JAXBException, Docx4JException {

    final Account account = createAccountFromPrincipal(token);
    RheinjugCertificationData rheinjugCertificationData = new RheinjugCertificationData(formUserData);

    if (inputHandler.areRheinjugUploadsOkForCertification() && inputHandler.verifyRheinjug()) {
      rheinjugCertificationData.setEventTitles(inputHandler.getEventTitles());
      certificationService.createCertification(rheinjugCertificationData);
    }
    if (inputHandler.isEntwickelbarUploadOkForCertification()
        && inputHandler.verifyEntwickelbar()) {
      certificationService.createCertification(rheinjugCertificationData);
    }

    model.addAttribute(ACCOUNT_ATTRIBUTE, account);
    model.addAttribute(INPUT_ATTRIBUTE, inputHandler);
    authenticatedAccess.increment();
    return "home";
  }

  @GetMapping("/logout")
  public String logout(final HttpServletRequest request) throws ServletException {
    request.logout();
    return "redirect:/";
  }
}
