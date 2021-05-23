package Model.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proje.MainActivity;
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

import java.util.List;

import Model.Comment;
import Model.User;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context mcontext;
    private List<Comment> yorumList;
   private String gonderid;
    public FirebaseUser firebaseUser;

    public CommentAdapter(Context mcontext, List<Comment> yorumList, String postid) {
        this.mcontext = mcontext;
        this.yorumList = yorumList;
        this.gonderid=postid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.comment_item, parent, false);

        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Comment comment = yorumList.get(position);
        holder.yorum.setText(comment.getYorum());
        kullaniciInf(holder.profil_image,holder.kullaniciadi,comment.getGonderen());
        holder.yorum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, MainActivity.class);
                intent.putExtra("gonderenid",comment.getGonderen());
                mcontext.startActivity(intent);

            }
        });
        holder.profil_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, MainActivity.class);
                intent.putExtra("gonderenid",comment.getGonderen());
                mcontext.startActivity(intent);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (comment.getGonderen().equals(firebaseUser.getUid())){
                    AlertDialog alertDialog = new AlertDialog.Builder(mcontext).create();
                    alertDialog.setTitle("Silmek istiyor musunuz?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Hayır", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           dialog.dismiss();
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Evet", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference().child("Yorumlar").child(gonderid).child(comment.getYorumid()).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull  Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(mcontext,"Silindi",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return yorumList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView profil_image,post_image;
        public TextView kullaniciadi, yorum;

        public ViewHolder(View itemView) {
            super(itemView);
            profil_image = itemView.findViewById(R.id.profil_image);
            kullaniciadi = itemView.findViewById(R.id.kullaniciadicomment);
            yorum = itemView.findViewById(R.id.comment);


        }
    }

    private void kullaniciInf(ImageView imageView, TextView kullaniciadi, String gonderenId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(gonderenId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                Glide.with(mcontext).load(user.getImageurl()).into(imageView);
                kullaniciadi.setText(user.getKullaniciadi());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}