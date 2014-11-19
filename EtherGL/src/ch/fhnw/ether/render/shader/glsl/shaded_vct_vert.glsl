#version 330

uniform mat4 projMatrix;
uniform mat4 viewMatrix;
uniform mat3 normalMatrix;

uniform bool useVertexColors;
uniform bool useTexture;

uniform vec4 materialDiffuseColor;

uniform vec4 lightPosition;
uniform vec3 lightSpotDirection;

in vec4 vertexPosition;
in vec4 vertexNormal;
in vec4 vertexColor;
in vec2 vertexTexCoord;

out vec4 vsPosition;		// vertex position in eye space
out vec3 vsNormal;			// vertex normal in eye space
out vec4 vsDiffuseColor;	// vertex diffuse color
out vec2 vsTexCoord;		// texture coordinate of color map

// light position and spot direction in eye space 
// XXX this can be precomputed and passed as uniform
out vec3 vsLightPosition;	
out vec3 vsLightSpotDirection;

void main() {
	vsPosition = viewMatrix * vertexPosition;

	vsNormal = normalize(normalMatrix * vertexNormal.xyz);

	vsDiffuseColor = materialDiffuseColor;
	if (useVertexColors)
		vsDiffuseColor *= vertexColor;

	if (useTexture)
		vsTexCoord = vertexTexCoord;

	vsLightPosition = (viewMatrix * lightPosition).xyz;
	vsLightSpotDirection = normalMatrix * lightSpotDirection;

	gl_Position = projMatrix * viewMatrix * vertexPosition;
}
