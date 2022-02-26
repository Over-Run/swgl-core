#version 330 core

layout (location = 0) in vec3 Position;
layout (location = 1) in vec2 UV2;
layout (location = 2) in vec3 Normal;
layout (location = 3) in mat4 InstanceMat;
layout (location = 7) in mat4 InstanceNormatMat;

out vec3 fragPos;
out vec2 texCoord2;
out vec3 normal;

uniform mat4 ProjMat;
uniform mat4 ViewMat;
uniform mat4 ModelMat;
uniform mat4 NormalMat;
uniform bool HasInstance;

void main() {
    mat4 mat = ModelMat;
    if (HasInstance) {
        mat *= InstanceMat;
    }
    gl_Position = ProjMat * ViewMat * mat * vec4(Position, 1.0);
    fragPos = vec3(mat * vec4(Position, 1.0));
    texCoord2 = vec2(UV2.s, 1.0 - UV2.t);
    if (HasInstance) {
        normal = vec3(InstanceNormatMat * vec4(Normal, 0.0));
    } else {
        normal = vec3(NormalMat * vec4(Normal, 0.0));
    }
}