package com.seongekim.tcc.server.shader.filters;

import com.seongekim.tcc.server.shader.ShaderImageFilter;
import com.seongekim.tcc.server.shader.ShaderUtils;

import java.io.IOException;

public class PassThroughFilter extends ShaderImageFilter {
    private static String VERTEX_SHADER;
    private static String FRAG_SHADER;


    static {
        try {
            VERTEX_SHADER = ShaderUtils.readVertexShader("pass");
            FRAG_SHADER =  ShaderUtils.readFragmentShader("pass");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PassThroughFilter() throws Exception {
        super(VERTEX_SHADER, FRAG_SHADER);
    }
}
