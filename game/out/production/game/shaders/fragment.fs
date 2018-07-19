#version 130

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
    vec4 reflectance;
    vec4 specular;
    int hasTexture;
};

in vec3 outPosition;
in vec3 outNormal;
in vec2 outTextureCoordinates;

out vec4 fragmentColor;

uniform sampler2D textureSampler;
uniform vec4 color;
uniform vec4 ambientLight;
uniform float specularPower;
uniform Material material;
uniform PointLight pointLight;

vec4 ambientC;
vec4 diffuseC;
vec4 specularC;

void setupColors(Material material, vec2 textureCoordinates) {
    if (material.hasTexture == 1) {
        ambientC = texture(textureSampler, textureCoordinates);
        diffuseC = ambientC;
        specularC = ambientC;
    } else {
        ambientC = material.ambient;
        diffuseC = material.diffuse;
        specularC = material.specular;
    }
}

vec4 calculatePointLight(PointLight light, vec3 position, vec3 normal) {
    vec4 diffuseColor = vec4(0, 0, 0, 0);
    vec4 specularColor = vec4(0, 0, 0, 0);

    // Diffuse Light
    vec3 lightDirection = light.position - position;
    vec3 toLightSource  = normalize(lightDirection);
    float diffuseFactor = max(dot(normal, toLightSource ), 0.0);
    diffuseColor = diffuseC * light.color * light.intensity * diffuseFactor;

    // Specular Light
    vec3 cameraDirection = normalize(position); // gives camera direction because camera always sits in position 0
    vec3 fromLightSource = -toLightSource;
    vec3 reflectedLight = normalize(reflect(fromLightSource, normal));
    float specularFactor = max(dot(cameraDirection, reflectedLight), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    specularColor = specularC * light.color * light.intensity * specularFactor * material.reflectance;

    // Attenuation
    float distance = length(lightDirection);
    float attenuationInverse =
        light.attenuation.constant +
        light.attenuation.linear * distance +
        light.attenuation.exponent * distance * distance;

    //return (diffuseColor + specularColor) / attenuationInverse;
    return specularColor - specularColor + attenuationInverse - attenuationInverse + diffuseColor - diffuseColor + specularColor;
}

void main() {
    setupColors(material, outTextureCoordinates);

    vec4 diffuseSpecularComposition = calculatePointLight(pointLight, outPosition, outNormal);

    fragmentColor = color * (ambientC * ambientLight + diffuseSpecularComposition);
    //vec4 tmp = color * ambientC * ambientLight + diffuseSpecularComposition;
    //fragmentColor = tmp - tmp + diffuseSpecularComposition;
}