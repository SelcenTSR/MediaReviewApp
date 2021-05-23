package Model.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proje.CommentsActivity;
import com.example.proje.Fragment.ProfileFragment;
import com.example.proje.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import Model.Post;
import Model.User;

  public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
      public Context mContext;
      public List<Post> mPost;

      public FirebaseUser firebaseUser;

      public PostAdapter(Context mContext, List<Post> mPost) {
          this.mContext = mContext;
          this.mPost = mPost;
      }

      @NonNull
      @Override
      public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
          View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, viewGroup, false);

          return new PostAdapter.ViewHolder(view);

      }

      @Override
      public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

          firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

          Post post = mPost.get(i);
          Glide.with(mContext).load(post.getGonderiimage()).into(viewHolder.gonderiimage);

          if (post.getInceleme().equals("")) {
              viewHolder.inceleme.setVisibility(View.GONE);
          } else {
              viewHolder.inceleme.setVisibility(View.VISIBLE);
              viewHolder.inceleme.setText(post.getInceleme());
          }
          publisherInfo(viewHolder.image_profile, viewHolder.username, viewHolder.paylasan, post.getPaylasan());
          Liked(post.getGonderiid(), viewHolder.like);
          likeSayısı(viewHolder.likes, post.getGonderiid());
          Disliked(post.getGonderiid(), viewHolder.dislike);
          dislikeSayisi(viewHolder.dislikes, post.getGonderiid());
          Comments(post.getGonderiid(), viewHolder.comments);


          viewHolder.image_profile.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                  editor.putString("profileid", post.getPaylasan());
                  editor.apply();
                  ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
              }
          });
          viewHolder.username.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                  editor.putString("profileid", post.getPaylasan());
                  editor.apply();
                  ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
              }
          });


          viewHolder.dislike.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  if (viewHolder.dislike.getTag().equals("dislike")) {
                      FirebaseDatabase.getInstance().getReference().child("Dislikes").child(post.getGonderiid())
                              .child(firebaseUser.getUid()).setValue(true);
                      addDislikeNotifications(post.getPaylasan(), post.getGonderiid());
                  } else {
                      FirebaseDatabase.getInstance().getReference().child("Dislikes").child(post.getGonderiid())
                              .child(firebaseUser.getUid()).removeValue();
                  }
              }
          });
          viewHolder.like.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  if (viewHolder.like.getTag().equals("like")) {
                      FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getGonderiid())
                              .child(firebaseUser.getUid()).setValue(true);
                      addNotifications(post.getPaylasan(), post.getGonderiid());
                  } else {
                      FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getGonderiid())
                              .child(firebaseUser.getUid()).removeValue();
                  }
              }
          });
          viewHolder.comment.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Intent intent = new Intent(mContext, CommentsActivity.class);
                  intent.putExtra("gonderiid", post.getGonderiid());
                  intent.putExtra("gonderenid", post.getPaylasan());
                  mContext.startActivity(intent);
              }
          });
          viewHolder.comments.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Intent intent = new Intent(mContext, CommentsActivity.class);
                  intent.putExtra("gonderiid", post.getGonderiid());
                  intent.putExtra("gonderenid", post.getPaylasan());
                  mContext.startActivity(intent);
              }
          });

          viewHolder.more.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  PopupMenu popupMenu = new PopupMenu(mContext, view);
                  popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                      @Override
                      public boolean onMenuItemClick(MenuItem menuItem) {

                          switch (menuItem.getItemId()){
                              case R.id.edit:
                                  editPost(post.getGonderiid());
                                  return true;
                              case R.id.delete:
                                  final String id = post.getGonderiid();
                                  FirebaseDatabase.getInstance().getReference("Paylasılan").child("Filmler")
                                          .child(post.getGonderiid()).removeValue()
                                          .addOnCompleteListener(new OnCompleteListener<Void>() {
                                              @Override
                                              public void onComplete(@NonNull Task<Void> task) {
                                                  if (task.isSuccessful()){
                                                      deleteNotifications(id, firebaseUser.getUid());
                                                  }
                                              }
                                          });
                                  return true;

                              default:
                                  return false;
                          }
                      }
                  });
                  popupMenu.inflate(R.menu.post_menu);
                  if (!post.getPaylasan().equals(firebaseUser.getUid())){
                      popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                      popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                  }
                  popupMenu.show();
              }
          });
         /* viewHolder.more.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  PopupMenu popupMenu = new PopupMenu(mContext, view);
                  popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                      @Override
                      public boolean onMenuItemClick(MenuItem menuItem) {

                          switch (menuItem.getItemId()){
                              case R.id.edit:
                                  editPost1(post.getGonderiid());
                                  return true;
                              case R.id.delete:
                                  final String id = post.getGonderiid();
                                  FirebaseDatabase.getInstance().getReference("Paylasılan").child("Diziler")
                                          .child(post.getGonderiid()).removeValue()
                                          .addOnCompleteListener(new OnCompleteListener<Void>() {
                                              @Override
                                              public void onComplete(@NonNull Task<Void> task) {
                                                  if (task.isSuccessful()){
                                                      deleteNotifications(id, firebaseUser.getUid());
                                                  }
                                              }
                                          });
                                  return true;

                              default:
                                  return false;
                          }
                      }
                  });
                  popupMenu.inflate(R.menu.post_menu);
                  if (!post.getPaylasan().equals(firebaseUser.getUid())){
                      popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                      popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                  }
                  popupMenu.show();
              }
          });
      /*    viewHolder.more.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  PopupMenu popupMenu = new PopupMenu(mContext, view);
                  popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                      @Override
                      public boolean onMenuItemClick(MenuItem menuItem) {

                          switch (menuItem.getItemId()){
                              case R.id.edit:
                                  editPost2(post.getGonderiid());
                                  return true;
                              case R.id.delete:
                                  final String id = post.getGonderiid();
                                  FirebaseDatabase.getInstance().getReference("Paylasılan").child("Kitaplar")
                                          .child(post.getGonderiid()).removeValue()
                                          .addOnCompleteListener(new OnCompleteListener<Void>() {
                                              @Override
                                              public void onComplete(@NonNull Task<Void> task) {
                                                  if (task.isSuccessful()){
                                                      deleteNotifications(id, firebaseUser.getUid());
                                                  }
                                              }
                                          });
                                  return true;

                              default:
                                  return false;
                          }
                      }
                  });
                  popupMenu.inflate(R.menu.post_menu);
                  if (!post.getPaylasan().equals(firebaseUser.getUid())){
                      popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                      popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                  }
                  popupMenu.show();
              }
          });*/
      }



              @Override
      public int getItemCount() {
          return mPost.size();
      }

      public static class ViewHolder extends RecyclerView.ViewHolder {
          public ImageView image_profile, gonderiimage, like, dislike, comment, more;
          public TextView username, likes, dislikes, paylasan, inceleme, comments;

          public ViewHolder(@NonNull View itemView) {
              super(itemView);
              image_profile = itemView.findViewById(R.id.image_profile);
              gonderiimage = itemView.findViewById(R.id.post_image);
              like = itemView.findViewById(R.id.like);
              dislike = itemView.findViewById(R.id.dislike);
              comment = itemView.findViewById(R.id.comment);
              more = itemView.findViewById(R.id.more);
              username = itemView.findViewById(R.id.username);
              likes = itemView.findViewById(R.id.likes);
              dislikes = itemView.findViewById(R.id.dislikes);
              paylasan = itemView.findViewById(R.id.publishers);
              inceleme = itemView.findViewById(R.id.description);
              comments = itemView.findViewById(R.id.comments);

          }
      }

      private void Comments(String gonderid, TextView yorumlar) {
          DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Yorumlar").child(gonderid);
          reference.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                  yorumlar.setText(snapshot.getChildrenCount() + " yorumun hepsini gör. ");
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {

              }
          });
      }

      private void Disliked(String gonderiid, ImageView imageView) {
          FirebaseUser mevcutKullanici = FirebaseAuth.getInstance().getCurrentUser();
          DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Dislikes").child(gonderiid);
          reference.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                  if (snapshot.child(mevcutKullanici.getUid()).exists()) {
                      imageView.setImageResource(R.drawable.ic_disliked);
                      imageView.setTag("disliked");

                  } else {
                      imageView.setImageResource(R.drawable.ic_dislike);
                      imageView.setTag("dislike");
                  }

              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {

              }
          });
      }

      private void addDislikeNotifications(String userid, String postid) {
          DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Bildirimler").child(firebaseUser.getUid());
          HashMap<String, Object> hashMap = new HashMap<>();
          hashMap.put("kullaniciid", userid);
          hashMap.put("text", "incelemeni beğenmedi");
          hashMap.put("incelemeid", postid);
          hashMap.put("ispost", true);
          reference.push().setValue(hashMap);
      }

      private void addNotifications(String userid, String postid) {
          DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Bildirimler").child(firebaseUser.getUid());
          HashMap<String, Object> hashMap = new HashMap<>();
          hashMap.put("kullaniciid", userid);
          hashMap.put("text", "incelemeni beğendi");
          hashMap.put("incelemeid", postid);
          hashMap.put("ispost", true);
          reference.push().setValue(hashMap);
      }
      private void deleteNotifications(String postid, String uid) {
          DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(uid);
          reference.addListenerForSingleValueEvent(new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                  for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                      if (snapshot.child("gonderiid").getValue().equals(postid)) {
                          snapshot.getRef().removeValue()
                                  .addOnCompleteListener(new OnCompleteListener<Void>() {
                                      @Override
                                      public void onComplete(@NonNull Task<Void> task) {
                                          Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                                      }
                                  });
                      }
                  }}

              @Override
              public void onCancelled(@NonNull  DatabaseError error) {

              }  });}

      private void dislikeSayisi(TextView dislikes, String gonderiid) {
          DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Dislikes").child(gonderiid);
          reference.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                  dislikes.setText(snapshot.getChildrenCount() + " ");
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {

              }
          });

      }

      private void Liked(String gonderiid, ImageView imageView) {
          FirebaseUser mevcutKullanici = FirebaseAuth.getInstance().getCurrentUser();
          DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(gonderiid);
          reference.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                  if (snapshot.child(mevcutKullanici.getUid()).exists()) {
                      imageView.setImageResource(R.drawable.ic_liked);
                      imageView.setTag("liked");

                  } else {
                      imageView.setImageResource(R.drawable.ic_like);
                      imageView.setTag("like");
                  }

              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {

              }
          });
      }

      private void likeSayısı(TextView likes, String gonderiid) {
          DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(gonderiid);
          reference.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                  likes.setText(snapshot.getChildrenCount() + " ");
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {

              }
          });
      }

      private void publisherInfo(ImageView image_profile, TextView username, TextView paylasan, String kullaniciid) {
          DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Kullanicilar").child(kullaniciid);
          reference.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  User user = dataSnapshot.getValue(User.class);
                  Glide.with(mContext).load(user.getImageurl()).into(image_profile);
                  username.setText(user.getKullaniciadi());
                  paylasan.setText(user.getKullaniciadi());


              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {

              }
          });
      }

      private void editPost(String postid) {
          AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
          alertDialog.setTitle("İncelemeyi Düzenle");

          final EditText editText = new EditText(mContext);
          LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                  LinearLayout.LayoutParams.MATCH_PARENT,
                  LinearLayout.LayoutParams.MATCH_PARENT);
          editText.setLayoutParams(lp);
          alertDialog.setView(editText);

          getText(postid, editText);

          alertDialog.setPositiveButton("Düzenle",
                  new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialogInterface, int i) {

                          HashMap<String, Object> hashMap = new HashMap<>();
                          hashMap.put("inceleme", editText.getText().toString());

                          FirebaseDatabase.getInstance().getReference("Paylasılan").child("Filmler")
                                  .child(postid).updateChildren(hashMap);
                      }
                  });
          alertDialog.setNegativeButton("İptal",
                  new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialogInterface, int i) {
                          dialogInterface.cancel();
                      }
                  });
          alertDialog.show();
      }

      private void getText(String postid, final EditText editText) {
          DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Paylasılan").child("Filmler")
                  .child(postid);
          reference.addListenerForSingleValueEvent(new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                  editText.setText(dataSnapshot.getValue(Post.class).getInceleme());
              }

              @Override
              public void onCancelled(DatabaseError databaseError) {

              }
          });

      }
      private void editPost1(String postid) {
          AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
          alertDialog.setTitle("İncelemeyi Düzenle");

          final EditText editText = new EditText(mContext);
          LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                  LinearLayout.LayoutParams.MATCH_PARENT,
                  LinearLayout.LayoutParams.MATCH_PARENT);
          editText.setLayoutParams(lp);
          alertDialog.setView(editText);

          getText1(postid, editText);

          alertDialog.setPositiveButton("Düzenle",
                  new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialogInterface, int i) {

                          HashMap<String, Object> hashMap = new HashMap<>();
                          hashMap.put("inceleme", editText.getText().toString());

                          FirebaseDatabase.getInstance().getReference("Paylasılan").child("Diziler")
                                  .child(postid).updateChildren(hashMap);
                      }
                  });
          alertDialog.setNegativeButton("İptal",
                  new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialogInterface, int i) {
                          dialogInterface.cancel();
                      }
                  });
          alertDialog.show();
      }

      private void getText1(String postid, final EditText editText) {
          DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Paylasılan").child("Diziler")
                  .child(postid);
          reference.addListenerForSingleValueEvent(new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                  editText.setText(dataSnapshot.getValue(Post.class).getInceleme());
              }

              @Override
              public void onCancelled(DatabaseError databaseError) {

              }
          });

      }
      private void editPost2(String postid) {
          AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
          alertDialog.setTitle("İncelemeyi Düzenle");

          final EditText editText = new EditText(mContext);
          LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                  LinearLayout.LayoutParams.MATCH_PARENT,
                  LinearLayout.LayoutParams.MATCH_PARENT);
          editText.setLayoutParams(lp);
          alertDialog.setView(editText);

          getText2(postid, editText);

          alertDialog.setPositiveButton("Düzenle",
                  new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialogInterface, int i) {

                          HashMap<String, Object> hashMap = new HashMap<>();
                          hashMap.put("inceleme", editText.getText().toString());

                          FirebaseDatabase.getInstance().getReference("Paylasılan").child("Kitaplar")
                                  .child(postid).updateChildren(hashMap);
                      }
                  });
          alertDialog.setNegativeButton("İptal",
                  new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialogInterface, int i) {
                          dialogInterface.cancel();
                      }
                  });
          alertDialog.show();
      }

      private void getText2(String postid, final EditText editText) {
          DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Paylasılan").child("Kitaplar")
                  .child(postid);
          reference.addListenerForSingleValueEvent(new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                   editText.setText(dataSnapshot.getValue(Post.class).getInceleme());
              }

              @Override
              public void onCancelled(DatabaseError databaseError) {

              }
          });

      }
  }
