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
import com.google.android.material.bottomsheet.BottomSheetDialog;

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
                navController.navigate(R.id.rideHistoryFragment2);
                return true;
            }

            if (id == R.id.nav_services) {
                showInsightsSheet(navController);
                return true;
            }

            return false;
        });

    }

    private void showInsightsSheet(NavController navController) {

        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View sheetView = getLayoutInflater()
                .inflate(R.layout.bottom_sheet_rides, null);

        dialog.setContentView(sheetView);

        sheetView.findViewById(R.id.btn_live)
                .setOnClickListener(v -> {
                    dialog.dismiss();
                    navController.navigate(R.id.userRideTrackingFragment);
                });

        sheetView.findViewById(R.id.btn_scheduled)
                .setOnClickListener(v -> {
                    dialog.dismiss();
                    navController.navigate(R.id.scheduledRidesFragment);
                });

        dialog.show();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_user, container, false);
    }
}