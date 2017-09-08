package com.hail.myphoto;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jph.takephoto.model.TResult;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends TakePhotoActivity implements PhotoAdapter.OnRecyclerViewItemClickListener {
    public static final int IMAGE_ITEM_ADD = -1;
    private RecyclerView mRecyclerView;
    private PhotoAdapter mAdapter;
    private List<ImageItem> mList = new ArrayList<>(); //存放照片
    private int maxImgCount = 3;               //允许选择图片最大数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycleView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new PhotoAdapter(this, mList, maxImgCount);
        mAdapter.setOnItemDetailClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                selectPhotoFixed(maxImgCount, true);
                selectPhotoFixed(2, true);
            }
        });
    }

    /**
     * 照片返回
     *
     * @param result
     */
    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        Log.d("图片", "takeSuccess: 》》》》" + result.getImages().size());
        if (result.getImages().size() > 1) {
            String path = result.getImages().get(0).getCompressPath();
            String path2 = result.getImages().get(1).getCompressPath();
            List<String> list = new ArrayList<String>();
            list.add(path);
            list.add(path2);
            mList.add(new ImageItem(path));
            mList.add(new ImageItem(path2));
            mAdapter.setImages(mList);
        } else {
            String path = result.getImages().get(0).getCompressPath();
            mList.add(new ImageItem(path));
            mAdapter.setImages(mList);
        }
    }

    @Override
    public void onItemClick(int position) {
        Log.d("AAAA", "点击" + position);
        Log.d("AAAA", "list" + mList.size());
//        Log.d("AAAA", "item" + mList.get(position));
        if (position == mList.size() || (position == 2 && mList.get(position) == null)) {
            Log.d("AAAA", "多选" + mList.size());
            selectPhotoFixed(maxImgCount - mList.size(), true);
        } else {
            Toast.makeText(this, "查看大图", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDelateItemClick(int position) {
        Toast.makeText(this, "删除", Toast.LENGTH_SHORT).show();
        Log.d("AAAA", "点击删除" + position);
        Log.d("AAAA", "list删除" + mList.size());
        mList.remove(position);
        mAdapter.setImages(mList);
    }
}
