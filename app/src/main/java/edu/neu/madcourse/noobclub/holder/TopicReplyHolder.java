package edu.neu.madcourse.noobclub.holder;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import edu.neu.madcourse.noobclub.R;
import edu.neu.madcourse.noobclub.base.RecyclerAdapter;
import edu.neu.madcourse.noobclub.entity.TopicReply;
import edu.neu.madcourse.noobclub.utils.DateUtils;


public class TopicReplyHolder extends RecyclerAdapter.ViewHolder<TopicReply>{

    TextView contentTv;
    TextView creatorNameTv;
    TextView createTimeTv;
    ImageView avatarIv;

    public TopicReplyHolder(View itemView) {
        super(itemView);
        contentTv = itemView.findViewById(R.id.contentTv);
        creatorNameTv = itemView.findViewById(R.id.creatorNameTv);
        createTimeTv = itemView.findViewById(R.id.createTimeTv);
        avatarIv = itemView.findViewById(R.id.avatarIv);
    }

    @Override
    protected void onBind(TopicReply reply)
    {
        creatorNameTv.setText(reply.creatorName);
        contentTv.setText(reply.content);
        createTimeTv.setText(DateUtils.formatDate(reply.createTime));
        try{
            Drawable drawableLeft = avatarIv.getContext().getDrawable(reply.crateAvatar);
            if(drawableLeft != null) {
                avatarIv.setImageDrawable(drawableLeft);
            }
        }catch (Exception e){
            avatarIv.setImageResource(R.drawable.ic_avatar);
        }
    }
}