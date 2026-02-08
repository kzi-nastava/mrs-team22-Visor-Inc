package inc.visor.voom.app.admin.users;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import inc.visor.voom.app.R;
import inc.visor.voom.app.admin.users.adapter.UserAdapter;
import inc.visor.voom.app.databinding.FragmentAdminUsersBinding;

public class AdminUsersFragment extends Fragment {

    private FragmentAdminUsersBinding binding;
    private AdminUsersViewModel viewModel;
    private UserAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAdminUsersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(this).get(AdminUsersViewModel.class);

        adapter = new UserAdapter();
        binding.rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvUsers.setAdapter(adapter);

        viewModel.getFilteredUsers().observe(getViewLifecycleOwner(), users -> {
            adapter.setUsers(users);
        });

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSearchTerm(s.toString());
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        binding.btnAddDriver.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.createDriverFragment)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
