package mops.hhu.de.rheinjug1.praxis.controller;

import static mops.hhu.de.rheinjug1.praxis.domain.Account.createAccountFromPrincipal;
import static mops.hhu.de.rheinjug1.praxis.thymeleaf.ThymeleafAttributesHelper.ACCOUNT_ATTRIBUTE;
import static mops.hhu.de.rheinjug1.praxis.thymeleaf.ThymeleafAttributesHelper.INPUT_ATTRIBUTE;

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
import mops.hhu.de.rheinjug1.praxis.domain.Account;
import mops.hhu.de.rheinjug1.praxis.domain.InputHandler;
import mops.hhu.de.rheinjug1.praxis.interfaces.ReceiptReaderInterface;
import mops.hhu.de.rheinjug1.praxis.interfaces.ReceiptVerificationInterface;
import mops.hhu.de.rheinjug1.praxis.services.CertificationService;
import mops.hhu.de.rheinjug1.praxis.services.FileReaderService;
import mops.hhu.de.rheinjug1.praxis.services.VerificationService;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
  private final Counter publicAccess;
  private final CertificationService certificationService = new CertificationService();
  private final ReceiptReaderInterface fileReaderService = new FileReaderService();
  private final ReceiptVerificationInterface verificationService = new VerificationService();

  public CertificationController(final MeterRegistry registry) {
    authenticatedAccess = registry.counter("access.authenticated");
    publicAccess = registry.counter("access.public");
  }

  @GetMapping("/")
  @Secured({"ROLE_studentin", "ROLE_orga"})
  public String home(final KeycloakAuthenticationToken token, final Model model) {
    if (token != null) {
      final Account account = createAccountFromPrincipal(token);
      model.addAttribute(ACCOUNT_ATTRIBUTE, account);
    }
    model.addAttribute(
        INPUT_ATTRIBUTE,
        new InputHandler(fileReaderService, verificationService, fileReaderService));
    authenticatedAccess.increment();
    return "home";
  }

  @PostMapping("/")
  @Secured({"ROLE_student", "ROLE_orga"})
  public String submitReceipt(
      final KeycloakAuthenticationToken token, final Model model, final InputHandler input)
      throws InvalidKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException,
          UnrecoverableEntryException, SignatureException, IOException {

    if (input.areRheinjugUploadsOkForCertification() && input.verifyRheinjug()) {
      certificationService.createCertification(input);
    }
    // und sowas wie mailservice.send
    if (input.isEntwickelbarUploadOkForCertification() && input.verifyEntwickelbar()) {
      certificationService.createCertification(input);
    }

    final Account account = createAccountFromPrincipal(token);
    model.addAttribute(ACCOUNT_ATTRIBUTE, account);
    model.addAttribute(INPUT_ATTRIBUTE, input);
    authenticatedAccess.increment();
    return "home";
  }

  @GetMapping("/logout")
  public String logout(final HttpServletRequest request) throws ServletException {
    request.logout();
    return "redirect:/";
  }
}
