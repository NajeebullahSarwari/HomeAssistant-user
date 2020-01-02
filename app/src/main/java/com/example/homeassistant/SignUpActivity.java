package com.example.homeassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.regex.Pattern;


public class SignUpActivity extends AppCompatActivity  {

    private EditText mEditTextUserName,mEditTextEmail,mEditTextPassword;
    private Button mButtonSignup;


    private FirebaseFirestore db ;
    private FirebaseAuth mAuth;
    //Progressbar to display while registering and Saving User details
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ///Actionbar and its title
        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {      //if statement because it may produce NullPointerException
            actionBar.setTitle("Sign UP");
            //enable back button
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mEditTextUserName=findViewById(R.id.edit_text_username);
        mEditTextEmail = findViewById(R.id.edit_text_email1);
        mEditTextPassword = findViewById(R.id.edit_text_password1);
        mButtonSignup = findViewById(R.id.button_signup);

        db = FirebaseFirestore.getInstance();
        //Get hold of an instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        loadingBar=new ProgressDialog(this);
        loadingBar.setTitle("Create Account");
        loadingBar.setMessage("Please Wait,while we are checking the credentials.");
        loadingBar.setCanceledOnTouchOutside(false);

        mButtonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();  //go previous activity
        return super.onSupportNavigateUp();
    }



    private void createAccount() {
        String email = mEditTextEmail.getText().toString().trim();
        String password = mEditTextPassword.getText().toString().trim();
        String userName = mEditTextUserName.getText().toString().trim();

        if (userName.isEmpty() ||!(Pattern.matches("[A-Za-z\\s]+",userName))) {
            mEditTextUserName.setError("You must enter name to register! Name can only contain letters and space");
            mEditTextUserName.requestFocus();

        }
        else if (email.isEmpty() || !(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            mEditTextEmail.setError("Enter a valid email address");
            mEditTextEmail.requestFocus();


        }
       else if (password.isEmpty() || !(Pattern.matches("[^\\s]{6,}",password))) {
            mEditTextPassword.setError("Password must be at least 6 characters long! and can't contain spaces");
            mEditTextPassword.requestFocus();

        }
       else {
            //if inputs are valid, Show Progress Dialog and start registering user
            loadingBar.show();
            validateEmail(userName,email,password);






        }
    }

    private void validateEmail(final String userName, final String email, final String password) {

        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference("Users");
        final String id=RootRef.push().getKey();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


              //check email already exist or not.
              mAuth.fetchSignInMethodsForEmail(email)
                      .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                          @Override
                          public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                              boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

                              if (isNewUser) {

                  HashMap<String,Object> userData=new HashMap<>();
                  userData.put("Email",email);
                  userData.put("Password",password);
                  userData.put("Name",userName);

                  RootRef.child(id).setValue(userData)
                          .addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {

                                  if(task.isSuccessful())
                                  {
                                      mAuth.createUserWithEmailAndPassword(email, password);
                                      Toast.makeText(SignUpActivity.this, "Congratulations, your account has been created", Toast.LENGTH_SHORT).show();
                                      loadingBar.dismiss();
                                      startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                                      finish();
                                  }
                                  else
                                  {
                                      loadingBar.dismiss();
                                      Toast.makeText(SignUpActivity.this, "Network Error: Please try again after some time...", Toast.LENGTH_SHORT).show();
                                  }

                              }
                          });



             }
             else
              {

                  loadingBar.dismiss();
                  Toast.makeText(SignUpActivity.this, "Email Address "+email+" already exists\nPlease try again using another Email or Login ", Toast.LENGTH_SHORT).show();

                  startActivity(new Intent(getApplicationContext(),MainActivity.class));
              }   }
                      });

          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });
    }




}

