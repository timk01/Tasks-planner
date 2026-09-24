# Task Planner

Основной сервис приложения для управления пользовательскими задачами.

Task Planner отвечает за регистрацию и аутентификацию пользователей, CRUD задач, хранение данных в PostgreSQL и предоставление Scheduler данных для ежедневных summary-отчётов.

## Основные возможности

- регистрация и вход пользователей;
- JWT-аутентификация;
- создание, получение, обновление и удаление задач;
- хранение пользователей и задач в PostgreSQL;
- миграции базы данных через Flyway;
- отправка приветственного email-события в Kafka после регистрации;
- предоставление Scheduler завершённых и незавершённых задач пользователей за заданный период.

## Связь с другими сервисами

```text
User
→ Task Planner
→ PostgreSQL

Task Planner
→ EMAIL_SENDING_TASKS
→ Email Sender

Scheduler
→ GET /tasks/getScheduledTasks
→ Task Planner
```

После регистрации Task Planner публикует `EmailSendingTask` в Kafka-топик:

```text
EMAIL_SENDING_TASKS
```

Email Sender использует это сообщение для отправки приветственного письма.

Scheduler получает данные для ежедневного отчёта через:

```text
GET /tasks/getScheduledTasks?from={Instant}&to={Instant}
```

Этот endpoint защищён отдельным scheduler key и недоступен обычному пользователю.

## API

### Пользователь

```text
POST /user
POST /auth/login
GET  /user
```

При регистрации и успешном входе JWT возвращается в заголовке:

```text
Authorization: Bearer <token>
```

### Задачи

```text
POST   /tasks
GET    /tasks
PATCH  /tasks/{taskId}
DELETE /tasks/{taskId}
```

Операции с задачами требуют валидный JWT.

### Scheduler

```text
GET /tasks/getScheduledTasks?from={Instant}&to={Instant}
```

Для доступа используются значения:

```env
SCHEDULER_AUTH_HEADER=...
SCHEDULER_AUTH_KEY=...
```

Значения должны совпадать с конфигурацией Scheduler.

## Локальная инфраструктура

Текущий `compose.yml` поднимает:

- PostgreSQL;
- Kafka.

Перед запуском необходимо создать `.env` на основе `.env.example`.

Основные переменные окружения:

```env
POSTGRES_USER=...
POSTGRES_PASSWORD=...
POSTGRES_DB=...

JWT_SECRET=...

SCHEDULER_AUTH_HEADER=...
SCHEDULER_AUTH_KEY=...
```

Kafka-параметры также задаются через `.env.example`.

Запуск инфраструктуры:

```bash
docker compose up -d
```

Запуск приложения:

```bash
./gradlew bootRun
```

## Технологии

- Java 21
- Spring Boot
- Spring MVC
- Spring Security
- JWT
- Spring Data JPA
- PostgreSQL
- Flyway
- Spring Kafka
- MapStruct
- Lombok
- Gradle
- JUnit 5
- Mockito
- Testcontainers

## Тесты

Проект содержит unit-, WebMvc- и integration-тесты для основной бизнес-логики, REST API, JWT/security и работы с PostgreSQL.

HTTP-сценарии для ручной проверки находятся в директории:

```text
requests/
```
