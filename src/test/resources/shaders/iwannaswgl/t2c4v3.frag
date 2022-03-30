#version 330 core

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 FragColor;

uniform int TextureEnabled;
uniform sampler2D Sampler0;

void main() {
    FragColor = vertexColor * (TextureEnabled * texture(Sampler0, texCoord0) - TextureEnabled + 1);
}