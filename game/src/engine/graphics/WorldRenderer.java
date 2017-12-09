/**
 * Copyright (c) 2008-2012 Ardor Labs, Inc.
 * <p>
 * This file is part of Ardor3D.
 * <p>
 * Ardor3D is free software: you can redistribute it and/or modify it
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */

package engine.graphics;

import com.ardor3d.annotation.MainThread;
import com.ardor3d.framework.Canvas;
import com.ardor3d.framework.Updater;
import com.ardor3d.image.Texture;
import com.ardor3d.input.InputState;
import com.ardor3d.input.Key;
import com.ardor3d.input.control.FirstPersonControl;
import com.ardor3d.input.logical.*;
import com.ardor3d.light.PointLight;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.MathUtils;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyMatrix3;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.renderer.state.LightState;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.shape.Icosahedron;
import com.ardor3d.ui.text.BasicText;
import com.ardor3d.util.ReadOnlyTimer;
import com.ardor3d.util.TextureManager;
import com.ardor3d.util.geom.BufferUtils;
import data.Data;
import environment.world.Planet;

import java.nio.ByteBuffer;
import java.util.Random;

public class WorldRenderer implements Updater {
	// private final Canvas view;
	private final WorldScene scene;
	private final Exit exit;
	private final LogicalLayer logicalLayer;
	private final Key toggleRotationKey;

	private final static float WORLD_ROTATE_SPEED = 0.2f;
	private double angle = 0;
	private Vector3 at,up;
	private Matrix3 rotationMatrix;

	private Planet planet;
	private Mesh[] tiles;
	private Node worldMeshNode;

	private final Matrix3 rotation = new Matrix3();

	private static final int MOVE_SPEED = 10;
	private int rotationSign = 1;
	private boolean inited;

	public WorldRenderer(final WorldScene scene, final Exit exit, final LogicalLayer logicalLayer, final Key toggleRotationKey, Planet planet) {
		this.scene = scene;
		this.exit = exit;
		this.logicalLayer = logicalLayer;
		this.toggleRotationKey = toggleRotationKey;

		MeshGenerator.setWorldRenderer(this);
	}

	@MainThread
	public void init() {
		if (inited) {
			return;
		}

		final ZBufferState buf = new ZBufferState();
		buf.setEnabled(true);
		buf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
		scene.getRoot().setRenderState(buf);

		// world lights

		final PointLight light = new PointLight();

		light.setDiffuse(new ColorRGBA(0.5f, 0.5f,0.5f, 0.5f));
		light.setAmbient(new ColorRGBA(1f, 1f, 0, 1f));
		light.setLocation(new Vector3(2000,2000,2000)); // take this line out maybe
		light.setEnabled(true);

		// Attach the light to a lightState and the lightState to rootNode.

		final LightState lightState = new LightState();
		lightState.setEnabled(true);
		lightState.attach(light);
		scene.getRoot().setRenderState(lightState);

		int tileAmount = 0;
		if (planet != null) {
			tileAmount = planet.getSize()*planet.getSize()*20;
		}
		tiles = new Mesh[tileAmount];

		// there is no input yet
		registerInputTriggers();

		inited = true;
	}

//	public void registerMesh(Mesh mesh) {
//		scene.getRoot().attachChild(mesh);
//	}

	public void registerWorldMesh(WorldMesh worldMesh) {
//		for (Mesh mesh : worldMesh.getMeshes()) {
//			registerMesh(mesh);
//		}
		worldMeshNode = worldMesh;
		scene.getRoot().attachChild(worldMesh);
	}

	private void registerInputTriggers() {
		final FirstPersonControl control = FirstPersonControl.setupTriggers(logicalLayer, Vector3.UNIT_Y, true);
		control.setMoveSpeed(MOVE_SPEED);

		logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.ESCAPE), new TriggerAction() {
			public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
				exit.exit();
			}
		}));

		logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(toggleRotationKey), new TriggerAction() {
			public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
				toggleRotation();
			}
		}));
		logicalLayer.registerTrigger(new InputTrigger(new KeyReleasedCondition(Key.U), new TriggerAction() {
			public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
				toggleRotation();
			}
		}));

		logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.ZERO), new TriggerAction() {
			public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
				resetCamera(source);
			}
		}));
		logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.NINE), new TriggerAction() {
			public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
				lookAtZero(source);
			}
		}));

		logicalLayer.registerTrigger(new InputTrigger(new AnyKeyCondition(), new TriggerAction() {
			public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
				final InputState current = inputStates.getCurrent();

				System.out.println("Key character pressed: " + current.getKeyboardState().getKeyEvent().getKeyChar());
			}
		}));
	}

	private void lookAtZero(final Canvas source) {
		source.getCanvasRenderer().getCamera().lookAt(Vector3.ZERO, Vector3.UNIT_Y);
	}

	private void resetCamera(final Canvas source) {
		final Vector3 loc = new Vector3(0.0f, 0.0f, (Data.getPlanet().getRadius()+255) * 2);
		final Vector3 left = new Vector3(-1.0f, 0.0f, 0.0f);
		final Vector3 up = new Vector3(0.0f, 1.0f, 0.0f);
		final Vector3 dir = new Vector3(0.0f, 0f, -1.0f);

		source.getCanvasRenderer().getCamera().setFrame(loc, left, up, dir);
	}

	private void toggleRotation() {
		rotationSign = rotationSign - 1;
	}

	@MainThread
	public void update(final ReadOnlyTimer timer) {
		final double tpf = timer.getTimePerFrame();

		logicalLayer.checkTriggers(tpf);

		// rotate away

		angle += tpf * WORLD_ROTATE_SPEED * rotationSign;

		at = new Vector3(Math.cos(angle),0,Math.sin(angle));
		up = new Vector3(0,1d,0);

		// here one would rotate the world

		rotationMatrix = new Matrix3();
		MathUtils.matrixLookAt(scene.getRoot().getWorldTranslation(),at,up,rotationMatrix);
		//if (worldMeshNode != null) worldMeshNode.setRotation(rotationMatrix);

		scene.getRoot().updateGeometricState(tpf, true);
	}
}
