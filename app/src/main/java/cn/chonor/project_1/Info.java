package cn.chonor.project_1;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Chonor on 2017/11/17.
 */

public class Info extends AppCompatActivity {
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    private static final int CAMERA = 1;
    private static final int IMAGE = 2;
    private Data data=new Data();
    private LayoutParams para = null;
    private int height=0,width=0;
    private ImageView imageView=null;
    private ImageView backs=null;
    private ImageView country=null;
    private TextView info_name=null;
    private TextView info_sex=null;/*Jy*/
    private TextView info_date=null;
    private TextView info_place=null;
    private TextView info_other=null;
    private TextView info_add=null;
    private View decorView;
    private float downX, downY;
    private float screenWidth, screenHeight;
    private boolean Is_change=false;
    private boolean Change=true,Add=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);
        Is_change=false;
        Receive_Data();
        Init();
        Set_Data();
        //不允许在搜索界面修改
        if(Change)Init_Listener();
        else Init_Listener_Search();
    }
    //初始化
    private void Init(){
        imageView=(ImageView)findViewById(R.id.info_image);
        backs=(ImageView)findViewById(R.id.info_back);
        country=(ImageView)findViewById(R.id.Info_country);
        int i = View.MeasureSpec.makeMeasureSpec(0, 0);
        int j = View.MeasureSpec.makeMeasureSpec(0, 0);
        imageView.measure(i,j);
        height=imageView.getMeasuredHeight();
        width=imageView.getMeasuredWidth();
        //////////
        //注册/*Jy*/
        info_name=(TextView)findViewById(R.id.info_name);
        info_sex=(TextView)findViewById(R.id.info_sex);
        info_date=(TextView)findViewById(R.id.info_date);
        info_place=(TextView)findViewById(R.id.info_place);
        info_other=(TextView)findViewById(R.id.info_other);
        info_add=(TextView)findViewById(R.id.info_add_main);
        ////////////

        decorView = getWindow().getDecorView();
        // 获得手机屏幕的宽度和高度，单位像素
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
    }
    //数据设置
    private void Set_Data(){
        if(data.getCache()==0)Glide.with(this).load(data.getUrl()).into(imageView);
        else imageView.setImageBitmap(data.getBitmap());
        country.setImageBitmap(data.getCountry_image(Info.this));
        info_name.setText(data.getName());
        info_sex.setText(data.getSex()==1?"男":"女");
        info_date.setText(data.getBoth_and_Dead());
        info_place.setText(data.getPlace());
        info_other.setText(data.getInfo());
        info_add.setVisibility(View.INVISIBLE);
    }
    //搜索界面的信息监听
    private void Init_Listener_Search(){
        info_add.setVisibility(View.VISIBLE);
        backs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Add)Return_change();
                else Return_no_change();
            }
        });
        info_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Add=true;
                Toast.makeText(Info.this,"成功添加",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void Init_Listener(){//点击事件监听
         /*Jy*/
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final String[] Items={"拍摄","从相册选择"};
                AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(Info.this);
                alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_info);
                alertDialogBuilder.setTitle("选择图片");
                alertDialogBuilder.setItems(Items,new DialogInterface.OnClickListener(){
                    @Override   //使用setItems构建选择列表 并增加点击检测
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(Info.this, "您选择了["+Items[i]+"]", Toast.LENGTH_SHORT).show();
                        if(i==0){//额外 打开相机
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, CAMERA);
                        }else {//额外 打开相册
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent, IMAGE);
                        }
                        data.setCache(1);
                        Is_change=true;
                    }
                });
                alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(Info.this,"修改取消",Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialogBuilder.setCancelable(true);//误点击可取消
                alertDialogBuilder.create().show();
                return true;
            }
        });
        //返回按钮根据是否修改返回
        backs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Is_change)Return_change();
                else Return_no_change();
            }
        });
        //国别选择
        country.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final String[] Items={"魏","蜀","吴","其他"};
                AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(Info.this);
                alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_info);
                alertDialogBuilder.setTitle("更改势力");
                alertDialogBuilder.setItems(Items,new DialogInterface.OnClickListener(){
                    @Override   //使用setItems构建选择列表 并增加点击检测
                    public void onClick(DialogInterface dialogInterface, int i) {
                        data.setCountry((i+1)%4);
                        country.setImageBitmap(data.getCountry_image(Info.this));
                        Is_change=true;
                    }
                });
                alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(Info.this,"修改取消",Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialogBuilder.setCancelable(true);//误点击可取消
                alertDialogBuilder.create().show();
                return true;
            }
        });
        //修改名字
        info_name.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final EditText input_text=new EditText(Info.this);
                AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(Info.this);
                alertDialogBuilder.setTitle("请输入姓名：")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(input_text)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String Input_text=input_text.getText().toString();
                                if(Input_text.equals(""))//不可为空
                                    Toast.makeText(getApplicationContext(),"姓名不可为空,修改失败",Toast.LENGTH_LONG).show();
                                else {
                                    data.setName(Input_text);
                                    info_name.setText(Input_text);
                                    Is_change=true;
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getApplicationContext(),"修改取消",Toast.LENGTH_LONG).show();
                            }
                        })
                        .setCancelable(true)
                        .create().show();
                return true;
            }
        });
        //修改性别
        info_sex.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final String[] Items={"男","女"};
                AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(Info.this);
                alertDialogBuilder.setTitle("请选择性别：")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setItems(Items,new DialogInterface.OnClickListener(){
                            @Override   //使用setItems构建选择列表 并增加点击检测
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i==0)
                                    data.setSex(1);
                                else
                                    data.setSex(0);
                                Toast.makeText(Info.this, "性别修改为："+Items[i], Toast.LENGTH_SHORT).show();
                                info_sex.setText(Items[i]);
                                Is_change=true;
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getApplicationContext(),"修改取消",Toast.LENGTH_LONG).show();
                            }
                        })
                        .setCancelable(true)
                        .create().show();
                return true;
            }
        });
        //修改生卒
        info_date.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //自定义界面
                final View view_birth_dead= LayoutInflater.from(Info.this).inflate(R.layout.alertdialog_birth_dead,null);
                final NumberPicker birth = (NumberPicker)view_birth_dead.findViewById(R.id.alertdialog_birth);
                final NumberPicker dead = (NumberPicker)view_birth_dead.findViewById(R.id.alertdialog_dead);
                birth.setMinValue(0);
                dead.setMinValue(0);
                birth.setMaxValue(1600);
                dead.setMaxValue(1600);
                birth.setValue(data.getBoth());
                dead.setValue(data.getDead());//当前值
                //控制生<卒
                birth.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                        if(i1>dead.getValue()){
                            dead.setValue(i1);
                        }
                    }
                });
                dead.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                        if(birth.getValue()>i1){
                            birth.setValue(i1);
                        }
                    }
                });
                AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(Info.this);
                alertDialogBuilder.setTitle("请输生卒：")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(view_birth_dead)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                data.setBoth_and_Dead(birth.getValue(),dead.getValue());
                                info_date.setText(data.getBoth_and_Dead());
                                Is_change=true;
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getApplicationContext(),"修改取消",Toast.LENGTH_LONG).show();
                            }
                        })
                        .setCancelable(true)
                        .create().show();
                return true;
            }
        });
        //修改籍贯
        info_place.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final EditText input_text=new EditText(Info.this);
                AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(Info.this);
                alertDialogBuilder.setTitle("请输籍贯：")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(input_text)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String Input_text=input_text.getText().toString();
                                if(Input_text.equals(""))
                                    Toast.makeText(getApplicationContext(),"输入为空,修改失败",Toast.LENGTH_LONG).show();
                                else{
                                    Is_change=true;
                                    data.setPlace(Input_text);
                                    info_place.setText(Input_text);
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getApplicationContext(),"修改取消",Toast.LENGTH_LONG).show();
                            }
                        })
                        .setCancelable(true)
                        .create().show();
                return true;
            }
        });
        //修改历史信息
        info_other.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final EditText input_text=new EditText(Info.this);
                AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(Info.this);
                alertDialogBuilder.setTitle("请输入人物历史介绍：")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(input_text)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String Input_text=input_text.getText().toString();
                                data.setInfo(Input_text);
                                info_other.setText(Input_text);
                                Is_change=true;
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getApplicationContext(),"修改取消",Toast.LENGTH_LONG).show();
                            }
                        })
                        .setCancelable(true)
                        .create().show();
                return true;
            }
        });
    }

    //数据接收
    private void Receive_Data(){
        Bundle extras = getIntent().getBundleExtra("mainActivity");
        if(extras!=null){
            data= databaseHelper.Select(extras.getInt("id"));
            Change=true;
        }
        else{//在所搜索界面不允许信息修改
            extras = getIntent().getBundleExtra("Search");
            data= databaseHelper.Select(extras.getInt("id"));
            Change=false;
        }
    }
    private void Return_change(){//修改了信息调用这个
        if(!Add)databaseHelper.Updatas(data);
        Intent i = new Intent(Info.this, Main.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("is_change",true);
        bundle.putInt("id",data.getId());
        i.putExtras(bundle);
        setResult(RESULT_OK,i);
        finish();
    }
    private void Return_no_change(){//没有修改调用这个
        Intent i = new Intent(Info.this, Main.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("is_change",false);
        i.putExtras(bundle);
        setResult(RESULT_OK,i);
        finish();
    }
    //调用相机or相册
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            this.data.setBitmap(photo);
            imageView.setImageBitmap(photo); //设置图片
            para = imageView.getLayoutParams();    // 设置自动宽高
            para.height = height;
            para.width = width;
            imageView.setLayoutParams(para);
        }else if(requestCode == IMAGE && resultCode == RESULT_OK){
            try { //此处提示需要捕获异常 所以加了
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Bitmap photo = BitmapFactory.decodeStream(inputStream); //使用输入流转化图片
                this.data.setBitmap(photo);
                imageView.setImageBitmap(photo);
                para =  imageView.getLayoutParams();// 设置自动宽高
                para.height = height;
                para.width = width;
                imageView.setLayoutParams(para);
            }catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    //滑动返回
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){// 当按下时
            // 获得按下时的X坐标
            downX = event.getX();
        }else if(event.getAction() == MotionEvent.ACTION_MOVE){// 当手指滑动时
            // 获得滑过的距离
            float moveDistanceX = event.getX() - downX;
            if(moveDistanceX > 0){// 如果是向右滑动
                decorView.setX(moveDistanceX); // 设置界面的X到滑动到的位置
            }
        }else if(event.getAction() == MotionEvent.ACTION_UP){// 当抬起手指时
            // 获得滑过的距离
            float moveDistanceX = event.getX() - downX;
            if(moveDistanceX > screenWidth / 2){
                Continue_Move(moveDistanceX);
            }else{ // 如果滑动距离没有超过一半
                // 恢复初始状态
                Back_To_Left(moveDistanceX);
            }
        }
        return super.onTouchEvent(event);
    }

    private void Continue_Move(float moveDistanceX){
        // 从当前位置移动到右侧。
        ValueAnimator anim = ValueAnimator.ofFloat(moveDistanceX, screenWidth);
        anim.setDuration(500); // 一秒的时间结束, 为了简单这里固定为0.5秒
        anim.start();
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 位移
                float x = (float) (animation.getAnimatedValue());
                decorView.setX(x);
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(Is_change){
                    Return_change();
                }else{
                    Return_no_change();
                }
            }
        });
    }
    private void Back_To_Left(float moveDistanceX){//Activity被滑动到中途时，滑回去
        ObjectAnimator.ofFloat(decorView, "X", moveDistanceX, 0).setDuration(300).start();
    }
}
