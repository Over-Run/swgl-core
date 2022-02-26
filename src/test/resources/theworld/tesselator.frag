#version 150 core

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 FragColor;

uniform sampler2D Sampler0;
uniform bool HasColor, HasTexture;

void main() {
    vec4 color = vec4(1.0, 1.0, 1.0, 1.0);
    if (HasColor)
        color *= vertexColor;
    if (HasTexture)
        color *= texture(Sampler0, texCoord0);
    if (color.a < 0.1)
        discard;
    FragColor = color;
}