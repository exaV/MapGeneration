#ifndef LIGHT_STRUCT_GLSL
#define LIGHT_STRUCT_GLSL 1

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

#endif // LIGHT_STRUCT_GLSL
