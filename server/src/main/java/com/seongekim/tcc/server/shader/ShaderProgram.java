package com.seongekim.tcc.server.shader;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL30.*;

public class ShaderProgram {

    private final int programId;

    private int vertexShaderId;

    private int fragmentShaderId;

    private ArrayList<ShaderProgramBinding> bindings;

    public ShaderProgram() throws Exception {
        OpenGLContext.getInitializedInstance();

        programId = glCreateProgram();
        if (programId == 0) {
            throw new Exception("Could not create Shader");
        }
        bindings = new ArrayList<>();
        bindings.add(AbstractShaderFilter.getAttributeBindings());
    }

    public int getAttribLocation(String attrib) {
        return glGetAttribLocation(programId, attrib);
    }

    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    protected int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new Exception("Error creating shader. Type: " + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(programId, shaderId);

        return shaderId;
    }

    public void link() throws Exception {
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

        if (vertexShaderId != 0) {
            glDetachShader(programId, vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            glDetachShader(programId, fragmentShaderId);
        }

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024));
        }
    }

    public void bind() throws Exception {
        glUseProgram(programId);
        for(ShaderProgramBinding binding : bindings) {
            binding.onBind(this);
        }
    }

    public void onBind(ShaderProgramBinding binding) {
        bindings.add(binding);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }

    public int getUniformLocation(String name) {
        return glGetUniformLocation(programId, name);
    }

    public void setUniform(String name, float value) {
        glUniform1f(getUniformLocation(name), value);
    }

    public void setUniform(String name, float[] value) throws Exception {
        if(value.length == 1)
            glUniform1fv(getUniformLocation(name), value);
        else if (value.length == 2)
            glUniform2fv(getUniformLocation(name), value);
        else if (value.length == 3)
            glUniform3fv(getUniformLocation(name), value);
        else if (value.length == 4)
            glUniform3fv(getUniformLocation(name), value);
        else throw new Exception("not a vector supported size");
    }
}