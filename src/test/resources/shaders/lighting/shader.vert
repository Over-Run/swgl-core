#version 110

attribute vec3 Position;
attribute vec2 UV2;
attribute vec3 Normal;

varying vec3 fragPos;
varying vec2 texCoord2;
varying vec3 normal;

uniform mat4 ProjMat;
uniform mat4 ViewMat;
uniform mat4 ModelMat;
uniform mat4 NormalMat;

void main() {
    gl_Position = ProjMat * ViewMat * ModelMat * vec4(Position, 1.0);
    fragPos = vec3(ModelMat * vec4(Position, 1.0));
    texCoord2 = UV2;
    normal = vec3(NormalMat * vec4(Normal, 0.0));
}