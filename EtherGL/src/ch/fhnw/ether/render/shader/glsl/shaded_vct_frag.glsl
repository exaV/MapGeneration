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
uniform bool lightIsLocal;
uniform bool lightIsSpot;
//uniform vec3 lightPosition; -- note that light position needs to be in eye space
uniform vec3 lightAmbientColor;
uniform vec3 lightColor;
uniform vec3 lightSpotDirection;
uniform float lightSpotCosCutoff;
uniform float lightSpotExponent;
uniform float lightConstantAttenuation; 
uniform float lightLinearAttenuation;
uniform float lightQuadraticAttenuation;

in vec4 vsPosition;
in vec3 vsNormal;
in vec4 vsDiffuseColor;
in vec2 vsTexCoord;
in vec3 vsLightPosition;

out vec4 fragColor;

void main() {

	vec3 lightDirection = vsLightPosition; 

	float attenuation = 1.0;

    // for local lights, compute per-fragment direction, and attenuation
	if (lightIsLocal) {
		lightDirection = lightDirection - vec3(vsPosition);
		float lightDistance = length(lightDirection); 
		lightDirection = lightDirection / lightDistance;
    	attenuation = 1.0 / (lightConstantAttenuation + lightLinearAttenuation * lightDistance + lightQuadraticAttenuation * lightDistance  * lightDistance);

		if (lightIsSpot) {
			float spotCos = dot(lightDirection, -lightSpotDirection); 
			if (spotCos < lightSpotCosCutoff)
            	attenuation = 0.0;
			else
				attenuation *= pow(spotCos, lightSpotExponent);
		}
	}
	
	float diffuseFactor = max(0.0, dot(vsNormal, lightDirection));
	float specularFactor = 0;

	if (diffuseFactor > 0) {
		vec3 halfVector = normalize(lightDirection + eyeDirection); 
		specularFactor = pow(max(0.0, dot(vsNormal, halfVector)), materialShininess) * materialStrength;
	}

	// accumulate all the lights' effects
	vec3 emittedLight = materialEmissionColor;
	vec3 scatteredLight = lightAmbientColor * attenuation + vsDiffuseColor.rgb * lightColor * diffuseFactor * attenuation;
    vec3 reflectedLight = materialSpecularColor * lightColor * specularFactor * attenuation;

	vec3 rgb = min(emittedLight + scatteredLight + reflectedLight, vec3(1.0));

	fragColor = vec4(rgb, vsDiffuseColor.a);

	//fragColor = useTexture ? vsDiffuseColor * texture(colorMap, vsTexCoord) : vsDiffuseColor;	
}
