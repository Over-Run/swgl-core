#version 110

attribute vec3 Position;
attribute vec4 Color;
attribute vec2 UV0;

varying vec4 vertexColor;
varying vec2 texCoord0;

uniform mat4 ProjMat;
uniform mat4 ViewMat;
uniform mat4 ModelMat;

void main() {
    gl_Position = ProjMat * ViewMat * ModelMat * vec4(Position, 1.0);
    vertexColor = Color;
    texCoord0 = UV0;
}