package com.seongekim.tcc.server.shader;

import org.lwjgl.BufferUtils;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL30.GL_BGRA;
import static org.lwjgl.opengl.GL30.GL_RGBA;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL30.GL_UNSIGNED_INT_8_8_8_8_REV;
import static org.lwjgl.opengl.GL30.glReadPixels;
import static org.lwjgl.opengl.GL30.glTexImage2D;

public class PackedImage {
    public enum Format {
        RGBA,
        ARGB
    }

    private static Map<Format, Integer> formatGlColor = new HashMap<Format, Integer>(){{
        put(Format.RGBA, GL_RGBA);
        put(Format.ARGB, GL_BGRA);
    }};

    private static Map<Format, Integer> formatGlType = new HashMap<Format, Integer>(){{
        put(Format.RGBA, GL_UNSIGNED_BYTE);
        put(Format.ARGB, GL_UNSIGNED_INT_8_8_8_8_REV);
    }};

    private ByteBuffer buffer;
    private int width;
    private int height;
    private Format format;

    public PackedImage(ByteBuffer buffer, int width, int height, Format format) {
        this.buffer = buffer;
        this.width = width;
        this.height = height;
        this.format = format;
    }

    public static PackedImage create(BufferedImage image)  {
        ByteBuffer buffer = BufferUtils.createByteBuffer(4 * image.getWidth() * image.getHeight());
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        buffer.asIntBuffer().put(pixels);
        buffer.rewind();

        return new PackedImage(buffer, image.getWidth(), image.getHeight(), Format.ARGB);
    }

    public static PackedImage createFromFramebuffer(int width, int height, Format format) {
        ByteBuffer buffer = BufferUtils.createByteBuffer(4 * width * height);
        glReadPixels(0, 0, width, height, formatGlColor.get(format), formatGlType.get(format), buffer);
        buffer.rewind();

        return new PackedImage(buffer, width, height, format);
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int glColorFormat() {
        return formatGlColor.get(format);
    }

    public int glTypeFormat() {
        return formatGlType.get(format);
    }

    public void putOnTexture() {
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, getWidth(),
                getHeight(), 0, glColorFormat(), glTypeFormat(), getBuffer());
        buffer.rewind();
    }

    public BufferedImage toBitmap() throws Exception {
        if(format != Format.ARGB) {
            throw new Exception("Unsupported color format: " + format);
        }
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int[] pixels = new int[width * height];
        buffer.asIntBuffer().get(pixels);
        buffer.rewind();

        image.setRGB(0, 0, width, height, pixels, 0, width);
        return image;
    }
}
