#version 150 core

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 FragColor;

uniform sampler2D Sampler0;

void main() {
    vec4 color = vertexColor * texture(Sampler0, texCoord0);
    if (color.a < 0.1)
        discard;
    FragColor = color;
}