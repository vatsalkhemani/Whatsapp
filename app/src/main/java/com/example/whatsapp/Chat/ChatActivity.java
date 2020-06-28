package com.example.whatsapp.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.whatsapp.R;
import com.example.whatsapp.User.UserObject;
import com.example.whatsapp.Utils.SendNotification;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {


    private RecyclerView mChat,mMedia;
    private RecyclerView.Adapter mChatAdapter,mMediaAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager,mMediaLayoutManager;

    ArrayList<MessageObject> messageList;
    ChatObject mChatOject;
    DatabaseReference mChatMessagesDb;
    String currentUserID;
    FirebaseAuth mAuth;

    DatabaseReference UsersRef;
    String currentUserName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mChatOject=(ChatObject) getIntent().getSerializableExtra("chatObject");

        mChatMessagesDb= FirebaseDatabase.getInstance().getReference().child("chat").child(mChatOject.getChatId()).child("messages");
        UsersRef=FirebaseDatabase.getInstance().getReference().child("user");

        ImageButton mSend=findViewById(R.id.send);
        ImageButton mAddMedia=findViewById(R.id.addMedia);
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        mAddMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();

            }
        });
        initializeMessage();
        initializeMedia();
        getChatMessages();
    }




    private void GetUserInfo()
    {

    }


    private void getChatMessages() {
        mChatMessagesDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                   String text="";
                   String name="";
                    final String[] creatorID = {""};
                    final ArrayList<String> mediaUriList=new ArrayList<>();
                    if(snapshot.child("text").getValue() != null)
                        text=snapshot.child("text").getValue().toString();
                    if(snapshot.child("creator").getValue() != null) {


                        final String finalText = text;
                        UsersRef.child(snapshot.child("creator").getValue().toString()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                if (dataSnapshot.exists())
                                {
                                   creatorID[0] = dataSnapshot.child("name").getValue().toString();
                                    MessageObject mMessage=new MessageObject(snapshot.getKey(), creatorID[0], finalText,mediaUriList);
                                    messageList.add(mMessage);
                                    mChatLayoutManager.scrollToPosition(messageList.size()-1);
                                    mChatAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                    if(snapshot.child("media").getChildrenCount()>0)
                        for(DataSnapshot mediaSnapshot: snapshot.child("media").getChildren())
                            mediaUriList.add(mediaSnapshot.getValue().toString());

                    Log.i("creator as mMessage= ","this" + creatorID[0]);

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    int totalMediaUploaded=0;
    ArrayList<String> medialList=new ArrayList<>();
    EditText mMessage;

    private void sendMessage() {
        mMessage=findViewById(R.id.mes);

            String messageId =mChatMessagesDb.push().getKey();
            final DatabaseReference newMessageDb= mChatMessagesDb.child(messageId);
            final Map newMessageMap=new HashMap<>();
            newMessageMap.put("creator",FirebaseAuth.getInstance().getUid());

            if(!mMessage.getText().toString().isEmpty())
                newMessageMap.put("text",mMessage.getText().toString());


            if(!mediaUriList.isEmpty()){
                for(String medialUri: mediaUriList){
                    String mediaId = newMessageDb.child("media").push().getKey();
                    medialList.add(mediaId);
                    final StorageReference filePath=FirebaseStorage.getInstance().getReference().child("chat").child(mChatOject.getChatId()).child(messageId).child(mediaId);
                    UploadTask uploadTask=filePath.putFile(Uri.parse(medialUri));
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newMessageMap.put("/media/" + medialList.get(totalMediaUploaded) + "/",uri.toString());
                                    totalMediaUploaded++;
                                    if(totalMediaUploaded==mediaUriList.size()){
                                        updateDatabaseWithNewMessage(newMessageDb,newMessageMap);
                                    }
                                }
                            });
                        }
                    });

                }
            }else{
                if(!mMessage.getText().toString().isEmpty())
                    updateDatabaseWithNewMessage(newMessageDb,newMessageMap);
            }



    }

    private  void updateDatabaseWithNewMessage(DatabaseReference newMessageDb,Map newMessageMap){
        newMessageDb.updateChildren(newMessageMap);
        mMessage.setText(null);
        mediaUriList.clear();
        medialList.clear();
        mMediaAdapter.notifyDataSetChanged();

        String message;

        if(newMessageMap.get("text") != null)
            message = newMessageMap.get("text").toString();
        else
            message = "Sent Media";

        for(UserObject mUser : mChatOject.getUserObjectArrayList()){
            if(!mUser.getUid().equals(FirebaseAuth.getInstance().getUid())){
                new SendNotification(message, "New Message", mUser.getNotificationKey());
            }
        }
    }

    @SuppressLint({"WrongViewCast", "WrongConstant"})
    private void initializeMessage(){

        messageList = new ArrayList<MessageObject>();
        mChat= findViewById(R.id.messageList);
        mChat.setNestedScrollingEnabled(false);
        mChat.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
        mChat.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new MessageAdapter(messageList);
        mChat.setAdapter(mChatAdapter);
    }


    int PICK_IMAGE_INTENT=1;
    ArrayList<String> mediaUriList=new ArrayList<>();

    @SuppressLint("WrongConstant")
    private void initializeMedia(){

        mediaUriList = new ArrayList<>();
        mMedia= findViewById(R.id.mediaList);
        mMedia.setNestedScrollingEnabled(false);
        mMedia.setHasFixedSize(false);
        mMediaLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.HORIZONTAL, false);
        mMedia.setLayoutManager(mMediaLayoutManager);
        mMediaAdapter = new MediaAdapter(getApplicationContext(),mediaUriList);
        mMedia.setAdapter(mMediaAdapter);
    }


    private void openGallery() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture(s)"),PICK_IMAGE_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== RESULT_OK){
            if(requestCode==PICK_IMAGE_INTENT) {
                if (data.getClipData() == null) {
                    mediaUriList.add(data.getData().toString());
                }else{
                    for(int i=0;i<data.getClipData().getItemCount();i++){
                        mediaUriList.add(data.getClipData().getItemAt(i).getUri().toString());
                    }
                }

                mMediaAdapter.notifyDataSetChanged();

            }
        }
    }
}