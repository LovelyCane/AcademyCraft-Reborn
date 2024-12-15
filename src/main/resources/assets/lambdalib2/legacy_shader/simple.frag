#version 120

varying vec4 Color;

uniform sampler2D gSampler;
varying vec2 UV;

void main() {
    gl_FragColor = Color * texture2D(gSampler, UV);
}