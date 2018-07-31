package engine.graphics.objects;

import engine.data.Planet;
import engine.graphics.objects.generators.MeshGenerator;
import engine.graphics.objects.generators.PlanetGenerator;
import engine.graphics.objects.light.*;
import engine.graphics.objects.textures.Texture;
import engine.graphics.renderer.color.RGBA;
import engine.math.numericalObjects.Vector3;
import load.TextureLoader;

public class Scene {

	private Camera camera;

	private AmbientLight ambientLight;
	private DirectionalLight directionalLight;
	private PointLight[] pointLights;
	private SpotLight[] spotLights;

	private GraphicalObject[] objects;

	public Scene() {
		initializeTestObjects();
	}

	private void initializeTestObjects() {
		Texture trollFace = TextureLoader.loadTexture("data/mods/vanilla/assets/textures/trollface.png");
		Texture cubeTexture = TextureLoader.loadTexture("data/mods/vanilla/assets/textures/space_cube2.png");
		Texture grasTexture = TextureLoader.loadTexture("data/mods/vanilla/assets/textures/gras.png");
		Texture icoTexture = TextureLoader.loadTexture("data/mods/vanilla/assets/textures/ico_wireframe.png");

		objects = new GraphicalObject[4];
		pointLights = new PointLight[3];
		spotLights = new SpotLight[8];
		double sunDistance = 1000;

		// background
		objects[0] = new GraphicalObject(MeshGenerator.createCube(true));
		objects[0].getMesh().setTexture(cubeTexture);
		objects[0].scale(sunDistance*2,sunDistance*2,sunDistance*2);
		objects[0].setAffectedByLight(false);
		objects[0].setStatic(true);
		objects[0].setUseDepthTest(false);

		// sun
		objects[2] = new GraphicalObject(MeshGenerator.createIcosahedron());
		objects[2].scale(10,10,10);
		objects[2].setPosition(0,0,sunDistance);
		objects[2].getMesh().setColor(1,1,0.5f);
		objects[2].setAffectedByLight(false);

		// moon
		objects[3] = new GraphicalObject(MeshGenerator.createIcosahedron());
		objects[3].getMesh().setTexture(icoTexture);
		//objects[3].scale(2,2,2);
		objects[3].setPosition(50,0,0);

		// sun lights
		pointLights[1] = new PointLight(0.5,0,0);
		pointLights[1].setAttenuation(Attenuation.CONSTANT());
		pointLights[1].setPosition(0,0,sunDistance);

		directionalLight = new DirectionalLight(0.5,1,1);
		directionalLight.setDirection(new Vector3(0,0,-1));

		ambientLight = new AmbientLight(0.5,0.5,0.5);

		camera = new Camera();
		camera.setRadius(2);
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

	public GraphicalObject[] getObjects() {
		return objects;
	}
	public void setObjects(GraphicalObject[] objects) {
		this.objects = objects;
	}
}
