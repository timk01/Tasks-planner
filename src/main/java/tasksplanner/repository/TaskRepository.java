package tasksplanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tasksplanner.entity.Task;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByIdAndTaskOwner_Id(Long id, Long taskOwnerId);

    @Modifying
    @Query("DELETE FROM Task t WHERE t.id = :taskId AND t.taskOwner.id = :userId")
    int deleteByTaskIdAndOwnerId(@Param("taskId") Long taskId, @Param("userId")Long userId);
}
