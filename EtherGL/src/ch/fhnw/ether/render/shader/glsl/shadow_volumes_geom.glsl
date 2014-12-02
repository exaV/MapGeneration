#version 330

#define MAX_LIGHTS 8

#include <light_struct.glsl>
#include <light_block.glsl>


struct VertexData {
	vec4 position;				// vertex position in eye space
};

uniform mat4 projMatrix;

uniform int lightIndex;
uniform float extrudeDistance;

in VertexData vd[3];

layout(triangles) in;
layout (triangle_strip, max_vertices=14) out;

void main() {
	Light light = lights[lightIndex];

	vec4 t0 = vd[0].position;
	vec4 t1 = vd[1].position;
	vec4 t2 = vd[2].position;
	vec3 norm = cross((t1 - t0).xyz, (t2 - t0).xyz);

	vec4 u0;
	vec4 u1;
	vec4 u2;
	
	if (light.type == 1.0) {
		if (dot(light.position, norm) < 0)
			return;
		vec4 d = normalize(vec4(-light.position, 0));
		u0 = t0 + extrudeDistance * d;
		u1 = t1 + extrudeDistance * d;
		u2 = t2 + extrudeDistance * d;
	} else {
		if (dot(light.position - t0.xyz, norm) < 0)
			return;
		float r = light.range;
		vec4 lp = vec4(light.position, 1);
		vec4 d0 = t0 - lp;
		vec4 d1 = t1 - lp;
		vec4 d2 = t2 - lp;
		float l0 = length(d0);
		float l1 = length(d1);
		float l2 = length(d2);
		if (l0 > r && l1 > r && l2 > r)
			return;
		d0 /= l0;
		d1 /= l1;
		d2 /= l2;
		u0 = t0 + extrudeDistance * d0;
		u1 = t1 + extrudeDistance * d1;
		u2 = t2 + extrudeDistance * d2;
	}
	
	t0 = projMatrix * t0;
	t1 = projMatrix * t1;
	t2 = projMatrix * t2;
	u0 = projMatrix * u0;
	u1 = projMatrix * u1;
	u2 = projMatrix * u2;

	// volume with caps (triangle strip)

	// top
	gl_Position = t0;	EmitVertex();
	gl_Position = t1;	EmitVertex();
	gl_Position = t2;	EmitVertex();
	EndPrimitive();

	// bottom
	gl_Position = u0;	EmitVertex();
	gl_Position = u2;	EmitVertex();
	gl_Position = u1;	EmitVertex();
	EndPrimitive();

	// sides
	gl_Position = t0;	EmitVertex();
	gl_Position = u0;	EmitVertex();
	gl_Position = t1;	EmitVertex();
	gl_Position = u1;	EmitVertex();
	gl_Position = t2;	EmitVertex();
	gl_Position = u2;	EmitVertex();
	gl_Position = t0;	EmitVertex();
	gl_Position = u0;	EmitVertex();
	EndPrimitive();
}
