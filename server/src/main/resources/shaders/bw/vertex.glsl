#version 110

attribute vec2 position;
attribute vec2 inputTextureCoordinate;

varying vec2 vTexCoord;

void main() {
    vTexCoord = inputTextureCoordinate;
    gl_Position = vec4(position, 0.0, 1.0);
}