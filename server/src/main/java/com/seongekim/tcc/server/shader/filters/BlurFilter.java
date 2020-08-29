package com.seongekim.tcc.server.shader.filters;

import com.seongekim.tcc.server.shader.ShaderImageFilter;
import com.seongekim.tcc.server.shader.ShaderImageFilterGroup;
import com.seongekim.tcc.server.shader.ShaderProgram;
import com.seongekim.tcc.server.shader.ShaderUtils;

public class BlurFilter extends ShaderImageFilterGroup {
    private float blurSize;
    private float sigma;

    public BlurFilter(float blurSize, float sigma) throws Exception {
        this.blurSize = blurSize;
        this.sigma = sigma;
        addFilter(getComponentFilter(new float[]{1.0f, 0.0f}));
        addFilter(getComponentFilter(new float[]{0.0f, 1.0f}));
    }

    public ShaderImageFilter getComponentFilter(float[] offset) throws Exception {
        ShaderImageFilter filter = new ShaderImageFilter(
                ShaderUtils.readVertexShader("blur"),
                ShaderUtils.readFragmentShader("blur"));

        filter.getProgram().onBind((ShaderProgram program) -> {
            program.setUniform("blurSize", blurSize);
            program.setUniform("offset", offset);
            program.setUniform("sigma", sigma);
        });
        return filter;
    }
}
