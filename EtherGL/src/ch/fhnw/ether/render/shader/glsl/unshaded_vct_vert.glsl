#version 140

uniform mat4 projMatrix;
uniform mat4 viewMatrix;

uniform bool useVertexColors;
uniform bool useTexture;

uniform vec4 color;

in vec4 vertexPosition;
in vec4 vertexColor;
in vec2 vertexTexCoord;

uniform vec4 materialColor;

out vec4 vsColor;
out vec2 vsTexCoord;

void main() {
	vsColor = materialColor;
	if(useVertexColors)
		vsColor *= vertexColor;
		
	if (useTexture)
		vsTexCoord = vertexTexCoord;
	gl_Position = projMatrix * viewMatrix * vertexPosition;
}
