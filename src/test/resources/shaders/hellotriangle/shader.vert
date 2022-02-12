#version 110

attribute vec3 Position;
attribute vec4 Color;

varying vec4 vertexColor;

uniform mat4 ModelViewMat;

void main() {
    gl_Position = ModelViewMat * vec4(Position, 1.0);
    vertexColor = Color;
}