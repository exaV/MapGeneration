#version 330

struct Light {
	bool isLocal;
	bool isSpot;
	vec4 position;
	vec3 ambientColor;
	vec3 color;
	vec3 spotDirection;
	float spotCosCutoff;
	float spotExponent;
	float constantAttenuation; 
	float linearAttenuation;
	float quadraticAttenuation;
};

struct VertexData {
	vec4 position;				// vertex position in eye space
	vec3 normal;				// vertex normal in eye space
	vec4 color;					// vertex diffuse color
	vec2 texCoord;				// texture coordinate of color map

	vec3 lightPosition;			// hack until we transform light pos/dir on cpu
	vec3 lightSpotDirection;
};


uniform mat4 projMatrix;
uniform mat4 viewMatrix;
uniform mat3 normalMatrix;

uniform bool useVertexColors;
uniform bool useColorMap;

uniform Light light;

in vec4 vertexPosition;
in vec4 vertexNormal;
in vec4 vertexColor;
in vec2 vertexTexCoord;

out VertexData vd;

void main() {
	vd.position = viewMatrix * vertexPosition;
	vd.normal = normalize(normalMatrix * vertexNormal.xyz);
	vd.color = useVertexColors ? vertexColor : vec4(1);

	if (useColorMap)
		vd.texCoord = vertexTexCoord;

	vd.lightPosition = (viewMatrix * light.position).xyz;
	vd.lightSpotDirection = normalMatrix * light.spotDirection;

	gl_Position = projMatrix * viewMatrix * vertexPosition;
}
