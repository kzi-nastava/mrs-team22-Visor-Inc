package inc.visor.voom.app.driver.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import inc.visor.voom.app.R;
import inc.visor.voom.app.user.profile.ProfileViewModel;

public class MainDriverFragment extends Fragment {

    public MainDriverFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_driver, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {

        NavController navController = NavHostFragment.findNavController(
                        getChildFragmentManager().findFragmentById(R.id.driver_nav_host)
                );

        BottomNavigationView bottomNav = view.findViewById(R.id.bottom_navigation_view);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                navController.navigate(R.id.driverHomeFragment);
                return true;
            }

            if (id == R.id.nav_services) {
                NavController parentNavController =
                        NavHostFragment.findNavController(
                                requireParentFragment()
                        );

                parentNavController.navigate(R.id.loginFragment);
                return true;
            }

            if (id == R.id.nav_profile) {
                navController.navigate(R.id.driverProfileFragment);
                return true;
            }

            if (id == R.id.nav_statistics) {
                navController.navigate(R.id.driverStatisticsFragment);
                return true;
            }


            if (id == R.id.nav_activity) {
                navController.navigate(R.id.driverRideHistoryFragment);
                return true;
            }

            return false;
        });

    }
}