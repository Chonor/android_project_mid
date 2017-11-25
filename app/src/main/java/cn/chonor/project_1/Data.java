package cn.chonor.project_1;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayOutputStream;
import java.util.Date;

/**
 * Created by Chonor on 2017/11/14.
 */

/**
 * name 存储名字
 * place 存储籍贯
 * info 存储历史信息
 * url 存储头像网址
 * sex 存储性别 1:男 0：女
 * both dead 存储生卒
 * id 数据库中位置
 * country 势力 0为其他 1：魏 2：蜀 3：吴
 * bitmap 存放图片
 */

public class Data  implements Parcelable {

    private String name,place,info,url;
    private int sex,both,dead,id,country,cache;
    private Bitmap bitmap;
    public Data(){

    }
    //构造
    public Data(String name,String place,String info,int sex,int country,int both,int dead,Bitmap bitmap){
        this.name=name;
        this.place=place;
        this.info=info;
        this.sex=sex;
        this.country=country;
        this.both=both;
        this.dead=dead;
        this.bitmap=bitmap;
        this.cache=0;
    }
    //构造
    public Data(Data data){
        this.id=data.id;
        this.name= data.name;
        this.place= data.place;
        this.info= data.info;
        this.sex= data.sex;
        this.country=data.country;
        this.both= data.both;
        this.dead= data.dead;
        this.bitmap=data.bitmap;
        this.url=data.url;
        this.cache=data.cache;
    }
    public void setUrl(String url){this.url=url;}
    public void setId(int id){
        this.id=id;
    }
    public void setName(String name){
        this.name=name;
    }
    public void setPlace(String place){
        this.place=place;
    }
    public void setInfo(String info){
        this.info=info;
    }
    public void setSex(int sex){
        this.sex=sex;
    }
    public void setCountry(int country){this.country=country;}
    public void setBoth_and_Dead(int both,int dead){
        this.both=both;
        this.dead=dead;
    }
    public void setBitmap(Bitmap bitmap){//顺带裁剪
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) 200) / width;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleWidth);
        // 得到新的图片
        this.bitmap=Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }
    public void setCache(int cache){this.cache=cache>0?1:0;}
    public void setbyte(byte[] in){
        setBitmap(BitmapFactory.decodeByteArray(in, 0, in.length));
    }
    public String getUrl(){return url;}
    public String getName(){
        return name;
    }
    public String getPlace(){
        return place;
    }
    public String getInfo(){
        return info;
    }
    public String getBoth_and_Dead(){
        return (both==0?"?":String.valueOf(both))+" - "+(dead==0?"?":String.valueOf(dead));
    }
    public int getSex(){
        return sex;
    }
    public int getId(){
        return id;
    }
    public int getBoth(){
        return both;
    }
    public int getDead(){
        return dead;
    }
    public int getCountry(){return country;}
    public Bitmap getCountry_image(Context context){
        if(country==1)return BitmapFactory.decodeResource(context.getResources(),R.mipmap.wei);
        else if(country ==2)return BitmapFactory.decodeResource(context.getResources(),R.mipmap.shu);
        else if(country==3)return BitmapFactory.decodeResource(context.getResources(),R.mipmap.wu);
        return BitmapFactory.decodeResource(context.getResources(),R.mipmap.other);
    }
    public Bitmap getBitmap(){
        return bitmap;
    }
    public byte[] getbyte(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
    public int getCache(){return  cache;}






    ////无视这个部分，这个是使用 Parcelable进行数据传输，使用数据库之后不需要
    @Override
    public int describeContents() {
        return 8;
    }
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(name);
        parcel.writeString(place);
        parcel.writeString(info);
        parcel.writeInt(sex);
        parcel.writeInt(country);
        parcel.writeInt(both);
        parcel.writeInt(dead);
        parcel.writeInt(id);
       // parcel.writeByteArray(getbyte());
    }
    public static final Parcelable.Creator<Data> CREATOR =new Parcelable.Creator<Data>() {
        @Override
        public Data createFromParcel(Parcel source){
            Data data= new Data();
            data.name=source.readString();
            data.place=source.readString();
            data.info=source.readString();
            data.sex=source.readInt();
            data.country=source.readInt();
            data.both=source.readInt();
            data.dead=source.readInt();
            data.id=source.readInt();
            /*byte in[]=new byte[]{};
            source.readByteArray(in);
            data.setbyte(in);*/
            return data;
        }
        @Override
        public Data[] newArray(int size){
            return new Data[size];
        }
    };
    ///////////////////////////////////////////////////////////////////////////////////////
}
