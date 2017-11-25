package cn.chonor.project_1;

/**
 * Created by Chonor on 2017/11/25.
 */

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;


import java.lang.reflect.GenericArrayType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ASUS on 2017/10/31.
 */

public class GwentBoard {
    public RecyclerView opponent_third;//敌方攻城列
    public RecyclerAdapter opponent_third_adapter;
    public List<GwentCard> opponent_third_list;
    public boolean opponent_third_special_card;//特殊卡是否已经使用，如号角，蘑菇，初始化为false
    public int opponent_third_power;

    public RecyclerView opponent_second;//敌方远程列
    public RecyclerAdapter opponent_second_adapter;
    public List<GwentCard> opponent_second_list;
    public boolean opponent_second_special_card;
    public int opponent_second_power;

    public RecyclerView opponent_first;//敌方近战列
    public RecyclerAdapter opponent_first_adapter;
    public List<GwentCard> oppeonent_first_list;
    public boolean opponent_first_special_card;
    public int opponent_first_power;

    public RecyclerView my_first;//我方近战列
    public RecyclerAdapter my_first_adapter;
    public List<GwentCard> my_first_list;
    public boolean my_first_special_card;
    public int my_first_power;

    public RecyclerView my_second;//我方远程列
    public RecyclerAdapter my_second_adapter;
    public List<GwentCard> my_second_list;
    public boolean my_second_special_card;
    public int my_second_power;

    public RecyclerView my_third;//我方攻城列
    public RecyclerAdapter my_third_adapter;
    public List<GwentCard> my_third_list;
    public boolean my_third_special_card;
    public int my_third_power;

    public GwentBoard(Context context){
        this.oppeonent_first_list=new ArrayList<>();
        this.opponent_first_adapter=new RecyclerAdapter(context,oppeonent_first_list);
        opponent_first_special_card=false;

        this.opponent_second_list=new ArrayList<>();
        this.opponent_second_adapter=new RecyclerAdapter(context,opponent_second_list);
        opponent_second_special_card=false;

        this.opponent_third_list=new ArrayList<>();
        this.opponent_third_adapter=new RecyclerAdapter(context,opponent_third_list);
        opponent_third_special_card=false;

        this.my_first_list=new ArrayList<>();
        this.my_first_adapter=new RecyclerAdapter(context,my_first_list);
        my_first_special_card=false;

        this.my_second_list=new ArrayList<>();
        this.my_second_adapter=new RecyclerAdapter(context,my_second_list);
        my_second_special_card=false;

        this.my_third_list=new ArrayList<>();
        this.my_third_adapter=new RecyclerAdapter(context,my_third_list);
        my_third_special_card=false;
    }

    public void AddCard(int pos,GwentCard card){//向牌局上增加卡牌，输入pos与card，pos：1-3为己方，依次为近战列、远程列、攻城列；-1~-3为敌方
        if(pos==1){//己方近战列增加卡牌
            this.my_first_list.add(card);
            //this.my_first_adapter.notifyDataSetChanged();
            this.my_first_adapter.notifyItemInserted(my_first_list.size()-1);
        }
        else if(pos==2){//己方远程列增加卡牌
            this.my_second_list.add(card);
            this.my_second_adapter.notifyDataSetChanged();
            //this.my_second_adapter.notifyItemInserted(my_second_list.size()-1);
        }
        else if(pos==3){//己方攻城列增加卡牌
            this.my_third_list.add(card);
            //this.my_third_adapter.notifyDataSetChanged();
            this.my_third_adapter.notifyItemInserted(my_third_list.size()-1);
        }
        else if(pos==-1){//敌方近战列增加卡牌
            this.oppeonent_first_list.add(card);
            //this.opponent_first_adapter.notifyDataSetChanged();
            this.opponent_first_adapter.notifyItemInserted(oppeonent_first_list.size()-1);
        }
        else if(pos==-2){//敌方远程列增加卡牌
            this.opponent_second_list.add(card);
            //this.my_second_adapter.notifyDataSetChanged();
            this.opponent_second_adapter.notifyItemInserted(opponent_second_list.size()-1);
        }
        else if(pos==-3){//敌方攻城列增加卡牌
            this.opponent_third_list.add(card);
            //this.opponent_third_adapter.notifyDataSetChanged();
            this.opponent_third_adapter.notifyItemInserted(opponent_third_list.size()-1);
        }
    }
    public void clear(){
        for(int i=0;i<my_first_list.size();i++){
            MainActivityGame.my_dust.add(my_first_list.get(i));
        }
        this.my_first_list.clear();
        for(int i=0;i<my_second_list.size();i++){
            MainActivityGame.my_dust.add(my_second_list.get(i));
        }
        this.my_second_list.clear();
        for(int i=0;i<my_third_list.size();i++){
            MainActivityGame.my_dust.add(my_third_list.get(i));
        }
        this.my_third_list.clear();
        for(int i=0;i<oppeonent_first_list.size();i++){
            MainActivityGame.opponent_dust.add(oppeonent_first_list.get(i));
        }
        this.oppeonent_first_list.clear();
        for(int i=0;i<opponent_second_list.size();i++){
            MainActivityGame.opponent_dust.add(opponent_second_list.get(i));
        }
        this.opponent_second_list.clear();
        for(int i=0;i<opponent_third_list.size();i++){
            MainActivityGame.opponent_dust.add(opponent_third_list.get(i));
        }
        this.opponent_third_list.clear();
        this.my_first_adapter.notifyDataSetChanged();
        this.my_second_adapter.notifyDataSetChanged();
        this.my_third_adapter.notifyDataSetChanged();
        this.opponent_first_adapter.notifyDataSetChanged();
        this.opponent_second_adapter.notifyDataSetChanged();
        this.opponent_third_adapter.notifyDataSetChanged();
        opponent_first_special_card=false;
        opponent_second_special_card=false;
        opponent_third_special_card=false;
        my_first_special_card=false;
        my_second_special_card=false;
        my_third_special_card=false;
    }
    public void Update(){
        //根据号角更新
        if(my_first_special_card){
            for(int i=0;i<my_first_list.size();i++){
                if(!my_first_list.get(i).getisHero()&&!my_first_list.get(i).isDouble){
                    my_first_list.get(i).now_power*=2;
                    my_first_list.get(i).isDouble=true;
                    Log.i("update","1");
                }
            }
        }
        if(my_second_special_card){
            for(int i=0;i<my_second_list.size();i++){
                if(!my_second_list.get(i).getisHero()&&!my_second_list.get(i).isDouble){
                    my_second_list.get(i).now_power*=2;
                    my_second_list.get(i).isDouble=true;
                    Log.i("update","2");
                }
            }
        }
        if(my_third_special_card){
            for(int i=0;i<my_third_list.size();i++){
                if(!my_third_list.get(i).getisHero()&&!my_third_list.get(i).isDouble){
                    my_third_list.get(i).now_power*=2;
                    my_third_list.get(i).isDouble=true;
                    Log.i("update","3");
                }
            }
        }
        if(opponent_first_special_card){
            for(int i=0;i<oppeonent_first_list.size();i++){
                if(!oppeonent_first_list.get(i).getisHero()&&!oppeonent_first_list.get(i).isDouble){
                    oppeonent_first_list.get(i).now_power*=2;
                    oppeonent_first_list.get(i).isDouble=true;
                }
            }
        }
        if(opponent_second_special_card){
            for(int i=0;i<opponent_second_list.size();i++){
                if(!opponent_second_list.get(i).getisHero()&&!opponent_second_list.get(i).isDouble){
                    opponent_second_list.get(i).now_power*=2;
                    opponent_second_list.get(i).isDouble=true;
                }
            }
        }
        if(opponent_third_special_card){
            for(int i=0;i<opponent_third_list.size();i++){
                if(!opponent_third_list.get(i).getisHero()&&!opponent_third_list.get(i).isDouble){
                    opponent_third_list.get(i).now_power*=2;
                    opponent_third_list.get(i).isDouble=true;
                }
            }
        }

        this.my_first_power=0;
        this.my_second_power=0;
        this.my_third_power=0;
        this.opponent_first_power=0;
        this.opponent_second_power=0;
        this.opponent_third_power=0;
        for(int i=0;i<my_first_list.size();i++){
            this.my_first_power+=my_first_list.get(i).now_power;
        }
        for(int i=0;i<my_second_list.size();i++){
            this.my_second_power+=my_second_list.get(i).now_power;
        }
        for(int i=0;i<my_third_list.size();i++){
            this.my_third_power+=my_third_list.get(i).now_power;
        }
        for(int i=0;i<oppeonent_first_list.size();i++){
            this.opponent_first_power+=oppeonent_first_list.get(i).now_power;
        }
        for(int i=0;i<opponent_second_list.size();i++){
            this.opponent_second_power+=opponent_second_list.get(i).now_power;
        }
        for(int i=0;i<opponent_third_list.size();i++){
            this.opponent_third_power+=opponent_third_list.get(i).now_power;
        }

        this.opponent_third_adapter.notifyDataSetChanged();
        this.opponent_second_adapter.notifyDataSetChanged();
        this.opponent_first_adapter.notifyDataSetChanged();
        this.my_first_adapter.notifyDataSetChanged();
        this.my_second_adapter.notifyDataSetChanged();
        this.my_third_adapter.notifyDataSetChanged();
    }
}

