package com.example.heng.jredu.jredu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.heng.jredu.R;
import com.example.heng.jredu.adapter.VedioAdapter;
import com.example.heng.jredu.entity.Vedio;
import com.example.heng.jredu.mediedictionarys.playerlibrary.PlayerActivity;
import com.example.heng.jredu.util.SystemBarTintManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity_download extends Activity {

    RelativeLayout back;
    private ListView lv_download;
    private SQLiteDatabase db;

    private List<Vedio> localList = new ArrayList<>();

    private VedioAdapter va;
    private Vedio vedio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_download);

        back = (RelativeLayout) findViewById(R.id.back);
        lv_download = (ListView) findViewById(R.id.lv_download);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lv_download.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity_download.this, PlayerActivity.class);
                intent.putExtra("Vedio", localList.get(position));
                intent.putExtra("flag", 2);

                intent.putExtra("vedioName", localList.get(position).getVedioName());
                startActivity(intent);
            }
        });


        lv_download.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity_download.this)
                        .setMessage("确定删除?").setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //删除数据
                                String delete = "DELETE FROM mobile_video where _id=" + localList.get(position).getId();
                                deleteDb(delete);

                                //删除文件
                                String fileNames = Environment.getExternalStorageDirectory() + "/JREDU"
                                        + localList.get(position).getVUri();
                                deleteFile(fileNames);

                                //记录这一行的位置 然后remove
                                localList.remove(position);
                                va.notifyDataSetChanged();
                                lv_download.invalidate();

                                Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_LONG).show();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        }).create();
                alertDialog.show();
                va.notifyDataSetChanged();

                return true;
            }
        });


        /**
         * 本地下载的视频列表localList
         */
        db = this.openOrCreateDatabase("mobile_video.db", Activity.MODE_PRIVATE, null);

        va = new VedioAdapter(getBaseContext(), localList);
        lv_download.setAdapter(va);
        va.notifyDataSetChanged();

        localList.clear();
        String search = "select * from mobile_video";
        Cursor cursor = db.rawQuery(search, null);
        while (cursor.moveToNext()) {
            vedio = new Vedio();
            vedio.setId(cursor.getLong(0));
            vedio.setVedioid(cursor.getString(1));
            vedio.setVedioName(cursor.getString(2));
            vedio.setVUri(cursor.getString(3));
            vedio.setProjId(cursor.getString(4));
            vedio.setInstruction(cursor.getString(5));
            vedio.setAuthor(cursor.getString(6));
            vedio.setPubDate(cursor.getString(7));
            vedio.setVPickUri(cursor.getString(8));
            vedio.setFlag(cursor.getString(9));
            localList.add(vedio);
        }

        setTranslucentStatus();
    }

    /**
     * 删除数据
     */

    private void deleteDb(String delete) {
        db = getBaseContext().openOrCreateDatabase("mobile_video.db", Activity.MODE_PRIVATE, null);
        db.execSQL(delete);
        db.close();

    }


    /**
     * 删除指定文件
     */
    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 沉浸式状态栏
     */
    public void setTranslucentStatus() {
        SystemBarTintManager mTintManager = new SystemBarTintManager(this);
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setNavigationBarTintEnabled(true);
        mTintManager.setStatusBarTintResource(R.color.tongzhilan);

    }

}
