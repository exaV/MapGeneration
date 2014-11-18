#version 330

uniform mat4 projMatrix;
uniform mat4 viewMatrix;
uniform mat4 normalMatrix;

uniform bool useVertexColors;
uniform bool useTexture;

uniform vec4 materialDiffuseColor;

uniform vec3 lightPosition;

in vec4 vertexPosition;
in vec4 vertexNormal;
in vec4 vertexColor;
in vec2 vertexTexCoord;

out vec4 vsPosition;
out vec3 vsNormal;
out vec4 vsDiffuseColor;
out vec2 vsTexCoord;
out vec3 vsLightPosition;

void main() {
	vsPosition = viewMatrix * vertexPosition;

	vsNormal = normalize(mat3(normalMatrix) * vertexNormal.xyz);

	vsDiffuseColor = materialDiffuseColor;
	if (useVertexColors)
		vsDiffuseColor *= vertexColor;

	if (useTexture)
		vsTexCoord = vertexTexCoord;

	vsLightPosition = mat3(viewMatrix) * lightPosition;

	gl_Position = projMatrix * viewMatrix * vertexPosition;
}
