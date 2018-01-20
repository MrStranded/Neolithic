/**
 * Copyright (c) 2008-2012 Ardor Labs, Inc.
 * <p>
 * This file is part of Ardor3D.
 * <p>
 * Ardor3D is free software: you can redistribute it and/or modify it
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */

package gui;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

import environment.world.Planet;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This examples demonstrates how to render OpenGL (via JOGL) on a AWT canvas.
 */
public class WorldWindow extends SimpleApplication implements WindowInterface {
	private Map<Canvas, Boolean> _showCursor1 = new HashMap<Canvas, Boolean>();
	private Planet planet;
	private String title;

	private int width=800,height=600;

	public WorldWindow(String title, Planet planet, int width, int height) {
		this.planet = planet;

//		this.settings.setWidth(width);
//		this.settings.setHeight(height);
//		this.settings.setTitle(title);
		this.start();
	}

	@Override
	public void simpleInitApp() {

		Mesh m = new Mesh();

		// Vertex positions in space
		Vector3f [] vertices = new Vector3f[4];
		vertices[0] = new Vector3f(0,0,0);
		vertices[1] = new Vector3f(3,0,0);
		vertices[2] = new Vector3f(0,3,0);
		vertices[3] = new Vector3f(3,3,0);

		// Texture coordinates
		Vector2f [] texCoord = new Vector2f[4];
		texCoord[0] = new Vector2f(0,0);
		texCoord[1] = new Vector2f(1,0);
		texCoord[2] = new Vector2f(0,1);
		texCoord[3] = new Vector2f(1,1);

		// Indexes. We define the order in which mesh should be constructed
		short[] indexes = {2, 0, 1, 1, 3, 2};

		// Setting buffers
		m.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
		m.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
		m.setBuffer(Type.Index, 1, BufferUtils.createShortBuffer(indexes));
		m.updateBound();

		// *************************************************************************
		// First mesh uses one solid color
		// *************************************************************************

		// Creating a geometry, and apply a single color material to it
		Geometry geom = new Geometry("OurMesh", m);
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);

		// Attaching our geometry to the root node.
		rootNode.attachChild(geom);
	}

	@Override
	public void init() {

	}

	public boolean draw() {
		return true;
	}

	public void close() {
		
	}

}
