package engine.graphics.objects;

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
		Texture cubeTexture = TextureLoader.loadTexture("data/mods/vanilla/assets/textures/space_cube.png");
		Texture grasTexture = TextureLoader.loadTexture("data/mods/vanilla/assets/textures/gras.png");
		Texture icoTexture = TextureLoader.loadTexture("data/mods/vanilla/assets/textures/ico_wireframe.png");

		objects = new GraphicalObject[4];
		pointLights = new PointLight[3];
		spotLights = new SpotLight[8];
		double sunDistance = 10;

		// background
		objects[0] = new GraphicalObject(MeshGenerator.createCube(true));
		objects[0].getMesh().setTexture(cubeTexture);
		objects[0].scale(sunDistance*2,sunDistance*2,sunDistance*2);
		objects[0].setAffectedByLight(false);
		objects[0].setStatic(true);
		objects[0].setUseDepthTest(false);

//		try {
//			objects[1] = new GraphicalObject(OBJLoader.loadMesh("data/mods/vanilla/assets/meshes/monkey.obj"));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		//objects[1] = new GraphicalObject(MeshGenerator.createIcosahedron());
		objects[1] = new GraphicalObject(PlanetGenerator.createPlanet(1));
		//objects[1].getMesh().setTexture(icoTexture);
		objects[1].scale(3,3,3);
		objects[1].rotate(0,0,Math.PI/8);
		//objects[1].getMesh().getMaterial().setSpecularPower(4);
		//objects[1].getMesh().getMaterial().setReflectanceStrength(new RGBA(1,0.5,0.5,0));

		objects[2] = new GraphicalObject(MeshGenerator.createIcosahedron());
		objects[2].scale(1,1,1);
		objects[2].setPosition(0,0,-sunDistance);
		objects[2].getMesh().setColor(1,1,0.5f);
		objects[2].setAffectedByLight(false);

		objects[3] = new GraphicalObject(MeshGenerator.createIcosahedron());
		objects[3].getMesh().setTexture(icoTexture);
		objects[3].scale(0.5,0.5,0.5);
		objects[3].setPosition(5,0,0);

		pointLights[1] = new PointLight(1,0,0);
		pointLights[1].setAttenuation(Attenuation.CONSTANT());
		pointLights[1].setPosition(0,0,-sunDistance);

		spotLights[5] = new SpotLight(0,1,0);
		spotLights[5].setAttenuation(Attenuation.MEDIUM());
		spotLights[5].setPosition(0,0,-sunDistance);
		spotLights[5].setDirection(new Vector3(0,0,1));
		spotLights[5].setConeAngle(Math.PI/32);

		directionalLight = new DirectionalLight(0,0,1);
		directionalLight.setDirection(new Vector3(0,0,1));

		ambientLight = new AmbientLight(0.5,0.5,0.5);

		camera = new Camera();
		camera.setRadius(5);
	}

	// ###################################################################################
	// ################################ Clean Up #########################################
	// ###################################################################################

	public void cleanUp() {
		for (GraphicalObject object : objects) {
			object.cleanUp();
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
