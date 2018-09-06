package engine.graphics.objects;

import engine.graphics.objects.generators.MeshGenerator;
import engine.graphics.objects.light.*;
import engine.graphics.objects.textures.Texture;
import engine.math.numericalObjects.Vector3;
import load.TextureLoader;

public class Scene {

	private Camera camera;

	private AmbientLight ambientLight;
	private DirectionalLight directionalLight;
	private PointLight[] pointLights;
	private SpotLight[] spotLights;

	private GraphicalObject[] objects;

	private ShadowMap shadowMap;

	public Scene() {
		initializeTestObjects();
	}

	private void initializeTestObjects() {
		Texture trollFace = TextureLoader.loadTexture("data/mods/vanilla/assets/textures/trollface.png");
		Texture cubeTexture = TextureLoader.loadTexture("data/mods/vanilla/assets/textures/space_cube2.png");
		Texture grasTexture = TextureLoader.loadTexture("data/mods/vanilla/assets/textures/gras.png");
		Texture moonTexture = TextureLoader.loadTexture("data/mods/vanilla/assets/textures/moon.png");

		objects = new GraphicalObject[4];
		pointLights = new PointLight[3];
		spotLights = new SpotLight[8];
		double sunDistance = 1000;

		// background
		objects[0] = new GraphicalObject(MeshGenerator.createCube(true));
		objects[0].getMesh().getMaterial().setTexture(cubeTexture);
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
		objects[3].getMesh().getMaterial().setTexture(moonTexture);
		objects[3].scale(0.5, 0.5, 0.5);
		objects[3].setPosition(0,0,50);
		objects[3].setRotation(0,0,Math.PI/8);

		// sun lights
		pointLights[1] = new PointLight(0.5,0.25,0.25);
		pointLights[1].setAttenuation(Attenuation.CONSTANT());
		pointLights[1].setPosition(0,0,sunDistance);

		directionalLight = new DirectionalLight(0.5,0.5,0.5);
		directionalLight.setDirection(new Vector3(0,0,-1));

		ambientLight = new AmbientLight(0.5,0.5,0.5);
		//ambientLight = new AmbientLight(0.75,0.75,0.75);

		// camera
		camera = new Camera();
		camera.setRadius(2);

		// shadow map
		try {
			shadowMap = new ShadowMap();
			shadowMap.setDistance(1.125d);
			shadowMap.setCamera(camera);
		} catch (Exception e) {
			e.printStackTrace();
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

	public GraphicalObject[] getObjects() {
		return objects;
	}
	public void setObjects(GraphicalObject[] objects) {
		this.objects = objects;
	}

	public ShadowMap getShadowMap() {
		return shadowMap;
	}
	public void setShadowMap(ShadowMap shadowMap) {
		this.shadowMap = shadowMap;
	}
}
