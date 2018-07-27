#version 130

in vec2 outTextureCoordinates;

out vec4 fragmentColor;

uniform sampler2D textureSampler;
uniform vec4 color;

void main() {
    fragmentColor = color - color + vec4(outTextureCoordinates, 1.0, 0.0) + texture(textureSampler, outTextureCoordinates) - texture(textureSampler, outTextureCoordinates);
}