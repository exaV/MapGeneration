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

uniform mat4 projMatrix;
uniform mat4 viewMatrix;

layout (std140) uniform lightBlock {
	Light lights[MAX_LIGHTS];
};

uniform int lightIndex;

in vec4 vertexPosition;

void main() {
	gl_Position = projMatrix * viewMatrix * vertexPosition;
}
