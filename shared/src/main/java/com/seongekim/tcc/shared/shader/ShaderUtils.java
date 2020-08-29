package com.seongekim.tcc.shared.shader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ShaderUtils {
    public static String readString(String filename) throws IOException {
        ClassLoader classLoader = ShaderUtils.class.getClassLoader();
        File file = new File(classLoader.getResource(filename).getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }

    public static String getVertexShader(String name) throws IOException {
        return readString(name + "/" + "vertex.glsl");
    }

    public static String getFragmentShader(String name) throws IOException {
        return readString(name + "/" + "frag.glsl");
    }
}
