package inc.visor.voom.app.chat.adapter;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import inc.visor.voom.app.R;
import inc.visor.voom.app.chat.dto.ChatMessageDto;
import inc.visor.voom.app.shared.DataStoreManager;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<ChatMessageDto> messages = new ArrayList<>();
    private String currentUserEmail = DataStoreManager.getInstance().getUserRole().blockingGet().equals("ADMIN") ? "admin" : DataStoreManager.getInstance().getUserEmail().blockingGet();

    public void setMessages(List<ChatMessageDto> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessageDto message = messages.get(position);
        holder.content.setText(message.getContent());

        if (message.getTimestamp() != null && message.getTimestamp().length() >= 16) {
            holder.time.setText(message.getTimestamp().substring(11, 16));
        } else {
            holder.time.setText(message.getTimestamp());
        }

        if (message.getSenderEmail().equals(currentUserEmail)) {
            holder.container.setGravity(Gravity.END);
            holder.bubble.setBackgroundResource(R.drawable.bg_message_sent);
            holder.content.setTextColor(Color.WHITE);
            holder.time.setTextColor(Color.WHITE);

            holder.senderName.setVisibility(View.GONE);

        } else {
            holder.container.setGravity(Gravity.START);
            holder.bubble.setBackgroundResource(R.drawable.bg_message_received);
            holder.content.setTextColor(Color.parseColor("#1F2937"));
            holder.time.setTextColor(Color.parseColor("#6B7280"));

            holder.senderName.setVisibility(View.VISIBLE);

            if (message.getSenderEmail().equals("admin")) {
                holder.senderName.setText("Admin");
            }
            else {
                holder.senderName.setText(message.getSenderFirstName() + " " + message.getSenderLastName());
            }

        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView content, time, senderName;
        LinearLayout container, bubble;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.textViewContent);
            time = itemView.findViewById(R.id.textViewTime);
            senderName = itemView.findViewById(R.id.textViewSenderName);
            container = itemView.findViewById(R.id.mainLayoutContainer);
            bubble = itemView.findViewById(R.id.bubbleContainer);
        }
    }
}
