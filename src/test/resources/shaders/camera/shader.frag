#version 110

varying vec4 vertexColor;
varying vec2 texCoord0;

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

void main() {
    vec4 color = mix(texture2D(Sampler0, texCoord0), texture2D(Sampler1, texCoord0), 0.2) * vertexColor;
    if (color.a < 0.1) {
        discard;
    }
    gl_FragColor = color;
}