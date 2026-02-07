package inc.visor.voom.app.admin.users;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import inc.visor.voom.app.admin.users.api.UserApi;
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

    public LiveData<List<UserProfileDto>> getFilteredUsers() {
        return filteredUsers;
    }
}
