package subdivider;

import java.util.ArrayList;
import java.util.HashMap;

import subdivider.util.HalfEdge;
import subdivider.util.QuadFace;
import subdivider.util.math.Vector3f;

/**
 * @author Nicholas
 *
 * Class used to divide an array of faces into more faces, 
 * also provides a method to calculate the normals for an array of faces
 */
public class QuadSubdivider {

	/**
	 * Subdivides a mesh of QuadFaces in approximately O(n) time
	 * 
	 * @param faces
	 * @return new faces list
	 */
	public static ArrayList<QuadFace> subdivideFaces(ArrayList<QuadFace> faces) {
		HashMap<Vector3f, ArrayList<HalfEdge>> halfEdges = HalfEdge.convertToHalfEdges(faces);
		for (Vector3f v : halfEdges.keySet())
			for (HalfEdge e : halfEdges.get(v))
				e.createEdgeMidpoint(); //update the midpoint
		
		for (Vector3f v : halfEdges.keySet())
			for (HalfEdge e : halfEdges.get(v))
				e.updatePosition(); //update the vertex position
		
		ArrayList<QuadFace> newFaces = new ArrayList<QuadFace>(); //new array for new faces
		for (Vector3f v : halfEdges.keySet())
			for (HalfEdge e : halfEdges.get(v)) 
				newFaces.add(e.generateNewFace()); //add the new faces to array
		
		return newFaces;
	}
	
	/**
	 * Calculates the normals for each vertex and returns a map 
	 * where the vertex is the key and the value is the normal
	 * 
	 * @param faces
	 * @return
	 */
	public static HashMap<Vector3f, Vector3f> calculateNormals(ArrayList<QuadFace> faces) {
		HashMap<Vector3f, Vector3f> normals = new HashMap<Vector3f, Vector3f>();
		HashMap<Vector3f, Integer> numNormals = new HashMap<Vector3f, Integer>();
		for (QuadFace f : faces) {
			for (Vector3f v : f.getVertices()) {
				if (!normals.containsKey(v)) { //key is not in array, put it in
					normals.put(v, new Vector3f());
					numNormals.put(v, new Integer(0));
				}
				
				normals.get(v).addLocal(f.getNormal()); //add face normal to vertex
				numNormals.put(v, numNormals.get(v) + 1);
			}
		}
		for (Vector3f v : normals.keySet()) //divide the normal by the amount of faces added
			normals.get(v).divideLocal(numNormals.get(v)).normalizeLocal(); 
		
		return normals;
	}
	
}
