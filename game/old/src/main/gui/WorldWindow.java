/**
 * Copyright (c) 2008-2012 Ardor Labs, Inc.
 * <p>
 * This file is part of Ardor3D.
 * <p>
 * Ardor3D is free software: you can redistribute it and/or modify it
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */

package main.gui;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import main.data.Data;
import main.engine.graphics.MeshGenerator;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This examples demonstrates how to render OpenGL (via JOGL) on a AWT canvas.
 */
public class WorldWindow extends SimpleApplication implements WindowInterface {
	private Map<Canvas, Boolean> _showCursor1 = new HashMap<Canvas, Boolean>();
	private String title;

	private static AssetManager staticAssetManager;
	private static Node staticRootNode;

	private int width=800,height=600;

	public WorldWindow(String title, int width, int height) {
//		this.settings.setWidth(width);
//		this.settings.setHeight(height);
//		this.settings.setTitle(title);
		this.start();
	}

	@Override
	public void simpleInitApp() {
		WorldWindow.staticAssetManager = assetManager;
		WorldWindow.staticRootNode = rootNode;

		WorldWindow.staticAssetManager.registerLocator("main/data/mods/vanilla/assets",FileLocator.class);

		MeshGenerator.createWorld(Data.getPlanet());

		cam.setLocation(new Vector3f(0,0,30f));
		flyCam.setMoveSpeed(12f);
	}

	public static AssetManager getStaticAssetManager() { return WorldWindow.staticAssetManager; }
	public static Node getStaticRootNode() { return WorldWindow.staticRootNode; }

	@Override
	public void init() {

	}

	public boolean draw() {
		return true;
	}

	public void close() {
		
	}

}