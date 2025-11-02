package com.banew.cw2025_client.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.banew.cw2025_client.ui.greetings.GreetingsActivity;
import com.banew.cw2025_client.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private MainPageModel mainPageModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        setUpPaddings();
        setUpNavigation();

        mainPageModel = new ViewModelProvider(this).get(MainPageModel.class);
        // kick user if not login
        if (mainPageModel.isShouldToSwitchToLogin()) {
            Intent intent = new Intent(this, GreetingsActivity.class);
            startActivity(intent);
        }

        // show errors
        mainPageModel.getLastException().observe(this, r -> {
            Toast.makeText(this,
                    r.getMessage(),
                    Toast.LENGTH_SHORT).show();
        });

        // swipeRefresh
        SwipeRefreshLayout swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(() -> {
            mainPageModel.refresh(() -> {
                swipeRefresh.setRefreshing(false);
            });
        });
    }

    private void setUpPaddings() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Видалити всі system insets padding'и
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.menu), (v, insets) -> {
            return WindowInsetsCompat.CONSUMED;
        });
    }

    private void setUpNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            BottomNavigationView navView = findViewById(R.id.menu);
            NavigationUI.setupWithNavController(navView, navController);
        }
    }
}