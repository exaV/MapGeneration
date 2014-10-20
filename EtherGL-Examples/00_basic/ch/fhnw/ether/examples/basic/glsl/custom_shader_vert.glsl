#version 140

uniform mat4 projMatrix;
uniform mat4 viewMatrix;

in vec4 vertexPosition;
in vec4 vertexColor;

out vec4 vsColor;

void main() {
	vsColor = vertexColor;
	gl_Position = projMatrix * viewMatrix * vertexPosition;
}
