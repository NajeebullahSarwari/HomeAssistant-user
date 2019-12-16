package com.example.homeassistant;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

 private EditText mEditTextFullName,mEditTextEmail,mEditTextPhoneNo,mEditTextPassword,mEditTextLocation,mEditTextProviderType;
 private Button mButtonSignup,mButtonFetchLocation;
    private FirebaseFirestore db ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //to set the title for form
        requireNonNull(getSupportActionBar()).setTitle("Sign Up");

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
    }

//Method for Fields Validations
    private boolean validateInputs(String name, String email, String password, String phone,String providerType, String location) {



        if (name.isEmpty()) {
            mEditTextFullName.setError("Name required");
            mEditTextFullName.requestFocus();
            return true;
        }
        if (email.isEmpty()) {
            mEditTextEmail.setError("Email required");
            mEditTextEmail.requestFocus();
            return true;
        }
        if (password.isEmpty() || password.length()<8) {
            mEditTextPassword.setError("minimum 8 character Password required ");
            mEditTextPassword.requestFocus();
            return true;
        }
        if (phone.isEmpty() || phone.length()!=10 ) {
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
        String email = mEditTextEmail.getText().toString().trim();
        String password = mEditTextPassword.getText().toString().trim();
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
                            Toast.makeText(SignUp.this, "Succefully Registered and details saved", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });


        }
    }
}

