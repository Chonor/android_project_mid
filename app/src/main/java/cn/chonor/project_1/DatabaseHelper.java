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
    private static final String name = "INFO.db";
    private static final int version = 1; //数据库版本

    public DatabaseHelper(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS role "
                + "(roleid integer primary key autoincrement, "
                + "name varchar(40), "
                + "place varchar(1000), "
                + "info varchar(3000), "
                + "sex INTEGER, "
                + "both INTEGER, "
                + "dead INTEGER, "
                + "image BLOB)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void Insert(Data data) {
        ContentValues cv = new ContentValues();
        cv.put("name", data.getName());
        cv.put("place", data.getPlace());
        cv.put("info", data.getInfo());
        cv.put("sex", data.getSex());
        cv.put("country",data.getCountry());
        cv.put("both", data.getBoth());
        cv.put("dead", data.getDead());
        cv.put("image", data.getbyte());
        getWritableDatabase().insert("role", null, cv);
    }
    public int Insert_back_id(Data data){
        int Id=0;
        Insert(data);
        Cursor cursor = getReadableDatabase().query("role", new String[]{"roleid"}, null, null, null, null, null);
        if(cursor.moveToLast()) {
            Id=cursor.getInt(cursor.getColumnIndex("roleid"));
        }
        return Id;
    }

    public void Updatas(Data data) {
        StringBuffer whereBuffer = new StringBuffer();
        whereBuffer.append("roleid").append(" = ").append("'").append(data.getId()).append("'");
        ContentValues cv = new ContentValues();
        cv.put("name", data.getName());
        cv.put("place", data.getPlace());
        cv.put("info", data.getInfo());
        cv.put("sex", data.getSex());
        cv.put("country",data.getCountry());
        cv.put("both", data.getBoth());
        cv.put("image", data.getbyte());
        cv.put("dead", data.getDead());
        getWritableDatabase().update("role", cv, whereBuffer.toString(), null);
    }

    public void Delete(Data data) {
        StringBuffer whereBuffer = new StringBuffer();
        whereBuffer.append("roleid").append(" = ").append("'").append(data.getId()).append("'");
        getWritableDatabase().delete("role", whereBuffer.toString(), null);
    }

    public Data Select(int id) {
        Data data = new Data();
        data.setId(id);
        StringBuffer whereBuffer = new StringBuffer();
        whereBuffer.append("roleid").append(" = ").append("'").append(id).append("'");
        String[] columns = {"name", "place", "info", "sex", "country","both", "dead", "image"};
        Cursor cursor = getReadableDatabase().query("role", columns, whereBuffer.toString(), null, null, null, null);
        if (cursor.moveToFirst()) {
            data.setName(cursor.getString(cursor.getColumnIndex("name")));
            data.setPlace(cursor.getString(cursor.getColumnIndex("place")));
            data.setInfo(cursor.getString(cursor.getColumnIndex("info")));
            data.setSex(cursor.getInt(cursor.getColumnIndex("sex")));
            data.setCountry(cursor.getInt(cursor.getColumnIndex("country")));
            data.setBoth_and_Dead(cursor.getInt(cursor.getColumnIndex("both")), cursor.getInt(cursor.getColumnIndex("dead")));
            data.setbyte(cursor.getBlob(cursor.getColumnIndex("image")));
        }
        if (cursor != null) {
            cursor.close();
        }
        return data;
    }
    public ArrayList<Data>Search_Data(String name,int country,int sex){
        ArrayList<Data> datas = new ArrayList<>();
        ArrayList<Integer>index=new ArrayList<>();
        StringBuffer whereBuffer = new StringBuffer();
        if(!name.equals("")){
            whereBuffer.append("name").append(" like ").append("'%").append(name).append("%'");
        }
        if(country!=0){
            if(whereBuffer.length()!=0) whereBuffer.append(" AND ");
            whereBuffer.append("country").append(" = ").append("'").append(country).append("'");
        }
        if(sex!=2){
            if(whereBuffer.length()!=0)whereBuffer.append(" AND ");
            whereBuffer.append("sex").append(" = ").append("'").append(sex).append("'");
        }
        Cursor cursor = getReadableDatabase().query("role", new String[]{"roleid"}, whereBuffer.toString(), null, null, null, null);
        while (cursor.moveToNext()) {
            index.add(cursor.getInt(cursor.getColumnIndex("roleid")));
        }
        if (cursor != null)cursor.close();
        for(int i=0;i<index.size();i++){
            datas.add(Select(index.get(i)));
        }
        return datas;
    }
    public ArrayList<Data> Select_ALL() {
        ArrayList<Data> datas = new ArrayList<>();
        ArrayList<Integer>index=new ArrayList<>();
        Cursor cursor = getReadableDatabase().query("role", new String[]{"roleid"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            index.add(cursor.getInt(cursor.getColumnIndex("roleid")));
        }
        cursor.close();
        for(int i=0;i<index.size();i++){
            datas.add(Select(index.get(i)));
        }
        return datas;
    }

    public int DB_Size(String tabName){
        if(tabName == null)
            return 0;
        Cursor cursor = getReadableDatabase().query(tabName,null, null, null, null, null, null);
        int size=cursor.getCount();
        cursor.close();
        return size;
    }
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

    public void Create_role(){
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS role "
                + "(roleid integer primary key autoincrement, "
                + "name varchar(40), "
                + "place varchar(1000), "
                + "info varchar(3000), "
                + "sex INTEGER, "
                + "country INTEGER,"
                + "both INTEGER, "
                + "dead INTEGER, "
                + "image BLOB)");
    }
    public void Create_settings(){
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS settings "
                + "(sid integer primary key autoincrement,"
                + "music INTEGER, "
                + "display INTEGER");
    }
    public void Create_backup(){
        getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS backup "
                + "(backupid integer primary key autoincrement, "
                + "name varchar(40), "
                + "place varchar(1000), "
                + "info varchar(3000), "
                + "sex INTEGER, "
                + "country INTEGER,"
                + "both INTEGER, "
                + "dead INTEGER, "
                + "image BLOB)");
    }
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
    public void Drop_Tab(String tabName){
        getWritableDatabase().execSQL("drop table "+tabName);
    }
    public void Delete_Tab(String tabName){
        getWritableDatabase().execSQL("delete from "+tabName);
    }
}
