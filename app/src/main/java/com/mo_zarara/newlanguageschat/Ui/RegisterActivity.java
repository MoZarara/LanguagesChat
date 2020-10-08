package com.mo_zarara.newlanguageschat.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mo_zarara.newlanguageschat.Ui.Fragments.MainActivity;
import com.mo_zarara.newlanguageschat.R;

public class RegisterActivity extends AppCompatActivity {

    private Button createAccountButton;
    private EditText userEmail, userPassword;
    private TextView alreadyHaveAccountLink;

    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;


    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        InitializeFields();

        alreadyHaveAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendUserToLoginActivity();
            }
        });



        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });

    }

    private void CreateNewAccount() {

        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please Enter Email...", Toast.LENGTH_LONG).show();
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please Enter password...", Toast.LENGTH_LONG).show();
        }
        else {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait while we creating a new account for you...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        String deviceToken = FirebaseInstanceId.getInstance().getToken();


                        String currentUserID = mAuth.getCurrentUser().getUid();
                        rootRef.child("Users").child(currentUserID).setValue("");


                        rootRef.child("Users").child(currentUserID).child("device_token")
                                .setValue(deviceToken);

                        /*sendUserToLoginActivity();*/
                        sendUserToMainActivity();
                        Toast.makeText(RegisterActivity.this, "account created successfully...", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    } else  {
                        String message = task.getException().toString();
                        Toast.makeText(RegisterActivity.this, "Error " + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });

        }

    }



    private void InitializeFields() {

        createAccountButton = (Button) findViewById(R.id.register_button);
        userEmail = (EditText) findViewById(R.id.register_email);
        userPassword = (EditText) findViewById(R.id.register_password);
        alreadyHaveAccountLink = (TextView) findViewById(R.id.already_have_account_link);

        loadingBar = new ProgressDialog(this);
    }



    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }


    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}
