package com.phigital.ai;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.phigital.ai.Adapter.AdapterPost;
import com.phigital.ai.Adapter.AdapterPost2;
import com.phigital.ai.Adapter.NewAdapter;
import com.phigital.ai.Adapter.Users;
import com.phigital.ai.Model.ModelPost;

import java.util.ArrayList;
import java.util.List;

public class UserFragment extends Fragment {

    View UserFragment;
    RecyclerView recycler_view_user;
    //   LinearLayoutManager manager;    //for linear layout
    AdapterPost2 adapter;
    String last_key="",last_node="";
    boolean isMaxData=false,isScrolling=false;
    int ITEM_LOAD_COUNT= 10;
    ProgressBar progressBar;
    NestedScrollView cv;

    int currentitems,tottalitems,scrolledoutitems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        UserFragment=inflater.inflate(R.layout.usersfragment,container,false);

        recycler_view_user=UserFragment.findViewById(R.id.recycler_view_user);
        progressBar=UserFragment.findViewById(R.id.progressBar1);
        cv=UserFragment.findViewById(R.id.cv);
        getLastKeyFromFirebase(); //43

        //   manager=new LinearLayoutManager(getContext());

//       GridLayoutManager manager = new GridLayoutManager(getContext(),2);   //for grid layout
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
//        manager.setStackFromEnd(true);
//        manager.setReverseLayout(true);

        adapter=new AdapterPost2(getContext());

        recycler_view_user.setAdapter(adapter);
        recycler_view_user.setLayoutManager(manager);
        getUsers();

        recycler_view_user.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling=true;
                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                currentitems=manager.getChildCount();
                tottalitems=manager.getItemCount();
                scrolledoutitems=manager.findFirstVisibleItemPosition();

                if(!isScrolling && currentitems + scrolledoutitems == tottalitems) {
                    Toast.makeText(getContext(), "fetch data", Toast.LENGTH_SHORT).show();
                    isScrolling=false;
                    //fetch data
                    progressBar.setVisibility(View.VISIBLE);
                    getUsers();
                }

            }
        });

//        cv.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
//            if(scrollY == v.getChildAt(0).getMeasuredHeight()-v.getMeasuredHeight()){
//                isScrolling=false;
//                //fetch data
//                progressBar.setVisibility(View.VISIBLE);
//                getUsers();
//                Toast.makeText(getContext(), "Load", Toast.LENGTH_SHORT).show();
//            }
//        });
        return UserFragment;
    }

    private void getUsers()  {
        if(!isMaxData) // 1st fasle
             {
            Query query;

            if (TextUtils.isEmpty(last_node)) {
                query = FirebaseDatabase.getInstance().getReference()
                        .child("Posts")
                        .orderByKey()
                        .limitToLast(ITEM_LOAD_COUNT);
            }else {
                query = FirebaseDatabase.getInstance().getReference()
                        .child("Posts")
                        .orderByKey()
                        .startAt(last_node)
                        .limitToLast(ITEM_LOAD_COUNT);
            }

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChildren()) {

                        List<ModelPost> newUsers = new ArrayList<>();
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            newUsers.add(userSnapshot.getValue(ModelPost.class));
                        }

                        last_node =newUsers.get(newUsers.size()-1).getId();    //10  if it greater than the toatal items set to visible then fetch data from server

                        if(!last_node.equals(last_key))
                            newUsers.remove(newUsers.size()-1);    // 19,19 so to remove duplicate removeone value
                        else
                            last_node="end";

                         Toast.makeText(getContext(), "last_node"+last_node, Toast.LENGTH_SHORT).show();

                        adapter.addAll(newUsers);
                        adapter.notifyDataSetChanged();

                    }
                    else   //reach to end no further child avaialable to show
                    {
                        isMaxData=true;
                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });
        }

        else
        {
            progressBar.setVisibility(View.GONE); //if data end
        }
    }

    private void getLastKeyFromFirebase(){
        Query getLastKey= FirebaseDatabase.getInstance().getReference()
                .child("Posts")
                .orderByKey()
                .limitToFirst(1);

        getLastKey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                for(DataSnapshot lastkey : snapshot.getChildren())
                   last_key=lastkey.getKey();
                   Toast.makeText(getContext(), "last_key"+last_key, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Toast.makeText(getContext(), "can not get last key", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
