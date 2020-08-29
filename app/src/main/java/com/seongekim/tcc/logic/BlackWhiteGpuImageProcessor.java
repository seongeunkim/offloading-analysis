package com.seongekim.tcc.logic;

import android.content.Context;
import android.graphics.Bitmap;

import com.seongekim.tcc.shared.IBitmap;
import com.seongekim.tcc.shared.ImageProcessor;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGrayscaleFilter;

public class BlackWhiteGpuImageProcessor extends ImageProcessor {
    private GPUImage gpuImage;

    private final Context context;

    public BlackWhiteGpuImageProcessor(final Context context) {
        this.context = context;
    }

    @Override
    public IBitmap process(IBitmap src) {
        gpuImage = new GPUImage(context);
        gpuImage.setFilter(new GPUImageGrayscaleFilter());
        final Bitmap bitmap = gpuImage.getBitmapWithFilterApplied((Bitmap) src.getBitmapObject());
        return new AndroidBitmapWrapper(bitmap);
    }
}
