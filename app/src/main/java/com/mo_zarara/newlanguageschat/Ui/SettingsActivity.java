package com.mo_zarara.newlanguageschat.Ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mo_zarara.newlanguageschat.Ui.Fragments.MainActivity;
import com.mo_zarara.newlanguageschat.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button updateAccountSettings;
    private EditText userName, userStatus;
    private CircleImageView userProfileImage;

    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    private static final int GALLERY_PICK = 1;
    private StorageReference userProfileImageRef;
    private ProgressDialog loadingBar;



    private Toolbar settingsToolBar;

    /*utton targetLanguageBtn;
    TextView targetLanguageText;
    String[] listItems;
    boolean[] checkedItems;
    ArrayList<Integer> mUserItems = new ArrayList<>();
    String itemLanguagesTarget_str;*/


    String nativeLanguages_str;
    int nativeLanguages_int;
    Button native_btn;
    TextView native_txt;
    //TextView nativeText;
    //Spinner spinnerShow;

    String targetLanguages_str;
    int targetLanguages_int;
    Button target_btn;
    TextView target_txt;
    String[] mlistLanguages = {"None",
            "CHINESE(中国人)",
            "SPANISH(Espanol)",
            "ENGLISH",
            "HINDI(भारतीय)",
            "ARABIC(العربيه)",
            "PORTUGUESE(Português)",
            "RUSSIAN(русский)",
            "JAPANESE(日本人)",
            "GERMAN(Deutsch)",
            "KOREAN(한국어)",
            "FRENCH(Le français)",
            "TURKISH(Türkçe)"};

    String multiNativeLanguages;
    String multiTargetLanguages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        InitializeFields();

        userName.setVisibility(View.INVISIBLE);

        updateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
            }
        });


        RetrieveUserInfo();


        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_PICK);
            }
        });


        native_btn = (Button) findViewById(R.id.native_button_languages_id);
        native_txt = (TextView) findViewById(R.id.native_text_languages_id);

        target_btn = (Button) findViewById(R.id.target_button_languages_id);
        target_txt = (TextView) findViewById(R.id.target_text_languages_id);


        rootRef.child("Users").child(currentUserID).child("nativeLanguages_int").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //nativeLang = dataSnapshot.getValue().toString();

                if (dataSnapshot.getValue() != null) {
                    //Log.d("uuum", "what : " + Integer.parseInt(dataSnapshot.getValue().toString()));
                    RadioBtnNativeLanguages(Integer.parseInt(dataSnapshot.getValue().toString()));
                }else {
                    RadioBtnNativeLanguages(nativeLanguages_int);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        rootRef.child("Users").child(currentUserID).child("targetLanguages_int").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //nativeLang = dataSnapshot.getValue().toString();

                if (dataSnapshot.getValue() != null) {
                    //Log.d("uuum", "what : " + Integer.parseInt(dataSnapshot.getValue().toString()));
                    RadioBtnTargetLanguages(Integer.parseInt(dataSnapshot.getValue().toString()));
                }else {
                    RadioBtnTargetLanguages(targetLanguages_int);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void RadioBtnNativeLanguages(final int checkItems){
        native_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle("Native languages");
                //builder.setMessage("Choose your native languages");

                builder.setSingleChoiceItems(mlistLanguages, checkItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nativeLanguages_int = which;
                        nativeLanguages_str = mlistLanguages[which];

                        native_txt.setText(mlistLanguages[which]);
                    }
                });

                /*builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
*/
                builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });


                AlertDialog mDialog = builder.create();
                mDialog.show();

            }
        });

    }

    public void RadioBtnTargetLanguages(final int checkItems){
        target_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle("Target languages");
                //builder.setMessage("Choose your native languages");

                builder.setSingleChoiceItems(mlistLanguages, checkItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        targetLanguages_int = which;
                        targetLanguages_str = mlistLanguages[which];

                        target_txt.setText(mlistLanguages[which]);
                    }
                });

                /*builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
*/
                builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });


                AlertDialog mDialog = builder.create();
                mDialog.show();

            }
        });

    }

    //String targetLang = "";
    @Override
    protected void onStart() {
        super.onStart();

        /*rootRef.child("Users").child(currentUserID).child("nativeLanguages").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("myTagg : ", "myMessage : " + dataSnapshot.getValue());
                nativeLanguages = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        spinnerShow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                position = nativeLanguages_int;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        /*String sss = "";
        for (int i = 0; i < listItems.length; i++) {
            sss = sss + listItems[i];
        }*/

        //spinnerShow.s

       // String[] myList = targetLang.split(", ");

        //Log.d("myTagg", "show00 :  " + Arrays.toString(myList));


    }

    /*private void checkedLanguageTarget() {
        targetLanguageBtn = findViewById(R.id.native_button_languages_id);
        targetLanguageText = findViewById(R.id.native_text_languages_id);

        listItems = getResources().getStringArray(R.array.target_language_array);
        checkedItems = new boolean[listItems.length];

        targetLanguageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingsActivity.this);
                mBuilder.setTitle("Target Languages");
                mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position, boolean isChecked) {
                        if (isChecked) {
                            if (!mUserItems.contains(position)) {
                                mUserItems.add(position);
                            }
                        } else if (mUserItems.contains(position)) {
                            //mUserItems.remove(position);
                            mUserItems.remove(mUserItems.indexOf(position));

                        }
                    }
                });
                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String item = "";
                        for (int i = 0; i < mUserItems.size(); i++) {
                            item = item + listItems[mUserItems.get(i)];
                            if (i != mUserItems.size() - 1) {
                                item = item + ", ";
                            }
                        }

                        targetLanguageText.setText(item);
                        itemLanguagesTarget_str = item;
                    }
                });
                mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                mBuilder.setNeutralButton("Clear all", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = false;
                            mUserItems.clear();
                            targetLanguageText.setText("");
                            itemLanguagesTarget_str = "";
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
    }*/

    private void UpdateSettings() {

        String setUserName = userName.getText().toString();
        String setStatus = userStatus.getText().toString();
        //String setLanguagesTarget = itemLanguagesTarget_str;


        if (TextUtils.isEmpty(setUserName)) {
            Toast.makeText(SettingsActivity.this, "Please write your user name first...", Toast.LENGTH_SHORT).show();
        }



        if (/*TextUtils.isEmpty(setStatus) &&*/ TextUtils.isEmpty(nativeLanguages_str)
                || nativeLanguages_int == 0
                || nativeLanguages_str == "None"
                || TextUtils.isEmpty(targetLanguages_str)
                || targetLanguages_int == 0
                || targetLanguages_str == "None") {

            Toast.makeText(SettingsActivity.this, "Please choose native and target languages...", Toast.LENGTH_SHORT).show();
        } else {

            /*MultiLanguageFunc(nativeLanguages_str, targetLanguages_str);
            MultiLanguageFunc(targetLanguages_str, nativeLanguages_str);*/

            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserID);
            profileMap.put("name", setUserName);
            profileMap.put("status", setStatus);
            profileMap.put("nativeLanguages", nativeLanguages_str);
            profileMap.put("nativeLanguages_int", nativeLanguages_int);
            profileMap.put("targetLanguages", targetLanguages_str);
            profileMap.put("targetLanguages_int", targetLanguages_int);
            /*profileMap.put("multiNativeLanguages", multiNativeLanguages);
            profileMap.put("multiTargetLanguages", multiTargetLanguages);*/

            //Log.d("multummm", "eeeee : " + multiNativeLanguages + multiTargetLanguages);
            //profileMap.put("targetLanguages", setLanguagesTarget);


            rootRef.child("Users").child(currentUserID).updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        sendUserToMainActivity();
                        Toast.makeText(SettingsActivity.this, "Profile updated successfully...", Toast.LENGTH_SHORT).show();
                    } else {
                        String message = task.getException().toString();
                        Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

    }

    /*public void MultiLanguageFunc(String nativeLang, String targetLang) {
        if (nativeLang == "CHINESE(中国人)" && targetLang == "SPANISH(Espanol)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "CHINESE(中国人)" && targetLang == "ENGLISH")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "CHINESE(中国人)" && targetLang == "HINDI(भारतीय)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "CHINESE(中国人)" && targetLang == "ARABIC(العربيه)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "CHINESE(中国人)" && targetLang == "PORTUGUESE(Português)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "CHINESE(中国人)" && targetLang == "RUSSIAN(русский)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "CHINESE(中国人)" && targetLang == "JAPANESE(日本人)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "CHINESE(中国人)" && targetLang == "GERMAN(Deutsch)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "CHINESE(中国人)" && targetLang == "KOREAN(한국어)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "CHINESE(中国人)" && targetLang == "FRENCH(Le français)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "CHINESE(中国人)" && targetLang == "TURKISH(Türkçe)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "SPANISH(Espanol)" && targetLang == "ENGLISH")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "SPANISH(Espanol)" && targetLang == "HINDI(भारतीय)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "SPANISH(Espanol)" && targetLang == "ARABIC(العربيه)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "SPANISH(Espanol)" && targetLang == "PORTUGUESE(Português)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "SPANISH(Espanol)" && targetLang == "RUSSIAN(русский)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "SPANISH(Espanol)" && targetLang == "JAPANESE(日本人)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "SPANISH(Espanol)" && targetLang == "GERMAN(Deutsch)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "SPANISH(Espanol)" && targetLang == "KOREAN(한국어)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "SPANISH(Espanol)" && targetLang == "FRENCH(Le français)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "SPANISH(Espanol)" && targetLang == "TURKISH(Türkçe)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "ENGLISH" && targetLang == "HINDI(भारतीय)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "ENGLISH" && targetLang == "ARABIC(العربيه)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "ENGLISH" && targetLang == "PORTUGUESE(Português)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "ENGLISH" && targetLang == "RUSSIAN(русский)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "ENGLISH" && targetLang == "JAPANESE(日本人)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "ENGLISH" && targetLang == "GERMAN(Deutsch)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "ENGLISH" && targetLang == "KOREAN(한국어)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "ENGLISH" && targetLang == "FRENCH(Le français)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "ENGLISH" && targetLang == "TURKISH(Türkçe)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "HINDI(भारतीय)" && targetLang == "ARABIC(العربيه)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "HINDI(भारतीय)" && targetLang == "PORTUGUESE(Português)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "HINDI(भारतीय)" && targetLang == "RUSSIAN(русский)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "HINDI(भारतीय)" && targetLang == "JAPANESE(日本人)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "HINDI(भारतीय)" && targetLang == "GERMAN(Deutsch)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "HINDI(भारतीय)" && targetLang == "KOREAN(한국어)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "HINDI(भारतीय)" && targetLang == "FRENCH(Le français)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "HINDI(भारतीय)" && targetLang == "TURKISH(Türkçe)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "ARABIC(العربيه)" && targetLang == "PORTUGUESE(Português)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "ARABIC(العربيه)" && targetLang == "RUSSIAN(русский)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "ARABIC(العربيه)" && targetLang == "JAPANESE(日本人)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "ARABIC(العربيه)" && targetLang == "GERMAN(Deutsch)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "ARABIC(العربيه)" && targetLang == "KOREAN(한국어)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "ARABIC(العربيه)" && targetLang == "FRENCH(Le français)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "ARABIC(العربيه)" && targetLang == "TURKISH(Türkçe)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "PORTUGUESE(Português)" && targetLang == "RUSSIAN(русский)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "PORTUGUESE(Português)" && targetLang == "JAPANESE(日本人)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "PORTUGUESE(Português)" && targetLang == "GERMAN(Deutsch)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "PORTUGUESE(Português)" && targetLang == "KOREAN(한국어)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "PORTUGUESE(Português)" && targetLang == "FRENCH(Le français)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "PORTUGUESE(Português)" && targetLang == "TURKISH(Türkçe)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "RUSSIAN(русский)" && targetLang == "JAPANESE(日本人)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "RUSSIAN(русский)" && targetLang == "GERMAN(Deutsch)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "RUSSIAN(русский)" && targetLang == "KOREAN(한국어)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "RUSSIAN(русский)" && targetLang == "FRENCH(Le français)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "RUSSIAN(русский)" && targetLang == "TURKISH(Türkçe)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "JAPANESE(日本人)" && targetLang == "GERMAN(Deutsch)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "JAPANESE(日本人)" && targetLang == "KOREAN(한국어)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "JAPANESE(日本人)" && targetLang == "FRENCH(Le français)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "JAPANESE(日本人)" && targetLang == "TURKISH(Türkçe)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "GERMAN(Deutsch)" && targetLang == "KOREAN(한국어)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "GERMAN(Deutsch)" && targetLang == "FRENCH(Le français)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "GERMAN(Deutsch)" && targetLang == "TURKISH(Türkçe)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "KOREAN(한국어)" && targetLang == "FRENCH(Le français)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "KOREAN(한국어)" && targetLang == "TURKISH(Türkçe)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        else if (nativeLang == "FRENCH(Le français)" && targetLang == "TURKISH(Türkçe)")
        {
            multiNativeLanguages = nativeLang + "_" + targetLang;
            multiTargetLanguages = targetLang + "_" + nativeLang;
        }
        *//*"CHINESE(中国人)",
                    "SPANISH(Espanol)",
                    "ENGLISH",
                    "HINDI(भारतीय)",
                    "ARABIC(العربيه)",
                    "PORTUGUESE(Português)",
                    "RUSSIAN(русский)",
                    "JAPANESE(日本人)",
                    "GERMAN(Deutsch)",
                    "KOREAN(한국어)",
                    "FRENCH(Le français)",
                    "TURKISH(Türkçe)"*//*
    }*/


    private void InitializeFields() {

        updateAccountSettings = (Button) findViewById(R.id.update_settings_button);
        userName = (EditText) findViewById(R.id.set_user_name);
        userStatus = (EditText) findViewById(R.id.set_profile_status);
        userProfileImage = (CircleImageView) findViewById(R.id.set_profile_image);

        //nativeText = (TextView) findViewById(R.id.native_text_languages_id);
        //spinnerShow = (Spinner)  findViewById(R.id.spinner_settings);

        loadingBar = new ProgressDialog(this);

        settingsToolBar =  (Toolbar) findViewById(R.id.setting_toolbar);
        //setSupportActionBar(settingsToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Account Settings");


       /* ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(this, R.array.native_language_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerShow.setAdapter(adapter);

        spinnerShow.setOnItemSelectedListener(this);*/
        //spinnerShow.setTex("fffff");


        //checkedLanguageTarget();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null) {

            Uri imageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                loadingBar.setTitle("Set profile image");
                loadingBar.setMessage("Please wait, your profile image updating...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();


                Uri resultUri = result.getUri();

                StorageReference filePath = userProfileImageRef.
                        child(currentUserID + ".jpg");

                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                Toast.makeText(SettingsActivity.this, "Profile image uploaded successfully...", Toast.LENGTH_SHORT).show();

                                final String downloadUri = uri.toString();

                                rootRef.child("Users").child(currentUserID)
                                        .child("image").setValue(downloadUri)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {
                                                    Toast.makeText(SettingsActivity.this, "Image save successfully..", Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                } else {

                                                    String message = task.getException().toString();

                                                    Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();

                                                    loadingBar.dismiss();
                                                }

                                            }
                                        });













                            }
                        });
                    }
                });





















            /*addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {

                            Toast.makeText(SettingsActivity.this, "Profile image uploaded successfully...", Toast.LENGTH_SHORT).show();

                            final String downloadUri = task.getResult().getStorage().getDownloadUrl().toString();

                            rootRef.child("Users").child(currentUserID)
                                    .child("image").setValue(downloadUri)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                Toast.makeText(SettingsActivity.this, "Image save successfully..", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            } else {

                                                String message = task.getException().toString();

                                                Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();

                                                loadingBar.dismiss();
                                            }

                                        }
                                    });

                        } else {

                            String message = task.getException().toString();

                            Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();

                            loadingBar.dismiss();
                        }

                    }
                });*/

            }


        }

    }

    private void RetrieveUserInfo() {
        rootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("name") &&
                dataSnapshot.hasChild("image")) {

                    String retrieveNativeLanguage = null;
                    String retrieveTargetLanguage = null;

                    /*String retrieveMultiNativeLanguage = null;
                    String retrieveMultiTargetLanguage = null;*/

                    String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                    String retrieveStatus = dataSnapshot.child("status").getValue().toString();
                    String retrieveProfileImage = dataSnapshot.child("image").getValue().toString();

                    if (dataSnapshot.child("nativeLanguages").getValue() != null)
                    retrieveNativeLanguage = dataSnapshot.child("nativeLanguages").getValue().toString();

                    if (dataSnapshot.child("targetLanguages").getValue() != null)
                        retrieveTargetLanguage = dataSnapshot.child("targetLanguages").getValue().toString();


                    /*ContentValues newsValues = new ContentValues();
                    newsValues.put(LanguagesChatContract.LanguagesChatEntry.COLUMN_NAME, retrieveUserName);
                    newsValues.put(LanguagesChatContract.LanguagesChatEntry.COLUMN_USER_STATUS, retrieveStatus);
                    newsValues.put(LanguagesChatContract.LanguagesChatEntry.COLUMN_IMAGE, retrieveProfileImage);
                    newsValues.put(LanguagesChatContract.LanguagesChatEntry.COLUMN_NATIVE_LANGUAGES, retrieveNativeLanguage);
                    newsValues.put(LanguagesChatContract.LanguagesChatEntry.COLUMN_TARGET_LANGUAGES, retrieveTargetLanguage);


                    Context context = null;
                    if (newsValues != null) {
                        *//* Get a handle on the ContentResolver to delete and insert data *//*
                        ContentResolver sunshineContentResolver = context.getContentResolver();

                        *//* Delete old weather data because we don't need to keep multiple days' data *//*
                        sunshineContentResolver.delete(
                                null,
                                null,
                                null);

                        *//* Insert our new weather data into Sunshine's ContentProvider *//*
                        sunshineContentResolver.bulkInsert(
                                null,
                                new ContentValues[]{newsValues});
                    }*/



                    /*if (dataSnapshot.child("multiNativeLanguages").getValue() != null)
                        retrieveMultiNativeLanguage = dataSnapshot.child("multiNativeLanguages").getValue().toString();

                    if (dataSnapshot.child("multiTargetLanguages").getValue() != null)
                        retrieveMultiTargetLanguage = dataSnapshot.child("multiTargetLanguages").getValue().toString();*/

                    //MultiLanguageFunc(retrieveNativeLanguage, retrieveTargetLanguage);

                    //String retrieveTargetLanguage = dataSnapshot.child("targetLanguages").getValue().toString();


                    userName.setText(retrieveUserName);
                    userStatus.setText(retrieveStatus);
                    native_txt.setText(retrieveNativeLanguage);
                    target_txt.setText(retrieveTargetLanguage);
                 //   nativeText.setText(retrieveNativeLanguage);
                    //spinnerShow.setPrompt(retrieveTargetLanguage);
                    Picasso.get().load(retrieveProfileImage).into(userProfileImage);

                } else if (dataSnapshot.exists() && dataSnapshot.hasChild("name")) {


                    String retrieveNativeLanguage = null;
                    String retrieveTargetLanguage = null;

                    /*String retrieveMultiNativeLanguage = null;
                    String retrieveMultiTargetLanguage = null;*/

                    String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                    String retrieveStatus = dataSnapshot.child("status").getValue().toString();

                    if (dataSnapshot.child("nativeLanguages").getValue() != null)
                        retrieveNativeLanguage = dataSnapshot.child("nativeLanguages").getValue().toString();

                    if (dataSnapshot.child("targetLanguages").getValue() != null)
                        retrieveTargetLanguage = dataSnapshot.child("targetLanguages").getValue().toString();


                    /*ContentValues newsValues = new ContentValues();
                    newsValues.put(LanguagesChatContract.LanguagesChatEntry.COLUMN_NAME, retrieveUserName);
                    newsValues.put(LanguagesChatContract.LanguagesChatEntry.COLUMN_USER_STATUS, retrieveStatus);
                    newsValues.put(LanguagesChatContract.LanguagesChatEntry.COLUMN_NATIVE_LANGUAGES, retrieveNativeLanguage);
                    newsValues.put(LanguagesChatContract.LanguagesChatEntry.COLUMN_TARGET_LANGUAGES, retrieveTargetLanguage);*/

                    /*if (dataSnapshot.child("multiNativeLanguages").getValue() != null)
                        retrieveMultiNativeLanguage = dataSnapshot.child("multiNativeLanguages").getValue().toString();

                    if (dataSnapshot.child("multiTargetLanguages").getValue() != null)
                        retrieveMultiTargetLanguage = dataSnapshot.child("multiTargetLanguages").getValue().toString();*/

                    //MultiLanguageFunc(retrieveNativeLanguage, retrieveTargetLanguage);

//                    String retrieveNativeLanguage = dataSnapshot.child("nativeLanguages").getValue().toString();
                    //String retrieveTargetLanguage = dataSnapshot.child("targetLanguages").getValue().toString();


                    userName.setText(retrieveUserName);
                    userStatus.setText(retrieveStatus);
                    native_txt.setText(retrieveNativeLanguage);
                    target_txt.setText(retrieveTargetLanguage);

                //    nativeText.setText(retrieveNativeLanguage);
                    //spinnerShow.setPrompt(retrieveTargetLanguage);

                } else {

                    userName.setVisibility(View.VISIBLE);
                    Toast.makeText(SettingsActivity.this, "Please set && update your profile information...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        /*nativeLanguages = parent.getItemAtPosition(position).toString();
        nativeLanguages_int = position;
        Log.d("myoo", "rrr" + position);*/
        //Toast.makeText(this, languagesText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
