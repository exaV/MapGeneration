#version 330

struct Material {
	vec3 emissionColor;
	vec3 ambientColor;
	vec3 diffuseColor;
	vec3 specularColor;
	float shininess;
	float strength;
	float alpha;
};

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
	float constantAttenuation; 
	float linearAttenuation;
	float quadraticAttenuation;
	float type; // 0 = off, 1 = directional, 2 = point, 3 = spot
};

struct VertexData {
	vec4 position;				// vertex position in eye space
	vec3 normal;				// vertex normal in eye space
	vec4 color;					// vertex diffuse color
	vec2 texCoord;				// texture coordinate of color map
};


uniform sampler2D colorMap;
uniform bool useColorMap;

//uniform Light light;
layout (std140) uniform lightBlock {
	Light light;
};

uniform Material material;

in VertexData vd;

out vec4 fragColor;

// lambert diffuse factor

float calculateDiffuseFactor(vec3 normal, vec3 lightDirection) {
	return max(0.0, dot(normal, lightDirection));
}

// blinn-phong specular factor

float calculateSpecularFactor(vec3 position, vec3 normal, vec3 lightDirection, float shininess, float strength) {
	vec3 eyeDirection = -normalize(position);
	vec3 halfVector = normalize(lightDirection + eyeDirection); 
	return pow(max(0.0, dot(normal, halfVector)), shininess) * strength;
}

// generic main function

void main() {
	vec3 lightDirection;
	float attenuation;

    // for local lights, compute per-fragment direction, and attenuation
	if (light.type > 1.0) {
		lightDirection = -(vd.position.xyz - light.position);
		float lightDistance = length(lightDirection);
		
		lightDirection = lightDirection / lightDistance;
    	attenuation = 1.0 / (light.constantAttenuation + light.linearAttenuation * lightDistance + light.quadraticAttenuation * lightDistance  * lightDistance);

		if (light.type > 2.0) {
			float spotCos = dot(lightDirection, -normalize(light.spotDirection)); 
			if (spotCos < light.spotCosCutoff)
            	attenuation = 0.0;
			else
				attenuation *= pow(spotCos, light.spotExponent);
		}
	} else {
		lightDirection = light.position;
		attenuation = 1.0;
	}

	vec3 normal = normalize(vd.normal);
	float diffuseFactor = calculateDiffuseFactor(normal, lightDirection);
	float specularFactor = calculateSpecularFactor(vd.position.xyz, normal, lightDirection, material.shininess, material.strength);

	// accumulate all the lights' effects
	vec3 diffuseColor = material.diffuseColor * vd.color.rgb;
	float alpha = material.alpha;
	
	if (useColorMap) {
		vec4 t = texture(colorMap, vd.texCoord);
		diffuseColor += t.rgb;
		alpha *= t.a;
	}

	vec3 emittedLight = material.emissionColor;
	vec3 scatteredLight = material.ambientColor * light.ambientColor * attenuation + diffuseColor * light.color * diffuseFactor * attenuation;
    vec3 reflectedLight = material.specularColor * light.color * specularFactor * attenuation;

	vec4 rgba = vec4(min(emittedLight + scatteredLight + reflectedLight, vec3(1.0)), alpha);

	fragColor = rgba;
}
