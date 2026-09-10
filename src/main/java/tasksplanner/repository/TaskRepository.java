package tasksplanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tasksplanner.entity.Task;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    public Optional<Task> findByIdAndTaskOwner_Id(Long id, Long taskOwnerId);
}
