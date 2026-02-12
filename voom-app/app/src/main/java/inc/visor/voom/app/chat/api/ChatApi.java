package inc.visor.voom.app.chat.api;

import inc.visor.voom.app.chat.dto.ChatMessageDto;
import inc.visor.voom.app.chat.dto.UserChatDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import java.util.List;

public interface ChatApi {
    @GET("/api/chat/conversations")
    Call<List<UserChatDto>> getActiveConversations();

    @GET("/api/chat/history/{userEmail}/{partnerEmail}")
    Call<List<ChatMessageDto>> getChatHistory(
            @Path("userEmail") String userEmail,
            @Path("partnerEmail") String partnerEmail
    );
}
