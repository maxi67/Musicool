package com.example.android.MusiCool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class RecordActivity extends Activity {

    View layout1, layout2, layout3;
    private ListView mListView;
    private RadioGroup _radioGroup;
    private MyDBHelper _helper;
    private Cursor c;
    private SimpleCursorAdapter adapter;
    private ImageButton _btn_selectRecord, _btn_selectPlay;
    private ImageButton _btn_start, _btn_stop, _btn_back, _btn_back2;
    private StringBuilder _newStr = new StringBuilder();
    private LinearLayout content_view;
    private Handler handler = new Handler();
    private TextView _time;

    private boolean view2Load, view3Load;//是否載入過的flag
    private Long startTime;
    private int state = 1;
    private static String getTone;
    private static boolean isGetData, isPressStart, isPressStop;

    private static final int STATE_PLAY_MODE = 1;
    private static final int STATE_RENAME_MODE = 2;
    private static final int STATE_DELETE_MODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _helper = MyDBHelper.getInstance(this);

        //=============畫面切換前置作業===============
        LayoutInflater inflater = LayoutInflater.from(this);

        //畫面設定
        layout1 = inflater.inflate(R.layout.activity_record, null);
        layout2 = inflater.inflate(R.layout.activity_record__recoder, null);
        layout3 = inflater.inflate(R.layout.activity_record_list, null);
        setView1();
    }

    //切換到main (選錄音或播放的介面)
    private void setView1() {
        setContentView(layout1);
        _btn_selectRecord = (ImageButton) findViewById(R.id.btn_select_record);
        _btn_selectPlay = (ImageButton) findViewById(R.id.btn_select_play);
        _btn_selectRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setView2();
            }
        });
        _btn_selectPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setView3();
            }
        });
    }

    //切換到main2 (Recorder)
    private void setView2() {
        setContentView(layout2);

        if (!view2Load) { //如果首次顯示,查找裡面的元件並綁定
            _btn_start = (ImageButton) findViewById(R.id.btn_start);
            _btn_stop = (ImageButton) findViewById(R.id.btn_stop);
            _btn_back2 = (ImageButton) findViewById(R.id.record_btn_back);
            _time = (TextView)findViewById(R.id.txt_time);
            view2Load = true;
        }

        _btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPressStart)
                    return;
                isPressStop = false;
                _newStr.setLength(0);
                startTime = System.currentTimeMillis();
                handler.postDelayed(RecordTimer, 1);
                isPressStart = true;
                Item.current_mode = Item.MODE_RECODE;
            }
        });

        _btn_back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(layout1);
            }
        });

        _btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPressStop)
                    return;
                isPressStop = true;
                isPressStart = false;
                _time.setText("0:00");
                handler.removeCallbacks(RecordTimer);
                Item.current_mode = Item.MODE_NONE;
              //=================   開對話框記錄新作品名字，新增至資料庫   ================
                final View item = LayoutInflater.from(RecordActivity.this).inflate(R.layout.item_view_rename, null);
                new AlertDialog.Builder(RecordActivity.this)
                        .setTitle("請輸入作品名稱(取消視同丟棄此作品)")
                        .setView(item)
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = (EditText) item.findViewById(R.id.editText);
                                while (editText.getText().toString().length() == 0)
                                {
                                    editText.setError("不能為空");
                                }
                                Item.new_song_name = editText.getText().toString();

                                ContentValues values = new ContentValues();
                                values.put(Item.KEY_NAME, Item.new_song_name);
                                values.put(Item.KEY_DATA, _newStr.toString());
                                _helper.getWritableDatabase().insert(Item.DATABASE_TABLE, null, values);
                                Toast.makeText(RecordActivity.this,"已存至資料庫！",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
                //=====================================================================
            }
        });
    }

    //切換到main3 (SongList)
    public void setView3() {
        setContentView(layout3);

        if(!view3Load) { //如果首次顯示,查找裡面的元件並綁定
            content_view = (LinearLayout)findViewById(R.id.layout_record_list);
            getLayoutInflater().inflate(R.layout.song_list_view_record, content_view, true);
            View item_view = content_view.getChildAt(0);
            mListView = (ListView) item_view.findViewById(R.id.song_List);
            _radioGroup = (RadioGroup) item_view.findViewById(R.id.radioGroup);
            _btn_back = (ImageButton) item_view.findViewById(R.id.list_btn_back);
            view3Load = true;
        }

        //從資料庫抓歌曲清單
        c = _helper.getReadableDatabase()
                .query(Item.DATABASE_TABLE, null, null, null, null, null, null);
        adapter = new SimpleCursorAdapter(this,
                R.layout.list_style, c,
                new String[]{"name"},
                new int[]{R.id.list_txt}, 1);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //get _id
                c.moveToPosition(position);
                Item._id = Integer.valueOf(c.getString(0));
                Item.current_position = position;

                switch(state){
                    case STATE_PLAY_MODE:
                        startActivity(new Intent(RecordActivity.this, RecordPlayActivity.class));
                        break;

                    case STATE_RENAME_MODE:
                        updateData();
                        break;

                    case STATE_DELETE_MODE:
                        new AlertDialog.Builder(RecordActivity.this)
                                .setTitle("提醒") //對話框標題
                                .setMessage("你確定要刪除嗎?") //對話框內容
                                .setNegativeButton("Cancel", null) //按下按鈕的動作，null表示沒動作
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        _helper.getReadableDatabase().delete(Item.DATABASE_TABLE, "_id=" + Item._id, null);
                                        Toast.makeText(RecordActivity.this, "已成功刪除", Toast.LENGTH_SHORT).show();

                                        //Update listView
                                        c = _helper.getReadableDatabase()
                                                .query(Item.DATABASE_TABLE, null, null, null, null, null, null);
                                        adapter.changeCursor(c);
                                    }
                                })
                                .setCancelable(false) //不可以點對話框以外區域取消
                                .show();
                        break;
                }
            }
        });

        _radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioButton_play:
                        state = STATE_PLAY_MODE;
                        break;
                    case R.id.radioButton_rename:
                        state = STATE_RENAME_MODE;
                        break;
                    case R.id.radioButton_delete:
                        state = STATE_DELETE_MODE;
                        break;
                    default:
                        break;
                }
            }
        });
        _btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(layout1);
            }
        });
    }

    @Override //建立 Menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override //Menu 事件
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_back:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //重新命名之資料庫更新
    public void updateData(){
        final View item = LayoutInflater.from(RecordActivity.this).inflate(R.layout.item_view_rename, null);
        new AlertDialog.Builder(RecordActivity.this)
                .setTitle("請輸入歌曲新名稱")
                .setView(item)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText) item.findViewById(R.id.editText);
                        while (editText.getText().toString().length() == 0)
                        {
                            editText.setError("不能為空");
                        }
                        Item.new_song_name = editText.getText().toString();

                        //Update Database
                        ContentValues values = new ContentValues();
                        values.put(Item.KEY_ID, Item._id);
                        values.put(Item.KEY_NAME, Item.new_song_name);
                        values.put(Item.KEY_DATA, c.getString(2));
                        _helper.getWritableDatabase().update(Item.DATABASE_TABLE, values, Item.KEY_ID + "=" + Item._id, null);

                        //Update listView
                        c = _helper.getReadableDatabase()
                                .query(Item.DATABASE_TABLE, null, null, null, null, null, null);
                        adapter.changeCursor(c);

                    }
                })
                .show();
    }

    //藍牙傳來訊號
    public static void send(String s){
        getTone = s;
        isGetData = true;
    }


    private Runnable RecordTimer = new Runnable() { //計時錄音
        public void run() {

            if(isGetData)
            {
                _newStr.append(getTone+" "+(System.currentTimeMillis() - startTime) + "b ");
                isGetData = !isGetData;
            }
            Long spentTime = System.currentTimeMillis() - startTime;
            final Long minutes = (spentTime / 1000) / 60; //計算目前已過分鐘數
            final Long seconds = (spentTime / 1000) % 60; //計算目前已過秒數
            _time.setText(minutes + ":" + seconds); //顯示
            handler.postDelayed(this, 1);
        }
    };
}
