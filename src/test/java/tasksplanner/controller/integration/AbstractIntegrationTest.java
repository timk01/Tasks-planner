package tasksplanner.controller.integration;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.postgresql.PostgreSQLContainer;
import tasksplanner.repository.UserRepository;
import tasksplanner.request.UserRegisterRequest;
import tasksplanner.response.UserResponse;
import tools.jackson.databind.json.JsonMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AbstractIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected UserRepository userRepository;

    protected static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:15-alpine");


    static {
        postgres.start();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    protected JsonMapper jsonMapper;

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    protected RegisteredUser registerUser() throws Exception {
        String email = "tim" + System.currentTimeMillis() + "@mail.ru";
        String password = "sadfasfkljkjl22##";

        UserRegisterRequest dto =
                new UserRegisterRequest(email, password, password);

        MvcResult result = mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        HttpHeaders.AUTHORIZATION, org.hamcrest.Matchers.startsWith("Bearer ")
                ))
                .andReturn();

        UserResponse response = jsonMapper.readValue(
                result.getResponse().getContentAsString(),
                UserResponse.class
        );

        String authorization = result.getResponse()
                .getHeader(HttpHeaders.AUTHORIZATION);

        return new RegisteredUser(response.id(), response.email(), password, authorization);
    }

    protected record RegisteredUser(
            Long id,
            String email,
            String password,
            String authorization
    ) {
    }
}
