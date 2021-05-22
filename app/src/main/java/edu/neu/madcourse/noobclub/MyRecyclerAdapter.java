package edu.neu.madcourse.noobclub;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

import edu.neu.madcourse.noobclub.entity.GamePostTopic;
import edu.neu.madcourse.noobclub.utils.DateUtils;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.PostCardViewHolder> {
    private ArrayList<GamePostTopic> posts;
    private onPostCardClickListener listener;

    public MyRecyclerAdapter(@NonNull ArrayList<GamePostTopic> posts) {
        this.posts = posts;
    }

    public void setOnPostCardListener(onPostCardClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public PostCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.post_card, parent, false);
        return new PostCardViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull PostCardViewHolder holder, int position) {
        GamePostTopic currentPost = posts.get(position);
        try{
            Drawable drawable = holder.avatar.getContext().getDrawable(currentPost.avatar);
            if(drawable != null) {
                holder.avatar.setImageDrawable(drawable);
            }
        }catch (Exception e){
            holder.avatar.setImageResource(R.drawable.ic_match);
        }

        holder.postTitle.setText(currentPost.title);
        holder.postAuthor.setText(currentPost.creatorName);
        holder.postGameType.setText(currentPost.gameName);
        holder.postCreatedTime.setText(DateUtils.formPostCreateTime(currentPost.createTime));
        holder.postNumReply.setText(String.valueOf(currentPost.replyCount));

    }


    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class PostCardViewHolder extends RecyclerView.ViewHolder{
        public ShapeableImageView avatar;
        public TextView postTitle;
        public TextView postAuthor;
        public TextView postGameType;
        public TextView postCreatedTime;
        public TextView postNumReply;

        public PostCardViewHolder(@NonNull View itemView, onPostCardClickListener listener) {
            super(itemView);

            avatar = itemView.findViewById(R.id.postCardAvatar);
            postTitle = itemView.findViewById(R.id.postCardTitle);
            postAuthor = itemView.findViewById(R.id.postCardAuthor);
            postGameType = itemView.findViewById(R.id.postCardGameType);
            postCreatedTime = itemView.findViewById(R.id.postCardCreatedTime);
            postNumReply = itemView.findViewById(R.id.postCardNumReply);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onPostCardClicked(position);
                        }
                    }
                }
            });
        }
    }

    public interface onPostCardClickListener {
        void onPostCardClicked(int position);
    }
}
