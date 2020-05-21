#version 450

in vec2 outTextureCoordinates;
in vec4 outColor;

out vec4 fragmentColor;

uniform sampler2D textureSampler;
//uniform vec4 color;

void main() {
    vec4 textureColor = texture(textureSampler, outTextureCoordinates);
    if (textureColor.w == 0.0) {
        discard;
        //fragmentColor = vec4(outTextureCoordinates, 0.0, 1.0);
    } else {
        fragmentColor = outColor * textureColor;
    }
}