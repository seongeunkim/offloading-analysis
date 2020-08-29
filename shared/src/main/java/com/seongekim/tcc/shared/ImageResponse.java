package com.seongekim.tcc.shared;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ImageResponse {
    public Long requestMs;
    public byte[] image;

    public ImageResponse(Long requestMs, byte[] image) {
        this.requestMs = requestMs;
        this.image = image;
    }

    public byte[] Serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeLong(requestMs);
        dos.write(image);
        dos.close();
        return baos.toByteArray();
    }

    public static ImageResponse Deserialize(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        Long requestMs = dis.readLong();
        byte[] image = new byte[data.length - Long.BYTES];
        dis.read(image);
        dis.close();
        return new ImageResponse(requestMs, image);
    }
}
