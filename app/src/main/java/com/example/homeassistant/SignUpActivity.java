package com.example.homeassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

 private EditText mEditTextFullName,mEditTextEmail,mEditTextPhoneNo,mEditTextPassword,mEditTextLocation,mEditTextProviderType;
 private Button mButtonSignup,mButtonFetchLocation;
    private FirebaseFirestore db ;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ///Actionbar and its title
        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Sign UP");
        }
        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        mEditTextFullName = findViewById(R.id.edit_text_fullname);
        mEditTextEmail = findViewById(R.id.edit_text_email);
        mEditTextPassword = findViewById(R.id.edit_text_password);
        mEditTextPhoneNo = findViewById(R.id.edit_text_phone);
        mEditTextProviderType = findViewById(R.id.edit_text_provider_type);
        mEditTextLocation = findViewById(R.id.edit_text_location);
        mButtonSignup = findViewById(R.id.button_signup);
        mButtonFetchLocation = findViewById(R.id.button_fetchLocation);
        findViewById(R.id.button_signup).setOnClickListener(this);
        db = FirebaseFirestore.getInstance();
        //Get hold of an instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
    }

//Method for Fields Validations
    private boolean validateInputs(String name, String email, String password, String phone,String providerType, String location) {



        if (name.isEmpty() ||!(Pattern.matches("[A-Za-z\\s]+",name))) {
            mEditTextFullName.setError("You must enter name to register! Name can only contain letters and space");
            mEditTextFullName.requestFocus();
            return true;
        }
        if (email.isEmpty() || !(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            mEditTextEmail.setError("Enter a valid email address");
            mEditTextEmail.requestFocus();
            return true;

        }
        if (password.isEmpty() || !(Pattern.matches("[^\\s]{6,}",password))) {
            mEditTextPassword.setError("Password must be at least 6 characters long! and can't contain spaces");
            mEditTextPassword.requestFocus();
            return true;
        }
        if (phone.isEmpty() || !(Pattern.matches("[0-9]{10}",phone)) ) {
            mEditTextPhoneNo.setError("Invalid!Please Enter 10 digits Phone Number");
            mEditTextPhoneNo.requestFocus();
            return true;
        }
        if (providerType.isEmpty()) {
            mEditTextProviderType.setError("Provider Type required");
            mEditTextProviderType.requestFocus();
            return true;
        }
        if (location.isEmpty()) {
            mEditTextLocation.setError("This field is required");
            mEditTextLocation.requestFocus();
            return true;
        }



        return false;
    }






    @Override
    public void onClick(View view) {
        final String email = mEditTextEmail.getText().toString().trim();
        final String password = mEditTextPassword.getText().toString().trim();
        String fullName = mEditTextFullName.getText().toString().trim();
        String phone = mEditTextPhoneNo.getText().toString().trim();
        String location = mEditTextLocation.getText().toString().trim();
        String providerType = mEditTextProviderType.getText().toString().trim();

        if (!validateInputs(fullName, email, password, phone, providerType, location)) {

            CollectionReference dbRef = db.collection("Providers");
            SignupModelClass information = new SignupModelClass(
                    fullName,
                    email,
                    providerType,
                    location,
                    phone
            );

            dbRef.add(information)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            mAuth.createUserWithEmailAndPassword(email,password);
                            Toast.makeText(SignUpActivity.this, "Succefully Registered and details are saved", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                            finish();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });


        }
    }





}

