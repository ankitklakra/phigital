package com.phigital.ai.Upload;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
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
import com.hendraanggrian.appcompat.widget.SocialEditText;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.phigital.ai.Adapter.AdapterUsersPost;
import com.phigital.ai.BaseActivity;
import com.phigital.ai.Model.ModelUser;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.databinding.ActivityPromoteBinding;
//import com.razorpay.Checkout;
//import com.razorpay.PaymentResultListener;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import gun0912.tedimagepicker.builder.TedImagePicker;
import id.zelory.compressor.Compressor;

@SuppressLint("SetTextI18n")
public class SponsorshipActivity extends BaseActivity implements View.OnClickListener {
    String privacyType;

    int a = 1;
    int b = 4;
    int c = 5;

    private static final int LOCATION_PICK_CODE = 1009;
    String type = "none";
    String mLocation = "";
    String audiencenumber = "";
    String link = "";
    File compressedImageFile;

    Context context = SponsorshipActivity.this;
    ActivityPromoteBinding binding;
    int duration = Toast.LENGTH_SHORT;

    private static final int PERMISSION_CODE = 1001;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    SharedPref sharedPref;
    Uri resulturi,video_uri;

    AdapterUsersPost adapterUsers;
    List<ModelUser> userList;

    //Dialog
    Dialog locationdialog,linkdialog,paymentdialog;
    TextInputEditText address,websiteurl;
    Button locationdone,linkdone;

    SocialEditText text;

    FirebaseAuth firebaseAuth;
    DatabaseReference dRef;

    String name,dp,id,locations,username;

    private static final int MY_READ_PERMISSION_CODE = 101;

    //payment
    TextView est,tax;
    TextInputEditText audience,budget;
    Button done;
    String money;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_promote);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        id = Objects.requireNonNull(user).getUid();


        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,new IntentFilter(
                "usertag"
        ));

        binding.image.setOnClickListener(v -> {
            TedImagePicker.with(SponsorshipActivity.this).backButton(R.drawable.ic_left).start(this::showSingleImage);
        });

        binding.meme.setOnClickListener(v -> {
            TedImagePicker.with(SponsorshipActivity.this).backButton(R.drawable.ic_left).start(this::showSingleImage);

        });

        binding.back.setOnClickListener(v -> {
            onBackPressed();
        });


        binding.link.setOnClickListener(v -> linkdialog.show());

        binding.next.setOnClickListener(v -> {
            if (!(binding.textBox.getText().toString().isEmpty()) && compressedImageFile != null){
                FirebaseDatabase.getInstance().getReference().child("Admin").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            if (snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                if (type.equals("image")){
                                    binding.pb.setVisibility(View.VISIBLE);
                                    uploadData(compressedImageFile);
                                }
                            }else{
                                paymentdialog.show();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }else
                { Snackbar.make(binding.postlayout,"Please fill the details", Snackbar.LENGTH_LONG).show();
            }
        });

        binding.location.setOnClickListener(v -> {
            Intent intent = new PlaceAutocomplete.IntentBuilder()
                    .accessToken("pk.eyJ1IjoicGhpZ2l0YWwtYWkiLCJhIjoiY2tzaGQ4dWJrMTloZzMwb2ZocHdwZzg5ZiJ9.i4HyC_bMbjwyZiAdwIbO7w")
                    .placeOptions(PlaceOptions.builder()
                            .backgroundColor(Color.parseColor("#ffffff"))
                            .build(PlaceOptions.MODE_CARDS))
                    .build(this);
            startActivityForResult(intent, LOCATION_PICK_CODE);
        });

        verifyStoragePermission(SponsorshipActivity.this);


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


        userList = new ArrayList<>();

        binding.rcv.setHasFixedSize(true);
        binding.rcv.setLayoutManager(new LinearLayoutManager(SponsorshipActivity.this));
        binding.rcv.smoothScrollToPosition(0);

        binding.textBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                parseText(s.toString());
//                if (s.toString().startsWith("@")){
//                    rcv.setVisibility(View.VISIBLE);
//                    if (!TextUtils.isEmpty(s.toString())){
//                        filterUser(s.toString());
//                    }
//                }
//                String sub =s.toString().substring(s.toString().indexOf("@")+ 1);
//                rcv.setVisibility(View.VISIBLE);
//                if (!TextUtils.isEmpty(sub)){
//                    filterUser(s.toString());
//                }

            }
        });

        if (ContextCompat.checkSelfPermission(SponsorshipActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SponsorshipActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_READ_PERMISSION_CODE);
        }
        loadb();
    }

    private void uploadData(File compressedImageFile) {
        if (audiencenumber.isEmpty()){
            audiencenumber = "1000";
        }
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Sponsorship/" + "Sponsorship_" + ""+System.currentTimeMillis());
        storageReference.putFile(Uri.fromFile(compressedImageFile)).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;
            Uri downloadUri = uriTask.getResult();
            if (uriTask.isSuccessful()){
                String timeStamp = String.valueOf(System.currentTimeMillis());
                HashMap<Object, String> hashMap = new HashMap<>();
                hashMap.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap.put("pId", timeStamp);
                hashMap.put("text", binding.textBox.getText().toString());
                hashMap.put("pViews", audiencenumber);
                hashMap.put("rViews", "");
                hashMap.put("type", "image");
                hashMap.put("video", "no");
                hashMap.put("image", downloadUri.toString());
                hashMap.put("reTweet", "");
                hashMap.put("reId", "");
                hashMap.put("content", "sponsor");
                hashMap.put("privacy", "public");
                hashMap.put("pTime", timeStamp);
                hashMap.put("location", ""+mLocation);
                hashMap.put("link", link);
                FirebaseDatabase.getInstance().getReference("Sponsorship").child(timeStamp).setValue(hashMap);
                Snackbar.make(binding.postlayout,"Post Uploaded", Snackbar.LENGTH_LONG).show();

                setDefault();
            }
        });
    }

    private void setDefault() {
        binding.textBox.setText("");
        binding.meme.setImageURI(null);
        binding.next.setEnabled(true);
        binding.loctv.setText("Add Location");
        binding.pb.setVisibility(View.GONE);
        type = "none";
        mLocation = "";
        audiencenumber = "";
        link = "";
    }

    private void showSingleImage(Uri uri) {
        BitmapFactory.Options options  = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(),options);
        int H = options.outHeight;
        int W = options.outWidth;
            if (H > W){
                crop1(uri);
            }
            if (H < W){
                crop2(uri);
            }
            if (H == W){
                crop1(uri);
            }
    }

    private void crop2(Uri u) {
        Uri destinationfile = Uri.fromFile(new File(SponsorshipActivity.this.getFilesDir(),"GoodJoy_Image_"+System.currentTimeMillis()+".png"));
//        Uri urlok = Uri.fromFile(new File(image));
        UCrop.Options options = new UCrop.Options();
        UCrop uCrop =  UCrop.of(u, destinationfile);
        uCrop.withAspectRatio(16,9);
        uCrop.withOptions(options);
        uCrop.withMaxResultSize(1024,1024);
        uCrop.start(SponsorshipActivity.this);
    }

    private void crop1(Uri u) {
        Uri destinationfile = Uri.fromFile(new File(SponsorshipActivity.this.getFilesDir(),"GoodJoy_Image_"+System.currentTimeMillis()+".png"));
//        Uri urlok = Uri.fromFile(new File(image));
        UCrop.Options options = new UCrop.Options();
        UCrop uCrop =  UCrop.of(u, destinationfile);
        options.setAspectRatioOptions(1,
                new AspectRatio(null,a,a),
                new AspectRatio(null,b,c)
        );

        uCrop.withOptions(options);
        uCrop.withMaxResultSize(1024,1024);
        uCrop.start(SponsorshipActivity.this);
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String user = intent.getStringExtra("username");
            binding.textBox.append(user);
        }
    };

    private void parseText(String text) {
        String[] words = text.split("[ \\.]");
        for (int i = 0; i < words.length; i++) {
            if (words[i].length() > 0
                    && words[i].charAt(0) == '@') {
                if (!TextUtils.isEmpty(text)) {
                    filterUser(words[i]);
                }
            }
        }
    }

    private void filterUser(String query) {
        String string2 = query.replaceFirst("@", "");
        binding.rcv.setVisibility(View.VISIBLE);
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
                    adapterUsers = new AdapterUsersPost(SponsorshipActivity.this, userList);
                    binding.rcv.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadb() {
        if (locationdialog == null) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(SponsorshipActivity.this).inflate(R.layout.location_dialog, null);
            address = view.findViewById(R.id.address);
            locationdone = view.findViewById(R.id.locationdone);

            locationdone.setOnClickListener(this);
            locationdialog = new BottomSheetDialog(SponsorshipActivity.this,R.style.AppBottomSheetDialogTheme);
            locationdialog.setContentView(view);
        }

        if (linkdialog == null) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(SponsorshipActivity.this).inflate(R.layout.link_dialog, null);
            websiteurl = view.findViewById(R.id.websiteurl);
            linkdone = view.findViewById(R.id.linkdone);

            linkdone.setOnClickListener(this);
            linkdialog = new BottomSheetDialog(SponsorshipActivity.this,R.style.AppBottomSheetDialogTheme);
            linkdialog.setContentView(view);
        }

        if (paymentdialog == null){
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.activity_payment, null);
            done = view.findViewById(R.id.done);
            budget = view.findViewById(R.id.budget);
            audience = view.findViewById(R.id.audience);
            est = view.findViewById(R.id.est);
            tax = view.findViewById(R.id.tax);
            back = view.findViewById(R.id.back);

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    paymentdialog.cancel();
                    budget.setText("");
                    audience.setText("");
                }
            });

//            Checkout.preload(getApplicationContext());

            money = Objects.requireNonNull(budget.getText()).toString();

            done.setOnClickListener(v -> {
                money = budget.getText().toString();
                int a =Integer.parseInt(money);
                if(a <100){
                    Toast.makeText(SponsorshipActivity.this,"Amount cannot be less than 100", Toast.LENGTH_SHORT).show();
                }else{
                    int i = Integer.parseInt(money);
                    int totaltax = (i * 21) / 100;

                    String totalamount = String.valueOf((totaltax + i)*100) ;
                    if (!totalamount.isEmpty()){
//                        startPayment(totalamount);
                        String size =  Objects.requireNonNull(audience.getText()).toString().trim();
                        audiencenumber = size;
                        Toast.makeText(SponsorshipActivity.this," Transaction is initiating please do not refresh ", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(SponsorshipActivity.this,"Amount cannot be less than 100", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            budget.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (!s.toString().isEmpty()){
                        setAudience(s.toString());
                        calculatetax(s.toString());
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().isEmpty()){
                        setAudience(s.toString());
                        calculatetax(s.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().isEmpty()){
                        setAudience(s.toString());
                        calculatetax(s.toString());
                    }
                }
            });

            paymentdialog = new Dialog(this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);

            paymentdialog.setContentView(view);
        }
    }

//    public void startPayment(String amount) {
//
//        /**
//         * Instantiate Checkout
//         */
//        Checkout checkout = new Checkout();
//        checkout.setKeyID("rzp_live_QEbhniXCho8QRC");
//
//        /**
//         * Set your logo here
//         */
////        checkout.setImage(R.drawable.logo);
//
//        /**
//         * Reference to current activity
//         */
//        final Activity activity = this;
//
//        /**
//         * Pass your payment options to the Razorpay Checkout as a JSONObject
//         */
//        try {
//            JSONObject options = new JSONObject();
//
//            options.put("name", "Phigital");
////            options.put("description", "Reference No. #123456");
//            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
////            options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
//            options.put("theme.color", "#00ACED");
//            options.put("currency", "INR");
//            options.put("amount", amount);//pass amount in currency subunits
//            options.put("prefill.email", "phigitalai@gmail.com");
//            options.put("prefill.contact","9988776655");
//            JSONObject retryObj = new JSONObject();
//            retryObj.put("enabled", true);
//            retryObj.put("max_count", 4);
//            options.put("retry", retryObj);
//            checkout.setKeyID("rzp_live_QEbhniXCho8QRC");
//            checkout.open(activity, options);
//
//        } catch(Exception e) {
//
//        }
//    }
//
//    @Override
//    public void onPaymentSuccess(String s) {
//        binding.pb.setVisibility(View.VISIBLE);
//        uploadData(compressedImageFile);
//    }
//
//    @Override
//    public void onPaymentError(int i, String s) {
//        Snackbar.make(binding.postlayout,s, Snackbar.LENGTH_LONG).show();
//    }


    private void calculatetax(String taxes) {
        if (!taxes.isEmpty()){
            int i = Integer.parseInt(taxes);
            int totaltax = (i * 21) / 100;

            est.setText("₹ "+ totaltax);

            tax.setText("₹ "+ (totaltax + i));
        }
    }

    private void setAudience(String s) {
        if (link.isEmpty()){
           audience.setText(s + "0");
        }else{
            audience.setText(String.valueOf(Integer.parseInt(s)/2) +"0");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.locationdone:
                locationdialog.cancel();
                String s =  (Objects.requireNonNull(address.getText())).toString().trim();
                locations = s;
                Toast.makeText(SponsorshipActivity.this, "LOCATION is set to " +s, Toast.LENGTH_SHORT).show();
                break;
            case R.id.linkdone:
                linkdialog.cancel();
                link =  (Objects.requireNonNull(websiteurl.getText())).toString().trim();
                Toast.makeText(SponsorshipActivity.this, "website url is set " +link, Toast.LENGTH_SHORT).show();
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
                    binding.meme.setVisibility(View.VISIBLE);
                    binding.meme.setImageURI(resulturi);
                    try {
                        compressedImageFile = new Compressor(this).compressToFile(new File(resulturi.getPath()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    type = "image";
                }
            }
        }
        //Location
        if (resultCode == Activity.RESULT_OK && requestCode == LOCATION_PICK_CODE && data != null) {
            CarmenFeature feature = PlaceAutocomplete.getPlace(data);
            binding.loctv.setText(feature.text());
            mLocation = feature.text();
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

}