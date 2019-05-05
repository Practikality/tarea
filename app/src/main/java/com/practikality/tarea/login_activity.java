package com.practikality.tarea;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class login_activity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressDialog mProgress;
    public String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser!=null){
            startActivity(new Intent(login_activity.this,home_activity.class));
            finish();
        }
        db = FirebaseFirestore.getInstance();
        mProgress = new ProgressDialog(this);
    }

    public void login(View view) {
        mProgress.setMessage("Logging in");
        mProgress.show();
        TextInputEditText emailet = findViewById(R.id.login_email_edit_text);
        final String email = valuefrominput(emailet);
        TextInputEditText passwordet = findViewById(R.id.login_password_edit_text);
        final String password = valuefrominput(passwordet);
        if (email.length() > 0 && password.length() > 0) {
            db.collection("users").whereEqualTo("username", email).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots.size() > 0) {
                        List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                            String emailaddress = String.valueOf(documentSnapshot.get("email"));
                            System.out.println(emailaddress);
                            userEmail = emailaddress;
                            tryloggingin(emailaddress, password);
                        }
                    } else {
                        makeSnackbar("Username not found");
                        mProgress.dismiss();
                    }
                }
            });
        } else {
            makeSnackbar("Enter your login details!");
            mProgress.dismiss();
        }
    }

    public void tryloggingin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(login_activity.this,home_activity.class));
                            finish();
                        } else {
                            makeSnackbar("Authentication Failed");
                        }
                        mProgress.dismiss();
                    }
                });
    }

    public String valuefrominput(TextInputEditText textInputEditText) {
        try {
            return textInputEditText.getText().toString();
        } catch (NullPointerException e) {
            return "notfilled";
        }
    }

    public void makeSnackbar(String message) {
        Snackbar.make(findViewById(R.id.login_email_edit_text), message, Snackbar.LENGTH_SHORT).show();
    }

    public void gotosignup(View view) {
        startActivity(new Intent(login_activity.this, sign_up_activity.class));
    }
}
