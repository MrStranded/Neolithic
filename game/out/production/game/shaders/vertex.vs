#version 130

in vec3 inPosition;
in vec3 inNormal;
in vec2 inTextureCoordinates;

out vec3 outPosition;
out vec3 outNormal;
out vec2 outTextureCoordinates;

out vec4 lightPosition;
out vec3 lightNormal;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;
uniform int dynamic; // 1 if the object may move, 0 if it is static

uniform mat4 modelLightViewMatrix;
uniform mat4 lightProjectionMatrix;

void main() {
    outPosition = (modelViewMatrix * vec4(inPosition, dynamic)).xyz;
    gl_Position = projectionMatrix * vec4(outPosition, 1.0);

    outNormal = normalize(modelViewMatrix * vec4(inNormal, 0.0)).xyz;
    outTextureCoordinates = inTextureCoordinates;

    lightPosition = lightProjectionMatrix * modelLightViewMatrix * vec4(inPosition, 1.0);
    lightNormal = (lightProjectionMatrix * modelLightViewMatrix * vec4(inNormal, 0.0)).xyz;
}