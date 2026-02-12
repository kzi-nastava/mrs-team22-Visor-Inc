package inc.visor.voom.app.chat.service;

import com.google.gson.Gson;

import java.util.List;

import inc.visor.voom.app.chat.api.ChatApi;
import inc.visor.voom.app.chat.dto.ChatMessageDto;
import inc.visor.voom.app.config.AppConfig;
import inc.visor.voom.app.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class ChatService {
    private StompClient mStompClient;
    private ChatApi chatApi;
    private String userEmail;

    public interface OnMessageReceivedListener {
        void onMessageReceived(ChatMessageDto message);
    }

    private OnMessageReceivedListener messageListener;

    public void setOnMessageReceivedListener(OnMessageReceivedListener listener) {
        this.messageListener = listener;
    }

    public ChatService(String userEmail) {
        this.userEmail = userEmail;

        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, AppConfig.getWsUrl());

        chatApi = RetrofitClient.getInstance().create(ChatApi.class);
    }

    public void connect() {
        mStompClient.connect();

        mStompClient.topic("/topic/messages/" + userEmail).subscribe(topicMessage -> {
            ChatMessageDto msg = new Gson().fromJson(topicMessage.getPayload(), ChatMessageDto.class);
            if (messageListener != null) {
                messageListener.onMessageReceived(msg);
            }
        });
    }

    public void sendMessage(String recipientEmail, String content) {
        ChatMessageDto message = new ChatMessageDto();
        message.setSenderEmail(this.userEmail);
        message.setRecipientEmail(recipientEmail);
        message.setContent(content);

        mStompClient.send("/app/chat.sendMessage", new Gson().toJson(message)).subscribe();
    }

    public ChatApi getChatApi() {
        return chatApi;
    }

    public Call<List<ChatMessageDto>> getConversationHistory(String partnerEmail) {
        return chatApi.getChatHistory(this.userEmail, partnerEmail);
    }

    public void disconnect() {
        mStompClient.disconnect();
    }
}
