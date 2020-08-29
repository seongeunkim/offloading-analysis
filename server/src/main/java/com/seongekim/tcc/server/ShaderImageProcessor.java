package com.seongekim.tcc.server;

import com.seongekim.tcc.server.shader.AbstractShaderFilter;
import com.seongekim.tcc.server.shader.OpenGLContext;
import com.seongekim.tcc.server.shader.PackedImage;
import com.seongekim.tcc.server.shader.ShaderContext;
import com.seongekim.tcc.server.shader.ShaderImageFilter;
import com.seongekim.tcc.shared.IBitmap;
import com.seongekim.tcc.shared.ImageProcessor;

import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicReference;

public class ShaderImageProcessor extends ImageProcessor {
    private AbstractShaderFilter filter;

    public ShaderImageProcessor(String vertexShader, String fragShader) throws Exception {
        filter = new ShaderImageFilter(vertexShader, fragShader);
    }
    public ShaderImageProcessor(AbstractShaderFilter filter) {
        this.filter = filter;
    }

    @Override
    public IBitmap process(IBitmap src) {
        BufferedImage image = (BufferedImage) src.getBitmapObject();

        OpenGLContext gl = OpenGLContext.getInitializedInstance();

        PackedImage tex = PackedImage.create(image);
        ShaderContext context = new ShaderContext(tex);
        filter.init(context);
        AtomicReference<BufferedImage> result = new AtomicReference<>();
        gl.submit(() -> {
            try {
                result.set(context.runThroughImage(filter, PackedImage.Format.ARGB).toBitmap());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        filter.dispose(context);
        context.dispose();

        return new JavaImageWrapper(result.get());
    }
}
