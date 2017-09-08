package com.hail.myphoto;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 小智
 * on 2017/8/31
 * 描述：
 */

public class PhotoAdapter extends RecyclerView.Adapter<ViewHolder> {
    private int maxImgCount;  //最大数量
    private Context mContext;
    private List<ImageItem> mData;
    private LayoutInflater mInflater;
    private boolean isAdded;   //是否额外添加了最后一个图片
    private int clickPosition;

    public void setImages(List<ImageItem> data) {
        mData = new ArrayList<>(data);
        if (getItemCount() < maxImgCount) {
            mData.add(new ImageItem(""));
            isAdded = true;
        } else {
            isAdded = false;
        }
        notifyDataSetChanged();
    }

    public List<ImageItem> getImages() {
        //由于图片未选满时，最后一张显示添加图片，因此这个方法返回真正的已选图片
        if (isAdded) return new ArrayList<>(mData.subList(0, mData.size() - 1));
        else return mData;
    }

    public PhotoAdapter(Context mContext, List<ImageItem> data, int maxImgCount) {
        this.mContext = mContext;
        this.mData = data;
        this.maxImgCount = maxImgCount;
        this.mInflater = LayoutInflater.from(mContext);
        setImages(data);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SelectedPicViewHolder(mInflater.inflate(R.layout.list_item_image, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ImageItem item = mData.get(position);
        SelectedPicViewHolder viewHolder = (SelectedPicViewHolder) holder;
        Log.d("AAAAA", "onBindViewHolder:  position >>>>>"+position);
        if (isAdded && position == getItemCount() - 1) {
            viewHolder.iv_img_del.setVisibility(View.GONE);
            viewHolder.iv_img.setImageResource(R.drawable.selector_image_add);
            clickPosition = MainActivity.IMAGE_ITEM_ADD;
            Log.d("AAAAA", "onBindViewHolder: clickPosition>>>>" + clickPosition);
        } else {
            viewHolder.iv_img_del.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(item.path)
                    .into(viewHolder.iv_img);
            clickPosition = position;
        }
//        viewHolder.item_all.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("AAAAA", "onClick: >>>>>>>>" + clickPosition);
//                listener.onItemClick(clickPosition);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class SelectedPicViewHolder extends ViewHolder {
        private ImageView iv_img;
        private ImageView iv_img_del;
        private RelativeLayout item_all;

        public SelectedPicViewHolder(View itemView) {
            super(itemView);
            iv_img = (ImageView) itemView.findViewById(R.id.iv_img);
            iv_img_del = (ImageView) itemView.findViewById(R.id.iv_img_del);
            item_all = (RelativeLayout) itemView.findViewById(R.id.item_all);
            iv_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(getLayoutPosition());
                }
            });
            iv_img_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDelateItemClick(getLayoutPosition());
                }
            });
        }

    }

    private OnRecyclerViewItemClickListener listener;

    public void setOnItemDetailClickListener(OnRecyclerViewItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(int position);
        void onDelateItemClick(int position);
    }
}
