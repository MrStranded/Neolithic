#version 130

in vec3 inPosition;
in vec3 inNormal;
in vec2 inTextureCoordinates;

uniform mat4 modelLightViewMatrix;
uniform mat4 orthographicProjectionMatrix;

void main() {
    gl_Position = orthographicProjectionMatrix * modelLightViewMatrix * vec4(inPosition, 1.0);
}