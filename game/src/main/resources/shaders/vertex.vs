#version 450

in vec3 inPosition;
in vec2 inTextureCoordinates;
in vec4 inColor;
in vec3 inNormal;

out vec3 outPosition;
out vec2 outTextureCoordinates;
out vec4 outColor;
out vec3 outNormal;

out vec4 lightPosition;

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
    outColor = inColor;

    lightPosition = lightProjectionMatrix * modelLightViewMatrix * vec4(inPosition, 1.0);
}