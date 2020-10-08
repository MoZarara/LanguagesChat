package com.mo_zarara.newlanguageschat.Ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.mo_zarara.newlanguageschat.Adapters.MessageAdapter;
import com.mo_zarara.newlanguageschat.Models.Messages;
import com.mo_zarara.newlanguageschat.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String messageReceiverID,
            messageReceiverName,
            messageReceiverImage,
            currentUserId;

    TextView userName, userLastSeen;
    CircleImageView userImage;

    Toolbar chatToolBar;

    private FirebaseAuth mAuth;
    DatabaseReference rootRef;

    ImageButton sendMessageButton, sendFilesButton;
    EditText messageInputText;


    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    MessageAdapter messageAdapter;
    RecyclerView userMessagesList;

    private String saveCurrentTime, saveCurrentDate;

    private String checker = "", myUrl = "";
    private StorageTask uploadTask;
    private Uri fileUri;

    private static final int REQUEST_CODE = 100;

    private ProgressDialog loadingBar;

    ImageView videoCallBtn;

    String calledBy = "";


    private static final String TAG = "ChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();


        if (getIntent().getExtras().get("visit_user_id") != null)
            messageReceiverID = getIntent().getExtras().get("visit_user_id").toString();
        else if (getIntent().getExtras().get("back_calling_activity_id") != null)
            messageReceiverID = getIntent().getExtras().get("back_calling_activity_id").toString();


        if (getIntent().getExtras().get("visit_user_name") != null)
            messageReceiverName = getIntent().getExtras().get("visit_user_name").toString();
        /*else if (getIntent().getExtras().get("back_calling_activity_name") != null)
            messageReceiverName = getIntent().getExtras().get("back_calling_activity_name").toString();*/

        if (getIntent().getExtras().get("visit_image") != null)
            messageReceiverImage = getIntent().getExtras().get("visit_image").toString();
        /*else if (getIntent().getExtras().get("back_calling_activity_image") != null)
            messageReceiverImage = getIntent().getExtras().get("back_calling_activity_image").toString();*/

        IntiaizeControllers();

        userName.setText(messageReceiverName);
        Picasso.get().load(messageReceiverImage).placeholder(R.drawable.profile_image).into(userImage);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        DisplayLastSeen();

        sendFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[] {
                        "Images",
                        "PDF Files",
                        "Ms Word Files"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Select the file");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            checker = "image";

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent, "Select Image"), REQUEST_CODE);
                        }

                        if (which == 1) {
                            checker = "pdf";

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/pdf");
                            startActivityForResult(intent.createChooser(intent, "Select PDF File"), REQUEST_CODE);
                        }

                        if (which == 2) {
                            checker = "docx";

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/msword");
                            startActivityForResult(intent.createChooser(intent, "Select Ms Word File"), REQUEST_CODE);
                        }
                    }
                });
                builder.show();
            }
        });



        rootRef.child("Messages").child(currentUserId).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Messages messages = dataSnapshot.getValue(Messages.class);

                        messagesList.add(messages);

                        messageAdapter.notifyDataSetChanged();

                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        //CheckForReceivingCall_2();
    }








    private void IntiaizeControllers() {
        chatToolBar = (Toolbar) findViewById(R.id.chat_toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater =
                (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(actionBarView);


        userImage = (CircleImageView) findViewById(R.id.custom_profile_image);
        userName = (TextView) findViewById(R.id.custom_profile_name);
        userLastSeen = (TextView) findViewById(R.id.custom_user_last_seen);

        sendMessageButton = (ImageButton) findViewById(R.id.send_message_btn);
        sendFilesButton = (ImageButton) findViewById(R.id.send_files_btn);
        messageInputText = (EditText) findViewById(R.id.input_message);

        //videoCallBtn = (ImageView) findViewById(R.id.video_call_toolbar_id);

        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList = (RecyclerView) findViewById(R.id.private_message_list_of_users);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);

        loadingBar = new ProgressDialog(this);


        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            loadingBar.setTitle("Sending file");
            loadingBar.setMessage("Please wait, we are sending that file...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();


            fileUri = data.getData();

            if (checker.equals("image")) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");

                final String messageSenderRef = "Messages/" + currentUserId + "/" + messageReceiverID;
                final String messageReceiverRef = "Messages/" + messageReceiverID + "/" + currentUserId;

                DatabaseReference userMessageKeyRef = rootRef.child("Messages")
                        .child(currentUserId).child(messageReceiverID).push();

                final String messagePushID = userMessageKeyRef.getKey();

                final StorageReference filePath = storageReference.child(messagePushID + "." + "jpg");

                uploadTask = filePath.putFile(fileUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {

                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            myUrl = downloadUri.toString();


                            Map messageTextBody = new HashMap();
                            messageTextBody.put("message", myUrl);
                            messageTextBody.put("name", fileUri.getLastPathSegment());
                            messageTextBody.put("type", checker);
                            messageTextBody.put("from", currentUserId);
                            messageTextBody.put("to", messageReceiverID);
                            messageTextBody.put("messageID", messagePushID);
                            messageTextBody.put("time", saveCurrentTime);
                            messageTextBody.put("date", saveCurrentDate);


                            Map messageBodyDetails = new HashMap();
                            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);

                            rootRef.updateChildren(messageBodyDetails)
                                    .addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {

                                            if (task.isSuccessful()) {
                                                loadingBar.dismiss();
                                                Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();

                                            } else {
                                                loadingBar.dismiss();
                                                Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                            }

                                            messageInputText.setText("");
                                        }
                                    });




                        }
                    }
                });


            } else if (!checker.equals("image")) {

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Document Files");

                final String messageSenderRef = "Messages/" + currentUserId + "/" + messageReceiverID;
                final String messageReceiverRef = "Messages/" + messageReceiverID + "/" + currentUserId;

                DatabaseReference userMessageKeyRef = rootRef.child("Messages")
                        .child(currentUserId).child(messageReceiverID).push();

                final String messagePushID = userMessageKeyRef.getKey();

                final StorageReference filePath = storageReference.child(messagePushID + "." + checker);

                filePath.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                Map messageTextBody = new HashMap();
                                messageTextBody.put("message", uri.toString());
                                messageTextBody.put("name", fileUri.getLastPathSegment());
                                messageTextBody.put("type", checker);
                                messageTextBody.put("from", currentUserId);
                                messageTextBody.put("to", messageReceiverID);
                                messageTextBody.put("messageID", messagePushID);
                                messageTextBody.put("time", saveCurrentTime);
                                messageTextBody.put("date", saveCurrentDate);


                                Map messageBodyDetails = new HashMap();
                                messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                                messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);

                                rootRef.updateChildren(messageBodyDetails);
                                loadingBar.dismiss();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingBar.dismiss();
                        Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double p = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        loadingBar.setMessage((int) p + " %  Uploading....");
                    }
                });

            } else {
                loadingBar.dismiss();
                Toast.makeText(this, "Nothing selected, Error.", Toast.LENGTH_SHORT).show();
            }




        }

    }

    private void DisplayLastSeen() {

        rootRef.child("Users").child(messageReceiverID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("userState").hasChild("state")) {
                            String state = dataSnapshot.child("userState").child("state").getValue().toString();
                            String date = dataSnapshot.child("userState").child("date").getValue().toString();
                            String time = dataSnapshot.child("userState").child("time").getValue().toString();

                            if (state.equals("online")) {
                                userLastSeen.setText("online");
                            } else if (state.equals("offline")) {
                                userLastSeen.setText("Last Seen: " + date + " " + time);
                            }
                        } else {
                            userLastSeen.setText("offline");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }



    private void sendMessage() {
        String messageText = messageInputText.getText().toString();
        
        if (TextUtils.isEmpty(messageText)) {

            Toast.makeText(this, "First write your message...", Toast.LENGTH_SHORT).show();
        } else {
            String messageSenderRef = "Messages/" + currentUserId + "/" + messageReceiverID;
            String messageReceiverRef = "Messages/" + messageReceiverID + "/" + currentUserId;

            DatabaseReference userMessageKeyRef = rootRef.child("Messages")
                    .child(currentUserId).child(messageReceiverID).push();

            String messagePushID = userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", currentUserId);
            messageTextBody.put("to", messageReceiverID);
            messageTextBody.put("messageID", messagePushID);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("date", saveCurrentDate);


            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);

            rootRef.updateChildren(messageBodyDetails)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            if (task.isSuccessful()) {

                                Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }

                            messageInputText.setText("");
                        }
                    });


        }
        
    }
}
