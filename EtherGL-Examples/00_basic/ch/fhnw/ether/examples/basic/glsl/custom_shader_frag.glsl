#version 140

in vec4 vsColor;

out vec4 fragColor;

void main() {
	fragColor = vec4(vsColor.r, 0, 0, 1);
}
