#version 330 core

layout (location = 0) in vec2 UV0;
layout (location = 1) in vec4 Color;
layout (location = 2) in vec3 Position;

out vec4 vertexColor;
out vec2 texCoord0;

uniform mat4 ProjMat;
uniform mat4 ViewMat;
uniform mat4 ModelMat;

void main() {
    gl_Position = ProjMat * ViewMat * ModelMat * vec4(Position, 1.0);
    vertexColor = Color;
    texCoord0 = UV0;
}