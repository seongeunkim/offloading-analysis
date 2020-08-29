package com.seongekim.tcc.server.shader;

public class ShaderImageFilter extends AbstractShaderFilter {
    private ShaderProgram program;

    public ShaderImageFilter(String vertexShader, String fragShader) throws Exception {
        this.program = new ShaderProgram();
        this.program.createVertexShader(vertexShader);
        this.program.createFragmentShader(fragShader);
        this.program.link();
    }

    public void onInit(ShaderContext context) {
        if (!hasResultTexture()) {
            setResultTexture(createResultTexture(context.getWidth(), context.getHeight()));
        }
    }

    public void onDispose(ShaderContext context) {}

    public void onBind(ShaderContext context) throws Exception {
        program.bind();
    }

    public void onRelease(ShaderContext context) {
        program.unbind();
    }

    public void run(ShaderContext context) throws Exception {
        context.bindInputTexture();
        context.drawOnTexture(resultTexture, program);
        context.unbindTexture();
    }

    public ShaderProgram getProgram() {
        return program;
    }
}
