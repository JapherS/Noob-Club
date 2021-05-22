package edu.neu.madcourse.noobclub.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import edu.neu.madcourse.noobclub.R;
import edu.neu.madcourse.noobclub.base.RecyclerAdapter;
import edu.neu.madcourse.noobclub.entity.GameCategory;

public class GameCategoryHolder extends RecyclerAdapter.ViewHolder<GameCategory>{

    ImageView iconIv;
    TextView nameTv;

    private RequestOptions options = new RequestOptions()
        .error(R.mipmap.ic_launcher_round)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .centerCrop();

    public GameCategoryHolder(View itemView) {
        super(itemView);
        iconIv = itemView.findViewById(R.id.gameIconIv);
        nameTv = itemView.findViewById(R.id.gameNameTv);
    }

    @Override
    protected void onBind(GameCategory messageInfo) {
        nameTv.setText(messageInfo.gameName);
        Glide.with(iconIv)
                .load(messageInfo.icon)
                .apply(options)
                .into(iconIv);
    }
}
