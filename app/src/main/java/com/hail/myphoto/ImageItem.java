package com.hail.myphoto;

import java.io.Serializable;

/**
 * Created by 小智
 * on 2017/9/7
 * 描述：
 */

public class ImageItem implements Serializable {
    public String path;       //图片的路径

    public ImageItem(String picture) {
        this.path = picture;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
