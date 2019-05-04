package com.practikality.tarea;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class home_activity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    FloatingActionButton floatingActionButton;
    BottomAppBar bottomAppBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_activity);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        floatingActionButton = findViewById(R.id.fab);
        bottomAppBar = findViewById(R.id.bottom_app_bar);
        setSupportActionBar(bottomAppBar);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(home_activity.this, add_task_activity.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser==null){
            startActivity(new Intent(home_activity.this,login_activity.class));
            finish();
        }
        db = FirebaseFirestore.getInstance();
        mProgress = new ProgressDialog(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_todo:
                break;
            case R.id.action_bar_assigned:
                break;
            case R.id.action_bar_settings:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Want to logout?");

        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAuth.signOut();
                startActivity(new Intent(home_activity.this, login_activity.class));
                finish();
            }
        });
        alertDialogBuilder.setNeutralButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertDialogBuilder.show();
    }
}
