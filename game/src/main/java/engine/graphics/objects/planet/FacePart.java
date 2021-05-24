package engine.graphics.objects.planet;

import constants.TopologyConstants;
import engine.data.entities.Tile;
import engine.graphics.objects.generators.PlanetGenerator;
import engine.graphics.objects.models.Mesh;
import engine.graphics.renderer.color.RGBA;
import engine.graphics.renderer.shaders.ShaderProgram;
import engine.math.numericalObjects.Matrix4;
import engine.math.numericalObjects.Vector3;
import engine.math.numericalObjects.Vector4;

public class FacePart {
	
	private static final Vector3 cameraDirection = new Vector3(0,0,1);
	
	private Mesh mesh;
	private Mesh waterMesh;
	private Vector3 normal;

	private Vector3 mid, waterMid;

	private FacePart superFace;
	private FacePart[] quarterFaces;

	private double height = 0;
	private double waterHeight = TopologyConstants.PLANET_OCEAN_HEIGHT;

	private double oldHeight = height;
	private double oldWaterHeight = waterHeight;

	private Tile tile;
	private RGBA topColor = TopologyConstants.TILE_DEFAULT_COLOR;
	private RGBA sideColor = topColor.times(TopologyConstants.TILE_SIDE_COLOR_FACTOR);

	private final Vector3 corner1, corner2, corner3;

	private int depth;
	private boolean rendersSelf = false;
	private Vector3 intersection = null;
	private boolean changed = false;

	public FacePart(Vector3 corner1, Vector3 corner2, Vector3 corner3, FacePart superFace) {
		this.corner1 = corner1;
		this.corner2 = corner2;
		this.corner3 = corner3;

		this.superFace = superFace;

		normal = (corner1.plus(corner2).plus(corner3)).normalize();
	}

	// ###################################################################################
	// ################################ Rendering ########################################
	// ###################################################################################

	private void renderSelf(ShaderProgram shaderProgram, boolean putDataIntoShader, boolean drawWater) {
		rendersSelf = true;

		Mesh m = drawWater ? waterMesh : mesh;
		if (m != null) {
			if (putDataIntoShader) {
				if (m.getMaterial() != null) {
					shaderProgram.setUniform("material", m.getMaterial());
				}
				m.render(true);
			} else {
				m.renderForShadowMap();
			}
		}

		// render all objects on the current tile, but only if we're in the real draw situation (not shadow map drawing)
		if (quarterFaces == null && tile != null && putDataIntoShader) {
			tile.render();
		}
	}
	
	public void render(ShaderProgram shaderProgram, Matrix4 viewWorldMatrix, int depth, boolean putDataIntoShader, boolean drawWater) {
		rendersSelf = false;

		if (quarterFaces == null) {
			renderSelf(shaderProgram, putDataIntoShader,drawWater);
			return;
		}

		Vector3 viewVector = viewWorldMatrix.times(new Vector4(normal,0)).extractVector3();//.normalize();
		Vector3 distanceVector = viewWorldMatrix.times(new Vector4(normal, 1)).extractVector3();
		
		double factor = viewVector.dot(cameraDirection);
		if (factor < 0) {
			factor = 0;
		}

		// effect extremification and normalization
		factor = factor * factor / viewVector.lengthSquared();
		
		// distance detail falloff
		double distanceQuotient = distanceVector.lengthSquared() / 4;
		if (distanceQuotient < 1d) {
			distanceQuotient = 1d;
		}

		int detailLevel = (int) ((double) depth * factor / distanceQuotient);
		// on camera facing side, go at least one level deep if close enough
		if (factor > 0 && detailLevel < 1 && distanceQuotient < 10) {
			detailLevel = 1;
		}
		if (detailLevel >= depth) {
			detailLevel = depth-1;
		}

		if (detailLevel >= this.depth) {
			for (FacePart facePart : quarterFaces) {
				if (facePart != null) {
					facePart.render(shaderProgram, viewWorldMatrix, depth, putDataIntoShader, drawWater);
				}
			}
		} else {
			renderSelf(shaderProgram, putDataIntoShader, drawWater);
		}
	}

	// ###################################################################################
	// ################################ Picking with Mouse ###############################
	// ###################################################################################

	public FacePart intersects(Vector3 rayOrigin, Vector3 rayDirection) {
	    intersection = null;
		if (normal.dot(rayDirection) >= 0) { return null; }

        if (rendersSelf || quarterFaces == null) {

            double f = PlanetGenerator.getHeightFactor(getMaxHeight());
            Vector3[] corners = new Vector3[]{corner1.times(f), corner2.times(f), corner3.times(f)};
            boolean intersects = intersectsTriangle(rayOrigin, rayDirection,
                    corners[0], corners[1], corners[2]);

            // why doesn't this work?
            if (!intersects) {
                Vector3 planetOrigin = new Vector3(0,0,0);
                for (int i = 0; i < 3; i++) {
                    int j = (i + 1) % 3;

                    intersects = intersectsTriangle(rayOrigin, rayDirection,
                            planetOrigin, corners[j], corners[i]);
                    if (intersects) { return this; }
                }
            }

            if (intersects) {
                return this;
            }

        } else {

            FacePart closest = null;

            for (FacePart facePart : quarterFaces) {
                FacePart intersectedPart = facePart.intersects(rayOrigin, rayDirection);
                if (intersectedPart != null) {
                    if (closest == null || intersectedPart.closerToCamera(closest, rayOrigin)) {
                        closest = intersectedPart;
                    }
                }
            }

            return closest;

        }

		return null;
	}

	private boolean intersectsTriangle(Vector3 rayOrigin, Vector3 rayDirection, Vector3 corner1, Vector3 corner2, Vector3 corner3) {
		Vector3 u = corner2.minus(corner1);
		Vector3 v = corner3.minus(corner1);
        Vector3 planeNormal = u.cross(v).normalize();

        //if (planeNormal.dot(rayDirection) >= 0) { return false; }

        double t = (planeNormal.dot(corner1) - planeNormal.dot(rayOrigin)) / planeNormal.dot(rayDirection);
        intersection = rayOrigin.plus(rayDirection.times(t));

        Vector3 w = intersection.minus(corner1);
        double denominator = (u.dot(v) * u.dot(v)) - u.dot(u) * v.dot(v);

        double lengthA = ((u.dot(v)) * (w.dot(v)) - (w.dot(u)) * v.dot(v)) / denominator;
        if (lengthA > 0 && lengthA < 1) {
            double lengthB = ((u.dot(v)) * (w.dot(u)) - (w.dot(v)) * u.dot(u)) / denominator;
            if (lengthB > 0 && lengthA + lengthB < 1) {
                return true;
            }
        }

		return false;
	}

	public boolean closerToCamera(FacePart other, Vector3 rayOrigin) {
	    if (other.intersection == null) { return true; }
	    if (intersection == null) { return false; }

	    //double fSelf = PlanetGenerator.getHeightFactor(height);
	    //double fOther = PlanetGenerator.getHeightFactor(other.height);
		return ((intersection).minus(rayOrigin).lengthSquared() < (other.intersection).minus(rayOrigin).lengthSquared());
	}

	// ###################################################################################
	// ################################ Clearing Change Flag & Clean Up ##################
	// ###################################################################################

	public void clearChangeFlags() {
		changed = false;
		if (quarterFaces != null) {
			for (FacePart facePart : quarterFaces) {
				if (facePart.hasChanged()) {
					facePart.clearChangeFlags();
				}
			}
		}
	}

	public void cleanMeshes() {
		if (mesh != null) { mesh.cleanUp(); }
		if (waterMesh != null) { waterMesh.cleanUp(); }

		mesh = null;
		waterMesh = null;
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public Tile getTile() {
		return tile;
	}
	public void setTile(Tile tile) {
		this.tile = tile;
	}

	/**
	 * If i is from one to three, the corresponding corner is returned.
	 * For other values of i, null is returned.
	 * @param i corner that should be returned
	 * @return corner with given number
	 */
	public Vector3 getScaledCorner(int i) {
		if (i < 1 || i > 3) { return null; }

		Vector3 corner = switch(i) {
			case 1 -> corner1;
			case 2 -> corner2;
			case 3 -> corner3;
			default -> null; // cannot happen
		};

		double f = PlanetGenerator.getHeightFactor(height);
		return corner.times(f);
	}
	public Vector3 getCorner1() {
		return corner1;
	}
	public Vector3 getCorner2() {
		return corner2;
	}
	public Vector3 getCorner3() {
		return corner3;
	}

	public Vector3 getIntersection() {
	    return intersection;
    }

	public Vector3 getMid() {
		return mid;
	}
	/**
	 * This value is set in PlanetGenerator.createTile()
	 * @param mid
	 */
	public void setMid(Vector3 mid) {
		this.mid = mid;
	}

	/**
	 * Returns the middle position of the water on the tile if it exists.
	 * If not water exists on the tile, the mid of the land position is returned.
	 * @return mid of water if existant, otherwise mid of water
	 */
	public Vector3 getWaterMid() {
		if (waterMid == null) {
			return mid;
		}
		return waterMid;
	}
	/**
	 * This value is set in PlanetGenerator.createTile()
	 * @param waterMid
	 */
	public void setWaterMid(Vector3 waterMid) {
		this.waterMid = waterMid;
	}

	public double getMaxHeight() {
		return Math.max(height, waterHeight);
	}

	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}

	public double getWaterHeight() {
		return waterHeight;
	}
	public void setWaterHeight(double waterHeight) {
		this.waterHeight = waterHeight;
	}

    public double getOldHeight() {
        return oldHeight;
    }
    public void setOldHeight(double oldHeight) {
        this.oldHeight = oldHeight;
    }

    public double getOldWaterHeight() {
        return oldWaterHeight;
    }
    public void setOldWaterHeight(double oldWaterHeight) {
        this.oldWaterHeight = oldWaterHeight;
    }

    public RGBA getTopColor() {
		return topColor;
	}
	public void setTopColor(RGBA topColor) {
		this.topColor = topColor;
	}

	public RGBA getSideColor() {
		return sideColor;
	}
	public void setSideColor(RGBA sideColor) {
		this.sideColor = sideColor;
	}

	public Mesh getWaterMesh() {
		return waterMesh;
	}
	public void setWaterMesh(Mesh waterMesh) {
		this.waterMesh = waterMesh;
	}

	public Mesh getMesh() {
		return mesh;
	}
	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}

	public Vector3 getNormal() {
		return normal;
	}
	public void setNormal(Vector3 normal) {
		this.normal = normal;
	}

	public FacePart[] getQuarterFaces() {
		return quarterFaces;
	}
	public void setQuarterFaces(FacePart[] quarterFaces) {
		this.quarterFaces = quarterFaces;
	}

	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}

	public boolean hasChanged() {
		return changed;
	}
	public void setChanged(boolean changed) {
		this.changed = changed;
		if (changed && superFace != null/* && !superFace.hasChanged()*/) {
			superFace.setChanged(true);
		}
	}
}
