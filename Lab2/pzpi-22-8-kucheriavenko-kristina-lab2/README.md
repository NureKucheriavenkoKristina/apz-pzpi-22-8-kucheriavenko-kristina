# API Документація для проєкту "Програмна система для управлінням медичними біологічними матеріалами"

API забезпечує функціональність для управління медичними біологічними матеріалами. Він дозволяє реєструвати, відстежувати та аналізувати матеріали, забезпечуючи інтеграцію з IoT пристроями для моніторингу умов зберігання.

## Встановлення та налаштування

### Передумови

Для взаємодії з проєктом необхідно встановити: 
- [Java](https://www.java.com/en/) (версія 21 та вище)
- [Apache Maven](https://maven.apache.org/) (версія 4.0 та вище)
- [PostgreSQL](https://www.postgresql.org/) (версія 17 та вище)
- [Spring Framework](https://spring.io/) (3.4.0 та вище)
- [Project Lombok](https://projectlombok.org/) (1.18 та вище)
- [IntelliJ IDEA](https://www.jetbrains.com/idea/)(не обов'язково) використовується як альтернативний спосіб запуску серверу
- [MQTT Explorer](https://mqtt-explorer.com/)для взаємодії з IOT пристроями

### Крок 1: Клонування репозиторію

Виконати клонування репозиторію на локальний комп'ютер:

git clone https://github.com/NureKucheriavenkoKristina/arkpz-pzpi-22-8-kucheriavenko-kristina/tree/22bef0682379c628666cd4cd298504c8a114fb73/Task5/arkpz-pzpi-22-8-kucheriavenko-kristina-task5

### Крок 2: Створення бази даних

Створити базу даних з назвою "system-for-managing-medical-biological-materials". При першому запуску проєкту таблиці автоматично створяться в базі даних з вже існуючими записами в таблицях. Після чого треба видалити DatabaseBackupImporter.java, щоб не виконувати імпорт БД кожен раз при запуску системи. 
Якщо ж потрібна пуста БД, то перед першим запуском необхідно видалити DatabaseBackupImporter.java. Таблиці автоматично створюються під час першого запуску.

### Крок 3: Запуск проекту

В main/resources/application.properties змінити username та password на свої власні дані, які використовуються під час створення бази даних. 

В url змінити порт на свій (встановлюється під час інсталяції PostgreSQL). 

У файлі main/java/com/BiologicalMaterialsSystem/config/SecurityConfig.java змінити username та password на власний. Використовується для доступу до сервера. 

В main/java/com/BiologicalMaterialsSystem/config/MqttConfig.java змінити MQTT_BROKER_URL, CLIENT_ID відповідно до своїх значень. MQTT_BROKER_URL - посилання на сервер брокеру, CLIENT_ID - айді клієнту у брокері. 

1 спосіб. Перейдіть у кореневу директорію проєкту та виконайте команду запуску сервера:
cd ...\BiologicalMaterialsSystem\src\main\java\com\BiologicalMaterialsSystem
mvn exec:java -Dexec.mainClass="com.BiologicalMaterialsSystem.BiologicalMaterialsSystemApplication".

2 спосіб. Завантажити застосунок IntelliJ IDEA, відкрити папку з проєктом, відкрити файл BiologicalMaterialsSystemApplication.java та запустити його. 

### Крок 4: Доступ до API

Для доступу до АРІ треба знати свій порт. Посилання: "http://localhost:8080/", 8080 - треба змінити.

### Крок 5: Доступ до ІОТ

Для доступу до ІОТ необхідно знати дані брокера (див. крок 3). Далі на веб-сторінку Wokwi (https://wokwi.com/) завантажити папку з назвою "IoT". Змінити порт, сервер, назву підключення mqtt, логін та пароль на власні значення. 
Щоб дані надходили до бази даних, необхідно відкрити  MQTT Explorer. Створити підключення з власними даними та внести їх до коду. Після чого запустити основний проєкт, а потім проєкт з ІОТ. 

## Контролери 

# BiologicalMaterialController API

## Маршрути

### Add Biological Material

**POST** '/api/biological-materials/admin/{userId}/add'

#### Опис

Додає новий біологічний матеріал від імені адміністратора.

#### Параметри запиту
Path:
- userId (Long): Ідентифікатор адміністратора, що виконує дію.

Body:
- materialName (String): Назва біологічного матеріалу.
- expirationDate (Date): Дата закінчення терміну придатності.
- status (String): Статус донорства.
- transferDate (Date): Дата передачі матеріалу.
- donorID (Long): Ідентифікатор донора.

#### Відповідь 

- 200 OK:
  - BiologicalMaterial (object): Об'єкт створеного матеріалу.
- 400 Bad Request:
  - message (string): Повідомлення про помилку у вхідних даних.
- 403 Forbidden:
  - message (string): Відсутність дозволу на виконання дії.
- 404 Not Found:
  - message (string): Адміністратора не знайдено.

### Get Biological Material By ID

**GET** '/api/biological-materials/{materialID}'

#### Опис

Отримує біологічний матеріал за його унікальним ідентифікатором.

#### Параметри запиту
Path:
- materialID (Long): Ідентифікатор матеріалу.

#### Відповідь 

- 200 OK:
  - BiologicalMaterial (object): Біологічний матеріал за вказаним ідентифікатором.
- 404 Not Found:
  - message (string): Повідомлення про те, що матеріал не знайдено.

### Get All Biological Materials

**GET** '/api/biological-materials'

#### Опис

Отримує всі біологічні матеріали.

#### Параметри запиту
Path:
- materialID (Long): Ідентифікатор матеріалу.

#### Відповідь 

- 200 OK:
  - List<BiologicalMaterial> (Array): Список усіх біологічних матеріалів.


### Update Biological Material

**PUT** '/api/biological-materials/admin/{userId}/{materialID}'

#### Опис

Оновлює існуючий біологічний матеріал від імені адміністратора.

#### Параметри запиту
Path:
- userId (Long): Ідентифікатор адміністратора, що виконує дію.
- materialID (Long): Ідентифікатор матеріалу, який потрібно оновити.
  
Body:
- materialName (String): Назва біологічного матеріалу.
- expirationDate (Date): Дата закінчення терміну придатності.
- status (String): Статус донорства.
- transferDate (Date): Дата передачі матеріалу.
- donorID (Long): Ідентифікатор донора.

#### Відповідь 

- 200 OK:
  - BiologicalMaterial (Object): Оновлений біологічний матеріал.
- 400 Bad Request:
  - message (String): Повідомлення про помилку у вхідних даних.
- 403 Forbidden:
  - message (String): Відсутність дозволу на виконання дії.
- 404 Not Found:
  - message (String): Повідомлення про те, що матеріал не знайдено.

### Delete Biological Material

**DELETE** '/api/biological-materials/admin/{userId}/{materialID}'

#### Опис

Видаляє біологічний матеріал від імені адміністратора.

#### Параметри запиту
Path:
- userId (Long): Ідентифікатор адміністратора, що виконує дію.
- materialID (Long): Ідентифікатор матеріалу, який потрібно видалити.

#### Відповідь 

- 204 No Content:
  - Видалення матеріалу відбулося успішно.
- 404 Not Found:
  - message (String): Повідомлення про те, що матеріал не знайдено.

# DonorController API

## Маршрути

### Add Donor

**POST** '/api/donors/admin/{userId}/add'

#### Опис

Додає нового донора від імені адміністратора.

#### Параметри запиту
Path:
- userId (Long): Ідентифікатор адміністратора, що виконує дію.

Body:
- firstName (String): Ім'я донора.
- lastName (String): Прізвище донора.
- birthDate (Date): Дата народження донора.
- gender (Gender): Стать донора.
- idNumber (String): Ідентифікаційний номер донора.
- bloodType (RhFactorOfBlood): Група крові донора.
- transplantRestrictions (String): Обмеження на трансплантацію.

#### Відповідь 

- 200 OK:
  - Donor (object): Об'єкт створеного донора.
- 400 Bad Request:
  - message (string): Повідомлення про помилку у вхідних даних.
- 403 Forbidden:
  - message (string): Відсутність дозволу на виконання дії.
- 404 Not Found:
  - message (string): Адміністратора не знайдено.

### Get Donor By ID

**GET** '/api/donors/{DonorID}'

#### Опис

Отримує інформацію про донора за його унікальним ідентифікатором.

#### Параметри запиту
Path:
- DonorID (Long): Ідентифікатор донора.

#### Відповідь 

- 200 OK:
  - Donor (object): Донор за вказаним ідентифікатором.
- 404 Not Found:
  - message (string): Повідомлення про те, що донор не знайдений.

### Get All Donors

**GET** '/api/donors'

#### Опис

Отримує список усіх донорів.

#### Відповідь 

- 200 OK:
  - List<Donor> (Array): Список усіх донорів.

### Update Donor

**PUT** '/api/donors/admin/{userId}/{DonorID}'

#### Опис

Оновлює існуючого донора від імені адміністратора.

#### Параметри запиту
Path:
- userId (Long): Ідентифікатор адміністратора, що виконує дію.
- DonorID (Long): Ідентифікатор донора, якого потрібно оновити.
  
Body:
- firstName (String): Ім'я донора.
- lastName (String): Прізвище донора.
- birthDate (Date): Дата народження донора.
- gender (Gender): Стать донора.
- idNumber (String): Ідентифікаційний номер донора.
- bloodType (RhFactorOfBlood): Група крові донора.
- transplantRestrictions (String): Обмеження на трансплантацію.

#### Відповідь 

- 200 OK:
  - Donor (Object): Оновлений об'єкт донора.
- 400 Bad Request:
  - message (String): Повідомлення про помилку у вхідних даних.
- 403 Forbidden:
  - message (String): Відсутність дозволу на виконання дії.
- 404 Not Found:
  - message (String): Повідомлення про те, що донор не знайдений.

### Delete Donor

**DELETE** '/api/donors/admin/{userId}/{id}'

#### Опис

Видаляє донора від імені адміністратора.

#### Параметри запиту
Path:
- userId (Long): Ідентифікатор адміністратора, що виконує дію.
- id (Long): Ідентифікатор донора, якого потрібно видалити.

#### Відповідь 

- 204 No Content:
  - Видалення донора відбулося успішно.
- 404 Not Found:
  - message (String): Повідомлення про те, що донор не знайдений.

# EventLogController API

## Маршрути

### Add Event Log

**POST** '/api/event-logs/admin/{userId}/add'

#### Опис

Додає новий запис про подію від імені адміністратора.

#### Параметри запиту
Path:
- userId (Long): Ідентифікатор адміністратора, що виконує дію.
  
Body:

#### Відповідь 

- 200 OK:
  - EventLog (object): Об'єкт створеного запису про подію.
- 400 Bad Request:
  - message (string): Повідомлення про помилку у вхідних даних.
- 403 Forbidden:
  - message (string): Відсутність дозволу на виконання дії.
- 404 Not Found:
  - message (string): Адміністратора не знайдено.

### Get Event Log By ID

**GET** '/api/event-logs/admin/{userId}/{id}'

#### Опис

Отримує запис про подію за його унікальним ідентифікатором.

#### Параметри запиту
Path:
- userId (Long): Ідентифікатор адміністратора, що виконує дію.
- id (Long): Ідентифікатор запису.

#### Відповідь 

- 200 OK:
  - EventLog (object): Запис про подію за вказаним ідентифікатором.
- 403 Forbidden:
  - message (string): Відсутність дозволу на виконання дії.
- 404 Not Found:
  - message (string): Запис не знайдено.

### Get All Event Logs

**GET** '/api/event-logs/admin/{userId}'

#### Опис

Отримує всі записи про події.

#### Параметри запиту
Path:
- userId (Long): Ідентифікатор адміністратора, що виконує дію.

#### Відповідь 

- 200 OK:
  - List<EventLog> (Array): Список всіх записів про події.
- 403 Forbidden:
  - message (string): Відсутність дозволу на виконання дії.

### Update Event Log

**PUT** '/api/event-logs/admin/{userId}/{id}'

#### Опис

Оновлює існуючий запис про подію від імені адміністратора.

#### Параметри запиту
Path:
- userId (Long): Ідентифікатор адміністратора, що виконує дію.
- id (Long): Ідентифікатор запису, який потрібно оновити.
  
Body:
- actionDetails (String): Деталі дії.
- actionTime (Date): Час виконання дії.

#### Відповідь 

- 200 OK:
  - EventLog (Object): Оновлений запис про подію.
- 400 Bad Request:
  - message (String): Повідомлення про помилку у вхідних даних.
- 403 Forbidden:
  - message (String): Відсутність дозволу на виконання дії.
- 404 Not Found:
  - message (String): Запис не знайдено.

### Delete Event Log

**DELETE** '/api/event-logs/admin/{userId}/{id}'

#### Опис

Видаляє запис про подію від імені адміністратора.

#### Параметри запиту
Path:
- userId (Long): Ідентифікатор адміністратора, що виконує дію.
- id (Long): Ідентифікатор запису, який потрібно видалити.

#### Відповідь 

- 204 No Content:
  - Видалення запису про подію відбулося успішно.
- 403 Forbidden:
  - message (String): Відсутність дозволу на виконання дії.
- 404 Not Found:
  - message (String): Запис не знайдено.
 
# NotificationController API

## Маршрути

### Add Notification

**POST** '/api/notifications/admin/{userId}/add'

#### Опис

Додає нове сповіщення від імені адміністратора.

#### Параметри запиту
Path:
- userId (Long): Ідентифікатор адміністратора, що виконує дію.
  
Body:
- eventTime (Date): Час події.
- eventType (String): Тип події.
- details (String): Деталі сповіщення.
- status (String): Статус сповіщення.
- materialID (BiologicalMaterial): Біологічний матеріал, що пов'язаний зі сповіщенням.

#### Відповідь 

- 200 OK:
  - Notification (object): Об'єкт створеного сповіщення.
- 400 Bad Request:
  - message (string): Повідомлення про помилку у вхідних даних.
- 403 Forbidden:
  - message (string): Відсутність дозволу на виконання дії.
- 404 Not Found:
  - message (string): Адміністратора не знайдено.

### Get Notification By ID

**GET** '/api/notifications/{id}'

#### Опис

Отримує сповіщення за його унікальним ідентифікатором.

#### Параметри запиту
Path:
- id (Long): Ідентифікатор сповіщення.

#### Відповідь 

- 200 OK:
  - Notification (object): Сповіщення за вказаним ідентифікатором.
- 404 Not Found:
  - message (string): Повідомлення про те, що сповіщення не знайдено.

### Get All Notifications

**GET** '/api/notifications'

#### Опис

Отримує список усіх сповіщень.

#### Відповідь 

- 200 OK:
  - List<Notification> (Array): Список усіх сповіщень.

### Update Notification

**PUT** '/api/notifications/admin/{userId}/{id}'

#### Опис

Оновлює існуюче сповіщення від імені адміністратора.

#### Параметри запиту
Path:
- userId (Long): Ідентифікатор адміністратора, що виконує дію.
- id (Long): Ідентифікатор сповіщення, яке потрібно оновити.
  
Body:
- eventTime (Date): Час події.
- eventType (String): Тип події.
- details (String): Деталі сповіщення.
- status (String): Статус сповіщення.
- materialID (BiologicalMaterial): Біологічний матеріал, що пов'язаний зі сповіщенням.

#### Відповідь 

- 200 OK:
  - Notification (Object): Оновлений об'єкт сповіщення.
- 400 Bad Request:
  - message (String): Повідомлення про помилку у вхідних даних.
- 403 Forbidden:
  - message (String): Відсутність дозволу на виконання дії.
- 404 Not Found:
  - message (String): Повідомлення про те, що сповіщення не знайдено.

### Delete Notification

**DELETE** '/api/notifications/admin/{userId}/{id}'

#### Опис

Видаляє сповіщення від імені адміністратора.

#### Параметри запиту
Path:
- userId (Long): Ідентифікатор адміністратора, що виконує дію.
- id (Long): Ідентифікатор сповіщення, яке потрібно видалити.

#### Відповідь 

- 204 No Content:
  - Видалення сповіщення відбулося успішно.
- 404 Not Found:
  - message (String): Повідомлення про те, що сповіщення не знайдено.

# ReportController API

## Маршрути

### Add Report

**POST** '/api/reports/admin/{userId}/add'

#### Опис

Додає новий звіт від імені адміністратора.

#### Параметри запиту
Path:
- userId (Long): Ідентифікатор адміністратора, що виконує дію.
  
Body:
- reportType (String): Тип звіту.
- creationDate (Date): Дата створення звіту.
- text (String): Текст звіту.
- fileLink (String): Посилання на файл звіту.
- eventLogID (EventLog): Пов'язаний ідентифікатор журналу подій.

#### Відповідь 

- 200 OK:
  - Report (object): Об'єкт створеного звіту.
- 400 Bad Request:
  - message (string): Повідомлення про помилку у вхідних даних.
- 403 Forbidden:
  - message (string): Відсутність дозволу на виконання дії.
- 404 Not Found:
  - message (string): Адміністратора не знайдено.

### Get Report By ID

**GET** '/api/reports/{id}'

#### Опис

Отримує звіт за його унікальним ідентифікатором.

#### Параметри запиту
Path:
- id (Long): Ідентифікатор звіту.

#### Відповідь 

- 200 OK:
  - Report (object): Звіт за вказаним ідентифікатором.
- 404 Not Found:
  - message (string): Повідомлення про те, що звіт не знайдено.

### Get All Reports

**GET** '/api/reports'

#### Опис

Отримує список усіх звітів.

#### Відповідь 

- 200 OK:
  - List<Report> (Array): Список усіх звітів.

### Update Report

**PUT** '/api/reports/admin/{userId}/{id}'

#### Опис

Оновлює існуючий звіт від імені адміністратора.

#### Параметри запиту
Path:
- userId (Long): Ідентифікатор адміністратора, що виконує дію.
- id (Long): Ідентифікатор звіту, який потрібно оновити.
  
Body:
- reportType (String): Тип звіту.
- creationDate (Date): Дата створення звіту.
- text (String): Текст звіту.
- fileLink (String): Посилання на файл звіту.
- eventLogID (EventLog): Пов'язаний ідентифікатор журналу подій.

#### Відповідь 

- 200 OK:
  - Report (Object): Оновлений об'єкт звіту.
- 400 Bad Request:
  - message (String): Повідомлення про помилку у вхідних даних.
- 403 Forbidden:
  - message (String): Відсутність дозволу на виконання дії.
- 404 Not Found:
  - message (String): Повідомлення про те, що звіт не знайдено.

### Delete Report

**DELETE** '/api/reports/admin/{userId}/{id}'

#### Опис

Видаляє звіт від імені адміністратора.

#### Параметри запиту
Path:
- userId (Long): Ідентифікатор адміністратора, що виконує дію.
- id (Long): Ідентифікатор звіту, яке потрібно видалити.

#### Відповідь 

- 204 No Content:
  - Видалення звіту відбулося успішно.
- 404 Not Found:
  - message (String): Повідомлення про те, що звіт не знайдено.
 
# StorageConditionController API

## Маршрути

### Add Storage Condition

**POST** '/api/storage-conditions/admin/{userId}/add'

#### Опис

Додає нові умови зберігання від імені адміністратора.

#### Параметри запиту
Path:
- userId (Long): Ідентифікатор адміністратора, що виконує дію.
  
Body:
- temperature (double): Температура зберігання.
- oxygenLevel (double): Рівень кисню.
- humidity (double): Вологість.
- measurementTime (Date): Час вимірювання.
- materialID (BiologicalMaterial): Ідентифікатор біологічного матеріалу.

#### Відповідь 

- 200 OK:
  - StorageCondition (object): Об'єкт створених умов зберігання.
- 400 Bad Request:
  - message (string): Повідомлення про помилку у вхідних даних.
- 403 Forbidden:
  - message (string): Відсутність дозволу на виконання дії.
- 404 Not Found:
  - message (string): Адміністратора не знайдено.

### Get Storage Condition By ID

**GET** '/api/storage-conditions/{id}'

#### Опис

Отримує умови зберігання за їх унікальним ідентифікатором.

#### Параметри запиту
Path:
- id (Long): Ідентифікатор умови зберігання.

#### Відповідь 

- 200 OK:
  - StorageCondition (object): Умова зберігання за вказаним ідентифікатором.
- 404 Not Found:
  - message (string): Повідомлення про те, що умова зберігання не знайдена.

### Get All Storage Conditions

**GET** '/api/storage-conditions'

#### Опис

Отримує список усіх умов зберігання.

#### Відповідь 

- 200 OK:
  - List<StorageCondition> (Array): Список усіх умов зберігання.

### Update Storage Condition

**PUT** '/api/storage-conditions/admin/{userId}/{id}'

#### Опис

Оновлює існуючі умови зберігання від імені адміністратора.

#### Параметри запиту
Path:
- userId (Long): Ідентифікатор адміністратора, що виконує дію.
- id (Long): Ідентифікатор умови зберігання, яку потрібно оновити.
  
Body:
- temperature (double): Температура зберігання.
- oxygenLevel (double): Рівень кисню.
- humidity (double): Вологість.
- measurementTime (Date): Час вимірювання.
- materialID (BiologicalMaterial): Ідентифікатор біологічного матеріалу.

#### Відповідь 

- 200 OK:
  - StorageCondition (object): Оновлений об'єкт умови зберігання.
- 400 Bad Request:
  - message (string): Повідомлення про помилку у вхідних даних.
- 403 Forbidden:
  - message (string): Відсутність дозволу на виконання дії.
- 404 Not Found:
  - message (string): Повідомлення про те, що умова зберігання не знайдена.

### Delete Storage Condition

**DELETE** '/api/storage-conditions/admin/{userId}/{id}'

#### Опис

Видаляє умову зберігання від імені адміністратора.

#### Параметри запиту
Path:
- userId (Long): Ідентифікатор адміністратора, що виконує дію.
- id (Long): Ідентифікатор умови зберігання, яку потрібно видалити.

#### Відповідь 

- 204 No Content:
  - Видалення умови зберігання відбулося успішно.
- 404 Not Found:
  - message (string): Повідомлення про те, що умова зберігання не знайдена.

# UserController API

## Маршрути

### Create User

**POST** '/api/users'

#### Опис

Створює нового користувача.

#### Параметри запиту

Body:
- firstName (String): Ім'я користувача.
- lastName (String): Призвище користувача.
- role (String): Роль користувача.
- accessRights (Access): Права доступу (READ_ONLY, READ_FULL FULL).
- login (String): Логін користувача (електронна пошта).
- password (String): Пароль користувача.

#### Відповідь

- 200 OK:
  - User (object): Об'єкт створеного користувача.
- 400 Bad Request:
  - message (string): Повідомлення про помилку у вхідних даних.


### Create User by Admin

**POST** '/api/users/admin/{userId}/add'

#### Опис

Створює нового користувача від імені адміністратора.

#### Параметри запиту
Path:
- userId (Long): Ідентифікатор адміністратора, що виконує дію.

Body:
- firstName (String): Ім'я користувача.
- lastName (String): Призвище користувача.
- role (String): Роль користувача.
- accessRights (Access): Права доступу (READ_FULL, READ_ONLY, FULL).
- login (String): Логін користувача (електронна пошта).
- password (String): Пароль користувача.

#### Відповідь

- 200 OK:
  - User (object): Об'єкт створеного користувача.
- 400 Bad Request:
  - message (string): Повідомлення про помилку у вхідних даних.
- 403 Forbidden:
  - message (string): Адміністратор не має прав для створення користувача.


### Get User by ID

**GET** '/api/users/{id}'

#### Опис

Отримує користувача за його унікальним ідентифікатором.

#### Параметри запиту
Path:
- id (Long): Ідентифікатор користувача.

#### Відповідь

- 200 OK:
  - User (object): Користувач за вказаним ідентифікатором.
- 404 Not Found:
  - message (string): Повідомлення про те, що користувач не знайдений.

# AuthController API

## Маршрути

### Login (User Login)

**POST** '/log/in'

#### Опис

Використовуються для входу користувача до системи. Використовує логін (електронну пошту) та пароль для автентифікації.

#### Параметри запиту
Body:
- login (String): Логін користувача (email).
- password (String): Пароль користувача.

#### Відповідь 

- 200 OK:
  - message (string): Повідомлення про успішний вхід "Вхід успішний. Ваш ID: [userID]".
- 401 Unauthorized:
  - message (string): Повідомлення про невірні облікові дані.

### Logout (User Logout)

**POST** '/log/out'

#### Опис

Використовуються для виходу користувача з системи, закінчуючи сесію.

#### Параметри запиту
Query:
- userId (Long): Ідентифікатор користувача, який виходить.

#### Відповідь 

- 200 OK:
  - message (string): Повідомлення про успішний вихід "Вихід успішний".
 
# Моделів даних

## BiologicalMaterial
Модель для біологічного матеріалу.

- `materialID` - Унікальний ідентифікатор біологічного матеріалу
- `materialName` - Назва біологічного матеріалу (від 2 до 100 символів)
- `expirationDate` - Дата закінчення терміну придатності біологічного матеріалу (має бути у майбутньому)
- `status` - Статус пожертвування (потрібно вказати статус пожертвування: `AVAILABLE`, `COLLECTED`, `DONATED`, `PENDING` `DISPOSED` )
- `transferDate` - Дата передачі матеріалу (має бути в минулому або поточному часі)
- `donorID` - Ідентифікатор донора, що надав біологічний матеріал (не може бути порожнім)

## Donor
Модель для донора біологічного матеріалу.

- `donorID` - Унікальний ідентифікатор донора
- `firstName` - Ім'я донора (від 2 до 100 символів)
- `lastName` - Прізвище донора (від 2 до 100 символів)
- `birthDate` - Дата народження донора (має бути в минулому)
- `gender` - Стать донора (`MALE`, `FEMALE`)
- `idNumber` - Ідентифікаційний номер донора (10 символів, алфавітно-цифровий формат)
- `bloodType` - Група крові донора (наприклад, `A_POS`, `O_NEG`)
- `transplantRestrictions` - Обмеження на трансплантацію (не більше 500 символів)

## EventLog
Модель для запису дій користувача в системі.

- `eventLogID` - Унікальний ідентифікатор запису події
- `actionDetails` - Опис дії користувача (від 2 до 1000 символів)
- `actionTime` - Час виконання дії (має бути в минулому або поточному часі)
- `creatorID` - Ідентифікатор користувача, що виконав дію (може бути порожнім)

## Notification
Модель для сповіщення про події в системі.
- `notificationID` - Унікальний ідентифікатор сповіщення
- `eventTime` - Час події (має бути в минулому або поточному часі)
- `eventType` - Тип події (від 2 до 100 символів)
- `details` - Опис події (від 5 до 500 символів)
- `status` - Статус сповіщення (від 2 до 50 символів)
- `materialID` - Ідентифікатор біологічного матеріалу, пов'язаного з подією (не може бути порожнім)

## Report
Модель для звіту в системі.
- `reportID` - Унікальний ідентифікатор звіту
- `reportType` - Тип звіту (від 2 до 100 символів)
- `creationDate` - Дата створення звіту (має бути в минулому або поточному часі)
- `text` - Текст звіту (від 5 до 1000 символів)
- `fileLink` - Посилання на файл звіту (від 5 до 500 символів)
- `eventLogID` - Ідентифікатор запису події, до якої прив'язаний звіт (не може бути порожнім)

## StorageCondition
Модель для умов зберігання біологічного матеріалу.
- `recordID` - Унікальний ідентифікатор запису умов зберігання
- `temperature` - Температура (від -100 до 100 градусів, не може бути порожнім)
- `oxygenLevel` - Рівень кисню (від 0 до 100%, не може бути порожнім)
- `humidity` - Вологість (від 0 до 100%, не може бути порожнім)
- `measurementTime` - Час вимірювання (має бути в минулому або поточному часі)
- `materialID` - Ідентифікатор біологічного матеріалу, до якого відносяться умови зберігання (не може бути порожнім)

## User
Модель для користувача системи.
- `userID` - Унікальний ідентифікатор користувача
- `firstName` - Ім'я користувача (від 2 до 255 символів)
- `lastName` - Призвище користувача (від 2 до 255 символів)
- `role` - Роль користувача (від 2 до 255 символів)
- `accessRights` - Права доступу користувача (можливі значення: `FULL`, `READ_ALL`, `READ_ONLY`)
- `login` - Логін користувача (електронна пошта, від 2 до 100 символів)
- `password` - Пароль користувача (не менше 6 символів)
