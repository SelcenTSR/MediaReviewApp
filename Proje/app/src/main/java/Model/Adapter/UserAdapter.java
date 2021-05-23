package Model.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proje.Fragment.ProfileFragment;
import com.example.proje.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;

    private FirebaseUser firebaseUser;

    public UserAdapter(Context mContext, List<User> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,viewGroup,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final User user = mUsers.get(i);
        viewHolder.btn_follow.setVisibility(View.VISIBLE);
        viewHolder.adsoyad.setText(user.getAdsoyad());
        viewHolder.kullaniciadi.setText(user.getKullaniciadi());
        Glide.with(mContext).load(user.getImageurl()).into(viewHolder.image_profile);

        isFollowing(user.getId(),viewHolder.btn_follow);

        if (user.getId().equals(firebaseUser.getUid())){ // kendisi ise follow butonunu göstermemesi için
            viewHolder.btn_follow.setVisibility(View.GONE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor= mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",user.getId());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();

            }
        });

        viewHolder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.btn_follow.getText().toString().equals("Takip et")){
                    FirebaseDatabase.getInstance().getReference().child("Takip").child(firebaseUser.getUid()) //Takip edildiyse takip edilenlere ekle
                            .child("TakipEdilenler").child(user.getId()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Takip").child(user.getId())
                            .child("Takipciler").child(firebaseUser.getUid()).setValue(true);
                    addNotifications(user.getId());
                }
                else{

                    FirebaseDatabase.getInstance().getReference().child("Takip").child(firebaseUser.getUid())
                            .child("TakipEdilenler").child(user.getId()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Takip").child(user.getId())
                            .child("Takipciler").child(firebaseUser.getUid()).removeValue();

                }
            }
        });

    }
    private void  addNotifications(String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Bildirimler").child(userid);
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("kullaniciid",firebaseUser.getUid());
        hashMap.put("text","seni takip etmeye başladı");
        hashMap.put("incelemeid","");
        hashMap.put("ispost",false);
        reference.push().setValue(hashMap);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView kullaniciadi,adsoyad;
        public CircleImageView image_profile;
        Button btn_follow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            kullaniciadi=itemView.findViewById(R.id.username);
            adsoyad=itemView.findViewById(R.id.fullname);
            image_profile=itemView.findViewById(R.id.image_profile);
            btn_follow=itemView.findViewById(R.id.button_follow);

        }
    }
    public void isFollowing(String userid,Button button){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Takip").child(firebaseUser.getUid()).child("TakipEdilenler");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(userid).exists()){
                    button.setText("Takip ediliyor");
                }else{
                    button.setText("Takip et");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
