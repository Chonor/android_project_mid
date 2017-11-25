package cn.chonor.project_1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Chonor on 2017/10/21.
 */
public class CommonAdapter extends RecyclerView.Adapter<CommonAdapter.ViewHolder> {
    private Context mContext;
    private List<Data> mDatas;
    private int mLayoutId;
    private int choose=0;

    //构造函数
    public CommonAdapter(Context mContext,int mLayoutId,List<Data>mDatas,int choose) {
        this.mLayoutId=mLayoutId;
        this.mContext = mContext;
        this.mDatas = mDatas;
        this.choose=choose;
    }
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup,int viewType) {
        ViewHolder viewHolder=ViewHolder.get(mContext,viewGroup,mLayoutId);
        return  viewHolder;
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        convert(viewHolder,mDatas.get(position));
        if(mOnItemClickListener !=null){
            viewHolder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    mOnItemClickListener.onClick(viewHolder.getAdapterPosition(),viewHolder);
                }
            });
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mOnItemClickListener.onLongClick(viewHolder.getAdapterPosition(),viewHolder);
                    return false;
                }
            });
        }
    }
    public void convert(ViewHolder viewHolder, Data data) {

        if(choose==0) {//显示界面1
            ImageView imageView = viewHolder.getView(R.id.item_image);
            ImageView country = viewHolder.getView(R.id.item_country);
            TextView name = viewHolder.getView(R.id.item_name);
            TextView sex = viewHolder.getView(R.id.item_sex);
            TextView both_dead = viewHolder.getView(R.id.item_date);
            TextView place = viewHolder.getView(R.id.item_place);
            TextView choose = viewHolder.getView(R.id.item_choose);
            //本地无缓存使用url获取
            if(data.getCache()==0) Glide.with(mContext).load(data.getUrl()).into(imageView);
            else imageView.setImageBitmap(data.getBitmap());
            name.setText(data.getName());
            choose.setText("");
            if (data.getSex() == 1) sex.setText("男");
            else sex.setText("女");
            both_dead.setText(data.getBoth_and_Dead());
            place.setText(data.getPlace());
            country.setImageBitmap(data.getCountry_image(mContext));

        }
        else if(choose==1){//界面2
            ImageView imageView = viewHolder.getView(R.id.item1_image);
            ImageView country = viewHolder.getView(R.id.item1_country);
            ImageView choose = viewHolder.getView(R.id.item1_choose);
            TextView name = viewHolder.getView(R.id.item1_name);
            Glide.with(mContext).load(data.getUrl()).into(imageView);
            //本地无缓存使用url获取
            if(data.getCache()==0)Glide.with(mContext).load(data.getUrl()).into(imageView);
            else imageView.setImageBitmap(data.getBitmap());
            name.setText(data.getName());
            country.setImageBitmap(data.getCountry_image(mContext));
            choose.setVisibility(View.INVISIBLE);
        }
        else {//界面3
            ImageView imageView = viewHolder.getView(R.id.item2_image);
            ImageView country = viewHolder.getView(R.id.item2_country);
            ImageView choose = viewHolder.getView(R.id.item2_choose);
            TextView name = viewHolder.getView(R.id.item2_name);
            //本地无缓存使用url获取
            if(data.getCache()==0)Glide.with(mContext).load(data.getUrl()).into(imageView);
            else imageView.setImageBitmap(data.getBitmap());
            name.setText(data.getName());
            country.setImageBitmap(data.getCountry_image(mContext));
            choose.setVisibility(View.INVISIBLE);
        }
    }
    //获取数据的数量
    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    //注册点击事件
    public interface OnItemClickListener{
        void onClick(int position, ViewHolder viewHolder);
        void onLongClick(int position, ViewHolder viewHolder);
    }
    private OnItemClickListener mOnItemClickListener = null;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mOnItemClickListener=onItemClickListener;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        private View mConvertView;//存储 list_Irem
        private SparseArray<View>mView;//存储 list_Item的子View
        public ViewHolder(Context context,View view,ViewGroup viewGroup){
            super(view);
            mConvertView=view;
            mView = new SparseArray<View>();
        }
        //获取viewHolder实例
        public  static ViewHolder get (Context context,ViewGroup viewGroup,int layoutId){
            View itemView = LayoutInflater.from(context).inflate(layoutId,viewGroup,false);
            ViewHolder viewHolder = new ViewHolder(context,itemView,viewGroup);
            return viewHolder;
        }
        //viewHolder尚未将子View缓存到SparseArray数组中时仍然需要通过findViewByld()创建View对象如果已缓存直接返回
        public <T extends View>T getView(int viewId){
            View view=mView.get(viewId);
            if(view == null){
                view=mConvertView.findViewById(viewId);
                mView.put(viewId,view);
            }
            return (T)view;
        }
    }
}
