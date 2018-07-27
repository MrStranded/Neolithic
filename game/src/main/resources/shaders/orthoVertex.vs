#version 130

in vec3 inPosition;
in vec3 inNormal;
in vec2 inTextureCoordinates;

out vec2 outTextureCoordinates;

uniform mat4 projectionViewMatrix;

void main() {
    gl_Position = projectionViewMatrix * vec4(inPosition, 1.0);

    outTextureCoordinates = inTextureCoordinates;
}