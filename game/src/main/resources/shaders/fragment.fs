#version 130

// ----------- constants

const int MAX_POINT_LIGHTS = 8;
const int MAX_SPOT_LIGHTS = 8;

// ----------- structures

struct Attenuation {
    float constant;
    float linear;
    float exponent;
};

struct DirectionalLight {
    vec4 color;
    // Light direction is assumed to be in view coordinates
    vec3 direction;
    float intensity;
};

struct PointLight {
    vec4 color;
    // Light position is assumed to be in view coordinates
    vec3 position;
    float intensity;
    Attenuation attenuation;
};

struct SpotLight {
    vec4 color;
    // Light position is assumed to be in view coordinates, as well as direction
    vec3 position;
    vec3 direction;
    float coneCosine;
    float intensity;
    Attenuation attenuation;
};

struct Material {
    vec4 ambient;
    vec4 diffuse;
    vec4 reflectance;
    float specularPower;
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
uniform int affectedByLight;

uniform Material material;

uniform vec4 ambientLight;
uniform DirectionalLight directionalLight;

uniform PointLight pointLight[MAX_POINT_LIGHTS];
uniform SpotLight spotLight[MAX_SPOT_LIGHTS];

// ----------- globals

vec4 ambientC;
vec4 diffuseC;
vec4 specularC;

// ----------- methods

// ----------------------------------------------------------------------------------------------- Set Up Colors

void setupColors(Material material, vec2 textureCoordinates) {
    if (material.hasTexture == 1) {
        ambientC = texture(textureSampler, textureCoordinates);
        diffuseC = ambientC;
        specularC = ambientC;
    } else {
        ambientC = material.ambient;
        diffuseC = material.diffuse;
        specularC = material.reflectance;
    }
}

// ----------------------------------------------------------------------------------------------- Calculate Light

vec4 calculateLight(vec3 lightPosition, vec4 lightColor, vec3 fromLightSource, vec3 position, vec3 normal) {
    vec4 diffuseColor = vec4(0, 0, 0, 0);
    vec4 specularColor = vec4(0, 0, 0, 0);

    // Diffuse Light
    vec3 toLightSource  = -fromLightSource;
    float diffuseFactor = max(dot(normal, toLightSource ), 0.0);
    diffuseColor = diffuseC * lightColor * diffuseFactor;

    // Specular Light
    vec3 cameraDirection = normalize(-position); // gives camera direction because camera always sits in position 0
    vec3 reflectedLight = normalize(reflect(fromLightSource, normal));
    float specularFactor = max(dot(cameraDirection, reflectedLight), 0.0);
    specularFactor = pow(specularFactor, material.specularPower);
    specularColor = specularC * lightColor * specularFactor * material.reflectance;

    return (diffuseColor + specularColor);
}

// ----------------------------------------------------------------------------------------------- Point Light

vec4 calculatePointLight(PointLight light, vec3 position, vec3 normal) {
    vec3 lightDirection = position - light.position;
    vec4 calculatedColor = calculateLight(light.position, light.color * light.intensity, normalize(lightDirection), position, normal);

    // Attenuation
    float distance = length(lightDirection);
    float attenuationInverse =
        light.attenuation.constant +
        light.attenuation.linear * distance +
        light.attenuation.exponent * distance * distance;

    return calculatedColor / attenuationInverse;
}

// ----------------------------------------------------------------------------------------------- Spot Light

vec4 calculateSpotLight(SpotLight light, vec3 position, vec3 normal) {
    vec4 calculatedColor = vec4(0, 0, 0, 0);

    vec3 lightDirection = position - light.position;
    vec3 fromLightSource = normalize(lightDirection);
    float angleCosine = dot(fromLightSource, light.direction);

    if (angleCosine > light.coneCosine) {
        calculatedColor = calculateLight(light.position, light.color * light.intensity, fromLightSource, position, normal);

        // radial strength of spot light
        calculatedColor = calculatedColor * (1.0 - (1.0-angleCosine) / (1.0-light.coneCosine));

        // Attenuation
        float distance = length(lightDirection);
        float attenuationInverse =
            light.attenuation.constant +
            light.attenuation.linear * distance +
            light.attenuation.exponent * distance * distance;

        calculatedColor = calculatedColor / attenuationInverse;
     }

    return calculatedColor;
}

// ----------------------------------------------------------------------------------------------- Directional Light

vec4 calculateDirectionalLight(DirectionalLight light, vec3 normal) {
    // Diffuse Light
    float diffuseFactor = max(dot(normal, -light.direction), 0.0);
    vec4 diffuseColor = diffuseC * light.color * light.intensity * diffuseFactor;

    return diffuseColor;
}

// ----------- main

void main() {
    setupColors(material, outTextureCoordinates);

    if (affectedByLight == 1) {
        vec4 pointLightColor = vec4(0, 0, 0, 0);

        for (int i=0; i<MAX_POINT_LIGHTS; i++) {
            if (pointLight[i].intensity > 0) {
                pointLightColor += calculatePointLight(pointLight[i], outPosition, outNormal);
            }
        }

        vec4 spotLightColor = vec4(0, 0, 0, 0);

        for (int i=0; i<MAX_SPOT_LIGHTS; i++) {
            if (spotLight[i].intensity > 0) {
                spotLightColor += calculateSpotLight(spotLight[i], outPosition, outNormal);
            }
        }

        vec4 directionalLightColor = calculateDirectionalLight(directionalLight, outNormal);

        fragmentColor = color * (ambientC * ambientLight + pointLightColor + spotLightColor + directionalLightColor);
    } else {
        fragmentColor = color * ambientC;
    }
}