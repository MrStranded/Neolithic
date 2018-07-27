#version 130

in vec2 outTextureCoordinates;

out vec4 fragmentColor;

uniform sampler2D textureSampler;
uniform vec4 color;

void main() {
    fragmentColor = color * texture(textureSampler, outTextureCoordinates);
}