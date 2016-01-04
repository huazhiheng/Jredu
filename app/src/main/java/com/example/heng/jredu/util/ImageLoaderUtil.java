package com.example.heng.jredu.util;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.example.heng.jredu.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class ImageLoaderUtil {

    private static DisplayImageOptions options =new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.banner_01)
            .showImageOnFail(R.drawable.banner_01)
            .showImageForEmptyUri(R.drawable.banner_01)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .resetViewBeforeLoading(true)
            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
            .displayer(new FadeInBitmapDisplayer(200))
            .build();
    private static DisplayImageOptions optionsBig = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.banner_02)
            .showImageOnFail(R.drawable.banner_02)
            .showImageForEmptyUri(R.drawable.banner_02)
            .cacheInMemory(true).cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565).resetViewBeforeLoading(true)
            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
            .displayer(new FadeInBitmapDisplayer(200)).build();
    private static DisplayImageOptions headerOptioins = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.banner_01)
            .showImageOnFail(R.drawable.banner_01)
            .showImageForEmptyUri(R.drawable.banner_01)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .resetViewBeforeLoading(true)
            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
            .displayer(new FadeInBitmapDisplayer(200))
            .build();

    public static void display(String uri,ImageView imageView){
        ImageLoader.getInstance().displayImage(uri, imageView, options);
    }
    public static void displayBigPhoto(String uri,ImageView imageView){
        ImageLoader.getInstance().displayImage(uri, imageView, optionsBig);
    }




//	public static void displayUserHeader(ImageView imageView){
//		String uri = MyAppliaction.getInstance().getCurUser().getImg();
//		ImageLoader.getInstance().displayImage(uri, imageView, headerOptioins);
//	}
}
