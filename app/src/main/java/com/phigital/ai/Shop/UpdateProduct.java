package com.phigital.ai.Shop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.phigital.ai.Adapter.HorizontalRecyclerView;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.databinding.ActivityUpdateProductBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;

@SuppressWarnings("ALL")
public class UpdateProduct extends AppCompatActivity {
    private int uploads = 0;
    SharedPref sharedPref;
    private static final int PICK_VIDEO_REQUEST = 1;
    ArrayList<String> sizelist = new ArrayList<>();
    String type = "none";
    HorizontalRecyclerView adapters;
    Map<String,Object> td;

    ArrayList<String> urlStrings = new ArrayList<>();
    ArrayList<String> imageslist = new ArrayList<>();
    ArrayList<Uri> list = new ArrayList<>();

//    ImageView meme,cancel;
//    VideoView vines;
//    ConstraintLayout add_meme,add_vines,update_remove,remove;
//    Button update_it, update_vine;
//    EditText text;
//    TextView mName,type;
//    CircleImageView circleImageView3;
//    ProgressBar pd;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    DatabaseReference productReference;

    String name, dp, id,username;
    String editText, editMeme, editVine;

    String editcategory,editproductname,editbrandname,editmrp,editdiscount,editprice,editsaveamount,editaddress,editdetail,editpTime,hisavailabilty;
    ActivityUpdateProductBinding binding;

    String editPostId;

    private Uri image_uri, video_uri;
//    MediaController mediaController;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_update_product);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_product);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,new IntentFilter(
                "remove"
        ));
//        meme = findViewById(R.id.meme);
//        add_meme = findViewById(R.id.constraintLayout3);
//        update_remove = findViewById(R.id.update_remove);
//        remove = findViewById(R.id.remove);
//        text = findViewById(R.id.post_text);
//        update_it = findViewById(R.id.post_it);
//        update_vine = findViewById(R.id.post_vine);
//        type = findViewById(R.id.username);
//        mName = findViewById(R.id.name);
//        circleImageView3 = findViewById(R.id.circleImageView3);
//        pd = findViewById(R.id.pb);
//        cancel = findViewById(R.id.imageView);
        binding.back.setOnClickListener(v -> onBackPressed());
//        add_vines = findViewById(R.id.vines_lt);
//        vines = findViewById(R.id.vines);
//        mediaController = new MediaController(this);
//        vines.setMediaController(mediaController);
//        mediaController.setAnchorView(vines);
//        MediaController ctrl = new MediaController(UpdatePost.this);
//        ctrl.setVisibility(View.GONE);
//        vines.setMediaController(ctrl);
//        vines.start();
//        vines.setOnPreparedListener(mp -> mp.setLooping(true));

        firebaseAuth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                dp = Objects.requireNonNull(dataSnapshot.child("photo").getValue()).toString();
                id = Objects.requireNonNull(dataSnapshot.child("id").getValue()).toString();
                username = Objects.requireNonNull(dataSnapshot.child("username").getValue()).toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        id = Objects.requireNonNull(user).getUid();

//        String[] items = new String[]{
//                "T-shirt",
//                "Mobiles",
//                "Wooden Items"
//
//        };
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                UpdateProduct.this,
//                R.layout.dropdown_menu,
//                items
//        );
//
//        binding.category.setAdapter(adapter);

        Intent intent = getIntent();
        String isUpdateKey = ""+intent.getStringExtra("key");
        editPostId = ""+intent.getStringExtra("editPostId");

        productReference = FirebaseDatabase.getInstance().getReference().child("Product").child(editPostId);
        loadPostData(editPostId);

         binding.a1.setOnClickListener(v -> {
            Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);
            intent2.setType("image/*");
            intent2.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(intent2, "Pictures: "), 1);
            list.clear();
        });

        binding.done.setOnClickListener(v -> {
            if (binding.category.getText().toString().equals("apparel")) {
                String category = binding.category.getText().toString().toLowerCase();
                String product = Objects.requireNonNull(binding.product.getText()).toString().toLowerCase();
                String brandname = Objects.requireNonNull(binding.brandname.getText()).toString().toLowerCase();
                String mrp = Objects.requireNonNull(binding.mrp.getText()).toString().toLowerCase();
                String discount = Objects.requireNonNull(binding.discount.getText()).toString().toLowerCase();
                String price = Objects.requireNonNull(binding.price.getText()).toString().toLowerCase();
                String save = Objects.requireNonNull(binding.save.getText()).toString().toLowerCase();
                String address = Objects.requireNonNull(binding.address.getText()).toString().toLowerCase();
                String detail = Objects.requireNonNull(binding.detail.getText()).toString().toLowerCase();

                List<String> sizeList = new ArrayList<String>();
                if (binding.xs.isChecked()) {
                    sizeList.add("xs");
                }
                if (binding.s.isChecked()) {
                    sizeList.add("s");
                }
                if (binding.m.isChecked()) {
                    sizeList.add("m");
                }
                if (binding.l.isChecked()) {
                    sizeList.add("l");
                }
                if (binding.xl.isChecked()) {
                    sizeList.add("xl");
                }
                if (binding.xxl.isChecked()) {
                    sizeList.add("xxl");
                }
                if (binding.xxxl.isChecked()) {
                    sizeList.add("xxxl");
                }

                binding.pb.setVisibility(View.VISIBLE);

                if (type.equals("updated")){
                    if (!category.isEmpty() && !product.isEmpty() && !brandname.isEmpty() && !mrp.isEmpty() && !discount.isEmpty() && !price.isEmpty() && !save.isEmpty() && !address.isEmpty() && !detail.isEmpty() && list != null ) {
                        String timeStamp = String.valueOf(System.currentTimeMillis());
                        final StorageReference ImageFolder =  FirebaseStorage.getInstance().getReference().child("ProductImage");
                        for (uploads=0; uploads < list.size(); uploads++) {
                            Uri IndividualImage = list.get(uploads);
                            final StorageReference ImageName = ImageFolder.child("Images" + IndividualImage.getLastPathSegment());
                            ImageName.putFile(IndividualImage).addOnSuccessListener(
                                    taskSnapshot -> ImageName.getDownloadUrl().addOnSuccessListener(
                                            uri -> {
                                                urlStrings.add(String.valueOf(uri));
                                                if (urlStrings.size() == list.size()){
                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                    HashMap<String, String> hashMap2 = new HashMap<>();
                                                    HashMap<String, String> hashMap3 = new HashMap<>();
                                                    for (int i = 0; i <urlStrings.size() ; i++) {
                                                        hashMap2.put("ImgLink"+i, urlStrings.get(i));
                                                    }
                                                    for (int i = 0; i <sizeList.size() ; i++) {
                                                        hashMap3.put("I"+i, sizeList.get(i));
                                                    }
                                                    hashMap.put("category", category);
                                                    hashMap.put("productname", product);
                                                    hashMap.put("brandname", brandname);
                                                    hashMap.put("mrp", mrp);
                                                    hashMap.put("available", "true");
                                                    hashMap.put("discount", discount);
                                                    hashMap.put("price", price);
                                                    hashMap.put("saveamount", save);
                                                    hashMap.put("address", address);
                                                    hashMap.put("detail", detail);
                                                    hashMap.put("pTime", timeStamp);
                                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Product");
                                                    databaseReference.child(editPostId).updateChildren(hashMap).addOnCompleteListener(
                                                            task -> databaseReference.child(editPostId).child("images").setValue(hashMap2))
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    databaseReference.child(editPostId).child("sizes").setValue(hashMap3);
                                                                }
                                                            }).addOnSuccessListener(aVoid -> {
                                                        binding.pb.setVisibility(View.GONE);
                                                        new StyleableToast
                                                                .Builder(getApplicationContext())
                                                                .text("Item Updated Successfully")
                                                                .textColor(Color.WHITE)
                                                                .textBold()
                                                                .gravity(0)
                                                                .length(2000)
                                                                .solidBackground()
                                                                .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                                                .show();
                                                    });
                                                }
                                            }
                                    ));
                        }
                    }else {
                        Toast.makeText(UpdateProduct.this, "Fill All details", Toast.LENGTH_SHORT).show();
                        binding.pb.setVisibility(View.GONE);
                    }
                }else{
                    if (!category.isEmpty() && !product.isEmpty() && !brandname.isEmpty() && !mrp.isEmpty() && !discount.isEmpty() && !price.isEmpty() && !save.isEmpty() && !address.isEmpty() && !detail.isEmpty() && list != null ) {
                        String timeStamp = String.valueOf(System.currentTimeMillis());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        HashMap<String, String> hashMap3 = new HashMap<>();
                        for (int i = 0; i <sizeList.size() ; i++) {
                            hashMap3.put("I"+i, sizeList.get(i));
                        }
                        hashMap.put("category", category);
                        hashMap.put("productname", product);
                        hashMap.put("brandname", brandname);
                        hashMap.put("mrp", mrp);
                        hashMap.put("available", "true");
                        hashMap.put("discount", discount);
                        hashMap.put("price", price);
                        hashMap.put("saveamount", save);
                        hashMap.put("address", address);
                        hashMap.put("detail", detail);
                        hashMap.put("pTime", timeStamp);
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Product");
                        databaseReference.child(editPostId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                databaseReference.child(editPostId).child("sizes").setValue(hashMap3);
                            }
                        }).addOnSuccessListener(aVoid -> {
                            binding.pb.setVisibility(View.GONE);
                            new StyleableToast
                                    .Builder(getApplicationContext())
                                    .text("Item Updated Successfully")
                                    .textColor(Color.WHITE)
                                    .textBold()
                                    .gravity(0)
                                    .length(2000)
                                    .solidBackground()
                                    .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                    .show();
                        });
                    }else {
                        Toast.makeText(UpdateProduct.this, "Fill All details", Toast.LENGTH_SHORT).show();
                        binding.pb.setVisibility(View.GONE);
                    }
                }
            }else{
                String category = binding.category.getText().toString().toLowerCase();
                String product = Objects.requireNonNull(binding.product.getText()).toString().toLowerCase();
                String brandname = Objects.requireNonNull(binding.brandname.getText()).toString().toLowerCase();
                String mrp = Objects.requireNonNull(binding.mrp.getText()).toString().toLowerCase();
                String discount = Objects.requireNonNull(binding.discount.getText()).toString().toLowerCase();
                String price = Objects.requireNonNull(binding.price.getText()).toString().toLowerCase();
                String save = Objects.requireNonNull(binding.save.getText()).toString().toLowerCase();
                String address = Objects.requireNonNull(binding.address.getText()).toString().toLowerCase();
                String detail = Objects.requireNonNull(binding.detail.getText()).toString().toLowerCase();

                binding.pb.setVisibility(View.VISIBLE);

                if (type.equals("updated")){
                    if (!category.isEmpty() && !product.isEmpty() && !brandname.isEmpty() && !mrp.isEmpty() && !discount.isEmpty() && !price.isEmpty() && !save.isEmpty() && !address.isEmpty() && !detail.isEmpty() && list != null ) {
                        String timeStamp = String.valueOf(System.currentTimeMillis());
                        final StorageReference ImageFolder =  FirebaseStorage.getInstance().getReference().child("ProductImage");
                        for (uploads=0; uploads < list.size(); uploads++) {
                            Uri IndividualImage = list.get(uploads);
                            final StorageReference ImageName = ImageFolder.child("Images" + IndividualImage.getLastPathSegment());
                            ImageName.putFile(IndividualImage).addOnSuccessListener(
                                    taskSnapshot -> ImageName.getDownloadUrl().addOnSuccessListener(
                                            uri -> {
                                                urlStrings.add(String.valueOf(uri));
                                                if (urlStrings.size() == list.size()){
                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                    HashMap<String, String> hashMap2 = new HashMap<>();
                                                    for (int i = 0; i <urlStrings.size() ; i++) {
                                                        hashMap2.put("ImgLink"+i, urlStrings.get(i));
                                                    }
                                                    hashMap.put("category", category);
                                                    hashMap.put("productname", product);
                                                    hashMap.put("brandname", brandname);
                                                    hashMap.put("mrp", mrp);
                                                    hashMap.put("available", "true");
                                                    hashMap.put("discount", discount);
                                                    hashMap.put("price", price);
                                                    hashMap.put("saveamount", save);
                                                    hashMap.put("address", address);
                                                    hashMap.put("detail", detail);
                                                    hashMap.put("pTime", timeStamp);
                                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Product");
                                                    databaseReference.child(editPostId).updateChildren(hashMap).addOnCompleteListener(
                                                            task -> databaseReference.child(editPostId).child("images").setValue(hashMap2)).addOnSuccessListener(aVoid -> {
                                                        binding.pb.setVisibility(View.GONE);
                                                        new StyleableToast
                                                                .Builder(getApplicationContext())
                                                                .text("Item Updated Successfully")
                                                                .textColor(Color.WHITE)
                                                                .textBold()
                                                                .gravity(0)
                                                                .length(2000)
                                                                .solidBackground()
                                                                .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                                                .show();
                                                    });
                                                }
                                            }
                                    ));
                        }
                    }else {
                        Toast.makeText(UpdateProduct.this, "Fill All details", Toast.LENGTH_SHORT).show();
                        binding.pb.setVisibility(View.GONE);
                    }
                }else{
                    if (!category.isEmpty() && !product.isEmpty() && !brandname.isEmpty() && !mrp.isEmpty() && !discount.isEmpty() && !price.isEmpty() && !save.isEmpty() && !address.isEmpty() && !detail.isEmpty() && list != null ) {
                        String timeStamp = String.valueOf(System.currentTimeMillis());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("category", category);
                        hashMap.put("productname", product);
                        hashMap.put("brandname", brandname);
                        hashMap.put("mrp", mrp);
                        hashMap.put("available", "true");
                        hashMap.put("discount", discount);
                        hashMap.put("price", price);
                        hashMap.put("saveamount", save);
                        hashMap.put("address", address);
                        hashMap.put("detail", detail);
                        hashMap.put("pTime", timeStamp);
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Product");
                        databaseReference.child(editPostId).updateChildren(hashMap).addOnSuccessListener(aVoid -> {
                            binding.pb.setVisibility(View.GONE);
                            new StyleableToast
                                    .Builder(getApplicationContext())
                                    .text("Item Updated Successfully")
                                    .textColor(Color.WHITE)
                                    .textBold()
                                    .gravity(0)
                                    .length(2000)
                                    .solidBackground()
                                    .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                    .show();
                        });
                    }else {
                        Toast.makeText(UpdateProduct.this, "Fill All details", Toast.LENGTH_SHORT).show();
                        binding.pb.setVisibility(View.GONE);
                    }
                }
            }
        });

//        if (isUpdateKey.equals("editPost")){
//
//
//        }

//        remove.setOnClickListener(v -> {
//
//            meme.setImageURI(null);
//            image_uri = null;
//            update_it.setVisibility(View.VISIBLE);
//            vines.setVisibility(View.GONE);
//            update_vine.setVisibility(View.GONE);
//            remove.setVisibility(View.GONE);
//            type.setText("Text");
//        });

//        update_vine.setOnClickListener(v -> {
//            String mText = text.getText().toString().trim();
//            if (TextUtils.isEmpty(mText)) {
//                Alerter.create(UpdatePost.this)
//                        .setTitle("Error")
//                        .setIcon(R.drawable.ic_error)
//                        .setBackgroundColorRes(R.color.colorPrimary)
//                        .setDuration(10000)
//                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
//                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
//                        .enableSwipeToDismiss()
//                        .setText("Enter caption")
//                        .show();
//            }else   if (!editVine.equals("noVideo")){
//                updateWithVine(mText, String.valueOf(video_uri));
//                pd.setVisibility(View.VISIBLE);
//            }else if (vines.getDrawableState() != null){
//                updateNowVine(mText, String.valueOf(video_uri));
//                pd.setVisibility(View.VISIBLE);
//            }
//
//        });
//        add_vines.setOnClickListener(v -> {
//            //Check Permission
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
//                        == PackageManager.PERMISSION_DENIED){
//                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
//                    requestPermissions(permissions, PERMISSION_CODE);
//                }
//                else {
//                    chooseVideo();
//                }
//            }
//            else {
//                chooseVideo();
//            }
//
//        });
//        add_meme.setOnClickListener(v -> {
//
//            type.setText("Image");
//            //Check Permission
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
//                        == PackageManager.PERMISSION_DENIED){
//                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
//                    requestPermissions(permissions, PERMISSION_CODE);
//                }
//                else {
//                    pickImageFromGallery();
//                }
//            }
//            else {
//                pickImageFromGallery();
//            }
//        });
//        update_it.setOnClickListener(v -> {
//            String mText = text.getText().toString().trim();
//
//            if (TextUtils.isEmpty(mText)) {
//                Alerter.create(UpdatePost.this)
//                        .setTitle("Error")
//                        .setIcon(R.drawable.ic_error)
//                        .setBackgroundColorRes(R.color.colorPrimary)
//                        .setDuration(10000)
//                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
//                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
//                        .enableSwipeToDismiss()
//                        .setText("Enter caption")
//                        .show();
//                return;
//            }
//            if (!editMeme.equals("noImage")){
//                updateWithMemeData(mText, String.valueOf(image_uri));
//                pd.setVisibility(View.VISIBLE);
//            }else if (meme.getDrawable() != null){
//                updateNowMemeData(mText, String.valueOf(image_uri));
//            }
//            else {
//                pd.setVisibility(View.VISIBLE);
//                updateData(mText);
//                pd.setVisibility(View.VISIBLE);
//            }
//
//        });

//        update_remove.setOnClickListener(v -> {
//            if (!editMeme.equals("noImage")){
//                deleteWithoutVine(editPostId, editMeme);
//                pd.setVisibility(View.VISIBLE);
//            }else if (!editVine.equals("noVideo")){
//                deleteWithoutMeme(editPostId, editVine);
//                pd.setVisibility(View.VISIBLE);
//            }
//        });


        binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.con.setVisibility(View.GONE);
                binding.image.setImageURI(null);
            }
        });

        binding.b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.clear();
                if (list.isEmpty()){
                    adapters.notifyDataSetChanged();
                    binding.b1.setVisibility(View.GONE);
                }
            }
        });

        binding.price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String mrp = Objects.requireNonNull(binding.mrp.getText()).toString();
                String price = Objects.requireNonNull(binding.price.getText()).toString();

                if (!mrp.isEmpty() && !price.isEmpty()){
                    int a = Integer.parseInt(mrp);
                    int b = Integer.parseInt(price);

                    int save = a - b;
                    binding.save.setText(String.valueOf(save));
                    int discount = save * 100 / a;
                    binding.discount.setText(String.valueOf(discount));
                }else{

                }

            }
        });

        binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProduct.this);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure to delete this Item?");
                builder.setPositiveButton("Delete", (dialog, which) -> beginDelete(editPostId))
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                builder.create().show();
            }
        });
    }

    private void beginDelete(String editPostId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = ref.child("Cart").orderByChild("pId").equalTo(editPostId);
        applesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        for (int i = 0; i <list.size() ; i++) {
            FirebaseStorage.getInstance().getReferenceFromUrl(String.valueOf(list.get(i))).delete();
        }
        new Handler().postDelayed(() -> {

            Query query = FirebaseDatabase.getInstance().getReference("Product").orderByChild("pId").equalTo(editPostId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        ds.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                onBackPressed();
                                Toast.makeText(UpdateProduct.this, "Deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }, 2000);

    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String id = intent.getStringExtra("removephoto");
            if (!id.isEmpty()){
                if (list.size() > 2){
                    int a = Integer.parseInt(id);
                    list.remove(a);
                    adapters.notifyItemRemoved(a);
                }
            }
        }
    };

    private void deleteWithoutMeme(String editPostId, String editVine) {

        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(editVine);
        picRef.delete().addOnSuccessListener(aVoid -> {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
            Query query = ref.orderByChild("pId").equalTo(editPostId);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        String child = ds.getKey();
                        dataSnapshot.getRef().child(Objects.requireNonNull(child)).child("vine").setValue("noVideo");
//                        vines.setVisibility(View.GONE);
//                        pd.setVisibility(View.GONE);
//                        update_remove.setVisibility(View.GONE);
//                        update_it.setVisibility(View.VISIBLE);
//                        update_vine.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }).addOnFailureListener(e -> {

        });
    }

    private void deleteWithoutVine(String editPostId, String editMeme) {

        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(editMeme);
        picRef.delete().addOnSuccessListener(aVoid -> {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
            Query query = ref.orderByChild("pId").equalTo(editPostId);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        String child = ds.getKey();
                        dataSnapshot.getRef().child(Objects.requireNonNull(child)).child("meme").setValue("noImage");
//                        meme.setImageURI(null);
//                        image_uri = null;
//                        meme.setVisibility(View.GONE);
//                        pd.setVisibility(View.GONE);
//                        update_remove.setVisibility(View.GONE);
//                        update_it.setVisibility(View.VISIBLE);
//                        update_vine.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    pd.setVisibility(View.GONE);
//                    Alerter.create(UpdatePost.this)
//                            .setTitle("Error")
//                            .setIcon(R.drawable.ic_check_wt)
//                            .setBackgroundColorRes(R.color.colorPrimaryDark)
//                            .setDuration(10000)
//                            .enableSwipeToDismiss()
//                            .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
//                            .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
//                            .setText(databaseError.getMessage())
//                            .show();
                }
            });

        }).addOnFailureListener(e -> {
//            pd.setVisibility(View.GONE);
//            Alerter.create(UpdatePost.this)
//                    .setTitle("Error")
//                    .setIcon(R.drawable.ic_check_wt)
//                    .setBackgroundColorRes(R.color.colorPrimaryDark)
//                    .setDuration(10000)
//                    .enableSwipeToDismiss()
//                    .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
//                    .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
//                    .setText(e.getMessage())
//                    .show();
        });

    }

    private void updateNowVine(String mText, String uri) {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Post/" + "Post_" + timeStamp;
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putFile(Uri.parse(uri)).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful());
            String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
            if (uriTask.isSuccessful()){
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", id);
                hashMap.put("name", name);
                hashMap.put("dp", dp);
                hashMap.put("text", mText);
                hashMap.put("type", "Video");
                hashMap.put("meme", "noImage");
                hashMap.put("vine", downloadUri);
                DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                dRef.child(editPostId).updateChildren(hashMap)
                        .addOnSuccessListener(aVoid -> {

//                            Alerter.create(UpdatePost.this)
//                                    .setTitle("Successful")
//                                    .setIcon(R.drawable.ic_check_wt)
//                                    .setBackgroundColorRes(R.color.colorPrimaryDark)
//                                    .setDuration(10000)
//                                    .enableSwipeToDismiss()
//                                    .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
//                                    .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
//                                    .setText("Post Updated")
//                                    .show();
//
//                            new Handler().postDelayed(() -> {
//                                Intent intent = new Intent(getApplicationContext(), Post.class);
//                                startActivity(intent);
//                                finish();
//
//
//                            }, 2000);
//
//                            text.setText("");
//                            vines.setVideoURI(null);
//                            video_uri = null;
//                            vines.setVisibility(View.GONE);
//                            update_vine.setVisibility(View.GONE);
//                            update_it.setVisibility(View.VISIBLE);
//                            type.setText("Text");
//                            pd.setVisibility(View.GONE);
//                            update_remove.setVisibility(View.GONE);
                        })
                        .addOnFailureListener(e -> {}
//                                Alerter.create(UpdatePost.this)
//                                .setTitle("Error")
//                                .setIcon(R.drawable.ic_check_wt)
//                                .setBackgroundColorRes(R.color.colorPrimaryDark)
//                                .setDuration(10000)
//                                .enableSwipeToDismiss()
//                                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
//                                .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
//                                .setText(e.getMessage())
//                                .show()
                                );
            }
        }).addOnFailureListener(e -> {
//            pd.setVisibility(View.GONE);
//            Alerter.create(UpdatePost.this)
//                    .setTitle("Error")
//                    .setIcon(R.drawable.ic_check_wt)
//                    .setBackgroundColorRes(R.color.colorPrimaryDark)
//                    .setDuration(10000)
//                    .enableSwipeToDismiss()
//                    .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
//                    .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
//                    .setText(e.getMessage())
//                    .show();
        });

    }

    private void updateWithVine(String mText, String uri) {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Post/" + "Post_" + timeStamp;
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putFile(Uri.parse(uri)).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful());
            String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
            if (uriTask.isSuccessful()){
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", id);
                hashMap.put("name", name);
                hashMap.put("dp", dp);
                hashMap.put("text", mText);
                hashMap.put("type", "Video");
                hashMap.put("meme", "noImage");
                hashMap.put("vine", downloadUri);
                DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                dRef.child(editPostId).updateChildren(hashMap).addOnSuccessListener(aVoid -> {
//                    Alerter.create(UpdatePost.this)
//                            .setTitle("Successful")
//                            .setIcon(R.drawable.ic_check_wt)
//                            .setBackgroundColorRes(R.color.colorPrimaryDark)
//                            .setDuration(10000)
//                            .enableSwipeToDismiss()
//                            .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
//                            .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
//                            .setText("Post Updated")
//                            .show();
//
//                    new Handler().postDelayed(() -> {
//                        Intent intent = new Intent(getApplicationContext(), Post.class);
//                        startActivity(intent);
//                        finish();
//
//
//                    }, 2000);
//                    text.setText("");
//                    update_remove.setVisibility(View.GONE);
//                    vines.setVideoURI(null);
//                    video_uri = null;
//                    vines.setVisibility(View.GONE);
//                    update_vine.setVisibility(View.GONE);
//                    update_it.setVisibility(View.VISIBLE);
//                    type.setText("Text");
//                    pd.setVisibility(View.GONE);
//                    update_remove.setVisibility(View.GONE);

                }).addOnFailureListener(e -> {
//                    pd.setVisibility(View.GONE);
//                    Alerter.create(UpdatePost.this)
//                            .setTitle("Error")
//                            .setIcon(R.drawable.ic_check_wt)
//                            .setBackgroundColorRes(R.color.colorPrimaryDark)
//                            .setDuration(10000)
//                            .enableSwipeToDismiss()
//                            .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
//                            .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
//                            .setText(e.getMessage())
//                            .show();
                });
            }
        }).addOnFailureListener(e -> {
//            pd.setVisibility(View.GONE);
//            Alerter.create(UpdatePost.this)
//                    .setTitle("Error")
//                    .setIcon(R.drawable.ic_check_wt)
//                    .setBackgroundColorRes(R.color.colorPrimaryDark)
//                    .setDuration(10000)
//                    .enableSwipeToDismiss()
//                    .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
//                    .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
//                    .setText(e.getMessage())
//                    .show();
        });




    }

    private void updateNowMemeData(String mText, String uri) {

        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Post/" + "Post_" + timeStamp;
        if (!uri.equals("noImage")) {
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putFile(Uri.parse(uri))
                    .addOnSuccessListener(taskSnapshot -> {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
                        if (uriTask.isSuccessful()) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", id);
                            hashMap.put("name", name);
                            hashMap.put("dp", dp);
                            hashMap.put("text", mText);
                            hashMap.put("type", "Image");
                            hashMap.put("meme", downloadUri);
                            hashMap.put("vine", "noVideo");
                            DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                            dRef.child(editPostId).updateChildren(hashMap)
                                    .addOnSuccessListener(aVoid -> {
//                                        text.setText("");
//                                        meme.setImageURI(null);
//                                        image_uri = null;
//                                        type.setText("Text");
//                                        pd.setVisibility(View.GONE);
//                                        update_it.setVisibility(View.VISIBLE);
//                                        update_vine.setVisibility(View.GONE);
//                                        update_remove.setVisibility(View.GONE);
//
//                                        new Handler().postDelayed(() -> {
//                                            Intent intent = new Intent(getApplicationContext(), Post.class);
//                                            startActivity(intent);
//                                            finish();
//
//
//                                        }, 2000);
//
//                                        Alerter.create(UpdatePost.this)
//                                                .setTitle("Successful")
//                                                .setIcon(R.drawable.ic_check_wt)
//                                                .setBackgroundColorRes(R.color.colorPrimaryDark)
//                                                .setDuration(10000)
//                                                .enableSwipeToDismiss()
//                                                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
//                                                .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
//                                                .setText("Post Updated")
//                                                .show();
                                    })
                                    .addOnFailureListener(e -> {
//                                        pd.setVisibility(View.GONE);
//                                        Alerter.create(UpdatePost.this)
//                                                .setTitle("Error")
//                                                .setIcon(R.drawable.ic_check_wt)
//                                                .setBackgroundColorRes(R.color.colorPrimaryDark)
//                                                .setDuration(10000)
//                                                .enableSwipeToDismiss()
//                                                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
//                                                .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
//                                                .setText(e.getMessage())
//                                                .show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
//                        pd.setVisibility(View.GONE);
//                        Alerter.create(UpdatePost.this)
//                                .setTitle("Error")
//                                .setIcon(R.drawable.ic_check_wt)
//                                .setBackgroundColorRes(R.color.colorPrimaryDark)
//                                .setDuration(10000)
//                                .enableSwipeToDismiss()
//                                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
//                                .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
//                                .setText(e.getMessage())
//                                .show();

                    });
        }

    }

    private void updateWithMemeData(String mText, String uri) {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Post/" + "Post_" + timeStamp;
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putFile(Uri.parse(uri)).addOnSuccessListener(taskSnapshot -> {

            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;
            String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();

            if (uriTask.isSuccessful()) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", id);
                hashMap.put("name", name);
                hashMap.put("dp", dp);
                hashMap.put("text", mText);
                hashMap.put("type", "Image");
                hashMap.put("meme", downloadUri);
                hashMap.put("vine", "noVideo");
                DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                dRef.child(editPostId).updateChildren(hashMap).addOnSuccessListener(aVoid -> {
//                    text.setText("");
//                    meme.setImageURI(null);
//                    image_uri = null;
//                    type.setText("Text");
//                    pd.setVisibility(View.GONE);
//                    update_remove.setVisibility(View.GONE);
//                    update_it.setVisibility(View.VISIBLE);
//                    update_vine.setVisibility(View.GONE);
//                    update_it.setVisibility(View.GONE);
//                    new Handler().postDelayed(() -> {
//                        Intent intent = new Intent(getApplicationContext(), Post.class);
//                        startActivity(intent);
//                        finish();
//
//
//                    }, 2000);
//
//                    Alerter.create(UpdatePost.this)
//                            .setTitle("Successful")
//                            .setIcon(R.drawable.ic_check_wt)
//                            .setBackgroundColorRes(R.color.colorPrimaryDark)
//                            .setDuration(10000)
//                            .enableSwipeToDismiss()
//                            .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
//                            .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
//                            .setText("Post Updated")
//                            .show();


                }).addOnFailureListener(e -> {

//                    pd.setVisibility(View.GONE);
//                    Alerter.create(UpdatePost.this)
//                            .setTitle("Error")
//                            .setIcon(R.drawable.ic_check_wt)
//                            .setBackgroundColorRes(R.color.colorPrimaryDark)
//                            .setDuration(10000)
//                            .enableSwipeToDismiss()
//                            .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
//                            .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
//                            .setText(e.getMessage())
//                            .show();
                });
            }

        }).addOnFailureListener(e -> {

//            pd.setVisibility(View.GONE);
//            Alerter.create(UpdatePost.this)
//                    .setTitle("Error")
//                    .setIcon(R.drawable.ic_check_wt)
//                    .setBackgroundColorRes(R.color.colorPrimaryDark)
//                    .setDuration(10000)
//                    .enableSwipeToDismiss()
//                    .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
//                    .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
//                    .setText(e.getMessage())
//                    .show();
        });

    }

    private void updateData(String mText) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("name", name);
        hashMap.put("dp", dp);
        hashMap.put("text", mText);
        hashMap.put("meme", "noImage");
        hashMap.put("type", "Text");
        hashMap.put("vine", "noVideo");
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
        dRef.child(editPostId).updateChildren(hashMap).addOnSuccessListener(aVoid -> {
//            Alerter.create(UpdatePost.this)
//                    .setTitle("Successful")
//                    .setIcon(R.drawable.ic_check_wt)
//                    .setBackgroundColorRes(R.color.colorPrimaryDark)
//                    .setDuration(10000)
//                    .enableSwipeToDismiss()
//                    .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
//                    .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
//                    .setText("Post Updated")
//                    .show();
//            new Handler().postDelayed(() -> {
//                Intent intent = new Intent(getApplicationContext(), Post.class);
//                startActivity(intent);
//                finish();
//
//
//            },2000);
//
//            text.setText("");
//            vines.setVideoURI(null);
//            video_uri = null;
//            vines.setVisibility(View.GONE);
//            update_vine.setVisibility(View.GONE);
//            update_it.setVisibility(View.VISIBLE);
//            type.setText("Text");
//            pd.setVisibility(View.GONE);
//            update_remove.setVisibility(View.GONE);
//            update_remove.setVisibility(View.GONE);
//            meme.setImageURI(null);
//            image_uri = null;

        }).addOnFailureListener(e -> {

//            pd.setVisibility(View.GONE);
//            Alerter.create(UpdatePost.this)
//                    .setTitle("Error")
//                    .setIcon(R.drawable.ic_check_wt)
//                    .setBackgroundColorRes(R.color.colorPrimaryDark)
//                    .setDuration(10000)
//                    .enableSwipeToDismiss()
//                    .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
//                    .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
//                    .setText(e.getMessage())
//                    .show();
        });

    }

    private void loadPostData(String editPostId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Product");
        Query query = reference.orderByChild("pId").equalTo(editPostId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
//                    hisId = ""+ds.child("id").getValue();
//                    hispId = ""+ds.child("pId").getValue();
                    editcategory = ""+ds.child("category").getValue();
                    editproductname = ""+ds.child("productname").getValue();
                    editbrandname = ""+ds.child("brandname").getValue();
                    editmrp = ""+ds.child("mrp").getValue();
                    editdiscount = ""+ds.child("discount").getValue();
                    editprice = ""+ds.child("price").getValue();
                    editsaveamount = ""+ds.child("saveamount").getValue();
                    editaddress = ""+ds.child("address").getValue();
                    editdetail = ""+ds.child("detail").getValue();
                    editpTime = ""+ds.child("pTime").getValue();
                    hisavailabilty = ""+ds.child("available").getValue();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Product").child(editPostId).child("images");
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {

                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                            td = (HashMap<String,Object>) dataSnapshot.getValue();

                            for (Map.Entry<String, Object> entry : td.entrySet()){
                                imageslist.add((String)entry.getValue());
                            }
                            imageslist.forEach(s -> list.add(Uri.parse(s)));

                            adapters = new HorizontalRecyclerView(list);
                            binding.photoView.setHasFixedSize(true);
                            binding.photoView.setLayoutManager(new LinearLayoutManager(UpdateProduct.this,LinearLayoutManager.HORIZONTAL, false));
                            binding.photoView.setAdapter(adapters);
                        }

                        @Override
                        public void onCancelled(@NotNull DatabaseError databaseError) {
                            //handle databaseError
                        }
                    });

                    if (editcategory.equals("apparel")){
                        binding.radiogroup.setVisibility(View.VISIBLE);
                        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("Product").child(editPostId).child("sizes");
                        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                                td = (HashMap<String,Object>) dataSnapshot.getValue();

                                for (Map.Entry<String, Object> entry : Objects.requireNonNull(td).entrySet()){
                                    sizelist.add((String)entry.getValue());
                                }

                                String[] sizes = sizelist.toArray(new String[sizelist.size()]);

                                for (String s : sizes) {
                                    if ("xs".equals(s)) {
                                        binding.xs.setChecked(true);
                                    }
                                    if ("s".equals(s)) {
                                        binding.s.setChecked(true);
                                    }
                                    if ("m".equals(s)) {
                                        binding.m.setChecked(true);
                                    }
                                    if ("l".equals(s)) {
                                        binding.l.setChecked(true);
                                    }
                                    if ("xl".equals(s)) {
                                        binding.xl.setChecked(true);
                                    }
                                    if ("xxl".equals(s)) {
                                        binding.xxl.setChecked(true);
                                    }
                                    if ("xxxl".equals(s)) {
                                        binding.xxxl.setChecked(true);
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NotNull DatabaseError databaseError) {
                                //handle databaseError
                            }
                        });
                    }

                    binding.category.setText(editcategory);
                    binding.product.setText(editproductname);
                    binding.price.setText(editprice);
                    binding.brandname.setText(editbrandname);
                    binding.discount.setText(editdiscount);
                    binding.save.setText(editsaveamount);
                    binding.detail.setText(editdetail);
                    binding.mrp.setText(editmrp);
                    binding.address.setText(editaddress);

                    if (hisavailabilty.equals("false")){
                        binding.show.setText("UNHIDE");
                        binding.show.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Query query = FirebaseDatabase.getInstance().getReference("Product").orderByChild("pId").equalTo(editPostId);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("available", "true");
                                            productReference.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(UpdateProduct.this, "Product is visible", Toast.LENGTH_SHORT).show();
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
                        binding.show.setText("HIDE");
                        binding.show.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Query query = FirebaseDatabase.getInstance().getReference("Product").orderByChild("pId").equalTo(editPostId);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("available", "false");
                                            productReference.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    deletecart(editPostId);
                                                    Toast.makeText(UpdateProduct.this, "Product is hidden", Toast.LENGTH_SHORT).show();
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
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

//                pd.setVisibility(View.GONE);
//                Alerter.create(UpdatePost.this)
//                        .setTitle("Error")
//                        .setIcon(R.drawable.ic_check_wt)
//                        .setBackgroundColorRes(R.color.colorPrimaryDark)
//                        .setDuration(10000)
//                        .enableSwipeToDismiss()
//                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
//                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
//                        .setText(databaseError.getMessage())
//                        .show();
            }
        });

    }

    private void deletecart(String editPostId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Cart");
//        database
//                .orderByChild("pId").equalTo(editPostId)
        reference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                           if (data.child(editPostId).hasChild(editPostId)){
                               dataSnapshot.getRef().setValue(null);
                           }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                }
    });
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Cart").child(id);
////        Query applesQuery = ref.child("Cart").orderByChild("pId").equalTo(editPostId);
//        Query query = ref.orderByChild("pId").equalTo(editPostId);
//        query.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                dataSnapshot.getRef().setValue(null);
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        applesQuery.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
//                    appleSnapshot.child(editPostId).getRef().setValue(null);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
//                Alerter.create(UpdatePost.this)
//                        .setTitle("Successful")
//                        .setIcon(R.drawable.ic_check_wt)
//                        .setBackgroundColorRes(R.color.colorPrimaryDark)
//                        .setDuration(10000)
//                        .enableSwipeToDismiss()
//                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
//                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
//                        .setText("Storage permission Allowed")
//                        .show();
            } else {
//                Alerter.create(UpdatePost.this)
//                        .setTitle("Error")
//                        .setIcon(R.drawable.ic_error)
//                        .setBackgroundColorRes(R.color.colorPrimaryDark)
//                        .setDuration(10000)
//                        .enableSwipeToDismiss()
//                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
//                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
//                        .setText("Storage permission is required")
//                        .show();
            }
        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE   && data != null && data.getData() != null){
//            image_uri = data.getData();
//            meme.setImageURI(image_uri);
//            update_vine.setVisibility(View.GONE);
//            update_it.setVisibility(View.VISIBLE);
//            meme.setVisibility(View.VISIBLE);
//            vines.setVisibility(View.GONE);
//            remove.setVisibility(View.VISIBLE);
//            type.setText("Image");
        }if (image_uri == null){
//            meme.setVisibility(View.GONE);
//            update_it.setVisibility(View.VISIBLE);
        }
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
//            video_uri = data.getData();
//            vines.setVideoURI(video_uri);
//            update_it.setVisibility(View.GONE);
//            vines.setVisibility(View.VISIBLE);
//            meme.setVisibility(View.GONE);
//            type.setText("Video");
//            remove.setVisibility(View.VISIBLE);
//            update_vine.setVisibility(View.VISIBLE);
        }
        if (video_uri == null){
//            update_vine.setVisibility(View.GONE);
//            vines.setVisibility(View.VISIBLE);
//            update_it.setVisibility(View.VISIBLE);
        }
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        list.add(data.getClipData().getItemAt(i).getUri());
                    }
                    type = "updated";
                    binding.b1.setVisibility(View.VISIBLE);
                    adapters.notifyDataSetChanged();
                }
            } else if (data.getData() != null) {
                String imagePath = data.getData().getPath();
            }
        }
    }

    private String getfileExt(Uri video_uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(video_uri));
    }

}