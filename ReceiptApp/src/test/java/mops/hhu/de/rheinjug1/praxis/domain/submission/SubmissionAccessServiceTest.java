package mops.hhu.de.rheinjug1.praxis.domain.submission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import mops.hhu.de.rheinjug1.praxis.adapters.auth.AccountImpl;
import mops.hhu.de.rheinjug1.praxis.domain.Account;
import mops.hhu.de.rheinjug1.praxis.domain.submission.exception.SubmissionNotFoundException;
import mops.hhu.de.rheinjug1.praxis.domain.submission.exception.UnauthorizedSubmissionAccessException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class SubmissionAccessServiceTest {
  @Autowired private SubmissionAccessService submissionAccessService;

  @MockBean private SubmissionRepository submissionRepository;

  private static final Account TEST_ACCOUNT =
      new AccountImpl("testName", "testEmail", "", new HashSet<>());

  @Test
  public void an_existent_submission_gets_accepted() throws SubmissionNotFoundException {
    final Optional<Submission> submissionOptional =
        Optional.of(Submission.builder().meetupId(0L).email("").name("").minIoLink("").build());

    when(submissionRepository.findById(0L)).thenReturn(submissionOptional);
    submissionAccessService.accept(0L);
    assertThat(submissionOptional.get().isAccepted()).isTrue();
  }

  @Test
  public void a_non_existent_submission_throws_exception() {
    when(submissionRepository.findById(0L)).thenReturn(Optional.empty());
    assertThrows(SubmissionNotFoundException.class, () -> submissionAccessService.accept(0L));
  }

  @Test
  public void throws_exception_if_submission_not_found() {
    when(submissionRepository.findById(0L)).thenReturn(Optional.empty());
    assertThrows(
        SubmissionNotFoundException.class,
        () -> submissionAccessService.getAcceptedSubmissionIfAuthorized(0L, TEST_ACCOUNT));
  }

  @Test
  public void returns_empty_optional_if_name_or_email_is_not_equal_to_account() {
    when(submissionRepository.findById(0L))
        .thenReturn(
            Optional.ofNullable(
                Submission.builder()
                    .email("testEmail")
                    .meetupId(0L)
                    .minIoLink("minio.com")
                    .name("anotherTestName")
                    .accepted(false)
                    .build()));
    when(submissionRepository.findById(1L))
        .thenReturn(
            Optional.ofNullable(
                Submission.builder()
                    .email("anotherTestEmail")
                    .meetupId(0L)
                    .minIoLink("minio.com")
                    .name("testName")
                    .accepted(false)
                    .build()));
    assertThrows(
        UnauthorizedSubmissionAccessException.class,
        () -> submissionAccessService.getAcceptedSubmissionIfAuthorized(0L, TEST_ACCOUNT));
    assertThrows(
        UnauthorizedSubmissionAccessException.class,
        () -> submissionAccessService.getAcceptedSubmissionIfAuthorized(1L, TEST_ACCOUNT));
  }

  @Test
  public void returns_submission_if_name_and_email_are_equal_to_account()
      throws SubmissionNotFoundException, UnauthorizedSubmissionAccessException {
    final Submission testSubmission =
        Submission.builder()
            .email("testEmail")
            .meetupId(0L)
            .minIoLink("minio.com")
            .name("testName")
            .accepted(false)
            .build();
    when(submissionRepository.findById(0L)).thenReturn(Optional.ofNullable(testSubmission));

    final Submission actualSubmission =
        submissionAccessService.getAcceptedSubmissionIfAuthorized(0L, TEST_ACCOUNT);
    assertEquals(testSubmission, actualSubmission);
  }
}
