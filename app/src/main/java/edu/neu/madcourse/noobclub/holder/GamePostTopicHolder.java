package edu.neu.madcourse.noobclub.holder;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import edu.neu.madcourse.noobclub.R;
import edu.neu.madcourse.noobclub.base.RecyclerAdapter;
import edu.neu.madcourse.noobclub.entity.GamePostTopic;
import edu.neu.madcourse.noobclub.utils.DateUtils;


public class GamePostTopicHolder extends RecyclerAdapter.ViewHolder<GamePostTopic>{

    TextView titleTv;
    TextView contentTv;
    TextView creatorNameTv;
    TextView createTimeTv;
    TextView replyCountTv;
    ImageView avatarIv;

    public GamePostTopicHolder(View itemView) {
        super(itemView);
        titleTv = itemView.findViewById(R.id.titleIv);
        contentTv = itemView.findViewById(R.id.contentIv);
        creatorNameTv = itemView.findViewById(R.id.creatorNameTv);
        replyCountTv = itemView.findViewById(R.id.replyCountTv);
        createTimeTv = itemView.findViewById(R.id.createTimeTv);
        avatarIv = itemView.findViewById(R.id.avatarIv);
    }

    @Override
    protected void onBind(GamePostTopic topic)
    {
        titleTv.setText(topic.title);
        contentTv.setText(topic.content);
        creatorNameTv.setText(topic.creatorName);
        replyCountTv.setText(String.valueOf(topic.replyCount));
        createTimeTv.setText(DateUtils.formPostCreateTime(topic.createTime));

        try{
            Drawable drawableLeft = avatarIv.getContext().getDrawable(topic.avatar);
            if(drawableLeft != null) {
                avatarIv.setImageDrawable(drawableLeft);
            }
        }catch (Exception e){
            avatarIv.setImageResource(R.drawable.ic_avatar);
        }
    }

}
