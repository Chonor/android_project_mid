package cn.chonor.project_1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.BitmapFactory;

import java.util.ArrayList;

/**
 * Created by Chonor on 2017/11/14.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String name = "Project_Data.db";
    private static final int version = 1; //数据库版本

    public DatabaseHelper(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    //插入数据
    private void Insert(Data data) {
        ContentValues cv = new ContentValues();
        cv.put("name", data.getName());
        cv.put("place", data.getPlace());
        cv.put("info", data.getInfo());
        cv.put("url",data.getUrl());
        cv.put("sex", data.getSex());
        cv.put("country",data.getCountry());
        cv.put("both", data.getBoth());
        cv.put("dead", data.getDead());
        cv.put("cache",data.getCache());
        cv.put("image", data.getbyte());
        getWritableDatabase().insert("role", null, cv);
    }
    //插入数据并反复数据库中其ID
    public int Insert_back_id(Data data){
        int Id=0;
        Insert(data);
        Cursor cursor = getReadableDatabase().query("role", new String[]{"roleid"}, null, null, null, null, null);
        if(cursor.moveToLast()) {
            Id=cursor.getInt(cursor.getColumnIndex("roleid"));
        }
        return Id;
    }
    //更新所有数据
    public void Updatas(Data data) {
        StringBuffer whereBuffer = new StringBuffer();
        whereBuffer.append("roleid").append(" = ").append("'").append(data.getId()).append("'");
        ContentValues cv = new ContentValues();
        cv.put("name", data.getName());
        cv.put("place", data.getPlace());
        cv.put("info", data.getInfo());
        cv.put("url",data.getUrl());
        cv.put("sex", data.getSex());
        cv.put("country",data.getCountry());
        cv.put("both", data.getBoth());
        cv.put("dead", data.getDead());
        cv.put("cache",data.getCache());
        cv.put("image", data.getbyte());
        getWritableDatabase().update("role", cv, whereBuffer.toString(), null);
    }
    //更新图片缓存
    public void Upcacheimage(Data data){
        StringBuffer whereBuffer = new StringBuffer();
        whereBuffer.append("roleid").append(" = ").append("'").append(data.getId()).append("'");
        ContentValues cv = new ContentValues();
        cv.put("cache",data.getCache());
        cv.put("image", data.getbyte());
        getWritableDatabase().update("role", cv, whereBuffer.toString(),null);
    }
    //删除一个数据
    public void Delete(Data data) {
        StringBuffer whereBuffer = new StringBuffer();
        whereBuffer.append("roleid").append(" = ").append("'").append(data.getId()).append("'");
        getWritableDatabase().delete("role", whereBuffer.toString(), null);
    }
    //数据库中读出一个数据
    public Data Select(int id) {
        Data data = new Data();
        data.setId(id);
        StringBuffer whereBuffer = new StringBuffer();
        whereBuffer.append("roleid").append(" = ").append("'").append(id).append("'");
        String[] columns = {"name", "place", "info","url","sex", "country","both", "dead", "cache","image"};
        Cursor cursor = getReadableDatabase().query("role", columns, whereBuffer.toString(), null, null, null, null);
        if (cursor.moveToFirst()) {
            data.setName(cursor.getString(cursor.getColumnIndex("name")));
            data.setPlace(cursor.getString(cursor.getColumnIndex("place")));
            data.setInfo(cursor.getString(cursor.getColumnIndex("info")));
            data.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            data.setSex(cursor.getInt(cursor.getColumnIndex("sex")));
            data.setCountry(cursor.getInt(cursor.getColumnIndex("country")));
            data.setBoth_and_Dead(cursor.getInt(cursor.getColumnIndex("both")), cursor.getInt(cursor.getColumnIndex("dead")));
            data.setCache(cursor.getInt(cursor.getColumnIndex("cache")));
            data.setbyte(cursor.getBlob(cursor.getColumnIndex("image")));
        }
        if (cursor != null) {
            cursor.close();
        }
        return data;
    }
    //模糊搜索
    public ArrayList<Data>Search_Data(String name,int country,int sex){
        ArrayList<Data> datas = new ArrayList<>();
        ArrayList<Integer>index=new ArrayList<>();
        StringBuffer whereBuffer = new StringBuffer();
        //拼合条件
        if(!name.equals("")){
            whereBuffer.append("name").append(" like ").append("'%").append(name).append("%'");
        }

        if(whereBuffer.length()!=0) whereBuffer.append(" AND ");
        whereBuffer.append("country").append(" = ").append("'").append(country).append("'");

        if(sex!=2){
            if(whereBuffer.length()!=0)whereBuffer.append(" AND ");
            whereBuffer.append("sex").append(" = ").append("'").append(sex).append("'");
        }
        Cursor cursor = getReadableDatabase().query("role", new String[]{"roleid"}, whereBuffer.toString(), null, null, null, null);
        while (cursor.moveToNext()) {//先搜索ID再通过ID 索引全部数据，因为有2M的上限
            index.add(cursor.getInt(cursor.getColumnIndex("roleid")));
        }
        if (cursor != null)cursor.close();
        for(int i=0;i<index.size();i++){
            datas.add(Select(index.get(i)));
        }
        return datas;
    }
    //精准搜索
    public Data Search_Name(String name){
        int pos=-1;
        StringBuffer whereBuffer = new StringBuffer();
        //拼合条件
        if(!name.equals("")){
            whereBuffer.append("name").append(" = ").append("'").append(name).append("'");
        }
        Cursor cursor = getReadableDatabase().query("role", new String[]{"roleid"}, whereBuffer.toString(), null, null, null, null);
        if(cursor.moveToFirst()) {//先搜索ID再通过ID 索引全部数据，因为有2M的上限
            pos=cursor.getInt(cursor.getColumnIndex("roleid"));
        }
        if (cursor != null)cursor.close();
        if(pos!=-1)return Select(pos);
        else return null;
    }
    //选择全部数据
    public ArrayList<Data> Select_ALL() {
        ArrayList<Data> datas = new ArrayList<>();
        ArrayList<Integer>index=new ArrayList<>();
        Cursor cursor = getReadableDatabase().query("role", new String[]{"roleid"}, null, null, null, null, null);
        while (cursor.moveToNext()) {//先搜索ID再通过ID 索引全部数据，因为有2M的上限
            index.add(cursor.getInt(cursor.getColumnIndex("roleid")));
        }
        cursor.close();
        for(int i=0;i<index.size();i++){
            datas.add(Select(index.get(i)));
        }
        return datas;
    }
    //返回数据库大小
    public int DB_Size(String tabName){
        if(tabName == null)
            return 0;
        Cursor cursor = getReadableDatabase().query(tabName,null, null, null, null, null, null);
        if(cursor!=null) {
            int size = cursor.getCount();
            cursor.close();
            return size;
        }
       else return -1;
    }
    //检测表是否存在
    public boolean tabIsExist(String tabName){
        boolean result = false;
        if(tabName == null){
            return false;
        }
        Cursor cursor = null;
        try {
            String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"+tabName.trim()+"' ";
            cursor = getReadableDatabase().rawQuery(sql, null);
            if(cursor.moveToNext()){
                int count = cursor.getInt(0);
                if(count>0){
                    result = true;
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return result;
    }
    //创建角色表
    public void Create_role(){
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS role "
                + "(roleid integer primary key autoincrement, "
                + "name varchar(40), "
                + "place varchar(1000), "
                + "info varchar(1000), "
                + "url varchar(1000), "
                + "sex INTEGER, "
                + "country INTEGER,"
                + "both INTEGER, "
                + "dead INTEGER, "
                + "cache INTEGER, "
                + "image BLOB)");
    }
    //主界面数据
    public void Create_main(){
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS main "
                + "(id integer primary key autoincrement, "
                + "roleid INTEGER)");
    }
    private void Insert_main(Data data) {
        ContentValues cv = new ContentValues();
        cv.put("roleid", data.getId());
        getWritableDatabase().insert("main", null, cv);
    }
    //像main插入数据并反回数据库中其ID
    public int Insert_back_mainid(Data data){
        int Id=0;
        Insert_main(data);
        Cursor cursor = getReadableDatabase().query("main", new String[]{"id"}, null, null, null, null, null);
        if(cursor.moveToLast()) {
            Id=cursor.getInt(cursor.getColumnIndex("id"));
        }
        return Id;
    }
    //找出main中所有数据
    public ArrayList<Data> Select_main_data(){
        ArrayList<Data> datas = new ArrayList<>();
        ArrayList<Integer>index=new ArrayList<>();
        Cursor cursor = getReadableDatabase().query("main", new String[]{"roleid"}, null, null, null, null, null);
        while (cursor.moveToNext()) {//先搜索ID再通过ID 索引全部数据，因为有2M的上限
            index.add(cursor.getInt(cursor.getColumnIndex("roleid")));
        }
        cursor.close();
        for(int i=0;i<index.size();i++){
            datas.add(Select(index.get(i)));
        }
        return datas;
    }
    //删除main中数据
    public void Delete_main(int id) {
        StringBuffer whereBuffer = new StringBuffer();
        whereBuffer.append("roleid").append(" = ").append("'").append(String.valueOf(id)).append("'");
        getWritableDatabase().delete("main", whereBuffer.toString(), null);
    }
    //判断数据是否存在
    public boolean DataisExist_main(int id){
        StringBuffer whereBuffer = new StringBuffer();
        whereBuffer.append("roleid").append(" = ").append("'").append(String.valueOf(id)).append("'");
        Cursor cursor = getReadableDatabase().query("main", new String[]{"roleid"}, whereBuffer.toString(), null, null, null, null);
        if(cursor.getCount()>0)return true;
        else return false;
    }
    //创建设置表
    public void Create_settings(){
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS settings "
                + "(sid integer primary key autoincrement,"
                + "music INTEGER, "
                + "display INTEGER)");
    }
    //插入设置表
    public void Insert_settings(int music,int display){
        ContentValues cv = new ContentValues();
        cv.put("music", music);
        cv.put("display", display);
        getWritableDatabase().insert("settings", null, cv);
    }
    public void Updata_settings(int music,int display){
        StringBuffer whereBuffer = new StringBuffer();
        whereBuffer.append("sid").append(" = ").append("'").append("1").append("'");
        ContentValues cv = new ContentValues();
        cv.put("music", music);
        cv.put("display", display);
        getWritableDatabase().update("settings", cv, whereBuffer.toString(), null);
    }
    public int Select_settings(){
        StringBuffer whereBuffer = new StringBuffer();
        whereBuffer.append("sid").append(" = ").append("'").append("1").append("'");
        Cursor cursor = getReadableDatabase().query("settings", new String[]{"music","display"}, whereBuffer.toString(), null, null, null, null);
        if(cursor.moveToNext()){
            int music=cursor.getInt(cursor.getColumnIndex("music"));
            int display=cursor.getInt(cursor.getColumnIndex("display"));
            return music*10+display;
        }
        else return -1;
    }
    /////////////////////暂时不用
    //创建恢复表
    public void Create_backup(){
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS backup "
                + "(backupid integer primary key autoincrement, "
                + "name varchar(40), "
                + "place varchar(1000), "
                + "info varchar(1000), "
                + "url varchar(1000), "
                + "sex INTEGER, "
                + "country INTEGER,"
                + "both INTEGER, "
                + "dead INTEGER, "
                + "cache INTEGER, "
                + "image BLOB)");
    }
    //数据库备份复制
    public void Copy_Tab(){
        if(tabIsExist("backup"))
            Delete_Tab("backup");
        try {
            getWritableDatabase().execSQL("INSERT INTO TABLE backup "
                    + "SELECT * "
                    + "FROM role");
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    //数据库回滚
    public void Backup_Tab(){
        if(tabIsExist("role"))Delete_Tab("role");
        try {
            getWritableDatabase().execSQL("INSERT INTO TABLE role "
                    + "SELECT * "
                    + "FROM backup");
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    ///////////////////////
    //删除表
    public void Drop_Tab(String tabName){
        getWritableDatabase().execSQL("drop table "+tabName);
    }
    //清空表
    public void Delete_Tab(String tabName){
        getWritableDatabase().execSQL("delete from "+tabName);
    }
}
