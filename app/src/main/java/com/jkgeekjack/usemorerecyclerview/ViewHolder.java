package com.jkgeekjack.usemorerecyclerview;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/6/26.
 */
public class ViewHolder extends RecyclerView.ViewHolder {
//    SparseArray类似HaspMap不过比HashMap节省空间，方法都差不多，id和控件对应
    private SparseArray<View> mViews;
    private View mConvertView;

    public ViewHolder( View itemView)
    {
        super(itemView);
        mViews = new SparseArray<View>();
        mConvertView=itemView;
    }

    //根据id和内容进行摆放
    public void setText(int viewId,String msg){
        TextView textView=getView(viewId);
        textView.setText(msg);
    }

    /**
     * 通过viewId获取控件
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId)
    {
        View view = mViews.get(viewId);
        if (view == null)
        {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }
}
