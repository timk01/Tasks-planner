package tasksplanner.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tasks")
@NoArgsConstructor
public class Task {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long id;

    @Column(name = "header", length = 60, nullable = false)
    private String header;

    @Column(name = "text", nullable = false)
    private String text;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "task_status", nullable = false)
    private TaskStatus taskStatus;

    @Column(name = "completion_time")
    private OffsetDateTime finishedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User taskOwner;

    public Task(String header, String text, TaskStatus taskStatus, OffsetDateTime finishedAt, User taskOwner) {
        this.header = header;
        this.text = text;
        this.taskStatus = taskStatus;
        this.finishedAt = finishedAt;
        this.taskOwner = taskOwner;
    }
}

