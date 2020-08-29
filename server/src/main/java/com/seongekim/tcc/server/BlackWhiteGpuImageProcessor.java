package com.seongekim.tcc.server;

import com.seongekim.tcc.server.shader.filters.GrayscaleFilter;

public class BlackWhiteGpuImageProcessor extends ShaderImageProcessor {
    public BlackWhiteGpuImageProcessor() throws Exception {
        super(new GrayscaleFilter());
    }
}
