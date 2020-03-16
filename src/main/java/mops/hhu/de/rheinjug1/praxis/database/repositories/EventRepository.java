package mops.hhu.de.rheinjug1.praxis.database.repositories;

import java.util.List;
import mops.hhu.de.rheinjug1.praxis.database.entities.Event;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface EventRepository extends CrudRepository<Event, Long> {

  @Override
  @Query("SELECT * FROM rheinjug1.event")
  List<Event> findAll();

  @Query(
      "UPDATE `rheinjug1`.`event`\n"
          + "SET\n"
          + "`id` = :id,\n"
          + "`duration` = :duration,\n"
          + "`name` = :name,\n"
          + "`status` = :status,\n"
          + "`zoned_date_time` = :time,\n"
          + "`link` = :link,\n"
          + "`description` = :description\n"
          +
          // "`meetup_type` = :meetup_type\n" +
          "WHERE `id` = :id\n")
  @Transactional
  @Modifying
  void updateWithoutParticipantsCounter(
      @Param("id") Long id,
      @Param("duration") String duration,
      @Param("name") String name,
      @Param("status") String status,
      @Param("time") String time,
      @Param("link") String link,
      @Param("description") String description);

  @Transactional
  @Modifying
  @Query(
      "UPDATE rheinjug1.event SET event.participants = event.participants + 1 WHERE event.id = :id")
  void incremetParticipants(@Param("id") Long id);
}
