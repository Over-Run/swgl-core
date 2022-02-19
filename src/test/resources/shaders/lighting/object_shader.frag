#version 110

uniform vec3 ObjectColor;
uniform vec3 LightColor;

void main() {
    gl_FragColor = vec4(ObjectColor * LightColor, 1.0);
}