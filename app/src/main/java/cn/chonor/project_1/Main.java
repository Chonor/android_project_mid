package cn.chonor.project_1;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.CompoundButton;
import android.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.chonor.project_1.R.attr.alpha;

public class Main extends AppCompatActivity {
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    private static int ADD=0;
    private static int INFO=1;
    private static int INFO_SEARCH=2;
    ArrayList<Data>datas=new ArrayList<>();
    ArrayList<Data>search_datas=new ArrayList<>();
    private RecyclerView mRecyclerView = null;
    private RecyclerView mRecyclerView_search = null;
    private CommonAdapter commonAdapter;
    private CommonAdapter commonAdapter_search;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected RecyclerView.LayoutManager mLayoutManager_search;
    private TabHost tabHost;
    private Button reset;
    private TextView edit;
    private ImageView main_add;
    private boolean Is_edit;
    private ArrayList<String>choose_list;
    private int display=0,country=0,sex=2,music=0;
    private RadioGroup setting_display,search_country,search_sex;
    private RadioButton display1,display2,display3;
    private RadioButton country1, country2,country3,country_all;
    private RadioButton sex_all,sex1,sex2;
    private Switch switch_music;
    private SearchView searchView;
    private int click_position=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Init();
        Load_Database();
        Change_display();
        Init_Listener();
    }
    //展示部分数据
    private void Insert_main_Data(){
        String name[] ={"董卓","吕布","曹操","刘备","孙权","荀彧","张辽","徐晃","许褚","夏侯惇","王朗","诸葛亮","庞统","关羽","张飞","马超","黄忠","赵云","魏延","周瑜","鲁肃","吕蒙","甘宁","太史慈","黄盖","袁绍","袁术","貂蝉","黄月英","张郃","马谡"};
        for(int i=0;i<name.length;i++){
            Data tmp=databaseHelper.Search_Name(name[i]);
            if(tmp!=null)databaseHelper.Insert_back_mainid(tmp);
        }
    }
    //数据库操作
    private void Load_Database(){
        if(!databaseHelper.tabIsExist("role")){//总数据库
            databaseHelper.getWritableDatabase();
            databaseHelper.Create_role();
            Read_Write_Data();
        }
        if(!databaseHelper.tabIsExist("main")) {//主界面数据库
            databaseHelper.Create_main();
            Insert_main_Data();
        }
        if(!databaseHelper.tabIsExist("settings")){
            databaseHelper.Create_settings();
            databaseHelper.Insert_settings(music,display);
        }
        datas=databaseHelper.Select_main_data();
        if(datas.size()==0){//主界面数据被清空
            Insert_main_Data();
            datas=databaseHelper.Select_main_data();
        }
        int display_music=databaseHelper.Select_settings();
        display=display_music%10;
        music=display_music/10;
        ((RadioButton)setting_display.getChildAt(display)).setChecked(true);
        switch_music.setChecked(music==1);
    }
    //获取控件及其初始化
    private void Init(){
        display=0;country=1;sex=2;click_position=-1;
        Is_edit=false;
        mRecyclerView = (RecyclerView) findViewById(R.id.mainrecyclerview); //初始化RecyclerView
        mRecyclerView_search=(RecyclerView)findViewById(R.id.search_info);
        //主界面 编辑
        edit=(TextView)findViewById(R.id.main_edit);
        main_add=(ImageView)findViewById(R.id.main_add);


        searchView=(SearchView)findViewById(R.id.main_search);
        searchView.setSubmitButtonEnabled(true);

        //搜索条件 势力
        search_country=(RadioGroup)findViewById(R.id.search_country);
        country_all=(RadioButton)findViewById(R.id.search_all_country);
        country1=(RadioButton)findViewById(R.id.search_wei);
        country2=(RadioButton)findViewById(R.id.search_shu);
        country3=(RadioButton)findViewById(R.id.search_wu);
        //搜索条件 性别
        search_sex=(RadioGroup)findViewById(R.id.search_sex);
        sex_all=(RadioButton)findViewById(R.id.search_sex_all);
        sex1=(RadioButton)findViewById(R.id.search_man);
        sex2=(RadioButton)findViewById(R.id.search_woman);
        //设置显示方式
        setting_display=(RadioGroup)findViewById(R.id.settings_display);
        display1=(RadioButton)findViewById(R.id.settings_display1);
        display2=(RadioButton)findViewById(R.id.settings_display2);
        display3=(RadioButton)findViewById(R.id.settings_display3);
        //设置音乐
        switch_music=(Switch)findViewById(R.id.settings_music);
        //数据还原
        reset=(Button)findViewById(R.id.reset);

        //主界面注册 切换
        tabHost=(TabHost)findViewById(R.id.tabhost);
        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec("one").setIndicator("词典主页").setContent(R.id.dictionary));
        tabHost.addTab(tabHost.newTabSpec("two").setIndicator("信息检索").setContent(R.id.Search));
        tabHost.addTab(tabHost.newTabSpec("thr").setIndicator("设置").setContent(R.id.Settings));

    }
    //界面main监听
    private void Init_Listener_Main(){
        //进入编辑界面
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Is_edit) {//处于编辑模式点击 退出编辑模式
                    Is_edit = false;
                    edit.setText("Edit");
                    main_add.setVisibility(View.INVISIBLE);
                    if(choose_list.size()>0) {
                        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(Main.this);
                        alertDialogBuilder.setTitle("即将删除"+String.valueOf(choose_list.size())+"条记录")
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Collections.sort(choose_list); //判断删除
                                        for (int j = choose_list.size() - 1; j >= 0; j--) {//从大到小删除避免位置改变
                                            Data tmp = datas.get((int) Integer.valueOf(choose_list.get(j)));
                                            if (tmp != null)
                                                databaseHelper.Delete_main(tmp.getId());
                                            datas.remove(tmp);
                                            commonAdapter.notifyDataSetChanged();
                                        }
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(getApplicationContext(), "删除取消", Toast.LENGTH_LONG).show();
                                        Change_display();
                                    }
                                })
                                .setCancelable(true)
                                .create().show();
                    }
                } else { //进入编辑模式
                    Is_edit = true;
                    edit.setText("Done");
                    main_add.setVisibility(View.VISIBLE);
                    choose_list = new ArrayList<String>();
                }
            }
        });
        //增加人物界面监听
        main_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (main_add.getVisibility() == View.VISIBLE) {
                    Intent i = new Intent(Main.this, Add_Role.class);
                    startActivityForResult(i, ADD);
                }
            }
        });
    }
    //界面search监听
    private void Init_Listener_Search(){
        //搜索框监听
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setQuery("　",false); //为空时设置全文搜索
            }
        });
        //搜索按钮监听
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                searchView.onActionViewCollapsed();
                search_datas=databaseHelper.Search_Data(query.replace("　",""),country,sex); //为空时设置全文搜索
                Toast.makeText(Main.this,"检索出" + String.valueOf(search_datas.size()+"条记录"),Toast.LENGTH_LONG).show();
                Change_display();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        //搜索条件 国家 设置变化监听
        search_country.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(country1.getId()==i)country=1;
                else if(country2.getId()==i)country=2;
                else if(country3.getId()==i)country=3;
                else country=0;
            }
        });
        //搜索条件性别 设置变化监听
        search_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(sex1.getId()==i)sex=1;
                else if(sex2.getId()==i)sex=0;
                else sex=2;
            }
        });
    }
    //界面setting监听
    private void Init_Listener_Settings(){
        //数据还原监听
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(Main.this);
                alertDialogBuilder.setTitle("WARNNING:数据库数据将初始化")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                datas=new ArrayList<>();
                                search_datas=new ArrayList<Data>();
                                databaseHelper.Drop_Tab("main");
                                databaseHelper.Drop_Tab("role");
                                databaseHelper.Create_role();
                                Read_Write_Data();
                                Load_Database();
                                Change_display();//重新加载
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getApplicationContext(), "初始化取消", Toast.LENGTH_LONG).show();
                                Change_display();
                            }
                        })
                        .setCancelable(true)
                        .create().show();
            }
        });
        //修改界面显示方式
        setting_display.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                int display_choose;
                if (display1.getId() == i)display_choose = 0;
                else if (display2.getId() == i) display_choose = 1;
                else display_choose = 2;
                //如果不变就不重新加载了
                if (display_choose != display) {
                    display = display_choose;
                    Change_display();
                    databaseHelper.Updata_settings(music,display);
                }
            }
        });
        switch_music.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Toast.makeText(Main.this,"音乐打开",Toast.LENGTH_SHORT).show();
                    music=1;
                    databaseHelper.Updata_settings(music,display);
                }
                else {
                    Toast.makeText(Main.this,"音乐关闭",Toast.LENGTH_SHORT).show();
                    music=0;
                    databaseHelper.Updata_settings(music,display);
                }
            }
        });

    }
    private void Init_Listener() {
        Init_Listener_Main();
        Init_Listener_Search();
        Init_Listener_Settings();
    }
    //RecyclerView item 点击事件监听
    private void RecyclerView_Init_Listener(){
        commonAdapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position, CommonAdapter.ViewHolder viewHolder) {
                if(position>=0) {//确保不会溢出
                    if(Is_edit){//在编辑模式，进入点击进入多选删除
                        if(display==0) { //根据显示模式不同设置不同点击标识
                            TextView choose = viewHolder.getView(R.id.item_choose);
                            if (!choose.getText().equals("    ")) {
                                choose.setText("    ");
                                choose_list.add(String.valueOf(position));
                            } else {
                                choose.setText("");
                                choose_list.remove(String.valueOf(position));
                            }
                        }
                        else{//宫格模式显示设置标识
                            ImageView choose = viewHolder.getView(display==1?R.id.item1_choose:R.id.item2_choose);
                            if (choose.getVisibility()==View.INVISIBLE) {
                                choose.setVisibility(View.VISIBLE);
                                choose_list.add(String.valueOf(position));
                            } else {
                                choose.setVisibility(View.INVISIBLE);
                                choose_list.remove(String.valueOf(position));
                            }
                        }
                    }
                    else {//非编辑模式 点击打开人物信息
                        click_position = position;
                        Intent i = new Intent(Main.this, Info.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("id", datas.get(position).getId());
                        i.putExtra("mainActivity", bundle);
                        startActivityForResult(i, INFO);
                    }
                }
            }
            @Override
            public void onLongClick(final int position,CommonAdapter.ViewHolder viewHolder) {//长按删除
                if(position>=0) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Main.this);
                    alertDialogBuilder.setTitle("删除人物")
                            .setMessage("确定删除人物？")
                            .setIcon(android.R.drawable.ic_dialog_info);
                    alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(Main.this,"取消删除",Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertDialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            databaseHelper.Delete_main(datas.get(position).getId());
                            datas.remove(position);
                            commonAdapter.notifyDataSetChanged();
                        }
                    });
                    alertDialogBuilder.setCancelable(true);//误点击可取消
                    alertDialogBuilder.create().show();
                }
            }
        });
    }
    //Search 界面的RecyclerView item 点击事件监听
    private void Search_RecyclerView_Init_Listener(){
        commonAdapter_search.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position, CommonAdapter.ViewHolder viewHolder) {
                Intent i = new Intent(Main.this, Info.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", search_datas.get(position).getId());
                i.putExtra("Search", bundle);
                startActivityForResult(i, INFO_SEARCH);
            }

            @Override
            public void onLongClick(int position, CommonAdapter.ViewHolder viewHolder) {
            }
        });
    }

    //RecyclerView界面布局切换
    private void Change_display(){
        int scrollPosition = 0;
        int scrollPosition1 = 0;
        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }
        if(mRecyclerView_search.getLayoutManager()!=null){
            scrollPosition1 = ((LinearLayoutManager) mRecyclerView_search.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }
        //布局切换
        if(display==0) {
            mLayoutManager = new LinearLayoutManager(this);
            mLayoutManager_search =new LinearLayoutManager(this);
            commonAdapter = new CommonAdapter(Main.this, R.layout.main_items, datas, display);
            commonAdapter_search = new CommonAdapter(Main.this, R.layout.main_items, search_datas, display);
        }else{
            mLayoutManager = new GridLayoutManager(this, display+1);
            mLayoutManager_search =new GridLayoutManager(this, display+1);
            commonAdapter = new CommonAdapter(Main.this, display==1?R.layout.main_item1:R.layout.main_item2, datas, display);
            commonAdapter_search = new CommonAdapter(Main.this, display==1?R.layout.main_item1:R.layout.main_item2, search_datas, display);
        }
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(commonAdapter);//填充数据
        mRecyclerView_search.setLayoutManager(mLayoutManager_search);
        mRecyclerView_search.setAdapter(commonAdapter_search);
        RecyclerView_Init_Listener();//建立监听
        Search_RecyclerView_Init_Listener();
        mRecyclerView.scrollToPosition(scrollPosition);
        mRecyclerView_search.scrollToPosition(scrollPosition1);
    }
    //返回事件
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent); //数据回传
        if (resultCode == RESULT_OK) {
            Bundle extras = intent.getExtras();
            if(extras!=null) {
                if (requestCode == ADD) { //添加界面
                    boolean is_add = extras.getBoolean("is_add");
                    if (is_add) {
                        Data tmp = databaseHelper.Select(extras.getInt("id"));
                        datas.add(tmp);
                        databaseHelper.Insert_back_mainid(tmp);
                        commonAdapter.notifyDataSetChanged();
                    }
                } else if (requestCode == INFO) {//信息界面
                    boolean is_change = extras.getBoolean("is_change");
                    if (is_change) {//更新数据
                        datas.remove(click_position);
                        commonAdapter.notifyDataSetChanged();
                        datas.add(click_position, databaseHelper.Select(extras.getInt("id")));
                        commonAdapter.notifyDataSetChanged();
                    }
                }else if(requestCode == INFO_SEARCH){
                    boolean is_change = extras.getBoolean("is_change");
                    if (is_change) {//更新数据
                        int id=extras.getInt("id");
                        if(!databaseHelper.DataisExist_main(id)) {//如果不存在就添加
                            Data tmp=databaseHelper.Select(id);
                            datas.add(tmp);
                            commonAdapter.notifyDataSetChanged();
                            databaseHelper.Insert_back_mainid(tmp);
                        }

                    }
                }
            }
        }
    }
    //读写数据
    private void Read_Write_Data(){
        String file_name[]={"Donghan","Dongzhuo","Qiyijun","Shu","Wei","Wu","Yuanshao","Yuanshu","Liubiao","Liuzhang","Qita","Shaoshuminzu","Xijin","Zaiye"}; //json文件名
        for(int i=0;i<file_name.length;i++){
            ArrayList<Data> read_data=Read_Data(file_name[i]);//返回的数据
            for(int j=0;j<read_data.size();j++){
                Data tmp=read_data.get(j);//先把图片设置为默认图片
                tmp.setBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.example));
                tmp.setCache(0);//本地无缓存
                tmp.setId(databaseHelper.Insert_back_id(tmp));//插入数据库
                //datas.add(tmp);
            }
        }
    }
    //读取json文件
    private ArrayList<Data> Read_Data(String json_name){
        ArrayList<Data>reads=new ArrayList<>();
        try {//将json文件读入buffer
            InputStreamReader isr = new InputStreamReader(getAssets().open(json_name+".json"),"UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while((line = br.readLine()) != null){
                builder.append(line);
            }
            br.close();
            isr.close();
            JSONObject testjson = new JSONObject(builder.toString());//builder读取了JSON中的数据。
            //直接传入JSONObject来构造一个实例
            JSONArray array = testjson.getJSONArray(json_name);         //从JSONObject中取出数组对象
            for (int i = 0; i < array.length(); i++) {
                JSONObject role = array.getJSONObject(i);    //取出数组中的对象
                Data tmp=new Data();
                String both,dead,place;
                //切割之前爬到的数据 此处需要分割一个标签。
                String desc1=role.getString("desc1").replace(" ","").replace("？","0").replace("史实人物","").replace("虚构人物","");
                String url=role.getString("img_url");
                String name=role.getString("name");
                String info=role.getString("desc2");
                String conutry=role.getString("country");
                //分割标签
                String sex = desc1.substring(0,1);
                int pos=desc1.indexOf("籍贯");
                if(pos!=-1)place=desc1.substring(pos+3,desc1.length());
                else place="";
                int both_pos=desc1.indexOf("生卒");
                if(both_pos!=-1){
                    int dead_pos=desc1.indexOf("-");
                    both=desc1.substring(both_pos+3,dead_pos).replace("公元前","-");
                    dead=desc1.substring(dead_pos+1,desc1.indexOf(")")).replace("公元前","-");
                }else{
                    both="0";
                    dead="0";
                }
                //数据设置回传
                tmp.setName(name);
                tmp.setPlace(place);
                tmp.setBoth_and_Dead(Integer.valueOf(both),Integer.valueOf(dead));
                if(sex.equals("男"))tmp.setSex(1);
                else tmp.setSex(0);
                if(conutry.equals("蜀"))tmp.setCountry(2);
                else if(conutry.equals("魏"))tmp.setCountry(1);
                else if(conutry.equals("吴"))tmp.setCountry(3);
                else tmp.setCountry(0);
                tmp.setUrl(url);
                tmp.setInfo(info);
                reads.add(tmp);
            }//

        } catch (Exception e) {
            e.printStackTrace();
        }
        return reads;
    }



    /*private void Data_IN(){//准备的一点数据
        String namse[]={"刘备","关羽","张飞","赵云","诸葛亮","马超"};
        String places[]={"幽州涿郡涿(河北保定市涿州)","司隶河东郡解(山西运城市临猗县西南)","幽州涿郡(河北保定市涿州)","冀州常山国真定(河北石家庄市正定县南)","徐州琅邪国阳都(山东临沂市沂南县南)","司隶扶风茂陵(陕西咸阳市兴平东)"};
        String Infos[]={"蜀汉的开国皇帝，相传是汉景帝之子中山靖王刘胜的后代。刘备少年丧父，与母亲贩鞋织草席为生。","前将军。本字长生，亡命奔涿郡。与张飞追随刘备征战，当刘备为平原相时，他们俩为别部司马。二人与刘备寝则同床，恩若兄弟。","车骑将军、司隶校尉。年少时与关羽投靠刘备，三人恩如兄弟。刘备被公孙瓒表为平原相后刘备以其为別部司马。","以英勇善战、一身是胆著称。初为本郡所举，将义从吏兵诣公孙瓒。时袁绍称冀州牧，瓒深忧州人之从绍也，善云来附，遂与瓒征讨。","政治家、军事家，被誉为“千古良相”的典范。父母早亡，由叔父玄抚养长大，后因徐州之乱，避乱荆州，潜心向学，淡泊明志。","马腾遣马超随钟繇讨郭援于平阳，马超为飞矢所中，乃以布带裹好受伤的小腿继续战斗，终破袁军。"};
        int sexs[]={1,1,1,1,1,1};
        int boths[]={161,0,0,0,181,176};
        int dead[]={223,219,221,229,234,222};
        int image_id[]={R.mipmap.liubei,R.mipmap.guanyu,R.mipmap.zhangfei,R.mipmap.zhaoyun,R.mipmap.zhuge,R.mipmap.machao};
        for(int i=0;i<6;i++){
            Bitmap bitmap=BitmapFactory.decodeResource(getResources(),image_id[i]);
            Data tmp=new Data(namse[i],places[i],Infos[i],sexs[i],2,boths[i],dead[i], bitmap);
            tmp.setId(databaseHelper.Insert_back_id(tmp));
            datas.add(tmp);
        }
    }*/





}


