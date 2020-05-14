package com.example.android.MusiCool;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.android.common.activities.SampleActivityBase;
import com.example.android.common.logger.Log;

import static com.example.android.MusiCool.BluetoothChatService.str;

public class MainActivity extends SampleActivityBase {

    public static SoundPool soundPool;
    public static int[] soundID = new int[16];
    public static int mode = 0;

    private ImageButton _button_learning, _button_record, _button_free;
    BluetoothChatFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soundPool = new SoundPool(16, AudioManager.STREAM_MUSIC, 100);
        soundPrepare();
        findViews();

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            fragment = new BluetoothChatFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Item.current_mode = Item.MODE_NONE;
    }

    //在Activity中取得畫面元件
    private void findViews() {
        _button_learning = (ImageButton)findViewById(R.id.btn_learning);
        _button_record = (ImageButton)findViewById(R.id.btn_record);
        _button_free = (ImageButton) findViewById(R.id.btn_free);

        //learn Mode====================================================
        _button_learning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fragment.mChatService.mState != BluetoothChatService.STATE_CONNECTED)
                {
                    fragment.chooseDevice();
                    Toast.makeText(fragment.getActivity(),"請先連線到嵌入式鋼琴XD",Toast.LENGTH_SHORT).show();
                    return;
                }
                Item.current_mode = Item.MODE_LEARN;
                mode = Item.MODE_LEARN;
                startActivity(new Intent(MainActivity.this, LearnActivity.class));
            }
        });

        //Record Mode ==================================================
        _button_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(fragment.mChatService.mState != BluetoothChatService.STATE_CONNECTED)
                {
                    fragment.chooseDevice();
                    Toast.makeText(fragment.getActivity(),"請先連線到嵌入式鋼琴XD",Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(MainActivity.this, RecordActivity.class));
                mode = Item.MODE_RECODE;
           //     Toast.makeText(fragment.getActivity(),"請先連線到嵌入式鋼琴XD",Toast.LENGTH_SHORT).show();
            }
        });

        //Free Mode ====================================================
        _button_free.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fragment.mChatService.mState != BluetoothChatService.STATE_CONNECTED)
                {
                    fragment.chooseDevice();
                    Toast.makeText(fragment.getActivity(),"請先連線到嵌入式鋼琴XD",Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(MainActivity.this, FreeActivity.class));
                mode = Item.MODE_FREE;
            }
        });
    }

    //載入聲音
    private void soundPrepare(){
        //b3(0 c4(1 c4p(2 d4(3 d4p(4 e4(5 f4(6 f4p(7 g4(8 g4p(9 a4(: a4p(; b4(< c5(= c5p(> d5(?
        soundID[0] = soundPool.load(this, R.raw.b3, 1);
        soundID[1] = soundPool.load(this, R.raw.c4, 1);
        soundID[2] = soundPool.load(this, R.raw.c4p, 1);
        soundID[3] = soundPool.load(this, R.raw.d4, 1);
        soundID[4] = soundPool.load(this, R.raw.d4p, 1);
        soundID[5] = soundPool.load(this, R.raw.e4, 1);
        soundID[6] = soundPool.load(this, R.raw.f4, 1);
        soundID[7] = soundPool.load(this, R.raw.f4p, 1);
        soundID[8] = soundPool.load(this, R.raw.g4, 1);
        soundID[9] = soundPool.load(this, R.raw.g4p, 1);
        soundID[10] = soundPool.load(this, R.raw.a4, 1);
        soundID[11] = soundPool.load(this, R.raw.a4p, 1);
        soundID[12] = soundPool.load(this, R.raw.b4, 1);
        soundID[13] = soundPool.load(this, R.raw.c5, 1);
        soundID[14] = soundPool.load(this, R.raw.c5p, 1);
        soundID[15] = soundPool.load(this, R.raw.d5, 1);
    }

    //播放模式判斷
    public static void play(String s){
        Log.e("State", " 音 :"+ str[s.charAt(0)-48]+" "+mode+" "+Item.current_mode);
        switch (mode){
            case Item.MODE_RECODE:
                RecordActivity.send(s);
                playMusic(s);
                break;

            case Item.MODE_FREE:
                playMusic(s);
                break;

            case Item.MODE_LEARN:
                LearnActivity.send(s);
                break;

            default:
                break;
        }
    }

    //播放琴音
    public static void playMusic(String s){
        if(soundPool != null)
            {   // 0-> 3B   1-> 4C   2-> 4C#  3-> 4D
                // 4-> 4D#  5-> 4E   6-> 4F   7-> 4F#
                // 8-> 4G   9-> 4G# 10-> 4A  11-> 4A#
                //12-> 4B  13-> 5C  14-> 5C# 15-> 5D
                Log.e("S",s);
                soundPool.play(MainActivity.soundID[s.charAt(0) - 48], 1, 1, 1, 0, 1);
            }
    }
}