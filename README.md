# Secure REST API — лабораторная работа 1

Учебное защищённое REST API на Java 21 и Spring Boot 4.1.1 для лабораторной работы по информационной безопасности.

Цель работы — реализовать API с аутентификацией, защитой от SQL Injection и XSS, а также подключить автоматические SAST/SCA-проверки в GitHub Actions.

## Стек и архитектура

* Java 21, Spring Boot 4.1.1, Spring Web, Spring Security 7;
* PostgreSQL 16 и Spring Data JPA/Hibernate;
* JWT для аутентификации и BCrypt для хэширования паролей;
* Jsoup для очистки пользовательского ввода;
* Maven, Docker Compose, SpotBugs и OWASP Dependency-Check.

Код разделён по логическим слоям:

* `controller` — HTTP endpoints и валидация входных DTO;
* `service` — бизнес-логика регистрации, входа и работы с данными;
* `repository` — доступ к PostgreSQL через Spring Data JPA;
* `entity` — JPA-сущности базы данных;
* `dto` — объекты запросов и ответов API;
* `security` — JWT-фильтр, JWT-сервис, BCrypt и конфигурация Spring Security;
* `config` — начальное заполнение базы данных.

## Запуск в Docker

```bash
docker compose up --build
```

API будет доступен на `http://localhost:8080`.

PostgreSQL из хоста доступен на порту `5433` (внутри Docker-сети база слушает стандартный `5432`).

Демо-пользователь: `demo` / `ChangeMe123!`.

## API

### Регистрация

```http
POST /auth/register
Content-Type: application/json
```

```json
{
  "username": "student",
  "password": "StrongPassword123!"
}
```

### Аутентификация

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"ChangeMe123!"}'
```

Ответ содержит JWT в поле `token`. Для защищенных методов передайте его так:

```text
Authorization: Bearer <token>
```

### Защищенные методы

| Метод | Endpoint | JWT | Назначение |
|---|---|---:|---|
| `POST` | `/auth/register` | Нет | Регистрация пользователя |
| `POST` | `/auth/login` | Нет | Проверка логина и выдача JWT |
| `GET` | `/api/data` | Да | Получение защищённых данных |
| `POST` | `/api/data` | Да | Создание записи (`title`, `content`) |
| `GET` | `/api/me` | Да | Профиль текущего пользователя |

Без JWT защищенные методы отвечают `401 Unauthorized`.

## Реализованные меры защиты

* SQL Injection: доступ к БД выполняется через Spring Data JPA/Hibernate и параметризованные операции репозиториев; SQL-конкатенация не используется.
* XSS: пользовательские поля очищаются `Jsoup` с `Safelist.none()` перед сохранением и повторно перед возвратом в ответе.
* JWT: после успешного входа сервер выдаёт подписанный HMAC-токен. `JwtAuthenticationFilter` проверяет токен на защищённых endpoints.
* Пароли: хранятся только в виде BCrypt-хэшей с work factor `12`.
* Stateless security: серверные HTTP-сессии отключены, доступ контролируется Spring Security.
* Валидация: обязательность и длина полей проверяются через Jakarta Validation.
* Конфигурация: секрет JWT и параметры БД задаются через переменные окружения.

## Проверка

### SAST: SpotBugs

SpotBugs подключён как Maven SAST-инструмент и запускается автоматически на фазе `verify`:

```bash
mvn clean verify
```

Плагин анализирует скомпилированный Java-байткод. При обнаружении проблем сборка завершается с ошибкой.

### SCA: OWASP Dependency-Check

OWASP Dependency-Check подключён как SCA-инструмент. Он проверяет зависимости Maven по базе известных CVE и создаёт отчёты в `target/dependency-check`:

```bash
mvn clean verify
```

Dependency-Check формирует отчёты даже при найденных CVE. Основные отчёты: `dependency-check-report.html`, `dependency-check-report.json` и `dependency-check-report.xml`. Порог блокировки сборки временно установлен в `11`, потому что исправляющие версии части CVE ещё недоступны в Maven Central.

1. Вызов `/api/data` без `Authorization` должен вернуть `401`.
2. Вызов `/auth/login` с неверным паролем не должен выдать токен.
3. При создании записи с `<script>alert(1)</script>` в ответе не должно быть HTML-тега.

Готовая упрощённая Postman-коллекция находится в файле
`postman/Secure REST API.postman_collection.json`. Все запросы используют
непосредственные адреса `http://localhost:8080` и не содержат переменных окружения,
скриптов или заголовков авторизации.

## GitHub Actions CI/CD

Workflow находится в `.github/workflows/ci-cd.yml` и выполняет:

* сборку и тесты на Java 21;
* SAST-проверку SpotBugs;
* SCA-проверку OWASP Dependency-Check;
* публикацию SAST/SCA-отчётов как GitHub Actions artifacts;
* сохранение результатов сборки и отчётов из каталога `target` как GitHub Actions artifacts.

Для ускорения обновления базы NVD можно добавить в настройках репозитория секрет `NVD_API_KEY`. Без него проверка также работает, но обновление базы занимает дольше.

На каждом `push` и при создании pull request запускается CI. Docker-образ в GitHub Actions не собирается и не публикуется; workflow сохраняет результаты Maven-сборки и отчёты из каталога `target`.

## Проверка API и материалы для сдачи

Минимальная проверка:

1. Вызвать `GET /api/data` без `Authorization` и получить `401 Unauthorized`.
2. Выполнить `/auth/login` с неверным паролем и убедиться, что JWT не выдан.
3. Выполнить login и вызвать защищённый endpoint с `Authorization: Bearer <token>`.
4. Создать запись с `<script>alert(1)</script>` и убедиться, что HTML-тег очищен.

Для сдачи лабораторной работы подготовить:

* ссылку на публичный GitHub-репозиторий;
* README с описанием API и мер защиты;
* ссылку на последний успешный запуск GitHub Actions;
* скриншот SpotBugs из Actions/Artifacts;
* скриншот HTML-отчёта OWASP Dependency-Check;
* скриншоты Postman или curl, подтверждающие login, JWT и отказ без токена.

В отчёте отдельно объяснить защиту от SQL Injection и XSS, работу JWT и BCrypt, а также различие между SAST и SCA.

## Ограничения учебной конфигурации

Демо-пользователь и пароль предназначены только для локального запуска. Для production необходимо использовать секреты Docker/GitHub и не хранить их в compose-файле. Dependency-Check продолжает показывать найденные CVE в отчёте; его порог блокировки временно установлен в `11`, поскольку часть исправлений ещё не опубликована в Maven Central.
