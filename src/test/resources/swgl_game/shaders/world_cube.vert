#version 330 core

layout(location = 0) in vec3 Position;
layout(location = 1) in vec3 Color;
layout(location = 2) in vec2 UV0;

out vec3 vertexColor;
out vec2 texCoord0;

uniform mat4 Projection;
uniform mat4 View;
uniform mat4 Model;

void main() {
    gl_Position = Projection * View * Model * vec4(Position, 1.0);
    vertexColor = Color;
    texCoord0 = UV0;
}