package lwjgltest;


import gui.graphics.Utils;
import org.joml.*;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Created by Michael on 08.11.2017.
 */
public class Main {

	/**
	 * Code from : https://github.com/SilverTiger/lwjgl3-tutorial/wiki/Rendering
	 */
	public static void main(String[] args) {

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		long window = glfwCreateWindow(800,600, "Neolithic", NULL, NULL);

		int vao = glGenVertexArrays();
		glBindVertexArray(vao);

		MemoryStack stack = MemoryStack.stackPush();
		FloatBuffer vertices = stack.mallocFloat(3 * 6);
		vertices.put(-0.6f).put(-0.4f).put(0f).put(1f).put(0f).put(0f);
		vertices.put(0.6f).put(-0.4f).put(0f).put(0f).put(1f).put(0f);
		vertices.put(0f).put(0.6f).put(0f).put(0f).put(0f).put(1f);
		vertices.flip();

		int vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
		MemoryStack.stackPop();

		int vertexShader=0,fragmentShader=0;

		try {
			vertexShader = glCreateShader(GL_VERTEX_SHADER);
			glShaderSource(vertexShader, Utils.loadResource("/vertex.vs"));
			glCompileShader(vertexShader);

			fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
			glShaderSource(fragmentShader, Utils.loadResource("/fragment.fs"));
			glCompileShader(fragmentShader);

			int status = glGetShaderi(vertexShader, GL_COMPILE_STATUS);
			if (status != GL_TRUE) {
				throw new RuntimeException(glGetShaderInfoLog(vertexShader));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		int shaderProgram = glCreateProgram();
		glAttachShader(shaderProgram, vertexShader);
		glAttachShader(shaderProgram, fragmentShader);
		glBindFragDataLocation(shaderProgram, 0, "fragColor");
		glLinkProgram(shaderProgram);

		int status = glGetProgrami(shaderProgram, GL_LINK_STATUS);
		if (status != GL_TRUE) {
			throw new RuntimeException(glGetProgramInfoLog(shaderProgram));
		}

		glUseProgram(shaderProgram);

		int floatSize = 4;

		int posAttrib = glGetAttribLocation(shaderProgram, "position");
		glEnableVertexAttribArray(posAttrib);
		glVertexAttribPointer(posAttrib, 3, GL_FLOAT, false, 6 * floatSize, 0);

		int colAttrib = glGetAttribLocation(shaderProgram, "color");
		glEnableVertexAttribArray(colAttrib);
		glVertexAttribPointer(colAttrib, 3, GL_FLOAT, false, 6 * floatSize, 3 * floatSize);

		int uniModel = glGetUniformLocation(shaderProgram, "model");
		Matrix4f model = new Matrix4f();
		FloatBuffer fb;
		glUniformMatrix4fv(uniModel, false, model.get(fb));

		int uniView = glGetUniformLocation(shaderProgram, "view");
		Matrix4f view = new Matrix4f();
		glUniformMatrix4fv(uniView, false, view.getBuffer());

		int uniProjection = glGetUniformLocation(shaderProgram, "projection");
		float ratio = 640f / 480f;
		Matrix4f projection = Matrix4f.orthographic(-ratio, ratio, -1f, 1f, -1f, 1f);
		glUniformMatrix4fv(uniProjection, false, projection.getBuffer());
	}

}
