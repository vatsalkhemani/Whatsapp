package com.example.whatsapp.Chat;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>{


    ArrayList<ChatObject> chatList;


    public ChatListAdapter(ArrayList<ChatObject> chatList){
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        ChatListViewHolder rcv = new ChatListViewHolder(layoutView);
        return rcv;
    }




    @Override
    public void onBindViewHolder(@NonNull final ChatListViewHolder holder, final int position) {


        final DatabaseReference mUserDB=FirebaseDatabase.getInstance().getReference().child("chat").child(chatList.get(position).getChatId()).child("info").child("users");
        mUserDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {
                    String s=snapshot.getValue().toString();
                   String[] users=s.split(",");
                   String[] user1ID=users[0].split("=");
                    String[] user2ID=users[1].split("=");
                    String user1=user1ID[0];
                    String user2=user2ID[0];
                    String user2value=user2;
                    user2value=user2value.substring(1);
                    String user1value = user1;
                    user1value = user1value.substring(1);

                    DatabaseReference userName=FirebaseDatabase.getInstance().getReference().child("user").child(user1value).child("name");
                    final String finalUser2value = user2value;
                    userName.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists())
                            holder.mTitle.setText(snapshot.getValue().toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    DatabaseReference userName2=FirebaseDatabase.getInstance().getReference().child("user").child(user2value).child("name");
                    userName2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                holder.mTitle.append("\n");
                                holder.mTitle.append(snapshot.getValue().toString());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ChatActivity.class);
                intent.putExtra("chatObject", chatList.get(holder.getAdapterPosition()));
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }





    public class ChatListViewHolder extends RecyclerView.ViewHolder{
        public TextView mTitle;
        public LinearLayout mLayout;
        public ChatListViewHolder(View view){
            super(view);
            mTitle = view.findViewById(R.id.title);
            mLayout = view.findViewById(R.id.layout);
        }
    }
}