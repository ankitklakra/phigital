package com.phigital.ai.Adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.pedromassango.doubleclick.DoubleClick;
import com.pedromassango.doubleclick.DoubleClickListener;
import com.phigital.ai.GetTimeAgo;
import com.phigital.ai.Model.ModelPost;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.Notifications.Data;
import com.phigital.ai.Notifications.Sender;
import com.phigital.ai.Notifications.Token;
import com.phigital.ai.Post.PostComments;
import com.phigital.ai.R;
import com.phigital.ai.Activity.SearchActivity;
import com.phigital.ai.Utility.UserProfile;
import com.squareup.picasso.Picasso;
import com.tylersuehr.socialtextview.SocialTextView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

@SuppressWarnings("ALL")
public class AdapterAdminPost extends RecyclerView.Adapter<AdapterAdminPost.MyHolder> {

    Context context;
    final List<ModelPost> postList;
    String likedP="no";
    String reTweetedString="no";

    private RequestQueue requestQueue;
    private boolean notify = false;
    private String userId;

    private final DatabaseReference likeRef;
    private final DatabaseReference rejoyRef;

    boolean mProcessLike = false;
    boolean mProcessRejoy = false;

    public AdapterAdminPost(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
        likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        rejoyRef = FirebaseDatabase.getInstance().getReference().child("ReTweet");
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post, parent, false);
        context = parent.getContext();
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        String id = postList.get(position).getId();
        String postId = postList.get(position).getpId();
        String pText = postList.get(position).getText();
        String pType = postList.get(position).getType();
        String pView = postList.get(position).getpViews();
        String video = postList.get(position).getVideo();
        String image = postList.get(position).getImage();
        String privacy = postList.get(position).getPrivacy();
        String pTime = postList.get(position).getpTime();
        String link = postList.get(position).getLink();
        String content = postList.get(position).getContent();
        String rViews = postList.get(position).getrViews();
        String location = postList.get(position).getLocation();
        String reTweet = postList.get(position).getReTweet();
        String reId = postList.get(position).getReId();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        //Time
        long lastTime = Long.parseLong(pTime);
        String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime);
        holder.time.setText(lastSeenTime);

        if (postList.get(position).getReTweet().isEmpty()) {
            FirebaseDatabase.getInstance().getReference().child("Views").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        if (snapshot.child(postId).exists()){
//                            holder.view.setText(String.valueOf(snapshot.child(pId).getChildrenCount()));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //User details
            FirebaseDatabase.getInstance().getReference().child("Users").child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        if (snapshot.hasChild("id")){
                            String userId = snapshot.child("id").getValue().toString();
                            String name = Objects.requireNonNull(snapshot.child("name").getValue()).toString().trim();
                            String dp = Objects.requireNonNull(snapshot.child("photo").getValue()).toString().trim();
                            String username = Objects.requireNonNull(snapshot.child("username").getValue()).toString().trim();
                            String mVerified = snapshot.child("verified").getValue().toString();

                            if (mVerified.isEmpty()){
                                holder.verified.setVisibility(View.GONE);
                            }else{
                                holder.verified.setVisibility(View.VISIBLE);
                            }

                            holder.pName.setText(name);

                            if (!dp.isEmpty()) {
                                Picasso.get().load(dp).placeholder(R.drawable.placeholder).into(holder.pCircleImageView);
                            }

                            //ClickToPro
                            holder.pName.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                        Intent intent = new Intent(context, UserProfile.class);
                                        intent.putExtra("hisUid", userId);
                                        context.startActivity(intent);
                                    }else {
                                        Intent intent = new Intent(context, UserProfile.class);
                                        intent.putExtra("hisUid", userId);
                                        context.startActivity(intent);
                                    }
                                }
                            });

                            holder.pCircleImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                        Intent intent = new Intent(context, UserProfile.class);
                                        intent.putExtra("hisUid", userId);
                                        context.startActivity(intent);
                                    }else {
                                        Intent intent = new Intent(context, UserProfile.class);
                                        intent.putExtra("hisUid", userId);
                                        context.startActivity(intent);
                                    }
                                }
                            });
                        }else{
                            holder.pName.setText("noname");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            if (location.isEmpty()){

            }else{
                holder.location.setVisibility(View.VISIBLE);
                holder.location.setText(location);
                holder.dot3.setVisibility(View.VISIBLE);
            }

            switch (pType){
                case "text": {
                    holder.pText.setLinkText(pText);
                    holder.pText.setVisibility(View.VISIBLE);
                    holder.pText.setOnLinkClickListener((i, s) -> {
                        if (i == 1) {
                            Intent intent = new Intent(context, SearchActivity.class);
                            intent.putExtra("hashTag", s);
                            AdapterAdminPost.this.context.startActivity(intent);
                        }
                        else if (i == 2){
                            String username = s.replaceFirst("@","");
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                            Query query = ref.orderByChild("username").equalTo(username.trim());
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        String id1 = null;
                                        for (DataSnapshot ds : snapshot.getChildren()){
                                            id1 = ds.child("id").getValue().toString();
                                        }
                                        if (id1 != null){
                                            Intent intent = new Intent(context, UserProfile.class);
                                            intent.putExtra("hisUid", id1);
                                            context.startActivity(intent);
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
                        }
                        else if (i == 4){
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", s, null));
                            context.startActivity(intent);
                        }
                        else if (i == 8){
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.parse("mailto:"));
                            intent.putExtra(Intent.EXTRA_EMAIL, s);
                            intent.putExtra(Intent.EXTRA_SUBJECT, "");
                            context.startActivity(intent);
                        }
                    });
                    break;
                }
                case"image": {
                    if (!pText.isEmpty()){
                        holder.pText.setLinkText(pText);
                        holder.pText.setVisibility(View.VISIBLE);
                        holder.pText.setOnLinkClickListener((i, s) -> {
                            if (i == 1) {
                                Intent intent = new Intent(context, SearchActivity.class);
                                intent.putExtra("hashTag", s);
                                AdapterAdminPost.this.context.startActivity(intent);
                            }
                            else if (i == 2){
                                String username = s.replaceFirst("@","");
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                Query query = ref.orderByChild("username").equalTo(username.trim());
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            String id1 = null;
                                            for (DataSnapshot ds : snapshot.getChildren()){
                                                id1 = ds.child("id").getValue().toString();
                                            }
                                            if (id1 != null){
                                                Intent intent = new Intent(context, UserProfile.class);
                                                intent.putExtra("hisUid", id1);
                                                context.startActivity(intent);
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
                            }
                            else if (i == 4){
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", s, null));
                                context.startActivity(intent);
                            }
                            else if (i == 8){
                                Intent intent = new Intent(Intent.ACTION_SENDTO);
                                intent.setData(Uri.parse("mailto:"));
                                intent.putExtra(Intent.EXTRA_EMAIL, s);
                                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                                context.startActivity(intent);

                            }
                        });
                    }else{
                        holder.pText.setVisibility(View.GONE);
                    }
                    holder.pMeme.setVisibility(View.VISIBLE);
                    try {
                        Picasso.get().load(image).placeholder(R.drawable.placeholder).into(holder.pMeme);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }

            //Like
            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mProcessLike = true;
                    String postId = postList.get(position).getpId();
                    likeRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (mProcessLike)  {
                                if (dataSnapshot.child(postId).hasChild(userId)) {
                                    likeRef.child(postId).child(userId).removeValue();
                                    mProcessLike = false;
                                } else {
                                    likeRef.child(postId).child(userId).setValue(true);
                                    mProcessLike = false;
                                    ModelUser user = dataSnapshot.getValue(ModelUser.class);
//                                    if (pType.equals("text")){
//                                        sendNotification(id, user.getUsername(),  "Liked your Feel",postList.get(position).getpId());
//                                        addToHisNotification2(""+id,""+postId,"Liked your Feel");
//                                    }else{
//                                        sendNotification(id, user.getUsername(),  "Liked your Post",postList.get(position).getpId());
//                                        addToHisNotification(""+id,""+postId,"Liked your Post",image);
//                                    }

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            });

            holder.rejoy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mProcessRejoy = true;
                    String postId = postList.get(position).getpId();
                    rejoyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (mProcessRejoy){
                                if (snapshot.child(postId).hasChild(userId)) {
                                    holder.rejoy.setImageResource(R.drawable.icon_loop);
                                    Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("reId").equalTo(postList.get(position).getpId());
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                String userId = ds.child("id").getValue().toString();
                                                if (userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                                    ds.getRef().removeValue();
                                                    FirebaseDatabase.getInstance().getReference().child("ReTweet").child(postList.get(position).getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                                    Toast.makeText(context, "Rejoyed Removed", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    mProcessRejoy = false;
                                } else {
                                    holder.rejoy.setImageResource(R.drawable.icon_loop2);
                                    String timeStamp = String.valueOf(System.currentTimeMillis());
                                    HashMap<Object, String> hashMap = new HashMap<>();
                                    hashMap.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    hashMap.put("pId", timeStamp);
                                    hashMap.put("text", pText);
                                    hashMap.put("pViews", pView);
                                    hashMap.put("rViews", rViews);
                                    hashMap.put("type", pType);
                                    hashMap.put("video", video);
                                    hashMap.put("image", image);
                                    hashMap.put("reTweet", id);
                                    hashMap.put("content", content);
                                    hashMap.put("reId", postList.get(position).getpId());
                                    hashMap.put("privacy", ""+privacy);
                                    hashMap.put("pTime", pTime);
                                    hashMap.put("location", location);
                                    hashMap.put("link", link);
                                    DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                                    dRef.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            notify = true;
                                            DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            dataRef.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    ModelUser user = dataSnapshot.getValue(ModelUser.class);
//                                                    if (notify){
//                                                        if (pType.equals("text")){
//                                                            sendNotification(id, user.getUsername(),   " Rejoyed your Feel",postList.get(position).getpId());
//                                                            addToHisNotification2(""+id,""+postId,"Rejoyed your Feel");
//                                                        }else{
//                                                            sendNotification(id, user.getUsername(),  " Rejoyed your post",postList.get(position).getpId());
//                                                            addToHisNotification(""+id,""+postId,"Rejoyed your Post",image);
//                                                        }
//                                                    }
                                                    notify = false;
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                            FirebaseDatabase.getInstance().getReference().child("ReTweet").child(postList.get(position).getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                                            Toast.makeText(context, "Rejoyed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    mProcessRejoy = false;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });

            checkLike(holder, postList.get(position).getpId());

            numberLike(holder, postList.get(position).getpId());

            checkReTweet(holder, postList.get(position).getpId());

            numberReTweet(holder, postList.get(position).getpId());

            holder.pMeme.setOnClickListener(new DoubleClick(new DoubleClickListener() {
                @Override
                public void onSingleClick(View view) {
                    mProcessLike = true;
                    String postId = postList.get(position).getpId();
                    likeRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (mProcessLike)  {
                                if (dataSnapshot.child(postId).hasChild(userId)) {
                                    likeRef.child(postId).child(userId).removeValue();
                                    mProcessLike = false;
                                } else {
                                    likeRef.child(postId).child(userId).setValue(true);
                                    mProcessLike = false;
                                    ModelUser user = dataSnapshot.getValue(ModelUser.class);
//                                    if (pType.equals("text")){
//                                        sendNotification(id, user.getUsername(),  "Liked your Feel",postList.get(position).getpId());
//                                        addToHisNotification2(""+id,""+postId,"Liked your Feel");
//                                    }else{
//                                        sendNotification(id, user.getUsername(),  "Liked your Post",postList.get(position).getpId());
//                                        addToHisNotification(""+id,""+postId,"Liked your Post",image);
//                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

                @Override
                public void onDoubleClick(View view) {

                }
            }));

            holder.pMeme.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent intent = new Intent(context, PostComments.class);
                    intent.putExtra("id", postList.get(position).getpId());
                    intent.putExtra("userid", postList.get(position).getId());
                    context.startActivity(intent);
                    return false;
                }
            });

            holder.pText.setOnClickListener(new DoubleClick(new DoubleClickListener() {
                @Override
                public void onSingleClick(View view) {
                    mProcessLike = true;
                    String postId = postList.get(position).getpId();
                    likeRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (mProcessLike)  {
                                if (dataSnapshot.child(postId).hasChild(userId)) {
                                    likeRef.child(postId).child(userId).removeValue();
                                    mProcessLike = false;
                                } else {
                                    likeRef.child(postId).child(userId).setValue(true);
                                    mProcessLike = false;
                                    ModelUser user = dataSnapshot.getValue(ModelUser.class);
//                                    if (pType.equals("text")){
//                                        sendNotification(id, user.getUsername(),  "Liked your Feel",postList.get(position).getpId());
//                                        addToHisNotification2(""+id,""+postId,"Liked your Feel");
//                                    }else{
//                                        sendNotification(id, user.getUsername(),  "Liked your Post",postList.get(position).getpId());
//                                        addToHisNotification(""+id,""+postId,"Liked your Post",image);
//                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

                @Override
                public void onDoubleClick(View view) {

                }
            }));

            holder.pText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent intent = new Intent(context, PostComments.class);
                    intent.putExtra("id", postList.get(position).getpId());
                    intent.putExtra("userid", postList.get(position).getId());
                    context.startActivity(intent);
                    return false;
                }
            });

            holder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PostComments.class);
                    intent.putExtra("id", postList.get(position).getpId());
                    intent.putExtra("userid", postList.get(position).getId());
                    context.startActivity(intent);
                }
            });

            holder.more.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context, holder.more, Gravity.END);
                    popupMenu.getMenu().add(Menu.NONE,1,0, "Delete");
                    popupMenu.getMenu().add(Menu.NONE,2,0, "Delete from report");

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            if (id == 1){
                                String postId = postList.get(position).getpId();
                                deletePost(postId,pType,image,video);
                                FirebaseDatabase.getInstance().getReference().child("Report").child(postId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        snapshot.getRef().removeValue();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }   else  if (id == 2){
                                String postId = postList.get(position).getpId();
                                FirebaseDatabase.getInstance().getReference().child("Report").child(postId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        snapshot.getRef().removeValue();

                                        Toast.makeText(context, "ReTweet Removed", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });

            //CommentCount
            FirebaseDatabase.getInstance().getReference("Posts").child(postList.get(position).getpId()).child("Comments").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getChildrenCount() == 0){
                        holder.commentNo.setText("0");
                    }else {
                        holder.commentNo.setText(String.valueOf(snapshot.getChildrenCount()));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {

            FirebaseDatabase.getInstance().getReference().child("Views").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        if (snapshot.child(postList.get(position).getReId()).exists()){
//                            holder.view.setText(String.valueOf(snapshot.child(reId).getChildrenCount()));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //ReTweetDetails
            FirebaseDatabase.getInstance().getReference().child("Users").child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        if (snapshot.hasChild("id")){
                            String userId = snapshot.child("id").getValue().toString();
                            String username = Objects.requireNonNull(snapshot.child("username").getValue()).toString().trim();

                            holder.username.setVisibility(View.VISIBLE);
                            holder.username.setText(username);

                            holder.dot2.setVisibility(View.VISIBLE);

                            holder.username.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                        Intent intent = new Intent(context, UserProfile.class);
                                        intent.putExtra("hisUid", userId);
                                        context.startActivity(intent);
                                    }else {
                                        Intent intent = new Intent(context, UserProfile.class);
                                        intent.putExtra("hisUid", userId);
                                        context.startActivity(intent);
                                    }
                                }
                            });
                        }else{
                            holder.username.setText("noname");
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //User details
            FirebaseDatabase.getInstance().getReference().child("Users").child(postList.get(position).getReTweet()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        if (snapshot.hasChild("id")){
                            String userId = snapshot.child("id").getValue().toString();
                            String name = Objects.requireNonNull(snapshot.child("name").getValue()).toString().trim();
                            String dp = Objects.requireNonNull(snapshot.child("photo").getValue()).toString().trim();
                            String username = Objects.requireNonNull(snapshot.child("username").getValue()).toString().trim();

                            holder.pName.setText(name);

                            String mVerified = snapshot.child("verified").getValue().toString();

                            if (mVerified.isEmpty()){
                                holder.verified.setVisibility(View.GONE);
                            }else {
                                holder.verified.setVisibility(View.VISIBLE);
                            }

                            if (!dp.isEmpty()) {
                                Picasso.get().load(dp).placeholder(R.drawable.placeholder).into(holder.pCircleImageView);
                            }

                            holder.pName.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (postList.get(position).getReTweet().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                        Intent intent = new Intent(context, UserProfile.class);
                                        intent.putExtra("hisUid", userId);
                                        context.startActivity(intent);
                                    }else {
                                        Intent intent = new Intent(context, UserProfile.class);
                                        intent.putExtra("hisUid", userId);
                                        context.startActivity(intent);
                                    }
                                }
                            });

                            holder.pCircleImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (postList.get(position).getReTweet().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                        Intent intent = new Intent(context, UserProfile.class);
                                        intent.putExtra("hisUid", userId);
                                        context.startActivity(intent);
                                    }else {
                                        Intent intent = new Intent(context, UserProfile.class);
                                        intent.putExtra("hisUid", userId);
                                        context.startActivity(intent);
                                    }
                                }
                            });
                        }else{
                            holder.pName.setText("noname");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            if (location.isEmpty()){ }else{
                holder.location.setVisibility(View.VISIBLE);
                holder.location.setText(location);
                holder.dot3.setVisibility(View.VISIBLE);
            }

            switch (pType){
                case "text": {
                    holder.pText.setLinkText(pText);
                    holder.pText.setVisibility(View.VISIBLE);
                    holder.pText.setOnLinkClickListener((i, s) -> {
                        if (i == 1) {
                            Intent intent = new Intent(context, SearchActivity.class);
                            intent.putExtra("hashTag", s);
                            AdapterAdminPost.this.context.startActivity(intent);
                        }
                        else if (i == 2){
                            String username = s.replaceFirst("@","");
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                            Query query = ref.orderByChild("username").equalTo(username.trim());
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        String id1 = null;
                                        for (DataSnapshot ds : snapshot.getChildren()){
                                            id1 = ds.child("id").getValue().toString();
                                        }
                                        if (id1 != null){
                                            Intent intent = new Intent(context, UserProfile.class);
                                            intent.putExtra("hisUid", id1);
                                            context.startActivity(intent);
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
                        }
                        else if (i == 4){
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", s, null));
                            context.startActivity(intent);
                        }
                        else if (i == 8){
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.parse("mailto:"));
                            intent.putExtra(Intent.EXTRA_EMAIL, s);
                            intent.putExtra(Intent.EXTRA_SUBJECT, "");
                            context.startActivity(intent);

                        }
                    });
                    break;
                }
                case"image": {
                    if (!pText.isEmpty()){
                        holder.pText.setLinkText(pText);
                        holder.pText.setVisibility(View.VISIBLE);
                        holder.pText.setOnLinkClickListener((i, s) -> {
                            if (i == 1) {
                                Intent intent = new Intent(context, SearchActivity.class);
                                intent.putExtra("hashTag", s);
                                AdapterAdminPost.this.context.startActivity(intent);
                            }
                            else if (i == 2){
                                String username = s.replaceFirst("@","");
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                Query query = ref.orderByChild("username").equalTo(username.trim());
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            String id1 = null;
                                            for (DataSnapshot ds : snapshot.getChildren()){
                                                id1 = ds.child("id").getValue().toString();
                                            }
                                            if (id1 != null){
                                                Intent intent = new Intent(context, UserProfile.class);
                                                intent.putExtra("hisUid", id1);
                                                context.startActivity(intent);
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
                            }
                            else if (i == 4){
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", s, null));
                                context.startActivity(intent);
                            }
                            else if (i == 8){
                                Intent intent = new Intent(Intent.ACTION_SENDTO);
                                intent.setData(Uri.parse("mailto:"));
                                intent.putExtra(Intent.EXTRA_EMAIL, s);
                                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                                context.startActivity(intent);

                            }
                        });
                    }else{
                        holder.pText.setVisibility(View.GONE);
                    }
                    holder.pMeme.setVisibility(View.VISIBLE);
                    try {
                        Picasso.get().load(image).placeholder(R.drawable.placeholder).into(holder.pMeme);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }

            //Like
            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mProcessLike = true;
                    String postId = postList.get(position).getReId();
                    likeRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (mProcessLike) {
                                if (dataSnapshot.child(postId).hasChild(userId)) {
                                    likeRef.child(postId).child(userId).removeValue();
                                    mProcessLike = false;
                                } else {
                                    likeRef.child(postId).child(userId).setValue(true);
                                    mProcessLike = false;
                                    ModelUser user = dataSnapshot.getValue(ModelUser.class);
//                                    if (pType.equals("text")){
//                                        sendNotification(postList.get(position).getReTweet(), user.getUsername(),  "Liked your Feel",postList.get(position).getReId());
//                                        addToHisNotification2(""+id,""+postId,"Liked your Feel");
//                                    }else{
//                                        sendNotification(postList.get(position).getReTweet(), user.getUsername(),  "Liked your Post",postList.get(position).getReId());
//                                        addToHisNotification(""+id,""+postId,"Liked your Post",image);
//                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            });

            holder.rejoy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mProcessRejoy = true;
                    String postId = postList.get(position).getReId();
                    rejoyRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (mProcessRejoy) {
                                if (dataSnapshot.child(postId).hasChild(userId)) {
                                    holder.rejoy.setImageResource(R.drawable.icon_loop);
                                    Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("reId").equalTo(postList.get(position).getReId());
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                String userId = ds.child("id").getValue().toString();
                                                if (userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                                    ds.getRef().removeValue();
                                                    FirebaseDatabase.getInstance().getReference().child("ReTweet").child(postList.get(position).getReId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                                    Toast.makeText(context, "ReTweet Removed", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    mProcessRejoy = false;
                                } else {
                                    holder.rejoy.setImageResource(R.drawable.icon_loop2);
                                    String timeStamp = String.valueOf(System.currentTimeMillis());
                                    HashMap<Object, String> hashMap = new HashMap<>();
                                    hashMap.put("id", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                                    hashMap.put("pId", timeStamp);
                                    hashMap.put("text", pText);
                                    hashMap.put("pViews", pView);
                                    hashMap.put("rViews", rViews);
                                    hashMap.put("type", pType);
                                    hashMap.put("video", video);
                                    hashMap.put("image", image);
                                    hashMap.put("content", content);
                                    hashMap.put("reTweet", postList.get(position).getReTweet());
                                    hashMap.put("reId", postList.get(position).getReId());
                                    hashMap.put("privacy", ""+privacy);
                                    hashMap.put("pTime", pTime);
                                    hashMap.put("link", link);
                                    hashMap.put("location", location);
                                    DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                                    dRef.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            notify = true;
                                            DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            dataRef.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    ModelUser user = dataSnapshot.getValue(ModelUser.class);
//                                                    if (notify){
//                                                        if (pType.equals("text")){
//                                                            sendNotification(postList.get(position).getReTweet(), user.getUsername(),  " Rejoyed your Feel",postList.get(position).getReId());
//                                                            addToHisNotification2(""+id,""+postId,"Rejoyed your Feel");
//                                                        }else{
//                                                            sendNotification(postList.get(position).getReTweet(), user.getUsername(),  " Rejoyed your post",postList.get(position).getReId());
//                                                            addToHisNotification(""+id,""+postId,"Rejoyed your Post",image);
//                                                        }
//                                                    }
                                                    notify = false;
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                            FirebaseDatabase.getInstance().getReference().child("ReTweet").child(postList.get(position).getReId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);

                                            Toast.makeText(context, "ReTweeted", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    mProcessRejoy = false;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            });

            checkLike(holder, postList.get(position).getReId());

            //LikeNumber
            numberLike(holder, postList.get(position).getReId());

            //CheckReTweet
            checkReTweet(holder, postList.get(position).getReId());

            //ReTweetNumber
            numberReTweet(holder, postList.get(position).getReId());

            holder.pMeme.setOnClickListener(new DoubleClick(new DoubleClickListener() {
                @Override
                public void onSingleClick(View view) {
                    mProcessLike = true;
                    String postId = postList.get(position).getReId();
                    likeRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (mProcessLike) {
                                if (dataSnapshot.child(postId).hasChild(userId)) {
                                    likeRef.child(postId).child(userId).removeValue();
                                    mProcessLike = false;
                                } else {
                                    likeRef.child(postId).child(userId).setValue(true);
                                    mProcessLike = false;
                                    ModelUser user = dataSnapshot.getValue(ModelUser.class);
//                                    if (pType.equals("text")){
//                                        sendNotification(postList.get(position).getReTweet(), user.getUsername(),  "Liked your Feel",postList.get(position).getReId());
//                                        addToHisNotification2(""+id,""+postId,"Liked your Feel");
//                                    }else{
//                                        sendNotification(postList.get(position).getReTweet(), user.getUsername(),  "Liked your Post",postList.get(position).getReId());
//                                        addToHisNotification(""+id,""+postId,"Liked your Post",image);
//                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

                @Override
                public void onDoubleClick(View view) {

                }
            }));

            holder.pMeme.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent intent = new Intent(context, PostComments.class);
                    intent.putExtra("id", postList.get(position).getReId());
                    intent.putExtra("userid", postList.get(position).getId());
                    context.startActivity(intent);
                    return false;
                }
            });

            holder.pText.setOnClickListener(new DoubleClick(new DoubleClickListener() {
                @Override
                public void onSingleClick(View view) {
                    mProcessLike = true;
                    String postId = postList.get(position).getReId();
                    likeRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (mProcessLike) {
                                if (dataSnapshot.child(postId).hasChild(userId)) {
                                    likeRef.child(postId).child(userId).removeValue();
                                    mProcessLike = false;
                                } else {
                                    likeRef.child(postId).child(userId).setValue(true);
                                    mProcessLike = false;
                                    ModelUser user = dataSnapshot.getValue(ModelUser.class);
//                                    if (pType.equals("text")){
//                                        sendNotification(postList.get(position).getReTweet(), user.getUsername(),  "Liked your Feel",postList.get(position).getReId());
//                                        addToHisNotification2(""+id,""+postId,"Liked your Feel");
//                                    }else{
//                                        sendNotification(postList.get(position).getReTweet(), user.getUsername(),  "Liked your Post",postList.get(position).getReId());
//                                        addToHisNotification(""+id,""+postId,"Liked your Post",image);
//                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

                @Override
                public void onDoubleClick(View view) {

                }
            }));

            holder.pText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent intent = new Intent(context, PostComments.class);
                    intent.putExtra("id", postList.get(position).getReId());
                    intent.putExtra("userid", postList.get(position).getId());
                    context.startActivity(intent);
                    return false;
                }
            });

            holder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PostComments.class);
                    intent.putExtra("id", postList.get(position).getReId());
                    intent.putExtra("userid", postList.get(position).getId());
                    context.startActivity(intent);
                }
            });

            //Comment
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (pType.equals("video")){
//                        FirebaseDatabase.getInstance().getReference().child("Views").addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                if (snapshot.exists()){
//                                    if (snapshot.child(reId).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
//                                        Intent intent = new Intent(context, CommentActivity.class);
//                                        intent.putExtra("id", reId);
//                                        context.startActivity(intent);
//                                    }else {
//                                        FirebaseDatabase.getInstance().getReference().child("Views").child(reId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
//                                        Intent intent = new Intent(context, CommentActivity.class);
//                                        intent.putExtra("id", reId);
//                                        context.startActivity(intent);
//                                    }
//                                }else {
//                                    FirebaseDatabase.getInstance().getReference().child("Views").child(reId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
//                                    Intent intent = new Intent(context, CommentActivity.class);
//                                    intent.putExtra("id", reId);
//                                    context.startActivity(intent);
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
//                    }else {
//                        Intent intent = new Intent(context, CommentActivity.class);
//                        intent.putExtra("id", reId);
//                        context.startActivity(intent);
//                    }
//                }
//            });
//
//            holder.media_layout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (pType.equals("video")){
//                        FirebaseDatabase.getInstance().getReference().child("Views").addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                if (snapshot.exists()){
//                                    if (snapshot.child(reId).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
//                                        Intent intent = new Intent(context, CommentActivity.class);
//                                        intent.putExtra("id", reId);
//                                        context.startActivity(intent);
//                                    }else {
//                                        FirebaseDatabase.getInstance().getReference().child("Views").child(reId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
//                                        Intent intent = new Intent(context, CommentActivity.class);
//                                        intent.putExtra("id", reId);
//                                        context.startActivity(intent);
//                                    }
//                                }else {
//                                    FirebaseDatabase.getInstance().getReference().child("Views").child(reId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
//                                    Intent intent = new Intent(context, CommentActivity.class);
//                                    intent.putExtra("id", reId);
//                                    context.startActivity(intent);
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
//                    }else {
//                        Intent intent = new Intent(context, CommentActivity.class);
//                        intent.putExtra("id", reId);
//                        context.startActivity(intent);
//                    }
//                }
//            });
//
//
//            holder.comment.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(context, CommentActivity.class);
//                    intent.putExtra("id", reId);
//                    context.startActivity(intent);
//                }
//            });
//
//            holder.commented.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(context, CommentActivity.class);
//                    intent.putExtra("id", reId);
//                    context.startActivity(intent);
//                }
//            });
//
//            holder.commentTv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(context, CommentActivity.class);
//                    intent.putExtra("id", reId);
//                    context.startActivity(intent);
//                }
//            });

            //More
            holder.more.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context, holder.more, Gravity.END);
                    popupMenu.getMenu().add(Menu.NONE,1,0, "Delete Now");
                    popupMenu.getMenu().add(Menu.NONE,2,0, "Remove from report");

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            if (id == 1){
                                String rePost = postList.get(position).getReId();
                                FirebaseDatabase.getInstance().getReference().child("Report").child(rePost).removeValue();
                                Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("reId").equalTo(rePost);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                                            String mId = postList.get(position).getId();
                                            String userId = ds.child("id").getValue().toString();
                                            if (userId.equals(mId)){
                                                ds.getRef().removeValue();
                                                String idP = postList.get(position).getId();
                                                FirebaseDatabase.getInstance().getReference().child("ReTweet").child(rePost).child(idP).removeValue();
                                                String re = postList.get(position).getpId();
                                                FirebaseDatabase.getInstance().getReference().child("Report").child(re).removeValue();
                                                reTweetedString = "no";

                                                Toast.makeText(context, "ReTweet Removed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                            else  if (id == 2){
                                String rePost = postList.get(position).getpId();
                               DatabaseReference d = FirebaseDatabase.getInstance().getReference().child("Report").child(rePost);
                               d.addListenerForSingleValueEvent(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                       if (snapshot.exists()){
                                         snapshot.getRef().removeValue();
                                       }
                                   }

                                   @Override
                                   public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                   }
                               });
                                Toast.makeText(context, "Report Removed", Toast.LENGTH_SHORT).show();
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });

            //CommentCount
            FirebaseDatabase.getInstance().getReference("Posts").child(postList.get(position).getReId()).child("Comments").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getChildrenCount() == 0){
                        holder.commentNo.setText("0");
                    }else {
                        holder.commentNo.setText(String.valueOf(snapshot.getChildrenCount()));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
//        //Post details
//        holder.tv_tweet_text.setLinkText(pText);
//        holder.tv_tweet_text.setOnLinkClickListener(new SocialTextView.OnLinkClickListener() {
//            @Override
//            public void onLinkClicked(int i, String s) {
//                if (i == 1){
//                    Intent intent = new Intent(context, SearchActivity.class);
//                    intent.putExtra("hashTag", s);
//                    context.startActivity(intent);
//                }else
//                if (i == 2){
//                    String username = s.substring(1);
//                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//                    Query query = ref.orderByChild("username").equalTo(username);
//                    query.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            if (snapshot.exists()){
//                                for (DataSnapshot ds : snapshot.getChildren()){
//                                    String id = ds.child("id").getValue().toString();
//                                    if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
//                                        Intent intent = new Intent(context, UserProfile.class);
//                                        intent.putExtra("hisUid", id);
//                                        context.startActivity(intent);
//                                    }else {
//                                        Intent intent = new Intent(context, UserProfile.class);
//                                        intent.putExtra("hisUid", id);
//                                        context.startActivity(intent);
//                                    }
//                                }
//                            }else {
//
//                                Toast.makeText(context, "Invalid username, can't find user with this username", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//                }
//                else if (i == 16){
//                    String url = s;
//                    if (!url.startsWith("https://") && !url.startsWith("http://")){
//                        url = "http://" + url;
//                    }
//                    Intent openUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                    context.startActivity(openUrlIntent);
//                }else if (i == 4){
//                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", s, null));
//                    context.startActivity(intent);
//                }else if (i == 8){
//                    Intent intent = new Intent(Intent.ACTION_SENDTO);
//                    intent.setData(Uri.parse("mailto:"));
//                    intent.putExtra(Intent.EXTRA_EMAIL, s);
//                    intent.putExtra(Intent.EXTRA_SUBJECT, "");
//                    context.startActivity(intent);
//
//                }
//            }
//        });
//        if (pType.equals("image")){
//            String imgURi = postList.get(position).getImage();
//            Glide.with(context).asBitmap().load(imgURi).into(holder.media);
//            holder.media_layout.setVisibility(View.VISIBLE);
//        }else
//        if (pType.equals("video")){
//            String vidURi = postList.get(position).getVideo();
//            holder.play.setVisibility(View.VISIBLE);
//            Glide.with(context).asBitmap().load(vidURi).into(holder.media);
//            holder.media_layout.setVisibility(View.VISIBLE);
//            holder.views.setVisibility(View.VISIBLE);
//            holder.view.setVisibility(View.VISIBLE);
//        }
//
//        //Share
//        holder.tweet_action_edit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                switch (pType) {
//                    case "image": {
//                        //Share Image
//                        String imgURi = postList.get(position).getImage();
//                        Uri uri = Uri.parse(imgURi);
//                        Intent intent = new Intent(Intent.ACTION_SEND);
//                        intent.putExtra(Intent.EXTRA_STREAM, uri);
//                        intent.putExtra(Intent.EXTRA_TEXT, pText);
//                        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
//                        intent.setType("image/*");
//                        context.startActivity(Intent.createChooser(intent, "Share Via"));
//
//                        break;
//                    }
//                    case "video": {
//
//                        //Share Video
//                        String vidURi = postList.get(position).getVideo();
//                        Intent intent = new Intent(Intent.ACTION_SEND);
//                        intent.setType("text/*");
//                        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
//                        intent.putExtra(Intent.EXTRA_TEXT, pText + " Link: " + vidURi);
//                        context.startActivity(Intent.createChooser(intent, "Share Via"));
//
//                        break;
//                    }
//                    case "text": {
//
//                        //Share Text
//                        Intent intent = new Intent(Intent.ACTION_SEND);
//                        intent.setType("text/*");
//                        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
//                        intent.putExtra(Intent.EXTRA_TEXT, pText);
//                        context.startActivity(Intent.createChooser(intent, "Share Via"));
//
//                        break;
//                    }
//                }
//
//            }
//        });
    }

    //Download
    private void download(String pId, String image, String video) {
        if (!image.isEmpty()){

            StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(image);
            picRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String url = uri.toString();
                downloadImage(context, "Image", ".png", DIRECTORY_DOWNLOADS, url);

            });

        }if (!video.isEmpty()){

            StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(video);
            picRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String url = uri.toString();
                downloadVideo(context, "Video", ".mp4", DIRECTORY_DOWNLOADS, url);

            });

        }
    }

    //DownloadVideo
    private void downloadVideo(Context context, String video, String s, String directoryDownloads, String url) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri1 = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri1);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, directoryDownloads, video + s);
        Objects.requireNonNull(downloadManager).enqueue(request);
    }

    //DownloadImage
    public void downloadImage(Context context, String fileName, String fileExtension, String destinationDirectory, String url){
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri1 = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri1);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);
        Objects.requireNonNull(downloadManager).enqueue(request);
    }

    //Delete Post
    private void deletePost(String postId, String pType, String image, String video) {

        if (pType.equals("image")){

            StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(image);
            picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(postId);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                ds.getRef().removeValue();
                                Query query1 = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("reId").equalTo(postId);
                                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                                            ds.getRef().removeValue();
                                            FirebaseDatabase.getInstance().getReference().child("ReTweet").child(postId).removeValue();
                                            FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });

        }else if (pType.equals("video")){

            StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(video);
            picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(postId);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                ds.getRef().removeValue();
                                Query query1 = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("reId").equalTo(postId);
                                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                                            ds.getRef().removeValue();
                                            FirebaseDatabase.getInstance().getReference().child("ReTweet").child(postId).removeValue();
                                            FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });

        }else{
            Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(postId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        ds.getRef().removeValue();
                        Query query1 = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("reId").equalTo(postId);
                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    ds.getRef().removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("ReTweet").child(postId).removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }

    //NumberReTweet
    private void numberReTweet(MyHolder holder, String postId) {
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("ReTweet")){
                    if (snapshot.child("ReTweet").hasChild(postId)){
                        FirebaseDatabase.getInstance().getReference().child("ReTweet").child(postId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getChildrenCount() == 0){
                                    holder.rejoyNo.setText("0");
                                }else {
                                    holder.rejoyNo.setText(String.valueOf(snapshot.getChildrenCount()));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //CheckReTweet
    private void checkReTweet(MyHolder holder, String postId) {
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("ReTweet")){
                    if (snapshot.child("ReTweet").hasChild(postId)){
                        FirebaseDatabase.getInstance().getReference().child("ReTweet").child(postId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild(userId)){
                                    reTweetedString="yes";
                                    holder.rejoy.setImageResource(R.drawable.icon_loop2);
//                                    holder.rejoyed.setVisibility(View.VISIBLE);
//                                    holder.rejoy.setVisibility(View.GONE);
                                }else {
                                    reTweetedString="no";
                                    holder.rejoy.setImageResource(R.drawable.icon_loop);
//                                    holder.rejoyed.setVisibility(View.GONE);
//                                    holder.rejoy.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //CheckLike
    private void checkLike(MyHolder holder, String postId) {
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("Likes")){
                    if (snapshot.child("Likes").hasChild(postId)){
                        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild(userId)){
                                    likedP="yes";
                                    holder.like.setImageResource(R.drawable.icon_fav2);
//                                    holder.liked.setVisibility(View.VISIBLE);
//                                    holder.like.setVisibility(View.GONE);
                                }else {
                                    likedP="no";
                                    holder.like.setImageResource(R.drawable.icon_fav);
//                                    holder.liked.setVisibility(View.GONE);
//                                    holder.like.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //NumberLike
    private void numberLike(MyHolder holder, String postId) {
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("Likes")){
                    if (snapshot.child("Likes").hasChild(postId)){
                        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getChildrenCount() == 0){
                                    holder.likeNo.setText("0");
                                }else {
                                    holder.likeNo.setText(String.valueOf(snapshot.getChildrenCount()));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }


    class MyHolder extends RecyclerView.ViewHolder{

        final CircleImageView pCircleImageView;
        final ImageView pMeme;
        final ImageView more;
        final ImageView rejoy;
        final ImageView comment;
        final ImageView like;
        final TextView rejoyNo;
        final ImageView like_img;
        final ImageView eye;
        final ImageView dot2;
        final ImageView dot3;
        final ImageView dot4;
        final ImageView verified;
        final TextView pName;

        final TextView username;
        final TextView time;
        final TextView location;
        final TextView updated;
        final SocialTextView pText;
        final TextView likeNo;
        final TextView commentNo;
        final TextView views;

        final FrameLayout mfl;

        final RelativeLayout commentlayout;
        final RelativeLayout likeLayout;

        final RelativeLayout view_ly;
        final ConstraintLayout lol;
        final RelativeLayout video_share;
        final RelativeLayout rejoylayout;
        public Context context;

        final ImageView pause;
        final ProgressBar load;
        final ConstraintLayout constraintLayout;
        final ConstraintLayout constraintLayout1;
        final ConstraintLayout constraintLayout2;
        final ConstraintLayout constraintLayout3;
        final ConstraintLayout constraintLayout4;
        final ConstraintLayout viewlt;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            pCircleImageView = itemView.findViewById(R.id.circleImageView);
            eye = itemView.findViewById(R.id.eye);
            pMeme = itemView.findViewById(R.id.imageView);
            pName = itemView.findViewById(R.id.name);
            likeNo = itemView.findViewById(R.id.likeNo);
            commentNo = itemView.findViewById(R.id.commentNo);
            load = itemView.findViewById(R.id.load);
            views = itemView.findViewById(R.id.views);
            view_ly = itemView.findViewById(R.id.view_ly);
            more = itemView.findViewById(R.id.more);
//            pVine = itemView.findViewById(R.id.playerview);
            pause = itemView.findViewById(R.id.exomedia_controls_play_pause_btn);
            like_img = itemView.findViewById(R.id.like_img);
            pText = itemView.findViewById(R.id.textView);
            viewlt = itemView.findViewById(R.id.viewlt);
            commentlayout = itemView.findViewById(R.id.commentlayout);
            likeLayout = itemView.findViewById(R.id.likeLayout);

            video_share = itemView.findViewById(R.id.vine_share);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
            constraintLayout1 = itemView.findViewById(R.id.constraintLayout1);
            constraintLayout2 = itemView.findViewById(R.id.constraintLayout2);
            constraintLayout3 = itemView.findViewById(R.id.constraintLayout3);
            constraintLayout4 = itemView.findViewById(R.id.constraintLayout4);
            mfl = itemView.findViewById(R.id.fl);
            lol = itemView.findViewById(R.id.lol);
            rejoylayout = itemView.findViewById(R.id.rejoylayout);
            verified = itemView.findViewById(R.id.verified);
            rejoy = itemView.findViewById(R.id.rejoy);
            comment = itemView.findViewById(R.id.comment);
            like = itemView.findViewById(R.id.like);

            rejoyNo = itemView.findViewById(R.id.rejoyNo);
            time = itemView.findViewById(R.id.time);
            updated = itemView.findViewById(R.id.updated);
            location = itemView.findViewById(R.id.location);
            username = itemView.findViewById(R.id.username);
            dot2 = itemView.findViewById(R.id.dot2);
            dot3 = itemView.findViewById(R.id.dot3);
            dot4 = itemView.findViewById(R.id.dot4);

        }
    }

    private void addToHisNotification(String msg,String hisUid){
//        String timestamp = ""+System.currentTimeMillis();
//        HashMap<Object, String> hashMap = new HashMap<>();
//        hashMap.put("pId", postId);
//        hashMap.put("timestamp", timestamp);
//        hashMap.put("pUid", hisUid);
//        hashMap.put("notification", msg);
//        hashMap.put("sUid", FirebaseAuth.getInstance().getCurrentUser().getUid());
//
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//        ref.child(hisUid).child("Notifications").child(timestamp).setValue(hashMap);

    }

    private void sendNotification(final String hisId, final String name,final String message){
        requestQueue = Volley.newRequestQueue(context);
        DatabaseReference allToken = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allToken.orderByKey().equalTo(hisId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(FirebaseAuth.getInstance().getCurrentUser().getUid(), name + " : " + message, "New Commment", hisId,"", R.drawable.logo);
                    Sender sender = new Sender(data, token.getToken());
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", jsonObject, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("JSON_RESPONSE", "onResponse" + response.toString());

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("JSON_RESPONSE", "onResponse" + error.toString());
                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", "key=AAAA55rtIn4:APA91bHzTbsLtCMfjHcaVnaDC-iXGPVyPOGcAMFfs5vdg9uoCmEv9ifCDF8kCcyZOUudp8TbRLcC5AfQY5xS-wAujnJMB6OZ5xO-erpivhaFcdasN9ecJHtlfhmSYT2vQY19M-GMCVMK");
                                return headers;
                            }
                        };
                        requestQueue.add(jsonObjectRequest);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}

