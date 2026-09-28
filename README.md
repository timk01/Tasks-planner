# Task Planner

Основной сервис приложения для управления пользовательскими задачами (служит центральным сервисом в схеме взаимодействия с другими сервисами — см. схему чуть ниже).

Отдельно Task Planner отвечает за регистрацию и аутентификацию пользователей, CRUD задач, хранение данных в PostgreSQL и предоставление Scheduler данных для ежедневных summary-отчётов.

Репозиторий также содержит общий `compose.yml`, позволяющий запустить весь проект из готовых Docker-образов (см. соответствующий раздел).

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
- Docker
- Docker Compose

## Основные возможности

- регистрация и вход пользователей;
- аутентификация (JWT-токен);
- создание, получение, обновление и удаление задач;
- хранение пользователей и задач (PostgreSQL);
- миграции базы данных (Flyway);
- отправка события для приветственного email в Kafka после регистрации;
- предоставление Scheduler завершённых и незавершённых задач пользователей за заданный период;
- участие в общей цепочке формирования ежедневного summary-отчёта.

## Связь с другими сервисами

### Приветственное письмо при регистрации

```text
                    ┌──────────────┐
                    │  PostgreSQL  │
                    └──────▲───────┘
                           │
                           │
┌──────┐   HTTP request┌──────────────┐
│ User │ ──────────►   │ Task Planner │
│      │ ◄──────────── │              │
└──────┘ HTTP response └──────────────┘
                           │
                           │ Kafka:
                           │ EMAIL_SENDING_TASKS
                           ▼
                    ┌──────────────┐
                    │ Email Sender │
                    └──────────────┘
```

### Формирование ежедневного summary

```text
                                      ┌───────────────────────┐                   ┌──────────┐
                                      │ Summarization Service │ ────────────────► │ GigaChat │
                                      │                       │    request        │          │
                                      │                       │ ◄──────────────── │          │
                                      └──────────┬────▲───────┘    response       └──────────┘
                                                 │    │
                                     Kafka reply │    │ Kafka request
                                  SummaryResponse│    │ SummarizationRequest
                                                 ▼    │
┌──────────────┐       HTTP request          ┌───────────┐
│ Task Planner │ ◄────────────────────────── │ Scheduler │
│              │ ──────────────────────────► │           │
└──────────────┘       tasks / users         └─────┬─────┘
                                                   │
                                                   │ Kafka:
                                                   │ SUMMARY_SENDING_TASKS
                                                   ▼
                                           ┌──────────────┐
                                           │ Email Sender │
                                           └──────────────┘
```

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

### Scheduler ---> Task Planner

```text
GET /tasks/getScheduledTasks?from={Instant}&to={Instant}
```

Для доступа используются переменные окружения:

```env
SCHEDULER_AUTH_HEADER=...
SCHEDULER_AUTH_KEY=...
```

Значения должны совпадать с конфигурацией Scheduler.

## Запуск всего проекта через Docker Compose

Проект можно полностью запустить через Docker Compose без локальной сборки отдельных сервисов.

`compose.yml` поднимает:

- Task Planner;
- Frontend;
- Scheduler;
- Summarization Service;
- Email Sender;
- PostgreSQL;
- Kafka.

Образы приложений загружаются из Docker Hub.

ВАЖНО: Перед запуском ОБЯЗАТЕЛЬНО создать `.env` на основе `.env.example` и заполнить необходимые параметры и секреты.

`.env.example` содержит настройки для:

- PostgreSQL;
- Kafka;
- JWT;
- взаимодействия Task Planner и Scheduler;
- Summarization Service;
- Email Sender.

После заполнения `.env`:

```bash
docker compose pull
docker compose up -d
```

Frontend будет доступен по адресу:

```text
http://localhost:5173
```

Для остановки стека используйте `docker compose down`.

## Локальная разработка

Аналогичный подход предполагается использовать для разработки остальных сервисов: контейнер нужного сервиса останавливается, после чего сервис запускается локально из своего репозитория с соответствующими переменными окружения.

Например, для локальной разработки Task Planner:

1. Поднять весь стек:

```bash
docker compose up -d
```

2. Остановить контейнер Task Planner:

```bash
docker compose stop task-planner
```

3. Запустить Task Planner локально из IDE или через Gradle:

```bash
./gradlew bootRun
```

При локальном запуске Task Planner использует опубликованные на localhost порты инфраструктуры:

```text
PostgreSQL -> localhost:5433
Kafka      -> localhost:9094
```

При запуске внутри Docker Compose используются адреса сервисов Docker-сети:

```text
PostgreSQL -> db:5432
Kafka      -> kafka:9092
```

## Переменные окружения

Полный список переменных и примеры значений находятся в:

```text
.env.example
```

Для локального запуска через IDE часть настроек имеет localhost fallback в `application.yml`. Секреты и обязательные значения необходимо передать через environment variables или конфигурацию запуска IDE.

Пример основных переменных Task Planner:

```env
POSTGRES_USER=...
POSTGRES_PASSWORD=...
POSTGRES_DB=...

JWT_SECRET=...

SCHEDULER_AUTH_HEADER=...
SCHEDULER_AUTH_KEY=...
```

## Тесты

Проект содержит:

- unit-тесты;
- WebMvc-тесты;
- integration-тесты;
- проверки JWT/security;
- интеграционные проверки работы с PostgreSQL;
- отдельный интеграционный сценарий с Kafka через Testcontainers.

Для запуска тестов необходим работающий Docker daemon, так как интеграционные тесты используют Testcontainers.

Запуск:

```bash
./gradlew test
```

HTTP-сценарии для ручной проверки находятся в директории (лучше запускать последовательно блоками):

```text
requests/
```
