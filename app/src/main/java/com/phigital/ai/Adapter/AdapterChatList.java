package com.phigital.ai.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.GetTimeAgo;
import com.phigital.ai.Model.ModelChat;
import com.phigital.ai.Model.ModelChatListGroups;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.R;
import com.phigital.ai.Chat.ChatActivity;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterChatList extends RecyclerView.Adapter<AdapterChatList.MyHolder> {

    final Context context;
    final List<ModelUser> modelChatLists;
    private final HashMap<String, String> lastMessageMap;
    public AdapterChatList(Context context, List<ModelUser> modelChatLists) {
        this.context = context;
        this.modelChatLists = modelChatLists;
        lastMessageMap = new HashMap<>();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(context).inflate(R.layout.chatlist, parent, false);
        return new MyHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String hisUid = modelChatLists.get(position).getId();
        String dp = modelChatLists.get(position).getPhoto();
        String name = modelChatLists.get(position).getName();
        String status = modelChatLists.get(position).getStatus();
        String lastMessage = lastMessageMap.get(hisUid);

        holder.mName.setText(name);

//        if (lastMessage == null || lastMessage.equals("default")){
//            holder.message.setVisibility(View.GONE);
//        }else {
//            holder.message.setVisibility(View.VISIBLE);
//            holder.message.setText(lastMessage);
//        }

        try{
            Picasso.get().load(dp).resize(150, 150).centerCrop().into(holder.mDp);
        }catch(Exception e){
            Picasso.get().load(R.drawable.placeholder).into(holder.mDp);
        }

        if (status.equals("online")) {
            holder.time.setText("online");
        }else{
            long lastTime = Long.parseLong(status);
            String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime);
            holder.time.setText(lastSeenTime);
        }

//        //UserInfo
//        FirebaseDatabase.getInstance().getReference().child("Users").child(modelChatLists.get(position).getId()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                //Time
//                if (Objects.requireNonNull(snapshot.child("status").getValue()).toString().equals("online")) {
//                    holder.time.setText("online");
//                }else{
//                    long lastTime = Long.parseLong(Objects.requireNonNull(snapshot.child("status").getValue()).toString());
//                    String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime);
//                    holder.time.setText(lastSeenTime);
//                }
//
//                //Name
//                holder.mName.setText(Objects.requireNonNull(snapshot.child("name").getValue()).toString());
//
//                //DP
//                if (!Objects.requireNonNull(snapshot.child("photo").getValue()).toString().isEmpty()) {
//                    try{
//                        Picasso.get()
//                                .load(Objects.requireNonNull(snapshot.child("photo").getValue()).toString())
//                                .resize(150, 150)
//                                .centerCrop()
//                                .into(holder.mDp);
//                    }catch(Exception e){
//                        Picasso.get().load(R.drawable.placeholder).into(holder.mDp);
//                    }
//
//                }
////                    Picasso.get().load(Objects.requireNonNull(snapshot.child("photo").getValue()).toString()).into(holder.mDp);
//
////                //Verify
////                if (Objects.requireNonNull(snapshot.child("verified").getValue()).toString().equals("yes")) holder.verified.setVisibility(View.VISIBLE);
//
//                //Typing
//                if (Objects.requireNonNull(snapshot.child("typingTo").getValue()).toString().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
//                    holder.message.setText("Typing...");
//                }else {
//                    //LastMessage
////                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
////                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
////                        @SuppressLint("SetTextI18n")
////                        @Override
////                        public void onDataChange(@NonNull DataSnapshot snapshot) {
////                            holder.message.setText("");
////                            for (DataSnapshot ds: snapshot.getChildren()){
////                                ModelChat chat = ds.getValue(ModelChat.class);
////                                if (chat == null){
////                                    continue;
////                                }
////                                String sender = chat.getSender();
////                                String receiver = chat.getReceiver();
////                                String hisId = Objects.requireNonNull(modelChatLists.get(position).getId());
////                                String myUid =Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
////                                if(sender == null || receiver == null){
////                                    continue;
////                                }
////                                if (chat.getReceiver().equals(myUid) && chat.getSender().equals(hisId) || chat.getReceiver().equals(hisId) && chat.getSender().equals(myUid)){
////                                    switch (chat.getType()) {
////                                        case "image":
////                                            holder.message.setText("Sent a photo");
////                                            break;
////                                        case "video":
////                                            holder.message.setText("Sent a video");
////                                            break;
////                                        case "post":
////                                            holder.message.setText("Sent a post");
////                                            break;
////                                        case "text":
////                                            holder.message.setText(chat.getMsg());
////                                            break;
//////                                        case "gif":
//////                                            holder.message.setText("Sent a GIF");
//////                                            break;
//////                                        case "audio":
//////                                            holder.message.setText("Sent a audio");
//////                                        case "doc":
//////                                            holder.message.setText("Sent a document");
//////                                            break;
//////                                        case "location":
//////                                            holder.message.setText("Sent a location");
//////                                            break;
//////                                        case "party":
//////                                            holder.message.setText("Sent a party invitation");
//////                                            break;
//////                                        case "reel":
//////                                            holder.message.setText("Sent a reel");
//////                                            break;
//////                                        case "story":
//////                                        case "high":
//////                                            holder.message.setText("Sent a story");
//////                                            break;
////                                        default:
////                                            holder.message.setText(chat.getMsg());
////                                            break;
////                                    }
////                                }
////                            }
////                        }
////
////                        @Override
////                        public void onCancelled(@NonNull DatabaseError error) {
////
////                        }
////                    });
//                    if (lastMessage == null || lastMessage.equals("default")){
//                        holder.message.setVisibility(View.GONE);
//                    }else {
//                        holder.message.setVisibility(View.VISIBLE);
//                        holder.message.setText(lastMessage);
//                    }
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        //Click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("hisUid", hisUid);
            context.startActivity(intent);
        });
//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
//                builder.setTitle("Delete");
//                builder.setMessage("Are you sure to delete this comment?");
//                builder.setPositiveButton("Delete", (dialog, which) -> {
//                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(pId);
//                    ref.child("Comments").child(cId).removeValue()
//                            .addOnSuccessListener(aVoid -> {
//                                Toast.makeText(context, "Comment Deleted", Toast.LENGTH_SHORT).show();
//                            }).addOnFailureListener(e -> {
//                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    });
//                }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
//                builder.create().show();
//                FirebaseDatabase.getInstance().getReference().child("Chatlist").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                        for (DataSnapshot ds : snapshot.getChildren()){
//
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//                    }
//                });
//                return false;
//            }
//        });

//        FirebaseDatabase.getInstance().getReference("Chats").addValueEventListener(new ValueEventListener() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                int i = 0;
//                for (DataSnapshot ds : snapshot.getChildren()) {
//                    String hisId = Objects.requireNonNull(hisUid);
//                    String myUid =Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
//                    if (Objects.requireNonNull(ds.child("sender").getValue()).toString().equals(hisId) && Objects.requireNonNull(ds.child("receiver").getValue()).toString().equals(myUid)) {
//                        ModelChat post = ds.getValue(ModelChat.class);
//                        assert post != null;
//                        if (!post.isIsSeen()) {
//                            i++;
//                        }
//                    }
//
//                }
//                if (i != 0){
//                    holder.count.setVisibility(View.VISIBLE);
//                    holder.count.setText(""+i);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        FirebaseDatabase.getInstance().getReference().child("Ban").child(hisUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    holder.itemView.setVisibility(View.GONE);
                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                    params.height = 0;
                    params.width = 0;
                    holder.itemView.setLayoutParams(params);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
//    public void setLastMessageMap(String userId, String lastMessage){
//        lastMessageMap.put(userId, lastMessage);
//    }
    @Override
    public int getItemCount() {
        return modelChatLists.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        final CircleImageView mDp;
        final TextView time;
        final TextView mName;
        final TextView message;
        final TextView count;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            mDp = itemView.findViewById(R.id.circleImageView);
            time = itemView.findViewById(R.id.time);
            mName = itemView.findViewById(R.id.name);
            message = itemView.findViewById(R.id.username);
            count = itemView.findViewById(R.id.count);
        }
    }

}
