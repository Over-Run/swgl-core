#version 150 core
#define SWGL_GAME_AMBIENT_STRENGTH (0.6)

struct DirLight {
    vec3 direction;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

in vec4 vertexColor;
in vec2 texCoord0;
in vec3 vertexNormal;

out vec4 FragColor;

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform bool HasColor, HasTexture;

void main() {
    vec4 color = ColorModulator;
    if (HasColor)
        color *= vertexColor;
    if (HasTexture)
        color *= texture(Sampler0, texCoord0);
    if (color.a < 0.1)
        discard;
    FragColor = color;
}