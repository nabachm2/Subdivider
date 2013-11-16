package subdivider.util;

import subdivider.util.math.Vector3f;

/** 
 * @author Nicholas
 * This class represents a very basic class implementation of a quad face,
 * which hold the vertices and calculates the midpoint. It also calculates the 
 * normal of the face assuming its planar (this assumption is not correct, but 
 * the distortion in the normal is minimal)
 */
public class QuadFace {
	
	public Vector3f center;
	public Vector3f[] vertices;
	
	public QuadFace(Vector3f[] ver) {
		vertices = ver;
		center = new Vector3f();
		for (Vector3f v : vertices)
			center.addLocal(v);
		
		center.divideLocal(vertices.length);
	}

	public Vector3f getNormal() {
		Vector3f normal = new Vector3f();
		Vector3f u = vertices[2].subtract(vertices[0]);
		Vector3f v = vertices[1].subtract(vertices[0]);
		v.cross(u, normal);
		return normal.normalizeLocal();
	}

	public Vector3f[] getVertices() {
		return vertices;
	}
	
}