package com.example.proje;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.proje.Fragment.AnasayfaFragment;
import com.example.proje.Fragment.NotificationFragment;
import com.example.proje.Fragment.ProfileFragment;
import com.example.proje.Fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
BottomNavigationView bottomNavigationView;
Fragment selectedFragment=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

Bundle intent = getIntent().getExtras();
if (intent != null)
{
    String gonderen = intent.getString("gonderenid");
    SharedPreferences.Editor editor = getSharedPreferences("PREFS",MODE_PRIVATE).edit();
    editor.putString("profileid",gonderen);
    editor.apply();
    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
}
else
{
    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AnasayfaFragment()).commit();
}

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AnasayfaFragment()).commit();

    }
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()){

                        case R.id.nav_home:
                            selectedFragment= new AnasayfaFragment();

                            break;
                        case R.id.nav_search:
                            selectedFragment= new SearchFragment();
                            break;
                        case R.id.nav_add:
                            selectedFragment= null;
                            startActivity(new Intent(MainActivity.this,PostActivity.class));
                            break;
                        case R.id.nav_notfy:
                            selectedFragment= new NotificationFragment();
                            break;
                        case R.id.nav_profile:
                            SharedPreferences.Editor editor = getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                            editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            editor.apply();
                            selectedFragment = new ProfileFragment();
                            break;

                    }
                    if (selectedFragment !=null){
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                    }

                    return true;
                }
            };
}