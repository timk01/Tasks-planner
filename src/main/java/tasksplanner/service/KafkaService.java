package tasksplanner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tasksplanner.dto.EmailSendingTask;

@RequiredArgsConstructor
@Service
public class KafkaService {
    private static final String EMAIL_SENDING_TOPIC = "EMAIL_SENDING_TASKS";


    private final KafkaTemplate<String, EmailSendingTask> kafkaTemplate;

    public void sendMessage(EmailSendingTask dto) {
        kafkaTemplate.send(EMAIL_SENDING_TOPIC, dto);
    }
}
