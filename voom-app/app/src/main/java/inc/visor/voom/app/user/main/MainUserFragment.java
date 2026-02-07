package inc.visor.voom.app.user.main;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import inc.visor.voom.app.R;
import inc.visor.voom.app.user.profile.ChangePasswordDialogFragment;
import inc.visor.voom.app.user.profile.ProfileViewModel;

public class MainUserFragment extends Fragment {

    private ProfileViewModel profileViewModel; //TODO remove

    public MainUserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = NavHostFragment.findNavController(getChildFragmentManager().findFragmentById(R.id.user_nav_host));

        BottomNavigationView bottomNav = view.findViewById(R.id.bottom_navigation_view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (requireContext().checkSelfPermission(
                    android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        100
                );
            }
        }

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                navController.navigate(R.id.userHomeFragment);
                return true;
            }

            if (id == R.id.nav_profile) {
                navController.navigate(R.id.userProfileFragment);
                return true;
            }

            if (id == R.id.nav_statistics) {
                navController.navigate(R.id.userStatisticsFragment);
                return true;
            }

            if (id == R.id.nav_activity) {
                navController.navigate(R.id.driverRideHistoryFragment);
                return true;
            }

            if (id == R.id.nav_services) {
                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(R.id.userRideTrackingFragment, true)
                        .build();

                navController.navigate(R.id.userRideTrackingFragment, null, navOptions);
                return true;
            }

            return false;
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_user, container, false);
    }
}