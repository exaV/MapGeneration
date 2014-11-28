#version 330

struct VertexData {
	vec4 position;				// vertex position in eye space
};

uniform mat4 projMatrix;
uniform mat4 viewMatrix;

in vec4 vertexPosition;

out VertexData vd;

void main() {
	vd.position = viewMatrix * vertexPosition;
	gl_Position = projMatrix * viewMatrix * vertexPosition;
}
