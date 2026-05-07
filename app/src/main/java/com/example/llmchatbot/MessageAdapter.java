package com.example.llmchatbot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> messageList;

    private static final int USER_MESSAGE = 1;
    private static final int BOT_MESSAGE = 2;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getSender().equals("user")) {
            return USER_MESSAGE;
        } else {
            return BOT_MESSAGE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == USER_MESSAGE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user_message, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_bot_message, parent, false);
            return new BotMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).userMessageTextView.setText(message.getMessageText());
            ((UserMessageViewHolder) holder).userTimeTextView.setText(message.getTime());
        } else {
            ((BotMessageViewHolder) holder).botMessageTextView.setText(message.getMessageText());
            ((BotMessageViewHolder) holder).botTimeTextView.setText(message.getTime());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class UserMessageViewHolder extends RecyclerView.ViewHolder {

        TextView userMessageTextView, userTimeTextView;

        public UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            userMessageTextView = itemView.findViewById(R.id.userMessageTextView);
            userTimeTextView = itemView.findViewById(R.id.userTimeTextView);
        }
    }

    static class BotMessageViewHolder extends RecyclerView.ViewHolder {

        TextView botMessageTextView, botTimeTextView;

        public BotMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            botMessageTextView = itemView.findViewById(R.id.botMessageTextView);
            botTimeTextView = itemView.findViewById(R.id.botTimeTextView);
        }
    }
}