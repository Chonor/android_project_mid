package cn.chonor.project_1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import java.util.List;
import java.util.Map;

/**
 * Created by ASUS on 2017/10/31.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.mViewHolder> {
    private Context context;
    List<GwentCard> mdata;
    private OnItemClickListener mOnItemClickListener;

    public RecyclerAdapter(Context context,List<GwentCard> datas){
        this.context=context;
        this.mdata=datas;
    }
    public interface OnItemClickListener{
        void OnClick(View v, int position);
    }
    public void setItemClickListener(OnItemClickListener listener){
        mOnItemClickListener=listener;
    }

    @Override
    public mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
        mViewHolder holder=new mViewHolder(v,mOnItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(mViewHolder holder, int position) {
        holder.img.setImageDrawable(context.getResources().getDrawable(mdata.get(position).getImg()));
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class mViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView img;
        OnItemClickListener mListener;
        public mViewHolder(View itemView,OnItemClickListener tmp) {
            super(itemView);
            img=(ImageView)itemView.findViewById(R.id.img);
            this.mListener=tmp;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mListener!=null){
                mListener.OnClick(v,getPosition());
            }
        }


    }
}
