package com.example.heng.jredu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.heng.jredu.R;
import com.example.heng.jredu.entity.ClassProj;
import com.example.heng.jredu.util.ImageLoaderUtil;
import com.example.heng.jredu.util.UrlUtil;

import java.util.List;

/**
 * Created by heng on 2015/11/17.
 */
public class ClassProjAdapter extends BaseAdapter {
    private Context context;
    private List<ClassProj> myDate;
    private ImageLoaderUtil ilu = new ImageLoaderUtil();

    public ClassProjAdapter(Context context, List<ClassProj> myDate) {
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
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_classproj, null);
            vh.text_classproj = (TextView) convertView.findViewById(R.id.text_classproj);
            vh.vedioCt = (TextView) convertView.findViewById(R.id.vedioCt);
            vh.image_classproj = (ImageView) convertView.findViewById(R.id.image_classproj);
            convertView.setTag(vh);

        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        ClassProj classProj = myDate.get(position);
        ilu.display(UrlUtil.BASE_URL + classProj.getPhotoUri(), vh.image_classproj);
        vh.text_classproj.setText(classProj.getRemark());
        vh.vedioCt.setText("(" + classProj.getVedioCt() + ")");

        return convertView;
    }

    public class ViewHolder {
        public TextView text_classproj;
        public ImageView image_classproj;
        public TextView vedioCt;

    }

}
