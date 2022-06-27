#version 330 core

in vec3 vertexColor;
in vec2 texCoord0;

out vec4 FragColor;

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;

void main() {
    vec4 color = vec4(vertexColor, 1.0) * texture(Sampler0, texCoord0) * ColorModulator;
    if (color.a < 0.1) {
        discard;
    }
    FragColor = color;
}