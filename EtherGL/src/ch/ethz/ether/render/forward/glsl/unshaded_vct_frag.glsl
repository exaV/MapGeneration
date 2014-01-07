#version 150

uniform sampler2D tex;

uniform bool hasTex;

in vec4 vsColor;
in vec2 vsTexCoord;

out vec4 fragColor;

void main() {
	fragColor = hasTex ? vsColor * texture(tex, vsTexCoord) : vsColor;
}
