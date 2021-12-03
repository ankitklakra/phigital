package com.phigital.ai.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.phigital.ai.Article.ArticleCommentsActivity;
import com.phigital.ai.GetTimeAgo;
import com.phigital.ai.Model.ModelComments;
import com.phigital.ai.Model.ModelUser;

import com.phigital.ai.R;
import com.phigital.ai.Activity.SearchActivity;
import com.phigital.ai.Utility.UserProfile;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pedromassango.doubleclick.DoubleClick;
import com.pedromassango.doubleclick.DoubleClickListener;
import com.squareup.picasso.Picasso;
import com.tylersuehr.socialtextview.SocialTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class AdapterArticleComments extends RecyclerView.Adapter<AdapterArticleComments.MyHolder> implements View.OnClickListener {

    Context context ;
    final List<ModelComments> commentsList;
    List<ModelComments> commentsList2;
    private DatabaseReference likeRef;
    private DatabaseReference postsRef1;
    private DatabaseReference postsRef2;
    ConstraintLayout delete;
    boolean mProcessLike = false;
    private AdapterArticleReplyComments replyComments;
    BottomSheetDialog DeleteBottomSheet;
    private String userId;

    MyHolder bolder;
    String pId,cId;

    public AdapterArticleComments(Context context, List<ModelComments> commentsList) {
        this.context = context;
        this.commentsList = commentsList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_comment, parent, false);
        return new MyHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String id = commentsList.get(position).getId();
        String cId = commentsList.get(position).getcId();
        String comment = commentsList.get(position).getComment();
        String time = commentsList.get(position).getTimestamp();
        String pId = commentsList.get(position).getpId();
        String pLikes = commentsList.get(position).getpLikes();
        String type = commentsList.get(position).getType();

        switch (type) {
            case "text":
                holder.mComment.setVisibility(View.VISIBLE);
                holder.rec_vid.setVisibility(View.GONE);
                holder.rec_img.setVisibility(View.GONE);
                holder.mComment.setText(comment);
                holder.play.setVisibility(View.GONE);
                break;
            case "image":
                holder.mComment.setVisibility(View.GONE);
                holder.rec_img.setVisibility(View.VISIBLE);
                holder.rec_vid.setVisibility(View.GONE);
                holder.play.setVisibility(View.GONE);
                Glide.with(context).asBitmap().centerCrop().load(comment).into(holder.rec_img);
                break;
            case "video":
                holder.mComment.setVisibility(View.GONE);
                holder.play.setVisibility(View.VISIBLE);
                holder.rec_img.setVisibility(View.GONE);
                holder.rec_vid.setVisibility(View.VISIBLE);
                Glide.with(context).asBitmap().centerCrop().load(comment).into(holder.rec_vid);
                break;
        }

        bolder = holder;

//        holder.rec_img.setOnClickListener(new DoubleClick(new DoubleClickListener() {
//            @Override
//            public void onSingleClick(View view) {
//                Intent intent = new Intent(context, MediaView.class);
//                intent.putExtra("type","image");
//                intent.putExtra("uri",comment);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);
//            }
//
//            @Override
//            public void onDoubleClick(View view) {
//                int pLikes = Integer.parseInt(commentsList.get(position).getpLikes());
//                mProcessLike = true;
//                String postId = commentsList.get(position).getcId();
//                likeRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (mProcessLike){
//                            if (dataSnapshot.child(postId).hasChild(userId)){
//                                postsRef1.child(postId).child("pLikes").setValue(""+(pLikes-1));
//                                likeRef.child(postId).child(userId).removeValue();
//                                mProcessLike = false;
//                            }else {
//                                postsRef1.child(postId).child("pLikes").setValue(""+(pLikes+1));
//                                likeRef.child(postId).child(userId).setValue("Liked");
//                                mProcessLike = false;
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//            }
//        }));
//
//        holder.rec_vid.setOnClickListener(new DoubleClick(new DoubleClickListener() {
//            @Override
//            public void onSingleClick(View view) {
//                Intent intent = new Intent(context, MediaView.class);
//                intent.putExtra("type","video");
//                intent.putExtra("uri",comment);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);
//            }
//
//            @Override
//            public void onDoubleClick(View view) {
//                int pLikes = Integer.parseInt(commentsList.get(position).getpLikes());
//                mProcessLike = true;
//                String postId = commentsList.get(position).getcId();
//                likeRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (mProcessLike){
//                            if (dataSnapshot.child(postId).hasChild(userId)){
//                                postsRef1.child(postId).child("pLikes").setValue(""+(pLikes-1));
//                                likeRef.child(postId).child(userId).removeValue();
//                                mProcessLike = false;
//                            }else {
//                                postsRef1.child(postId).child("pLikes").setValue(""+(pLikes+1));
//                                likeRef.child(postId).child(userId).setValue("Liked");
//                                mProcessLike = false;
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//            }
//        }));

        likeRef = FirebaseDatabase.getInstance().getReference().child("cLikes");
        postsRef1 = FirebaseDatabase.getInstance().getReference().child("Article").child(pId).child("Comments");
        postsRef2 = FirebaseDatabase.getInstance().getReference().child("Article").child(pId).child("Comments").child(cId).child("replies");
        holder.mName.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserProfile.class);
            intent.putExtra("hisUid", id);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
        holder.mDp.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserProfile.class);
            intent.putExtra("hisUid", id);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        if (pLikes.equals("0")){
            holder.liketv.setVisibility(View.GONE);
        }else {
            holder.liketv.setVisibility(View.VISIBLE);
            holder.liketv.setText(pLikes);
        }

        holder.mComment.setLinkText(comment);
        holder.mComment.setOnLinkClickListener((i, s) -> {
            if (i == 1){
                Intent intent = new Intent(context, SearchActivity.class);
                intent.putExtra("hashTag", s);
                context.startActivity(intent);
            }else
            if (i == 2){
                String username = s.replaceFirst("@","");
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                Query query = ref.orderByChild("username").equalTo(username.trim());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot ds : snapshot.getChildren()){
                                String id1 = ds.child("id").getValue().toString();
                                if (id1.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    Intent intent = new Intent(context, UserProfile.class);
                                    intent.putExtra("hisUid", id1);
                                    context.startActivity(intent);
                                }else {
                                    Intent intent = new Intent(context, UserProfile.class);
                                    intent.putExtra("hisUid", id1);
                                    context.startActivity(intent);
                                }
                            }
                        }else {

                            Toast.makeText(context, "Invalid username, can't find user with this username", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            else if (i == 16){
                String url = s;
                if (!url.startsWith("https://") && !url.startsWith("http://")){
                    url = "http://" + url;
                }
                Intent openUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(openUrlIntent);
            }else if (i == 4){
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", s, null));
                context.startActivity(intent);
            }else if (i == 8){
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, s);
                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                context.startActivity(intent);

            }
        });

        setLikes(holder, cId);

        GetTimeAgo getTimeAgo = new GetTimeAgo();
        long lastTime = Long.parseLong(time);
        String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime);

        holder.time.setText(lastSeenTime);
        holder.mComment.setText(comment);

        getUserInfo(holder, id);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        holder.mComment.setOnClickListener(new DoubleClick(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {

            }

            @Override
            public void onDoubleClick(View view) {
                int pLikes = Integer.parseInt(commentsList.get(position).getpLikes());
                mProcessLike = true;
                String postId = commentsList.get(position).getcId();
                likeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (mProcessLike){
                            if (dataSnapshot.child(postId).hasChild(userId)){
                                postsRef1.child(postId).child("pLikes").setValue(""+(pLikes-1));
                                likeRef.child(postId).child(userId).removeValue();
                                mProcessLike = false;
                            }else {
                                postsRef1.child(postId).child("pLikes").setValue(""+(pLikes+1));
                                likeRef.child(postId).child(userId).setValue("Liked");
                                mProcessLike = false;
                                addToHisNotification(""+id,""+postId,"Liked your comment");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }));

//        holder.image.setOnClickListener(v -> {
//            int pLikes1 = Integer.parseInt(commentsList.get(position).getpLikes());
//            mProcessLike = true;
//            String postId = commentsList.get(position).getcId();
//            likeRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (mProcessLike){
//                        if (dataSnapshot.child(postId).hasChild(userId)){
//                            postsRef1.child(postId).child("pLikes").setValue(""+(pLikes1 -1));
//                            likeRef.child(postId).child(userId).removeValue();
//                        }else {
//                            postsRef1.child(postId).child("pLikes").setValue(""+(pLikes1 +1));
//                            likeRef.child(postId).child(userId).setValue("Liked");
//                        }
//                        mProcessLike = false;
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        });

        holder.showreply.setVisibility(View.VISIBLE);
        holder.reply.setOnClickListener(v -> {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ModelUser user = snapshot.getValue(ModelUser.class);
                    String username = (Objects.requireNonNull(user).getUsername());
                    Intent intent = new Intent("comment");
                    intent.putExtra("fromComment", username);
                    intent.putExtra("postId", pId);
                    intent.putExtra("fromCid", cId);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        holder.com.setOnClickListener(new DoubleClick(new DoubleClickListener() {
                    @Override
                    public void onSingleClick(View view) {
//                       view = ((ArticleComments)context).getLayoutInflater().inflate(R.layout.delete_bottom_sheet,null);
//                      BottomSheetDialog deleteBottomSheet =new BottomSheetDialog(context);
//                      deleteBottomSheet.setContentView(view);
//                      deleteBottomSheet.show();
                    }

                    @Override
                    public void onDoubleClick(View view) {
                        mProcessLike = true;
                        String postId = commentsList.get(position).getcId();
                        likeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (mProcessLike) {
                                    if (dataSnapshot.child(postId).hasChild(userId)) {
                                        likeRef.child(postId).child(userId).removeValue();
                                        mProcessLike = false;
                                    } else {
                                        likeRef.child(postId).child(userId).setValue("Liked");
                                        mProcessLike = false;
                                        addToHisNotification(""+id,""+postId,"Liked your comment");
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }));

        holder.showreply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadreplies(holder,position,cId,pId);
                holder.replyview.setVisibility(View.VISIBLE);
                holder.showreply.setVisibility(View.GONE);
                holder.hidereply.setVisibility(View.VISIBLE);
            }
        });

        holder.hidereply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.replyview.setVisibility(View.GONE);
                holder.showreply.setVisibility(View.VISIBLE);
                holder.hidereply.setVisibility(View.GONE);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (id.equals(userId)){
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                builder.setTitle("Delete");
                builder.setMessage("Are you sure to delete this comment?");
                builder.setPositiveButton("Delete", (dialog, which) -> {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Article").child(pId);
                    ref.child("Comments").child(cId).removeValue()
                            .addOnSuccessListener(aVoid -> {

                            }).addOnFailureListener(e -> {
                    });
                }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                builder.create().show();

            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                builder.setTitle("Report");
                builder.setMessage("Are you sure to report this comment?");
                builder.setPositiveButton("Report", (dialog, which) -> {
//                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(pId);
//                ref.child("Comments").child(cId).removeValue()
//                        .addOnSuccessListener(aVoid -> {
//
//                        }).addOnFailureListener(e -> {
//                });
                    Toast.makeText(context, "Comment Reported", Toast.LENGTH_SHORT).show();
                }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                builder.create().show();
            }
            return false;

        });

//        holder.com.setOnLongClickListener(v -> {
//            deletecontent();
//            return false;
//        });

        holder.image.setOnClickListener(v -> {
            mProcessLike = true;
            String postId = commentsList.get(position).getcId();
            likeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (mProcessLike) {
                        if (dataSnapshot.child(postId).hasChild(userId)) {
                            likeRef.child(postId).child(userId).removeValue();
                            mProcessLike = false;
                        } else {
                            likeRef.child(postId).child(userId).setValue("Liked");
                            mProcessLike = false;
                            addToHisNotification(""+id,""+postId,"Liked your comment");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });
//        if (id.equals(userId)){
//            holder.delete.setVisibility(View.VISIBLE);
//        }else {
//            holder.delete.setVisibility(View.GONE);
//        }



//        holder.delete.setOnClickListener(v -> {
//
//        });
        loadPostInfo();
        noLike(position,holder);
    }

    private void loadreplies(MyHolder holder, int position, String cId, String pId) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        holder.replyview.setLayoutManager(layoutManager);
        commentsList2 = new ArrayList<>();
        DatabaseReference ref =FirebaseDatabase.getInstance().getReference().child("Article").child(pId).child("Comments").child(cId);
        ref.child("replies").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentsList2.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                    ModelComments modelComments = ds.getValue(ModelComments.class);
                    commentsList2.add(modelComments);
                }
                replyComments = new AdapterArticleReplyComments(context, commentsList2);
                holder.replyview.setAdapter(replyComments);
                replyComments.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addToHisNotification(String hisId, String pId, String notification){
        String image = ArticleCommentsActivity.getmeme();
        String timestamp = ""+ System.currentTimeMillis();
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId", pId);
        hashMap.put("timestamp", timestamp);
        hashMap.put("pUid", hisId);
        hashMap.put("notification", notification);
        hashMap.put("sUid", userId);
        hashMap.put("sImage", image);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hisId).child("Notifications").child(timestamp).setValue(hashMap)
                .addOnSuccessListener(aVoid -> {

                }).addOnFailureListener(e -> {

        });

    }

    private void noLike(int position, MyHolder holder) {
        String postId = commentsList.get(position).getcId();
        likeRef.child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String numOfLikes = String.valueOf((int) snapshot.getChildrenCount());
                if (numOfLikes.equals("0")) {
                    holder.liketv.setVisibility(View.GONE);
                } else {
                    holder.liketv.setVisibility(View.VISIBLE);
                    holder.liketv.setText(snapshot.getChildrenCount()+"");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadPostInfo() {
        if (DeleteBottomSheet == null) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.delete_bottom_sheet, null);
            delete = view.findViewById(R.id.delete);
            delete.setOnClickListener(this);
            DeleteBottomSheet = new BottomSheetDialog(context.getApplicationContext());
            DeleteBottomSheet.setContentView(view);
        }
    }

    private void getUserInfo(MyHolder holder, String id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser user = snapshot.getValue(ModelUser.class);
                holder.mName.setText(Objects.requireNonNull(user).getName());
                try {
                    Picasso.get().load(user.getPhoto()).placeholder(R.drawable.placeholder).into(holder.mDp);
                }catch (Exception ignored){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setLikes(MyHolder holder, String postKey) {
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postKey).hasChild(userId)){
                    holder.image.setImageResource(R.drawable.icon_fav2);
                }else {
                    holder.image.setImageResource(R.drawable.icon_fav);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete:
                DeleteBottomSheet.cancel();
                deletecontent();
                break;
        }
    }

    private void deletecontent() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Article").child(pId);
        ref.child("Comments").child(cId).removeValue();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String comments = ""+ snapshot.child("pComments").getValue();
                int newCommentVal = Integer.parseInt(comments) -1;
                ref.child("pComments").setValue(""+newCommentVal);
                bolder.com.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                bolder.com.setVisibility(View.GONE);
            }
        });
    }

    class MyHolder extends RecyclerView.ViewHolder{
        final CircleImageView mDp;
        final TextView mName;
        final SocialTextView mComment;
        final TextView time;
        final TextView reply;
        final TextView liketv;
        final ImageView showreply;
        final ImageView hidereply;
        final ImageView rec_img;
        final ImageView rec_vid;
        final ImageView play;
        final ImageView image;
        final ImageView delete;
        final ConstraintLayout com;
        final RecyclerView replyview;


        public MyHolder(@NonNull View itemView) {
            super(itemView);
            mDp = itemView.findViewById(R.id.dp);
            mName = itemView.findViewById(R.id.name);
            liketv = itemView.findViewById(R.id.liketv);
            reply = itemView.findViewById(R.id.reply);

            rec_img = itemView.findViewById(R.id.rec_img);
            rec_vid = itemView.findViewById(R.id.rec_vid);
            play = itemView.findViewById(R.id.play);

            mComment = itemView.findViewById(R.id.comment);
            delete = itemView.findViewById(R.id.delete);
            Typeface typeface = ResourcesCompat.getFont(context, R.font.roboto_medium);
            Typeface typeface2 = ResourcesCompat.getFont(context, R.font.roboto_regular);
            mName.setTypeface(typeface);
            time = itemView.findViewById(R.id.time);
            image = itemView.findViewById(R.id.image);
            mComment.setTypeface(typeface2);
            reply.setTypeface(typeface);
            liketv.setTypeface(typeface);
            time.setTypeface(typeface);
            com = itemView.findViewById(R.id.messageLayout);
            replyview = itemView.findViewById(R.id.replyview);
            showreply = itemView.findViewById(R.id.showreply);
            hidereply = itemView.findViewById(R.id.hidereply);
        }
    }

}
