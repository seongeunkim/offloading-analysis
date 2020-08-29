package com.seongekim.tcc.shared;

public class BlackWhiteImageProcessor extends ImageProcessor {
    public IBitmap process(IBitmap src) {
        IBitmap dst = src.createBitmap(src.getWidth(), src.getHeight());
        int A, R, G, B;
        int pixel;

        for (int x = 0; x < dst.getWidth(); x++) {
            for (int y = 0; y < dst.getHeight(); y++) {
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                R = G = B = (int) ((float) (R + G + B) / 3);
                dst.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        return dst;
    }
}
