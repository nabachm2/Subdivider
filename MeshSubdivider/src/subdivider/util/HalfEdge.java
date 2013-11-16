package subdivider.util;

import java.util.ArrayList;
import java.util.HashMap;

import subdivider.util.math.Vector3f;

/**
 * @author Nicholas
 *
 * Implementation of the HalfEdge data structure. 
 * Instead of holding a reference to a Face, the structure only holds
 * the midpoint of the face, since that is the only part that is used. 
 * 
 * 
 * Finally this class provides a method to convert an array of faces to
 * an array of halfedges in approximately O(n) time, (using hashmaps).
 */
public class HalfEdge {

	private Vector3f end;
	private HalfEdge opposite;
	private HalfEdge next;
	private Vector3f facePoint;
	
	private Vector3f edgeMidpoint;
	
	public HalfEdge(Vector3f e, Vector3f f) {
		end = e;
		facePoint = f;
	}
	
	public void setOpposite(HalfEdge edge) {
		opposite = edge;
		edge.opposite = this;
	}
	
	/**
	 * updates the midpoint of the edge using 
	 * the formula (f1 + f2 + e1 + e2) / 4
	 */
	public void createEdgeMidpoint() {
		edgeMidpoint = new Vector3f();
		edgeMidpoint.addLocal(end);
		edgeMidpoint.addLocal(opposite.end);
		edgeMidpoint.addLocal(facePoint);
		edgeMidpoint.addLocal(opposite.facePoint);
		edgeMidpoint.divideLocal(4);
	}
	
	/**
	 * Updates the vertex position using the formula
	 * provided in class. 
	 * 
	 * Note: since each vertex is unique (ie no half edge shares
	 * the same vertex object) we dont have to worry about updating the
	 * same vertex twice.
	 */
	public void updatePosition() {
		int n = 0;
		Vector3f edgeAvg = new Vector3f();
		Vector3f faceAvg = new Vector3f();
		HalfEdge edge = this;
		do {
			n ++;
			edgeAvg.addLocal(edge.edgeMidpoint);
			faceAvg.addLocal(edge.facePoint);
			edge = edge.next.opposite;
		} while (edge != this);
		edgeAvg.divideLocal(n);
		faceAvg.divideLocal(n);
		
		edgeAvg.multLocal(2);
		end.multLocal(n - 3);
		end.addLocal(edgeAvg);
		end.addLocal(faceAvg);
		end.divideLocal(n);
	}
	
	/**
	 * Generates a new QuadFace by creating it from 
	 * the edge midpoint, the vertex, the next HalfEdge's midpoint
	 * and the face point
	 * 
	 * @return
	 */
	public QuadFace generateNewFace() {
		Vector3f[]vertices = {
			new Vector3f(edgeMidpoint),
			new Vector3f(end),
			new Vector3f(next.edgeMidpoint),
			new Vector3f(facePoint)
		};
		return new QuadFace(vertices);
	}
	
	/**
	 * Converts a list of QuadFaces to a Map of HalfEdges, with vertices as Keys,
	 * This runs in O(n) time.
	 * 
	 * @param faces
	 * @return
	 */
	public static HashMap<Vector3f, ArrayList<HalfEdge>> convertToHalfEdges(ArrayList<QuadFace> faces) {
		HashMap<Vector3f, ArrayList<HalfEdge>> edges = new HashMap<Vector3f, ArrayList<HalfEdge>>();
		for (QuadFace f : faces) {
			ArrayList<HalfEdge> faceEdges = new ArrayList<HalfEdge>();
			for (int i = 0;i < f.vertices.length;i ++) {
				Vector3f start = new Vector3f(f.vertices[i]);
				Vector3f end = new Vector3f(f.vertices[i + 1 == f.vertices.length ? 0 : i + 1]);
				HalfEdge edge = new HalfEdge(end, f.center);
				ArrayList<HalfEdge> opposites = edges.get(end); //look in the map to see if the opposite
				if (opposites != null) {                        //edge has been added
					for (HalfEdge e : opposites) {
						if (e.end.equals(start)) {
							edge.setOpposite(e);
							break;
						}
					}
				}
				
				faceEdges.add(edge);
				if (!edges.containsKey(start))
					edges.put(start, new ArrayList<HalfEdge>()); //add the half edge to the map
					
				edges.get(start).add(edge);
			}
			
			for (int i = 0;i < faceEdges.size();i ++) {
				int nextI = (i + 1) == faceEdges.size() ? 0 : i + 1;
				faceEdges.get(i).next = faceEdges.get(nextI); //connect the halfedge to its next value
			}
		}
		
		return edges;
	}
	
}
