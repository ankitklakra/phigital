package com.phigital.ai.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.phigital.ai.GetTimeAgo;
import com.phigital.ai.Model.ModelPoll;
import com.phigital.ai.R;
import com.phigital.ai.Utility.MediaView;
import com.phigital.ai.Utility.UserProfile;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

//@SuppressWarnings("ALL")
public class AdapterPoll extends RecyclerView.Adapter<AdapterPoll.MyHolder> {

    Context context;

    final List<ModelPoll> pollList;
    ConstraintLayout reportCL,deleteCL;

    private String userId;
    int a = 100;
    int opt1;
    int opt2;
    int opt3;
    int opt4;

    BottomSheetDialog pollbottomsheet,pollbottomsheet2;

    public AdapterPoll(Context context, List<ModelPoll> pollList) {
        this.context = context;
        this.pollList = pollList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.poll, parent, false);
        context = parent.getContext();
        return new MyHolder(view);
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        String id = pollList.get(position).getId();
        String pId = pollList.get(position).getpId();
        String titletext = pollList.get(position).getTitletext();
        String text1 = pollList.get(position).getText1();
        String text2 = pollList.get(position).getText2();
        String text3 = pollList.get(position).getText3();
        String text4 = pollList.get(position).getText4();
        String pic1 = pollList.get(position).getPic1();
        String pic2 = pollList.get(position).getPic2();
        String pic3 = pollList.get(position).getPic3();
        String pic4 = pollList.get(position).getPic4();
        String op1 = pollList.get(position).getOption1();
        String op2 = pollList.get(position).getOption2();
        String op3 = pollList.get(position).getOption3();
        String op4 = pollList.get(position).getOption4();
        String pTime = pollList.get(position).getpTime();
        String rem = pollList.get(position).getTimeRemain();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        try {
            opt1 =Integer.parseInt(op1);
        }catch (NumberFormatException e){
            opt1 = 0;
        }
        try {
            opt2 =Integer.parseInt(op2);
        }catch (NumberFormatException e){
            opt2 = 0;
        }
        try {
            opt3 =Integer.parseInt(op3);
        }catch (NumberFormatException e){
            opt3 = 0;
        }
        try {
            opt4 =Integer.parseInt(op4);
        }catch (NumberFormatException e){
            opt4 = 0;
        }

        //Time
        long lastTime = Long.parseLong(pTime);
        String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime);

        FirebaseDatabase.getInstance().getReference().child("Ban").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    holder.itemView.setVisibility(View.GONE);
                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                    params.height = 0;
                    params.width = 0;
                    holder.itemView.setLayoutParams(params);
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
                    if (snapshot.child(pollList.get(position).getpId()).exists()){
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

                holder.name.setText(name);

                if (!dp.isEmpty()) {
                    try{
                        Picasso.get()
                                .load(dp)
                                .resize(150, 150)
                                .centerCrop()
                                .into(holder.pDp);
                    }catch(Exception e){
                        Picasso.get().load(R.drawable.placeholder).into(holder.pDp);
                    }
                }

                //ClickToPro
                holder.name.setOnClickListener(new View.OnClickListener() {
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

//        long remainTime = Long.parseLong(rem);
////        String lastremainTime = GetTimeAgo.getTimeRemaining(remainTime);
//
//        int hours = (int) remainTime/ HOUR_MILLIS;
//        int temp = (int) remainTime- hours * 3600;
//        int mins = temp / 60;
//        temp = temp - mins * 60;
//        int secs = temp;
//
//        String requiredFormat = hours+ ": "+mins+": "+secs;//hh:mm:ss formatted string
//        long now = System.currentTimeMillis();
//
//        long newTime = remainTime - now;
//
//        int  seconds = (int) (duration/1000)%60;
//        int  minutes = (int) ((duration/(1000*60))%60);
//        int  hours = (int) ((duration/(1000*60*60))%24);
//
////        holder.textViewtimer.setText(seconds);
////        holder.textViewtimer2.setText(minutes);
////        holder.textViewtimer3.setText(hours);
//
//        new CountDownTimer(duration, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                String sDuration = String.format(Locale.ENGLISH,"%02d : %02d : %02d"
//                , TimeUnit.MILLISECONDS.toHours(hours)
//                ,TimeUnit.MILLISECONDS.toMinutes(minutes)
//                ,TimeUnit.MILLISECONDS.toSeconds(seconds)- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(minutes)));
////                holder.textViewtimer.setText(sDuration);
//            }
//
//            @Override
//            public void onFinish() {
//                Toast.makeText(context, "CountDown has finished", Toast.LENGTH_SHORT).show();
//            }
//        };


        //Set Data

        long now = System.currentTimeMillis();
        long future = Long.parseLong(rem);

        final long diff = future - now;

        if (now > future) {
            holder.remain.setText("Time Over");;
            calculate(holder,position);
            holder.linlayout2.setVisibility(View.GONE);
            holder.voted.setVisibility(View.VISIBLE);
            FirebaseDatabase.getInstance().getReference().child("PollCount").child(pId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (FirebaseAuth.getInstance().getCurrentUser()!= null){
                        if (snapshot.hasChild(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                            holder.votedtv.setText("Voted");
                        }else {
                            holder.votedtv.setText("Finished");
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else {
            FirebaseDatabase.getInstance().getReference().child("PollCount").child(pId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                        holder.voted.setVisibility(View.VISIBLE);
                        holder.linlayout2.setVisibility(View.GONE);
                        holder.votedtv.setText("Voted");
                    }else {
                        holder.voted.setVisibility(View.GONE);
                        holder.linlayout2.setVisibility(View.VISIBLE);
                        holder.votedtv.setText("Finished");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            calculate(holder,position);
        }
//

        if (holder.timer != null) {
            holder.timer.cancel();
        }

        holder.timer = new CountDownTimer(diff, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;
                String timet = days+""+":" +hours % 24 + ":" + minutes % 60 + ":" + seconds % 60;
                holder.remain.setText(timet);
            }

            @Override
            public void onFinish() {
//                Toast.makeText(context, "CountDown has finished", Toast.LENGTH_SHORT).show();
            }
        }.start();

        holder.time.setText(lastSeenTime);

        holder.ttv.setText(titletext);
//        holder.aedtext.setText(text1);
//        holder.bedtext.setText(text2);
//        holder.cedtext.setText(text3);
//        holder.dedtext.setText(text4);
//        holder.tv_option1.setText(text1);
//        holder.tv_option2.setText(text2);
//        holder.tv_option3.setText(text3);
//        holder.tv_option4.setText(text4);
//        holder.tv_option5.setText(text1);
//        holder.tv_option6.setText(text2);
//        holder.tv_option7.setText(text3);
//        holder.tv_option8.setText(text4);
        holder.aedtext.setText(text1);
        holder.bedtext.setText(text2);
        holder.cedtext.setText(text3);
        holder.dedtext.setText(text4);

        try{
            Picasso.get()
                    .load(pic1)
                    .resize(150, 150)
                    .centerCrop()
                    .into(holder.ima1);
        }catch(Exception e){
            Picasso.get().load(R.drawable.placeholder).into(holder.ima1);
        }
        try{
            Picasso.get()
                    .load(pic2)
                    .resize(150, 150)
                    .centerCrop()
                    .into(holder.imb2);
        }catch(Exception e){
            Picasso.get().load(R.drawable.placeholder).into(holder.imb2);
        }
        try{
            Picasso.get()
                    .load(pic3)
                    .resize(150, 150)
                    .centerCrop()
                    .into(holder.imc3);
        }catch(Exception e){
            Picasso.get().load(R.drawable.placeholder).into(holder.imc3);
        }
        try{
            Picasso.get()
                    .load(pic4)
                    .resize(150, 150)
                    .centerCrop()
                    .into(holder.imd4);
        }catch(Exception e){
            Picasso.get().load(R.drawable.placeholder).into(holder.imd4);
        }

//        Picasso.get().load(pic1).into(holder.ima1);
//        Picasso.get().load(pic2).into(holder.imb2);
//        Picasso.get().load(pic3).into(holder.imc3);
//        Picasso.get().load(pic4).into(holder.imd4);

        holder.seek_bar1.setOnTouchListener((v, event) -> true);
        holder.seek_bar2.setOnTouchListener((v, event) -> true);
        holder.seek_bar3.setOnTouchListener((v, event) -> true);
        holder.seek_bar4.setOnTouchListener((v, event) -> true);

        holder.new5.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference().child("Poll").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if(snapshot.hasChild(pId)){
                        FirebaseDatabase.getInstance().getReference().child("PollCount").child(pId).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(true);
                        FirebaseDatabase.getInstance().getReference().child("Poll").child(pId).child("option1").setValue("" + (Integer.parseInt(op1) + 1))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(@NonNull @NotNull Void unused) {
                                        calculate(holder,position);
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });

//            pollid.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.child("pollusers").hasChild(userId)){
//                        holder.voted.setVisibility(View.VISIBLE);
//                        holder.linlayout2.setVisibility(View.GONE);
//                    } else {
//                        holder.voted.setVisibility(View.GONE);
//                        holder.linlayout2.setVisibility(View.VISIBLE);
//                        pollid.child("option1").setValue("" + (Integer.parseInt(op1) + 1));
//                        pollid.child("pollusers").child(userId).setValue("polled");
//                    }
//                    calculate(holder,position);
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//
//            updateCount(postId);
        });

        holder.new6.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference().child("Poll").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if(snapshot.hasChild(pId)){
                        FirebaseDatabase.getInstance().getReference().child("PollCount").child(pId).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(true);
                        FirebaseDatabase.getInstance().getReference().child("Poll").child(pId).child("option2").setValue("" + (Integer.parseInt(op2) + 1))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(@NonNull @NotNull Void unused) {
                                        calculate(holder,position);
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });

//            pollid.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.child("pollusers").hasChild(userId)){
//                        holder.voted.setVisibility(View.VISIBLE);
//                        holder.linlayout2.setVisibility(View.GONE);
//                    } else {
//                        holder.voted.setVisibility(View.GONE);
//                        holder.linlayout2.setVisibility(View.VISIBLE);
//                        pollid.child("option2").setValue("" + (Integer.parseInt(op2) + 1));
//                        pollid.child("pollusers").child(userId).setValue("polled");
//
//                    }
//                    calculate(holder,position);
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
        });

        holder.new7.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference().child("Poll").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if(snapshot.hasChild(pId)){
                        FirebaseDatabase.getInstance().getReference().child("PollCount").child(pId).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(true);
                        FirebaseDatabase.getInstance().getReference().child("Poll").child(pId).child("option3").setValue("" + (Integer.parseInt(op3) + 1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(@NonNull @NotNull Void unused) {
                                calculate(holder,position);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });


//            pollid.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.child("pollusers").hasChild(userId)){
//                        holder.voted.setVisibility(View.VISIBLE);
//                        holder.linlayout2.setVisibility(View.GONE);
//                    } else {
//                        holder.voted.setVisibility(View.GONE);
//                        holder.linlayout2.setVisibility(View.VISIBLE);
//                        pollid.child("option3").setValue("" + (Integer.parseInt(op3) + 1));
//                        pollid.child("pollusers").child(userId).setValue("polled");
//                    }
//                    calculate(holder,position);
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
        });

        holder.new8.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference().child("Poll").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if(snapshot.hasChild(pId)){
                        FirebaseDatabase.getInstance().getReference().child("PollCount").child(pId).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(true);
                        FirebaseDatabase.getInstance().getReference().child("Poll").child(pId).child("option4").setValue("" + (Integer.parseInt(op4) + 1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(@NonNull @NotNull Void unused) {
                                calculate(holder,position);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });


//            pollid.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.child("pollusers").hasChild(userId)){
//                        holder.voted.setVisibility(View.VISIBLE);
//                        holder.linlayout2.setVisibility(View.GONE);
//                    } else {
//                        holder.voted.setVisibility(View.GONE);
//                        holder.linlayout2.setVisibility(View.VISIBLE);
//                        pollid.child("option4").setValue("" + (Integer.parseInt(op4) + 1));
//                        pollid.child("pollusers").child(userId).setValue("polled");
//                    }
//                    calculate(holder,position);
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
        });

        int max = opt1;

        if (opt2 > max ){
            max = opt2;
        }
        if (opt3 > max ){
            max = opt3;
        }
        if (opt4 > max ){
            max = opt4;
        }

        if (max == 0){
            holder.cons1.setVisibility(View.GONE);
            holder.lastlayout.setVisibility(View.GONE);
        }

        if (max == opt1){
            try{
                Picasso.get()
                        .load(pic1)
                        .resize(150, 150)
                        .centerCrop()
                        .into(holder.imtop);
            }catch(Exception e){
                Picasso.get().load(R.drawable.placeholder).into(holder.imtop);
            }
        }
        if (max == opt2){
            try{
                Picasso.get()
                        .load(pic2)
                        .resize(150, 150)
                        .centerCrop()
                        .into(holder.imtop);
            }catch(Exception e){
                Picasso.get().load(R.drawable.placeholder).into(holder.imtop);
            }
        }
        if (max == opt3){
            try{
                Picasso.get()
                        .load(pic3)
                        .resize(150, 150)
                        .centerCrop()
                        .into(holder.imtop);
            }catch(Exception e){
                Picasso.get().load(R.drawable.placeholder).into(holder.imtop);
            }
        }
        if (max == opt4){
            try{
                Picasso.get()
                        .load(pic4)
                        .resize(150, 150)
                        .centerCrop()
                        .into(holder.imtop);
            }catch(Exception e){
                Picasso.get().load(R.drawable.placeholder).into(holder.imtop);
            }
        }

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(id.equals(userId)){
                    createdeletebottomdialog(holder,pId,position,pic1,pic2,pic3,pic4);
                    pollbottomsheet.show();
                }else{
                    createreportbottomdialog(holder,pId,position,pic1,pic2,pic3,pic4);
                    pollbottomsheet2.show();
                }
            }
        });

        holder.ima1.setOnClickListener(v -> {
            Intent intent1 = new Intent(context, MediaView.class);
            intent1.putExtra("type","image");
            intent1.putExtra("uri",pic1);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        });
        holder.imb2.setOnClickListener(v -> {
            Intent intent2 = new Intent(context, MediaView.class);
            intent2.putExtra("type","image");
            intent2.putExtra("uri",pic2);
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent2);
        });
        holder.imc3.setOnClickListener(v -> {
            Intent intent3 = new Intent(context, MediaView.class);
            intent3.putExtra("type","image");
            intent3.putExtra("uri",pic3);
            intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent3);
        });
        holder.imd4.setOnClickListener(v -> {
            Intent intent4 = new Intent(context, MediaView.class);
            intent4.putExtra("type","image");
            intent4.putExtra("uri",pic4);
            intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent4);
        });

        FirebaseDatabase.getInstance().getReference().child("Poll").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild(pId)){
                    holder.itemView.setVisibility(View.GONE);
                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                    params.height = 0;
                    params.width = 0;
                    holder.itemView.setLayoutParams(params);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void calculate(MyHolder holder, int position) {
        //      DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference().child("Poll");
//      Query q = databaseReference.orderByChild("pId").equalTo(pollList.get(position).getpId());
//      q.addChildEventListener(new ChildEventListener() {
//          @Override
//          public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
//              int  opt1 =Integer.parseInt(pollList.get(position).getOption1());
//              int  opt2 =Integer.parseInt(pollList.get(position).getOption2());
//              int  opt3 =Integer.parseInt(pollList.get(position).getOption3());
//              int  opt4 =Integer.parseInt(pollList.get(position).getOption4());
//              double total = opt1 + opt2 + opt3 + opt4 ;
//              double num1 = ((double) opt1/total)* a;
//              double num2 = ((double) opt2/total)* a;
//              double num3 = ((double) opt3/total)* a;
//              double num4 = ((double) opt4/total)* a;
//
//              if (num1 == 0){
//                  holder.seek_bar1.setProgress(1);
//                  holder.tv_percent1.setText("0 %");
//              }else{
//                  holder.seek_bar1.setProgress((int)num1);
//                  holder.tv_percent1.setText(String.format("%s%%", new DecimalFormat("##.#").format(num1)));
//              }
//              if (num2 == 0){
//                  holder.seek_bar2.setProgress(1);
//                  holder.tv_percent2.setText("0 %");
//              }else{
//                  holder.tv_percent2.setText(String.format("%s%%", new DecimalFormat("##.#").format(num2)));
//                  holder.seek_bar2.setProgress((int)num2);
//              }
//              if (num3 == 0){
//                  holder.seek_bar3.setProgress(1);
//                  holder.tv_percent3.setText("0 %");
//              }else{
//                  holder.tv_percent3.setText(String.format("%s%%", new DecimalFormat("##.#").format(num3)));
//                  holder.seek_bar3.setProgress((int)num3);
//              }
//              if (num4 == 0){
//                  holder.seek_bar4.setProgress(1);
//                  holder.tv_percent4.setText("0 %");
//              }else{
//                  holder.tv_percent4.setText(String.format("%s%%", new DecimalFormat("##.#").format(num4)));
//                  holder.seek_bar4.setProgress((int)num4);
//              }
//              int integer = (int) total;
//              holder.totalvotes.setText(String.valueOf(integer));
//              if (integer == 0){
//                  holder.cons1.setVisibility(View.GONE);
//                  holder.lastlayout.setVisibility(View.GONE);
//              }
//              holder.seek_bar1.getProgressDrawable().setColorFilter(Color.parseColor("#00ACED"), PorterDuff.Mode.SRC_IN);
//              holder.seek_bar2.getProgressDrawable().setColorFilter(Color.parseColor("#00ACED"), PorterDuff.Mode.SRC_IN);
//              holder.seek_bar3.getProgressDrawable().setColorFilter(Color.parseColor("#00ACED"), PorterDuff.Mode.SRC_IN);
//              holder.seek_bar4.getProgressDrawable().setColorFilter(Color.parseColor("#00ACED"), PorterDuff.Mode.SRC_IN);
//          }
//
//          @Override
//          public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
//              int  opt1 =Integer.parseInt(pollList.get(position).getOption1());
//              int  opt2 =Integer.parseInt(pollList.get(position).getOption2());
//              int  opt3 =Integer.parseInt(pollList.get(position).getOption3());
//              int  opt4 =Integer.parseInt(pollList.get(position).getOption4());
//              double total = opt1 + opt2 + opt3 + opt4 ;
//              double num1 = ((double) opt1/total)* a;
//              double num2 = ((double) opt2/total)* a;
//              double num3 = ((double) opt3/total)* a;
//              double num4 = ((double) opt4/total)* a;
//
//              if (num1 == 0){
//                  holder.seek_bar1.setProgress(1);
//                  holder.tv_percent1.setVisibility(View.VISIBLE);
//              }else{
//                  holder.seek_bar1.setProgress((int)num1);
//                  holder.tv_percent1.setText(String.format("%s%%", new DecimalFormat("##.#").format(num1)));
//              }
//              if (num2 == 0){
//                  holder.seek_bar2.setProgress(1);
//                  holder.tv_percent2.setVisibility(View.VISIBLE);
//              }else{
//                  holder.tv_percent2.setText(String.format("%s%%", new DecimalFormat("##.#").format(num2)));
//                  holder.seek_bar2.setProgress((int)num2);
//              }
//              if (num3 == 0){
//                  holder.seek_bar3.setProgress(1);
//                  holder.tv_percent3.setVisibility(View.VISIBLE);
//              }else{
//                  holder.tv_percent3.setText(String.format("%s%%", new DecimalFormat("##.#").format(num3)));
//                  holder.seek_bar3.setProgress((int)num3);
//              }
//              if (num4 == 0){
//                  holder.seek_bar4.setProgress(1);
//                  holder.tv_percent4.setVisibility(View.VISIBLE);
//              }else{
//                  holder.tv_percent4.setText(String.format("%s%%", new DecimalFormat("##.#").format(num4)));
//                  holder.seek_bar4.setProgress((int)num4);
//              }
//              int integer = (int) total;
//              holder.totalvotes.setText(String.valueOf(integer));
//              if (integer == 0){
//                  holder.cons1.setVisibility(View.GONE);
//                  holder.lastlayout.setVisibility(View.GONE);
//              }
//              holder.seek_bar1.getProgressDrawable().setColorFilter(Color.parseColor("#00ACED"), PorterDuff.Mode.SRC_IN);
//              holder.seek_bar2.getProgressDrawable().setColorFilter(Color.parseColor("#00ACED"), PorterDuff.Mode.SRC_IN);
//              holder.seek_bar3.getProgressDrawable().setColorFilter(Color.parseColor("#00ACED"), PorterDuff.Mode.SRC_IN);
//              holder.seek_bar4.getProgressDrawable().setColorFilter(Color.parseColor("#00ACED"), PorterDuff.Mode.SRC_IN);
//          }
//
//          @Override
//          public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {
//
//          }
//
//          @Override
//          public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
//
//          }
//
//          @Override
//          public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//          }
//      });
        FirebaseDatabase.getInstance().getReference().child("Poll").child(pollList.get(position).getpId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                int  opt1 =Integer.parseInt(pollList.get(position).getOption1());
                int  opt2 =Integer.parseInt(pollList.get(position).getOption2());
                int  opt3 =Integer.parseInt(pollList.get(position).getOption3());
                int  opt4 =Integer.parseInt(pollList.get(position).getOption4());
                double total = opt1 + opt2 + opt3 + opt4 ;
                double num1 = ((double) opt1/total)* a;
                double num2 = ((double) opt2/total)* a;
                double num3 = ((double) opt3/total)* a;
                double num4 = ((double) opt4/total)* a;

              if (num1 == 0){
                  holder.seek_bar1.setProgress(1);
                  holder.tv_percent1.setText("0 %");
              }else{
                  holder.seek_bar1.setProgress((int)num1);
                  holder.tv_percent1.setText(String.format("%s%%", new DecimalFormat("##.#").format(num1)));
              }
              if (num2 == 0){
                  holder.seek_bar2.setProgress(1);
                  holder.tv_percent2.setText("0 %");
              }else{
                  holder.tv_percent2.setText(String.format("%s%%", new DecimalFormat("##.#").format(num2)));
                  holder.seek_bar2.setProgress((int)num2);
              }
              if (num3 == 0){
                  holder.seek_bar3.setProgress(1);
                  holder.tv_percent3.setText("0 %");
              }else{
                  holder.tv_percent3.setText(String.format("%s%%", new DecimalFormat("##.#").format(num3)));
                  holder.seek_bar3.setProgress((int)num3);
              }
              if (num4 == 0){
                  holder.seek_bar4.setProgress(1);
                  holder.tv_percent4.setText("0 %");
              }else{
                  holder.tv_percent4.setText(String.format("%s%%", new DecimalFormat("##.#").format(num4)));
                  holder.seek_bar4.setProgress((int)num4);
              }
              int integer = (int) total;
              holder.totalvotes.setText(String.valueOf(integer));
              if (integer == 0){
                  holder.cons1.setVisibility(View.GONE);
                  holder.lastlayout.setVisibility(View.GONE);
              }
              holder.seek_bar1.getProgressDrawable().setColorFilter(Color.parseColor("#00ACED"), PorterDuff.Mode.SRC_IN);
              holder.seek_bar2.getProgressDrawable().setColorFilter(Color.parseColor("#00ACED"), PorterDuff.Mode.SRC_IN);
              holder.seek_bar3.getProgressDrawable().setColorFilter(Color.parseColor("#00ACED"), PorterDuff.Mode.SRC_IN);
              holder.seek_bar4.getProgressDrawable().setColorFilter(Color.parseColor("#00ACED"), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void createreportbottomdialog(MyHolder holder, String pId, int position, String pic1, String pic2, String pic3, String pic4) {
        if (pollbottomsheet2 == null){
            @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.poll_bottom_sheet2, null);
            reportCL = view.findViewById(R.id.reportCL);
            deleteCL = view.findViewById(R.id.deleteCL);

            pollbottomsheet2 = new BottomSheetDialog(context);
            pollbottomsheet2.setContentView(view);

            reportCL.setOnClickListener(v -> {
                pollbottomsheet2.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Report");
                builder.setMessage("Are you sure to report this poll?");
                builder.setPositiveButton("Report",(dialog, which) -> {
                    FirebaseDatabase.getInstance().getReference().child("PollReport").child(pId).setValue(true);
                    Toast.makeText(context, "Poll Reported...", Toast.LENGTH_SHORT).show();
                }).setNegativeButton("Cancel",(dialog, which) -> {
                    dialog.dismiss();
                });
                builder.create().show();
            });
        }
    }

    private void createdeletebottomdialog(MyHolder holder, String pId, int position, String pic1, String pic2, String pic3, String pic4) {
        if (pollbottomsheet == null){
            @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.poll_bottom_sheet,null);
            reportCL = view.findViewById(R.id.reportCL);
            deleteCL = view.findViewById(R.id.deleteCL);

            pollbottomsheet = new BottomSheetDialog(context);
            pollbottomsheet.setContentView(view);

            deleteCL.setOnClickListener(v -> {
                StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pic1);
                StorageReference picRef2 = FirebaseStorage.getInstance().getReferenceFromUrl(pic2);
                StorageReference picRef3 = FirebaseStorage.getInstance().getReferenceFromUrl(pic3);
                StorageReference picRef4 = FirebaseStorage.getInstance().getReferenceFromUrl(pic4);
                picRef.delete().addOnSuccessListener(aVoid1 -> {
                    picRef2.delete().addOnSuccessListener(aVoid2 -> {
                        picRef3.delete().addOnSuccessListener(aVoid3 -> {
                            picRef4.delete().addOnSuccessListener(aVoid4 -> {
                                FirebaseDatabase.getInstance().getReference().child("Poll").child(pId).removeValue();
                                FirebaseDatabase.getInstance().getReference().child("PollCount").child(pId).removeValue();
                            });
                        });
                    });
                });
                pollbottomsheet.dismiss();
                holder.itemView.setVisibility(View.GONE);
            });
        }
    }

    private void addToHisNotification(String hisUid, String pId, String notification){
        String timestamp = ""+System.currentTimeMillis();
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId", pId);
        hashMap.put("timestamp", timestamp);
        hashMap.put("pUid", hisUid);
        hashMap.put("notification", notification);
        hashMap.put("sUid", userId);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hisUid).child("Notifications").child(timestamp).setValue(hashMap)
                .addOnSuccessListener(aVoid -> {

                }).addOnFailureListener(e -> {

        });

    }

    @Override
    public int getItemCount() {
        return pollList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        final CircleImageView pDp;
        final ImageView pMeme;
        CountDownTimer timer;

        final SeekBar seek_bar1;
        final SeekBar seek_bar2;
        final SeekBar seek_bar3;
        final SeekBar seek_bar4;

        final ImageView ima1;
        final ImageView imb2;
        final ImageView imc3;
        final ImageView imd4;
        final ImageView imtop;
        final ImageView more;
        final ImageView like_img;
        final ImageView eye;

        final TextView name;
        final TextView time;
        final TextView username;
        final TextView updated;
        final TextView location;
        final ImageView verified;

        final TextView ttv;
        final TextView aedtext;
        final TextView bedtext;
        final TextView cedtext;
        final TextView dedtext;
        final TextView totalvotes;
        final TextView remain;

        final TextView tv_percent1;
        final TextView tv_percent2;
        final TextView tv_percent3;
        final TextView tv_percent4;

        final TextView votedtv;
        final TextView pText;
        final TextView likeNo;
        final TextView commentNo;
        final TextView views;

        final RelativeLayout like;
        final RelativeLayout comment;
        final RelativeLayout view_ly;
        final RelativeLayout video_share;
        public Context context;

        final ImageView pause;
        final ProgressBar load;
        final ConstraintLayout constraintLayout10;
        final ConstraintLayout new5;
        final ConstraintLayout new6;
        final ConstraintLayout new7;
        final ConstraintLayout new8;
        final ConstraintLayout viewlt;
        final LinearLayout linlayout2;
        final ConstraintLayout voted;
        final ConstraintLayout topcons;
        final RelativeLayout cons1;
        final RelativeLayout lastlayout;
        final ConstraintLayout mainlayout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            pDp = itemView.findViewById(R.id.circleImageView3);
            eye = itemView.findViewById(R.id.eye);
            pMeme = itemView.findViewById(R.id.imageView2);
            mainlayout = itemView.findViewById(R.id.mainlayout);
            name = itemView.findViewById(R.id.name);
            verified = itemView.findViewById(R.id.verified);
            username = itemView.findViewById(R.id.username);
            time = itemView.findViewById(R.id.time);
            updated = itemView.findViewById(R.id.updated);
            location = itemView.findViewById(R.id.location);


            likeNo = itemView.findViewById(R.id.likeNo);
            commentNo = itemView.findViewById(R.id.commentNo);
            load = itemView.findViewById(R.id.load);
            views = itemView.findViewById(R.id.views);
            view_ly = itemView.findViewById(R.id.view_ly);
            more = itemView.findViewById(R.id.more);
            pause = itemView.findViewById(R.id.exomedia_controls_play_pause_btn);
            like_img = itemView.findViewById(R.id.like_img);
            pText = itemView.findViewById(R.id.textView2);
            viewlt = itemView.findViewById(R.id.viewlt);
            like = itemView.findViewById(R.id.relativeLayout);
            comment = itemView.findViewById(R.id.relativeLayout6);

            video_share = itemView.findViewById(R.id.vine_share);
            constraintLayout10 = itemView.findViewById(R.id.constraintLayout10);


            ttv = itemView.findViewById(R.id.titleTv);
            ima1 = itemView.findViewById(R.id.ima1);
            imb2 = itemView.findViewById(R.id.imb2);
            imc3 = itemView.findViewById(R.id.imc3);
            imd4 = itemView.findViewById(R.id.imd4);

            aedtext = itemView.findViewById(R.id.aedtext);
            bedtext = itemView.findViewById(R.id.bedtext);
            cedtext = itemView.findViewById(R.id.cedtext);
            dedtext = itemView.findViewById(R.id.dedtext);


            tv_percent1 = itemView.findViewById(R.id.tv_percent1);
            tv_percent2 = itemView.findViewById(R.id.tv_percent2);
            tv_percent3 = itemView.findViewById(R.id.tv_percent3);
            tv_percent4 = itemView.findViewById(R.id.tv_percent4);

            seek_bar1 = itemView.findViewById(R.id.seek_bar1);
            seek_bar2 = itemView.findViewById(R.id.seek_bar2);
            seek_bar3 = itemView.findViewById(R.id.seek_bar3);
            seek_bar4 = itemView.findViewById(R.id.seek_bar4);


            new5 = itemView.findViewById(R.id.new5);
            new6 = itemView.findViewById(R.id.new6);
            new7 = itemView.findViewById(R.id.new7);
            new8 = itemView.findViewById(R.id.new8);

            cons1 = itemView.findViewById(R.id.relativeLayout7);
            totalvotes = itemView.findViewById(R.id.totalvotes);
            remain = itemView.findViewById(R.id.remain);
            imtop = itemView.findViewById(R.id.imtop);
            linlayout2 = itemView.findViewById(R.id.linLayout2);
            voted = itemView.findViewById(R.id.voted);
            topcons = itemView.findViewById(R.id.topcons);
            votedtv = itemView.findViewById(R.id.votedtv);
            lastlayout = itemView.findViewById(R.id.lastlayout);

        }

    }

}








