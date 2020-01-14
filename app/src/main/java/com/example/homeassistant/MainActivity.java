package com.example.homeassistant;

import androidx.annotation.NonNull;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
   private FirebaseAuth mAuth;
   private NavigationView mNavigationView;
   private DrawerLayout mDrawerLayout;
   private ActionBarDrawerToggle mActionBarDrawerToggle;
   private Toolbar mToolbar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar= findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");

        //init
        mAuth=FirebaseAuth.getInstance();
        mDrawerLayout=findViewById(R.id.drawable_layout);
        //to show hamburger
        mActionBarDrawerToggle=new ActionBarDrawerToggle(MainActivity.this,mDrawerLayout,R.string.drawer_open,R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNavigationView=findViewById(R.id.navigation_view);
        View navView=mNavigationView.inflateHeaderView(R.layout.navigation_header);

        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //to include Navigation header in navagation drawer
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                UserMenuSelector(menuItem);
                return false;
            }
        });


    }

    private void UserMenuSelector(MenuItem menuItem) {
        switch (menuItem.getItemId())
        {
            case R.id.nav_home:
                Toast.makeText(this, "Home Clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_about_us:
                Toast.makeText(this, "About Us Clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_settings:
                Toast.makeText(this, "Settings Clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_rate:
                Toast.makeText(this, "Rate App clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_contact:
                Toast.makeText(this, "Contact Us Clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
               mAuth.signOut();
               startActivity(new Intent(getApplicationContext(),LoginActivity.class));
               finish();
                break;

        }

    }

    private  void checkUserStatus(){
        FirebaseUser user =mAuth.getCurrentUser();
             if(user!=null)
              {
            //user is signed stay here
            //set email of logged in user
                  }
        else{
            //user not signed in, go to main Activity
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            finish();
        }

    }

    @Override
    protected void onStart() {
        //check on start of app
        checkUserStatus();
        super.onStart();
    }

    /*inflate Options menu*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflating menu
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }
    /*handle menu item clicks*/

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //get item id
        int id=item.getItemId();
        if(id==R.id.action_logout){
            mAuth.signOut();
            checkUserStatus();
        }
        //to make hamburger worked
        if(mActionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
