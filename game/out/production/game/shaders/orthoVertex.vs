#version 130

in vec3 inPosition;
in vec3 inNormal;
in vec2 inTextureCoordinates;
in vec4 inColor;

out vec2 outTextureCoordinates;
out vec4 outColor;

uniform mat4 projectionViewMatrix;

void main() {
    gl_Position = projectionViewMatrix * vec4(inPosition, 1.0);

    outColor = inColor;

    // So this is very interesting: if we do not 'use' inNormal here, the gpu seems to think it does not need the
    // inNormal value, thus disabling the vertex attribute and apparantely also the following attribute, which happens
    // to be the texture coordinates.
    // Consequently we need to 'use' the inNormal value to trick the gpu into thinking the value is needed, such that
    // we may use the texture coordinates.
    outTextureCoordinates = inTextureCoordinates + inNormal.xy - inNormal.xy;
}