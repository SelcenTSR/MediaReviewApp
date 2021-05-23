package com.example.proje;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Model.Adapter.CommentAdapter;
import Model.Comment;
import Model.User;

public class CommentsActivity extends AppCompatActivity {
    private RecyclerView comments;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    EditText edt_comment;
    ImageView profile_image;
    TextView txt_gonder;

    String gonderid,gonderenid;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        Toolbar toolbar = findViewById(R.id.commentToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Yorumlar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        profile_image = findViewById(R.id.profile_image);
        edt_comment=findViewById(R.id.yorumEkle);
        txt_gonder=findViewById(R.id.send);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        gonderid=intent.getStringExtra("gonderiid");
        gonderenid=intent.getStringExtra("gonderenid");
        comments=findViewById(R.id.comments);
        comments.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        comments.setLayoutManager(linearLayoutManager);
        commentList= new ArrayList<>();

        commentAdapter= new CommentAdapter(this,commentList,gonderid);
        comments.setAdapter(commentAdapter);
        user= FirebaseAuth.getInstance().getCurrentUser();

        txt_gonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edt_comment.getText().toString().equals("")){
                    Toast.makeText(CommentsActivity.this,"Boş yorum gönderemezsiniz",Toast.LENGTH_SHORT).show();
                }
                else {
                    yorumEkle();
                }
            }
        });
        //metot çağırma
        commentRead();
        profilImage();
    }

    private void yorumEkle() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Yorumlar").child(gonderid);
        String yorumid = reference.push().getKey();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("yorum",edt_comment.getText().toString());
        hashMap.put("gonderen",user.getUid());
        hashMap.put("yorumid",yorumid);

        reference.child(yorumid).setValue(hashMap);
        addNotifications();
        edt_comment.setText(" ");
    }
    private void  addNotifications(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Bildirimler").child(gonderenid);
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("kullaniciid",user.getUid());
        hashMap.put("text","incelemene yorum yaptı : "+ edt_comment.getText().toString());
        hashMap.put("incelemeid",gonderid);
        hashMap.put("ispost",true);
        reference.push().setValue(hashMap);
    }
    private void profilImage(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Kullanicilar").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageurl()).into(profile_image);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void commentRead(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Yorumlar").child(gonderid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    commentList.add(comment);
                }
commentAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}