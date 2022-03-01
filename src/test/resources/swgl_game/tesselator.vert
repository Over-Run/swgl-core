#version 330 core

layout (location = 0) in vec3 Position;
layout (location = 1) in vec4 Color;
layout (location = 2) in vec2 UV0;
layout (location = 3) in vec3 Normal;

out vec4 vertexColor;
out vec2 texCoord0;
out vec3 fragPos;
out vec3 vertexNormal;

uniform mat4 ProjMat, ModelMat, ViewMat;

void main() {
    gl_Position = ProjMat * ViewMat * ModelMat * vec4(Position, 1.0);
    vertexColor = Color;
    texCoord0 = UV0;
    fragPos = vec3(ModelMat * vec4(Position, 1.0));
    vertexNormal = Normal;
}