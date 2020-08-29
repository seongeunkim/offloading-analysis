package com.seongekim.tcc.server.shader;

import com.seongekim.tcc.server.shader.filters.PassThroughFilter;

import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Stack;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.*;

public class ShaderContext {
    static private float quads[] = {
            -1.0f, -1.0f, 0.0f, 1.0f,
            1.0f, -1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 0.0f,
            -1.0f, 1.0f, 0.0f, 0.0f,
    };

    static private float invertedQuads[] = {
            -1.0f, -1.0f, 0.0f, 0.0f,
            1.0f, -1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, 0.0f, 1.0f,
    };

    static private int indices[] = {
            2, 3, 0,
            0, 1, 2,
    };

    private MemoryStack stack;

    private Stack<Integer> textureStack;
    private Integer createdTexture;

    private int quadId;
    private int invertedQuadId;
    private IntBuffer indicesBuffer;

    private int width;
    private int height;
    private int fbo;

    private ShaderProgram screenProgram;

    public ShaderContext(int inputTexture, int width, int height) {
        this.width = width;
        this.height = height;
        init();
        pushTexture(inputTexture);
    }

    public ShaderContext(PackedImage image) {
        this.width = image.getWidth();
        this.height = image.getHeight();
        int texId = AbstractShaderFilter.createTexture();
        image.putOnTexture();
        glBindTexture(GL_TEXTURE_2D, 0);
        createdTexture = texId;
        init();
        pushTexture(texId);
    }

    private void init() {
        stack = stackPush();
        textureStack = new Stack<>();

        FloatBuffer verticesBuffer = stack.mallocFloat(quads.length);
        verticesBuffer.put(quads).flip();

        FloatBuffer invertedBuffer = stack.mallocFloat(quads.length);
        invertedBuffer.put(invertedQuads).flip();

        indicesBuffer = stack.mallocInt(indices.length);
        indicesBuffer.put(indices).flip();

        quadId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, quadId);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);

        invertedQuadId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, invertedQuadId);
        glBufferData(GL_ARRAY_BUFFER, invertedBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        fbo = glGenFramebuffers();
    }

    public void dispose() {
        glDeleteBuffers(new int[]{quadId, invertedQuadId});
        if(createdTexture != null)
            glDeleteTextures(createdTexture);
    }

    public void pushTexture(int texId) {
        textureStack.push(texId);
    }

    public void popTexture() {
        textureStack.pop();
    }

    public void runOnInputTexture(int texId, Runnable runnable) {
        pushTexture(texId);
        runnable.run();
        popTexture();
    }

    public void runOnInputTexture(int texId, AbstractShaderFilter filter) throws Exception {
        pushTexture(texId);
        run(filter);
        popTexture();
    }

    public void run(AbstractShaderFilter filter) throws Exception {
        filter.run(this);
    }


    public void runThroughScreen(AbstractShaderFilter filter, int width, int height) throws Exception {
        run(filter);
        filter.bindResultTexture();
        drawOnScreen(width, height);
        unbindTexture();
    }

    public PackedImage runThroughImage(AbstractShaderFilter filter, PackedImage.Format format) throws Exception {
        run(filter);
        bindTextureToFramebuffer(filter.getResultTexture());
        PackedImage result = PackedImage.createFromFramebuffer(width, height, format);

        // Test.
        filter.bindResultTexture();
        drawOnScreen(800, 600);
        unbindTexture();

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        return result;
    }

    public Integer getInputTexture() {
        if(textureStack.isEmpty())
            return null;
        return textureStack.peek();
    }

    public void bindInputTexture() {
        glBindTexture(GL_TEXTURE_2D, getInputTexture());
    }

    public void unbindTexture() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    private void bindTextureToFramebuffer(int texId) throws Exception {
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texId, 0);
        glDrawBuffer(GL_COLOR_ATTACHMENT0);

        int status;
        if((status = glCheckFramebufferStatus(GL_FRAMEBUFFER)) != GL_FRAMEBUFFER_COMPLETE) {
            throw new Exception("Framebuffer is invalid state (" + status + ")");
        }
    }

    public void drawOnScreen(int width, int height) throws Exception {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, quadId);
        glViewport(0, 0, width, height);

        getScreenProgram().bind();
        glDrawElements(GL_TRIANGLES, indicesBuffer);
        getScreenProgram().unbind();

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void drawOnTexture(int texId, ShaderProgram program) throws Exception {
        bindTextureToFramebuffer(texId);

        glBindBuffer(GL_ARRAY_BUFFER, invertedQuadId);
        glViewport(0, 0, width, height);

        program.bind();
        glDrawElements(GL_TRIANGLES, indicesBuffer);
        program.unbind();

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ShaderProgram getScreenProgram() throws Exception {
        if(screenProgram == null) {
            screenProgram = new PassThroughFilter().getProgram();
        }
        return screenProgram;
    }
}
