package com.example.heng.jredu.jredu;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.heng.jredu.R;
import com.example.heng.jredu.entity.UserDemo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity_chat extends Activity implements Runnable {
    private Button bt_msg;
    private EditText et_msg;
    private TextView tv_msg;

    private static final String HOST = "192.168.191.1";
    private static final int PORT = 9990;// 端口号

    private Socket socket;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private String content;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            // tv_msg.setText(content);
            tv_msg.append(content);
        }
    };

    private UserDemo userDemo = MyApplaction.getMyApplaction().getUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_chat);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        bt_msg = (Button) findViewById(R.id.bt_send);
        et_msg = (EditText) findViewById(R.id.et_msg);
        tv_msg = (TextView) findViewById(R.id.tv_msg);

        try {
            socket = new Socket(HOST, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
            out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(),
                            "utf-8")), true);
        } catch (IOException e) {
            e.printStackTrace();
        }


        bt_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = userDemo.getUname() + ":" + et_msg.getText().toString();
                if (socket.isConnected()) {
                    if (!socket.isOutputShutdown()) {
                        out.println(msg);
                    }
                }
                et_msg.setText("");
            }
        });

        new Thread(this).start();
    }


    /**
     * 读取服务器发来的消息
     */

    public void run() {
        while (true) {
            if (!socket.isClosed()) {
                if (socket.isConnected()) {
                    if (!socket.isInputShutdown()) {
                        try {
                            if ((content = in.readLine()) != null) {
                                content += "\n";
                                mHandler.sendMessage(mHandler.obtainMessage());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

    }

}
