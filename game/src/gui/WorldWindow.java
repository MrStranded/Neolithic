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

import com.ardor3d.framework.Canvas;
import com.ardor3d.framework.DisplaySettings;
import com.ardor3d.framework.FrameHandler;
import com.ardor3d.framework.jogl.JoglAwtCanvas;
import com.ardor3d.framework.jogl.JoglCanvasRenderer;
import com.ardor3d.image.util.awt.AWTImageLoader;
import com.ardor3d.input.ControllerWrapper;
import com.ardor3d.input.Key;
import com.ardor3d.input.MouseCursor;
import com.ardor3d.input.PhysicalLayer;
import com.ardor3d.input.awt.AwtFocusWrapper;
import com.ardor3d.input.awt.AwtKeyboardWrapper;
import com.ardor3d.input.awt.AwtMouseManager;
import com.ardor3d.input.awt.AwtMouseWrapper;
import com.ardor3d.input.logical.*;
import com.ardor3d.util.Timer;
import com.ardor3d.util.resource.ResourceLocatorTool;
import com.ardor3d.util.resource.SimpleResourceLocator;
import engine.graphics.Exit;
import engine.graphics.WorldRenderer;
import engine.graphics.WorldScene;
import engine.graphics.examples.ExampleScene;
import engine.graphics.examples.RotatingCubeGame;
import environment.world.Planet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * This examples demonstrates how to render OpenGL (via JOGL) on a AWT canvas.
 */
public class WorldWindow implements WindowInterface {
	private Map<Canvas, Boolean> _showCursor1 = new HashMap<Canvas, Boolean>();
	private Planet planet;

	private int width=800,height=600;

	public WorldWindow(Planet planet, int width, int height) {
		this.planet = planet;
		this.width = width;
		this.height = height;
	}

	public void init() {
		System.setProperty("ardor3d.useMultipleContexts", "true");

		final Timer timer = new Timer();
		final FrameHandler frameWork = new FrameHandler(timer);

		final MyExit exit = new MyExit();
		final LogicalLayer logicalLayer = new LogicalLayer();

		final WorldScene scene = new WorldScene();
		final WorldRenderer game = new WorldRenderer(scene, exit, logicalLayer, Key.SPACE, planet);

		frameWork.addUpdater(game);

		final JFrame frame = new JFrame("Neolithic");
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				exit.exit();
			}
		});

		//frame.setLayout(new GridLayout(2, 3));

		AWTImageLoader.registerLoader();

		try {
			addCanvas(frame, scene, logicalLayer, frameWork);
		} catch (Exception e) {
			e.printStackTrace();
		}

		frame.pack();
		frame.setVisible(true);

		game.init();

		while (!exit.isExit()) {
			frameWork.updateFrame();
			Thread.yield();
		}

		frame.dispose();
		System.exit(0);
	}

	private void addCanvas(final JFrame frame, final WorldScene scene, final LogicalLayer logicalLayer, final FrameHandler frameWork) throws Exception {
		final JoglCanvasRenderer canvasRenderer = new JoglCanvasRenderer(scene);

		final DisplaySettings settings = new DisplaySettings(width, height, 32, 0, 0, 16, 0, 0, false, false);
		final JoglAwtCanvas theCanvas = new JoglAwtCanvas(settings, canvasRenderer);

		frame.add(theCanvas);

		_showCursor1.put(theCanvas, true);

		theCanvas.setSize(new Dimension(width, height));
		theCanvas.setVisible(true);

		final AwtKeyboardWrapper keyboardWrapper = new AwtKeyboardWrapper(theCanvas);
		final AwtFocusWrapper focusWrapper = new AwtFocusWrapper(theCanvas);
		final AwtMouseManager mouseManager = new AwtMouseManager(theCanvas);
		final AwtMouseWrapper mouseWrapper = new AwtMouseWrapper(theCanvas, mouseManager);
		final ControllerWrapper controllerWrapper = new DummyControllerWrapper();

		final PhysicalLayer pl = new PhysicalLayer(keyboardWrapper, mouseWrapper, controllerWrapper, focusWrapper);

		logicalLayer.registerInput(theCanvas, pl);

		frameWork.addCanvas(theCanvas);

	}

	public boolean draw() {
		return true;
	}

	public void close() {
		
	}
	
	private class MyExit implements Exit {
		private volatile boolean exit = false;

		@Override
		public void exit() {
			exit = true;
		}

		public boolean isExit() {
			return exit;
		}
	}
}
