package com.example.heng.jredu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.heng.jredu.adapter.ClassProjAdapter;
import com.example.heng.jredu.adapter.MyViewPagerAdapter;
import com.example.heng.jredu.adapter.VedioAdapter;
import com.example.heng.jredu.dao.ClassProjDao;
import com.example.heng.jredu.dao.VedioDao;
import com.example.heng.jredu.entity.ClassProj;
import com.example.heng.jredu.entity.UserDemo;
import com.example.heng.jredu.entity.Vedio;
import com.example.heng.jredu.jredu.MainActivity_article;
import com.example.heng.jredu.jredu.MainActivity_chat;
import com.example.heng.jredu.jredu.MainActivity_collect;
import com.example.heng.jredu.jredu.MainActivity_download;
import com.example.heng.jredu.jredu.MainActivity_grcentre;
import com.example.heng.jredu.jredu.MainActivity_on;
import com.example.heng.jredu.jredu.MainActivity_setting;
import com.example.heng.jredu.jredu.MyApplaction;
import com.example.heng.jredu.mediedictionarys.playerlibrary.PlayerActivity;
import com.example.heng.jredu.util.ImageLoaderUtil;
import com.example.heng.jredu.util.PreLoginUtil;
import com.example.heng.jredu.util.StringPostRequest;
import com.example.heng.jredu.util.SystemBarTintManager;
import com.example.heng.jredu.util.UrlUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;

public class MainActivity extends Activity {

    private View view1;
    private View view2;
    private TextView title;
    private RelativeLayout allcourse_layout, mycourse_layout, download_layout, mymessage_layout,
            mynote_layout, article_layout, setting_layout;

    private PullToRefreshListView pull_list;    //主ListView
    private View headerView; //头布局
    private ViewPager vp;
    private MyViewPagerAdapter myViewPagerAdapter;
    private List<Vedio> list = new ArrayList<>();

    private LinearLayout line_point;//小圆点的布局
    private ImageView iv_point;//小圆点

    private ScheduledExecutorService scheduledExecutorService;
    private int currentItem;

    private VedioAdapter va;
    private List<Vedio> myData = new ArrayList<>();//首页视频
    private List<Vedio> child_list = new ArrayList<>();//点击类别之后的列表
    private List<Vedio> part_list = new ArrayList<>();//一开始加载部分列表
    private List<ClassProj> myData2 = new ArrayList<>();//右侧边栏课程类别

    private RequestQueue requestQueue = MyApplaction.getMyApplaction().getRequestQueue();

    private ClassProjAdapter ca;

    private ListView lv_rigth;

    private ImageView image_rigth;

    private ImageView head_image;//左侧边栏头像
    private TextView login_rightnow;//左侧边栏立即登录

    private int flag = 0;//表示用户还没点击侧边栏，加载视频的list为myDate

    private VedioDao vedioDao;
    private VedioDao vedioDao_pic;
    private ClassProjDao classProjDao;
    private int count = 2;

    UserDemo userDemo = MyApplaction.getMyApplaction().getUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = (TextView) findViewById(R.id.title);//标题
        view1 = LayoutInflater.from(this).inflate(R.layout.slidingmenu_left, null);//左侧边栏
        view2 = LayoutInflater.from(this).inflate(R.layout.slidingmenu_right, null);//右侧边栏

        allcourse_layout = (RelativeLayout) view1.findViewById(R.id.allcourse_layout);
        mycourse_layout = (RelativeLayout) view1.findViewById(R.id.mycourse_layout);
        article_layout = (RelativeLayout) view1.findViewById(R.id.article_layout);
        setting_layout = (RelativeLayout) view1.findViewById(R.id.setting_layout);
        mymessage_layout = (RelativeLayout) view1.findViewById(R.id.mymessage_layout);
        download_layout = (RelativeLayout) view1.findViewById(R.id.download_layout);


        head_image = (ImageView) view1.findViewById(R.id.head_image);
        login_rightnow = (TextView) view1.findViewById(R.id.login_rightnow);
        pull_list = (PullToRefreshListView) findViewById(R.id.pull_list);
        lv_rigth = (ListView) view2.findViewById(R.id.lv_right);
        image_rigth = (ImageView) findViewById(R.id.image_rigth);

        /**
         * 上拉、下拉刷新
         */
        pull_list.setMode(PullToRefreshBase.Mode.BOTH);
        pull_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                new FinishRefresh().execute();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                new UpToRefresh().execute();
            }
        });

        //头布局加入到主布局
        headerView = LayoutInflater.from(this).inflate(R.layout.main_header, null);
        pull_list.getRefreshableView().addHeaderView(headerView);
        vp = (ViewPager) headerView.findViewById(R.id.myViewPager);

        //加载顶部图片
        myViewPagerAdapter = new MyViewPagerAdapter(this, list_top, list);
        vp.setAdapter(myViewPagerAdapter);
        vp.setCurrentItem(0);
        loadViewpager();
        //图片轮换
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(new ViewPagerTask(), 3, 3, TimeUnit.SECONDS);

        //加载首页视频
        va = new VedioAdapter(getBaseContext(), part_list);
        pull_list.setAdapter(va);
        loadNews();

        //加载右侧边栏课程类别
        ca = new ClassProjAdapter(this, myData2);
        lv_rigth.setAdapter(ca);
        loadRight();

        //沉浸式状态栏
        setTranslucentStatus();

        //侧边栏
        final SlidingMenu sm = new SlidingMenu(this);
        sm.setMode(SlidingMenu.LEFT_RIGHT);
        sm.setMenu(view1);
        sm.setSecondaryMenu(view2);
        sm.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        sm.setBehindOffset(400);
        sm.setFadeEnabled(false);


        //点击图片呼出两个侧边栏
        RelativeLayout left = (RelativeLayout) findViewById(R.id.left);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sm.toggle();
            }
        });
        ImageView image_right = (ImageView) findViewById(R.id.image_rigth);
        image_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sm.showSecondaryMenu();
            }
        });

        //设置右侧边栏打开(关闭)时右上角图片改变
        sm.setSecondaryOnOpenListner(new SlidingMenu.OnOpenListener() {
            @Override
            public void onOpen() {
                image_rigth.setImageResource(R.drawable.sliding_menu_right_pressed);
            }
        });
        sm.setOnClosedListener(new SlidingMenu.OnClosedListener() {
            @Override
            public void onClosed() {
                image_rigth.setImageResource(R.drawable.sliding_menu_right);
            }

        });

        /**
         * ********************左侧边栏里的监听***************************
         */
        //显示全部课程
        allcourse_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("=====", "" + pull_list.getRefreshableView().getHeaderViewsCount());
                if (pull_list.getRefreshableView().getHeaderViewsCount() == 1) {
                    myData.clear();
                    pull_list.getRefreshableView().addHeaderView(headerView);
                    va = new VedioAdapter(getBaseContext(), part_list);
                    pull_list.setAdapter(va);
                    loadNews();
                    title.setText("全部课程");
                    sm.toggle();
                    flag = 0;
                } else {
                    sm.toggle();
                }
            }
        });
        //我的课程
        mycourse_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userDemo != null) {
                    Intent intent = new Intent(getBaseContext(), MainActivity_collect.class);
                    startActivity(intent);
                } else {
                    startActivity(new Intent(getBaseContext(), MainActivity_on.class));
                }
            }
        });
        download_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MainActivity_download.class);
                startActivity(intent);
            }
        });


        //我的消息
        mymessage_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userDemo != null) {
                    Intent intent = new Intent(getBaseContext(), MainActivity_chat.class);
                    startActivity(intent);
                } else {
                    startActivity(new Intent(getBaseContext(), MainActivity_on.class));
                }
            }
        });


        //设置
        setting_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MainActivity_setting.class);
                startActivity(intent);
            }
        });

        //立即登录
        login_rightnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userDemo == null) {
                    Intent intent = new Intent(MainActivity.this, MainActivity_on.class);
                    startActivity(intent);
                }
            }
        });

        //点击头像跳转到个人中心
        head_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userDemo != null) {
                    Intent intent = new Intent(MainActivity.this, MainActivity_grcentre.class);
                    startActivity(intent);
                } else {
                    startActivity(new Intent(MainActivity.this, MainActivity_on.class));
                }
            }
        });

        //文章
        article_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity_article.class);
                startActivity(intent);
            }
        });


        /**
         *  ********************右侧边栏里的监听***************************
         *点击课程类别，主界面显示对应的课程列表
         */
        lv_rigth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                child_list.clear();
                for (int i = 0; i < myData.size(); i++) {
                    if (myData.get(i).getProjId().equals(myData2.get(position).getProjId())) {
                        child_list.add(myData.get(i));
                    }
                }
                //置顶视频
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getProjId().equals(myData2.get(position).getProjId())) {
                        child_list.add(list.get(i));
                    }
                }
                title.setText(myData2.get(position).getRemark());
                va.notifyDataSetChanged();
                va = new VedioAdapter(MainActivity.this, child_list);
                pull_list.setAdapter(va);
                pull_list.getRefreshableView().removeHeaderView(headerView);
                sm.toggle();
                flag = 1;

            }
        });


        /**
         *视频播放
         */
        pull_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (flag == 0) {
                    Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                    intent.putExtra("vedioName", myData.get(position - 2).getVedioName());
                    intent.putExtra("Vedio", myData.get(position - 2));
                    intent.putExtra("flag", 1);

                    intent.putExtra("projid", myData.get(position - 2).getProjId());
                    intent.putExtra("vedioid", myData.get(position - 2).getVedioid());

                    startActivity(intent);

                } else if (flag == 1) {
                    Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                    intent.putExtra("vedioName", myData.get(position - 2).getVedioName());
                    intent.putExtra("Vedio", myData.get(position - 2));
                    intent.putExtra("flag", 1);

                    intent.putExtra("projid", myData.get(position - 2).getProjId());
                    intent.putExtra("vedioid", myData.get(position - 2).getVedioid());

                    startActivity(intent);

                }
            }
        });

        /**
         * *****自动登录*****
         */
        String remember_02;
        remember_02 = PreLoginUtil.gettString(getBaseContext(), "remember_02");
        if (remember_02.equals("yes")) {
            String sturl = UrlUtil.UserLogin;
            StringPostRequest stringRequest = new StringPostRequest(sturl
                    , new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    Gson gson = new Gson();
                    UserDemo user = gson.fromJson(s, UserDemo.class);
                    if (user.getUname().equals("")) {
                        Toast.makeText(getApplication(), "自动登录失败", Toast.LENGTH_LONG).show();
                    } else {
                        MyApplaction.getMyApplaction().setUser(user);
                        userDemo = MyApplaction.getMyApplaction().getUser();

                        //侧边栏显示头像、昵称
                        login_rightnow.setText(userDemo.getNickName());
                        ImageLoaderUtil.display(userDemo.getPhotoUri(), head_image);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(getApplication(), "网络连接失败", Toast.LENGTH_SHORT).show();
                }
            });
            stringRequest.putParams("uname", PreLoginUtil.gettString(getBaseContext(), "name"));
            stringRequest.putParams("upwd", PreLoginUtil.gettString(getBaseContext(), "pwd"));
            stringRequest.putParams("flag", "1");
            MyApplaction.getMyApplaction().getRequestQueue().add(stringRequest);
        }



    }


    /**
     * **********************************************************************************************
     */

    //设置图片轮换
    private class ViewPagerTask implements Runnable {
        @Override
        public void run() {
            currentItem = (currentItem + 1) % list_top.size();
            handler.obtainMessage().sendToTarget();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            vp.setCurrentItem(currentItem);
        }
    };


    /**
     * 加载头布局的图片
     */
    private List<ImageView> list_top = new ArrayList();

    public void loadViewpager() {
        //加载本地数据
        vedioDao_pic = MyApplaction.getDaoSession(getBaseContext()).getVedioDao();
        List<Vedio> localList3 = vedioDao_pic.loadAll();
        if (localList3.size() > 0) {
            list.clear();
            list.addAll(localList3);
            myViewPagerAdapter.notifyDataSetChanged();
        }

        String url = UrlUtil.TOP_URL;
        StringRequest request = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Gson gson = new Gson();
                        List<Vedio> templist = gson.fromJson(s,
                                new TypeToken<ArrayList<Vedio>>() {
                                }.getType());
                        list.clear();
                        list.addAll(templist);
                        for (int i = 0; i < list.size(); i++) {
                            ImageView image = new ImageView(getApplication());
                            ImageLoaderUtil.display(UrlUtil.BASE_URL + list.get(i).getVPickUri(), image);
                            list_top.add(image);
                            line_point = (LinearLayout) headerView.findViewById(R.id.line_point);
                            iv_point = new ImageView(getBaseContext());
                            if (i == 0) {
                                iv_point.setImageResource(R.drawable.image_indicator_focus);
                            } else {
                                iv_point.setImageResource(R.drawable.image_indicator);
                            }
                            line_point.addView(iv_point);
                        }
                        myViewPagerAdapter.notifyDataSetChanged();
                        //将下载的数据更新到本地数据库
                        vedioDao_pic.deleteAll();
                        for (int i = 0; i < list.size(); i++) {
                            vedioDao_pic.insertOrReplace(list.get(i));

                        }


                        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageScrolled(int position, float positionOffset,
                                                       int positionOffsetPixels) {
                            }

                            @Override
                            public void onPageSelected(int position) {
                                for (int i = 0; i < list_top.size(); i++) {
                                    iv_point = (ImageView) line_point.getChildAt(i);
                                    if (i == position % list_top.size()) {
                                        iv_point.setImageResource(R.drawable.image_indicator_focus);
                                    } else {
                                        iv_point.setImageResource(R.drawable.image_indicator);
                                    }
                                }
                                currentItem = position;
                            }

                            @Override
                            public void onPageScrollStateChanged(int state) {

                            }
                        });


                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }


    /**
     * 加载首页视频
     */
    public void loadNews() {
        //加载本地数据
        vedioDao = MyApplaction.getDaoSession(getBaseContext()).getVedioDao();
        List<Vedio> localList = vedioDao.loadAll();
        if (localList.size() > 0) {
            myData.clear();
            myData.addAll(localList);
            va.notifyDataSetChanged();
        }

        //加载网络数据
        StringPostRequest request = new StringPostRequest(UrlUtil.VEDIO_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        pull_list.onRefreshComplete();//收起下拉刷新
                        Gson gson = new Gson();
                        ArrayList<Vedio> data = gson.fromJson(s,
                                new TypeToken<ArrayList<Vedio>>() {
                                }.getType());
                        if (data != null && data.size() > 0) {
                            myData.clear();
                            myData.addAll(data);
                            va.notifyDataSetChanged();
                        }
                        //将下载的数据更新到本地数据库
                        vedioDao.deleteAll();
                        for (int i = 0; i < myData.size(); i++) {
                            vedioDao.insertOrReplace(myData.get(i));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pull_list.onRefreshComplete();
            }
        });

        request.putParams("top", "2");
        requestQueue.add(request);

        if (myData.size() > 10) {
            for (int i = 0; i < 10; i++) {
                part_list.add(myData.get(i));
            }
        } else {
            for (int i = 0; i < myData.size(); i++) {
                part_list.add(myData.get(i));
            }
        }
    }

    /**
     * 加载右侧边栏课程类别
     */
    public void loadRight() {
        //加载本地数据
        classProjDao = MyApplaction.getDaoSession(getBaseContext()).getClassProjDao();
        List<ClassProj> localList2 = classProjDao.loadAll();
        if (localList2.size() > 0) {
            myData2.clear();
            myData2.addAll(localList2);
            ca.notifyDataSetChanged();
        }

        //加载网络数据
        StringPostRequest request = new StringPostRequest(UrlUtil.CLASSPROJ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Gson gson = new Gson();
                        ArrayList<ClassProj> data = gson.fromJson(s,
                                new TypeToken<ArrayList<ClassProj>>() {
                                }.getType());
                        if (data != null && data.size() > 0) {
                            myData2.clear();
                            myData2.addAll(data);
                            ca.notifyDataSetChanged();
                        }
                        //将下载的数据更新到本地数据库
                        classProjDao.deleteAll();
                        for (int i = 0; i < myData2.size(); i++) {
                            classProjDao.insertOrReplace(myData2.get(i));
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        request.putParams("top", "1");
        requestQueue.add(request);


    }

    //沉浸式状态栏
    public void setTranslucentStatus() {
        SystemBarTintManager mTintManager = new SystemBarTintManager(this);
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setNavigationBarTintEnabled(true);
        mTintManager.setStatusBarTintResource(R.color.tongzhilan);

    }

    //左侧边栏显示头像、昵称
    @Override
    protected void onStart() {
        userDemo = MyApplaction.getMyApplaction().getUser();
        if (userDemo != null) {
            login_rightnow.setText(userDemo.getNickName());
            ImageLoaderUtil.display(userDemo.getPhotoUri(), head_image);
        } else {
            login_rightnow.setText("立即登录");
            head_image.setImageResource(R.drawable.head_default);

        }

        super.onStart();
    }

    //下拉刷新 加载10条数据
    private class FinishRefresh extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void resule) {
            pull_list.onRefreshComplete();
            va.notifyDataSetChanged();
            ca.notifyDataSetChanged();
            pull_list.onRefreshComplete();
            count = 2;
            part_list.clear();
            if (myData.size() > 10) {
                for (int i = 0; i < 10; i++) {
                    part_list.add(myData.get(i));
                }
            } else {
                for (int i = 0; i < myData.size(); i++) {
                    part_list.add(myData.get(i));
                }
            }
            va.notifyDataSetChanged();

        }
    }

    //上拉刷新 一次增加10条
    private class UpToRefresh extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            va.notifyDataSetChanged();
            if (flag == 0) {
                int n = (myData.size() / 10) + 1;//1是不够十个，2是十几个
                part_list.clear();
                if (count >= n) {
                    for (int i = 0; i < myData.size(); i++) {
                        part_list.add(myData.get(i));
                    }
                    Toast.makeText(getApplication(), "已经加载全部课程", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < count * 10; i++) {
                        part_list.add(myData.get(i));
                    }
                    count = count + 1;
                }
                va.notifyDataSetChanged();
            }
            pull_list.onRefreshComplete();
        }
    }

    //监听返回键退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            this.show();
            return false;
        }
        return false;
    }

    private void show() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setMessage("确定退出程序?").setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                }).create();
        alertDialog.show();
    }

    //增加统计代码
    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

}
