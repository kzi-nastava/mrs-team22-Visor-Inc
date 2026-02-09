package inc.visor.voom.app.chat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import inc.visor.voom.app.R;
import inc.visor.voom.app.chat.dto.UserChatDto;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    private List<UserChatDto> users;
    private OnUserClickListener listener;

    @NonNull
    @Override
    public ChatListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_user, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.ViewHolder holder, int position) {
        UserChatDto user = users.get(position);
        holder.nameText.setText(user.getSenderFirstName() + " " + user.getSenderLastName());
        holder.emailText.setText(user.getEmail());

        holder.itemView.setOnClickListener(v -> listener.onUserClick(user));
    }


    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }

    public interface OnUserClickListener {
        void onUserClick(UserChatDto user);
    }

    public ChatListAdapter(List<UserChatDto> users, OnUserClickListener listener) {
        this.users = users;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameText;
        public TextView emailText;

        public ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.textViewUserName);
            emailText = itemView.findViewById(R.id.textViewUserEmail);
        }
    }


}
