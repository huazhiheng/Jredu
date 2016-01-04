package com.example.heng.jredu.jredu;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heng.jredu.R;
import com.example.heng.jredu.entity.UserDemo;
import com.example.heng.jredu.util.ImageLoaderUtil;
import com.example.heng.jredu.util.PreLoginUtil;
import com.example.heng.jredu.util.SystemBarTintManager;
import com.example.heng.jredu.util.VersionManager;

import java.io.File;

public class MainActivity_setting extends Activity {

    private ImageView head_image;
    private TextView nickname, press, tv_cache;
    private Button exit;
    private RelativeLayout info, cache, version, aboutjredu, back;

    private TextView this_version;
    private CheckBox checkbox_wifi;
    private String check = "1";

    UserDemo userDemo = MyApplaction.getMyApplaction().getUser();

    private static final int YSPEED_MIN = 1000;//手指上下滑动时的最小速度

    private static final int XDISTANCE_MIN = 50;//手指向右滑动时的最小距离

    private static final int YDISTANCE_MIN = 100;//手指向上滑或下滑时的最小距离

    private float xDown;//记录手指按下时的横坐标。

    private float yDown;//记录手指按下时的纵坐标。

    private float xMove;//记录手指移动时的横坐标。

    private float yMove;//记录手指移动时的纵坐标。

    private VelocityTracker mVelocityTracker;//用于计算手指滑动的速度。


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity__setting);
        setTranslucentStatus();

        head_image = (ImageView) findViewById(R.id.head_image);
        nickname = (TextView) findViewById(R.id.nickname);
        press = (TextView) findViewById(R.id.press);
        this_version = (TextView) findViewById(R.id.tv_version);

        info = (RelativeLayout) findViewById(R.id.info);
        cache = (RelativeLayout) findViewById(R.id.cache);
        version = (RelativeLayout) findViewById(R.id.version);
        aboutjredu = (RelativeLayout) findViewById(R.id.aboutjredu);
        back = (RelativeLayout) findViewById(R.id.back);
        exit = (Button) findViewById(R.id.exit);
        tv_cache = (TextView) findViewById(R.id.tv_cache);
        checkbox_wifi = (CheckBox) findViewById(R.id.checkbox_wifi);

        final String path = Environment.getExternalStorageDirectory()
                + "/Android/data/" + getPackageName() + "/cache";
        final File file = new File(path);
        tv_cache.setText(convertStorage(getFolderSize(file)));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            set(true);
        }
        PackageManager manager = getPackageManager();
        PackageInfo ino;
        String versionName = null;
        int versionCode;
        try {
            ino = manager.getPackageInfo(this.getPackageName(), 0);
            versionName = ino.versionName;
            versionCode = ino.versionCode;
            MyApplaction.getMyApplaction().setVerCode(versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        this_version.setText("当前版本:" + versionName);

        //个人信息
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userDemo != null) {
                    Intent intent = new Intent(MainActivity_setting.this, MainActivity_grzl.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity_setting.this, MainActivity_on.class);
                    startActivity(intent);
                }

            }
        });

        //清除缓存
        cache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFolderFile(path);
                tv_cache.setText(convertStorage(getFolderSize(file)));
                Toast.makeText(getApplication(), "清除成功", Toast.LENGTH_SHORT).show();

            }
        });

        //版本更新
        version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VersionManager vManager = new VersionManager(MainActivity_setting.this);
                vManager.downloadApkInfo();

            }
        });

        //关于杰瑞
        aboutjredu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity_setting.this, MainActivity_aboutjredu.class);
                startActivity(intent);
            }
        });
        //退出登录
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userDemo != null) {
                    UserDemo userDemo = null;
                    MyApplaction.getMyApplaction().setUser(userDemo);
                    //把自动登录的勾去掉，其余留着
                    PreLoginUtil.putString(getBaseContext(), "remember_02", "no");
                    finish();
                } else {
                    Toast.makeText(getApplication(), "当前未登录", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //是否允许非wifi播放视频
        if (PreLoginUtil.gettString(getBaseContext(), "wifi").equals("0")) {
            checkbox_wifi.setChecked(false);
        } else {
            checkbox_wifi.setChecked(true);
        }

        checkbox_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkbox_wifi.isChecked()) {
                    check = "1";
                    PreLoginUtil.putString(getBaseContext(), "wifi", check);
                } else {
                    check = "0";
                    PreLoginUtil.putString(getBaseContext(), "wifi", check);
                }
            }
        });


    }

    //清除缓存
    public void deleteFolderFile(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {// 处理目录
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFolderFile(files[i].getAbsolutePath());
                    }
                }
                if (!file.isDirectory()) {// 如果是文件，删除
                    file.delete();
                } else {// 目录
                    if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                        file.delete();
                    }
                }
                //  }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    //获取缓存大小
    public static long getFolderSize(File file) {
        long size = 0;
        try {
            java.io.File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return size;
    }

    //转换文件大小
    public static String convertStorage(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size > kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else {
            return String.format("%d B", size);
        }
    }


    @Override
    protected void onStart() {

        userDemo = MyApplaction.getMyApplaction().getUser();
        if (userDemo != null) {
            press.setText("");
            nickname.setText(userDemo.getNickName());
            ImageLoaderUtil.display(userDemo.getPhotoUri(), head_image);
        }
        super.onStart();
    }

    //沉浸式状态栏
    public void setTranslucentStatus() {
        SystemBarTintManager mTintManager = new SystemBarTintManager(this);
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setNavigationBarTintEnabled(true);
        mTintManager.setStatusBarTintResource(R.color.tongzhilan);

    }


    private void set(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        createVelocityTracker(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDown = event.getRawX();
                yDown = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                xMove = event.getRawX();
                yMove = event.getRawY();
                //滑动的距离
                int distanceX = (int) (xMove - xDown);
                int distanceY = (int) (yMove - yDown);
                //获取顺时速度
                int ySpeed = getScrollVelocity();
                //关闭Activity需满足以下条件：
                //1.x轴滑动的距离>XDISTANCE_MIN
                //2.y轴滑动的距离在YDISTANCE_MIN范围内
                //3.y轴上（即上下滑动的速度）<XSPEED_MIN，如果大于，则认为用户意图是在上下滑动而非左滑结束Activity
                if (distanceX > XDISTANCE_MIN && (distanceY < YDISTANCE_MIN && distanceY > -YDISTANCE_MIN) && ySpeed < YSPEED_MIN) {
                    finish();
                }
                break;
            case MotionEvent.ACTION_UP:
                recycleVelocityTracker();
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 创建VelocityTracker对象，并将触摸界面的滑动事件加入到VelocityTracker当中。
     *
     * @param event
     */
    private void createVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * 回收VelocityTracker对象。
     */
    private void recycleVelocityTracker() {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }

    /**
     * @return 滑动速度，以每秒钟移动了多少像素值为单位。
     */
    private int getScrollVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) mVelocityTracker.getYVelocity();
        return Math.abs(velocity);
    }


}



