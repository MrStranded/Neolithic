#version 450

in vec2 outTextureCoordinates;
in vec4 outColor;

out vec4 fragmentColor;

uniform sampler2D textureSampler;
uniform float horizontalStep;
uniform float verticalStep;
//uniform vec4 color;

void main() {
    vec4 blur = texture(textureSampler, outTextureCoordinates) * 2.0;
    for (int x=-1; x<=1; x=x+2) {
        for (int y=-1; y<=1; y=y+2) {
            blur = blur + texture(textureSampler, outTextureCoordinates + vec2(x * horizontalStep, y * verticalStep));
        }
    }
    blur = blur / 6.0;

    // w is the alpha value of the texture
    if (blur.w == 0.0) {
        discard;
    } else {
        fragmentColor = outColor * blur;
    }
}