package com.example.android.MusiCool;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static com.example.android.MusiCool.Item.current_position;

public class RecordPlayActivity extends Activity {
    //    Toast.makeText(RecordPlayActivity.this,"current_position:"+Item.current_position,Toast.LENGTH_SHORT).show();
    private Long startTime;
    private Handler handler = new Handler();
    private static MyDBHelper _helper;
    public long[] Record = new long[1000];
    public char[] musicRecord = new char[1000];
    public int times = 0, index = 0;
    public String _songData;
    private Cursor c;

    LinearLayout content_view;
    RelativeLayout content_view2;
    View item_view;
    TextView _title;
    ImageButton _btn_back, _btn_play, _btn_stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_play);

        _helper = MyDBHelper.getInstance(this);
        c = _helper.getReadableDatabase()
                .query(Item.DATABASE_TABLE, null, null, null, null, null, null);
        c.moveToPosition(current_position);
        ViewInitialize();

        //取值(index) 1:name 2:data
        _songData = c.getString(2);
        prepareForPlay();
    }

    @Override
    protected void onDestroy(){
        handler.removeCallbacks(PlayTimer);
        super.onDestroy();
    }

    public void ViewInitialize(){
        //        Toast.makeText(this, Item._id+"", Toast.LENGTH_SHORT).show();
        content_view = (LinearLayout)findViewById(R.id.layout_play_title);
        content_view2 = (RelativeLayout) findViewById(R.id.layout_play_action);
        getLayoutInflater().inflate(R.layout.item_view_title, content_view, true);
        getLayoutInflater().inflate(R.layout.action_item_view, content_view2, true);

        //Title & button_back
        item_view = content_view.getChildAt(0);
        _title = (TextView)item_view.findViewById(R.id.txt_title);
        _btn_back = (ImageButton) item_view.findViewById(R.id.player_title_btn_back);
        _title.setText(c.getString(1));
        _btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Play & Stop
        item_view = content_view2.getChildAt(0);
        _btn_play = (ImageButton)item_view.findViewById(R.id.btn_play);
        _btn_stop = (ImageButton)item_view.findViewById(R.id.btn_stop);

        //Play Button
        _btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = 0;
                startTime = System.currentTimeMillis();
                Item.current_mode = Item.MODE_PLAY;
                handler.postDelayed(PlayTimer, 1);
            //    Toast.makeText(RecordPlayActivity.this,"playXD",Toast.LENGTH_SHORT).show();
            }
        });

        //Stop Button
        _btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(PlayTimer);
                Item.current_mode =Item.MODE_NONE;
            //    Toast.makeText(RecordPlayActivity.this,"stop",Toast.LENGTH_SHORT).show();
            }
        });
    }

    //工作環境準備
    public void prepareForPlay(){
        resetWorkSpace();
        int timeIndex, musicIndex, end = 0;
        do {
            musicIndex = _songData.indexOf("a");
            timeIndex = _songData.indexOf("b");
            char _music_tone = _songData.charAt(0);
            Long _play_time = Long.valueOf(_songData.substring(musicIndex + 2, timeIndex));
            musicRecord[times] = _music_tone;
            Record[times] = _play_time;

            _songData = _songData.substring(timeIndex + 2, _songData.length());
            if (_songData.length() <= (timeIndex + 2))
                end++;

            times++;
        }while( end < 2);
        times--;
    }

    //工作環境重設
    public void resetWorkSpace(){
        handler.removeCallbacks(PlayTimer);
        for(int i = 0; i < times; i++) {
            musicRecord[i] = 0;
            Record[i] = 0;
        }
        times = 0;
        index = 0;
    }

    //播放器(計時與播放)
    private Runnable PlayTimer = new Runnable() {
        public void run() {

            if(times == 0){
                handler.removeCallbacks(this);
            }

            while( (System.currentTimeMillis()- startTime) / 30  == Record[index] / 30) {
                MainActivity.playMusic(musicRecord[index] + "");
                index++;
            }

            if(index > times)
                handler.removeCallbacks(this);
            handler.postDelayed(this, 1);
        }
    };

    @Override //建立 Menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_player, menu);
        return true;
    }

    @Override //Menu 事件
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_fore:
                if(Item.current_position == 0)
                    break;
                c.moveToPosition(--current_position);
                Log.e("FOREcurrent_position",Item.current_position + " ");
                _songData = c.getString(2);
                prepareForPlay();
                _title.setText(c.getString(1));
                break;

            case R.id.menu_next:
                //取得資料表內的歌曲總數
                Cursor c2 = _helper.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM " + Item.DATABASE_TABLE, null);
                c2.moveToPosition(0);
                if((Item.current_position + 1 ) > Integer.valueOf(c2.getString(0)) - 1)
                    break;
                c2.close();
                c.moveToPosition(++current_position);
                Log.e("NEXT current_position",Item.current_position + " ");
                _songData = c.getString(2);
                prepareForPlay();
                _title.setText(c.getString(1));
                break;

            case R.id.menu_home:
                resetWorkSpace();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}