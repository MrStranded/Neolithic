#version 130

in vec3 position;
in vec3 inColor;
in vec2 inTextureCoordinates;

out vec3 outColor;
out vec2 outTextureCoordinates;

uniform mat4 projectionMatrix;
uniform mat4 worldMatrix;

void main() {
    gl_Position = projectionMatrix * worldMatrix * vec4(position, 1.0);
    outColor = inColor;
    outTextureCoordinates = inTextureCoordinates;
}