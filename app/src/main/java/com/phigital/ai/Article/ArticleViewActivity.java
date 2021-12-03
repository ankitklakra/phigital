package com.phigital.ai.Article;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.GetTimeAgo;
import com.phigital.ai.MainActivity;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.Notifications.Data2;
import com.phigital.ai.Notifications.Sender2;
import com.phigital.ai.Notifications.Token;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.Utility.MediaView;
import com.phigital.ai.Activity.ShareGroupActivity;
import com.phigital.ai.Utility.PostLikedBy;

import com.phigital.ai.Utility.UserProfile;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pedromassango.doubleclick.DoubleClick;
import com.pedromassango.doubleclick.DoubleClickListener;
import com.phigital.ai.databinding.ActivityArticleDetailsBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class ArticleViewActivity extends BaseActivity implements View.OnClickListener {

    Context context = ArticleViewActivity.this;
    SharedPref sharedPref;
    ActivityArticleDetailsBinding binding;
    BottomSheetDialog articlebottomsheet;
    private String userId,id;
    private RequestQueue requestQueue;
    private boolean notify = false;
    String hisId, hispId, articleId, hisType, hisImage, hisVideo, hisCategory, hisTime, hisText, hisTitle,hisViews;

    boolean mProcessCLike = false;

    ConstraintLayout likedbyCL,commentCL,shareCL,reportCL,editCL,deleteCL;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_article_details);
        binding.back.setOnClickListener(v -> onBackPressed());
        Intent intent = getIntent();
        articleId = intent.getStringExtra("articleId");
        id = intent.getStringExtra("id");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        binding.more.setOnClickListener(v -> articlebottomsheet.show());

        binding.nsv.setOnClickListener(new DoubleClick(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {

            }

            @Override
            public void onDoubleClick(View view) {
                likePost();
            }
        }));

        binding.likeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likePost();
            }
        });

        binding.meme.setOnClickListener(v -> {
            Intent intent1 = new Intent(ArticleViewActivity.this, MediaView.class);
            intent1.putExtra("type","image");
            intent1.putExtra("uri",hisImage);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
        });

        loadPostInfo();

        setLikes();

        createBottomSheetDialog();
    }

    private void createBottomSheetDialog(){
        if (articlebottomsheet == null){
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.article_bottom_sheet, null);
            likedbyCL = view.findViewById(R.id.likedbyCL);
            commentCL = view.findViewById(R.id.commentCL);
            shareCL = view.findViewById(R.id.shareCL);
            reportCL = view.findViewById(R.id.reportCL);
            editCL = view.findViewById(R.id.editCL);
            deleteCL = view.findViewById(R.id.deleteCL);

            likedbyCL.setOnClickListener(this);
            commentCL.setOnClickListener(this);
            shareCL.setOnClickListener(this);
            reportCL.setOnClickListener(this);
            editCL.setOnClickListener(this);
            deleteCL.setOnClickListener(this);

            articlebottomsheet = new BottomSheetDialog(this);
            articlebottomsheet.setContentView(view);
        }
    }

    private void deleteWithoutVine() {
        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(hisImage);
        picRef.delete().addOnSuccessListener(aVoid -> {
            Query query = FirebaseDatabase.getInstance().getReference("Article").orderByChild("pId").equalTo(articleId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        ds.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(@NonNull @NotNull Void unused) {
                                onBackPressed();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }).addOnFailureListener(e -> {
        });

    }

    private void setLikes() {
        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(articleId).hasChild(userId)){
                    binding.likeImg.setImageResource(R.drawable.icon_fav2);
                }else {
                    binding.likeImg.setImageResource(R.drawable.icon_fav);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void likePost() {
        mProcessCLike = true;
        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mProcessCLike) {
                    if (dataSnapshot.child(articleId).hasChild(userId)) {
                        likeRef.child(articleId).child(userId).removeValue();
                        mProcessCLike = false;
                    } else {
                        likeRef.child(articleId).child(userId).setValue("Liked");
                        mProcessCLike = false;
                        if(!id.equals(userId)){
                            addToHisNotification(id, "Liked on your Article", articleId,hisImage);
                            notify = true;
                            FirebaseDatabase.getInstance().getReference("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ModelUser user = snapshot.getValue(ModelUser.class);
                                    if (notify){
                                        sendNotification(id, Objects.requireNonNull(user).getName(), "Liked on your Article",articleId);
                                    }
                                    notify = false;
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addToHisNotification(String id, String message, String pId,String image){
        String timestamp = ""+System.currentTimeMillis();
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId", id);
        hashMap.put("timestamp", timestamp);
        hashMap.put("pUid", pId);
        hashMap.put("notification", message);
        hashMap.put("sImage", image);
        hashMap.put("sUid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        FirebaseDatabase.getInstance().getReference("Users").child(id).child("Notifications").child(timestamp).setValue(hashMap);
        FirebaseDatabase.getInstance().getReference("Users").child(id).child("Count").child(timestamp).setValue(true);

    }

    private void loadPostInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Article");
        Query query = ref.orderByChild("pId").equalTo(articleId);
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    hisId = ""+ds.child("id").getValue();
                    hispId = ""+ds.child("pId").getValue();
                    hisText = ""+ds.child("text").getValue();
                    hisViews = ""+ds.child("pViews").getValue();
                    hisType = ""+ds.child("type").getValue();
                    hisImage = ""+ds.child("image").getValue();
                    hisVideo = ""+ds.child("video").getValue();
                    hisCategory = ""+ds.child("category").getValue();
                    hisTitle = ""+ds.child("title").getValue();
                    hisTime = ""+ds.child("pTime").getValue();

                    long lastTime = Long.parseLong(hisTime);
                    String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime);
                    binding.time.setText(lastSeenTime);

                    FirebaseDatabase.getInstance().getReference().child("Users").child(hisId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String userId = snapshot.child("id").getValue().toString();
                            String name = Objects.requireNonNull(snapshot.child("name").getValue()).toString().trim();
                            String dp = Objects.requireNonNull(snapshot.child("photo").getValue()).toString().trim();
                            String username = Objects.requireNonNull(snapshot.child("username").getValue()).toString().trim();

                            String mVerified = snapshot.child("verified").getValue().toString();

                            if (mVerified.isEmpty()){
                                binding.verified.setVisibility(View.GONE);
                            }else {
                                binding.verified.setVisibility(View.VISIBLE);
                            }

                            binding.name.setText(name);

                            if (!dp.isEmpty()) {
                                Picasso.get().load(dp).placeholder(R.drawable.placeholder).into(binding.circleImageView);
                            }

                            //ClickToPro
                            binding.name.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (hisId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
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

                            binding.circleImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (hisId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
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

                    binding.title.setText(hisTitle);
                    binding.text.setText(hisText);
                    try {
                        Picasso.get().load(hisImage).placeholder(R.drawable.placeholder).into(binding.meme);
                    }catch (Exception ignored){

                    }

                    if (!hisId.equals(userId)) {
                        editCL.setVisibility(View.GONE);
                        deleteCL.setVisibility(View.GONE);
                    }else{
                        reportCL.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void shareImageAndText(String text, Bitmap bitmap) {
        Uri uri = saveImageToShare(bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
        intent.setType("image/*");
        startActivity(Intent.createChooser(intent, "Share Via"));
    }

    private Uri saveImageToShare(Bitmap bitmap) {
        File imageFolder = new File(this.getCacheDir(), "images");
        Uri uri = null;
        try {
            imageFolder.mkdir();
            File file = new File(imageFolder, "shared_image.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(this, "com.jarvis.goodjoy.fileprovider", file);

        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return uri;
    }

    private void shareTextOnly(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/*");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(intent, "Share Via"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.appshare:
                String shareText = binding.text.getText().toString().trim();
                BitmapDrawable bitmapDrawable = (BitmapDrawable) binding.meme.getDrawable();
                if (bitmapDrawable == null) {
                    shareTextOnly(shareText);
                } else {
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    shareImageAndText(shareText, bitmap);
                }
                break;
//            case R.id.appvid:
//                String shareBody = hisText;
//                String shareUrl = hisVideo;
//                Intent intent2 = new Intent(Intent.ACTION_SEND);
//                intent2.setType("text/*");
//                intent2.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
//                intent2.putExtra(Intent.EXTRA_TEXT,shareBody+" Link: "+shareUrl);
//                startActivity(Intent.createChooser(intent2, "Share Via"));
//                break;
            case R.id.groupShare:
                Intent intent4 = new Intent(ArticleViewActivity.this, ShareGroupActivity.class);
                intent4.putExtra("articleId", articleId);
                startActivity(intent4);
                break;
            case R.id.likedbyCL:
                articlebottomsheet.dismiss();
                Intent intent1 = new Intent(ArticleViewActivity.this, PostLikedBy.class);
                intent1.putExtra("postId", articleId);
                startActivity(intent1);
                break;
            case R.id.commentCL:
                articlebottomsheet.dismiss();
                Intent intent3 = new Intent(context, ArticleCommentsActivity.class);
                intent3.putExtra("articleId", articleId);
                intent3.putExtra("image", hisImage);
                intent3.putExtra("id", hisId);
                context.startActivity(intent3);
                break;
            case R.id.shareCL:
                articlebottomsheet.dismiss();
                sendShareToWhatsAppIntent();
                break;
            case R.id.reportCL:
                articlebottomsheet.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Report");
                builder.setMessage("Are you sure to report this Article?");
                builder.setPositiveButton("Report",(dialog, which) -> {
                    FirebaseDatabase.getInstance().getReference().child("ArticleReport").child(articleId).setValue(true);
                    Toast.makeText(context, "Article Reported...", Toast.LENGTH_SHORT).show();
                }).setNegativeButton("Cancel",(dialog, which) -> {
                    dialog.dismiss();
                });
                builder.create().show();
                break;
            case R.id.editCL:
                articlebottomsheet.dismiss();
                Intent intent5 = new Intent(ArticleViewActivity.this, ArticleUpdateActivity.class);
                intent5.putExtra("key","editPost");
                intent5.putExtra("editPostId", articleId);
                intent5.putExtra("meme", hisImage);
                startActivity(intent5);
                break;
            case R.id.deleteCL:
                articlebottomsheet.dismiss();
                deleteWithoutVine();
                break;
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    public void sendShareToWhatsAppIntent() {
        //setup intent:
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        //setup image extra, if exists:
        BitmapDrawable bitmapDrawable = (BitmapDrawable) binding.meme.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
            if (bitmap != null) {
                String url = MediaStore.Images.Media.insertImage(ArticleViewActivity.this.getContentResolver(), bitmap, "", "");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(url));
                sharingIntent.setType("*/*");
            } else {
                //if no picture, just text set - this MIME
                sharingIntent.setType("text/plain");
            }


        sharingIntent.putExtra(Intent.EXTRA_TEXT, hisText);

        if (sharingIntent.resolveActivity(ArticleViewActivity.this.getPackageManager()) == null) {
            Toast.makeText(ArticleViewActivity.this, "Sharing failed please try again later", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(Intent.createChooser(sharingIntent,"Share"));
        }

    }

    private void sendNotification(final String hisId, final String name,final String message,final String postId){
        requestQueue = Volley.newRequestQueue(context);
        DatabaseReference allToken = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allToken.orderByKey().equalTo(hisId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data2 data = new Data2(FirebaseAuth.getInstance().getCurrentUser().getUid(), name + " : " + message, "New Notification", hisId, R.drawable.logo,postId);
                    assert token != null;
                    Sender2 sender = new Sender2(data, token.getToken());
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", jsonObject, response -> Log.d("JSON_RESPONSE", "onResponse" + response.toString()), error -> Log.d("JSON_RESPONSE", "onResponse" + error.toString())){
                            @Override
                            public Map<String, String> getHeaders() {
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