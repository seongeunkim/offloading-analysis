package com.seongekim.tcc.logic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.seongekim.tcc.shared.IBitmap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AndroidBitmapWrapper implements IBitmap {
    private Bitmap bitmap;

    public AndroidBitmapWrapper(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    public int getWidth() {
        return bitmap.getWidth();
    }

    @Override
    public int getHeight() {
        return bitmap.getHeight();
    }

    public Bitmap.Config getConfig() {
        return bitmap.getConfig();
    }

    @Override
    public int getPixel(int x, int y) {
        return bitmap.getPixel(x, y);
    }

    @Override
    public void setPixel(int x, int y, int color) {
        bitmap.setPixel(x, y, color);
    }

    @Override
    public IBitmap createBitmap(int width, int height) {
        return new AndroidBitmapWrapper(Bitmap.createBitmap(getWidth(), getHeight(), getConfig()));
    }

    public static AndroidBitmapWrapper fromBytes(byte[] bytes) {
        return new AndroidBitmapWrapper(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
    }

    public byte[] bytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    @Override
    public Object getBitmapObject() {
        return bitmap;
    }
}
