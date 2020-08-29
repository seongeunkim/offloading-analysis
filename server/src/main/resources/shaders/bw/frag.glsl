#version 110

varying vec2 vTexCoord;
uniform sampler2D source;

void main() {
    vec4 color = texture2D(source, vTexCoord);
    // 0.2126*R + 0.7152*G + 0.0722*B
    float lum = dot(color.rgb, vec3(0.2126, 0.7152, 0.0722));
    gl_FragColor = vec4(lum, lum, lum, color.a);
}