#version 410

in vec4 a_position;	// vertex position as a homogenous vector in NDC 
in vec3 a_colour; // vertex colour RGB

uniform mat4 u_modelMatrix;

out vec3 v_colour; // to fragment shader

void main() {

	vec4 p = u_modelMatrix * a_position; // vertex to world coordinates

	v_colour = a_colour;

	// pad the vertex to a homogeneous 3D point
    gl_Position = vec4(p.xy, 0, 1);

}

