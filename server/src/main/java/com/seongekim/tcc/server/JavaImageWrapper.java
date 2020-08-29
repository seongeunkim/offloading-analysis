package com.seongekim.tcc.server;

import com.seongekim.tcc.shared.IBitmap;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class JavaImageWrapper implements IBitmap {
    BufferedImage img;
    Graphics2D graphics;

    public JavaImageWrapper(BufferedImage img) {
        this.img = img;
    }

    public BufferedImage getImage() {
        return img;
    }

    @Override
    public int getPixel(int x, int y) {
        return img.getRGB(x, y);
    }

    @Override
    public int getWidth() {
        return img.getWidth();
    }

    @Override
    public int getHeight() {
        return img.getHeight();
    }

    @Override
    public void setPixel(int x, int y, int color) {
        img.setRGB(x, y, color);
    }

    @Override
    public IBitmap createBitmap(int width, int height) {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        return new JavaImageWrapper(bufferedImage);
    }

    public static JavaImageWrapper fromStream(InputStream stream) throws IOException {
        return new JavaImageWrapper(ImageIO.read(stream));
    }

    public byte[] bytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();
        return imageInByte;
    }

    @Override
    public Object getBitmapObject() {
        return img;
    }
}
