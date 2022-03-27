#version 110

attribute vec4 Color;
attribute vec3 Position;

varying vec4 vertexColor;

uniform mat4 ProjMat;
uniform mat4 ModelViewMat;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    vertexColor = Color;
}