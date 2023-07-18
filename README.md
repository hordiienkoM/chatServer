<h3> Приложение - чат сервер. </h3> 

Для настройки, нужно прописать настройки бд в application.properties, 
желаемый порт сервера и ключ шифрования для токена авторизации. 

Сервер отвечает за регистрацию, авторизацию/аутентификацию пользователей, позволяет 
публиковать и редактировать сообщения, читать общий чат. 


<h3> Приложение - клиент. </h3>

Для настройки клиента, нужно зайти в папку client/src и в файле constants.ts прописать в 
переменную RSOCKET_SERVER_URL адресс сервера на котором запущен чат. 

На данный момент клиент имеет функции регистрации, авторизации/разовторизации, чтения 
общего чата, отправки сообщений в чат, редактирования своих сообщений.  