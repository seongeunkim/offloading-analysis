package com.seongekim.tcc.shared;

public class Color {
    public static int getComponent(int color, int i) {
        return (color >> (i * 8)) & 0xFF;
    }
    public static int red(int color) {
        return getComponent(color, 2);
    }
    public static int green(int color) {
        return getComponent(color, 1);
    }
    public static int blue(int color) {
        return getComponent(color, 0);
    }
    public static int alpha(int color) {
        return getComponent(color, 3);
    }
    public static float redf(int color) {
        return red(color) / 255.0f;
    }
    public static float greenf(int color) {
        return green(color) / 255.0f;
    }
    public static float bluef(int color) {
        return blue(color) / 255.0f;
    }
    public static float alphaf(int color) {
        return alpha(color) / 255.0f;
    }
    public static int argb(int A, int R, int G, int B) {
       return  (A & 0xff) << 24 | (R & 0xff) << 16 | (G & 0xff) << 8 | (B & 0xff);
    }
    public static float argb(float A, float R, float G, float B) {
        return argb(fromFloat(A), fromFloat(R), fromFloat(G), fromFloat(B));
    }
    private static int fromFloat(float x) {
        return Math.round(x * 255.0f);
    }
}
