package com.example.llmchatbot;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {



    TextView chatTitleTextView;
    RecyclerView chatRecyclerView;
    EditText messageEditText;
    Button sendButton;

    ArrayList<Message> messageList;
    MessageAdapter messageAdapter;
    DatabaseHelper databaseHelper;

    String username;
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatTitleTextView = findViewById(R.id.chatTitleTextView);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        username = getIntent().getStringExtra("username");

        if (username == null || username.isEmpty()) {
            username = "User";
        }

        chatTitleTextView.setText("Welcome, " + username);

        databaseHelper = new DatabaseHelper(this);
        messageList = databaseHelper.getMessages(username);

        messageAdapter = new MessageAdapter(messageList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(messageAdapter);

        if (messageList.size() > 0) {
            chatRecyclerView.scrollToPosition(messageList.size() - 1);
        }

        sendButton.setOnClickListener(view -> sendMessage());
    }

    private void sendMessage() {
        String userMessageText = messageEditText.getText().toString().trim();

        if (userMessageText.isEmpty()) {
            Toast.makeText(this, "Please type a message", Toast.LENGTH_SHORT).show();
            return;
        }

        String time = getCurrentTime();

        Message userMessage = new Message(userMessageText, "user", time);
        messageList.add(userMessage);

        databaseHelper.addMessage(username, userMessageText, "user", time);

        messageAdapter.notifyItemInserted(messageList.size() - 1);
        chatRecyclerView.scrollToPosition(messageList.size() - 1);

        messageEditText.setText("");

        getGroqResponse(userMessageText);
    }

    private void getGroqResponse(String userPrompt) {
        String url = "https://api.groq.com/openai/v1/chat/completions";

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("model", "llama-3.1-8b-instant");

            JSONArray messagesArray = new JSONArray();

            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", "You are a helpful Android chatbot. Keep answers clear and short.");

            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", userPrompt);

            messagesArray.put(systemMessage);
            messagesArray.put(userMessage);

            jsonBody.put("messages", messagesArray);
            jsonBody.put("temperature", 0.7);
            jsonBody.put("max_tokens", 300);

            RequestBody body = RequestBody.create(
                    jsonBody.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + GROQ_API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    addBotMessage("Failed to connect to Groq API. Please check your internet connection.");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = "";

                    if (response.body() != null) {
                        responseData = response.body().string();
                    }

                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonResponse = new JSONObject(responseData);

                            JSONArray choices = jsonResponse.getJSONArray("choices");
                            JSONObject firstChoice = choices.getJSONObject(0);
                            JSONObject messageObject = firstChoice.getJSONObject("message");

                            String botReply = messageObject.getString("content");

                            addBotMessage(botReply.trim());

                        } catch (Exception e) {
                            addBotMessage("Error reading Groq response. Please try again.");
                        }
                    } else {
                        addBotMessage("Groq API error: " + response.code() + ". Please check your API key or model.");
                    }
                }
            });

        } catch (Exception e) {
            addBotMessage("Request error. Please try again.");
        }
    }

    private void addBotMessage(String botReply) {
        runOnUiThread(() -> {
            String botTime = getCurrentTime();

            Message botMessage = new Message(botReply, "bot", botTime);
            messageList.add(botMessage);

            databaseHelper.addMessage(username, botReply, "bot", botTime);

            messageAdapter.notifyItemInserted(messageList.size() - 1);
            chatRecyclerView.scrollToPosition(messageList.size() - 1);
        });
    }

    private String getCurrentTime() {
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());

        return simpleDateFormat.format(new Date());
    }
}