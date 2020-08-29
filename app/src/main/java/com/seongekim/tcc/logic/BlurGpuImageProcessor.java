package com.seongekim.tcc.logic;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;

import com.seongekim.tcc.logic.shader.ShaderUtils;
import com.seongekim.tcc.shared.IBitmap;
import com.seongekim.tcc.shared.ImageProcessor;

import java.io.IOException;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup;

public class BlurGpuImageProcessor extends ImageProcessor {
    private GPUImage gpuImage;
    private float blurSize;
    private float sigma;

    private final Context context;

    public BlurGpuImageProcessor(final Context context, float blurSize, float sigma) {
        this.context = context;
        this.blurSize = blurSize;
        this.sigma = sigma;
    }

    private String getVertexShader() throws IOException {
        return ShaderUtils.getVertexShader(context, "blur");
    }

    private String getFragmentShader() throws IOException {
        return ShaderUtils.getFragmentShader(context, "blur");
    }

    private GPUImageFilter getFilter(final float widthOffset, final float heightOffset) throws IOException {
        return new GPUImageFilter(getVertexShader(), getFragmentShader()) {
            @Override
            public void onInit() {
                super.onInit();
                setFloatVec2(GLES20.glGetUniformLocation(getProgram(), "offset"),
                        new float[]{widthOffset, heightOffset});
                setFloat(GLES20.glGetUniformLocation(getProgram(), "blurSize"),  blurSize);
                setFloat(GLES20.glGetUniformLocation(getProgram(), "sigma"),  sigma);
            }
        };
    }

    @Override
    public IBitmap process(IBitmap src) {
        gpuImage = new GPUImage(context);
        GPUImageFilterGroup group = new GPUImageFilterGroup();
        try {
            group.addFilter(getFilter(1.0f, 0.0f));
            group.addFilter(getFilter(0.0f, 1.0f));
        } catch (IOException e) {
            e.printStackTrace();
        }
        gpuImage.setFilter(group);
        final Bitmap bitmap = gpuImage.getBitmapWithFilterApplied((Bitmap) src.getBitmapObject());
        return new AndroidBitmapWrapper(bitmap);
    }
}
