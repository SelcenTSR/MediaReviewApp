package Model.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proje.R;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import Model.Post;

public class FotoAdapter extends RecyclerView.Adapter<FotoAdapter.ViewHolder> {
    private Context context;
    private List<Post> mPost;
    public FirebaseUser firebaseUser;

    public FotoAdapter(Context context, List<Post> mPost) {
        this.context = context;
        this.mPost = mPost;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.post_item,parent,false);

        return new FotoAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = mPost.get(position);
        Glide.with(context).load(post.getGonderiimage()).into(holder.post_image);



    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView post_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            post_image=itemView.findViewById(R.id.post_image);
        }
    }
}
