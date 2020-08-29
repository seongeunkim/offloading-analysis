package com.seongekim.tcc.server.shader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ShaderUtils {
    private static String readString(String filename) throws IOException {
        StringBuilder buffer = new StringBuilder();
        InputStream stream = ShaderUtils.class.getResourceAsStream(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while ((line = reader.readLine()) != null){
            buffer.append(line);
            buffer.append("\n");
        }
        return buffer.toString();
    }

    public static String readVertexShader(String name) throws IOException {
        return readString("/shaders/" + name + "/vertex.glsl");
    }

    public static String readFragmentShader(String name) throws IOException {
        return readString("/shaders/" + name + "/frag.glsl");
    }
}
