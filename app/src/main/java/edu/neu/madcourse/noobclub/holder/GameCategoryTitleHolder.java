package edu.neu.madcourse.noobclub.holder;

import android.view.View;
import android.widget.TextView;

import edu.neu.madcourse.noobclub.R;
import edu.neu.madcourse.noobclub.base.RecyclerAdapter;
import edu.neu.madcourse.noobclub.entity.GameCategory;


public class GameCategoryTitleHolder extends RecyclerAdapter.ViewHolder<GameCategory>{

    TextView firstCharTv;

    public GameCategoryTitleHolder(View itemView) {
        super(itemView);
        firstCharTv = itemView.findViewById(R.id.firstCharTv);
    }

    @Override
    protected void onBind(GameCategory messageInfo)
    {
        firstCharTv.setText(messageInfo.firstChar);
    }
}
