package com.example.homeassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
   private FirebaseAuth mAuth;
   private TextView mTextViewProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {      //if statement because it may produce NullPointerException
            actionBar.setTitle("Profile");}
        //init
        mAuth=FirebaseAuth.getInstance();
        mTextViewProfile=findViewById(R.id.text_view_profile);

    }

    private  void checkUserStatus(){
        FirebaseUser user =mAuth.getCurrentUser();
        if(user!=null)
        {
            //user is signed stay here
            //set email of logged in user
            mTextViewProfile.setText(user.getEmail());
        }
        else{
            //user not signed in, go to main Activity
            startActivity(new Intent(ProfileActivity.this,MainActivity.class));
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

        return super.onOptionsItemSelected(item);
    }
}
