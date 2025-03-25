package comp3170.week3;

import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL15.glBindBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import comp3170.GLBuffers;
import comp3170.Shader;
import comp3170.ShaderLibrary;

public class Scene {

	final private String VERTEX_SHADER = "vertex.glsl";
	final private String FRAGMENT_SHADER = "fragment.glsl";

	private Vector4f[] vertices;
	private int vertexBuffer;
	private int[] indices;
	private int indexBuffer;
	private Vector3f[] colours;
	private int colourBuffer;
	
	private Matrix4f modelMatrix;
	private Matrix4f translationMatrix;
	private Matrix4f rotationMatrix;
	private Matrix4f scaleMatrix;
	private long oldTime;

	private Shader shader;

	public Scene() {
		
		modelMatrix = new Matrix4f();
		translationMatrix = new Matrix4f();
		rotationMatrix = new Matrix4f();
		scaleMatrix = new Matrix4f();
		
		oldTime = System.nanoTime();

		shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);

		// @formatter:off
			//          (0,1)
			//           /|\
			//          / | \
			//         /  |  \
			//        / (0,0) \
			//       /   / \   \
			//      /  /     \  \
			//     / /         \ \		
			//    //             \\
			//(-1,-1)           (1,-1)
			//
	 		
		vertices = new Vector4f[] {
			new Vector4f( 0, 0, 0, 1),
			new Vector4f( 0, 1, 0, 1),
			new Vector4f(-1,-1, 0, 1),
			new Vector4f( 1,-1, 0, 1),
		};
			
			// @formatter:on
		vertexBuffer = GLBuffers.createBuffer(vertices);

		// @formatter:off
		colours = new Vector3f[] {
			new Vector3f(1,0,1),	// MAGENTA
			new Vector3f(1,0,1),	// MAGENTA
			new Vector3f(1,0,0),	// RED
			new Vector3f(0,0,1),	// BLUE
		};
			// @formatter:on

		colourBuffer = GLBuffers.createBuffer(colours);

		// @formatter:off
		indices = new int[] {  
			0, 1, 2, // left triangle
			0, 1, 3, // right triangle
			};
			// @formatter:on

		indexBuffer = GLBuffers.createIndexBuffer(indices);
		
//		rotationMatrix((float) (-Math.PI/2), rotationMatrix);
		
//		scaleMatrix(.5f ,.5f, scaleMatrix);
//		translationMatrix(.5f, -.5f, translationMatrix);
		
//		rotationMatrix((float) (Math.PI/4), rotationMatrix);
//		scaleMatrix(.5f, .5f, scaleMatrix);
//		translationMatrix(0, 1f, translationMatrix);
		
		translationMatrix(0, -.75f, translationMatrix);
		scaleMatrix(.2f, .2f, scaleMatrix);
		rotationMatrix((float) (-Math.PI/2), rotationMatrix);
		
		
		modelMatrix.mul(translationMatrix);
		modelMatrix.mul(scaleMatrix);
		modelMatrix.mul(rotationMatrix);

		
	}

	public void draw() {
		
		shader.enable();
		// set the attributes
		shader.setAttribute("a_position", vertexBuffer);
		shader.setAttribute("a_colour", colourBuffer);
		
		shader.setUniform("u_modelMatrix", modelMatrix);
		
		long newTime = System.nanoTime();
		
		
		float deltaTime = (float) ((newTime - oldTime)/ Math.pow(10, 8));
		
		System.out.println("oldTime: " + oldTime);
		System.out.println("newTime: " + newTime);
		System.out.println("deltaTime: " + deltaTime);
		
		rotationMatrix((float) (Math.PI/12) * deltaTime, rotationMatrix);
		translationMatrix(0, 1f * deltaTime, translationMatrix);
		
		modelMatrix.mul(rotationMatrix);
		modelMatrix.mul(translationMatrix);
		
		

		// draw using index buffer
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
		oldTime = newTime;

	}

	/**
	 * Set the destination matrix to a translation matrix. Note the destination
	 * matrix must already be allocated.
	 * 
	 * @param tx   Offset in the x direction
	 * @param ty   Offset in the y direction
	 * @param dest Destination matrix to write into
	 * @return
	 */

	public static Matrix4f translationMatrix(float tx, float ty, Matrix4f dest) {
		// clear the matrix to the identity matrix
		dest.identity();

		//     [ 1 0 0 tx ]
		// T = [ 0 1 0 ty ]
	    //     [ 0 0 0 0  ]
		//     [ 0 0 0 1  ]

		// Perform operations on only the x and y values of the T vec. 
		// Leaves the z value alone, as we are only doing 2D transformations.
		
		dest.m30(tx);
		dest.m31(ty);

		return dest;
	}

	/**
	 * Set the destination matrix to a rotation matrix. Note the destination matrix
	 * must already be allocated.
	 *
	 * @param angle Angle of rotation (in radians)
	 * @param dest  Destination matrix to write into
	 * @return
	 */

	public static Matrix4f rotationMatrix(float angle, Matrix4f dest) {
		
		dest.identity();

            //	   [ cos(angle) -sin(angle) 0 0  ]
			// T = [ sin(angle) cos(angle)  0 0  ]
		    //     [ 0          0           0 0  ]
			//     [ 0          0           0 1  ]

		dest.m00((float) Math.cos(angle));
		dest.m01((float) Math.sin(angle));
		
		dest.m10((float) -Math.sin(angle));
		dest.m11((float) Math.cos(angle));
		
		return dest;
	}

	/**
	 * Set the destination matrix to a scale matrix. Note the destination matrix
	 * must already be allocated.
	 *
	 * @param sx   Scale factor in x direction
	 * @param sy   Scale factor in y direction
	 * @param dest Destination matrix to write into
	 * @return
	 */

	public static Matrix4f scaleMatrix(float sx, float sy, Matrix4f dest) {
		
		dest.identity();

        //	   [ sx 0  0 0 ]
		// T = [ 0  sy 0 0 ]
	    //     [ 0  0  0 0 ]
		//     [ 0  0  0 1 ]
		
		dest.m00(sx);
		dest.m11(sy);

		return dest;
	}

}
