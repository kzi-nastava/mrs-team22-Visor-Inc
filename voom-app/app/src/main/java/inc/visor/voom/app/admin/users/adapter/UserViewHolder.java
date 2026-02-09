package inc.visor.voom.app.admin.users.adapter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jspecify.annotations.NonNull;

import inc.visor.voom.app.R;

public class UserViewHolder extends RecyclerView.ViewHolder {

    TextView tvName, tvEmail, tvRole, tvStatus;
    ImageButton btnBlock;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.tvName);
        tvEmail = itemView.findViewById(R.id.tvEmail);
        tvRole = itemView.findViewById(R.id.tvRole);
        tvStatus = itemView.findViewById(R.id.tvStatus);
        btnBlock = itemView.findViewById(R.id.btnBlock);

    }
}

