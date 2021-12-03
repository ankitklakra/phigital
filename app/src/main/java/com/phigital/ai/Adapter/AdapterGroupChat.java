package com.phigital.ai.Adapter;

import  android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.phigital.ai.Groups.GroupChat;
import com.phigital.ai.Activity.ShareGroupActivity;
import com.phigital.ai.Model.ModelGroupChat;
import com.phigital.ai.Post.PostDetails;
import com.phigital.ai.R;
import com.phigital.ai.Utility.MediaView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterGroupChat extends RecyclerView.Adapter<AdapterGroupChat.MyHolder> {
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    private final Context context;
    private final ArrayList<ModelGroupChat> modelGroupChats;
    FirebaseUser firebaseUser;
    BottomSheetDialog reel_options;
    public AdapterGroupChat(Context context, ArrayList<ModelGroupChat> modelGroupChats) {
        this.context = context;
        this.modelGroupChats = modelGroupChats;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.row_group_right ,parent,false);
            return new MyHolder(view);
        }
            View view = LayoutInflater.from(context).inflate(R.layout.row_group_left ,parent,false);
            return new MyHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        ModelGroupChat model = modelGroupChats.get(position);
        String msg = model.getMsg();
        String type = model.getType();
        String sender = model.getSender();
        String timestamp = model.getTimestamp();

        switch (type) {
            case "text":
                holder.message.setVisibility(View.VISIBLE);
                holder.rec_vid.setVisibility(View.GONE);
                holder.rec_img.setVisibility(View.GONE);
                holder.message.setText(msg);
                holder.postly.setVisibility(View.GONE);
                holder.play.setVisibility(View.GONE);
                break;
            case "image":
                holder.message.setVisibility(View.GONE);
                holder.rec_img.setVisibility(View.VISIBLE);
                holder.postly.setVisibility(View.GONE);
                holder.rec_vid.setVisibility(View.GONE);
                holder.play.setVisibility(View.GONE);
                Glide.with(context).asBitmap().centerCrop().load(msg).into(holder.rec_img);
                break;
            case "video":
                holder.message.setVisibility(View.GONE);
                holder.play.setVisibility(View.VISIBLE);
                holder.rec_img.setVisibility(View.GONE);
                holder.postly.setVisibility(View.GONE);
                holder.rec_vid.setVisibility(View.VISIBLE);
                Glide.with(context).asBitmap().centerCrop().load(msg).into(holder.rec_vid);
                break;
            case "post":
                holder.message.setVisibility(View.GONE);
                holder.play.setVisibility(View.GONE);
                holder.rec_img.setVisibility(View.GONE);
                holder.rec_vid.setVisibility(View.GONE);
                holder.postly.setVisibility(View.VISIBLE);
                Glide.with(context).asBitmap().centerCrop().load(msg).into(holder.rec_vid);
                break;
        }
        holder.postly.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostDetails.class);
            intent.putExtra("mean", "post");
            intent.putExtra("postId", msg);
            context.startActivity(intent);
        });

        if (type.equals("post")){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
            Query query = ref.orderByChild("pId").equalTo(msg);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        String hisText = ""+ds.child("text").getValue();

                        String hisId = ""+ds.child("id").getValue();

                        String hisType = ""+ds.child("type").getValue();

                        String hisImage = ""+ds.child("image").getValue();

                        if (hisType.equals("text")){
                            holder.text.setMaxLines(4);
                            holder.text.setText(hisText);
                            holder.imageView13.setVisibility(View.GONE);
                        }
                        if(hisType.equals("image")){
                            holder.text.setVisibility(View.GONE);

                            try {
                                Picasso.get().load(hisImage).placeholder(R.drawable.placeholder).into(holder.imageView13);
                            }catch (Exception ignored){

                            }
                        }
                        if(hisType.equals("meme")){
                            holder.text.setText(hisText);

                            try {
                                Picasso.get().load(hisImage).placeholder(R.drawable.placeholder).into(holder.imageView13);
                            }catch (Exception ignored){

                            }
                        }

                        holder.pPlay.setVisibility(View.GONE);

                        if(!hisId.isEmpty()){
                            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Users");
                            Query query2 = ref2.orderByChild("id").equalTo(hisId);
                            query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                                        String myName = ""+ds.child("name").getValue();
                                        String myDp = ""+ds.child("photo").getValue();

                                        holder.cName.setText(myName);
                                        //DP
                                        try {
                                            Picasso.get().load(myDp).placeholder(R.drawable.placeholder).into(holder.circleImageView2);
                                        }catch (Exception ignored){

                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        setUserName(model , holder);

        holder.rec_img.setOnClickListener(v -> {
            Intent intent = new Intent(context, MediaView.class);
            intent.putExtra("type","image");
            intent.putExtra("uri",msg);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
        holder.rec_vid.setOnClickListener(v -> {
            Intent intent = new Intent(context, MediaView.class);
            intent.putExtra("type","video");
            intent.putExtra("uri",msg);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        holder.messageLayout.setOnLongClickListener(v -> {
            more_bottom(holder, position);
            reel_options.show();
            return false;

        });
    }

    private void setUserName(ModelGroupChat model, MyHolder holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("id").equalTo(model.getSender())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String name = ""+ds.child("name").getValue();
                            holder.name.setText(name);
                            holder.name2.setText(name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void more_bottom(MyHolder holder, int position) {
        if (reel_options == null){
            @SuppressLint("InflateParams") View view = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.activity_chat_bottom_sheet, null);

            ConstraintLayout save = view.findViewById(R.id.save);
            ImageView star = view.findViewById(R.id.star);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference().child("Favourite").child((Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser())).getUid());
                    likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(modelGroupChats.get(position).getTimestamp()).exists()) {
                                likeRef.child(modelGroupChats.get(position).getTimestamp()).removeValue();
                                star.setImageResource(R.drawable.starregular);
                            } else {
                                likeRef.child(modelGroupChats.get(position).getTimestamp()).setValue("star");
                                star.setImageResource(R.drawable.star);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    Snackbar.make(holder.itemView,"VaultActivity", Snackbar.LENGTH_LONG).show();
                }
            });
            save.setVisibility(View.GONE);

            ConstraintLayout forward = view.findViewById(R.id.forward);
            forward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ShareGroupActivity.class);
                    intent.putExtra("postId", modelGroupChats.get(position).getMsg());
                    intent.putExtra("type", modelGroupChats.get(position).getType());
                    intent.putExtra("content", "chat");
                    context.startActivity(intent);
                }
            });

            ConstraintLayout delete = view.findViewById(R.id.delete);
            delete.setOnClickListener(v -> {
                String type = modelGroupChats.get(position).getType();
                if (type.equals("text")){
                    Query query =  FirebaseDatabase.getInstance().getReference("Groups").child(GroupChat.getGroupId()).child("Message").orderByChild("timestamp").equalTo(modelGroupChats.get(position).getTimestamp());
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()){
                                ds.getRef().removeValue();
                                Snackbar.make(holder.itemView,"Deleted", Snackbar.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else if (type.equals("post")){
                    Query query = FirebaseDatabase.getInstance().getReference("Groups").child(GroupChat.getGroupId()).child("Message").orderByChild("timestamp").equalTo(modelGroupChats.get(position).getTimestamp());
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()){
                                ds.getRef().removeValue();
                                Snackbar.make(holder.itemView,"Deleted", Snackbar.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else{
                    FirebaseStorage.getInstance().getReferenceFromUrl(modelGroupChats.get(position).getMsg()).delete().addOnCompleteListener(task -> {
                        Query query = FirebaseDatabase.getInstance().getReference("Groups").child(GroupChat.getGroupId()).child("Message").orderByChild("timestamp").equalTo(modelGroupChats.get(position).getTimestamp());
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()){
                                    ds.getRef().removeValue();
                                    Snackbar.make(holder.itemView,"Deleted", Snackbar.LENGTH_LONG).show();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    });
                }
            });

            ConstraintLayout copy = view.findViewById(R.id.copy);
            copy.setOnClickListener(v -> {
                Snackbar.make(holder.itemView,"Copied", Snackbar.LENGTH_LONG).show();
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", modelGroupChats.get(position).getMsg());
                clipboard.setPrimaryClip(clip);
            });

            reel_options = new BottomSheetDialog(holder.itemView.getContext());
            reel_options.setContentView(view);
        }
    }

    @Override
    public int getItemCount() {
        return modelGroupChats.size();
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (modelGroupChats.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }

    static class  MyHolder extends RecyclerView.ViewHolder{

        private final TextView message;
        private final TextView name;
        private final TextView name2;
        public final ImageView rec_vid;
        public ImageView play;
        public final ImageView rec_img;

        public final ConstraintLayout postly;
        public final RelativeLayout messageLayout;
        public final CircleImageView circleImageView2;
        public final ImageView pPlay;
        public final ImageView imageView13;
        public final TextView text;
        public final TextView cName;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.rec_msg);
            name = itemView.findViewById(R.id.name);
            name2 = itemView.findViewById(R.id.name2);
            rec_vid = itemView.findViewById(R.id.rec_vid);
            rec_img = itemView.findViewById(R.id.rec_img);
            play = itemView.findViewById(R.id.play);

            circleImageView2 = itemView.findViewById(R.id.circleImageView2);
            play = itemView.findViewById(R.id.play);
            text = itemView.findViewById(R.id.text);
            postly = itemView.findViewById(R.id.postly);
            cName = itemView.findViewById(R.id.nameTv);
            pPlay = itemView.findViewById(R.id.pPlay);
            imageView13 = itemView.findViewById(R.id.imageView13);

            messageLayout = itemView.findViewById(R.id.messageLayout);

        }
    }
}
