package com.example.heng.jredu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.heng.jredu.R;
import com.example.heng.jredu.entity.UserCollect;
import com.example.heng.jredu.util.ImageLoaderUtil;
import com.example.heng.jredu.util.UrlUtil;

import java.util.List;

/**
 * Created by heng on 2015/11/17.
 */
public class UserCollectAdapter extends BaseAdapter {
    private Context context;
    private List<UserCollect> myDate;
    private ImageLoaderUtil ilu = new ImageLoaderUtil();

    public UserCollectAdapter(Context context, List<UserCollect> myDate) {
        this.context = context;
        this.myDate = myDate;
    }

    @Override
    public int getCount() {
        return myDate.size();
    }

    @Override
    public Object getItem(int position) {
        return myDate.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_vedio, null);
            vh.vedioName = (TextView) convertView.findViewById(R.id.vedioName);
            vh.pubDate = (TextView) convertView.findViewById(R.id.pubDate);
            vh.image = (ImageView) convertView.findViewById(R.id.img);
            convertView.setTag(vh);

        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        UserCollect userCollect = myDate.get(position);
        ilu.display(UrlUtil.BASE_URL + userCollect.getVedioima(), vh.image);
        vh.vedioName.setText(userCollect.getRemark());
        vh.pubDate.setText(userCollect.getDate());

        return convertView;
    }

    public class ViewHolder {
        public TextView vedioName;
        public TextView pubDate;
        public ImageView image;
    }


}
