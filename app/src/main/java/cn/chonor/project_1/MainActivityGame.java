
package cn.chonor.project_1;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.io.*;
import java.util.Random;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

public class MainActivityGame extends AppCompatActivity implements View.OnClickListener,View.OnLongClickListener{
    public static List<GwentCard> my_library=new ArrayList<>();//我方卡组
    public static List<GwentCard> opponent_library=new ArrayList<>();//敌方卡组
    public static List<GwentCard> my_hand=new ArrayList<>();//我方手牌
    public static List<GwentCard> opponent_hand=new ArrayList<>();//敌方手牌
    public static List<GwentCard> my_dust=new ArrayList<>();//我方墓地
    public static List<GwentCard> opponent_dust=new ArrayList<>();//敌方墓地
    public static boolean enabled;//使用手牌使能，只有在我方回合才能使用手牌
    public static boolean enabled_resurrection;//复活使能，只有在该使能激活时才能触发墓地的点击事件
    public static boolean enabled_decoy;//使用诱饵使能，只有在打出诱饵牌后激活使能
    public static boolean is_my_round_end;//判断玩家回合是否结束，为true才能结束玩家回合
    public GwentCard decoy;//初始化一张诱饵牌
    public boolean my_give_up;//我方是否放弃本轮
    public boolean opponent_give_up;//敌方是否放弃本轮
    public int turn;//为1表示我方行动，为-1表示敌方行动
    public int my_life;//我方血槽
    public int opponent_life;//敌方血槽
    public static GwentBoard board;//棋盘

    int img1[]={R.mipmap.decoy,R.mipmap.commander_horn,R.mipmap.blue_stripes_commando,R.mipmap.crinfrid_reavers_dragon_hunter,R.mipmap.catapult,R.mipmap.thaler,R.mipmap.mysterious_elf,R.mipmap.dijkstra,R.mipmap.prince_stennis,R.mipmap.geralt,R.mipmap.yennefer,R.mipmap.villentretenmerth,R.mipmap.ciri,R.mipmap.dun_banner_medic,R.mipmap.esterad_thyssen,R.mipmap.john_natalis,R.mipmap.philippa_eilhart,R.mipmap.vernon_roche};
    int img2[]={R.mipmap.decoy,R.mipmap.commander_horn,R.mipmap.arachas_behemoth,R.mipmap.arachas,R.mipmap.vampire_ekimmara,R.mipmap.vampire_fleder,R.mipmap.vampire_garkain,R.mipmap.vampire_bruxa,R.mipmap.vampire_katakan,R.mipmap.crone_brewess,R.mipmap.crone_weavess,R.mipmap.crone_whispess,R.mipmap.draug,R.mipmap.vesemir,R.mipmap.earth_elemental,R.mipmap.imlerith,R.mipmap.leshen,R.mipmap.triss_merigold,R.mipmap.yennefer,R.mipmap.geralt,R.mipmap.ciri,R.mipmap.villentretenmerth,R.mipmap.mysterious_elf};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);

        findViewById(R.id.to_hand_dust).setOnClickListener(MainActivityGame.this);
        findViewById(R.id.give_up).setOnLongClickListener(MainActivityGame.this);
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("use_card");
        intentFilter.addAction("resave_card");
        registerReceiver(mBroadcastReceiver,intentFilter);
        IntentFilter intentFilter1=new IntentFilter();
        intentFilter1.addAction("my_game_begin");
        intentFilter1.addAction("opponent_game_begin");
        registerReceiver(mBroadcastReceiver1,intentFilter1);
        IntentFilter intentFilter2=new IntentFilter();
        intentFilter2.addAction("my_game_end");
        intentFilter2.addAction("opponent_game_end");
        registerReceiver(mBroadcastReceiver2,intentFilter2);



        Init_library();
        for(int i=0;i<my_library.size();i++){
            if(my_library.get(i).getType().equals("decoy")){
                decoy=new GwentCard(my_library.get(i).getImg(),my_library.get(i).getPower(),my_library.get(i).getType(),my_library.get(i).getisHero(),my_library.get(i).getCol(),my_library.get(i).getId(),my_library.get(i).getName(),my_library.get(i).getProperty());
                break;
            }
        }
        Init_parameter();
        board=new GwentBoard(this);
        Init_RecyclerView(board);
        Init_hand();
        /*turn=1;
        enabled=true;*/
        if(turn==1) sendBroadcast(new Intent("my_game_begin"));
        else sendBroadcast(new Intent("opponent_game_begin"));
    }

    public void Init_library(){ //初始化卡组
        InputStream inputStream1=getResources().openRawResource(R.raw.northern_area);//初始化我方卡组
        try {
            String text=getString(inputStream1),tmp=new String();
            String information[]=new String[img1.length];
            int j=0;
            for(int i=0;i<text.length();i++){
                if(text.charAt(i)!='\n') tmp+=text.charAt(i);
                else{
                    information[j]=tmp;
                    j++;
                    tmp="";
                }
            }
            for(int i=0;i<information.length;i++){
                int power,col,num;
                boolean isHero;
                String type=new String(),power_tmp=new String(),name=new String(),property=new String();
                //power=information[i].charAt(0)-'0';
                for(j=0;j<information[i].length();j++){
                    if(information[i].charAt(j)!=' ') power_tmp+=information[i].charAt(j);
                    else break;
                }
                j++;
                power=Integer.valueOf(power_tmp).intValue();
                //Log.i("power",""+power);
                for(;j<information[i].length();j++){
                    if(information[i].charAt(j)!=' ') type+=information[i].charAt(j);
                    else break;
                }
                //Log.i("type",type);
                j++;
                isHero=information[i].charAt(j)=='1' ? true : false;
                //Log.i("ishero",isHero+"");
                j+=2;
                col=information[i].charAt(j)-'0';
                //Log.i("col",""+col);
                j+=2;
                num=information[i].charAt(j)-'0';
                j+=2;
                for(;j<information[i].length();j++){
                    if(information[i].charAt(j)==' ') break;
                    name+=information[i].charAt(j);
                }
                j++;
                for(;j<information[i].length();j++){
                    property+=information[i].charAt(j);
                }
                //Log.i("name",name);
                //Log.i("num",""+num);
                GwentCard temp=new GwentCard(img1[i],power,type,isHero,col,i,name,property);
                for(int k=0;k<num;k++) my_library.add(temp);
            }
            //Toast.makeText(this,getString(inputStream),Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream inputStream2=getResources().openRawResource(R.raw.monster);//初始化敌方卡组
        try {
            String text=getString(inputStream2),tmp=new String();
            String information[]=new String[img2.length];
            int j=0;
            for(int i=0;i<text.length();i++){
                if(text.charAt(i)!='\n') tmp+=text.charAt(i);
                else{
                    information[j]=tmp;
                    j++;
                    tmp="";
                }
            }
            for(int i=0;i<information.length;i++){
                int power,col,num,id;
                boolean isHero;
                String type=new String(),power_tmp=new String(),id_tmp=new String(),name=new String(),property=new String();
                for(j=0;j<information[i].length();j++){
                    if(information[i].charAt(j)!=' ') power_tmp+=information[i].charAt(j);
                    else break;
                }
                j++;
                power=Integer.valueOf(power_tmp).intValue();
                //Log.i("power",""+power);
                for(;j<information[i].length();j++){
                    if(information[i].charAt(j)!=' ') type+=information[i].charAt(j);
                    else break;
                }
                //Log.i("type",type);
                j++;
                isHero=information[i].charAt(j)=='1' ? true : false;
                j+=2;
                col=information[i].charAt(j)-'0';col*=-1;
                //Log.i("col",""+col);
                j+=2;
                for(;j<information[i].length();j++){
                    if(information[i].charAt(j)!=' ') id_tmp+=information[i].charAt(j);
                    else break;
                }
                id=Integer.valueOf(id_tmp).intValue();
                //Log.i("id",""+id);
                j++;
                num=information[i].charAt(j)-'0';
                //Log.i("num",""+num);
                j+=2;
                for(;j<information[i].length();j++){
                    if(information[i].charAt(j)==' ') break;
                    name+=information[i].charAt(j);
                }
                j++;
                for(;j<information[i].length();j++){
                    property+=information[i].charAt(j);
                }
                //Log.i("name",name);
                GwentCard temp=new GwentCard(img2[i],power,type,isHero,col,id,name,property);
                for(int k=0;k<num;k++) opponent_library.add(temp);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public void Init_parameter(){
        enabled=false;
        enabled_resurrection=false;
        enabled_decoy=false;
        my_give_up=false;
        opponent_give_up=false;
        is_my_round_end=false;
        my_life=2;
        opponent_life=2;
        Random random=new Random();
        int tmp=random.nextInt();
        if(tmp%2==0) turn=1;
        else turn=-1;
    }

    public void Init_RecyclerView(final GwentBoard board){ //初始化board的RecyclerView
        LinearLayoutManager linearLayoutManager1=new LinearLayoutManager(this);
        linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);//设置每个View横向布置
        board.opponent_third=(RecyclerView) findViewById(R.id.opponent_third);
        board.opponent_third.setLayoutManager(linearLayoutManager1);
        //board.opponent_third.setAdapter(board.opponent_third_adapter);
        AlphaInAnimationAdapter alphaInAnimationAdapter1=new AlphaInAnimationAdapter(board.opponent_third_adapter);
        alphaInAnimationAdapter1.setInterpolator(new OvershootInterpolator());
        board.opponent_third.setAdapter(alphaInAnimationAdapter1);

        LinearLayoutManager linearLayoutManager2=new LinearLayoutManager(this);
        linearLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        board.opponent_second=(RecyclerView) findViewById(R.id.opponent_second);
        board.opponent_second.setLayoutManager(linearLayoutManager2);
        //board.opponent_second.setAdapter(board.opponent_second_adapter);
        AlphaInAnimationAdapter alphaInAnimationAdapter2=new AlphaInAnimationAdapter(board.opponent_second_adapter);
        alphaInAnimationAdapter2.setInterpolator(new OvershootInterpolator());
        board.opponent_second.setAdapter(alphaInAnimationAdapter2);

        LinearLayoutManager linearLayoutManager3=new LinearLayoutManager(this);
        linearLayoutManager3.setOrientation(LinearLayoutManager.HORIZONTAL);
        board.opponent_first=(RecyclerView) findViewById(R.id.opponent_first);
        board.opponent_first.setLayoutManager(linearLayoutManager3);
        //board.opponent_first.setAdapter(board.opponent_first_adapter);
        AlphaInAnimationAdapter alphaInAnimationAdapter3=new AlphaInAnimationAdapter(board.opponent_first_adapter);
        alphaInAnimationAdapter3.setInterpolator(new OvershootInterpolator());
        board.opponent_first.setAdapter(alphaInAnimationAdapter3);

        LinearLayoutManager linearLayoutManager4=new LinearLayoutManager(this);
        linearLayoutManager4.setOrientation(LinearLayoutManager.HORIZONTAL);
        board.my_first=(RecyclerView) findViewById(R.id.my_first);
        board.my_first.setLayoutManager(linearLayoutManager4);
        board.my_first_adapter.setItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void OnClick(View v, int position) {
                if(enabled_decoy){
                    GwentCard tmp=board.my_first_list.get(position);
                    if(tmp.getCol()<0) tmp.setCol(tmp.getCol()*(-1));
                    my_hand.add(tmp);
                    board.my_first_list.remove(position);
                    board.my_first_list.add(decoy);
                    enabled_decoy=false;
                    board.Update();//更新数据适配器和每行的TextView
                    Update_Textview(board);
                }
            }
        });
        //board.my_first.setAdapter(board.my_first_adapter);
        AlphaInAnimationAdapter alphaInAnimationAdapter4=new AlphaInAnimationAdapter(board.my_first_adapter);
        alphaInAnimationAdapter4.setInterpolator(new OvershootInterpolator());
        board.my_first.setAdapter(alphaInAnimationAdapter4);

        LinearLayoutManager linearLayoutManager5=new LinearLayoutManager(this);
        linearLayoutManager5.setOrientation(LinearLayoutManager.HORIZONTAL);
        board.my_second=(RecyclerView) findViewById(R.id.my_second);
        board.my_second.setLayoutManager(linearLayoutManager5);
        board.my_second_adapter.setItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void OnClick(View v, int position) {
                if(enabled_decoy){
                    GwentCard tmp=board.my_second_list.get(position);
                    if(tmp.getCol()<0) tmp.setCol(tmp.getCol()*(-1));
                    my_hand.add(tmp);
                    board.my_second_list.remove(position);
                    board.my_second_list.add(decoy);
                    enabled_decoy=false;
                    board.Update();//更新数据适配器和每行的TextView
                    Update_Textview(board);
                }
            }
        });
        //board.my_second.setAdapter(board.my_second_adapter);
        AlphaInAnimationAdapter alphaInAnimationAdapter5=new AlphaInAnimationAdapter(board.my_second_adapter);
        alphaInAnimationAdapter5.setInterpolator(new OvershootInterpolator());
        board.my_second.setAdapter(alphaInAnimationAdapter5);

        LinearLayoutManager linearLayoutManager6=new LinearLayoutManager(this);
        linearLayoutManager6.setOrientation(LinearLayoutManager.HORIZONTAL);
        board.my_third=(RecyclerView) findViewById(R.id.my_third);
        board.my_third.setLayoutManager(linearLayoutManager6);
        board.my_third_adapter.setItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void OnClick(View v, int position) {
                if(enabled_decoy){
                    GwentCard tmp=board.my_third_list.get(position);
                    if(tmp.getCol()<0) tmp.setCol(tmp.getCol()*(-1));
                    my_hand.add(tmp);
                    board.my_third_list.remove(position);
                    board.my_third_list.add(decoy);
                    enabled_decoy=false;
                    board.Update();//更新数据适配器和每行的TextView
                    Update_Textview(board);
                }
            }
        });
        //board.my_third.setAdapter(board.my_third_adapter);
        AlphaInAnimationAdapter alphaInAnimationAdapter6=new AlphaInAnimationAdapter(board.my_third_adapter);
        alphaInAnimationAdapter6.setInterpolator(new OvershootInterpolator());
        board.my_third.setAdapter(alphaInAnimationAdapter6);
    }
    public static String getString(InputStream inputStream) throws IOException { //读取文件转为String
        InputStreamReader inputStreamReader=null;
        inputStreamReader=new InputStreamReader(inputStream);
        BufferedReader reader=new BufferedReader(inputStreamReader);
        StringBuffer sb=new StringBuffer("");
        String line;
        while ((line=reader.readLine())!=null){
            sb.append(line);
            sb.append("\n");
        }
        return sb.toString();
    }
    public void Update_Textview(GwentBoard board){ //根据传入的board中的数据更新TextView
        TextView textView1=(TextView) findViewById(R.id.opponent_third_num_power);
        textView1.setText(""+board.opponent_third_power);
        TextView textView2=(TextView) findViewById(R.id.opponent_second_num_power);
        textView2.setText(""+board.opponent_second_power);
        TextView textView3=(TextView) findViewById(R.id.opponent_first_num_power);
        textView3.setText(""+board.opponent_first_power);
        TextView textView4=(TextView) findViewById(R.id.my_first_num_power);
        textView4.setText(""+board.my_first_power);
        TextView textView5=(TextView) findViewById(R.id.my_second_num_power);
        textView5.setText(""+board.my_second_power);
        TextView textView6=(TextView) findViewById(R.id.my_third_num_power);
        textView6.setText(""+board.my_third_power);

        TextView my_power=(TextView) findViewById(R.id.my_power);
        int my_power_num=board.my_first_power+board.my_second_power+board.my_third_power;
        my_power.setText(""+my_power_num);
        TextView opponent_power=(TextView) findViewById(R.id.opponent_power);
        int opponent_power_num=board.opponent_first_power+board.opponent_second_power+board.opponent_third_power;
        opponent_power.setText(""+opponent_power_num);

        TextView my_life_view=(TextView) findViewById(R.id.my_life);
        my_life_view.setText("我方剩余生命："+my_life);
        TextView opponent_life_view=(TextView) findViewById(R.id.oppenent_life);
        opponent_life_view.setText("敌方剩余生命："+opponent_life);
    }

    public void Init_hand(){ //初始化双方手牌
        for(int i=0;i<10;i++){
            Random random=new Random();
            int pos=random.nextInt(my_library.size());
            my_hand.add(my_library.get(pos));
            my_library.remove(pos);
        }
        for(int i=0;i<10;i++){
            Random random=new Random();
            int pos=random.nextInt(opponent_library.size());
            opponent_hand.add(opponent_library.get(pos));
            opponent_library.remove(pos);
        }
    }
    public void Use_Card(final GwentBoard board, final GwentCard card){ //将卡添加到board上并发挥效果，没有从手牌中删除
        if(card.getType().equals("human")){
            board.AddCard(card.getCol(),card);
        }
        else if(card.getType().equals("friends")){
            int num=1;
            if(card.getCol()==1){
                for(int i=0;i<board.my_first_list.size();i++){
                    if(board.my_first_list.get(i).getType().equals("friends")&&board.my_first_list.get(i).getId()==card.getId()) num++;
                }
                board.AddCard(card.getCol(),card);
                if(num!=1) Toast.makeText(MainActivityGame.this,card.getName()+"发动同袍之情，点数倍增",Toast.LENGTH_SHORT).show();
                for(int i=0;i<board.my_first_list.size();i++){
                    if(board.my_first_list.get(i).getType().equals("friends")&&board.my_first_list.get(i).getId()==card.getId()) board.my_first_list.get(i).now_power=board.my_first_list.get(i).getPower()*num;
                    if(!board.my_first_list.get(i).isDouble&&board.my_first_special_card){
                        board.my_first_list.get(i).now_power*=2;
                        board.my_first_list.get(i).isDouble=true;
                    }
                    else if(board.my_first_list.get(i).isDouble) board.my_first_list.get(i).now_power*=2;//应该被号角加倍
                    Log.i(board.my_first_list.get(i).getName(),""+board.my_first_list.get(i).now_power);
                }
            }
            else if(card.getCol()==2){
                for(int i=0;i<board.my_second_list.size();i++){
                    if(board.my_second_list.get(i).getType().equals("friends")&&board.my_second_list.get(i).getId()==card.getId()) num++;
                }
                board.AddCard(card.getCol(),card);
                if(num!=1) Toast.makeText(MainActivityGame.this,card.getName()+"发动同袍之情，点数倍增",Toast.LENGTH_SHORT).show();
                for(int i=0;i<board.my_second_list.size();i++){
                    if(board.my_second_list.get(i).getType().equals("friends")&&board.my_second_list.get(i).getId()==card.getId()) board.my_second_list.get(i).now_power=board.my_second_list.get(i).getPower()*num;
                    if(!board.my_second_list.get(i).isDouble&&board.my_second_special_card){
                        board.my_second_list.get(i).now_power*=2;
                        board.my_second_list.get(i).isDouble=true;
                        Log.i("同袍2","倍增");
                    }
                    else if(board.my_second_list.get(i).isDouble) board.my_second_list.get(i).now_power*=2;
                }
            }
            else if(card.getCol()==3){
                for(int i=0;i<board.my_third_list.size();i++){
                    if(board.my_third_list.get(i).getType().equals("friends")&&board.my_third_list.get(i).getId()==card.getId()) num++;
                }
                board.AddCard(card.getCol(),card);
                if(num!=1) Toast.makeText(MainActivityGame.this,card.getName()+"发动同袍之情，点数倍增",Toast.LENGTH_SHORT).show();
                for(int i=0;i<board.my_third_list.size();i++){
                    if(board.my_third_list.get(i).getType().equals("friends")&&board.my_third_list.get(i).getId()==card.getId()) board.my_third_list.get(i).now_power=board.my_third_list.get(i).getPower()*num;
                    if(!board.my_third_list.get(i).isDouble&&board.my_third_special_card) {
                        board.my_third_list.get(i).now_power*=2;
                        board.my_third_list.get(i).isDouble=true;
                        Log.i("同袍3","倍增");
                    }
                    else if(board.my_third_list.get(i).isDouble) board.my_third_list.get(i).now_power*=2;
                }
            }
        }
        else if (card.getType().equals("spy")){
            if(card.getCol()>0){ //我方卡组的间谍
                board.AddCard(-1*card.getCol(),card);
                Toast.makeText(MainActivityGame.this,"发动间谍特效，我方额外获得两张卡",Toast.LENGTH_SHORT).show();
                for(int i=0;i<2;i++){
                    Random random=new Random();
                    int tmp=random.nextInt(my_library.size());
                    my_hand.add(my_library.get(tmp));
                    my_library.remove(tmp);
                }
            }
            else {
                board.AddCard(-1*card.getCol(),card);
                Toast.makeText(MainActivityGame.this,"发动间谍特效，敌方额外获得两张卡",Toast.LENGTH_SHORT).show();
                for(int i=0;i<2;i++){
                    Random random=new Random();
                    int tmp=random.nextInt(opponent_library.size());
                    opponent_hand.add(opponent_library.get(tmp));
                    opponent_library.remove(tmp);
                }
            }
        }
        else if (card.getType().equals("doctor")){
            if(card.getCol()>0){
                board.AddCard(card.getCol(),card);
                if(my_dust.size()!=0){
                    enabled_resurrection=true;//复活使能置true
                    enabled=false;//出牌使能置false
                    Toast.makeText(MainActivityGame.this,"请从墓地中选择需要复活的卡",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent("android.intent.action.Hand_and_Dust"));
                }

            }
            else { //电脑使用医生卡，计划是优先复活间谍，其次复活点数最高的卡

            }
        }
        else if (card.getType().equals("fire")){
            board.AddCard(card.getCol(),card);
            if(card.getCol()>0){ //我方使用三寒鸦
                if(board.opponent_first_power>=10){ //敌方近战力量>10，摧毁最强卡
                    int max_power=0;
                    for(int i=0;i<board.oppeonent_first_list.size();i++){
                        if(board.oppeonent_first_list.get(i).now_power>max_power&&!board.oppeonent_first_list.get(i).getisHero()) max_power=board.oppeonent_first_list.get(i).now_power;
                    }
                    Toast.makeText(MainActivityGame.this,"火灼发动",Toast.LENGTH_SHORT).show();
                    for(int i=0;i<board.oppeonent_first_list.size();i++){
                        if(board.oppeonent_first_list.get(i).now_power==max_power&&!board.oppeonent_first_list.get(i).getisHero()){
                            opponent_dust.add(board.oppeonent_first_list.get(i));
                            board.oppeonent_first_list.remove(i);
                            i--;
                        }
                    }
                }
            }
            else{ //敌方使用三寒鸦
                if(board.my_first_power>=10){
                    int max_power=0;
                    for(int i=0;i<board.my_first_list.size();i++){
                        if(board.my_first_list.get(i).now_power>max_power&&!board.my_first_list.get(i).getisHero()) max_power=board.my_first_list.get(i).now_power;
                    }
                    Toast.makeText(MainActivityGame.this,"火灼发动",Toast.LENGTH_SHORT).show();
                    for(int i=0;i<board.my_first_list.size();i++){
                        if(board.my_first_list.get(i).now_power==max_power&&!board.my_first_list.get(i).getisHero()) {
                            my_dust.add(board.my_first_list.get(i));
                            board.my_first_list.remove(i);
                            i--;
                        }
                    }
                }
            }
        }
        else if(card.getType().equals("set")){
            int id=card.getId();
            for(int i=0;i<opponent_hand.size();i++){
                if(opponent_hand.get(i).getId()==id){
                    board.AddCard(opponent_hand.get(i).getCol(),opponent_hand.get(i));
                    opponent_hand.remove(i);
                    i--;
                }
            }
            for(int i=0;i<opponent_library.size();i++){
                if(opponent_library.get(i).getId()==id){
                    board.AddCard(opponent_library.get(i).getCol(),opponent_library.get(i));
                    opponent_library.remove(i);
                    i--;
                }
            }
            Toast.makeText(MainActivityGame.this,card.getName()+"发动集群特效，召唤同类",Toast.LENGTH_SHORT).show();
        }
        else if(card.getType().equals("decoy")){
            if(card.getCol()>0){ //我方使用诱饵
                enabled_decoy=true;
                Toast.makeText(MainActivityGame.this,"请选择需要替换的卡",Toast.LENGTH_SHORT).show();
            }
        }
        else if(card.getType().equals("commander_horn")){
            if(card.getCol()>0){ //我方行动
                AlertDialog.Builder talk=new AlertDialog.Builder(MainActivityGame.this);
                talk.setTitle("请选择领导号角放置的位置");
                String[] items=new String[]{"近战列","远程列","攻城列"};
                talk.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){ //近战列使用号角
                            if(!board.my_first_special_card){ //该列未使用号角
                                board.AddCard(1,card);
                                for(int i=0;i<board.my_first_list.size();i++){
                                    if(!board.my_first_list.get(i).getisHero()&&!board.my_first_list.get(i).isDouble){
                                        board.my_first_list.get(i).isDouble=true;
                                        board.my_first_list.get(i).now_power*=2;
                                    }
                                }
                                Toast.makeText(MainActivityGame.this,"我方近战列点数翻倍",Toast.LENGTH_SHORT).show();
                                board.my_first_special_card=true;
                            }
                            else {
                                Toast.makeText(MainActivityGame.this,"该列已有领导号角",Toast.LENGTH_SHORT).show();
                                my_hand.add(card);
                            }
                        }
                        else if(which==1){ //远程列使用号角
                            if(!board.my_second_special_card){
                                board.AddCard(2,card);
                                for(int i=0;i<board.my_second_list.size();i++){
                                    if(!board.my_second_list.get(i).getisHero()&&!board.my_second_list.get(i).isDouble) {
                                        board.my_second_list.get(i).isDouble=true;
                                        board.my_second_list.get(i).now_power*=2;
                                    }
                                }
                                Toast.makeText(MainActivityGame.this,"我方远程列点数翻倍",Toast.LENGTH_SHORT).show();
                                board.my_second_special_card=true;
                            }
                            else {
                                Toast.makeText(MainActivityGame.this,"该列已有领导号角",Toast.LENGTH_SHORT).show();
                                my_hand.add(card);
                            }
                        }
                        else if(which==2){ //攻城列使用号角
                            if(!board.my_third_special_card){
                                board.AddCard(3,card);
                                for(int i=0;i<board.my_third_list.size();i++){
                                    if(!board.my_third_list.get(i).getisHero()&&!board.my_third_list.get(i).isDouble) {
                                        board.my_third_list.get(i).isDouble=true;
                                        board.my_third_list.get(i).now_power*=2;
                                    }
                                }
                                Toast.makeText(MainActivityGame.this,"我方攻城列点数翻倍",Toast.LENGTH_SHORT).show();
                                board.my_third_special_card=true;
                            }
                            else {
                                Toast.makeText(MainActivityGame.this,"该列已有领导号角",Toast.LENGTH_SHORT).show();
                                my_hand.add(card);
                            }
                        }
                        board.Update();//更新数据适配器和每行的TextView
                        Update_Textview(board);
                    }
                });
                talk.show();
            }
            else {
                if(!board.opponent_first_special_card){
                    board.AddCard(-1,card);
                    for(int i=0;i<board.oppeonent_first_list.size();i++){
                        if(!board.oppeonent_first_list.get(i).getisHero()){
                            board.oppeonent_first_list.get(i).isDouble=true;
                            board.oppeonent_first_list.get(i).now_power*=2;
                        }
                    }
                    Toast.makeText(MainActivityGame.this,"敌方近战列点数翻倍",Toast.LENGTH_SHORT).show();
                    board.opponent_first_special_card=true;
                    board.Update();//更新数据适配器和每行的TextView
                    Update_Textview(board);
                }
                else if(!board.opponent_second_special_card){
                    board.AddCard(-2,card);
                    for(int i=0;i<board.opponent_second_list.size();i++){
                        if(!board.opponent_second_list.get(i).getisHero()){
                            board.opponent_second_list.get(i).isDouble=true;
                            board.opponent_second_list.get(i).now_power*=2;
                        }
                    }
                    Toast.makeText(MainActivityGame.this,"敌方远程列点数翻倍",Toast.LENGTH_SHORT).show();
                    board.opponent_second_special_card=true;
                    board.Update();//更新数据适配器和每行的TextView
                    Update_Textview(board);
                }
                else if(!board.opponent_third_special_card){
                    board.AddCard(-3,card);
                    for(int i=0;i<board.opponent_third_list.size();i++){
                        if(!board.opponent_third_list.get(i).getisHero()){
                            board.opponent_third_list.get(i).isDouble=true;
                            board.opponent_third_list.get(i).now_power*=2;
                        }
                    }
                    Toast.makeText(MainActivityGame.this,"敌方远程列点数翻倍",Toast.LENGTH_SHORT).show();
                    board.opponent_third_special_card=true;
                    board.Update();//更新数据适配器和每行的TextView
                    Update_Textview(board);
                }
            }
        }
        board.Update();//更新数据适配器和每行的TextView
        Update_Textview(board);
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.to_hand_dust){
            startActivity(new Intent("android.intent.action.Hand_and_Dust"));
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if(v.getId()==R.id.give_up){
            sendBroadcast(new Intent("my_game_end"));
        }
        return false;
    }

    private BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) { //广播里从手牌删去了卡
            String action=intent.getAction();
            if(action.equals("use_card")){
                Bundle bundle=intent.getExtras();
                int pos=bundle.getInt("pos");
                Use_Card(board,my_hand.get(pos));
                my_hand.remove(pos);
                if(!opponent_give_up) sendBroadcast(new Intent("opponent_game_begin"));
                else sendBroadcast(new Intent("my_game_begin"));
                turn*=-1;
            }
            else if(action.equals("resave_card")){
                Bundle bundle=intent.getExtras();
                int pos=bundle.getInt("pos");
                if(my_dust.get(pos).getType().equals("spy")) my_dust.get(pos).setCol(my_dust.get(pos).getCol()*(-1));
                Use_Card(board,my_dust.get(pos));
                my_dust.remove(pos);
                if(!opponent_give_up) sendBroadcast(new Intent("opponent_game_begin"));
                else sendBroadcast(new Intent("my_game_begin"));
                turn*=-1;
            }
            is_my_round_end=true;
        }
    };
    private  BroadcastReceiver mBroadcastReceiver1=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if(action.equals("my_game_begin")){
                enabled=true;
                Toast.makeText(MainActivityGame.this,"我方行动",Toast.LENGTH_SHORT).show();
            }
            else if(action.equals("opponent_game_begin")){
                if(opponent_life>1&&my_give_up&&board.opponent_first_power+board.opponent_second_power+board.opponent_third_power>board.my_first_power+board.my_second_power+board.my_third_power){
                    sendBroadcast(new Intent("opponent_game_end"));
                }
                else if(opponent_life>1&&board.opponent_first_power+board.opponent_second_power+board.opponent_third_power>board.my_first_power+board.my_second_power+board.my_third_power+35){
                    sendBroadcast(new Intent("opponent_game_end"));
                }
                else {
                    boolean isUseCard=false;
                    while(true){
                        for(int i=0;i<opponent_hand.size();i++){
                            if(opponent_hand.get(i).getType().equals("spy")){
                                Toast.makeText(MainActivityGame.this,"敌方使用"+opponent_hand.get(i).getName(),Toast.LENGTH_SHORT).show();
                                Use_Card(board,opponent_hand.get(i));
                                opponent_hand.remove(i);
                                isUseCard=true;
                                break;
                            }
                        }
                        if(isUseCard) break;
                        for(int i=0;i<opponent_hand.size();i++){
                            if(opponent_hand.get(i).getType().equals("decoy")&&isSpyExist()!=-999){
                                Toast.makeText(MainActivityGame.this,"敌方使用诱饵替换间谍",Toast.LENGTH_SHORT).show();
                                opponent_hand.remove(i);
                                if(isSpyExist()==-1){
                                    for(int j=0;j<board.oppeonent_first_list.size();j++){
                                        if(board.oppeonent_first_list.get(j).getType().equals("spy")&&!board.oppeonent_first_list.get(j).getisHero()){
                                            board.oppeonent_first_list.get(j).setCol(board.oppeonent_first_list.get(j).getCol()*(-1));
                                            opponent_hand.add(board.oppeonent_first_list.get(j));
                                            board.oppeonent_first_list.remove(j);
                                            board.oppeonent_first_list.add(decoy);
                                            break;
                                        }
                                    }
                                }
                                else{
                                    for(int j=0;j<board.opponent_third_list.size();j++){
                                        if(board.opponent_third_list.get(j).getType().equals("spy")&&!board.opponent_third_list.get(j).getisHero()){
                                            board.opponent_third_list.get(j).setCol(board.opponent_third_list.get(j).getCol()*(-1));
                                            opponent_hand.add(board.opponent_third_list.get(j));
                                            board.opponent_third_list.remove(j);
                                            board.opponent_third_list.add(decoy);
                                            break;
                                        }
                                    }
                                }
                                isUseCard=true;
                                break;
                            }
                        }
                        if(isUseCard) break;
                        for(int i=0;i<opponent_hand.size();i++){
                            if(opponent_hand.get(i).getType().equals("doctor")&&opponent_dust.size()>=6){
                                board.AddCard(-2,opponent_hand.get(i));
                                opponent_hand.remove(i);
                                boolean flag=false;
                                for(int j=0;j<opponent_dust.size();j++){
                                    if(opponent_dust.get(j).getType().equals("spy")&&!opponent_dust.get(j).getisHero()){
                                        opponent_dust.get(j).setCol(opponent_dust.get(j).getCol()*(-1));
                                        Use_Card(board,opponent_dust.get(j));
                                        opponent_dust.remove(j);
                                        flag=true;
                                        break;
                                    }
                                }
                                if(!flag){
                                    int max=0;
                                    for(int j=0;j<opponent_dust.size();j++){
                                        if(opponent_dust.get(j).getPower()>max&&!opponent_dust.get(j).getisHero()){
                                            max=opponent_dust.get(j).getPower();
                                        }
                                    }
                                    for(int j=0;j<opponent_dust.size();j++){
                                        if(opponent_dust.get(j).getPower()==max&&!opponent_dust.get(j).getisHero()){
                                            board.AddCard(opponent_dust.get(j).getCol(),opponent_dust.get(j));
                                            break;
                                        }
                                    }
                                }
                                isUseCard=true;
                                break;
                            }
                        }
                        if(isUseCard) break;
                        for(int i=0;i<opponent_hand.size();i++){
                            if(opponent_hand.get(i).getType().equals("fire")&&board.my_first_power>=30){
                                Use_Card(board,opponent_hand.get(i));
                                opponent_hand.remove(i);
                                isUseCard=true;
                                break;
                            }
                        }
                        if(isUseCard) break;
                        //set human horn
                        if(board.opponent_first_power>40&&!board.opponent_first_special_card){
                            for(int i=0;i<opponent_hand.size();i++){
                                if(opponent_hand.get(i).getType().equals("commander_horn")){
                                    Use_Card(board,opponent_hand.get(i));
                                    opponent_hand.remove(i);
                                    isUseCard=true;
                                    break;
                                }
                            }
                            if(isUseCard) break;
                        }
                        if(board.my_first_power+board.my_second_power+board.my_third_power>board.opponent_first_power+board.opponent_second_power+board.opponent_third_power+30){
                            for(int i=0;i<opponent_hand.size();i++){
                                if(opponent_hand.get(i).getType().equals("set")){
                                    Use_Card(board,opponent_hand.get(i));
                                    isUseCard=true;
                                    break;
                                }
                            }
                            if(isUseCard) break;
                        }
                        else {
                            for(int i=0;i<opponent_hand.size();i++){
                                if(opponent_hand.get(i).getType().equals("human")){
                                    Use_Card(board,opponent_hand.get(i));
                                    opponent_hand.remove(i);
                                    isUseCard=true;
                                    break;
                                }
                            }
                            if(isUseCard) break;
                        }
                        for(int i=0;i<opponent_hand.size();i++){
                            if(opponent_hand.get(i).getType().equals("set")){
                                Use_Card(board,opponent_hand.get(i));
                                isUseCard=true;
                                break;
                            }
                            else{
                                Use_Card(board,opponent_hand.get(i));
                                opponent_hand.remove(i);
                                isUseCard=true;
                                break;
                            }
                        }
                        break;
                    }
                    turn*=-1;
                    if(!isUseCard) sendBroadcast(new Intent("opponent_game_end"));
                    else {
                        if(!my_give_up) sendBroadcast(new Intent("my_game_begin"));
                        else sendBroadcast(new Intent("opponent_game_begin"));
                    }
                }
            }
        }
    };
    private BroadcastReceiver mBroadcastReceiver2=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if(action.equals("my_game_end")){
                my_give_up=true;
                enabled=false;
                Toast.makeText(MainActivityGame.this,"我方放弃本轮",Toast.LENGTH_SHORT).show();
                if(opponent_give_up){
                    int my_power_num=board.my_first_power+board.my_second_power+board.my_third_power;
                    int opponent_power_num=board.opponent_first_power+board.opponent_second_power+board.opponent_third_power;
                    enabled=false;
                    enabled_resurrection=false;
                    enabled_decoy=false;
                    my_give_up=false;
                    opponent_give_up=false;
                    is_my_round_end=false;
                    if(my_power_num>opponent_power_num){ //我方获胜
                        Toast.makeText(MainActivityGame.this,"本轮我方获胜",Toast.LENGTH_SHORT);
                        opponent_life--;
                        if(opponent_life==0){
                            AlertDialog.Builder talk=new AlertDialog.Builder(MainActivityGame.this);
                            talk.setTitle("玩家胜出!");
                            talk.show();
                            //End_game();
                        }
                        else{
                            board.clear();
                            board.Update();
                            Update_Textview(board);
                            sendBroadcast(new Intent("my_game_begin"));
                        }
                    }
                    else if(my_power_num==opponent_power_num){
                        Toast.makeText(MainActivityGame.this,"本轮平局",Toast.LENGTH_SHORT);
                        my_life--;
                        opponent_life--;
                        if(opponent_life==0){
                            AlertDialog.Builder talk=new AlertDialog.Builder(MainActivityGame.this);
                            talk.setTitle("玩家胜出!");
                            talk.show();
                            //End_game();
                        }
                        else if(my_life==0){
                            AlertDialog.Builder talk=new AlertDialog.Builder(MainActivityGame.this);
                            talk.setTitle("对手胜出!");
                            talk.show();
                            //End_game();
                        }
                        else {
                            board.clear();
                            board.Update();
                            Update_Textview(board);
                            sendBroadcast(new Intent("my_game_begin"));
                        }
                    }
                    else {
                        Toast.makeText(MainActivityGame.this,"本轮敌方获胜",Toast.LENGTH_SHORT);
                        my_life--;
                        if(my_life==0){
                            AlertDialog.Builder talk=new AlertDialog.Builder(MainActivityGame.this);
                            talk.setTitle("对手胜出!");
                            talk.show();
                            //End_game();
                        }
                        else{
                            board.clear();
                            board.Update();
                            Update_Textview(board);
                            sendBroadcast(new Intent("opponent_game_begin"));
                        }
                    }
                    End_game();
                }
                else{
                    Toast.makeText(MainActivityGame.this,"test",Toast.LENGTH_SHORT);
                    sendBroadcast(new Intent("opponent_game_begin"));
                }
            }
            else if(action.equals("opponent_game_end")){
                opponent_give_up=true;
                Toast.makeText(MainActivityGame.this,"敌方放弃本轮",Toast.LENGTH_SHORT).show();
                if(my_give_up){
                    int my_power_num=board.my_first_power+board.my_second_power+board.my_third_power;
                    int opponent_power_num=board.opponent_first_power+board.opponent_second_power+board.opponent_third_power;
                    enabled=false;
                    enabled_resurrection=false;
                    enabled_decoy=false;
                    my_give_up=false;
                    opponent_give_up=false;
                    is_my_round_end=false;
                    if(my_power_num>opponent_power_num){ //我方获胜
                        Toast.makeText(MainActivityGame.this,"本轮我方获胜",Toast.LENGTH_SHORT);
                        opponent_life--;
                        if(opponent_life==0){
                            AlertDialog.Builder talk=new AlertDialog.Builder(MainActivityGame.this);
                            talk.setTitle("玩家胜出!");
                            talk.show();
                            End_game();
                        }
                        else{
                            board.clear();
                            board.Update();
                            Update_Textview(board);
                            sendBroadcast(new Intent("my_game_begin"));
                        }
                    }
                    else if(my_power_num==opponent_power_num){
                        Toast.makeText(MainActivityGame.this,"本轮平局",Toast.LENGTH_SHORT);
                        my_life--;
                        opponent_life--;
                        if(opponent_life==0){
                            AlertDialog.Builder talk=new AlertDialog.Builder(MainActivityGame.this);
                            talk.setTitle("玩家胜出!");
                            talk.show();
                            End_game();
                        }
                        else if(my_life==0){
                            AlertDialog.Builder talk=new AlertDialog.Builder(MainActivityGame.this);
                            talk.setTitle("对手胜出!");
                            talk.show();
                            End_game();
                        }
                        else {
                            board.clear();
                            board.Update();
                            Update_Textview(board);
                            sendBroadcast(new Intent("my_game_begin"));
                        }
                    }
                    else {
                        Toast.makeText(MainActivityGame.this,"本轮敌方获胜",Toast.LENGTH_SHORT);
                        my_life--;
                        if(my_life==0){
                            AlertDialog.Builder talk=new AlertDialog.Builder(MainActivityGame.this);
                            talk.setTitle("对手胜出!");
                            talk.show();
                            End_game();
                        }
                        else{
                            board.clear();
                            board.Update();
                            Update_Textview(board);
                            sendBroadcast(new Intent("opponent_game_begin"));
                        }
                    }
                }
                else{
                    sendBroadcast(new Intent("my_game_begin"));
                }
            }
        }
    };
    public int isSpyExist(){ //存在间谍则返回列数，否则返回-999
        for(int i=0;i<board.oppeonent_first_list.size();i++){
            if(board.oppeonent_first_list.get(i).getType().equals("spy")&&!board.oppeonent_first_list.get(i).getisHero()){
                return -1;
            }
        }
        for(int i=0;i<board.opponent_third_list.size();i++){
            if(board.opponent_third_list.get(i).getType().equals("spy")&&!board.opponent_third_list.get(i).getisHero()){
                return -3;
            }
        }
        return -999;
    }
    public void delay(int ms){
        try{
            Thread.currentThread();
            Thread.sleep(ms);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    public void End_game(){
        setResult(RESULT_OK);
        finish();
    }
}
