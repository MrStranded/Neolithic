#version 450

in vec2 outTextureCoordinates;
in vec4 outColor;

out vec4 fragmentColor;

uniform sampler2D textureSampler;
//uniform vec4 color;

void main() {
    vec4 textureColor = texture(textureSampler, outTextureCoordinates);

    // textureColor.w is the alpha value of the texture
    if (textureColor.w == 0.0) {
        discard;
    } else {
        fragmentColor = outColor * textureColor;
    }
}