#version 140

uniform mat4 projMatrix;
uniform mat4 viewMatrix;

uniform bool hasColor;

uniform vec4 color;
uniform float pointSize;
uniform float pointDecay;

in vec4 vertexPosition;
in vec4 vertexColor;

out vec4 vsColor;

void main() {
	vsColor = hasColor ? vertexColor : color;
	vec4 pos = projMatrix * viewMatrix * vertexPosition;
	gl_PointSize = pointSize * (1.0 - pointDecay * pos.z / pos.w);
	gl_Position = pos;
}
