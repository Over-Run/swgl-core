#version 330 core

layout (location = 0) in vec3 Position;
layout (location = 1) in vec2 UV2;
layout (location = 2) in vec3 Normal;

out vec3 fragPos;
out vec2 texCoord2;
out vec3 normal;

uniform mat4 ProjMat;
uniform mat4 ViewMat;
uniform mat4 ModelMat;
uniform mat4 NormalMat;

void main() {
    gl_Position = ProjMat * ViewMat * ModelMat * vec4(Position, 1.0);
    fragPos = vec3(ModelMat * vec4(Position, 1.0));
    texCoord2 = vec2(UV2.x, 1.0 - UV2.y);
    normal = vec3(NormalMat * vec4(Normal, 0.0));
}