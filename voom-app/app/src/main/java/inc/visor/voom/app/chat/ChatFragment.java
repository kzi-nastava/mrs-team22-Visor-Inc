package inc.visor.voom.app.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import inc.visor.voom.app.R;
import inc.visor.voom.app.chat.adapter.MessageAdapter;
import inc.visor.voom.app.chat.dto.UserChatDto;
import inc.visor.voom.app.shared.DataStoreManager;

public class ChatFragment extends Fragment {
    private ChatViewModel viewModel;
    private MessageAdapter adapter;
    private String recipientEmail = "admin";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        RecyclerView rv = view.findViewById(R.id.recyclerViewMessages);
        EditText input = view.findViewById(R.id.editTextMessage);
        Button send = view.findViewById(R.id.buttonSend);

        adapter = new MessageAdapter();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);


        viewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            adapter.setMessages(messages);
            rv.scrollToPosition(messages.size() - 1);
        });

        send.setOnClickListener(v -> {
            String text = input.getText().toString();
            if (!text.isEmpty()) {
                viewModel.sendMessage(text);
                input.setText("");
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String userRole = DataStoreManager.getInstance().getUserRole().blockingGet();
        String currentUserEmail = DataStoreManager.getInstance().getUserEmail().blockingGet();
        ImageButton closeBtn = view.findViewById(R.id.btnCloseChat);

        Log.d("CLOSE BTN: ", " " + closeBtn.getId());

        if (userRole.equals("USER") || userRole.equals("DRIVER")) {
            this.recipientEmail = "admin";
            viewModel.init(currentUserEmail, recipientEmail);
            closeBtn.setOnClickListener(v -> {
                Log.d("CLOSE CHAT", "yep registering listener");
                requireActivity().findViewById(R.id.chatContainer).setVisibility(View.GONE);
                requireActivity().findViewById(R.id.fabChatBubble).setVisibility(View.VISIBLE);
            });
        } else {
            if (getArguments() != null) {
                UserChatDto selectedUser = (UserChatDto) getArguments().getSerializable("selected_user");
                if (selectedUser != null) {
                    this.recipientEmail = selectedUser.getEmail();
                    closeBtn.setVisibility(View.GONE);
                    viewModel.init("admin", recipientEmail);
                }
            }
        }

    }
}