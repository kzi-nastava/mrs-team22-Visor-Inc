package inc.visor.voom.app.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import inc.visor.voom.app.R;
import inc.visor.voom.app.chat.adapter.ChatListAdapter;
import inc.visor.voom.app.chat.dto.UserChatDto;
import inc.visor.voom.app.chat.service.ChatService;
import inc.visor.voom.app.shared.DataStoreManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatListFragment extends Fragment {
    private RecyclerView recyclerView;
    private ChatListAdapter adapter;
    private ChatService chatService;
    private List<UserChatDto> userList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewChatList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ChatListAdapter(userList, user -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("selected_user", user);
            Navigation.findNavController(view).navigate(R.id.chatFragment, bundle);
        });
        recyclerView.setAdapter(adapter);

        chatService = new ChatService("admin");
        loadConversations();
        setupRealTimeUpdates();
        return view;
    }

    private void loadConversations() {
        chatService.getChatApi().getActiveConversations().enqueue(new Callback<List<UserChatDto>>() {
            @Override
            public void onResponse(Call<List<UserChatDto>> call, Response<List<UserChatDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userList.clear();
                    userList.addAll(response.body());
                    Log.d("CHATS LEN: ", " " + userList.size());
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<List<UserChatDto>> call, Throwable t) {
                Log.e("ChatList", "Failed to load: " + t.getMessage());
            }
        });
    }

    private void setupRealTimeUpdates() {
        chatService.setOnMessageReceivedListener(incomingMsg -> {
            String partnerEmail = incomingMsg.getSenderEmail().equals("admin")
                    ? incomingMsg.getRecipientEmail()
                    : incomingMsg.getSenderEmail();

            boolean exists = false;
            for (UserChatDto user : userList) {
                if (user.getEmail().equals(partnerEmail)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                UserChatDto newUser = new UserChatDto();
                newUser.setEmail(partnerEmail);
                newUser.setSenderFirstName(incomingMsg.getSenderFirstName());
                newUser.setSenderLastName(incomingMsg.getSenderLastName());
                newUser.setProfilePic("");

                userList.add(0, newUser);

                getActivity().runOnUiThread(() -> adapter.notifyItemInserted(0));
            }
        });

        chatService.connect();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (chatService != null) {
            chatService.setOnMessageReceivedListener(null);
        }
    }
}