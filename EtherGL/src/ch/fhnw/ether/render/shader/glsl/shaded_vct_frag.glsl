#version 330

#define MAX_LIGHTS 8

#define DIFFUSE_LAMBERT 1

//#define SPECULAR_BLINN_PHONG 1
#define SPECULAR_PHONG 1

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

struct Material {
	vec3 emissionColor;
	vec3 ambientColor;
	vec3 diffuseColor;
	vec3 specularColor;
	float shininess;
	float strength;
	float alpha;
};

struct VertexData {
	vec4 position;				// vertex position in eye space
	vec3 normal;				// vertex normal in eye space
	vec4 color;					// vertex diffuse color
	vec2 texCoord;				// texture coordinate of color map
};


uniform sampler2D colorMap;
uniform bool useColorMap;

layout (std140) uniform lightBlock {
	Light lights[MAX_LIGHTS];
};

uniform Material material;

in VertexData vd;

out vec4 fragColor;

// lambert diffuse factor

#ifdef DIFFUSE_LAMBERT

float calculateDiffuseFactor(vec3 normal, vec3 lightDirection) {
	return max(0.0, dot(normal, lightDirection));
}

#endif

// blinn-phong specular factor

#ifdef SPECULAR_BLINN_PHONG

float calculateSpecularFactor(vec3 position, vec3 normal, vec3 lightDirection, float shininess, float strength) {
	vec3 eyeDirection = -normalize(position);
	vec3 halfVector = normalize(lightDirection + eyeDirection); 
	return pow(max(0.0, dot(normal, halfVector)), shininess) * strength;
}

#endif

#ifdef SPECULAR_PHONG

float calculateSpecularFactor(vec3 position, vec3 normal, vec3 lightDirection, float shininess, float strength) {
	vec3 eyeDirection = -normalize(position);
	vec3 reflectionVector = reflect(-lightDirection, normal);
	return pow(max(0.0, dot(reflectionVector, eyeDirection)), shininess) * strength;
}

#endif

// generic main function

void main() {
	vec3 emittedLight = material.emissionColor;
	vec3 scatteredLight = vec3(0);
    vec3 reflectedLight = vec3(0);

	for (int i = 0; i < MAX_LIGHTS; ++i) {
		if (lights[i].type == 0.0)
			continue;
	
		vec3 lightDirection;
		float attenuation;
	    // for local lights, compute per-fragment direction, and attenuation
		if (lights[i].type > 1.0) {
			lightDirection = -(vd.position.xyz - lights[i].position);
			float lightDistance = length(lightDirection);
			
			lightDirection = lightDirection / lightDistance;
			attenuation = 1 - smoothstep(0, lights[i].range, lightDistance);
	
			if (lights[i].type > 2.0) {
				float spotCos = dot(lightDirection, -normalize(lights[i].spotDirection)); 
				if (spotCos < lights[i].spotCosCutoff)
	            	attenuation = 0.0;
				else
					attenuation *= pow(spotCos, lights[i].spotExponent);
			}
		} else {
			lightDirection = lights[i].position;
			attenuation = 1.0;
		}

		vec3 normal = normalize(vd.normal);
		float diffuseFactor = calculateDiffuseFactor(normal, lightDirection);
		float specularFactor = calculateSpecularFactor(vd.position.xyz, normal, lightDirection, material.shininess, material.strength);

		scatteredLight += material.ambientColor * lights[i].ambientColor * attenuation + material.diffuseColor * lights[i].color * diffuseFactor * attenuation;
		reflectedLight += material.specularColor * lights[i].color * specularFactor * attenuation;
	}

	float alpha = material.alpha;

	if (useColorMap) {
		vec4 t = texture(colorMap, vd.texCoord);
		scatteredLight *= t.rgb;
		alpha *= t.a;
	}
	vec4 rgba = vec4(min(emittedLight + scatteredLight + reflectedLight, vec3(1.0)), alpha);

	fragColor = rgba;
}
