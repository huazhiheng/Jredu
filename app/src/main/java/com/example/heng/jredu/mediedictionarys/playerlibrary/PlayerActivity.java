package com.example.heng.jredu.mediedictionarys.playerlibrary;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.heng.jredu.R;
import com.example.heng.jredu.entity.UserCollect;
import com.example.heng.jredu.entity.UserDemo;
import com.example.heng.jredu.entity.Vedio;
import com.example.heng.jredu.jredu.MyApplaction;
import com.example.heng.jredu.service.DownloadService;
import com.example.heng.jredu.util.StringPostRequest;
import com.example.heng.jredu.util.UrlUtil;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlayerActivity extends Activity implements PlayerView.OnChangeListener,
        OnClickListener, OnSeekBarChangeListener, Callback {

    private static final int SHOW_PROGRESS = 0;
    private static final int ON_LOADED = 1;
    private static final int HIDE_OVERLAY = 2;

    private View rlLoading;
    private PlayerView mPlayerView;
    private String mUrl;
    private String vedioName;

    private TextView tvTitle, tvBuffer, tvTime, tvLength;
    private SeekBar sbVideo;
    private ImageButton ibLock, ibFarward, ibBackward, ibPlay, ibSize;
    private View llOverlay, rlOverlayTitle;
    private Handler mHandler;

    private ImageView collect_img, download;
    UserDemo userDemo = MyApplaction.getMyApplaction().getUser();
    private List<UserCollect> list = new ArrayList<>();
    private RequestQueue requestQueue = MyApplaction.getMyApplaction().getRequestQueue();

    private Vedio vedio;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int flag = getIntent().getIntExtra("flag", 1);
        vedio = (Vedio) getIntent().getSerializableExtra("Vedio");

        vedioName = getIntent().getStringExtra("vedioName");

        if (flag == 1) {//首页
            mUrl = UrlUtil.BASE_URL + vedio.getVUri();
        } else if (flag == 2) {//下载
            mUrl = "file://" + Environment.getExternalStorageDirectory().toString() + "/JREDU" + vedio.getVUri();
        } else if (flag == 3) {//收藏
            mUrl = getIntent().getStringExtra("url");
        }


        if (TextUtils.isEmpty(mUrl)) {
            Toast.makeText(this, "error:no url in intent!", Toast.LENGTH_SHORT).show();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_player);

        mHandler = new Handler(this);

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvLength = (TextView) findViewById(R.id.tv_length);
        sbVideo = (SeekBar) findViewById(R.id.sb_video);
        sbVideo.setOnSeekBarChangeListener(this);
        ibLock = (ImageButton) findViewById(R.id.ib_lock);
        ibLock.setOnClickListener(this);
        ibBackward = (ImageButton) findViewById(R.id.ib_backward);
        ibBackward.setOnClickListener(this);
        ibPlay = (ImageButton) findViewById(R.id.ib_play);
        ibPlay.setOnClickListener(this);
        ibFarward = (ImageButton) findViewById(R.id.ib_forward);
        ibFarward.setOnClickListener(this);
        ibSize = (ImageButton) findViewById(R.id.ib_size);
        ibSize.setOnClickListener(this);

        llOverlay = findViewById(R.id.ll_overlay);
        rlOverlayTitle = findViewById(R.id.rl_title);

        rlLoading = findViewById(R.id.rl_loading);
        tvBuffer = (TextView) findViewById(R.id.tv_buffer);

        collect_img = (ImageView) findViewById(R.id.collect_img);
        download = (ImageView) findViewById(R.id.download);
        download.setOnClickListener(this);

        //使用步骤
        //第一步 ：通过findViewById或者new PlayerView()得到mPlayerView对象
        //mPlayerView= new PlayerView(PlayerActivity.this);
        mPlayerView = (PlayerView) findViewById(R.id.pv_video);

        //第二步：设置参数，毫秒为单位
        mPlayerView.setNetWorkCache(20000);

        //第三步:初始化播放器
        mPlayerView.initPlayer(mUrl);

        //第四步:设置事件监听，监听缓冲进度等
        mPlayerView.setOnChangeListener(this);

        //第五步：开始播放
        mPlayerView.start();
        //init view
        tvTitle.setText(vedioName);
        showLoading();
        hideOverlay();


        db = this.openOrCreateDatabase("mobile_video.db", Activity.MODE_PRIVATE, null);
        String createTable =
                "CREATE TABLE IF NOT EXISTS mobile_video(_id  int NOT NULL " +
                        ",vedioid  varchar(255) NULL ,vedioName  varchar(255) NULL " +
                        ",VUri  varchar(255) NULL ,projId  varchar(255) NULL" +
                        " ,instruction  varchar(255) NULL ,author  varchar(255) NULL " +
                        ",pubDate  varchar(255) NULL ,VPickUri  varchar(255) NULL " +
                        ",flag  varchar(255) NULL ,PRIMARY KEY (_id))";
        db.execSQL(createTable);


/**
 *
 * 收藏
 */

//        Intent intent = getIntent();
//        Bundle bundle = intent.getBundleExtra("bundle");
//        vedio = (Vedio) bundle.get("vedio");
//        userCollect = (UserCollect) bundle.get("userCollect");

//        Log.d("++++", MainActivity_collect.vid);


        if (userDemo == null) {
            collect_img.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplication(), "请登录后收藏", Toast.LENGTH_SHORT).show();
                }
            });

        }
        if (userDemo != null) {
            collect_img.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    collect();
                    collect_img.setImageResource(R.drawable.collect_focused);
                    Toast.makeText(getApplication(), "收藏成功", Toast.LENGTH_SHORT).show();
                }
            });


        }

        /**
         * 分享
         */
        findViewById(R.id.share).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, mUrl);
                Intent chooserIntent = Intent.createChooser(intent, "分享");
                if (chooserIntent == null) {
                    return;
                }
                try {
                    startActivity(chooserIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getApplication(), "Can't find share component to share", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    //收藏
    private void collect() {
        String url = UrlUtil.COLLECT_URL;
        StringPostRequest str = new StringPostRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        str.putParams("uname", userDemo.getUname());
        str.putParams("enddate", "1");
        str.putParams("projid", getIntent().getStringExtra("projid"));
        str.putParams("vedioid", getIntent().getStringExtra("vedioid"));
        str.putParams("flag", "1");
        MyApplaction.getMyApplaction().getRequestQueue().add(str);
    }


    //取消收藏
    private void delete() {
        String url = UrlUtil.COLLECT_URL;
        StringPostRequest str = new StringPostRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        str.putParams("uname", userDemo.getUname());
        str.putParams("enddate", "1");
        str.putParams("projid", getIntent().getStringExtra("projid"));
        str.putParams("vedioid", getIntent().getStringExtra("vedioid"));
        str.putParams("flag", "2");
        MyApplaction.getMyApplaction().getRequestQueue().add(str);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (llOverlay.getVisibility() != View.VISIBLE) {
                showOverlay();
            } else {
                hideOverlay();
            }
        }
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mPlayerView.changeSurfaceSize();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPause() {
        hideOverlay();
        mPlayerView.stop();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBufferChanged(float buffer) {
        if (buffer >= 100) {
            hideLoading();
        } else {
            showLoading();
        }
        tvBuffer.setText("正在缓冲中..." + (int) buffer + "%");
    }

    private void showLoading() {
        rlLoading.setVisibility(View.VISIBLE);

    }

    private void hideLoading() {
        rlLoading.setVisibility(View.GONE);
    }

    @Override
    public void onLoadComplet() {
        mHandler.sendEmptyMessage(ON_LOADED);
    }

    @Override
    public void onError() {
        Toast.makeText(getApplicationContext(), "Player Error Occur！", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onEnd() {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_lock:
                break;
            case R.id.ib_forward:
                mPlayerView.seek(10000);
                break;
            case R.id.ib_play:
                if (mPlayerView.isPlaying()) {
                    mPlayerView.pause();
                    ibPlay.setBackgroundResource(R.drawable.ic_play);
                } else {
                    mPlayerView.play();
                    ibPlay.setBackgroundResource(R.drawable.ic_pause);
                }
                break;

            case R.id.ib_backward:
                mPlayerView.seek(-10000);
                break;
            case R.id.ib_size:
                break;

            //下载
            case R.id.download:
                Intent intent = new Intent(this, DownloadService.class);
                intent.putExtra("vedio", vedio);
                //加入本地数据库
                int a = 0;
                Cursor cursor = db.rawQuery("SELECT _id FROM mobile_video WHERE _id = ?"
                        , new String[]{vedio.getId().toString()});
                while (cursor.moveToNext()) {
                    if (cursor.getString(0).equals(vedio.getId().toString())) {
                        a = 1;
                    }
                }
                if (a == 0) {
                    startService(intent);
                    String insert = "INSERT INTO mobile_video (_id,vedioid,vedioName,VUri,projId"
                            + ",instruction,author,pubDate,VPickUri,flag) VALUES"
                            + "(" + vedio.getId() + ",'" + vedio.getVedioid() + "','"
                            + vedio.getVedioName() + "','" + vedio.getVUri() + "','"
                            + vedio.getProjId() + "','" + vedio.getInstruction() + "','"
                            + vedio.getAuthor() + "','" + vedio.getPubDate() + "','"
                            + vedio.getVPickUri() + "','" + vedio.getFlag() + "')";
                    db.execSQL(insert);
                    Toast.makeText(getApplicationContext(), "加入下载列表成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "已下载", Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                break;
        }
    }

    private void showOverlay() {
        rlOverlayTitle.setVisibility(View.VISIBLE);
        llOverlay.setVisibility(View.VISIBLE);
        mHandler.sendEmptyMessage(SHOW_PROGRESS);
        mHandler.removeMessages(HIDE_OVERLAY);
        mHandler.sendEmptyMessageDelayed(HIDE_OVERLAY, 5 * 1000);
    }

    private void hideOverlay() {
        rlOverlayTitle.setVisibility(View.GONE);
        llOverlay.setVisibility(View.GONE);
        mHandler.removeMessages(SHOW_PROGRESS);
    }

    private int setOverlayProgress() {
        if (mPlayerView == null) {
            return 0;
        }
        int time = (int) mPlayerView.getTime();
        int length = (int) mPlayerView.getLength();
        boolean isSeekable = mPlayerView.canSeekable() && length > 0;
        ibFarward.setVisibility(isSeekable ? View.VISIBLE : View.GONE);
        ibBackward.setVisibility(isSeekable ? View.VISIBLE : View.GONE);
        sbVideo.setMax(length);
        sbVideo.setProgress(time);
        if (time >= 0) {
            tvTime.setText(millisToString(time, false));
        }
        if (length >= 0) {
            tvLength.setText(millisToString(length, false));
        }
        return time;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser && mPlayerView.canSeekable()) {
            mPlayerView.setTime(progress);
            setOverlayProgress();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case SHOW_PROGRESS:
                setOverlayProgress();
                mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 20);
                break;
            case ON_LOADED:
                showOverlay();
                hideLoading();
                break;
            case HIDE_OVERLAY:
                hideOverlay();
                break;
            default:
                break;
        }
        return false;
    }

    private String millisToString(long millis, boolean text) {
        boolean negative = millis < 0;
        millis = Math.abs(millis);
        int mini_sec = (int) millis % 1000;
        millis /= 1000;
        int sec = (int) (millis % 60);
        millis /= 60;
        int min = (int) (millis % 60);
        millis /= 60;
        int hours = (int) millis;

        String time;
        DecimalFormat format = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        format.applyPattern("00");

        DecimalFormat format2 = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        format2.applyPattern("000");
        if (text) {
            if (millis > 0)
                time = (negative ? "-" : "") + hours + "h" + format.format(min) + "min";
            else if (min > 0)
                time = (negative ? "-" : "") + min + "min";
            else
                time = (negative ? "-" : "") + sec + "s";
        } else {
            if (millis > 0)
                time = (negative ? "-" : "") + hours + ":" + format.format(min) + ":" + format.format(sec) + ":" + format2.format(mini_sec);
            else
                time = (negative ? "-" : "") + min + ":" + format.format(sec) + ":" + format2.format(mini_sec);
        }
        return time;
    }


}
