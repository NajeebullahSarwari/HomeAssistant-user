package com.example.homeassistant;

import androidx.annotation.NonNull;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
   private FirebaseAuth mAuth;
   private FirebaseDatabase mFirebaseDatabase;
   private DatabaseReference mDatabaseReference;
   FirebaseUser user;
   CircleImageView mCircleImageView;
   private NavigationView mNavigationView;
   private DrawerLayout mDrawerLayout;
   private ActionBarDrawerToggle mActionBarDrawerToggle;
   private Toolbar mToolbar;
   private TextView NavProfileName;
   ImageView teacher;
   ImageView carpenter;
   ImageView mechanic;
   ImageView plumber;
   ImageView electrician;
   ImageView engineer;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar= findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");

        //init
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mDatabaseReference=mFirebaseDatabase.getReference("Users");
        mCircleImageView=findViewById(R.id.nav_profile_image);
        teacher=findViewById(R.id.teacher);
        carpenter = findViewById(R.id.carpenter);
        mechanic = findViewById(R.id.mechanic);
        plumber = findViewById(R.id.plumber);
        electrician = findViewById(R.id.electrician);
        engineer = findViewById(R.id.engineer);


        mDrawerLayout=findViewById(R.id.drawable_layout);
        //to show hamburger
        mActionBarDrawerToggle=new ActionBarDrawerToggle(MainActivity.this,mDrawerLayout,R.string.drawer_open,R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNavigationView=findViewById(R.id.navigation_view);
        View navView=mNavigationView.inflateHeaderView(R.layout.navigation_header);
        NavProfileName=navView.findViewById(R.id.nav_user_fullname);

        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //to include Navigation header in navagation drawer
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                UserMenuSelector(menuItem);
                //when menu item is clicked we want to close the navigation drawer
                DrawerLayout drawer =  findViewById(R.id.drawable_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });


teacher.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent i=new Intent(MainActivity.this,ItemsActivity.class);
        startActivity(i);
    }
});
carpenter.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent i = new Intent(MainActivity.this,CarpenterViewItemActivity.class);
        startActivity(i);
    }
});
        mechanic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,MechanicViewItemActivity.class);
                startActivity(i);
            }
        });
        plumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,PlumberViewItemActivity.class);
                startActivity(i);
            }
        });
        electrician.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,ElectricianViewItemActivity.class);
                startActivity(i);
            }
        });
        engineer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,EngineerViewItemActivity.class);
                startActivity(i);
            }
        });



    }

    private void setProfileDetails() {
        if(user!=null) {

            Query query = mDatabaseReference.orderByChild("Email").equalTo(user.getEmail());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //check until required data get
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        //get Data
                        String name = "" + ds.child("Name").getValue();
                        String email = "" + ds.child("Email").getValue();
                        
                        //set Data
                        NavProfileName.setText(name);




                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{
            //user not signed in, go to Login Activity
            goToLoginActivity();
        }
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



    @Override
    protected void onStart() {
        //check on start of app
        setProfileDetails();
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
            goToLoginActivity();


        }
        //to make hamburger worked
        if(mActionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToLoginActivity() {
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
        finish();
    }
}
