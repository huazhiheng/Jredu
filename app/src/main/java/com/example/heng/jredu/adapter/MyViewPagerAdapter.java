package com.example.heng.jredu.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.heng.jredu.entity.Vedio;
import com.example.heng.jredu.mediedictionarys.playerlibrary.PlayerActivity;

import java.util.List;

/**
 * Created by heng on 2015/11/17.
 */
public class MyViewPagerAdapter extends PagerAdapter {
    private List<ImageView> myDate;
    private List<Vedio> mydata;
    private Context context;

    public MyViewPagerAdapter(Context context, List<ImageView> myDate, List<Vedio> mydate) {
        this.context = context;
        this.myDate = myDate;
        this.mydata = mydate;
    }

    @Override
    public int getCount() {
        return myDate.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(myDate.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView imageV = myDate.get(position);

        imageV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, PlayerActivity.class);
                intent.putExtra("vedioName", mydata.get(position).getVedioName());
                intent.putExtra("Vedio", mydata.get(position));
                intent.putExtra("flag", 1);
                context.startActivity(intent);
            }
        });

        container.addView(imageV);
        imageV.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageV;
    }

}