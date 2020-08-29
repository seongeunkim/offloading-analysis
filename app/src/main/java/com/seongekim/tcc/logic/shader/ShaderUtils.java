package com.seongekim.tcc.logic.shader;

import android.content.Context;

import com.seongekim.tcc.logic.Utils;

import java.io.IOException;

public class ShaderUtils {
    public static String getVertexShader(final Context context, String name) throws IOException {
        int id = context.getResources().getIdentifier(name + "_vertex", "raw", context.getPackageName());
        return Utils.readString(context.getResources().openRawResource(id));
    }

    public static String getFragmentShader(final Context context, String name) throws IOException {
        int id = context.getResources().getIdentifier(name + "_frag", "raw", context.getPackageName());
        return Utils.readString(context.getResources().openRawResource(id));
    }
}
