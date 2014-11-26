#version 330

uniform mat4 projMatrix;
uniform mat4 viewMatrix;

in vec4 vertexPosition;

void main() {
	gl_Position = projMatrix * viewMatrix * vertexPosition;
}
