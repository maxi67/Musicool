package com.example.android.MusiCool;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class FreeActivity extends Activity {

    Button[] kyb = new Button[16];
    ImageButton _btn_select_keyboard, _btn_select_screen, _btn_free_back;
    View layout1, layout2;
    //b3(0 c4(1 c4p(2 d4(3 d4p(4 e4(5 f4(6 f4p(7 g4(8 g4p(9 a4(: a4p(; b4(< c5(= c5p(> d5(?
//    private boolean view2Load;
//    int foreIndex = 0;
//    int[] isTapped = new int[16];
//    char[] tone = new char[]{'0','1','2','3','4','5','6','7','8','9',':',';','<','=','>','?'};
//    int[] kybID = new int[]{
//            R.id.kyb_00b3, R.id.kyb_01c4, R.id.kyb_02c4p, R.id.kyb_03d4,
//            R.id.kyb_04d4p, R.id.kyb_05e4, R.id.kyb_06f4, R.id.kyb_07f4p,
//            R.id.kyb_08g4, R.id.kyb_09g4p, R.id.kyb_10a4, R.id.kyb_11a4p,
//            R.id.kyb_12b4, R.id.kyb_13c5, R.id.kyb_14c5p, R.id.kyb_15d5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_keyboard);
//        setContentView(R.layout.activity_free);

        _btn_free_back = (ImageButton)findViewById(R.id.btn_free_back);
        _btn_free_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //=============畫面切換前置作業===============
        //功能相同
        //LayoutInflater inflater = LayoutInflater.from(this);
        //LayoutInflater inflater = getLayoutInflater();

        //畫面設定
  //      layout1 = inflater.inflate(R.layout.activity_free, null);
//        layout2 = inflater.inflate(R.layout.activity_free_screen, null);

//        _btn_select_keyboard = (ImageButton)findViewById(R.id.btn_select_keyboard);
//        _btn_select_screen = (ImageButton)findViewById(R.id.btn_select_screen);

//        _btn_select_screen.setVisibility(View.INVISIBLE);
//        _btn_select_screen.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setView2();
//            }
//        });

//        _btn_select_keyboard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setContentView(R.layout.activity_free_keyboard);
//                _btn_free_back = (ImageButton)findViewById(R.id.btn_free_back);
//                _btn_free_back.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        setContentView(R.layout.activity_free);
//                    }
//                });
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Item.current_mode = Item.MODE_FREE;
        MainActivity.mode = Item.MODE_FREE;
    }


//    private void setView2() {
//        setContentView(layout2);
//
//        if (!view2Load) { //如果首次顯示,查找裡面的元件並綁定
//            for(int i = 0;i<16;i++) {
//                final int index = i;
//                kyb[i] = (Button) findViewById(kybID[i]);
//
//                kyb[i].setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        MainActivity.play(tone[index] + "");
//                        foreIndex = index;
//                    }
//                });
//            }
//        }
//    }

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
}
