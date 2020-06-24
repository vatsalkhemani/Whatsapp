package com.example.whatsapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder>{

    ArrayList<UserObject> userList;

    public UserListAdapter(ArrayList<UserObject> userList) {
        this.userList=userList;
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user,null,false);
        RecyclerView.LayoutParams lp =new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        UserListViewHolder rev=new UserListViewHolder(layoutView);
        return rev;
    }

    @Override
    public void onBindViewHolder(@NonNull UserListViewHolder holder, int position) {
        holder.mName.setText(userList.get(position).getName());
        holder.mPhone.setText(userList.get(position).getPhone());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    public class UserListViewHolder extends RecyclerView.ViewHolder{
        public TextView mName,mPhone;
        public UserListViewHolder(View view){
            super(view);
            mName=view.findViewById(R.id.name);
            mPhone=view.findViewById(R.id.phone);
        }
    }
}
