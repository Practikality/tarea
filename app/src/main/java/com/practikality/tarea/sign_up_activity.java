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

import org.w3c.dom.Text;

import java.util.HashMap;

public class sign_up_activity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mProgress = new ProgressDialog(this);
    }

    public void signup(View view){
        mProgress.setMessage("Signing Up");
        mProgress.show();
        TextInputEditText usernameet = findViewById(R.id.signup_username_edit_text);
        TextInputEditText emailet = findViewById(R.id.signup_email_edit_text);
        TextInputEditText passwordet = findViewById(R.id.signup_password_edit_text);
        final String username = valuefrominput(usernameet);
        final String email = valuefrominput(emailet);
        final String password = valuefrominput(passwordet);
        if(!username.equals("notfilled")){
            if(!email.equals("notfilled")){
                if(!password.equals("notfilled")){
                    db.collection("users").whereEqualTo("username",username).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            int numberofusers = queryDocumentSnapshots.size();
                            if(numberofusers>0){
                                makeSnackbar("Username already exists!");
                                mProgress.dismiss();
                            }else{
                                signUpUser(email, password, username);
                            }
                        }
                    });
                }else{
                    makeSnackbar("Password cannot be empty");
                    mProgress.dismiss();
                }
            }else{
                makeSnackbar("Email cannot be empty");
                mProgress.dismiss();
            }
        }else{
            makeSnackbar("Username cannot be empty");
            mProgress.dismiss();
        }
    }

    public void signUpUser(final String email, String password, final String username){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userid = user.getUid();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("username",username);
                            map.put("email",email);
                            db.collection("users").document(userid).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mProgress.dismiss();
                                    Toast.makeText(getApplicationContext(), "Signed up succesfully!",Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(sign_up_activity.this, login_activity.class));
                                    mAuth.signOut();
                                    finish();
                                }
                            });
                        } else {
                            makeSnackbar("Authentication failed!");
                        }
                    }
                });
    }

    public void cancel(View view){
        finish();
    }

    public void makeSnackbar(String message){
        Snackbar.make(findViewById(R.id.signup_password_edit_text),message,Snackbar.LENGTH_SHORT).show();
    }

    public String valuefrominput(TextInputEditText textInputEditText){
        try{
            return textInputEditText.getText().toString();
        }catch (NullPointerException e){
            return "notfilled";
        }
    }
}
