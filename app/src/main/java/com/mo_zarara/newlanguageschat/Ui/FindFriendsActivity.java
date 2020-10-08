package com.mo_zarara.newlanguageschat.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mo_zarara.newlanguageschat.Models.Contacts;
import com.mo_zarara.newlanguageschat.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {

    Toolbar mToolbar;
    RecyclerView findFriendsRecyclerList;

    private DatabaseReference usersRef;

    //EditText searchFindFriends;
    ArrayList<Contacts> arrayList;

    //Contacts contacts;

    FirebaseAuth mAuth;
    String myID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        mAuth = FirebaseAuth.getInstance();
        myID = mAuth.getCurrentUser().getUid();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");


        findFriendsRecyclerList = (RecyclerView) findViewById(R.id.find_friend_recycler_list);
        findFriendsRecyclerList.setLayoutManager(new LinearLayoutManager(this));

        mToolbar = (Toolbar) findViewById(R.id.find_friends_toolbar);
        //setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");


        arrayList = new ArrayList<>();

        //searchFindFriends = (EditText) findViewById(R.id.search_find_friends);

        //contacts = new Contacts();

        /*String st = contacts.getTargetLanguages();
        char[] ararryChar = st.toCharArray();
        for (int i = 0; i < ararryChar.length; i++) {
            Log.d("myTagg", String.valueOf(ararryChar[i]));
        }*/

        /*searchFindFriends.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    search(s.toString());
                } else {
                    search("");
                }
            }
        });*/


    }
    String nativeLang;
    private void search(/*String s*/) {

        //final Query query = usersRef.orderByChild("nativeLanguages").startAt("ENGLISH")/*.endAt(nativeLang + "\uf8ff")*/;
        usersRef.child(myID).child("nativeLanguages").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Log.d("my000 : ", "native : " + dataSnapshot.getValue());
                nativeLang = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        usersRef.child(myID).child("targetLanguages").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Log.d("my000 : ", "target : " + dataSnapshot.getValue());
                String targetLang = dataSnapshot.getValue().toString();
                final Query query = usersRef.orderByChild("nativeLanguages").startAt(targetLang).endAt(targetLang + "\uf8ff");





                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            arrayList.clear();
                            for (DataSnapshot des: dataSnapshot.getChildren()) {
                                final Contacts contacts = des.getValue(Contacts.class);
                                if (contacts.getTargetLanguages().equals(nativeLang))
                                    arrayList.add(contacts);
                            }

                            FirebaseRecyclerOptions<Contacts> options =
                                    new FirebaseRecyclerOptions.Builder<Contacts>().
                                            setQuery(query, Contacts.class).
                                            build();


                            FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder> adapter =
                                    new FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder>(options) {
                                        @Override
                                        protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull Contacts model) {
                                            holder.userName.setText(model.getName());
                                            holder.userStatus.setText(model.getUserStatus());
                                            holder.userNativeLanguage.setText("Native language : " + model.getNativeLanguages());
                                            holder.userTargetLanguage.setText("Target language : " + model.getTargetLanguages());

                                            //Picasso.get().load(model.getStatus()).into(holder.userStatus);
                                            Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.profileImage);


                                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    String visit_user_id = getRef(position).getKey();

                                                    Intent profileIntent = new Intent(FindFriendsActivity.this, ProfileActivity.class);
                                                    profileIntent.putExtra("visit_user_id", visit_user_id);
                                                    startActivity(profileIntent);

                                                }
                                            });


                                        }

                                        @NonNull
                                        @Override
                                        public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                            View view = LayoutInflater.from(parent.getContext()).
                                                    inflate(R.layout.users_display_layout, parent, false);

                                            FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);

                                            return viewHolder;
                                        }
                                    };


                            findFriendsRecyclerList.setAdapter(adapter);
                            adapter.startListening();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });









            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }


    @Override
    protected void onStart() {
        super.onStart();

        search();

        /*FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>().
                        setQuery(usersRef, Contacts.class).
                        build();


        FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull Contacts model) {
                        holder.userName.setText(model.getName());
                        holder.userStatus.setText(model.getUserStatus());
                        holder.userNativeLanguage.setText("Native language : " + model.getNativeLanguages());
                        holder.userTargetLanguage.setText("Target language : " + model.getTargetLanguages());
                        //Log.d("myTagg", "Target language : " + model.getTargetLanguages());
                        //Picasso.get().load(model.getStatus()).into(holder.userStatus);
                        Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.profileImage);


                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String visit_user_id = getRef(position).getKey();

                                Intent profileIntent = new Intent(FindFriendsActivity.this, ProfileActivity.class);
                                profileIntent.putExtra("visit_user_id", visit_user_id);
                                startActivity(profileIntent);

                            }
                        });


                    }

                    @NonNull
                    @Override
                    public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.users_display_layout, parent, false);

                        FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);

                        return viewHolder;
                    }
                };


        findFriendsRecyclerList.setAdapter(adapter);
        adapter.startListening();*/


    }


    public static class FindFriendViewHolder extends RecyclerView.ViewHolder {

        TextView userName, userStatus, userNativeLanguage, userTargetLanguage;
        //ImageView userStatus;
        CircleImageView profileImage;


        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            userNativeLanguage = itemView.findViewById(R.id.user_native_language);
            userTargetLanguage = itemView.findViewById(R.id.user_target_language);
            profileImage = itemView.findViewById(R.id.users_profile_image);
        }


    }


}
