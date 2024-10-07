package com.phigital.ai.Adapter;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
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
import com.google.firebase.storage.StorageReference;
import com.phigital.ai.Chat.ChatBottomSheet;
import com.phigital.ai.Model.ModelChat;
import com.phigital.ai.Model.ModelUser;

import com.phigital.ai.Post.PostDetails;
import com.phigital.ai.PostBottomSheet;
import com.phigital.ai.R;
import com.phigital.ai.Activity.SearchActivity;
import com.phigital.ai.Activity.ShareActivity;
import com.phigital.ai.Utility.UserProfile;
import com.phigital.ai.Utility.MediaView;

import com.squareup.picasso.Picasso;
import com.tylersuehr.socialtextview.SocialTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import me.jagar.chatvoiceplayerlibrary.VoicePlayerView;

import static android.os.Environment.DIRECTORY_DOWNLOADS;


public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder>{
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    FirebaseUser firebaseUser;
    private final Context context;
    private final ArrayList<ModelChat> modelChats;

    String postType;
    BottomSheetDialog reel_options;
//     String userId;
//     String sender;
    public AdapterChat(Context context, ArrayList<ModelChat> modelChats) {
        this.context = context;
        this.modelChats = modelChats;
    }

    @NonNull
    @Override
    public AdapterChat.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view;
//        if (viewType == MSG_TYPE_RIGHT){
//            view = LayoutInflater.from(context).inflate(R.layout.chat_left_list, parent, false);
//        }else{
//            view = LayoutInflater.from(context).inflate(R.layout.chat_right_list, parent, false);
//        }
//        return new MyHolder(view);
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_left_list, parent, false);
            return new MyHolder(view);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.chat_right_list, parent, false);
        return new MyHolder(view);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterChat.MyHolder holder, final int position) {

        holder.setIsRecyclable(false);
        //Seen
        ModelChat mChat = modelChats.get(position);

        if(mChat.getReceiver().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
            if (mChat.getHide()) {
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                holder.itemView.setVisibility(View.GONE);
            }
        }

        if (position == modelChats.size()-1){
            if (mChat.isIsSeen()) {
                holder.seen.setText("Seen");
            }else {
                holder.seen.setText("Delivered");
            }
        }else {
            holder.seen.setVisibility(View.GONE);
        }

        switch (modelChats.get(position).getType()) {
            case "text":
                holder.text.setVisibility(View.VISIBLE);
                holder.text.setLinkText(modelChats.get(position).getMsg());
                holder.text.setOnLinkClickListener((i, s) -> {
                    if (i == 1) {
                        Intent intent = new Intent(context, SearchActivity.class);
                        intent.putExtra("hashtag", s);
                        context.startActivity(intent);

                    } else if (i == 2) {
                        String username = s.replaceFirst("@", "");
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                        Query query = ref.orderByChild("username").equalTo(username.trim());
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        String id = ds.child("id").getValue().toString();
                                        if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                            Snackbar.make(holder.itemView, "It's you", Snackbar.LENGTH_LONG).show();
                                        } else {
                                            Intent intent = new Intent(context, UserProfile.class);
                                            intent.putExtra("hisUID", id);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            context.startActivity(intent);
                                        }
                                    }
                                } else {
                                    Snackbar.make(holder.itemView, "Invalid username, can't find user with this username", Snackbar.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Snackbar.make(holder.itemView, error.getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        });
                    } else if (i == 16) {
                        if (!s.startsWith("https://") && !s.startsWith("http://")) {
                            s = "http://" + s;
                        }
                        Intent openUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                        context.startActivity(openUrlIntent);
                    } else if (i == 4) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", s, null));
                        context.startActivity(intent);
                    } else if (i == 8) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:"));
                        intent.putExtra(Intent.EXTRA_EMAIL, s);
                        intent.putExtra(Intent.EXTRA_SUBJECT, "");
                        context.startActivity(intent);

                    }
                });
                break;
            case "image":
                holder.media.setVisibility(View.VISIBLE);
                holder.media_layout.setVisibility(View.VISIBLE);
                try{
                    Picasso.get()
                            .load(modelChats.get(position).getMsg())
                            .resize(300, 300)
                            .centerCrop()
                            .into(holder.media);
                }catch(Exception e){
                    Picasso.get().load(R.drawable.placeholder).into(holder.media);
                }
//                try {
//                    Picasso.get().load(modelChats.get(position).getMsg()).placeholder(R.drawable.placeholder).into(holder.media);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                break;
            case "doc":
                holder.doc.setVisibility(View.VISIBLE);
                holder.media_layout.setVisibility(View.VISIBLE);
                break;
            case "party":

                holder.text.setVisibility(View.VISIBLE);

                holder.text.setLinkText(modelChats.get(position).getMsg());

                break;
            case "video":
                holder.play.setVisibility(View.VISIBLE);
                holder.media.setVisibility(View.VISIBLE);
                holder.media_layout.setVisibility(View.VISIBLE);
                Glide.with(context).asBitmap().load(modelChats.get(position).getMsg()).thumbnail(0.1f).into(holder.media);
                break;
            case "audio":
                holder.voicePlayerView.setVisibility(View.VISIBLE);
                holder.media_layout.setVisibility(View.VISIBLE);
                holder.voicePlayerView.setAudio(modelChats.get(position).getMsg());
                break;
            case "gif":
                holder.media.setVisibility(View.VISIBLE);
                holder.media_layout.setVisibility(View.VISIBLE);
                Glide.with(context).load(modelChats.get(position).getMsg()).thumbnail(0.1f).into(holder.media);
                break;
            case "location":

                FirebaseDatabase.getInstance().getReference().child("Location").child(modelChats.get(position).getMsg()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

//                        double longitude = Double.parseDouble(snapshot.child("longitude").getValue().toString());
//                        double latitude = Double.parseDouble(snapshot.child("latitude").getValue().toString());
//
//                        MapboxStaticMap staticImage = MapboxStaticMap.builder()
//                                .accessToken("sk.eyJ1Ijoic3BhY2VzdGVyIiwiYSI6ImNrbmg2djJmdzJpZGQyd2xjeTk3a2twNTQifQ.iIiTRT_GwIYwFMsCWP5XGA")
//                                .styleId(StaticMapCriteria.DARK_STYLE)
//                                .cameraPoint(Point.fromLngLat(longitude, latitude))
//                                .cameraZoom(13)
//                                .width(250) // Image width
//                                .height(200) // Image height
//                                .retina(true) // Retina 2x image will be returned
//                                .build();
//
//                        holder.media.setVisibility(View.VISIBLE);
//                        holder.media_layout.setVisibility(View.VISIBLE);
//                        String imageUrl = staticImage.url().toString();
//                        Picasso.get().load(imageUrl).into(holder.media);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                break;
            case "post":
                holder.postly.setVisibility(View.VISIBLE);

                FirebaseDatabase.getInstance().getReference().child("Posts").child(modelChats.get(position).getMsg()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            postType = snapshot.child("type").getValue().toString();

                            FirebaseDatabase.getInstance().getReference("Users").child(snapshot.child("id").getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    //Top
                                    holder.nametv.setText(snapshot.child("name").getValue().toString());
                                    if (!snapshot.child("photo").getValue().toString().isEmpty()) {
                                        Picasso.get().load(snapshot.child("photo").getValue().toString()).into(holder.circleImageView2);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            //Mid
                            if (snapshot.child("type").getValue().toString().equals("text")) {
                                holder.image.setVisibility(View.GONE);
                            }
                            if (snapshot.child("type").getValue().toString().equals("image")) {
                                holder.image.setVisibility(View.VISIBLE);
                                Picasso.get().load(snapshot.child("image").getValue().toString()).into(holder.image);
                            }
                            // Later
//                        if (snapshot.child("type").getValue().toString().equals("vine")){
//                            holder.post_media.setVisibility(View.VISIBLE);
//                            holder.post_play.setVisibility(View.VISIBLE);
//                            Glide.with(context).asBitmap().load(snapshot.child("vine").getValue().toString()).thumbnail(0.1f).into(holder.post_media);
//                        }
//                        if (snapshot.child("type").getValue().toString().equals("gif")){
//                            holder.post_media.setVisibility(View.VISIBLE);
//                            Glide.with(context).load(modelChats.get(position).getMsg()).thumbnail(0.1f).into(holder.post_media);
//                        }
//                        if (snapshot.child("type").getValue().toString().equals("audio")){
//                            holder.post_voicePlayerView.setVisibility(View.VISIBLE);
//                            holder.post_voicePlayerView.setAudio(snapshot.child("meme").getValue().toString());
//                        }
//                        if (snapshot.child("type").getValue().toString().equals("bg")){
//                            holder.post_media.setVisibility(View.VISIBLE);
//                            Picasso.get().load(snapshot.child("meme").getValue().toString()).into( holder.post_media);
//                        }

                            //Bottom

                            holder.texttv.setVisibility(View.VISIBLE);
                            holder.texttv.setLinkText(snapshot.child("text").getValue().toString());
                            holder.texttv.setOnLinkClickListener((i, s) -> {
                                if (i == 1) {
                                    Intent intent = new Intent(context, SearchActivity.class);
                                    intent.putExtra("hashtag", s);
                                    context.startActivity(intent);

                                } else if (i == 2) {
                                    String username = s.replaceFirst("@", "");
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                    Query query = ref.orderByChild("username").equalTo(username.trim());
                                    query.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                for (DataSnapshot ds : snapshot.getChildren()) {
                                                    String id = ds.child("id").getValue().toString();
                                                    if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                                        Snackbar.make(holder.itemView, "It's you", Snackbar.LENGTH_LONG).show();
                                                    } else {
                                                        Intent intent = new Intent(context, UserProfile.class);
                                                        intent.putExtra("hisUID", id);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        context.startActivity(intent);
                                                    }
                                                }
                                            } else {
                                                Snackbar.make(holder.itemView, "Invalid username, can't find user with this username", Snackbar.LENGTH_LONG).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Snackbar.make(holder.itemView, error.getMessage(), Snackbar.LENGTH_LONG).show();
                                        }
                                    });
                                } else if (i == 16) {
                                    if (!s.startsWith("https://") && !s.startsWith("http://")) {
                                        s = "http://" + s;
                                    }
                                    Intent openUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                                    context.startActivity(openUrlIntent);
                                } else if (i == 4) {
                                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", s, null));
                                    context.startActivity(intent);
                                } else if (i == 8) {
                                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                                    intent.setData(Uri.parse("mailto:"));
                                    intent.putExtra(Intent.EXTRA_EMAIL, s);
                                    intent.putExtra(Intent.EXTRA_SUBJECT, "");
                                    context.startActivity(intent);

                                }
                            });
                        } else {
                            Query query = FirebaseDatabase.getInstance().getReference().child("Chat").orderByChild("timestamp").equalTo(mChat.getTimestamp());
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        ds.getRef().removeValue();
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

                //Click
                holder.itemView.setOnClickListener(v -> {
                    if (modelChats.get(position).getType().equals("post")) {

                        if (postType.equals("video")) {
//                        FirebaseDatabase.getInstance().getReference().child("Views").child(modelChats.get(position).getMsg()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
//                        Intent intent = new Intent(context, CommentActivity.class);
//                        intent.putExtra("postID", modelChats.get(position).getMsg());
//                        context.startActivity(intent);
                        } else {
//                        Intent intent = new Intent(context, CommentActivity.class);
//                        intent.putExtra("postID", modelChats.get(position).getMsg());
//                        context.startActivity(intent);
                        }

                    } else if (modelChats.get(position).getType().equals("image")) {
                        Intent intent = new Intent(context, MediaView.class);
                        intent.putExtra("type", "image");
                        intent.putExtra("uri", modelChats.get(position).getMsg());
                        context.startActivity(intent);
                    } else if (modelChats.get(position).getType().equals("video")) {
                        Intent intent = new Intent(context, MediaView.class);
                        intent.putExtra("type", "video");
                        intent.putExtra("uri", modelChats.get(position).getMsg());
                        context.startActivity(intent);
                    }
                });

                break;
            case "voice_call":
                holder.main.setVisibility(View.GONE);
                holder.call_layout.setVisibility(View.VISIBLE);
                holder.call.setText(modelChats.get(position).getMsg());
                holder.call_img.setImageResource(R.drawable.ic_audio_call);
                break;
            case "video_call":
                holder.main.setVisibility(View.GONE);
                holder.call_layout.setVisibility(View.VISIBLE);
                holder.call.setText(modelChats.get(position).getMsg());
                holder.call_img.setImageResource(R.drawable.ic_video_call);
                break;
            case "reel":
                holder.main.setVisibility(View.VISIBLE);
                holder.reelView.setVisibility(View.VISIBLE);
                Glide.with(context).asBitmap().load(modelChats.get(position).getMsg()).thumbnail(0.1f).into(holder.reelSource);
                break;
            case "story":
                holder.main.setVisibility(View.VISIBLE);
                holder.reelView.setVisibility(View.VISIBLE);
                holder.icon.setVisibility(View.GONE);
                holder.text.setVisibility(View.VISIBLE);
                holder.text.setText(modelChats.get(position).getMsg());
                FirebaseDatabase.getInstance().getReference("Story").child(modelChats.get(position).getReceiver()).child(modelChats.get(position).getTimestamp()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("type").getValue().toString().equals("image")) {
                            Picasso.get().load(snapshot.child("imageUri").getValue().toString()).into(holder.reelSource);
                        } else {
                            Glide.with(context).asBitmap().load(snapshot.child("imageUri").getValue().toString()).thumbnail(0.1f).into(holder.reelSource);
                            Toast.makeText(context, snapshot.child("imageUri").getValue().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
        }

        //More
        holder.itemView.setOnLongClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("id", modelChats.get(position).getSender());
            args.putString("chatId", modelChats.get(position).getTimestamp());
            args.putString("content", "chat");
            ChatBottomSheet bottomSheet = new ChatBottomSheet();
            bottomSheet.setArguments(args);
            bottomSheet.show(((FragmentActivity) context).getSupportFragmentManager(), bottomSheet.getTag());
//            more_bottom(holder, position);
//            reel_options.show();
            return false;
        });
        holder.text.setOnLongClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("id", modelChats.get(position).getSender());
            args.putString("chatId", modelChats.get(position).getTimestamp());
            args.putString("content", "chat");
            ChatBottomSheet bottomSheet = new ChatBottomSheet();
            bottomSheet.setArguments(args);
            bottomSheet.show(((FragmentActivity) context).getSupportFragmentManager(), bottomSheet.getTag());
//            more_bottom(holder, position);
//            reel_options.show();
            return false;
        });
        holder.media.setOnLongClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("id", modelChats.get(position).getSender());
            args.putString("chatId", modelChats.get(position).getTimestamp());
            args.putString("content", "chat");
            ChatBottomSheet bottomSheet = new ChatBottomSheet();
            bottomSheet.setArguments(args);
            bottomSheet.show(((FragmentActivity) context).getSupportFragmentManager(), bottomSheet.getTag());
//            more_bottom(holder, position);
//            reel_options.show();
            return false;
        });

        holder.voicePlayerView.setOnLongClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("id", modelChats.get(position).getSender());
            args.putString("chatId", modelChats.get(position).getTimestamp());
            args.putString("content", "chat");
            ChatBottomSheet bottomSheet = new ChatBottomSheet();
            bottomSheet.setArguments(args);
            bottomSheet.show(((FragmentActivity) context).getSupportFragmentManager(), bottomSheet.getTag());
//            more_bottom(holder, position);
//            reel_options.show();
            return false;
        });

        holder.post.setOnLongClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("id", modelChats.get(position).getSender());
            args.putString("chatId", modelChats.get(position).getTimestamp());
            args.putString("content", "chat");
            ChatBottomSheet bottomSheet = new ChatBottomSheet();
            bottomSheet.setArguments(args);
            bottomSheet.show(((FragmentActivity) context).getSupportFragmentManager(), bottomSheet.getTag());
//            more_bottom(holder, position);
//            reel_options.show();
            return false;
        });

        holder.media_layout.setOnClickListener(v -> {

            switch (modelChats.get(position).getType()) {

                case "doc":

                    Snackbar.make(v, "Downloading...", Snackbar.LENGTH_LONG).show();

                    StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(modelChats.get(position).getMsg());
                    picRef.getDownloadUrl().addOnSuccessListener(uri -> {

                        picRef.getMetadata().addOnSuccessListener(storageMetadata -> {
                            String extension = storageMetadata.getContentType();
                            String url = uri.toString();
                            downloadDoc(context, DIRECTORY_DOWNLOADS, url, extension);
                        });


                    });


                    break;

                case "image":

                    Intent intent = new Intent(context, MediaView.class);
                    intent.putExtra("type", "image");
                    intent.putExtra("uri", modelChats.get(position).getMsg());
                    context.startActivity(intent);

                    break;
                case "video":

                    Intent intent1 = new Intent(context, MediaView.class);
                    intent1.putExtra("type", "video");
                    intent1.putExtra("uri", modelChats.get(position).getMsg());
                    context.startActivity(intent1);

                    break;
                case "party":

                    FirebaseDatabase.getInstance().getReference().child("party").child( modelChats.get(position).getTimestamp()).child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Party").child(modelChats.get(position).getTimestamp()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String timeStamp = ""+System.currentTimeMillis();
                            HashMap<String, Object> hashMap1 = new HashMap<>();
                            hashMap1.put("ChatId", timeStamp);
                            hashMap1.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            hashMap1.put("msg", "has joined");
                            FirebaseDatabase.getInstance().getReference().child("Party").child(modelChats.get(position).getTimestamp()).child("Chats").child(timeStamp).setValue(hashMap1);

                            if (snapshot.child("type").getValue().toString().equals("upload_youtube")){
//                                Intent intent = new Intent(context, StartYouTubeActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                intent.putExtra("room", modelChats.get(position).getTimestamp());
//                                context.startActivity(intent);
                            }else {
//                                Intent intent = new Intent(context, StartPartyActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                intent.putExtra("room", modelChats.get(position).getTimestamp());
//                                context.startActivity(intent);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    break;

                case "reel":

//                    Intent intent3 = new Intent(context, ViewReelActivity.class);
//                    intent3.putExtra("id", modelChats.get(position).getMsg());
//                    context.startActivity(intent3);

                    break;

                case "story":

//                    Intent intent9 = new Intent(context, ChatStoryViewActivity.class);
//                    intent9.putExtra("userid", modelChats.get(position).getReceiver());
//                    intent9.putExtra("storyid", modelChats.get(position).getTimestamp());
//                    context.startActivity(intent9);

                    break;

                case "high":

//                    Intent intent2 = new Intent(context, HighViewActivity.class);
//                    intent2.putExtra("userid", modelChats.get(position).getMsg());
//                    context.startActivity(intent2);

                    break;

                case "location":

                    FirebaseDatabase.getInstance().getReference().child("Location").child(modelChats.get(position).getMsg()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){

                                double longitude = Double.parseDouble(snapshot.child("longitude").getValue().toString());
                                double latitude = Double.parseDouble(snapshot.child("latitude").getValue().toString());

                                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
                                Intent intent11 = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                context.startActivity(intent11);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    break;
                case "meet":

//                    Intent intent21 = new Intent(context, MeetingActivity.class);
//                    intent21.putExtra("meet", modelChats.get(position).getTimestamp());
//                    context.startActivity(intent21);

                    break;

            }

        });

        holder.itemView.setOnClickListener(v -> {
            switch (modelChats.get(position).getType()) {

                case "doc":

                    Snackbar.make(v, "Downloading...", Snackbar.LENGTH_LONG).show();

                    StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(modelChats.get(position).getMsg());
                    picRef.getDownloadUrl().addOnSuccessListener(uri -> {

                        picRef.getMetadata().addOnSuccessListener(storageMetadata -> {
                            String extension = storageMetadata.getContentType();
                            String url = uri.toString();
                            downloadDoc(context, DIRECTORY_DOWNLOADS, url, extension);
                        });


                    });


                    break;

                case "image":

                    Intent intent = new Intent(context, MediaView.class);
                    intent.putExtra("type", "image");
                    intent.putExtra("uri", modelChats.get(position).getMsg());
                    context.startActivity(intent);

                    break;
                case "video":

                    Intent intent1 = new Intent(context, MediaView.class);
                    intent1.putExtra("type", "video");
                    intent1.putExtra("uri", modelChats.get(position).getMsg());
                    context.startActivity(intent1);

                    break;
                case "party":

                    FirebaseDatabase.getInstance().getReference().child("party").child( modelChats.get(position).getTimestamp()).child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Party").child(modelChats.get(position).getTimestamp()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String timeStamp = ""+System.currentTimeMillis();
                            HashMap<String, Object> hashMap1 = new HashMap<>();
                            hashMap1.put("ChatId", timeStamp);
                            hashMap1.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            hashMap1.put("msg", "has joined");
                            FirebaseDatabase.getInstance().getReference().child("Party").child(modelChats.get(position).getTimestamp()).child("Chats").child(timeStamp).setValue(hashMap1);

                            if (snapshot.child("type").getValue().toString().equals("upload_youtube")){
//                                Intent intent = new Intent(context, StartYouTubeActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                intent.putExtra("room", modelChats.get(position).getTimestamp());
//                                context.startActivity(intent);
                            }else {
//                                Intent intent = new Intent(context, StartPartyActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                intent.putExtra("room", modelChats.get(position).getTimestamp());
//                                context.startActivity(intent);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    break;

                case "reel":

//                    Intent intent3 = new Intent(context, ViewReelActivity.class);
//                    intent3.putExtra("id", modelChats.get(position).getMsg());
//                    context.startActivity(intent3);

                    break;

                case "story":

//                    Intent intent9 = new Intent(context, ChatStoryViewActivity.class);
//                    intent9.putExtra("userid", modelChats.get(position).getReceiver());
//                    intent9.putExtra("storyid", modelChats.get(position).getTimestamp());
//                    context.startActivity(intent9);

                    break;

                case "high":

//                    Intent intent2 = new Intent(context, HighViewActivity.class);
//                    intent2.putExtra("userid", modelChats.get(position).getMsg());
//                    context.startActivity(intent2);

                    break;
                case "location":

                    FirebaseDatabase.getInstance().getReference().child("Location").child(modelChats.get(position).getMsg()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){

                                double longitude = Double.parseDouble(snapshot.child("longitude").getValue().toString());
                                double latitude = Double.parseDouble(snapshot.child("latitude").getValue().toString());

                                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
                                Intent intent11 = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                context.startActivity(intent11);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    break;

                case "meet":

//                    Intent intent21 = new Intent(context, MeetingActivity.class);
//                    intent21.putExtra("meet", modelChats.get(position).getTimestamp());
//                    context.startActivity(intent21);

                    break;
                case "post":
                    Intent intent2 = new Intent(context, PostDetails.class);
                    intent2.putExtra("mean", "post");
                    intent2.putExtra("postId", modelChats.get(position).getMsg());
                    context.startActivity(intent2);
                    break;
            }
        });

        holder.media.setOnClickListener(v -> {

            switch (modelChats.get(position).getType()) {

                case "doc":

                    Snackbar.make(v, "Downloading...", Snackbar.LENGTH_LONG).show();

                    StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(modelChats.get(position).getMsg());
                    picRef.getDownloadUrl().addOnSuccessListener(uri -> {

                        picRef.getMetadata().addOnSuccessListener(storageMetadata -> {
                            String extension = storageMetadata.getContentType();
                            String url = uri.toString();
                            downloadDoc(context, DIRECTORY_DOWNLOADS, url, extension);
                        });


                    });


                    break;

                case "image":

                    Intent intent = new Intent(context, MediaView.class);
                    intent.putExtra("type", "image");
                    intent.putExtra("uri", modelChats.get(position).getMsg());
                    context.startActivity(intent);

                    break;
                case "video":

                    Intent intent1 = new Intent(context, MediaView.class);
                    intent1.putExtra("type", "video");
                    intent1.putExtra("uri", modelChats.get(position).getMsg());
                    context.startActivity(intent1);

                    break;
                case "party":

                    FirebaseDatabase.getInstance().getReference().child("party").child( modelChats.get(position).getTimestamp()).child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Party").child(modelChats.get(position).getTimestamp()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String timeStamp = ""+System.currentTimeMillis();
                            HashMap<String, Object> hashMap1 = new HashMap<>();
                            hashMap1.put("ChatId", timeStamp);
                            hashMap1.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            hashMap1.put("msg", "has joined");
                            FirebaseDatabase.getInstance().getReference().child("Party").child(modelChats.get(position).getTimestamp()).child("Chats").child(timeStamp).setValue(hashMap1);

                            if (snapshot.child("type").getValue().toString().equals("upload_youtube")){
//                                Intent intent = new Intent(context, StartYouTubeActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                intent.putExtra("room", modelChats.get(position).getTimestamp());
//                                context.startActivity(intent);
                            }else {
//                                Intent intent = new Intent(context, StartPartyActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                intent.putExtra("room", modelChats.get(position).getTimestamp());
//                                context.startActivity(intent);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    break;

                case "reel":

//                    Intent intent3 = new Intent(context, ViewReelActivity.class);
//                    intent3.putExtra("id", modelChats.get(position).getMsg());
//                    context.startActivity(intent3);

                    break;

                case "story":

//                    Intent intent9 = new Intent(context, ChatStoryViewActivity.class);
//                    intent9.putExtra("userid", modelChats.get(position).getReceiver());
//                    intent9.putExtra("storyid", modelChats.get(position).getTimestamp());
//                    context.startActivity(intent9);

                    break;

                case "high":

//                    Intent intent2 = new Intent(context, HighViewActivity.class);
//                    intent2.putExtra("userid", modelChats.get(position).getMsg());
//                    context.startActivity(intent2);

                    break;

                case "location":

                    FirebaseDatabase.getInstance().getReference().child("Location").child(modelChats.get(position).getMsg()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){

                                double longitude = Double.parseDouble(snapshot.child("longitude").getValue().toString());
                                double latitude = Double.parseDouble(snapshot.child("latitude").getValue().toString());

                                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
                                Intent intent11 = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                context.startActivity(intent11);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    break;
                case "meet":

//                    Intent intent21 = new Intent(context, MeetingActivity.class);
//                    intent21.putExtra("meet", modelChats.get(position).getTimestamp());
//                    context.startActivity(intent21);

                    break;

            }

        });

        holder.reelSource.setOnClickListener(v -> {

            switch (modelChats.get(position).getType()){
                case "reel":

//                    Intent intent3 = new Intent(context, ViewReelActivity.class);
//                    intent3.putExtra("id", modelChats.get(position).getMsg());
//                    context.startActivity(intent3);

                    break;
                case "story":

//                    Intent intent = new Intent(context, ChatStoryViewActivity.class);
//                    intent.putExtra("userid", modelChats.get(position).getReceiver());
//                    intent.putExtra("storyid", modelChats.get(position).getTimestamp());
//                    context.startActivity(intent);

                    break;

                case "high":

//                    Intent intent2 = new Intent(context, HighViewActivity.class);
//                    intent2.putExtra("userid", modelChats.get(position).getMsg());
//                    context.startActivity(intent2);

                    break;

            }

        });
    }

    private void more_bottom(MyHolder holder, int position) {
        if (reel_options == null){
            View view = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.activity_chat_bottom_sheet, null);
//            TextView time = view.findViewById(R.id.time);
//            @SuppressLint("SimpleDateFormat")
//            String value = new java.text.SimpleDateFormat("dd/MM/yy - h:mm a").
//                    format(new java.util.Date(Long.parseLong(modelChats.get(position).getTimestamp()) * 1000));
//            time.setText(value);
            ConstraintLayout save = view.findViewById(R.id.save);
            ImageView star = view.findViewById(R.id.star);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference().child("Favourite").child((Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser())).getUid());
                likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(modelChats.get(position).getTimestamp()).exists()) {
                            likeRef.child(modelChats.get(position).getTimestamp()).removeValue();
                            star.setImageResource(R.drawable.starregular);
                        } else {
                            likeRef.child(modelChats.get(position).getTimestamp()).setValue("star");
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

            ConstraintLayout forward = view.findViewById(R.id.forward);
            forward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ShareActivity.class);
                    intent.putExtra("postId", modelChats.get(position).getMsg());
                    intent.putExtra("type", modelChats.get(position).getType());
                    intent.putExtra("content", "chat");
                    context.startActivity(intent);
                }
            });
//            LinearLayout report = view.findViewById(R.id.report);
//            report.setOnClickListener(v -> {
//                FirebaseDatabase.getInstance().getReference().child("ReportChat").child(modelChats.get(position).getTimestamp()).setValue(true);
//                Snackbar.make(holder.itemView,"Reported", Snackbar.LENGTH_LONG).show();
//            });
            ConstraintLayout delete = view.findViewById(R.id.delete);
            delete.setOnClickListener(v -> {
                String type = modelChats.get(position).getType();
                if (type.equals("text") || type.equals("location")){
                    Query query = FirebaseDatabase.getInstance().getReference().child("Chats").orderByChild("timestamp").equalTo(modelChats.get(position).getTimestamp());
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()){
                                ds.getRef().removeValue();
//                                Snackbar.make(holder.itemView,"Deleted", Snackbar.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else if (type.equals("story") || type.equals("high")){
                    Query query = FirebaseDatabase.getInstance().getReference().child("Chats").orderByChild("timestamp").equalTo(modelChats.get(position).getTimestamp());
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
                }  else if (type.equals("voice_call")){
                    Query query = FirebaseDatabase.getInstance().getReference().child("Chats").orderByChild("timestamp").equalTo(modelChats.get(position).getTimestamp());
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()){
                                ds.getRef().removeValue();
                                Snackbar.make(holder.itemView,"Deleted", Snackbar.LENGTH_LONG).show();
                                Query query = FirebaseDatabase.getInstance().getReference().child("calling").orderByChild("room").equalTo(modelChats.get(position).getTimestamp());
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot ds : snapshot.getChildren()){
                                            ds.getRef().removeValue();
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
                }
                else if (type.equals("video_call")){
                    Query query = FirebaseDatabase.getInstance().getReference().child("Chats").orderByChild("timestamp").equalTo(modelChats.get(position).getTimestamp());
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()){
                                ds.getRef().removeValue();
                                Snackbar.make(holder.itemView,"Deleted", Snackbar.LENGTH_LONG).show();

                                Query query = FirebaseDatabase.getInstance().getReference().child("calling").orderByChild("room").equalTo(modelChats.get(position).getTimestamp());
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot ds : snapshot.getChildren()){
                                            ds.getRef().removeValue();
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
                }
                else if (type.equals("post")){
                    Query query = FirebaseDatabase.getInstance().getReference().child("Chats").orderByChild("timestamp").equalTo(modelChats.get(position).getTimestamp());
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
                else if (type.equals("party")){
                    Query query = FirebaseDatabase.getInstance().getReference().child("Chats").orderByChild("timestamp").equalTo(modelChats.get(position).getTimestamp());
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
                }else if (type.equals("reel")){
                    Query query = FirebaseDatabase.getInstance().getReference().child("Chats").orderByChild("timestamp").equalTo(modelChats.get(position).getTimestamp());
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
                else  if (type.equals("gif")){
                    Query query = FirebaseDatabase.getInstance().getReference().child("Chats").orderByChild("timestamp").equalTo(modelChats.get(position).getTimestamp());
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
                    FirebaseStorage.getInstance().getReferenceFromUrl(modelChats.get(position).getMsg()).delete().addOnCompleteListener(task -> {
                        Query query = FirebaseDatabase.getInstance().getReference().child("Chats").orderByChild("timestamp").equalTo(modelChats.get(position).getTimestamp());
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
//            LinearLayout download = view.findViewById(R.id.download);
//            download.setOnClickListener(v -> {
//                String type = modelChats.get(position).getType();
//                switch (type) {
//                    case "doc":
//                        Snackbar.make(v, "Downloading...", Snackbar.LENGTH_LONG).show();
//
//                        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(modelChats.get(position).getMsg());
//                        picRef.getDownloadUrl().addOnSuccessListener(uri -> {
//
//                            picRef.getMetadata().addOnSuccessListener(storageMetadata -> {
//                                String extension = storageMetadata.getContentType();
//                                String url = uri.toString();
//                                downloadDoc(context, DIRECTORY_DOWNLOADS, url, extension);
//                            });
//
//
//                        });
//
//                        break;
//                    case "video": {
//                        Snackbar.make(holder.itemView, "Please wait downloading", Snackbar.LENGTH_LONG).show();
//                        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(modelChats.get(position).getMsg()));
//                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//                        request.setDestinationInExternalFilesDir(context, DIRECTORY_DOWNLOADS, String.valueOf(System.currentTimeMillis()) + ".mp4");
//                        Objects.requireNonNull(downloadManager).enqueue(request);
//                        break;
//                    }
//                    case "reel": {
//                        Snackbar.make(holder.itemView, "Downloading", Snackbar.LENGTH_LONG).show();
//                        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(modelChats.get(position).getMsg()));
//                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//                        request.setDestinationInExternalFilesDir(context, DIRECTORY_DOWNLOADS, String.valueOf(System.currentTimeMillis()) + ".mp4");
//                        Objects.requireNonNull(downloadManager).enqueue(request);
//                        break;
//                    }
//                    case "image": {
//                        Snackbar.make(holder.itemView, "Please wait downloading", Snackbar.LENGTH_LONG).show();
//                        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(modelChats.get(position).getMsg()));
//                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//                        request.setDestinationInExternalFilesDir(context, DIRECTORY_DOWNLOADS, String.valueOf(System.currentTimeMillis()) + ".png");
//                        Objects.requireNonNull(downloadManager).enqueue(request);
//                        break;
//                    }
//                    case "audio": {
//                        Snackbar.make(holder.itemView, "Please wait downloading", Snackbar.LENGTH_LONG).show();
//                        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(modelChats.get(position).getMsg()));
//                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//                        request.setDestinationInExternalFilesDir(context, DIRECTORY_DOWNLOADS, System.currentTimeMillis() + ".mp3");
//                        Objects.requireNonNull(downloadManager).enqueue(request);
//                        break;
//                    }
//                    default:
//                        Snackbar.make(holder.itemView, "This type of message can't be downloaded", Snackbar.LENGTH_LONG).show();
//                        break;
//                }
//            });
            ConstraintLayout copy = view.findViewById(R.id.copy);
            copy.setOnClickListener(v -> {
                Snackbar.make(holder.itemView,"Copied", Snackbar.LENGTH_LONG).show();
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", modelChats.get(position).getMsg());
                clipboard.setPrimaryClip(clip);
            });
//            LinearLayout maximize = view.findViewById(R.id.maximize);
//
//            switch (modelChats.get(position).getType()) {
//                case "image":
//                    maximize.setVisibility(View.VISIBLE);
//                case "video":
//                    maximize.setVisibility(View.VISIBLE);
//                    break;
//            }
//
//            if (modelChats.get(position).getType().equals("image") || modelChats.get(position).getType().equals("video")  || modelChats.get(position).getType().equals("doc") || modelChats.get(position).getType().equals("audio") || modelChats.get(position).getType().equals("reel")){
//                download.setVisibility(View.VISIBLE);
//            }else {
//                download.setVisibility(View.GONE);
//            }
//
//            maximize.setOnClickListener(v -> {
//                switch (modelChats.get(position).getType()) {
//                    case "image":
//
//                        Intent intent = new Intent(context, MediaView.class);
//                        intent.putExtra("type", "image");
//                        intent.putExtra("uri", modelChats.get(position).getMsg());
//                        context.startActivity(intent);
//
//                        break;
//                    case "video":
//
//                        Intent intent1 = new Intent(context, MediaView.class);
//                        intent1.putExtra("type", "video");
//                        intent1.putExtra("uri", modelChats.get(position).getMsg());
//                        context.startActivity(intent1);
//
//                        break;
//                }
//            });

            reel_options = new BottomSheetDialog(holder.itemView.getContext());
            reel_options.setContentView(view);
        }
    }

    @Override
    public int getItemCount() {
        return modelChats.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        final TextView seen;
        final SocialTextView text;
        final ImageView media;
        final ImageView post_media;
        final ImageView play;
        final ImageView post_play;
        final VoicePlayerView voicePlayerView;
        final VoicePlayerView post_voicePlayerView;
        final RelativeLayout media_layout;
        final RelativeLayout doc;

        //Post
        final TextView name;
        final SocialTextView post_text;
        final CircleImageView avatar;
        final LinearLayout head;
        final LinearLayout post;

        final ConstraintLayout postly;
        final CircleImageView circleImageView2;
        final TextView nametv;
        final SocialTextView texttv;
        final ImageView image;

        //Call
        final LinearLayout call_layout;
        final LinearLayout main;
        ImageView call_img;
        final TextView call;
         LinearLayout messageLayout;

        //Reel
        final CardView reelView;
        final ImageView reelSource;
        final ImageView icon;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            postly = itemView.findViewById(R.id.postly);
            messageLayout = itemView.findViewById(R.id.messageLayout);
            circleImageView2 = itemView.findViewById(R.id.circleImageView2);
            nametv = itemView.findViewById(R.id.nametv);
            texttv = itemView.findViewById(R.id.texttv);
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
            media =  itemView.findViewById(R.id.media);
            play =  itemView.findViewById(R.id.play);
            voicePlayerView =  itemView.findViewById(R.id.voicePlayerView);
            avatar  =  itemView.findViewById(R.id.avatar);
            name  =  itemView.findViewById(R.id.name);
            doc  =  itemView.findViewById(R.id.doc);
            head  =  itemView.findViewById(R.id.head);
            post_media =  itemView.findViewById(R.id.post_media);
            post_play =  itemView.findViewById(R.id.post_play);
            post_voicePlayerView  =  itemView.findViewById(R.id.post_voicePlayerView);
            post_text  =  itemView.findViewById(R.id.post_text);
            seen   =  itemView.findViewById(R.id.seen);
            media_layout =  itemView.findViewById(R.id.media_layout);
            post =  itemView.findViewById(R.id.post);
            call_layout =  itemView.findViewById(R.id.call_layout);
            call_img =  itemView.findViewById(R.id.call_img);
            call_img =  itemView.findViewById(R.id.call_img);
            call  =  itemView.findViewById(R.id.call);
            main =  itemView.findViewById(R.id.main);
            reelView=  itemView.findViewById(R.id.reel);
            reelSource=  itemView.findViewById(R.id.reelSource);
            icon = itemView.findViewById(R.id.icon);
        }

    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (!modelChats.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_LEFT;
        }
        else {
            return MSG_TYPE_RIGHT;
        }
    }

    private void downloadDoc(Context context, String directoryDownloads, String url, String extension) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri1 = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri1);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, directoryDownloads, extension);
        Objects.requireNonNull(downloadManager).enqueue(request);
    }
}

