package com.phigital.ai.Shop;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Adapter.AdapterProductInvoicelist;
import com.phigital.ai.Model.ModelCart;
import com.phigital.ai.R;
import com.phigital.ai.SharedPref;
import com.phigital.ai.databinding.ActivityBillBinding;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class InvoiceActivity extends AppCompatActivity {

    Display mDisplay;
    String imagesUri;
    String path;
    Bitmap bitmap;
    int totalHeight;
    int totalWidth;

    String file_name = "Invoice";
    File myPath;

    ActivityBillBinding binding;
    SharedPref sharedPref;
    String hispId,hisId;
    AdapterProductInvoicelist adapters;
    List<ModelCart> productList;
    List<Integer> nList ;
    List<String> pList ;
    int sum = 0;
    public static final int READ_PHONE = 110;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSION_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    String hiscity,hiscountry,hisfullname,hishouseno,hislandmark,hispincode,userId,hismobile,hisstate,hisid;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else setTheme(R.style.AppTheme);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bill);

        WindowManager wm =(WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mDisplay = wm.getDefaultDisplay();

        if(Build.VERSION.SDK_INT >= 23){
            if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED){
            }else{
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PHONE);
            }
        }
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        Intent intent = getIntent();
        hispId = intent.getStringExtra("hispId");
        hisid = intent.getStringExtra("hisId");

//        long milliSeconds= Long.parseLong(hispId);
//        String date = DateFormat.format("dd-MM-yyyy", milliSeconds).toString();

        binding.invoice.setText("Invoice no. " +hispId);
        verifyStoragePermission(InvoiceActivity.this);
        productList = new ArrayList<>();
        pList = new ArrayList<>();
        nList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Users").child(hisid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = Objects.requireNonNull(snapshot.child("name").getValue()).toString().trim();
                binding.name.setText(name);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Invoice").child(hispId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String hisinvoicedate = Objects.requireNonNull(snapshot.child("invoicedate").getValue()).toString().trim();
                binding.date.setText("Date Issued  " +hisinvoicedate);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        adapters = new AdapterProductInvoicelist(InvoiceActivity.this,productList);
        binding.rcview.setHasFixedSize(true);
        binding.rcview.setLayoutManager(new LinearLayoutManager(InvoiceActivity.this,LinearLayoutManager.VERTICAL, false));
        binding.rcview.setAdapter(adapters);
//
//        binding.back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

//        setnum(hisOrderid);

        loadPost(hispId);
        gettotal();

        binding.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(InvoiceActivity.this);
                builder.setTitle("Download PDF");
                builder.setMessage("Are you sure to download pdf?");
                builder.setPositiveButton("Download", (dialog, which) -> {
                    takeScreenshot();
                }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                builder.create().show();
            }
        });

    }
    public Bitmap getBitmapFromView(View view, int totalHeight, int totalWidth){
        Bitmap returnedBitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();

        if(bgDrawable != null){
            bgDrawable.draw(canvas);
        }else{
            canvas.drawColor(Color.WHITE);
        }

        view.draw(canvas);
        return returnedBitmap;
    }
    private void takeScreenshot() {
//        Date now = new Date();
//        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        try {
            // image naming and path  to include sd card  appending name you choose for file
        //    String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";
            File dir = new File(Environment.getExternalStorageDirectory()+"/Download/Phigital/");
            dir.mkdirs();
            File localefile = null;
            try {
                localefile = File.createTempFile("image",".jpg",dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(String.valueOf(localefile));

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

//            openScreenshot(imageFile);
            createPdffile(bitmap);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            Toast.makeText(this, "Something Wrong: "+e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void createPdffile(Bitmap bitmap) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#ffffff"));
        canvas.drawPaint(paint);

        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);

        File root = new File(Environment.getExternalStorageDirectory()+"/Download/Phigital/");
        if (!root.exists()){
            root.mkdir();
        }
        File file = new File(root,"invoice"+hispId+".pdf");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            document.writeTo(fileOutputStream);
            Toast.makeText(this, "Pdf Created Successfully", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        document.close();

    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    private void takeScreenShot(){

        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/Phigital/");

        if(!folder.exists()){
            boolean success = folder.mkdir();
        }

        path = folder.getAbsolutePath();
        path = path + "/" + file_name + System.currentTimeMillis() + ".pdf";

        totalHeight = binding.layout.getChildAt(0).getHeight();
        totalWidth = binding.layout.getChildAt(0).getWidth();

        String extr = Environment.getExternalStorageDirectory() + "/Invoice/";
        File file = new File(extr);
        if(!file.exists())
            file.mkdir();
        String fileName = file_name + ".jpg";
        myPath = new File(extr, fileName);
        imagesUri = myPath.getPath();
        bitmap = getBitmapFromView(binding.layout, totalHeight, totalWidth);

        try{
            FileOutputStream fos = new FileOutputStream(myPath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        }catch (Exception e){
            Toast.makeText(this, "Something Wrong: "+e.toString(), Toast.LENGTH_SHORT).show();
        }

        createPdf();


    }

    private void createPdf() {

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#ffffff"));
        canvas.drawPaint(paint);

        Bitmap bitmap = Bitmap.createScaledBitmap(this.bitmap, this.bitmap.getWidth(), this.bitmap.getHeight(), true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);
        File filePath = new File(path);
        try{
            document.writeTo(new FileOutputStream(filePath));
        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(this, "Something Wrong: "+e.toString(), Toast.LENGTH_SHORT).show();
        }

        document.close();

        if (myPath.exists())
            myPath.delete();

        openPdf(path);

    }

    private void openPdf(String path) {
        Toast.makeText(this, "Download Success", Toast.LENGTH_SHORT).show();
        File file = new File(path);
        Intent target = new Intent(Intent.ACTION_VIEW);
        Uri photoURI = FileProvider.getUriForFile(InvoiceActivity.this, InvoiceActivity.this.getApplicationContext().getPackageName() + ".provider", file);
        target.setDataAndType(photoURI, "application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Intent intent = Intent.createChooser(target, "Open FIle");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try{
            startActivity(intent);
        }catch (ActivityNotFoundException e){
            Toast.makeText(this, "No Apps to read PDF FIle", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadPost(String hispId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Invoice").child(hispId).child("Order");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pList.clear();
                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()){
                    ModelCart post2 = snapshot1.getValue(ModelCart.class);
                    productList.add(post2);
                }
                adapters.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void gettotal() {
        DatabaseReference d = FirebaseDatabase.getInstance().getReference("Invoice").child(hispId).child("Order");
        d.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                nList.clear();
                productList.clear();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    ModelCart post1 = postSnapshot.getValue(ModelCart.class);
                    productList.add(post1);
                    try {
                        nList.add(Integer.parseInt(Objects.requireNonNull(post1).getFinalprice()));
                    }catch (Exception ignored){

                    }
                }

                sum = nList.stream().mapToInt(Integer::intValue).sum();
                binding.totalpricetv.setText("₹ "+sum);

            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    public static void verifyStoragePermission(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) ;


        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSION_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
}