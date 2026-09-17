package tasksplanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tasksplanner.entity.Task;
import tasksplanner.entity.TaskStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    public static final String DELETE_TASK
            = """
            DELETE FROM Task t
            WHERE t.id = :taskId AND t.taskOwner.id = :userId
            """;
    public static final String FIND_ALL_FINISHED_TASKS
            = """
            SELECT t FROM Task t
            JOIN FETCH t.user
            WHERE t.status = :status
            AND t.finishedAt >= :from
            AND t.finished < :to
            """;

    public static final String FIND_ALL_UNFINISHED_TASKS
            = """
            SELECT t FROM Task t
            JOIN FETCH t.user
            WHERE t.status != :status
            """;

    Optional<Task> findByIdAndTaskOwner_Id(Long id, Long taskOwnerId);

    @Modifying
    @Query(DELETE_TASK)
    int deleteByTaskIdAndOwnerId(
            @Param("taskId") Long taskId,
            @Param("userId") Long userId
    );

    List<Task> findAllByTaskOwner_Id(Long userId);

    @Query(FIND_ALL_FINISHED_TASKS)
    List<Task> findAllFinishedTasksByOwners(
            @Param("status") TaskStatus status,
            @Param("from") Instant from,
            @Param("to") Instant to);

    @Query(FIND_ALL_UNFINISHED_TASKS)
    List<Task> findAllUnfinishedTasksByOwners(
            @Param("status") TaskStatus status
    );
}
