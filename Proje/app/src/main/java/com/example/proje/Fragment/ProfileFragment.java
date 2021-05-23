package com.example.proje.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proje.EditProfileActivity;
import com.example.proje.LogoutActivity;
import com.example.proje.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import Model.Adapter.PostAdapter;
import Model.Post;
import Model.User;


public class ProfileFragment extends Fragment {
    LinearLayout l_bar;
    ImageView image_profile,options;
    TextView posts,followers,following,fullname,bio,username;
    Button edit_profile;
    FirebaseUser firebaseUser;
   String profileid;
    int i=0,j=0,z=0;
RecyclerView recyclerView;
PostAdapter fotoAdapter;
List<Post> postList;
Button film,dizi,kitap;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid=prefs.getString("profileid","none");
        image_profile=view.findViewById(R.id.image_profile);
        options=view.findViewById(R.id.options);
        followers=view.findViewById(R.id.followers);
        following=view.findViewById(R.id.following);
        fullname=view.findViewById(R.id.fullname_sss);
        bio=view.findViewById(R.id.bio);
        username=view.findViewById(R.id.username);
        posts=view.findViewById(R.id. posts);
        edit_profile=view.findViewById(R.id.edit_profile);

        l_bar=view.findViewById(R.id.l_bar);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
       fotoAdapter= new PostAdapter(getContext(),postList);
        recyclerView.setAdapter(fotoAdapter);
        film=view.findViewById(R.id.film);
        dizi=view.findViewById(R.id.dizi);
        kitap=view.findViewById(R.id.kitap);


        userInfo();
        getFollowers();
      getdiziPosts();
      getfilmPosts();
      getkitapPosts();

options.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), LogoutActivity.class);
        startActivity(intent);
    }
});
      film.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Film();
          }
      });
      dizi.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Dizi();
          }
      });
      kitap.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Kitap();
          }
      });

        if (profileid.equals(firebaseUser.getUid())){
            edit_profile.setText("Profili Düzenle");
        }else {
            checkFollow();

        }

       edit_profile.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
String btn = edit_profile.getText().toString();

if (btn.equals("Profili Düzenle")){
  startActivity(new Intent(getContext(), EditProfileActivity.class));
}else if (btn.equals("Takip et")){
    FirebaseDatabase.getInstance().getReference().child("Takip").child(firebaseUser.getUid()) //Takip edildiyse takip edilenlere ekle
            .child("TakipEdilenler").child(profileid).setValue(true);

    FirebaseDatabase.getInstance().getReference().child("Takip").child(profileid)
            .child("Takipciler").child(firebaseUser.getUid()).setValue(true);
    addNotifications();
} else if (btn.equals("Takip ediliyor")){
    FirebaseDatabase.getInstance().getReference().child("Takip").child(firebaseUser.getUid()) //Takip edilmekten çıkıldıysa takip edilenlere ekle
            .child("TakipEdilenler").child(profileid).removeValue();

    FirebaseDatabase.getInstance().getReference().child("Takip").child(profileid)
            .child("Takipciler").child(firebaseUser.getUid()).removeValue();
}
           }
       });



        return view;
    }
    private void  addNotifications(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Bildirimler").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("kullaniciid",firebaseUser.getUid());
        hashMap.put("text","seni takip etmeye başladı");
        hashMap.put("incelemeid","");
        hashMap.put("ispost",false);
        reference.push().setValue(hashMap);
    }
    private void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Kullanicilar").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);
                Glide.with(getContext()).load(user.getImageurl()).into(image_profile);
                username.setText(user.getKullaniciadi());
                fullname.setText(user.getAdsoyad());
                bio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private  void checkFollow(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Takip").child(firebaseUser.getUid()).child("TakipEdilenler");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(profileid).exists()){
                    edit_profile.setText("Takip ediliyor");
                }else{
                    edit_profile.setText("Takip et");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getFollowers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Takip").child(profileid).child("Takipciler");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Takip").child(profileid).child("TakipEdilenler");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getdiziPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Paylasılan").child("Diziler");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPaylasan().equals(profileid)){
                        i++;
                    }

                }
                posts.setText(" " +(i+j+z));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getfilmPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Paylasılan").child("Filmler");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPaylasan().equals(profileid)){
                        j++;
                    }

                }
                posts.setText(" " +(i+j+z));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getkitapPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Paylasılan").child("Kitaplar");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPaylasan().equals(profileid)){
                        z++;
                    }

                }
                posts.setText(" " +(i+j+z));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void Film(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Paylasılan").child("Filmler");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPaylasan().equals(profileid)){
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                fotoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void Dizi(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Paylasılan").child("Diziler");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPaylasan().equals(profileid)){
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                fotoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void Kitap(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Paylasılan").child("Kitaplar");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPaylasan().equals(profileid)){
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                fotoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




}