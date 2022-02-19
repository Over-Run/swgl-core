#version 110

attribute vec3 Position;

uniform mat4 ProjMat;
uniform mat4 ViewMat;
uniform mat4 ModelMat;

void main() {
    gl_Position = ProjMat * ViewMat * ModelMat * vec4(Position, 1.0);
}