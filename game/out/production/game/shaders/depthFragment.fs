#version 130

in vec3 outNormal;

out vec4 fragmentColor;

void main() {
    //gl_FragDepth = gl_FragCoord.z;
    fragmentColor = vec4(gl_FragCoord.z, 0.0, dot(outNormal, vec3(0.0, 0.0, 1.0)), 1.0);
}