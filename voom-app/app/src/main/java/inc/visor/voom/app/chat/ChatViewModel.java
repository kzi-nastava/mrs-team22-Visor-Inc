package inc.visor.voom.app.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import inc.visor.voom.app.chat.dto.ChatMessageDto;
import inc.visor.voom.app.chat.service.ChatService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatViewModel extends ViewModel {
    private final MutableLiveData<List<ChatMessageDto>> messages = new MutableLiveData<>(new ArrayList<>());
    private ChatService chatService;
    private String currentRecipientEmail;

    public void init(String userEmail, String recipientEmail) {
        this.currentRecipientEmail = recipientEmail;
        chatService = new ChatService(userEmail);

        chatService.setOnMessageReceivedListener(msg -> {
            List<ChatMessageDto> currentList = new ArrayList<>(messages.getValue());

            boolean isFromPartner = msg.getSenderEmail().equals(recipientEmail);

            boolean isFromMe = msg.getSenderEmail().equals(userEmail) && msg.getRecipientEmail().equals(recipientEmail);

            if (isFromPartner || isFromMe) {
                currentList.add(msg);
                messages.postValue(currentList);
            }
        });

        chatService.connect();
        loadHistory(recipientEmail);
    }

    private void loadHistory(String email) {
        chatService.getConversationHistory(email).enqueue(new Callback<List<ChatMessageDto>>() {
            @Override
            public void onResponse(Call<List<ChatMessageDto>> call, Response<List<ChatMessageDto>> response) {
                if (response.isSuccessful()) {
                    messages.setValue(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<ChatMessageDto>> call, Throwable t) {}
        });
    }

    public void sendMessage(String text) {
        chatService.sendMessage(currentRecipientEmail, text);
    }

    public LiveData<List<ChatMessageDto>> getMessages() {
        return messages;
    }
}
