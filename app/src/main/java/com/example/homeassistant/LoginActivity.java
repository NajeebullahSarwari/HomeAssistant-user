package com.example.homeassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private EditText mEditTextUserName,mEditTextEmail,mEditTextPassword;
    private Button mButtonLogin;
    private ProgressDialog loadingBar;
    private String ParentDbName="Users";
    private FirebaseAuth mAuth;
    private TextView mAdminLink,mNotAdminLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ///Actionbar and its title
        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {      //if statement because it may produce NullPointerException
            actionBar.setTitle("Sign In");
            //enable back button
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();
        mEditTextEmail = findViewById(R.id.edit_text_email);
        mEditTextPassword = findViewById(R.id.edit_text_password);
        mButtonLogin = findViewById(R.id.button_login);
        mAdminLink=findViewById(R.id.admin_panel_link);
        mNotAdminLink=findViewById(R.id.not_admin_panel_link);
        loadingBar=new ProgressDialog(this);
        loadingBar.setTitle("Login Account");
        loadingBar.setMessage("Logging In...");
        loadingBar.setCanceledOnTouchOutside(false);

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }
        });

        mAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mButtonLogin.setText("Login Admin");
                mAdminLink.setVisibility(View.INVISIBLE);
                mNotAdminLink.setVisibility(View.VISIBLE);
                ParentDbName="Admins";
            }
        });
        mNotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mButtonLogin.setText("Login");
                mAdminLink.setVisibility(View.VISIBLE);
                mNotAdminLink.setVisibility(View.INVISIBLE);
                ParentDbName="Users";
            }
        });
    }

    private void LoginUser() {

        final String email = mEditTextEmail.getText().toString().trim();
        String password = mEditTextPassword.getText().toString().trim();
        if (email.isEmpty() || !(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            mEditTextEmail.setError("Enter a valid email address");
            mEditTextEmail.requestFocus();
        }
        else if (password.isEmpty() || !(Pattern.matches("[^\\s]{6,}",password))) {
            mEditTextPassword.setError("Password must be at least 6 characters long! and can't contain spaces");
            mEditTextPassword.requestFocus();
        }
        else
        {
            loadingBar.show();


            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful() ) {
                                DatabaseReference RootRef;
                                RootRef= FirebaseDatabase.getInstance().getReference().child(ParentDbName);
                                RootRef.orderByChild("Email").equalTo(email)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                       // for(DataSnapshot id:dataSnapshot.getChildren())

                                        if(dataSnapshot.exists() && ParentDbName.equals("Users"))
                                        {
                                            Log.d("HomeAssistant", "signInWithEmail:success");
                                            loadingBar.dismiss();
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                                            finish();
                                        }
                                       else if(dataSnapshot.exists() && ParentDbName.equals("Admins"))
                                        {

                                            Log.d("HomeAssistant", "signInWithEmail:success");
                                            loadingBar.dismiss();
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            startActivity(new Intent(getApplicationContext(),AdminActivity.class));
                                            finish();
                                        }
                                       else{
                                           loadingBar.dismiss();
                                           Toast.makeText(LoginActivity.this, "Wrong Credentials", Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });






                            }

                            else {
                                // If sign in fails, display a message to the user.
                                loadingBar.dismiss();
                                Log.w("HomeAssistant", "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();

                            }


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();


                }
            });

        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();  //go previous activity
        return super.onSupportNavigateUp();
    }


}
