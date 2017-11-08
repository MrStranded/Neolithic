package gui.graphics;

import gui.WindowInterface;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.system.MemoryUtil.memFree;

/**
 * A class containing all needed static variables and methods.
 *
 * Created by michael1337 on 08/11/17.
 */
public class GraphicsHandler {

	private static WindowInterface window;
	private static ShaderProgram shaderProgram;
	private static int vaoId,vboId;

	public static void addWindow(WindowInterface w) {
		window = w;
	}

	public static void init() {
		if (window != null) {
			window.init();

			try {
				shaderProgram = new ShaderProgram();
				shaderProgram.createVertexShader(Utils.loadResource("/vertex.vs"));
				shaderProgram.createFragmentShader(Utils.loadResource("/fragment.fs"));
				shaderProgram.link();

				float[] vertices = new float[]{
						0.0f,  0.5f, 0.0f,
						-0.5f, -0.5f, 0.0f,
						0.5f, -0.5f, 0.0f
				};

				FloatBuffer verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
				verticesBuffer.put(vertices).flip();

				vaoId = glGenVertexArrays();
				glBindVertexArray(vaoId);

				vboId = glGenBuffers();
				glBindBuffer(GL_ARRAY_BUFFER, vboId);
				glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
				memFree(verticesBuffer);

				glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

				// Unbind the VBO
				glBindBuffer(GL_ARRAY_BUFFER, 0);

				// Unbind the VAO
				glBindVertexArray(0);

				if (verticesBuffer != null) {
					MemoryUtil.memFree(verticesBuffer);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean draw() {
		if (window != null) {
			shaderProgram.bind();

			// Bind to the VAO
			glBindVertexArray(vaoId);
			glEnableVertexAttribArray(0);

			// Draw the vertices
			glDrawArrays(GL_TRIANGLES, 0, 3);

			// Restore state
			glDisableVertexAttribArray(0);
			glBindVertexArray(0);

			shaderProgram.unbind();

			return window.draw();
		}
		return false;
	}

	public static void tearDown() {
		if (window != null) {
			window.close();

			if (shaderProgram != null) {
				shaderProgram.cleanup();
			}

			glDisableVertexAttribArray(0);

			// Delete the VBO
			glBindBuffer(GL_ARRAY_BUFFER, 0);
			glDeleteBuffers(vboId);

			// Delete the VAO
			glBindVertexArray(0);
			glDeleteVertexArrays(vaoId);
		}
	}

}
