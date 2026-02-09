package inc.visor.voom.app.admin.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import inc.visor.voom.app.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainAdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainAdminFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MainAdminFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainAdminFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainAdminFragment newInstance(String param1, String param2) {
        MainAdminFragment fragment = new MainAdminFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_admin, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {

        NavController navController = NavHostFragment.findNavController(
                getChildFragmentManager().findFragmentById(R.id.admin_nav_host)
        );

        BottomNavigationView bottomNav = view.findViewById(R.id.bottom_navigation_view);

        bottomNav.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                navController.navigate(R.id.editPricingFragment);
                return true;
            }

            if (id == R.id.nav_tracking) {
                navController.navigate(R.id.adminTrackingFragment);
                return true;
            }

            if (id == R.id.nav_insights) {
                showInsightsSheet(navController);
                return false;
            }

            if (id == R.id.nav_activity) {
                navController.navigate(R.id.chatListFragment);
            }

            if (id == R.id.nav_profile) {
                navController.navigate(R.id.adminProfileFragment);
                return true;
            }

            return false;
        });

    }

    private void showInsightsSheet(NavController navController) {

        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View sheetView = getLayoutInflater()
                .inflate(R.layout.bottom_sheet_insights, null);

        dialog.setContentView(sheetView);

        sheetView.findViewById(R.id.btn_users)
                .setOnClickListener(v -> {
                    dialog.dismiss();
                    navController.navigate(R.id.adminUsersFragment);
                });

        sheetView.findViewById(R.id.btn_statistics)
                .setOnClickListener(v -> {
                    dialog.dismiss();
                    navController.navigate(R.id.adminStatisticsFragment);
                });

        dialog.show();
    }

}