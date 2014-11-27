#version 330

#define MAX_LIGHTS 8

struct Light {
	vec3 position;
	float pad0;
	vec3 ambientColor;
	float pad1;
	vec3 color;
	float pad2;
	vec3 spotDirection;
	float pad3;
	float spotCosCutoff;
	float spotExponent;
	float range;
	float type; // 0 = off, 1 = directional, 2 = point, 3 = spot
};

layout (std140) uniform lightBlock {
	Light lights[MAX_LIGHTS];
};

uniform int lightIndex;

layout(triangles) in;
layout (triangle_strip, max_vertices=3) out;

void main() {
	for(int i = 0; i < gl_in.length(); i++) {
		gl_Position = gl_in[i].gl_Position;
 
 		EmitVertex();
 	}
}
