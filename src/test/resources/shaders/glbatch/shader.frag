#version 110

varying vec4 vertexColor;

void main() {
    vec4 color = vertexColor;
    if (color.a < 0.1) {
        discard;
    }
    gl_FragColor = color;
}