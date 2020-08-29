#version 110

const int GAUSSIAN_SAMPLES = 9;

attribute vec2 position;
attribute vec2 inputTextureCoordinate;

varying vec2 blurCoord[GAUSSIAN_SAMPLES];

uniform vec2 offset;
uniform float blurSize;

void main() {
    gl_Position = vec4(position, 0.0, 1.0);
    for(int i = 0; i < GAUSSIAN_SAMPLES; i++) {
        blurCoord[i] = inputTextureCoordinate + blurSize * float(i - (GAUSSIAN_SAMPLES/2)) / float(GAUSSIAN_SAMPLES/2) * offset;
    }
}