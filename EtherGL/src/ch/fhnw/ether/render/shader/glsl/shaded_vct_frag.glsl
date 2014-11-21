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
	bool isLocal;
	bool isSpot;
	vec4 position;
	vec3 ambientColor;
	vec3 color;
	vec3 spotDirection;
	float spotCosCutoff;
	float spotExponent;
	float constantAttenuation; 
	float linearAttenuation;
	float quadraticAttenuation;
};

struct VertexData {
	vec4 position;				// vertex position in eye space
	vec3 normal;				// vertex normal in eye space
	vec4 color;					// vertex diffuse color
	vec2 texCoord;				// texture coordinate of color map

	vec3 lightPosition;			// hack until we transform light pos/dir on cpu
	vec3 lightSpotDirection;
};


uniform sampler2D colorMap;
uniform bool useColorMap;

uniform Light light;

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
	if (light.isLocal) {
		lightDirection = -(vd.position.xyz - vd.lightPosition);
		float lightDistance = length(lightDirection);
		
		lightDirection = lightDirection / lightDistance;
    	attenuation = 1.0 / (light.constantAttenuation + light.linearAttenuation * lightDistance + light.quadraticAttenuation * lightDistance  * lightDistance);

		if (light.isSpot) {
			float spotCos = dot(lightDirection, -normalize(vd.lightSpotDirection)); 
			if (spotCos < light.spotCosCutoff)
            	attenuation = 0.0;
			else
				attenuation *= pow(spotCos, light.spotExponent);
		}
	} else {
		lightDirection = vd.lightPosition;
		attenuation = 1.0;
	}

	vec3 normal = normalize(vd.normal);
	float diffuseFactor = calculateDiffuseFactor(normal, lightDirection);
	float specularFactor = calculateSpecularFactor(vd.position.xyz, normal, lightDirection, material.shininess, material.strength);

	// accumulate all the lights' effects
	vec3 diffuseColor = material.diffuseColor * vd.color.rgb;

	vec3 emittedLight = material.emissionColor;
	vec3 scatteredLight = material.ambientColor * light.ambientColor * attenuation + diffuseColor * light.color * diffuseFactor * attenuation;
    vec3 reflectedLight = material.specularColor * light.color * specularFactor * attenuation;

	vec4 rgba = vec4(min(emittedLight + scatteredLight + reflectedLight, vec3(1.0)), material.alpha);

	fragColor = useColorMap ? rgba * texture(colorMap, vd.texCoord) : rgba;
}
