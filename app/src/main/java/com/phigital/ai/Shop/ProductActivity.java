package com.phigital.ai.Shop;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.muddzdev.styleabletoast.StyleableToast;
import com.phigital.ai.Adapter.AdapterUsersPost;
import com.phigital.ai.Adapter.HorizontalRecyclerView;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.databinding.ActivityProductBinding;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class ProductActivity extends AppCompatActivity implements View.OnClickListener {


    private int uploads = 0;

    int a = 1;
    int b = 4;
    int c = 5;

    ArrayList<String> urlStrings = new ArrayList<>();

    String type = "none";

    ArrayList<Uri> uriArrayList = new ArrayList<>();
    RecyclerView recyclerView;
    HorizontalRecyclerView adapters;

    Context context = ProductActivity.this;
    ActivityProductBinding binding;

    int duration = Toast.LENGTH_SHORT;

    private static final int PERMISSION_CODE = 1001;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private Uri  video_uri ;
    private static final int MY_READ_PERMISSION_CODE = 101;

    SharedPref sharedPref;
    Uri resulturi;

    AdapterUsersPost adapterUsers;
    List<ModelUser> userList;

    BottomSheetDialog locationdialog;
    Button locationdone;

    TextInputEditText address;

    FirebaseAuth firebaseAuth;
    DatabaseReference dRef;

    String name, dp, id ,locations,username,privacyType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        }
        else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_product);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,new IntentFilter(
                "remove"
        ));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        id = Objects.requireNonNull(user).getUid();

        String[] items = new String[]{
                "Apparel",
                "Wooden Items"

        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                ProductActivity.this,
                R.layout.dropdown_menu,
                items
        );

        binding.category.setAdapter(adapter);

        adapters = new HorizontalRecyclerView(uriArrayList);
        binding.photoView.setHasFixedSize(true);
        binding.photoView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));
        binding.photoView.setAdapter(adapters);

        binding.a1.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(intent, "Pictures: "), 1);
        });

        binding.productimage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Pictures: "), 2);
        });

        binding.back.setOnClickListener(v -> {
            onBackPressed();
        });

        verifyStoragePermission(ProductActivity.this);

        firebaseAuth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        dRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        dRef.addValueEventListener(new ValueEventListener() {
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

        binding.done.setOnClickListener(v -> {
            if (binding.category.getText().toString().equals("Apparel")){
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

                if(!sizeList.isEmpty()){
                    if (!category.isEmpty() && !product.isEmpty() && !brandname.isEmpty()
                            && !mrp.isEmpty() && !discount.isEmpty() && !price.isEmpty()
                            && !save.isEmpty() && !address.isEmpty() && !detail.isEmpty() && uriArrayList != null ) {
                        String timeStamp = String.valueOf(System.currentTimeMillis());
                        final StorageReference ImageFolder =  FirebaseStorage.getInstance().getReference().child("ProductImage");
                        for (uploads=0; uploads < uriArrayList.size(); uploads++)
                        {
                            Uri IndividualImage = uriArrayList.get(uploads);
                            final StorageReference ImageName = ImageFolder.child("Images" + IndividualImage.getLastPathSegment());
                            ImageName.putFile(IndividualImage).addOnSuccessListener(
                                    taskSnapshot -> ImageName.getDownloadUrl().addOnSuccessListener(
                                            uri -> {
                                                urlStrings.add(String.valueOf(uri));
                                                if (urlStrings.size() == uriArrayList.size()){
                                                    HashMap<String, String> hashMap = new HashMap<>();
                                                    HashMap<String, String> hashMap2 = new HashMap<>();
                                                    HashMap<String, String> hashMap3 = new HashMap<>();
                                                    for (int i = 0; i <urlStrings.size() ; i++) {
                                                        hashMap2.put("ImgLink"+i, urlStrings.get(i));
                                                    }
                                                    for (int i = 0; i <sizeList.size() ; i++) {
                                                        hashMap3.put("I"+i, sizeList.get(i));
                                                    }
                                                    hashMap.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                    hashMap.put("pId", timeStamp);
                                                    hashMap.put("available", "true");
                                                    hashMap.put("category", category);
                                                    hashMap.put("productname", product);
                                                    hashMap.put("brandname", brandname);
                                                    hashMap.put("mrp", mrp);
                                                    hashMap.put("discount", discount);
                                                    hashMap.put("price", price);
                                                    hashMap.put("saveamount", save);
                                                    hashMap.put("address", address);
                                                    hashMap.put("detail", detail);
                                                    hashMap.put("pTime", timeStamp);
                                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Product");
                                                    databaseReference.child(timeStamp).setValue(hashMap).addOnSuccessListener(
                                                            task -> databaseReference.child(timeStamp).child("images").setValue(hashMap2)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            databaseReference.child(timeStamp).child("sizes").setValue(hashMap3);
                                                        }
                                                    }).addOnSuccessListener(aVoid -> {
                                                        binding.pb.setVisibility(View.GONE);
                                                        new StyleableToast
                                                                .Builder(getApplicationContext())
                                                                .text("Image Uploaded Successfully")
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
//                    Uri Image  = uriArrayList.get(uploads);
//                    final StorageReference imagename = ImageFolder.child("image/"+Image.getLastPathSegment());
//                    int a = uriArrayList.size();
//                    imagename.putFile(uriArrayList.get(uploads)). addOnSuccessListener(
//                            taskSnapshot -> imagename.getDownloadUrl().addOnSuccessListener(uri -> {
//                                String url = String.valueOf(uri);
//                                HashMap<Object, String> hashMap = new HashMap<>();
//                                hashMap.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
//                                hashMap.put("pId", timeStamp);
//                                hashMap.put("category", category);
//                                hashMap.put("productname", product);
//                                hashMap.put("brandname", brandname);
//                                hashMap.put("mrp", mrp);
//                                hashMap.put("discount", discount);
//                                hashMap.put("images", url);
//                                hashMap.put("price", price);
//                                hashMap.put("saveamount", save);
//                                hashMap.put("address", address);
//                                hashMap.put("detail", detail);
//                                hashMap.put("pTime", timeStamp);
//                                DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Product");
//                                dRef.child(timeStamp).setValue(hashMap).addOnSuccessListener(aVoid -> {
//                                    binding.pb.setVisibility(View.GONE);
//                                    new StyleableToast
//                                            .Builder(getApplicationContext())
//                                            .text("Image Uploaded Successfully")
//                                            .textColor(Color.WHITE)
//                                            .textBold()
//                                            .gravity(0)
//                                            .length(2000)
//                                            .solidBackground()
//                                            .backgroundColor(getResources().getColor(R.color.colorPrimary))
//                                            .show();
//                                });
//                            }));
//                }
                    }else {
                        Toast.makeText(context, "Fill All details", Toast.LENGTH_SHORT).show();
                        binding.pb.setVisibility(View.GONE);
                    }
                }else{
                    Toast.makeText(context, "Please select Size", Toast.LENGTH_SHORT).show();
                }
            }
            else{
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

                if (!category.isEmpty() && !product.isEmpty() && !brandname.isEmpty()
                        && !mrp.isEmpty() && !discount.isEmpty() && !price.isEmpty()
                        && !save.isEmpty() && !address.isEmpty() && !detail.isEmpty() && uriArrayList != null ) {
                    String timeStamp = String.valueOf(System.currentTimeMillis());
                    final StorageReference ImageFolder =  FirebaseStorage.getInstance().getReference().child("ProductImage");
                    for (uploads=0; uploads < uriArrayList.size(); uploads++)
                    {
                        Uri IndividualImage = uriArrayList.get(uploads);
                        final StorageReference ImageName = ImageFolder.child("Images" + IndividualImage.getLastPathSegment());
                        ImageName.putFile(IndividualImage).addOnSuccessListener(
                                taskSnapshot -> ImageName.getDownloadUrl().addOnSuccessListener(
                                        uri -> {
                                            urlStrings.add(String.valueOf(uri));
                                            if (urlStrings.size() == uriArrayList.size()){
                                                HashMap<String, String> hashMap = new HashMap<>();
                                                HashMap<String, String> hashMap2 = new HashMap<>();
                                                for (int i = 0; i <urlStrings.size() ; i++) {
                                                    hashMap2.put("ImgLink"+i, urlStrings.get(i));
                                                }
                                                hashMap.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                hashMap.put("pId", timeStamp);
                                                hashMap.put("available", "true");
                                                hashMap.put("category", category);
                                                hashMap.put("productname", product);
                                                hashMap.put("brandname", brandname);
                                                hashMap.put("mrp", mrp);
                                                hashMap.put("discount", discount);
                                                hashMap.put("price", price);
                                                hashMap.put("saveamount", save);
                                                hashMap.put("address", address);
                                                hashMap.put("detail", detail);
                                                hashMap.put("pTime", timeStamp);
                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Product");
                                                databaseReference.child(timeStamp).setValue(hashMap).addOnSuccessListener(
                                                        task -> databaseReference.child(timeStamp).child("images").setValue(hashMap2)).addOnSuccessListener(aVoid -> {
                                                    binding.pb.setVisibility(View.GONE);
                                                    new StyleableToast
                                                            .Builder(getApplicationContext())
                                                            .text("Image Uploaded Successfully")
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
//                    Uri Image  = uriArrayList.get(uploads);
//                    final StorageReference imagename = ImageFolder.child("image/"+Image.getLastPathSegment());
//                    int a = uriArrayList.size();
//                    imagename.putFile(uriArrayList.get(uploads)). addOnSuccessListener(
//                            taskSnapshot -> imagename.getDownloadUrl().addOnSuccessListener(uri -> {
//                                String url = String.valueOf(uri);
//                                HashMap<Object, String> hashMap = new HashMap<>();
//                                hashMap.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
//                                hashMap.put("pId", timeStamp);
//                                hashMap.put("category", category);
//                                hashMap.put("productname", product);
//                                hashMap.put("brandname", brandname);
//                                hashMap.put("mrp", mrp);
//                                hashMap.put("discount", discount);
//                                hashMap.put("images", url);
//                                hashMap.put("price", price);
//                                hashMap.put("saveamount", save);
//                                hashMap.put("address", address);
//                                hashMap.put("detail", detail);
//                                hashMap.put("pTime", timeStamp);
//                                DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Product");
//                                dRef.child(timeStamp).setValue(hashMap).addOnSuccessListener(aVoid -> {
//                                    binding.pb.setVisibility(View.GONE);
//                                    new StyleableToast
//                                            .Builder(getApplicationContext())
//                                            .text("Image Uploaded Successfully")
//                                            .textColor(Color.WHITE)
//                                            .textBold()
//                                            .gravity(0)
//                                            .length(2000)
//                                            .solidBackground()
//                                            .backgroundColor(getResources().getColor(R.color.colorPrimary))
//                                            .show();
//                                });
//                            }));
//                }
                }else {
                    Toast.makeText(context, "Fill All details", Toast.LENGTH_SHORT).show();
                    binding.pb.setVisibility(View.GONE);
                }
            }
        });

        userList = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(ProductActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ProductActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_READ_PERMISSION_CODE);
        }

        loadb();

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
                  uriArrayList.clear();
                  if (uriArrayList.isEmpty()){
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
                        binding.discount.setText(String.valueOf(discount)+ "%");
                    }else{

                    }

                }
            });
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String id = intent.getStringExtra("removephoto");
            if (!id.isEmpty()){
                if (uriArrayList.size() > 2){
                    int a = Integer.parseInt(id);
                    uriArrayList.remove(a);
                    adapters.notifyItemRemoved(a);
                }
            }
        }
    };

//    private void showvideo(Uri uri) {
////        binding.playerview.set(uri);
//        video_uri = uri;
//        final SimpleExoPlayer player = new SimpleExoPlayer.Builder(context).build();
//        binding.playerview.setPlayer(player);
//        MediaItem mediaItem = MediaItem.fromUri(video_uri);
//        player.setMediaItem(mediaItem);
//        player.setRepeatMode(Player.REPEAT_MODE_OFF);
//        player.setPlayWhenReady(true);
//        binding.fl.setVisibility(View.VISIBLE);
//        binding.playerview.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
//        type = "video";
////                player.setVideoScalingMode();
//    };

    private void SendLink(String url) {

    }

    private void showSingleImage(Uri uri) {
        BitmapFactory.Options options  = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(),options);
        int H = options.outHeight;
        int W = options.outWidth;
        crop2(uri);

    }

    private void crop2(Uri u) {
        Uri destinationfile = Uri.fromFile(new File(ProductActivity.this.getFilesDir(),"GoodJoy_Image_"+System.currentTimeMillis()+".png"));
//        Uri urlok = Uri.fromFile(new File(image));
        UCrop.Options options = new UCrop.Options();
        UCrop uCrop =  UCrop.of(u, destinationfile);
        uCrop.withAspectRatio(16,9);
        uCrop.withOptions(options);
        uCrop.withMaxResultSize(1024,1024);
        uCrop.start(ProductActivity.this);
    }

    private void crop1(Uri u) {
        Uri destinationfile = Uri.fromFile(new File(ProductActivity.this.getFilesDir(),"GoodJoy_Image_"+System.currentTimeMillis()+".png"));
//        Uri urlok = Uri.fromFile(new File(image));
        UCrop.Options options = new UCrop.Options();
        UCrop uCrop =  UCrop.of(u, destinationfile);
        options.setAspectRatioOptions(1,
                new AspectRatio(null,a,a),
                new AspectRatio(null,b,c)
        );

        uCrop.withOptions(options);
        uCrop.withMaxResultSize(1024,1024);
        uCrop.start(ProductActivity.this);
    }


    private void parseText(String text) {
        String[] words = text.split("[ \\.]");
        for (int i = 0; i < words.length; i++) {
            if (words[i].length() > 0
                    && words[i].charAt(0) == '@') {
                if (!TextUtils.isEmpty(text)) {
//                    filterUser(words[i]);
                }
//                System.out.println(words[i]);
            }
        }
    }

    private void filterUser(String query) {
        String string2 = query.replaceFirst("@", "");
//        binding.rcv.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    if (Objects.requireNonNull(modelUser).getUsername().toLowerCase().contains(string2.toLowerCase()) ||
                            modelUser.getName().toLowerCase().contains(string2.toLowerCase()))
                        userList.add(modelUser);
                    adapterUsers = new AdapterUsersPost(ProductActivity.this, userList);
//                   binding.rcv.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadb() {
        if (locationdialog == null) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(ProductActivity.this).inflate(R.layout.location_dialog, null);

            address = view.findViewById(R.id.address);
            locationdone = view.findViewById(R.id.locationdone);
            locationdone.setOnClickListener(this);
            locationdialog = new BottomSheetDialog(ProductActivity.this);
            locationdialog.setContentView(view);

            String text = Objects.requireNonNull(address.getText()).toString();
            if (text.isEmpty()){

            }else{
                address.setText(text);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.locationdone:
                locationdialog.cancel();
                String s =  (Objects.requireNonNull(address.getText())).toString().toLowerCase().trim();
                if (s.equals("sponsored")){
                    Toast.makeText(ProductActivity.this, "Invalid location ", Toast.LENGTH_SHORT).show();
                }else{
                    locations = s;
                    Toast.makeText(ProductActivity.this, "Location is set to " +s, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Storage permission Allowed", duration).show();
            } else {
                Toast.makeText(context, "Storage permission is required", duration).show();
            }
        }
        if (requestCode == MY_READ_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Read external storage Permission granted", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    resulturi = UCrop.getOutput(data);
//                   binding.meme.setVisibility(View.VISIBLE);
//                   binding.meme.setImageURI(resulturi);
                    type = "image";
                }
            }
        }
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        uriArrayList.add(data.getClipData().getItemAt(i).getUri());
                    }
                    binding.b1.setVisibility(View.VISIBLE);
                    adapters.notifyDataSetChanged();
                }
            } else if (data.getData() != null) {
                String imagePath = data.getData().getPath();
            }
        }
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                Uri a = data.getData();
                binding.con.setVisibility(View.VISIBLE);
                binding.image.setImageURI(a);
            } else if (data.getData() != null) {
                String imagePath = data.getData().getPath();
            }
        }
    }


    public static void verifyStoragePermission(Activity activity){
        int permission =ActivityCompat.checkSelfPermission(activity,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void storeLink(ArrayList<String> urlStrings) {
        HashMap<String, String> hashMap = new HashMap<>();

        for (int i = 0; i <urlStrings.size() ; i++) {
            hashMap.put("ImgLink"+i, urlStrings.get(i));

        }
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Product");

        databaseReference.push().setValue(hashMap)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(ProductActivity.this, "Successfully Uplosded", Toast.LENGTH_SHORT).show();
                            }
                        }
                ).addOnFailureListener(e -> Toast.makeText(ProductActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show());

        uriArrayList.clear();
    }

}