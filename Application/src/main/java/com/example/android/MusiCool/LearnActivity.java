package com.example.android.MusiCool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.common.logger.Log;

import static com.example.android.MusiCool.BluetoothChatFragment.mChatService;

public class LearnActivity extends Activity {

    private MyDBHelper _helper;
    private Cursor c;
    private SimpleCursorAdapter adapter;
    private LinearLayout content_view;
    private ListView mListView;
    private ImageView pic;

    private Handler handler = new Handler();
    private static String _songData, getTone;
    private int times, index;
    private static boolean isGetData;
    public char[] musicRecord = new char[1000];
    private int[] pianoPicture = {
              R.drawable.piano_00b3, R.drawable.piano_01c4, R.drawable.piano_02c4p, R.drawable.piano_03d4,
              R.drawable.piano_04d4p, R.drawable.piano_05e4, R.drawable.piano_06f4, R.drawable.piano_07f4p,
              R.drawable.piano_08g4, R.drawable.piano_09g4p, R.drawable.piano_10a4, R.drawable.piano_11a4p,
              R.drawable.piano_12b4, R.drawable.piano_13c5, R.drawable.piano_14c5p, R.drawable.piano_15d5 };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        _helper = MyDBHelper.getInstance(this);

        content_view = (LinearLayout)findViewById(R.id.layout_learn_list);
        getLayoutInflater().inflate(R.layout.song_list_view_learn, content_view, true);
        View item_view = content_view.getChildAt(0);
        mListView = (ListView) item_view.findViewById(R.id.song_List);

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
                _songData = c.getString(2);
                prepareSong();

                //指引畫面佈置
                setContentView(R.layout.activity_learn_action);
                ImageButton back = (ImageButton)findViewById(R.id.back2);
                TextView txt =(TextView) findViewById(R.id.txt_title);
                txt.setText(c.getString(1));

                //返回選單鍵
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //代表結束，送滅燈訊號
                        sendMessage("e");
                        recreate();
                    }
                });

                pic = (ImageView)findViewById(R.id.picture);
                pic.setImageResource( pianoPicture[ (int)musicRecord[index] - 48 ]);

                sendMessage(musicRecord[index] + "");
                handler.postDelayed(LearnTimer, 10);
            }
        });

    }

    private void sendMessage(String message) {
        // 沒連線
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i("Sending ", "no:"+index+", send: "+message+", times: " + times);

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);
        }
    }

    private void prepareSong(){
        resetWorkSpace();
        int musicIndex, end = 0;
        do {
            musicIndex = _songData.indexOf("a");
            char _music_tone = _songData.charAt(musicIndex - 1);
            musicRecord[times] = _music_tone;
            _songData = _songData.substring(musicIndex + 1, _songData.length());
            times++;

            //讀到data尾
            if (!_songData.contains("a"))
                end++;
        }while(end < 1);
        times--;
    }

    public void resetWorkSpace(){
        for(int i = 0; i < times; i++) {
            musicRecord[i] = 0;
        }
        times = 0;
        index = 0;
    }

    //藍牙傳來訊號
    public static void send(String s){
        getTone = s;
        isGetData = true;
    }

    @Override
    protected void onDestroy() {
        //將執行緒銷毀掉
        handler.removeCallbacks(LearnTimer);
        super.onDestroy();
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

    //計時錄音
    private Runnable LearnTimer = new Runnable() {
        public void run() {

            if(isGetData)
            {
                if(getTone.charAt(0) == musicRecord[index])
                {
                    isGetData = !isGetData;
                    MainActivity.playMusic(getTone);

                    if(++index > times)
                    {
                        //代表結束，送滅燈訊號
                        sendMessage("e");
                        new AlertDialog.Builder(LearnActivity.this)
                                .setTitle("恭喜完成曲目!!")
                                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        handler.removeCallbacks(LearnTimer);
                                        recreate();
                                    }
                                })
                                .show();
                    }else {
                        //下一個
                        sendMessage(musicRecord[index] + "");
                        pic.setImageResource(pianoPicture[(int) musicRecord[index] - 48]);
                    }
                }
            }

            handler.postDelayed(this, 10);
        }
    };
}
