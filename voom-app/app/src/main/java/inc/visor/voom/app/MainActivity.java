package inc.visor.voom.app;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import org.osmdroid.config.Configuration;

import inc.visor.voom.app.shared.DataStoreManager;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private DataStoreManager dataStoreManager;
    private NavController navController;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Configuration.getInstance().setUserAgentValue(getPackageName());
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        dataStoreManager = DataStoreManager.getInstance(this);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.main_nav_host);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

        Uri data = getIntent().getData();
        if (data != null) {
            Log.d("DEEPLINK", "URI: " + data);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        checkLoginStatusAndNavigate();
    }

    private void checkLoginStatusAndNavigate() {
        final Disposable disposable = this.dataStoreManager
            .isLoggedIn()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(isLoggedIn -> {
//                if (isLoggedIn) {
//                    getUserTypeAndNavigate();
//                } else {
                    navigateToLogin();
//                }
            },
            throwable -> {
                Log.e("MainActivity", "Error checking login status", throwable);
                navigateToLogin();
            }
        );
        compositeDisposable.add(disposable);
    }

    private void getUserTypeAndNavigate() {
        final Disposable disposable = dataStoreManager
            .getUserRole()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(userType -> {
                switch (userType) {
                    case "DRIVER":
                        navigateToDriverHome();
                        break;
                    case "USER":
                        navigateToUserHome();
                        break;
                    case "ADMIN":
                        navigateToAdminHome();
                        break;
                    default:
                        navigateToLogin();
                        break;
                }
            },
            throwable -> {
                Log.e("MainActivity", "Error getting user type", throwable);
                navigateToLogin();
            }
        );
        compositeDisposable.delete(disposable);
    }
    private void navigateToLogin() {
        if (navController != null) {
            navController.navigate(R.id.action_splashFragment_to_loginFragment);
        }
    }

    private void navigateToDriverHome() {
        if (navController != null) {
            navController.navigate(R.id.action_splashFragment_to_mainDriverFragment);
        }
    }

    private void navigateToUserHome() {
        if (navController != null) {
            navController.navigate(R.id.action_splashFragment_to_mainUserFragment);
        }
    }

    private void navigateToAdminHome() {
        if (navController != null) {
            navController.navigate(R.id.mainAdminFragment);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.compositeDisposable.dispose();
    }
}

