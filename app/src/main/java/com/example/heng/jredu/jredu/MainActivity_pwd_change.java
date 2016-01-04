package com.example.heng.jredu.jredu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.heng.jredu.R;
import com.example.heng.jredu.entity.UserDemo;
import com.example.heng.jredu.util.StringPostRequest;
import com.example.heng.jredu.util.SystemBarTintManager;
import com.example.heng.jredu.util.UrlUtil;
import com.google.gson.Gson;

public class MainActivity_pwd_change extends Activity {

    private RelativeLayout back;//返回


    private EditText oldEdit;//旧密码
    private EditText newEdit;//新密码
    private EditText newConEdit;//确认新密码
    private Button bt_pwd;//确定

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_pwd_change);

        back = (RelativeLayout) findViewById(R.id.left_icon);
        bt_pwd = (Button) findViewById(R.id.bt_pwd);
        oldEdit = (EditText) findViewById(R.id.userOldPwd);
        newEdit = (EditText) findViewById(R.id.userNewPwd);
        newConEdit = (EditText) findViewById(R.id.userNewConPwd);

        //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        pwd();

        //沉浸式状态栏
        SystemBarTintManager mTintManager = new SystemBarTintManager(this);
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setNavigationBarTintEnabled(true);
        mTintManager.setStatusBarTintResource(R.color.tongzhilan);
    }

    private void pwd() {
        bt_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String oldPwd = oldEdit.getText().toString();
                final String newPwd = newEdit.getText().toString();
                String conNewPwd = newConEdit.getText().toString();

                if (TextUtils.isEmpty(oldPwd)) {
                    Toast.makeText(getApplicationContext(), "请填写当前密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                final UserDemo userDemo =MyApplaction.getMyApplaction().getUser();
                if (!oldPwd.equals(userDemo.getUpwd())) {
                    Toast.makeText(getApplicationContext(), "当前密码错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(newPwd)) {
                    Toast.makeText(getApplicationContext(), "请填写新密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(conNewPwd)) {
                    Toast.makeText(getApplicationContext(), "请填写确认密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!newPwd.equals(conNewPwd)) {
                    Toast.makeText(getApplicationContext(), "新密码与确认密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }

                userDemo.setUpwd(newPwd);
                String url = UrlUtil.UserLogin;
                StringPostRequest str_nick = new StringPostRequest(url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        Gson gson = new Gson();
                        UserDemo user = gson.fromJson(s, UserDemo.class);
                        if (user.getUname().equals("")) {
                            Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_LONG).show();
                        } else {
                            MyApplaction.getMyApplaction().setUser(user);

                            Intent it = new Intent(MainActivity_pwd_change.this, MainActivity_on.class);
                            Toast.makeText(getApplicationContext(), "检测到您的密码已修改，请重新登陆"
                                    , Toast.LENGTH_SHORT).show();
                            startActivity(it);
                            finish();
                        }
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
        });

    }

}
