Features

-> User login screen
-> AI chatbot conversation interface
-> Real-time LLM responses using Groq API
-> RecyclerView-based chat display
-> Separate user and bot message layouts
-> Chat history storage using SQLite database
-> Message timestamps
-> Error handling for API failures
-> Persistent conversations after reopening the app

Technologies Used

-> Java
-> Android Studio
-> XML Layout Design
-> RecyclerView
-> SQLite Database
-> OkHttp Library
-> JSON Parsing
-> Groq API (LLM Backend)


How the Application Works

1. The user enters a username on the login screen.
2. The app opens the chatbot screen.
3. User messages are displayed using RecyclerView.
4. The message is sent to the Groq LLM API using an HTTP POST request.
5. The API response is received in JSON format.
6. The response is parsed and displayed as a chatbot reply.
7. All messages are stored locally using SQLite database.
8. Previous chats are loaded when the app is reopened.
