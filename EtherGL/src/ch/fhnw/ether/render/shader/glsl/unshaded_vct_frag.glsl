#version 140

uniform sampler2D tex;

uniform bool useTexture;

in vec4 vsColor;
in vec2 vsTexCoord;

out vec4 fragColor;

void main() {
	fragColor = useTexture ? vsColor * texture(tex, vsTexCoord) : vsColor;
}
