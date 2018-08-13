#version 130

in vec3 inPosition;
in vec3 inNormal;
in vec2 inTextureCoordinates;

out vec3 outNormal;

uniform mat4 modelLightViewMatrix;
uniform mat4 orthographicProjectionMatrix;

void main() {
    outNormal = (modelLightViewMatrix * vec4(inNormal, 0.0)).xyz;
    gl_Position = orthographicProjectionMatrix * modelLightViewMatrix * vec4(inPosition, 1.0);
}