package inc.visor.voom.app.admin.users.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import inc.visor.voom.app.R;
import inc.visor.voom.app.admin.users.dto.UserProfileDto;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {

    private List<UserProfileDto> users = new ArrayList<>();

    public void setUsers(List<UserProfileDto> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_card, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserProfileDto user = users.get(position);

        holder.tvName.setText(user.firstName + " " + user.lastName);
        holder.tvEmail.setText(user.email);
        holder.tvRole.setText(user.userRoleName);
        holder.tvStatus.setText(user.userStatus);

        if ("BLOCKED".equals(user.userStatus)) {
            holder.tvStatus.setTextColor(Color.RED);
        } else {
            holder.tvStatus.setTextColor(Color.GRAY);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
