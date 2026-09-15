package tasksplanner.controller.integration.user;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.kafka.KafkaContainer;
import tasksplanner.controller.integration.AbstractIntegrationTest;
import tasksplanner.dto.EmailSendingTask;
import tasksplanner.entity.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class UserKafkaIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private PasswordEncoder encoder;

    protected static KafkaContainer kafka =
            new KafkaContainer("apache/kafka-native:3.8.0");


    static {
        kafka.start();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.kafka.bootstrap-servers",
                kafka::getBootstrapServers
        );
    }

    @Test
    public void registerUserIsSucceeded() throws Exception {
        Properties properties = prepareProperties();

        try (KafkaConsumer<String, String> consumer =
                     new KafkaConsumer<>(properties)) {

            consumer.subscribe(List.of("EMAIL_SENDING_TASKS"));

            RegisteredUser registeredUser = registerUser();

            ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofSeconds(5));

            ConsumerRecord<String, String> record =
                    records.iterator().next();

            String json = record.value();

            EmailSendingTask emailTask =
                    jsonMapper.readValue(json, EmailSendingTask.class);

            assertThat(emailTask.recipient()).isEqualTo(registeredUser.email());

            assertThat(emailTask.subject()).isEqualTo("greetings");

            assertThat(emailTask.text()).isEqualTo("Welcome aboard!");
        }
    }

    private Properties prepareProperties() {
        Properties properties = new Properties();

        properties.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafka.getBootstrapServers()
        );
        properties.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "test-consumer"
        );
        properties.put(
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                "earliest"
        );
        properties.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );
        properties.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );
        return properties;
    }
}
