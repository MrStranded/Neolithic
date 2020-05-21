#version 450

in vec3 inPosition;

uniform mat4 modelLightViewMatrix;
uniform mat4 orthographicProjectionMatrix;

void main() {
    gl_Position = orthographicProjectionMatrix * modelLightViewMatrix * vec4(inPosition, 1.0);
}