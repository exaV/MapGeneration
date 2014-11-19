#version 330

uniform vec3 eyeDirection;

uniform sampler2D colorMap;

uniform bool useTexture;

// material parameters
uniform vec3 materialEmissionColor;
uniform vec3 materialSpecularColor;
uniform float materialShininess;
uniform float materialStrength;

// light parameters (one light only at this point)
// note that light position and light spot direction and passed from vertex shader
// XXX better be precalculated
uniform bool lightIsLocal;
uniform bool lightIsSpot;
//uniform vec3 lightPosition;
uniform vec3 lightAmbientColor;
uniform vec3 lightColor;
//uniform vec3 lightSpotDirection;
uniform float lightSpotCosCutoff;
uniform float lightSpotExponent;
uniform float lightConstantAttenuation; 
uniform float lightLinearAttenuation;
uniform float lightQuadraticAttenuation;

in vec4 vsPosition;			// vertex position in eye space
in vec3 vsNormal;			// vertex normal in eye space
in vec4 vsDiffuseColor;		// vertex diffuse color
in vec2 vsTexCoord;			// texture coordinate of color map

in vec3 vsLightPosition;
in vec3 vsLightSpotDirection;

out vec4 fragColor;

void main() {
	vec3 lightDirection;
	float attenuation;

    // for local lights, compute per-fragment direction, and attenuation
	if (lightIsLocal) {
		lightDirection = -(vsPosition.xyz - vsLightPosition);
		float lightDistance = length(lightDirection);
		
		lightDirection = lightDirection / lightDistance;
    	attenuation = 1.0 / (lightConstantAttenuation + lightLinearAttenuation * lightDistance + lightQuadraticAttenuation * lightDistance  * lightDistance);

		if (lightIsSpot) {
			float spotCos = dot(lightDirection, -normalize(vsLightSpotDirection)); 
			if (spotCos < lightSpotCosCutoff)
            	attenuation = 0.0;
			else
				attenuation *= pow(spotCos, lightSpotExponent);
		}
	} else {
		lightDirection = vsLightPosition;
		attenuation = 1.0;
	}
	
	vec3 normal = normalize(vsNormal);
	float diffuseFactor = max(0.0, dot(normal, lightDirection));
	float specularFactor = 0;

	if (diffuseFactor > 0) {
		vec3 eyeDirection = -normalize(vsPosition.xyz);
		vec3 halfVector = normalize(lightDirection + eyeDirection); 
		specularFactor = pow(max(0.0, dot(normal, halfVector)), materialShininess) * materialStrength;
	}

	// accumulate all the lights' effects
	vec3 emittedLight = materialEmissionColor;
	vec3 scatteredLight = lightAmbientColor * attenuation + vsDiffuseColor.rgb * lightColor * diffuseFactor * attenuation;
    vec3 reflectedLight = materialSpecularColor * lightColor * specularFactor * attenuation;

	vec4 rgba = vec4(min(emittedLight + scatteredLight + reflectedLight, vec3(1.0)), vsDiffuseColor.a);

	fragColor = useTexture ? rgba * texture(colorMap, vsTexCoord) : rgba;
}
