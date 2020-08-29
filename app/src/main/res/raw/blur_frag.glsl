const int GAUSSIAN_SAMPLES = 9;

varying vec2 blurCoord[GAUSSIAN_SAMPLES];
uniform float sigma;
uniform sampler2D inputImageTexture;

float normpdf(in float x, in float sigma)
{
    return 0.39894*exp(-0.5*x*x/(sigma*sigma))/sigma;
}

void main() {
    vec3 color = vec3(0.0);
    float sum_weight = 0.0;
    for(int i = 0; i < GAUSSIAN_SAMPLES; i++) {
        float weight = normpdf(float(i - (GAUSSIAN_SAMPLES / 2)), sigma);
        sum_weight += weight;
        color += texture2D(inputImageTexture, blurCoord[i]).rgb * weight;
    }
    vec4 mainColor = texture2D(inputImageTexture, blurCoord[GAUSSIAN_SAMPLES / 2]);
    gl_FragColor = vec4(color / sum_weight, mainColor.a);
}