#version 130

in vec2 outTextureCoordinates;

out vec4 fragmentColor;

uniform sampler2D textureSampler;
uniform vec4 color;

void main() {
    vec4 textureColor = texture(textureSampler, outTextureCoordinates);
    if (textureColor.w == 0.0) {
        discard;
        //fragmentColor = vec4(outTextureCoordinates, 0.0, 1.0);
    } else {
        fragmentColor = color * textureColor;
    }
}