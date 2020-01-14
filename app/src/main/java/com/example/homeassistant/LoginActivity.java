package com.example.homeassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private FirebaseAuth mAuth;
    private TextView mNoAccountLink,mForgotPasswordLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ///Actionbar and its title
        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {      //if statement because it may produce NullPointerException
            actionBar.setTitle("Sign In");

        }

        mAuth = FirebaseAuth.getInstance();
        mEditTextEmail = findViewById(R.id.edit_text_email);
        mEditTextPassword = findViewById(R.id.edit_text_password);
        mButtonLogin = findViewById(R.id.button_login);
        mNoAccountLink=findViewById(R.id.no_account_link);
        mForgotPasswordLink=findViewById(R.id.forgot_password_link);

        loadingBar=new ProgressDialog(this);
        loadingBar.setTitle("Login Account");
        loadingBar.setMessage("Logging In...");
        loadingBar.setCanceledOnTouchOutside(false);

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginAdmin();
            }
        });
        mNoAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class));

            }
        });
        //forgot password Textview Click
        mForgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRecoverPasswordDialog();
            }

            private void showRecoverPasswordDialog() {
                //AlertDialog
                AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Recover Password");
                //set layout linear layout
                LinearLayout linearLayout=new LinearLayout(LoginActivity.this);
                final EditText eTextEmail=new EditText(LoginActivity.this);
                eTextEmail.setHint("Email");
                eTextEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                /*sets the min width of a EditView to fit a text of n 'M' letters regardless of the actual text extension and text size*/
                eTextEmail.setMinEms(16);
                linearLayout.addView(eTextEmail);
                linearLayout.setPadding(10,10,10,10);
                builder.setView(linearLayout);
                //button Recover
                builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String RecoverEmail=eTextEmail.getText().toString().trim();
                        beginRecovery(RecoverEmail);




                    }
                });
                //button Cancel
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();


                    }
                });
                //show dialog
                builder.create().show();

            }
        });



    }

    private void beginRecovery(String recoverEmail) {
         final ProgressDialog progressdialog;
        //show ProgressDialog
        progressdialog=new ProgressDialog(this);
        progressdialog.setTitle("");
        progressdialog.setMessage("Sending Email...");
        progressdialog.setCanceledOnTouchOutside(false);
        progressdialog.show();
        mAuth.sendPasswordResetEmail(recoverEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            progressdialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Password reset link has been sent to your e-mail address!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            progressdialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Failed...", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressdialog.dismiss();
                        //get and Show proper error message
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void LoginAdmin() {

        final String email = mEditTextEmail.getText().toString().trim();
        final String password = mEditTextPassword.getText().toString().trim();
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
            DatabaseReference RootRef;
            RootRef= FirebaseDatabase.getInstance().getReference().child("Users");
            RootRef.orderByChild("Email").equalTo(email)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists())
                            {
                                mAuth.signInWithEmailAndPassword(email,password)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {

                                                if (task.isSuccessful() ) {
                                                    Log.d("HomeAssistant", "signInWithEmail:success");
                                                    loadingBar.dismiss();
                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                    finish();

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
                            else{
                                loadingBar.dismiss();
                                Toast.makeText(LoginActivity.this, "User with the Email "+email+" dose not exists!", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



        }
    }





}
