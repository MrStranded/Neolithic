#version 130

in vec3 position;
in vec3 inColor;

out vec3 outColor;

void main() {
    gl_Position = vec4(position, 1.0);
    outColor = inColor;
}