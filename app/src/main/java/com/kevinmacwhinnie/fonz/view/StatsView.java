package com.kevinmacwhinnie.fonz.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kevinmacwhinnie.fonz.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StatsView extends RelativeLayout {
    @Bind(R.id.view_stats_life) TextView lifeText;
    @Bind(R.id.view_stats_score) TextView scoreText;

    //region Lifecycle

    public StatsView(@NonNull Context context) {
        this(context, null);
    }

    public StatsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_stats, this, true);
        ButterKnife.bind(this);
    }

    //endregion


    //region Attributes

    public void setLife(int life) {
        lifeText.setText(String.format("%d", life));
    }

    public void setScore(int score) {
        scoreText.setText(String.format("%d", score));
    }

    //endregion
}
