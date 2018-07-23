#version 130

in vec3 inPosition;
in vec3 inNormal;
in vec2 inTextureCoordinates;

out vec3 outPosition;
out vec3 outNormal;
out vec2 outTextureCoordinates;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;
uniform int dynamic; // 1 if the object may move, 0 if it is static

void main() {
    vec4 modelViewPosition = modelViewMatrix * vec4(inPosition, dynamic);
    gl_Position = projectionMatrix * modelViewPosition;

    outPosition = modelViewPosition.xyz;
    outNormal = normalize(modelViewMatrix * vec4(inNormal, 0.0)).xyz;
    outTextureCoordinates = inTextureCoordinates;
}