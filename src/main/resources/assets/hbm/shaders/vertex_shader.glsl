#version 120

varying vec3 vPosition;

void main() {
    vPosition = gl_Vertex.xyz;
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}