#version 450

in vec3 inPosition;
in vec2 inTextureCoordinates;
in vec4 inColor;

out vec2 outTextureCoordinates;
out vec4 outColor;

uniform mat4 projectionViewMatrix;
uniform float horizontalStep;
uniform float verticalStep;

void main() {
    gl_Position = projectionViewMatrix * vec4(inPosition, 1.0);

    outColor = inColor;

    outTextureCoordinates = inTextureCoordinates;
}