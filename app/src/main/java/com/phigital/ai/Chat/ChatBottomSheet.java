package com.phigital.ai.Chat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
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
import com.phigital.ai.Activity.ShareActivity;
import com.phigital.ai.Activity.ShareGroupActivity;
import com.phigital.ai.Post.PostComments;
import com.phigital.ai.Post.UpdatePost;
import com.phigital.ai.R;
import com.phigital.ai.Utility.MediaView;
import com.phigital.ai.Utility.PostFeelBy;
import com.phigital.ai.Utility.PostLikedBy;
import com.phigital.ai.databinding.ActivityChatBottomSheetBinding;
import com.phigital.ai.databinding.HomeBottomSheetBinding;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChatBottomSheet extends BottomSheetDialogFragment {

    ActivityChatBottomSheetBinding binding;
    private String msg,sender,receiver,timestamp,type,chatId,id,content,mtype,mmsg;

    public ChatBottomSheet() {

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ActivityChatBottomSheetBinding.inflate(getLayoutInflater());
        Bundle mArgs = getArguments();
        if (mArgs != null){
            id = mArgs.getString("id");
            chatId = mArgs.getString("chatId");
            content = mArgs.getString("content");
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");
        Query query = ref.orderByChild("timestamp").equalTo(chatId);
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    msg = ""+ds.child("msg").getValue();
                    receiver = ""+ds.child("receiver").getValue();
                    sender = ""+ds.child("sender").getValue();
                    timestamp = ""+ds.child("timestamp").getValue();
                    type = ""+ds.child("type").getValue();;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Favourite").child((Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser())).getUid())
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(chatId).exists()) {
                    binding.star.setImageResource(R.drawable.star);
                    binding.text.setText("Unsave");
                } else {
                    binding.star.setImageResource(R.drawable.starregular);
                    binding.text.setText("Save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference().child("Favourite").child((Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser())).getUid());
                likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(chatId).exists()) {
                            likeRef.child(chatId).removeValue();
                            binding.star.setImageResource(R.drawable.starregular);
                            Toast.makeText(getActivity(), "Unsaved", Toast.LENGTH_SHORT).show();
                        } else {
                            likeRef.child(chatId).setValue("star");
                            binding. star.setImageResource(R.drawable.star);
                            Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        binding.forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShareActivity.class);
                intent.putExtra("postId",msg);
                intent.putExtra("type", type);
                intent.putExtra("content", "chat");
                startActivity(intent);
            }
        });

        binding.delete.setOnClickListener(v -> {
            if (type.equals("text") || type.equals("post")){
                Query query2 = FirebaseDatabase.getInstance().getReference().child("Chats").orderByChild("timestamp").equalTo(chatId);
                query2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()){
                            if (sender.equals((Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser())).getUid())){
                                ds.getRef().removeValue();
                                Toast.makeText(getActivity(),"message deleted...",Toast.LENGTH_SHORT).show();
                            }else{
                                Map<String, Object> hashMap = new HashMap<>();
                                hashMap.put("hide", true);
                                ds.getRef().updateChildren(hashMap);
                                Toast.makeText(getActivity(),"message deleted...",Toast.LENGTH_SHORT).show();
                            }
//                                Snackbar.make(holder.itemView,"Deleted", Snackbar.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else{
                FirebaseStorage.getInstance().getReferenceFromUrl(msg).delete().addOnCompleteListener(task -> {
                    Query query3 = FirebaseDatabase.getInstance().getReference().child("Chats").orderByChild("timestamp").equalTo(chatId);
                    query3.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()){
                                if (sender.equals((Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser())).getUid())){
                                    ds.getRef().removeValue();
                                    Toast.makeText(getActivity(),"message deleted...",Toast.LENGTH_SHORT).show();
                                }else{
                                    Map<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("hide", true);
                                    ds.getRef().updateChildren(hashMap);
                                    Toast.makeText(getActivity(),"message deleted...",Toast.LENGTH_SHORT).show();
                                }
//                                Snackbar.make(holder.itemView,"Deleted", Snackbar.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                });
            }
        });

        if (content.equals("save")){
            binding.delete.setVisibility(View.GONE);
        }

        binding.copy.setOnClickListener(v -> {
            Snackbar.make(binding.cbs,"Copied", Snackbar.LENGTH_LONG).show();
            ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("text", msg);
            clipboard.setPrimaryClip(clip);
        });

       return binding.getRoot();
    }

}