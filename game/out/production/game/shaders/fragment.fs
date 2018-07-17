#version 130

in vec2 outTextureCoordinates;

out vec4 fragmentColor;

uniform sampler2D textureSampler;
uniform vec4 color;
uniform int colorOnly;

void main(){
    if (colorOnly == 1) {
        fragmentColor = color;
    } else {
        fragmentColor = color * texture(textureSampler, outTextureCoordinates);
    }
}