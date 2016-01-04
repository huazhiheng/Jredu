package com.example.heng.jredu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.heng.jredu.R;
import com.example.heng.jredu.entity.Vedio;
import com.example.heng.jredu.util.ImageLoaderUtil;
import com.example.heng.jredu.util.UrlUtil;

import java.util.List;

/**
 * Created by heng on 2015/11/17.
 */
public class VedioAdapter extends BaseAdapter {
    private Context context;
    private List<Vedio> myDate;
    private ImageLoaderUtil ilu = new ImageLoaderUtil();

    public VedioAdapter(Context context, List<Vedio> myDate) {
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

        Vedio vedio = myDate.get(position);
        ilu.display(UrlUtil.BASE_URL+vedio.getVPickUri(), vh.image);
        vh.vedioName.setText(vedio.getVedioName());
        vh.pubDate.setText(vedio.getPubDate());

        return convertView;
    }

    public class ViewHolder {
        public TextView vedioName;
        public TextView pubDate;
        public ImageView image;

    }


}
