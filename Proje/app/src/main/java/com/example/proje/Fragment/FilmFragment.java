package com.example.proje.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proje.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Model.Adapter.PostAdapter;
import Model.Post;


public class FilmFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postlist;
    private List<String>  takipListesi;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       View view = inflater.inflate(R.layout.fragment_film, container, false);

       recyclerView = view.findViewById(R.id.recycler_view);
       recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        postlist = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(),postlist);
        recyclerView.setAdapter(postAdapter);

        checkFollowing();



       return view;

    }
    private void checkFollowing(){
       takipListesi = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Takip").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("TakipEdilenler");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                takipListesi.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                   takipListesi.add(snapshot.getKey());
                }
                takipListesi.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                readPosts();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void readPosts()
    {
        DatabaseReference yol = FirebaseDatabase.getInstance().getReference().child("Paylasılan").child("Filmler");

       yol.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
             postlist.clear();
             for (DataSnapshot dataSnapshot:snapshot.getChildren()){

                 Post post = dataSnapshot.getValue(Post.class);

                  for (String id:takipListesi){
                      if (post.getPaylasan().equals(id)){
                          postlist.add(post);
                      }
                  }

                 }
             postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}