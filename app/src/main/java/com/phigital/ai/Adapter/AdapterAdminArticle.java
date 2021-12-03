package com.phigital.ai.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.phigital.ai.GetTimeAgo;
import com.phigital.ai.Article.ArticleViewActivity;
import com.phigital.ai.Model.ModelArticle;

import com.phigital.ai.R;
import com.phigital.ai.Utility.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterAdminArticle extends RecyclerView.Adapter<AdapterAdminArticle.MyHolder> {

    Context context;
    final List<ModelArticle> articleList;

    public AdapterAdminArticle(Context context, List<ModelArticle> articleList) {
        this.context = context;
        this.articleList = articleList;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_item, parent, false);
        context = parent.getContext();
        return new MyHolder(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        String id = articleList.get(position).getId();
        String pId = articleList.get(position).getpId();
        String text = articleList.get(position).getText();
        String pViews = articleList.get(position).getpViews();
        String pTime = articleList.get(position).getpTime();
        String title = articleList.get(position).getTitle();
        String type = articleList.get(position).getType();
        String category = articleList.get(position).getCategory();
        String image = articleList.get(position).getImage();
        String video = articleList.get(position).getVideo();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String  userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        long lastTime = Long.parseLong(pTime);
        String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime);

        FirebaseDatabase.getInstance().getReference().child("Ban").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    holder.itemView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Views").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.child(articleList.get(position).getpId()).exists()){
//                            holder.view.setText(String.valueOf(snapshot.child(postList.get(position).getpId()).getChildrenCount()));
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
                String userId = snapshot.child("id").getValue().toString();
                String name = Objects.requireNonNull(snapshot.child("name").getValue()).toString().trim();
                String dp = Objects.requireNonNull(snapshot.child("photo").getValue()).toString().trim();
                String username = Objects.requireNonNull(snapshot.child("username").getValue()).toString().trim();

                String mVerified = snapshot.child("verified").getValue().toString();

                if (mVerified.isEmpty()){
                    holder.verified.setVisibility(View.GONE);
                }else {
                    holder.verified.setVisibility(View.VISIBLE);
                }

                holder.pName.setText(name);

                if (!dp.isEmpty()) {
                    Picasso.get().load(dp).placeholder(R.drawable.placeholder).into(holder.pDp);
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

                holder.pDp.setOnClickListener(new View.OnClickListener() {
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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        holder.pCategory.setText(category);
        holder.pTitle.setText(title);
        holder.time.setText(lastSeenTime);

        if (type.equals("image")){
            try {
                Picasso.get().load(image).placeholder(R.drawable.placeholder).into(holder.pMeme);
            } catch (Exception ignored) {

            }
        }

        holder.constraintLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, ArticleViewActivity.class);
            intent.putExtra("articleId", pId);
            context.startActivity(intent);
        });

        holder.constraintLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.pMeme, Gravity.END);
                popupMenu.getMenu().add(Menu.NONE,1,0, "Delete");
                popupMenu.getMenu().add(Menu.NONE,2,0, "Delete from report");

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == 1){
                            deletePost(image,pId);
                            FirebaseDatabase.getInstance().getReference().child("ArticleReport").child(pId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    snapshot.getRef().removeValue();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }   else  if (id == 2){
                            FirebaseDatabase.getInstance().getReference().child("ArticleReport").child(pId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    snapshot.getRef().removeValue();

                                    Toast.makeText(context, "Article Removed", Toast.LENGTH_SHORT).show();
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
                return false;
            }
        });

    }

    private void deletePost(String image, String pId) {
        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(image);
        picRef.delete().addOnSuccessListener(aVoid -> {
            Query query = FirebaseDatabase.getInstance().getReference("Article").orderByChild("pId").equalTo(pId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        ds.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }).addOnFailureListener(e -> {
        });
    }


    @Override
    public int getItemCount() {
        return articleList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        final CircleImageView pDp;
        final ImageView pMeme;
        final ImageView more;
        final ImageView like_img;
        final ImageView eye;
        final TextView pName;
        final TextView time;
        final TextView pCategory;
        final TextView pTitle;
        final TextView likeNo;
        final TextView commentNo;
        final TextView views;
        final ImageView pVine;
        final ImageView verified;
        final RelativeLayout like;
        final RelativeLayout comment;

        final RelativeLayout view_ly;
        final RelativeLayout video_share;
        final ConstraintLayout constraintLayout;

        final ImageView pause;
        final ProgressBar load;
        final ConstraintLayout constraintLayout10;
        final ConstraintLayout viewlt;


        public MyHolder(@NonNull View itemView) {
            super(itemView);
            pDp = itemView.findViewById(R.id.circleImageView);
            eye = itemView.findViewById(R.id.eye);
            pMeme = itemView.findViewById(R.id.meme);
            pName = itemView.findViewById(R.id.name);
            verified = itemView.findViewById(R.id.verified);
            time = itemView.findViewById(R.id.time);
            likeNo = itemView.findViewById(R.id.likeNo);
            commentNo = itemView.findViewById(R.id.commentNo);
            load = itemView.findViewById(R.id.load);
            views = itemView.findViewById(R.id.views);
            view_ly = itemView.findViewById(R.id.view_ly);
            more = itemView.findViewById(R.id.more);
            pVine = itemView.findViewById(R.id.videoView);
            pause = itemView.findViewById(R.id.exomedia_controls_play_pause_btn);
            like_img = itemView.findViewById(R.id.like_img);
            pCategory = itemView.findViewById(R.id.category);
            pTitle = itemView.findViewById(R.id.title);
            viewlt = itemView.findViewById(R.id.viewlt);
            like = itemView.findViewById(R.id.relativeLayout);
            comment = itemView.findViewById(R.id.relativeLayout6);

            video_share = itemView.findViewById(R.id.vine_share);
            constraintLayout10 = itemView.findViewById(R.id.constraintLayout10);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);

        }
    }

}



