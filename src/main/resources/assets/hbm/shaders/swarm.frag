#version 120

uniform float iTime;
uniform sampler2D iChannel1;
uniform float iOffset; // swarm member count

#define PI 3.1415926538

float DistLine(vec3 ro, vec3 rd, vec3 p) {
	return length(cross(p-ro, rd))/length(rd);
}

// shitey SDF as a quick concept
void main() {
	vec2 uv = gl_TexCoord[0].xy;
    uv -= .5;

    vec3 ro = vec3(0., .5, -2.);
    vec3 rd = vec3(uv.x, uv.y, 0.)-ro;

    float t = iTime*4.;
    vec3 p =.5*vec3(sin(t), 0., .5+cos(t));
    vec3 q =.5*vec3(sin(t+PI), 0., .5+cos(t+PI));
    float d = DistLine(ro, rd, p);
    float e = DistLine(ro, rd, q);
    
    d = pow(.05/(d+0.), 1.);
    e = pow(.05/(e+0.), 1.);

	gl_FragColor = vec4(d*vec3(.8,.3,1.)+e*vec3(.3,.8,1.),1.);
}
