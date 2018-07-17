#version 130

in vec3 position;
in vec3 normal;
in vec2 inTextureCoordinates;

out vec2 outTextureCoordinates;

uniform mat4 viewMatrix;
uniform mat4 worldMatrix;

void main() {
    gl_Position = viewMatrix * worldMatrix * vec4(position, 1.0);
    outTextureCoordinates = inTextureCoordinates;
}