package com.example.heng.jredu.jredu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.heng.jredu.R;
import com.example.heng.jredu.adapter.UserCollectAdapter;
import com.example.heng.jredu.entity.UserDemo;
import com.example.heng.jredu.entity.UserCollect;
import com.example.heng.jredu.mediedictionarys.playerlibrary.PlayerActivity;
import com.example.heng.jredu.util.StringPostRequest;
import com.example.heng.jredu.util.SystemBarTintManager;
import com.example.heng.jredu.util.UrlUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class MainActivity_collect extends Activity {

    private ListView list_collect;

    private UserCollectAdapter ua;
    public static List<UserCollect> list = new ArrayList<>();
    private RequestQueue requestQueue = MyApplaction.getMyApplaction().getRequestQueue();
    UserDemo userDemo = MyApplaction.getMyApplaction().getUser();

    private View view;
    private ImageView collect_img;

    public static String vid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_collect);

//        view = LayoutInflater.from(this).inflate(R.layout.activity_player, null);
//        collect_img = (ImageView)view. findViewById(R.id.collect_img);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        list_collect = (ListView) findViewById(R.id.lv_collect);

        ua = new UserCollectAdapter(getBaseContext(), list);
        list_collect.setAdapter(ua);
        load();


        /**
         *视频播放
         */
        list_collect.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity_collect.this, PlayerActivity.class);
                intent.putExtra("url", UrlUtil.BASE_URL + list.get(position).getVedioUrl());
                intent.putExtra("vedioName", list.get(position).getRemark());
                intent.putExtra("flag", 3);

                intent.putExtra("uName", list.get(position).getUname());
                intent.putExtra("projid", list.get(position).getProjid());
                intent.putExtra("vedioid", list.get(position).getVedioid());

                startActivity(intent);

            }
        });


    }

    public void load() {
        //加载网络数据
        StringPostRequest request = new StringPostRequest(UrlUtil.COLLECT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Gson gson = new Gson();
                        ArrayList<UserCollect> data = gson.fromJson(s,
                                new TypeToken<ArrayList<UserCollect>>() {
                                }.getType());
                        if (data != null && data.size() > 0) {
                            list.clear();
                            list.addAll(data);
                            ua.notifyDataSetChanged();
                            for (int i = 0; i < list.size(); i++) {
                                vid = list.get(i).getVedioid();
                            }

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });

        request.putParams("uname", userDemo.getUname());
        request.putParams("flag", "3");
        requestQueue.add(request);

        setTranslucentStatus();
    }


    //沉浸式状态栏
    public void setTranslucentStatus() {
        SystemBarTintManager mTintManager = new SystemBarTintManager(this);
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setNavigationBarTintEnabled(true);
        mTintManager.setStatusBarTintResource(R.color.tongzhilan);

    }


}
