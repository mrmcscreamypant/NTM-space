
#version 120
#extension GL_EXT_gpu_shader4 : require

uniform float iTime;

varying vec3 vPosition;

float hash(float x){ return fract(cos(x*124.123)*412.0); }
float hash(int x){ return fract(cos(x*124.123)*412.0); }

void main() {
    vPosition = gl_Vertex.xyz;
    float t = gl_VertexID + iTime;
    float r = hash(gl_VertexID);
    float y = cos(t) * r + sin(t) * (1-r) * 0.5; // wrong, please fix me
    gl_Position = gl_ModelViewProjectionMatrix * (gl_Vertex + vec4(0.0, y, 0.0, 0.0));
    gl_TexCoord[0] = gl_MultiTexCoord0;
}