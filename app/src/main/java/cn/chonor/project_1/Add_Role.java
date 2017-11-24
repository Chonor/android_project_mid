package cn.chonor.project_1;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Chonor on 2017/11/17.
 */

public class Add_Role extends AppCompatActivity  {
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    private static final int CAMERA = 1;
    private static final int IMAGE = 2;
    private Data data=new Data();
    private LayoutParams para = null;
    private int height=0,width=0;
    private ImageView imageView=null;
    private ImageView backs=null;
    private TextInputLayout add_name=null;
    private TextInputLayout add_place=null;
    private NumberPicker add_dead=null;
    private NumberPicker add_both=null;
    private EditText add_info=null;
    private EditText input_name=null;
    private EditText input_place=null;
    private RadioGroup add_sex=null;
    private RadioGroup add_country=null;
    private RadioButton add_man=null;
    private RadioButton add_woman=null;
    private RadioButton add_wei=null;
    private RadioButton add_wu=null;
    private RadioButton add_shu=null;
    private RadioButton add_other=null;
    private Button add_button=null;
    private int max_year;//最晚年份
    private int min_year;//最早年份，既然可以留0不能负数，那就是0
    private int choose_both;
    private int choose_dead;
    private int choose_sex;
    private int choose_country;
    private View decorView;
    private float downX, downY;
    private float screenWidth, screenHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add);
        Init();
        Init_Listener();
    }
    private void Init(){
        imageView=(ImageView)findViewById(R.id.add_image);
        backs=(ImageView)findViewById(R.id.add_back);
        int i = View.MeasureSpec.makeMeasureSpec(0, 0);
        int j = View.MeasureSpec.makeMeasureSpec(0, 0);
        imageView.measure(i,j);
        height=imageView.getMeasuredHeight();
        width=imageView.getMeasuredWidth();
        ////////////注册 Jy
        add_name=(TextInputLayout)findViewById(R.id.add_name);
        add_place=(TextInputLayout)findViewById(R.id.add_place);
        add_info=(EditText)findViewById(R.id.add_info);
        input_name=(EditText)findViewById(R.id.input_name);
        input_place=(EditText)findViewById(R.id.input_place);
        add_both=(NumberPicker)findViewById(R.id.add_both);
        add_dead=(NumberPicker)findViewById(R.id.add_dead);
        add_sex=(RadioGroup)findViewById(R.id.add_radioGroup);
        add_country=(RadioGroup)findViewById(R.id.add_country);
        add_man=(RadioButton)findViewById(R.id.add_sex1);
        add_woman=(RadioButton)findViewById(R.id.add_sex2);
        add_wei=(RadioButton)findViewById(R.id.add_wei);
        add_shu=(RadioButton)findViewById(R.id.add_shu);
        add_wu=(RadioButton)findViewById(R.id.add_wu);
        add_other=(RadioButton)findViewById(R.id.add_other);
        add_button=(Button)findViewById(R.id.add_button);
        Set_NumberPickers();

        decorView = getWindow().getDecorView();
        // 获得手机屏幕的宽度和高度，单位像素
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
    }
    private void Set_NumberPickers(){
        choose_country=0;
        choose_sex=0;
        max_year=400;
        min_year=0;
        choose_both=100;
        choose_dead=150;
        add_both.setMinValue(min_year);
        add_dead.setMinValue(min_year);
        add_both.setMaxValue(max_year);
        add_dead.setMaxValue(max_year);
        add_both.setValue(choose_both);
        add_dead.setValue(choose_dead);//当前值
    }

    private void Init_Listener(){
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] Items={"拍摄","从相册选择"};
                AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(Add_Role.this);
                alertDialogBuilder .setIcon(android.R.drawable.ic_dialog_info);
                alertDialogBuilder.setTitle("选择图片");
                alertDialogBuilder.setItems(Items,new DialogInterface.OnClickListener(){
                    @Override   //使用setItems构建选择列表 并增加点击检测
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(Add_Role.this, "您选择了["+Items[i]+"]", Toast.LENGTH_SHORT).show();
                        if(i==0){//额外 打开相机
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, CAMERA);
                        }else {//额外 打开相册
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent, IMAGE);
                        }
                    }
                });
                alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(Add_Role.this,"选择取消",Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialogBuilder.setCancelable(true);//误点击可取消
                alertDialogBuilder.create().show();
            }
        });
        backs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Returns_null();
            }
        });

        add_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (add_man.getId() == i)
                    choose_sex=1;
                else if (add_woman.getId() == i)
                    choose_sex=0;
            }
        });
        add_country.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(add_other.getId()==i)choose_country=0;
                else if(add_wei.getId()==i)choose_country=1;
                else if(add_shu.getId()==i)choose_country=2;
                else if(add_wu.getId()==i)choose_country=3;
            }
        });
        input_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() ==0){
                    add_name.setErrorEnabled(true);
                    add_name.setError("姓名不可为空");
                    Toast.makeText(Add_Role.this, "姓名不可为空,请重新填写", Toast.LENGTH_SHORT).show();
                } else{
                    add_name.setErrorEnabled(false);
                }
            }
        });
        input_place.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() == 0) {
                    add_place.setErrorEnabled(true);
                    add_place.setError("籍贯不可为空");
                    Toast.makeText(Add_Role.this, "籍贯不可为空,请重新填写", Toast.LENGTH_SHORT).show();
                }
                else {
                    add_place.setErrorEnabled(false);
                }
            }
        });


        add_both.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                choose_both=i1;
                if(i1>choose_dead){
                    choose_dead=choose_both;
                    add_dead.setValue(choose_dead);
                }
                //data.setBoth_and_Dead(choose_both,choose_dead);
            }
        });
        add_dead.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                choose_dead=i1;
                if(choose_both>choose_dead){
                    choose_both=choose_dead;
                    add_both.setValue(choose_both);
                }
                //data.setBoth_and_Dead(choose_both,choose_dead);
            }
        });
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(input_name.getText().length()!=0&&input_place.getText().length()!=0){
                    Returns_Add_new();
                }else{
                    add_place.setErrorEnabled(true);
                    add_place.setError("籍贯不可为空");
                    add_name.setErrorEnabled(true);
                    add_name.setError("姓名不可为空");
                    Toast.makeText(Add_Role.this,"请补全信息",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void Returns_Add_new(){//如果加了数据调用这个返回
        data.setName(input_name.getText().toString());
        data.setPlace(input_place.getText().toString());
        data.setSex(choose_sex);
        data.setCountry(choose_country);
        data.setBoth_and_Dead(choose_both,choose_dead);
        data.setInfo(add_info.getText().toString());
        data.setBitmap(((BitmapDrawable) imageView.getDrawable()).getBitmap());
        Intent i = new Intent(Add_Role.this, Main.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("is_add",true);
        bundle.putInt("id",databaseHelper.Insert_back_id(data));
        i.putExtras(bundle);
        setResult(RESULT_OK,i);
        finish();
    }
    private void Returns_null(){//不添加调用这个返回
        Intent i = new Intent(Add_Role.this, Main.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("is_add",false);
        i.putExtras(bundle);
        setResult(RESULT_OK,i);
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo); //设置图片
            para = imageView.getLayoutParams();    // 设置自动宽高
            para.height = height;
            para.width = width;
            imageView.setLayoutParams(para);

        }else if(requestCode == IMAGE && resultCode == RESULT_OK){
            try { //此处提示需要捕获异常 所以加了
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Bitmap photo = BitmapFactory.decodeStream(inputStream); //使用输入流转化图片
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
                Returns_null();
            }
        });
    }
    private void Back_To_Left(float moveDistanceX){//Activity被滑动到中途时，滑回去
        ObjectAnimator.ofFloat(decorView, "X", moveDistanceX, 0).setDuration(300).start();
    }
}
