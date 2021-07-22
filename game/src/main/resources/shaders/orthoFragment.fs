#version 450

in vec2 outTextureCoordinates;
in vec4 outColor;

out vec4 fragmentColor;

uniform sampler2D textureSampler;
uniform float horizontalStep;
uniform float verticalStep;
//uniform vec4 color;

void main() {
    vec4 textureColor = texture(textureSampler, outTextureCoordinates);

    vec4 top = texture(textureSampler, outTextureCoordinates + vec2(0.0, -verticalStep));
    vec4 right = texture(textureSampler, outTextureCoordinates + vec2(horizontalStep, 0.0));
    vec4 bottom = texture(textureSampler, outTextureCoordinates + vec2(0.0, verticalStep));
    vec4 left = texture(textureSampler, outTextureCoordinates + vec2(-horizontalStep, 0.0));

    // textureColor.w is the alpha value of the texture
    if (textureColor.w == 0.0) {
        discard;
    } else {
        fragmentColor = outColor * (textureColor + top + right + bottom + left) / 5.0;
    }
}