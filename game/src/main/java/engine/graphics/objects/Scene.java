package engine.graphics.objects;

import constants.GraphicalConstants;
import constants.TopologyConstants;
import engine.data.Data;
import engine.graphics.objects.generators.MeshGenerator;
import engine.graphics.objects.light.*;
import engine.graphics.objects.models.Mesh;
import engine.graphics.objects.planet.Sun;
import engine.math.numericalObjects.Vector3;
import engine.parser.utils.Logger;

import java.util.ArrayList;
import java.util.Collection;

public class Scene {

	private Camera camera;

	private AmbientLight ambientLight;
	private DirectionalLight directionalLight;
	private PointLight[] pointLights = new PointLight[3];
	private SpotLight[] spotLights = new SpotLight[8];

	private Collection<GraphicalObject> objects = new ArrayList<GraphicalObject>(16);

	private ShadowMap shadowMap;

	public Scene() {
		initializeUniverseObjects();
	}

	private void initializeUniverseObjects() {
		// ------------------------------------------------------------------------------------------- camera
		camera = new Camera();
		camera.setRadius(2);

		// ------------------------------------------------------------------------------------------- ambient light
		double ambient = 0.2;
		ambientLight = new AmbientLight(ambient, ambient, ambient);

		// ------------------------------------------------------------------------------------------- universe
		new Builder(MeshGenerator.createCube(true))
				.texture("space_cube4.png")
				.scale(GraphicalConstants.SUN_DISTANCE * 2)
				.affectedByLight(false)
				.setStatic(true)
				.useDepthTest(false);

		// ------------------------------------------------------------------------------------------- sun
		GraphicalObject sunObject = new Builder(MeshGenerator.createIcosahedron(false))
				.scale(10)
				.position(0,0,GraphicalConstants.SUN_DISTANCE)
				.color(1,1,0.5f)
				.affectedByLight(false)
				.build();
		Sun sun = new Sun(sunObject);
		Data.setSun(sun);

		// sun lights
		pointLights[1] = new PointLight(0.2,0.1,0.1);
		pointLights[1].setAttenuation(Attenuation.CONSTANT());
		pointLights[1].setPosition(0,0,GraphicalConstants.SUN_DISTANCE);
		sun.addCompanion(pointLights[1], 1);

		double directional = 0.9;
		directionalLight = new DirectionalLight(directional, directional, directional);
		directionalLight.setDirection(new Vector3(0,0,-1));
		sun.addCompanion(directionalLight, 1);

		// inner shadow plane
		GraphicalObject shadowPlane = new Builder(MeshGenerator.createCircle(
				36,
				(float) ((TopologyConstants.PLANET_MINIMUM_HEIGHT + TopologyConstants.PLANET_OCEAN_HEIGHT) / TopologyConstants.PLANET_MINIMUM_HEIGHT)
		))
				.renderInScene(false)
				.build();
		sun.addCompanion(shadowPlane, 1);

		// shadow map
		try {
			shadowMap = new ShadowMap();
			shadowMap.setDistance(1.125d);
			shadowMap.setCamera(camera);
			sun.addCompanion(shadowMap, 1);
		} catch (Exception e) {
			Logger.error("Could not create shadow map");
			e.printStackTrace();
		}

		// ------------------------------------------------------------------------------------------- moon
		GraphicalObject moon = new Builder(MeshGenerator.createIcosahedron(false))
				.texture("moon.png")
				.scale(0.5, 0.5, 0.5)
				.position(0,0,50)
				.rotation(0,0,Math.PI/8)
				.build();
		Data.setMoon(moon);

		// ------------------------------------------------------------------------------------------- atmosphere
		double atmosphereScale = (TopologyConstants.PLANET_MAXIMUM_HEIGHT + TopologyConstants.PLANET_HEIGHT_RANGE) / TopologyConstants.PLANET_MINIMUM_HEIGHT;

		for (int i=0; i<3; i++) {
			GraphicalObject layer = new Builder(MeshGenerator.createIcosahedron(true))
					.scale(atmosphereScale, atmosphereScale, atmosphereScale)
					.color(0.65f - 0.1f * i, 0.75f - 0.1f * i, 1f - 0.1f * i, 0.5f - 0.2f * i)
					.useDepthTest(false)
					.affectedByLight(true)
					.affectedByShadow(false)
					.lightDirectionFlipped(true)
					.renderInShadowMap(false)
					.build();
			sun.addCompanion(layer, (i + 1d) / (i + 2d) );

			atmosphereScale *= 1.2;
		}

	}

	// ###################################################################################
	// ################################ Utility ##########################################
	// ###################################################################################

	private class Builder {
		private GraphicalObject obj;

		Builder(Mesh mesh) {
			if (mesh == null) { mesh = MeshGenerator.createCube(false); }
			obj = new GraphicalObject(mesh);
			objects.add(obj);
		}

		GraphicalObject build() { return obj; }

		Builder texture(String path) {
			obj.setTexture(path);
			return this;
		}

		Builder position(double x, double y, double z) {
			obj.setPosition(x, y, z);
			return this;
		}

		Builder scale(double s) {
			return scale(s, s, s);
		}
		Builder scale(double x, double y, double z) {
			obj.scale(x, y, z);
			return this;
		}

		Builder rotation(double x, double y, double z) {
			obj.rotate(x, y, z);
			return this;
		}

		Builder color(float r, float g, float b) {
			return color(r, g, b, 1f);
		}
		Builder color(float r, float g, float b, float a) {
			obj.getMesh().setColor(r, g, b, a);
			return this;
		}

		Builder affectedByLight(boolean affected) {
			obj.setAffectedByLight(affected);
			return this;
		}
		Builder affectedByShadow(boolean affected) {
			obj.setAffectedByShadow(affected);
			return this;
		}

		Builder lightDirectionFlipped(boolean flipped) {
			obj.setLightDirectionFlipped(flipped);
			return this;
		}

		Builder setStatic(boolean isStatic) {
			obj.setStatic(isStatic);
			return this;
		}

		Builder useDepthTest(boolean test) {
			obj.setUseDepthTest(test);
			return this;
		}

		Builder renderInScene(boolean render) {
			obj.setRenderInScene(render);
			return this;
		}
		Builder renderInShadowMap(boolean render) {
			obj.setRenderInShadowMap(render);
			return this;
		}

	}

	// ###################################################################################
	// ################################ Clean Up #########################################
	// ###################################################################################

	public void cleanUp() {
		for (GraphicalObject object : objects) {
			if (object != null) {
				object.cleanUp();
			}
		}
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public Camera getCamera() {
		return camera;
	}
	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public AmbientLight getAmbientLight() {
		return ambientLight;
	}
	public void setAmbientLight(AmbientLight ambientLight) {
		this.ambientLight = ambientLight;
	}

	public DirectionalLight getDirectionalLight() {
		return directionalLight;
	}
	public void setDirectionalLight(DirectionalLight directionalLight) {
		this.directionalLight = directionalLight;
	}

	public PointLight[] getPointLights() {
		return pointLights;
	}
	public void setPointLights(PointLight[] pointLights) {
		this.pointLights = pointLights;
	}

	public SpotLight[] getSpotLights() {
		return spotLights;
	}
	public void setSpotLights(SpotLight[] spotLights) {
		this.spotLights = spotLights;
	}

	public Collection<GraphicalObject> getObjects() {
		return objects;
	}

	public ShadowMap getShadowMap() {
		return shadowMap;
	}
	public void setShadowMap(ShadowMap shadowMap) {
		this.shadowMap = shadowMap;
	}
}
