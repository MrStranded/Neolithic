#version 130

// ----------- structures

struct Attenuation
{
    float constant;
    float linear;
    float exponent;
};

struct PointLight
{
    vec4 color;
    // Light position is assumed to be in view coordinates
    vec3 position;
    float intensity;
    Attenuation attenuation;
};

struct Material
{
    vec4 ambient;
    vec4 diffuse;
    int hasTexture;
};

// ----------- in / out

in vec3 outPosition;
in vec3 outNormal;
in vec2 outTextureCoordinates;

out vec4 fragmentColor;

// ----------- uniforms

uniform sampler2D textureSampler;
uniform vec4 color;

uniform vec4 ambientLight;

uniform Material material;

uniform PointLight pointLight;

// ----------- globals

vec4 ambientC;
vec4 diffuseC;

// ----------- methods

void setupColors(Material material, vec2 textureCoordinates) {
    if (material.hasTexture == 1) {
        ambientC = texture(textureSampler, textureCoordinates);
        diffuseC = ambientC;
    } else {
        ambientC = material.ambient;
        diffuseC = material.diffuse;
    }
}

vec4 calculatePointLight(PointLight light, vec3 position, vec3 normal) {
    vec4 diffuseColor = vec4(0, 0, 0, 0);

    // Diffuse Light
    vec3 lightDirection = light.position - position;
    vec3 toLightSource  = normalize(lightDirection);
    float diffuseFactor = max(dot(normal, toLightSource ), 0.0);
    diffuseColor = diffuseC * light.color * light.intensity * diffuseFactor;

    // Attenuation
    float distance = length(lightDirection);
    float attenuationInverse =
        light.attenuation.constant +
        light.attenuation.linear * distance +
        light.attenuation.exponent * distance * distance;

    return diffuseColor / attenuationInverse;
}

// ----------- main

void main() {
    setupColors(material, outTextureCoordinates);

    vec4 diffuseSpecularComposition = calculatePointLight(pointLight, outPosition, outNormal);

    fragmentColor = color * (ambientC * ambientLight + diffuseSpecularComposition);
}