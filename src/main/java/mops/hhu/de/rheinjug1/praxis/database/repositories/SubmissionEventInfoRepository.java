package mops.hhu.de.rheinjug1.praxis.database.repositories;

import java.util.List;
import mops.hhu.de.rheinjug1.praxis.models.SubmissionEventInfo;
import mops.hhu.de.rheinjug1.praxis.models.SubmissionEventInfoRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SubmissionEventInfoRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;

  @Autowired
  public SubmissionEventInfoRepository(final NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public List<SubmissionEventInfo> getSubmissionInfoListByEmail(final String email) {
    final MapSqlParameterSource paramSource = new MapSqlParameterSource();
    paramSource.addValue("email", email);

    final String query =
        "SELECT e.name, e.link, e.meetup_type, e.zoned_date_time, s.id, s.meetup_id, s.min_io_link, s.accepted FROM rheinjug1.submission s INNER JOIN rheinjug1.event e ON s.meetup_id = e.id WHERE s.email = :email";

    final SubmissionEventInfoRowMapper rowMapper = new SubmissionEventInfoRowMapper();
    return jdbcTemplate.query(query, paramSource, rowMapper);
  }

  public List<SubmissionEventInfo> getAllEventsWithUserInfosByEmail(final String email) {
    final MapSqlParameterSource paramSource = new MapSqlParameterSource();
    paramSource.addValue("email", email);

    final String query =
        "SELECT e.name, e.link, e.meetup_type, e.zoned_date_time, s.id, e.id as meetup_id, s.min_io_link, s.accepted FROM rheinjug1.submission s RIGHT OUTER JOIN rheinjug1.event e ON s.meetup_id = e.id";

    final SubmissionEventInfoRowMapper rowMapper = new SubmissionEventInfoRowMapper();
    return jdbcTemplate.query(query, paramSource, rowMapper);
  }
}
