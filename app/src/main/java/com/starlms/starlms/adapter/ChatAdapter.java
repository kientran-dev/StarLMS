package com.starlms.starlms.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.starlms.starlms.R;
import com.starlms.starlms.entity.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private final List<Message> messages;
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.isFromStudent()) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message_sent, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message_received, parent, false);
        }
        return new MessageViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageBody;
        private final TextView messageTimestamp;
        private final ImageView profileImage; // Only in received layout

        public MessageViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            messageBody = itemView.findViewById(R.id.text_message_body);
            messageTimestamp = itemView.findViewById(R.id.text_message_timestamp);
            if (viewType == VIEW_TYPE_RECEIVED) {
                profileImage = itemView.findViewById(R.id.image_message_profile);
            } else {
                profileImage = null;
            }
        }

        public void bind(Message message) {
            messageBody.setText(message.getText());
            // Format and display the timestamp
            SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
            messageTimestamp.setText(sdf.format(new Date(message.getTimestamp())));

            // Avatar is only in received messages, so no need to handle it here as it's set in the layout
        }
    }
}
