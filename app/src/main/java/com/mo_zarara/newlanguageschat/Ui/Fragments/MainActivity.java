package com.mo_zarara.newlanguageschat.Ui.Fragments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mo_zarara.newlanguageschat.R;
import com.mo_zarara.newlanguageschat.Ui.FindFriendsActivity;
import com.mo_zarara.newlanguageschat.Ui.LoginActivity;
import com.mo_zarara.newlanguageschat.Ui.SettingsActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdapter myTabsAccessorAdapter;


    //private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    String currentUserID;


    private int[] tabIcons = {
            R.drawable.chat,
            R.drawable.group,
            R.drawable.contact,
            R.drawable.friend_requests
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        //currentUser = mAuth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();


        //mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        //setSupportActionBar(mToolbar);
        //getSupportActionBar().setTitle("Languages Chat");


        ActionBar actionBar = getSupportActionBar();
        //actionBar.setLogo(R.drawable.phone);
        actionBar.setTitle("Languages Chat");
        //actionBar.setDisplayUseLogoEnabled(true);
        //actionBar.setDisplayShowHomeEnabled(true);


        myViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        myTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdapter);

        myTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);

        myTabLayout.getTabAt(0).setIcon(tabIcons[0]);
        myTabLayout.getTabAt(1).setIcon(tabIcons[1]);
        myTabLayout.getTabAt(2).setIcon(tabIcons[2]);
        myTabLayout.getTabAt(3).setIcon(tabIcons[3]);

        /*ImageView homeView = (ImageView) findViewById(R.id.anything_0_toolbar_id);
        ImageView profileView = (ImageView) findViewById(R.id.anything_1_toolbar_id);*/
        /*homeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
            }
        });

        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "profile", Toast.LENGTH_SHORT).show();
            }
        });*/

    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {

            sendUserToLoginActivity();
        } else {
            UpdateUsersStatus("online");
            verifyUserExistence();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (/*currentUserID*/ currentUser != null) {
            UpdateUsersStatus("offline");

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (currentUserID != null) {
            UpdateUsersStatus("offline");

        }

    }

    private void verifyUserExistence() {
        String currentUsetID = mAuth.getCurrentUser().getUid();
        rootRef.child("Users").child(currentUsetID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("name").exists()) {
                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                } else {
                    sendUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu, menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_option) {

            UpdateUsersStatus("offline");

            mAuth.signOut();
            sendUserToLoginActivity();
        }

        if (item.getItemId() == R.id.main_settings_option) {
            sendUserToSettingsActivity();
        }

        if (item.getItemId() == R.id.main_create_group_option) {
            RequestNewGroup();
        }

        if (item.getItemId() == R.id.main_find_friends_option) {
            sendUserToFindFriendsActivity();
        }

        /*if (item.getItemId() == R.id.anything_0_id) {
            Toast.makeText(this, "anything_0", Toast.LENGTH_SHORT).show();
        }*/

        /*if (item.getItemId() == R.id.anything_1_id) {
            Toast.makeText(this, "anything_1", Toast.LENGTH_SHORT).show();
        }*/

        return true;
    }

    private void RequestNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.myAlertDialog);
        builder.setTitle("Add Group Name :");

        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("New Group");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String groupName = groupNameField.getText().toString();
                if (TextUtils.isEmpty(groupName)) {
                    Toast.makeText(MainActivity.this, "Please write group name...", Toast.LENGTH_SHORT).show();
                } else {

                    CreateNewGroup(groupName);
                }
            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String groupName = groupNameField.getText().toString();
                if (TextUtils.isEmpty(groupName)) {
                    dialog.cancel();
                }
            }
        });


        builder.show();


    }

    private void CreateNewGroup(final String groupName) {
        rootRef.child("Groups").child(groupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               if (task.isSuccessful()) {
                   Toast.makeText(MainActivity.this, groupName + " group is created successfully...", Toast.LENGTH_SHORT).show();
               }
            }
        });
    }


    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }

    private void sendUserToSettingsActivity() {
        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    private void sendUserToFindFriendsActivity() {
        Intent findFrindesIntent = new Intent(MainActivity.this, FindFriendsActivity.class);
        startActivity(findFrindesIntent);
    }


    private void UpdateUsersStatus(String state) {
        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state", state);

        if (mAuth.getCurrentUser() != null)
            currentUserID = mAuth.getCurrentUser().getUid();
        else
            startActivity(new Intent(MainActivity.this, LoginActivity.class));

        rootRef.child("Users").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);

    }

}
