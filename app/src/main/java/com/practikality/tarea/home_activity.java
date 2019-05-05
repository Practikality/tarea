package com.practikality.tarea;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.button.MaterialButton;
import android.support.design.card.MaterialCardView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.practikality.tarea.adapters.TasksRecyclerAdapter;
import com.practikality.tarea.models.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class home_activity extends AppCompatActivity implements TasksRecyclerAdapter.OnTaskListener {

    public String mMethod;
    BottomNavigationView bottomNavigationView;
    FloatingActionButton floatingActionButton;
    BottomAppBar bottomAppBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    public RecyclerView mRecyclerView;
    public TasksRecyclerAdapter mTasksRecyclerAdapter;
    public ArrayList<Task> mTasks;
    public ArrayList<String> mTaskIDS;
    public String musername;
    public boolean usernamenotsetyet;
    private ProgressDialog mProgress;
    public String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_activity);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        floatingActionButton = findViewById(R.id.fab);
        usernamenotsetyet = true;
        mMethod = "from";
        bottomAppBar = findViewById(R.id.bottom_app_bar);
        setSupportActionBar(bottomAppBar);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home_activity.this, add_task_activity.class);
                intent.putExtra("username",musername);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mTasks = new ArrayList<>();
        mTaskIDS = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        mProgress = new ProgressDialog(this);
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(home_activity.this, login_activity.class));
            finish();
        } else {
            mRecyclerView = findViewById(R.id.tasks_rv);
            loadTasks(firebaseUser.getUid(),"from");
        }

    }

    public void loadTasks(String userid, final String method) {
        mUid = userid;
        mMethod = method;
        mProgress.setMessage("Fetching");
        mProgress.show();
        mTasks = new ArrayList<>();
        mTaskIDS = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(home_activity.this);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mTasksRecyclerAdapter = new TasksRecyclerAdapter(mTasks, home_activity.this);
        mRecyclerView.setAdapter(mTasksRecyclerAdapter);
        db.collection("users").document(userid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> map = documentSnapshot.getData();
                String username = String.valueOf(map.get("username"));
                musername = username;
                if (musername.length() > 0) {
                    db.collection("tasks").whereEqualTo(method, username).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots.size()>0){
                                List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                                for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                                    Task task = new Task();
                                    if(String.valueOf(documentSnapshot.get("status")).equals("rejected") && method.equals("from")){
                                        task.setTaskTo("Rejected by " + String.valueOf(documentSnapshot.get("to")));
                                        task.setPriority("rejected");
                                    }else if(String.valueOf(documentSnapshot.get("status")).equals("done") && method.equals("to")){
                                        task.setTaskTo(String.valueOf(documentSnapshot.get("to")));
                                        task.setPriority("completed");
                                    }
                                    else{
                                        task.setTaskTo(String.valueOf(documentSnapshot.get("to")));
                                        task.setPriority(String.valueOf(documentSnapshot.get("priority")));
                                    }
                                    task.setTitle(String.valueOf(documentSnapshot.get("title")));
                                    if((String.valueOf(documentSnapshot.get("status")).equals("rejected") || String.valueOf(documentSnapshot.get("status")).equals("done"))&& method.equals("to")){
                                        //
                                    }else{
                                        mTasks.add(task);
                                        mTaskIDS.add(documentSnapshot.getId());
                                    }
                                }
                                mTasksRecyclerAdapter.notifyDataSetChanged();
                                if(mTasks.size()>0){
                                    findViewById(R.id.text_view_empty_stack).setVisibility(View.GONE);
                                }else{
                                    findViewById(R.id.text_view_empty_stack).setVisibility(View.VISIBLE);
                                }
                            }else{
                                findViewById(R.id.text_view_empty_stack).setVisibility(View.VISIBLE);
                            }
                            mProgress.dismiss();
                        }
                    });
                } else {
                    makeSnackbar("Could not find user.");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        switch (item.getItemId()) {
            case R.id.action_bar_todo:
                loadTasks(firebaseUser.getUid(),"from");
                break;
            case R.id.action_bar_assigned:
                loadTasks(firebaseUser.getUid(),"to");
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

    public void makeSnackbar(String message) {
        Snackbar.make(findViewById(R.id.tasks_rv), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskClick(int position) {
        final String taskID = mTaskIDS.get(position);
        db.collection("tasks").document(taskID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final Map<String, Object> map = documentSnapshot.getData();
                String from = String.valueOf(map.get("from"));
                String to = String.valueOf(map.get("to"));
                String title = String.valueOf(map.get("title"));
                String description = String.valueOf(map.get("description"));
                String deadline = String.valueOf(map.get("deadline_time"))+ " on " + String.valueOf(map.get("deadline_date"));
                String status = String.valueOf(map.get("status"));
                int color = Color.rgb(238,238,238);
                String priority = String.valueOf(map.get("priority"));
                if(priority.equals("casual")){
                    color = Color.rgb(3,169,244);
                }else if(priority.equals("important")){
                    color = Color.rgb(255,183,77);
                }else if(priority.equals("urgent")){
                    color = Color.rgb(255,138,101);
                }


                AlertDialog.Builder builder = new AlertDialog.Builder(home_activity.this);
                LayoutInflater layoutInflater = home_activity.this.getLayoutInflater();
                builder.setView(layoutInflater.inflate(R.layout.dialog_view_task, null));
                final AlertDialog alertDialog = builder.create();
                try {
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                alertDialog.show();

                MaterialCardView materialCardView = alertDialog.findViewById(R.id.dialog_card_view);
                materialCardView.setBackgroundTintList(ColorStateList.valueOf(color));
                TextView textView = alertDialog.findViewById(R.id.dialog_text_view);
                textView.setText("From: " + from + "\nTo: " + to + "\nTitle: " + title + "\nDescription: " + description + "\nDeadline: " + deadline);

                MaterialButton dialogcancelbutton = alertDialog.findViewById(R.id.dialog_cancel_button);
                if(mMethod.equals("from")){
                    dialogcancelbutton.setText("Delete");
                }else{
                    dialogcancelbutton.setText("Reject");
                }
                dialogcancelbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mMethod.equals("from")){
                            db.collection("tasks").document(taskID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    makeSnackbar("Task Deleted!");
                                    alertDialog.dismiss();
                                    loadTasks(mUid,mMethod);
                                }
                            });
                        }else{
                            db.collection("tasks").document(taskID).update("status","rejected").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    makeSnackbar("Task Rejected!");
                                    alertDialog.dismiss();
                                    loadTasks(mUid,mMethod);
                                }
                            });
                        }
                    }
                });

                MaterialButton dialogconfirmbutton = alertDialog.findViewById(R.id.dialog_confirm_button);
                if(mMethod.equals("from")){
                    dialogconfirmbutton.setText("Nudge");
                }else{
                    if(status.equals("done")){
                        dialogconfirmbutton.setText("Complete");
                    }else{
                        dialogconfirmbutton.setText("Done?");
                    }
                }
                dialogconfirmbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mMethod.equals("from")){
                            makeSnackbar("Nudge delivered");
                            alertDialog.dismiss();
                            loadTasks(mUid,mMethod);
                        }else{
                            db.collection("tasks").document(taskID).update("status","done").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    makeSnackbar("Task Completed!");
                                    alertDialog.dismiss();
                                    loadTasks(mUid,mMethod);
                                }
                            });
                        }
                    }
                });
            }
        });
    }
}
