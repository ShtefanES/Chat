# Chat
### Пример реализации общего чата для Android. 

![link](https://github.com/ShtefanES/Chat/blob/master/screenshots/img1.png)
![link](https://github.com/ShtefanES/Chat/blob/master/screenshots/img3.png)
![link](https://github.com/ShtefanES/Chat/blob/master/screenshots/img2.jpg)
![link](https://github.com/ShtefanES/Chat/blob/master/screenshots/img4.jpg)
![link](https://github.com/ShtefanES/Chat/blob/master/screenshots/img5.jpg)

## Особенности
- Авторизация через логин/пароль
- Общий чат для авторизованных пользователей
- Push-уведомления
- Отправка аудио-сообщений
- Индикация набора текса собеседником
- Всплывающая дата при скролле -"сегодня"/"вчера"/"d MMM yyyy г."
- Backend от Firebase
## Подготовка
- Скачать проект. 
- Интегрировать Firebase в проект(https://firebase.google.com/docs/android/setup)
- В Firebase console выбрать созданный проект. Слева выбрать Storage. Созать папку "Audio"
- В Firebase console выбрать Database. Выбрать "Импорт из файла JSON". Выбрать файл Chat/important/db_structure.json
- Настроить Firebase Functions(https://firebase.google.com/docs/functions/get-started). Заменить index.js на Chat/important/index.js
