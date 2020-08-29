package com.seongekim.tcc.server.shader;

@FunctionalInterface
public interface ShaderProgramBinding {
    void onBind(ShaderProgram program) throws Exception;
}
