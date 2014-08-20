#version 140

uniform mat4 projMatrix;
uniform mat4 viewMatrix;

uniform bool hasColor;
uniform bool hasTex;

uniform vec4 color;

in vec4 vertexPosition;
in vec4 vertexColor;
in vec2 vertexTexCoord;

out vec4 vsColor;
out vec2 vsTexCoord;

void main() {
	vsColor = hasColor ? vertexColor : color;
	if (hasTex)
		vsTexCoord = vertexTexCoord;
	gl_Position = projMatrix * viewMatrix * vertexPosition;
}
