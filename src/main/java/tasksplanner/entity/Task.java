package tasksplanner.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    @Getter
    @Setter
    @Column(name = "header", length = 60, nullable = false)
    private String header;

    @Getter
    @Setter
    @Column(name = "text", nullable = false)
    private String text;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "task_status", nullable = false)
    private TaskStatus taskStatus;

    @Getter
    @Setter
    @Column(name = "completion_time")
    private OffsetDateTime finishedAt;

    @Getter
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

