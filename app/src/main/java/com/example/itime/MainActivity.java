package com.example.itime;

import android.content.Intent;
import android.os.Bundle;

import com.example.itime.ui.home.HomeFragment;
import com.example.itime.ui.label.LabelFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        Toolbar toolbar = findViewById (R.id.toolbar);
        setSupportActionBar (toolbar);

        //布局自带的
        DrawerLayout drawer = findViewById (R.id.drawer_layout);
        NavigationView navigationView = findViewById (R.id.nav_view);
        // 将每个菜单ID作为一组ID传递
        // menu should be considered as top level destinations.菜单应被视为顶级目的地
        mAppBarConfiguration = new AppBarConfiguration.Builder (
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout (drawer)
                .build ( );
        NavController navController = Navigation.findNavController (this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController (this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController (navigationView, navController);
        //以上看不懂

//        HomeFragment homeFragment=new HomeFragment ();
//        LabelFragment labelFragment=new LabelFragment ();
//        getSupportFragmentManager ().beginTransaction ().replace (R.id.nav_home,homeFragment,"homeFragment").commit ();
//        getSupportFragmentManager ().beginTransaction ().replace (R.id.nav_gallery,labelFragment,"labelFragment").commit ();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ( ).inflate (R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController (this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp (navController, mAppBarConfiguration)
                || super.onSupportNavigateUp ( );
    }
}
