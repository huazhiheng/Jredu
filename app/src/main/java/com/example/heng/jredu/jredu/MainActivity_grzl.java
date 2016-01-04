package com.example.heng.jredu.jredu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.heng.jredu.R;
import com.example.heng.jredu.entity.UserDemo;
import com.example.heng.jredu.util.ImageLoaderUtil;
import com.example.heng.jredu.util.StringPostRequest;
import com.example.heng.jredu.util.SystemBarTintManager;
import com.example.heng.jredu.util.UrlUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class MainActivity_grzl extends Activity implements View.OnClickListener {
    private RelativeLayout left_icon, relativeLayout3, relativeLayout4, relativeLayout5, relativeLayout6;
    private TextView nickname, sexId, level;
    private ImageView face_grzl;
    private EditText et_nickname;

    private View view, view2, view3;
    private PopupWindow popupWindow;
    private PopupWindow popupWindow2;

    public static final int PHONE_PHOTO = 0;//调用相册选取照片
    public static final int TAKE_PHOTO = 1;//调用相机拍照
    public static final int RESULT_PHOTO = 2;//裁剪后的头像
    private String capturePath = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_grzl);

        nickname = (TextView) findViewById(R.id.nickname);
        sexId = (TextView) findViewById(R.id.sexId);
        level = (TextView) findViewById(R.id.level);
        face_grzl = (ImageView) findViewById(R.id.face_grzl);

        left_icon = (RelativeLayout) findViewById(R.id.left_icon);
        relativeLayout3 = (RelativeLayout) findViewById(R.id.relativeLayout3);
        relativeLayout4 = (RelativeLayout) findViewById(R.id.relativeLayout4);
        relativeLayout5 = (RelativeLayout) findViewById(R.id.relativeLayout5);
        relativeLayout6 = (RelativeLayout) findViewById(R.id.relativeLayout7);

        left_icon.setOnClickListener(this);
        relativeLayout3.setOnClickListener(this);
        relativeLayout4.setOnClickListener(this);
        relativeLayout5.setOnClickListener(this);
        relativeLayout6.setOnClickListener(this);

        view = this.getLayoutInflater().inflate(R.layout.layout_popwindow, null);
        view.findViewById(R.id.take_photo).setOnClickListener(this);
        view.findViewById(R.id.phone_photo).setOnClickListener(this);
        view.findViewById(R.id.cancel).setOnClickListener(this);

        view2 = this.getLayoutInflater().inflate(R.layout.popwindow_sex, null);
        view2.findViewById(R.id.man).setOnClickListener(this);
        view2.findViewById(R.id.woman).setOnClickListener(this);

        view3 = this.getLayoutInflater().inflate(R.layout.popwindow_nickname, null);
        view3.findViewById(R.id.dismiss).setOnClickListener(this);
        view3.findViewById(R.id.sure).setOnClickListener(this);
        view3.findViewById(R.id.et_nickname).setOnClickListener(this);
        et_nickname = (EditText) view3.findViewById(R.id.et_nickname);
        et_nickname.setHint(userDemo.getNickName());

        userDemo = MyApplaction.getMyApplaction().getUser();

        level.setText(userDemo.getLevel());

        ImageLoaderUtil.display(userDemo.getPhotoUri(), face_grzl);
        sexId.setText(userDemo.getSexId());

        //沉浸式状态栏
        setTranslucentStatus();
    }

    //显示并得到 popwindow(拍照)
    public PopupWindow getPopwindow(View view) {
        popupWindow = new PopupWindow(view,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.6f;
        getWindow().setAttributes(lp);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.showAtLocation(findViewById(R.id.relativeLayout3), Gravity.BOTTOM, 0, 0);
        //设置当popupWindow消失的时候背景为透明
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        return popupWindow;
    }

    //显示并得到 popwindow2
    public PopupWindow getPopwindow2(View view) {
        popupWindow2 = new PopupWindow(view,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        popupWindow2.setFocusable(true);
        popupWindow2.setOutsideTouchable(true);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.6f;
        getWindow().setAttributes(lp);
        popupWindow2.setBackgroundDrawable(new ColorDrawable());
        popupWindow2.showAtLocation(findViewById(R.id.relativeLayout3), Gravity.CENTER,-70,-70);
        //设置当popupWindow消失的时候背景为透明
        popupWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        return popupWindow2;
    }

    PopupWindow window = null;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_icon:    //返回上一页
                finish();
                break;
            case R.id.relativeLayout3:  //头像
                window = getPopwindow(view);
                break;
            case R.id.relativeLayout4:  //昵称
                window = getPopwindow2(view3);
                break;
            case R.id.relativeLayout5:  //性别
                window = getPopwindow2(view2);
                break;
            case R.id.relativeLayout7:  //密码
                Intent it3 = new Intent(MainActivity_grzl.this, MainActivity_pwd_change.class);

                startActivity(it3);
                break;

            case R.id.take_photo:
                String state = Environment.getExternalStorageState();
                if (state.equals(Environment.MEDIA_MOUNTED)) {
                    Intent camera = new Intent("android.media.action.IMAGE_CAPTURE");
                    //创建文件夹
                    File fileDir = new File(Environment.getExternalStorageDirectory() + "/headImag");
                    if (!fileDir.exists()) {
                        fileDir.mkdir();
                    }
                    capturePath = fileDir.getPath()
                            + File.separatorChar
                            + System.currentTimeMillis()
                            + ".jpg";
                    camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(capturePath)));
                    camera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    startActivityForResult(camera, TAKE_PHOTO);
                } else {
                    Toast.makeText(getApplicationContext(), "SD卡不可用", Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.phone_photo:
                Intent picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(picture, PHONE_PHOTO);

                break;

            case R.id.cancel:
                window.dismiss();
                break;

            case R.id.man:
                userDemo.setSexId("男");
                sexId.setText("男");
                change();
                window.dismiss();
                Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_LONG).show();
                break;

            case R.id.woman:
                userDemo.setSexId("女");
                sexId.setText("女");
                change();
                window.dismiss();
                Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_LONG).show();
                break;

            case R.id.sure:
                String nick_name = et_nickname.getText().toString();
                userDemo.setNickName(nick_name);
                nickname.setText(nick_name);
                change();
                window.dismiss();
                Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_LONG).show();


            case R.id.dismiss:
                window.dismiss();
                break;


            default:
                break;

        }


    }


    private void change() {

        String url = UrlUtil.UserLogin;
        StringPostRequest str_nick = new StringPostRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        str_nick.putParams("uname", userDemo.getUname());
        str_nick.putParams("upwd", userDemo.getUpwd());
        str_nick.putParams("unickname", userDemo.getNickName());
        str_nick.putParams("sexid", userDemo.getSexId());
        str_nick.putParams("photoUrl", userDemo.getPhotoUri());
        str_nick.putParams("flag", "3");
        MyApplaction.getMyApplaction().getRequestQueue().add(str_nick);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case PHONE_PHOTO://相册
                Cursor cursor = this.getContentResolver().query(data.getData()
                        , new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                cursor.moveToFirst();
                String capturePath1 = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                cursor.close();
                startPhotoZoom(Uri.fromFile(new File(capturePath1)));
                break;
            case TAKE_PHOTO://相机
                startPhotoZoom(Uri.fromFile(new File(capturePath)));
                break;
            case RESULT_PHOTO:
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    Bitmap bitmap = bundle.getParcelable("data");
                    face_grzl.setImageBitmap(bitmap);
                    alertDialogshow(bitmap);
                }
            default:
                break;
        }
        //关闭popupWindow
        window.dismiss();

    }

    //将Bitmap转换为Base64编码的字符串
    public String convertBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        try {
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytes = bos.toByteArray();
        byte[] enCode = Base64.encode(bytes, Base64.DEFAULT);
        return new String(enCode);
    }

    public void alertDialogshow(final Bitmap bitmap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = LayoutInflater.from(this).inflate(R.layout.layout_dialog, null);
        builder.setView(v);
        final AlertDialog ad = builder.create();

        v.findViewById(R.id.positiveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
                uploadPic(convertBitmap(bitmap));//调用上传
            }
        });

        v.findViewById(R.id.negativeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
            }
        });
        TextView title = (TextView) v.findViewById(R.id.title);
        title.setText("提示");
        TextView subtitlt = (TextView) v.findViewById(R.id.subtitle);
        subtitlt.setText("是否上传头像");
        ad.show();
    }

    //上传
    ProgressDialog pd;
    UserDemo userDemo = MyApplaction.getMyApplaction().getUser();

    public void uploadPic(String pic) {
        StringPostRequest request = new StringPostRequest(UrlUtil.UserLogin, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (pd != null && pd.isShowing())
                    pd.dismiss();
                userDemo.setPhotoUri(result);
                MyApplaction.getMyApplaction().setUser(userDemo);
                Toast.makeText(getApplicationContext(), "头像上传成功", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (pd != null && pd.isShowing())
                    pd.dismiss();
                Toast.makeText(getApplicationContext(), "头像上传失败", Toast.LENGTH_LONG).show();
            }
        });
        request.putParams("uname", userDemo.getUname());
        request.putParams("image", pic);
        request.putParams("flag", "4");
        MyApplaction.getMyApplaction().getRequestQueue().add(request);

    }


    //图片裁剪
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //设置裁剪
        intent.putExtra("crop", "true");
        //设置宽度高度
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //设置图片的宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESULT_PHOTO);
    }


    @Override
    protected void onStart() {
        nickname.setText(userDemo.getNickName());
        super.onStart();
    }

    //沉浸式状态栏
    public void setTranslucentStatus() {
        SystemBarTintManager mTintManager = new SystemBarTintManager(this);
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setNavigationBarTintEnabled(true);
        mTintManager.setStatusBarTintResource(R.color.tongzhilan);

    }

}
