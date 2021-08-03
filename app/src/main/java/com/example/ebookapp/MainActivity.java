package com.example.ebookapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.room.Room;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.ebookapp.Fragments.Bookmark_Fragment;
import com.example.ebookapp.Fragments.Category_Fragment;
import com.example.ebookapp.Fragments.Download_Fragment;
import com.example.ebookapp.Fragments.Favourite_Fragment;
import com.example.ebookapp.Fragments.Home_Fragment;
import com.example.ebookapp.Fragments.SearchFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    public static Myappdatabas myappdatabas;
    MeowBottomNavigation meowBottomNavigation;
    Fragment selectedFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        myappdatabas = Room.databaseBuilder(getApplicationContext(),Myappdatabas.class,"userdb").allowMainThreadQueries().build();

        meowBottomNavigation = findViewById(R.id.bottom_nav);
        MeowBottomNavigation.ReselectListener reselectListener = new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {

            }
        };
        meowBottomNavigation.setOnReselectListener(reselectListener);

        meowBottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_category));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.ic_bookmark_filled));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.ic_homeicon));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(4, R.drawable.ic_favourite));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(5, R.drawable.ic_download));

        if (selectedFragment == null) {
            selectedFragment = new Home_Fragment();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

        meowBottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                if (item.getId() == 1) {
                    selectedFragment = new Category_Fragment();
                    toolbar.setTitle("Category");
                } else if (item.getId() == 2) {
                    selectedFragment = new Bookmark_Fragment();
                    toolbar.setTitle("Bookmark");
                } else if (item.getId() == 3) {
                    selectedFragment = new Home_Fragment();
                    toolbar.setTitle("Home");
                } else if (item.getId() == 4) {
                    selectedFragment = new Favourite_Fragment();
                    toolbar.setTitle("Favourites");
                } else if (item.getId() == 5) {
                    selectedFragment = new Download_Fragment();
                    toolbar.setTitle("Downloads");
                } else {
                    selectedFragment = new Home_Fragment();
                    toolbar.setTitle("Home");
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

            }
        });

        meowBottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                if (item.getId() != 1 && item.getId() != 2 && item.getId() != 3 && item.getId() != 4 && item.getId() != 5) {
                    meowBottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.ic_homeicon));
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem menuitem = menu.findItem(R.id.action_search);
        menuitem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SearchFragment()).addToBackStack(null).commit();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBackPressed() {

        if (!(selectedFragment instanceof Home_Fragment)) {

            meowBottomNavigation.setSelected(true);
            Log.i("bottomMenu: ", "onBackPressed: " + meowBottomNavigation.getId());
            if(meowBottomNavigation.getId() != 3){
                meowBottomNavigation.setId(3);
                selectedFragment = new Home_Fragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }
            return;
        }
        super.onBackPressed();
    }
}