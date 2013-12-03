#version 150

uniform sampler2D texture;

uniform bool hasTexture;

in vec4 vsColor;
in vec2 vsTexCoord;

out vec4 fragmentColor;

void main() {
	fragmentColor = hasTexture ? vsColor * texture(texture, vsTexCoord) : vsColor;
}
