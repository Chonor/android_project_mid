package cn.chonor.project_1;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main extends AppCompatActivity {
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    private static int ADD=0;
    private static int INFO=1;
    ArrayList<Data>datas=new ArrayList<>();
    ArrayList<Data>search_datas=new ArrayList<>();
    private RecyclerView mRecyclerView = null;
    private RecyclerView mRecyclerView_search = null;
    private CommonAdapter commonAdapter;
    private CommonAdapter commonAdapter_search;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected RecyclerView.LayoutManager mLayoutManager_search;
    private TabHost tabHost;
    private int click_position=-1;
    private Button reset;
    private TextView edit;
    private ImageView main_add;
    private boolean Is_edit;
    private ArrayList<String>choose_list;
    private int display=0;
    private int country=0;
    private int sex=2;
    private RadioGroup setting_display;
    private RadioGroup search_country;
    private RadioGroup search_sex;
    private RadioButton display1;
    private RadioButton display2;
    private RadioButton display3;
    private RadioButton country1;
    private RadioButton country2;
    private RadioButton country3;
    private RadioButton country_all;
    private RadioButton sex_all;
    private RadioButton sex1;
    private RadioButton sex2;
    private Switch music;
    private SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Init();
        if(databaseHelper.tabIsExist("role")){//这个数据库先停用
            Toast.makeText(Main.this,String.valueOf(databaseHelper.DB_Size("role")),Toast.LENGTH_SHORT).show();
            datas=databaseHelper.Select_ALL();
            //databaseHelper.Drop_Tab("role");
        }else{
            databaseHelper.Create_role();
            Data_IN();//加点数据
            Toast.makeText(Main.this,"no_exits",Toast.LENGTH_SHORT).show();
        }
        //databaseHelper.getWritableDatabase();
        ListView_display();
        Init_Listener();
    }
    private void Init(){
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
        music=(Switch)findViewById(R.id.settings_music);
        //数据还原
        reset=(Button)findViewById(R.id.reset);

        //主界面注册 切换
        tabHost=(TabHost)findViewById(R.id.tabhost);
        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec("one").setIndicator("词典主页").setContent(R.id.dictionary));
        tabHost.addTab(tabHost.newTabSpec("two").setIndicator("信息检索").setContent(R.id.Search));
        tabHost.addTab(tabHost.newTabSpec("thr").setIndicator("设置").setContent(R.id.Settings));

    }

    private void Init_Listener() {
        //数据还原监听
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //回填数据库
                databaseHelper.Drop_Tab("role");
                databaseHelper.Create_role();
                Data_IN();//加点数据
                Change_display();//重新加载
            }
        });
        //进入编辑界面
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Is_edit) {//处于编辑模式点击 退出编辑模式
                    Is_edit = false;
                    edit.setText("Edit");
                    main_add.setVisibility(View.INVISIBLE);
                    Collections.sort(choose_list); //判断删除
                    for (int i = choose_list.size() - 1; i >= 0; i--) {//从大到小删除避免位置改变
                        datas.remove((int) Integer.valueOf(choose_list.get(i)));
                        commonAdapter.notifyDataSetChanged();
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
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setQuery("　",false);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                searchView.onActionViewCollapsed();
                search_datas=databaseHelper.Search_Data(query.replace("　",""),country,sex);
                Toast.makeText(Main.this,"检索出" + String.valueOf(search_datas.size()),Toast.LENGTH_LONG);
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
                }
            }
        });
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
                            databaseHelper.Delete(datas.get(position));
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
    private void Search_RecyclerView_Init_Listener(){
        commonAdapter_search.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position, CommonAdapter.ViewHolder viewHolder) {
                Intent i = new Intent(Main.this, Info.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", search_datas.get(position).getId());
                i.putExtra("Search", bundle);
                startActivityForResult(i, INFO);
            }

            @Override
            public void onLongClick(int position, CommonAdapter.ViewHolder viewHolder) {

            }
        });
    }
    //初始化时设置显示
    private void ListView_display(){
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager_search = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView_search.setLayoutManager(mLayoutManager_search);
        commonAdapter = new CommonAdapter(Main.this, R.layout.main_items, datas,display);
        commonAdapter_search = new CommonAdapter(Main.this, R.layout.main_items,search_datas,display);
        mRecyclerView.setAdapter(commonAdapter);//填充数据
        mRecyclerView_search.setAdapter(commonAdapter_search);
        RecyclerView_Init_Listener();
        Search_RecyclerView_Init_Listener();
    }
    //界面布局切换
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
                }
            }
        }
    }





    private void Data_IN(){//准备的一点数据
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
    }



}
