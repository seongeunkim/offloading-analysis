#version 110

varying vec2 textureCoordinate;

uniform sampler2D source;

void main() {
    gl_FragColor = texture2D(source, textureCoordinate);
}