package Model.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proje.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import Model.Notification;
import Model.Post;
import Model.User;

 public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{
    private Context mContext;
    private List<Notification> mNotification;
     public FirebaseUser firebaseUser;

    public NotificationAdapter(Context mContext, List<Notification> mNotification) {
        this.mContext = mContext;
        this.mNotification = mNotification;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item,parent,false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Notification notification = mNotification.get(position);
       holder.text.setText(notification.getText());
       getUserInfo(holder.image_profile, holder.username, notification.getKullaniciid());



    }

    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView image_profile;
        public TextView username,text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_profile=itemView.findViewById(R.id.image_profile);
            username=itemView.findViewById(R.id.username);
            text=itemView.findViewById(R.id.text);

        }
    }
    private void getUserInfo(ImageView imageView, TextView username,String kullaniciid){
FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(kullaniciid)
     .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user.getImageurl().equals("default")) {
                imageView.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Picasso.get().load(user.getImageurl()).into(imageView);
                }
                username.setText(user.getKullaniciadi());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

     private void getPostImagedizi(final ImageView post_image, String postid){
         DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Paylasılan")
                 .child("Diziler").child(postid);

         reference.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 Post post = dataSnapshot.getValue(Post.class);
                 Glide.with(mContext).load(post.getGonderiimage()).into(post_image);
             }

             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         });
     }
     private void getPostImagekitap(final ImageView post_image, String postid){
         DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Paylasılan")
                 .child("Kitaplar").child(postid);

         reference.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 Post post = dataSnapshot.getValue(Post.class);
                 Glide.with(mContext).load(post.getGonderiimage()).into(post_image);
             }

             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         });
     }
}
