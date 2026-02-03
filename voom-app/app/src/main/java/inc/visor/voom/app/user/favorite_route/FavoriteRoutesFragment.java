package inc.visor.voom.app.user.favorite_route;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import inc.visor.voom.app.R;
import inc.visor.voom.app.user.favorite_route.adapters.FavoriteRoutesAdapter;
import inc.visor.voom.app.user.favorite_route.dto.FavoriteRouteUI;

public class FavoriteRoutesFragment extends Fragment
        implements FavoriteRoutesAdapter.Listener {

    private FavoriteRoutesViewModel viewModel;
    private FavoriteRoutesAdapter adapter;

    public FavoriteRoutesFragment() {
        super(R.layout.fragment_favorite_routes);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this)
                .get(FavoriteRoutesViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.favoriteRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new FavoriteRoutesAdapter(this);
        recyclerView.setAdapter(adapter);

        viewModel.getRoutes().observe(
                getViewLifecycleOwner(),
                adapter::submitList
        );

        viewModel.fetch();

        viewModel.getMessage().observe(
                getViewLifecycleOwner(),
                msg -> {
                    if (msg != null) {
                        android.widget.Toast.makeText(
                                requireContext(),
                                msg,
                                android.widget.Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );

    }

    @Override
    public void onPick(FavoriteRouteUI route) {

        Bundle bundle = new Bundle();
        bundle.putSerializable("picked_route", new ArrayList<>(route.dto.points));

        androidx.navigation.Navigation
                .findNavController(requireView())
                .navigate(R.id.action_favoriteRoutesFragment_to_profileFragment, bundle);
    }

    @Override
    public void onDelete(FavoriteRouteUI route) {
        viewModel.deleteRoute(route.id);
    }
}