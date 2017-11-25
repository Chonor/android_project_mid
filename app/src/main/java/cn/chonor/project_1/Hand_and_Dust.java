package cn.chonor.project_1;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by ASUS on 2017/11/14.
 */

public class Hand_and_Dust extends AppCompatActivity implements View.OnClickListener{
    public RecyclerView Dust;
    RecyclerAdapter dust_adapter;
    public RecyclerView Hand;
    RecyclerAdapter hand_adapter;
    public static Hand_and_Dust instance=null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hand_and_dust);

        Init_RecyclerView();
        Update();
        findViewById(R.id.back).setOnClickListener(this);
        instance=this;
    }

    public void Init_RecyclerView(){
        Dust=(RecyclerView) findViewById(R.id.dust);
        LinearLayoutManager linearLayoutManager1=new LinearLayoutManager(this);
        linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);//设置每个View横向布置
        Dust.setLayoutManager(linearLayoutManager1);
        dust_adapter=new RecyclerAdapter(this,MainActivityGame.my_dust);
        dust_adapter.setItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void OnClick(View v, final int position) {
                if(MainActivityGame.enabled_resurrection){
                    AlertDialog.Builder talk=new AlertDialog.Builder(Hand_and_Dust.this);
                    talk.setTitle("是否选择复活"+MainActivityGame.my_dust.get(position).getName());
                    talk.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    talk.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Toast.makeText(Hand_and_Dust.this,"您选择了确定",Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent();
                            intent.setAction("resave_card");
                            Bundle bundle=new Bundle();
                            bundle.putInt("pos",position);
                            intent.putExtras(bundle);
                            sendBroadcast(intent);
                            MainActivityGame.enabled_resurrection=false;
                            Hand_and_Dust.this.finish();
                        }
                    });
                    talk.show();
                }
            }
        });
        Dust.setAdapter(dust_adapter);

        Hand=(RecyclerView) findViewById(R.id.hand);
        LinearLayoutManager linearLayoutManager2=new LinearLayoutManager(this);
        linearLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);//设置每个View横向布置
        Hand.setLayoutManager(linearLayoutManager2);
        hand_adapter=new RecyclerAdapter(this,MainActivityGame.my_hand);
        hand_adapter.setItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void OnClick(View v, final int position) {
                if(MainActivityGame.enabled){
                    AlertDialog.Builder talk=new AlertDialog.Builder(Hand_and_Dust.this);
                    talk.setTitle("是否选择使用"+MainActivityGame.my_hand.get(position).getName());
                    talk.setMessage(MainActivityGame.my_hand.get(position).getProperty());
                    talk.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    talk.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Toast.makeText(Hand_and_Dust.this,"您选择了确定",Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent();
                            intent.setAction("use_card");
                            Bundle bundle=new Bundle();
                            bundle.putInt("pos",position);
                            intent.putExtras(bundle);
                            sendBroadcast(intent);
                            Hand_and_Dust.this.finish();
                        }
                    });
                    talk.show();
                }
            }
        });
        Hand.setAdapter(hand_adapter);
    }

    public void Update(){
        dust_adapter.notifyDataSetChanged();
        hand_adapter.notifyDataSetChanged();

        int opponent_remain_num_hand=MainActivityGame.opponent_hand.size();
        int my_remain_num_hand=MainActivityGame.my_hand.size();
        TextView my_hand=(TextView) findViewById(R.id.text_hand);
        my_hand.setText("手牌:"+my_remain_num_hand);
        TextView opponent_hand=(TextView) findViewById(R.id.hint);
        opponent_hand.setText("对方剩余手牌数量:"+opponent_remain_num_hand);
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.back) Hand_and_Dust.this.finish();
    }
}
