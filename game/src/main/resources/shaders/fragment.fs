#version 130

in vec3 outColor;
in vec2 outTextureCoordinates;

out vec4 fragmentColor;

uniform sampler2D textureSampler;

void main(){
    fragmentColor = vec4(outColor, 1.0) * texture(textureSampler, outTextureCoordinates);
}