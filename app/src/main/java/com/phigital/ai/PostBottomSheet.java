package com.phigital.ai;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.phigital.ai.Activity.ShareGroupActivity;
import com.phigital.ai.Auth.RegisterActivity;
import com.phigital.ai.Post.PostComments;
import com.phigital.ai.Utility.PostFeelBy;
import com.phigital.ai.Utility.PostLikedBy;
import com.phigital.ai.Post.UpdatePost;
import com.phigital.ai.Activity.ShareActivity;
import com.phigital.ai.Utility.MediaView;
import com.phigital.ai.databinding.HomeBottomSheetBinding;


import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;


public class PostBottomSheet extends BottomSheetDialogFragment {

    HomeBottomSheetBinding binding;
    private String postId,image,video,id,text,type,rejoy;
    private String hid,pId,pViews,rViews,reTweet,reId,pTime,privacy,location,content,link;
    BottomSheetDialog sharebottom;
    String userId;
    byte[] byteArray;
    public PostBottomSheet() {

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = HomeBottomSheetBinding.inflate(getLayoutInflater());
        Bundle mArgs = getArguments();
        if (mArgs != null){
            id = mArgs.getString("id");
            postId = mArgs.getString("postId");
            byteArray = mArgs.getByteArray("byteArray");
        }
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("pId").equalTo(postId);
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    hid = ""+ds.child("id").getValue();
                    pId = ""+ds.child("pId").getValue();
                    text = ""+ds.child("text").getValue();
                    pViews = ""+ds.child("pViews").getValue();
                    rViews = ""+ds.child("rViews").getValue();
                    type = ""+ds.child("type").getValue();
                    image = ""+ds.child("image").getValue();
                    video = ""+ds.child("video").getValue();
                    reTweet = ""+ds.child("reTweet").getValue();
                    reId = ""+ds.child("reId").getValue();
                    pTime = ""+ds.child("pTime").getValue();
                    privacy = ""+ds.child("privacy").getValue();
                    location = ""+ds.child("location").getValue();
                    content = ""+ds.child("content").getValue();
                    link = ""+ds.child("link").getValue();

                    if ("text".equals(type)) {
                        binding.fullscreen.setVisibility(View.GONE);
                        binding.download.setVisibility(View.GONE);
                    }else{
                        binding.fullscreen.setVisibility(View.VISIBLE);
                        binding.download.setVisibility(View.VISIBLE);
                    }

                    if (userId.equals(hid)){
                       binding.delete.setVisibility(View.VISIBLE);
                       binding.edit.setVisibility(View.VISIBLE);
                       binding.report.setVisibility(View.GONE);
                    }else{
                        binding.delete.setVisibility(View.GONE);
                        binding.edit.setVisibility(View.GONE);
                        binding.report.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        binding.refeel.setOnClickListener(v1 -> {
            Intent intent = new Intent(getActivity(), PostFeelBy.class);
            intent.putExtra("postId", postId);
            startActivity(intent);
        });

        binding.edit.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UpdatePost.class);
            intent.putExtra("key","editPost");
            intent.putExtra("editPostId", postId);
            startActivity(intent);
        });

        binding.likes.setOnClickListener(v1 -> {
            Intent intent = new Intent(getActivity(), PostLikedBy.class);
            intent.putExtra("postId", postId);
            startActivity(intent);
        });

        binding.comment.setOnClickListener(v2 -> {
            Intent intent = new Intent(getActivity(), PostComments.class);
            intent.putExtra("postId",postId);
            intent.putExtra("id", id);
            startActivity(intent);
        });

        binding.fullscreen.setOnClickListener(v3 -> {
            Intent intent = new Intent(getActivity(), MediaView.class);
            intent.putExtra("type","image");
            intent.putExtra("uri",image);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        binding.delete.setOnClickListener(v3 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Delete");
            builder.setMessage("Are you sure to delete this post?");
            builder.setPositiveButton("Delete", (dialog, which) -> {
                if (type.equals("image")){
                    StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(image);
                    picRef.delete().addOnSuccessListener(aVoid -> {
                        Query query2 = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(postId);
                        query2.addListenerForSingleValueEvent(new ValueEventListener() {
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
                    });
                    FirebaseDatabase.getInstance().getReference("Vault").child(userId).child(postId).removeValue();
                }else{
                    Query query3 = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(postId);
                    query3.addListenerForSingleValueEvent(new ValueEventListener() {
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
                    FirebaseDatabase.getInstance().getReference("Vault").child(userId).child(postId).removeValue();
                }
            }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });

        binding.download.setOnClickListener(v4 -> {
            StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(image);
            picRef.getDownloadUrl().addOnSuccessListener(uri -> {
                File dir = new File(Environment.getExternalStorageDirectory()+"/Download/Phigital/");
                dir.mkdirs();
                File localefile = null;
                try {
                    localefile = File.createTempFile("image",".jpg",dir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                picRef.getFile(localefile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(requireActivity(), "Download Success", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireActivity(), "Download Failed", Toast.LENGTH_SHORT).show();
                    }
                });
//                downloadFile(requireActivity(), "image", ".jpeg", DIRECTORY_DOWNLOADS, url);

            }).addOnFailureListener(e -> {

            });

        });

        binding.save.setOnClickListener(v5 -> {
            FirebaseDatabase.getInstance().getReference().child("Vault").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(postId)){
                            snapshot.child(postId).getRef().removeValue();
                            Toast.makeText(requireActivity(), "Post is removed from vault", Toast.LENGTH_SHORT).show();
                        }else {
                            FirebaseDatabase.getInstance().getReference().child("Vault").child(userId).child(postId).setValue(true);
                            Toast.makeText(requireActivity(), "Post is added to vault", Toast.LENGTH_SHORT).show();
                        }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        FirebaseDatabase.getInstance().getReference().child("Vault").child(userId).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(postId)){
                        binding.savetv.setText("Remove");
                    }else {
                        binding.savetv.setText("Save");
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.report.setOnClickListener(v6 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Report");
            builder.setMessage("Are you sure to report this post?");
            builder.setPositiveButton("Report",(dialog, which) -> {
                FirebaseDatabase.getInstance().getReference().child("Report").child(postId).setValue(true);
                Toast.makeText(getActivity(), "Post is reported", Toast.LENGTH_SHORT).show();
            }).setNegativeButton("Cancel",(dialog, which) -> {
                dialog.dismiss();
            });
            builder.create().show();
        });

        binding.share.setOnClickListener(v8 -> {
            BottomDialog();
            sharebottom.show();
        });
        BottomDialog();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle mArgs = getArguments();
        byte[] recStr= mArgs.getByteArray("byteArray");
        mArgs.clear();
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle oldInstanceState) {
        super.onSaveInstanceState(oldInstanceState);
        oldInstanceState.clear();
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void BottomDialog(){
        if (sharebottom == null){
            @SuppressLint("InflateParams") View view = LayoutInflater.from(getActivity()).inflate(R.layout.share_vid_bottom_sheet, null);
            ConstraintLayout appCL = view.findViewById(R.id.appCL);
            ConstraintLayout chatCL = view.findViewById(R.id.chatCL);
            ConstraintLayout groupCL = view.findViewById(R.id.groupCL);
            appCL.setOnClickListener(v -> {
                //setup intent:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                //setup image extra, if exists:
                if(type.equals("text" )){
                    sharingIntent.setType("text/plain");
                }else{
                    Bitmap picBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    if (picBitmap != null) {
                        String url = MediaStore.Images.Media.insertImage(requireActivity().getContentResolver(), picBitmap, "", "");
                        sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(url));
                        sharingIntent.setType("*/*");
                    } else {
                        //if no picture, just text set - this MIME
                        sharingIntent.setType("text/plain");
                    }
                }
                sharingIntent.putExtra(Intent.EXTRA_TEXT, text);

                if (sharingIntent.resolveActivity(requireActivity().getPackageManager()) == null) {
                    Toast.makeText(getActivity(), "Sharing failed please try again later", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(Intent.createChooser(sharingIntent,"Share"));
                }
                sharebottom.dismiss();
            });
            chatCL.setOnClickListener(v -> {
                Intent intent = new Intent(requireActivity(), ShareActivity.class);
                intent.putExtra("postId", postId);
                intent.putExtra("type",type);
                intent.putExtra("content", "post");
                requireActivity().startActivity(intent);
                sharebottom.dismiss();
            });
            groupCL.setOnClickListener(v -> {
                Intent intent = new Intent(requireActivity(), ShareGroupActivity.class);
                intent.putExtra("postId",  postId);
                intent.putExtra("type",type);
                intent.putExtra("content", "post");
                requireActivity().startActivity(intent);
                sharebottom.dismiss();
            });
            sharebottom = new BottomSheetDialog(requireActivity());
            sharebottom.setContentView(view);
        }
    }
}