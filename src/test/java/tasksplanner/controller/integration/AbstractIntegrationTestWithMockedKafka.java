package tasksplanner.controller.integration;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tasksplanner.service.KafkaService;

public abstract class AbstractIntegrationTestWithMockedKafka
        extends AbstractIntegrationTest {

    @MockitoBean
    protected KafkaService kafkaService;
}