package com.seongekim.tcc.server;

import com.seongekim.tcc.server.shader.filters.BlurFilter;

public class BlurGpuImageProcessor extends ShaderImageProcessor {
    public BlurGpuImageProcessor(float blurSize, float sigma) throws Exception {
        super(new BlurFilter(blurSize, sigma));
    }
}
