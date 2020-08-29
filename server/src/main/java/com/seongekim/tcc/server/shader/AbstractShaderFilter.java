package com.seongekim.tcc.server.shader;

import static org.lwjgl.opengl.GL30.*;

public abstract class AbstractShaderFilter {
    protected int resultTexture;
    protected boolean isInitialized;

    abstract void onInit(ShaderContext context);
    abstract void onDispose(ShaderContext context);
    void onBind(ShaderContext context) throws Exception {}
    void onRelease(ShaderContext context) {}
    abstract void run(ShaderContext context) throws Exception;

    public void init(ShaderContext context) {
        onInit(context);
        isInitialized = true;
    }

    public void dispose(ShaderContext context) {
        onDispose(context);
        if(hasResultTexture()) {
            glDeleteTextures(resultTexture);
            resultTexture = 0;
        }
        isInitialized = false;
    }

    public void bind(ShaderContext context) throws Exception {
        onBind(context);
    }

    public void release(ShaderContext context) {
        onRelease(context);
    }

    public static int createTexture() {
        int texId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        // Texture filtering params
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        return texId;
    }

    public static int createResultTexture(int width, int height) {
        int texId = createTexture();
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width,
                height, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
        glBindTexture(GL_TEXTURE_2D, 0);
        return texId;
    }

    public void setResultTexture(int textureId) {
        resultTexture = textureId;
    }

    public int getResultTexture() {
        return resultTexture;
    }

    public boolean hasResultTexture() {
        return resultTexture != 0;
    }

    public void bindResultTexture() {
        glBindTexture(GL_TEXTURE_2D, resultTexture);
    }

    public static ShaderProgramBinding getAttributeBindings() {
        return (ShaderProgram program) -> {
            int position;
            position = program.getAttribLocation("position");
            glVertexAttribPointer(position, 2, GL_FLOAT, false, Float.BYTES * 4, 0);
            glEnableVertexAttribArray(position);

            position = program.getAttribLocation("inputTextureCoordinate");
            glVertexAttribPointer(position, 2, GL_FLOAT, false, Float.BYTES * 4, Float.BYTES * 2);
            glEnableVertexAttribArray(position);
        };
    }
}
