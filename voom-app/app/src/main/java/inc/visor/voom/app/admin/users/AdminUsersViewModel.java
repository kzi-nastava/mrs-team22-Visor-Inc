package inc.visor.voom.app.admin.users;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import inc.visor.voom.app.admin.users.api.UserApi;
import inc.visor.voom.app.admin.users.dto.BlockUserRequestDto;
import inc.visor.voom.app.admin.users.dto.UserProfileDto;
import inc.visor.voom.app.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUsersViewModel extends ViewModel {

    private final MutableLiveData<List<UserProfileDto>> _allUsers =
            new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<String> _searchTerm =
            new MutableLiveData<>("");

    public final MediatorLiveData<List<UserProfileDto>> filteredUsers =
            new MediatorLiveData<>();

    public AdminUsersViewModel() {

        filteredUsers.addSource(_allUsers, users -> filter());
        filteredUsers.addSource(_searchTerm, term -> filter());

        fetchUsers();
    }

    private void filter() {
        List<UserProfileDto> users = _allUsers.getValue();
        String term = _searchTerm.getValue().toLowerCase().trim();

        if (users == null) return;

        if (term.isEmpty()) {
            filteredUsers.setValue(users);
            return;
        }

        List<UserProfileDto> filtered = users.stream()
                .filter(u ->
                        u.firstName.toLowerCase().contains(term) ||
                                u.lastName.toLowerCase().contains(term)
                )
                .collect(Collectors.toList());

        filteredUsers.setValue(filtered);
    }

    public void setSearchTerm(String term) {
        _searchTerm.setValue(term);
    }

    private void fetchUsers() {
        UserApi api = RetrofitClient.getInstance().create(UserApi.class);

        api.getUsers().enqueue(new Callback<List<UserProfileDto>>() {
            @Override
            public void onResponse(Call<List<UserProfileDto>> call,
                                   Response<List<UserProfileDto>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    _allUsers.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<UserProfileDto>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void blockUser(Long userId, String reason) {

        UserApi api = RetrofitClient.getInstance().create(UserApi.class);

        Map<String, String> body = new HashMap<>();
        body.put("reason", reason);

        api.blockUser(userId, body)
                .enqueue(new Callback<UserProfileDto>() {
                    @Override
                    public void onResponse(Call<UserProfileDto> call,
                                           Response<UserProfileDto> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            UserProfileDto updatedUser = response.body();

                            List<UserProfileDto> current = _allUsers.getValue();
                            if (current == null) return;

                            List<UserProfileDto> updated = new ArrayList<>();

                            for (UserProfileDto u : current) {
                                if (u.id == updatedUser.id) {
                                    updated.add(updatedUser);
                                } else {
                                    updated.add(u);
                                }
                            }

                            _allUsers.setValue(updated);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserProfileDto> call, Throwable t) {
                        Log.e("BLOCK", "Error blocking user", t);
                    }
                });
    }



    public LiveData<List<UserProfileDto>> getFilteredUsers() {
        return filteredUsers;
    }
}
