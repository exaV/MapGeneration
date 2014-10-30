#version 140

uniform mat4 projMatrix;
uniform mat4 viewMatrix;

uniform bool useVertexColors;

uniform vec4 materialColor;
uniform float pointSize;

in vec4 vertexPosition;
in vec4 vertexColor;

out vec4 vsColor;

void main() {
	vsColor = materialColor;
	if (useVertexColors)
		vsColor *= vertexColor;

	gl_PointSize = pointSize;
	gl_Position = projMatrix * viewMatrix * vertexPosition;
}
