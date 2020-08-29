package com.seongekim.tcc.shared;

import java.io.IOException;

public interface IBitmap {
    int getPixel(int x, int y);
    int getWidth();
    int getHeight();

    void setPixel(int x, int y, int color);

    IBitmap createBitmap(int width, int height);

    byte[] bytes() throws IOException;

    Object getBitmapObject();
}
