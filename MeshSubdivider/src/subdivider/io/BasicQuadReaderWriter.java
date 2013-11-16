package subdivider.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import subdivider.util.QuadFace;
import subdivider.util.math.Vector3f;

/**
 * Basic reader and writer for a simple quad file format, where all
 * the vertices for one quad face are on each line with values seperated by spaces. 
 * The normals will be written after the line "NORMALS", however these values
 * are ignored by the reader, since it will generate them anyway
 * 
 * @author Nicholas
 *
 */
public class BasicQuadReaderWriter {

	/**
	 * Given a file, this will return the an array of QuadFaces
	 * from the file, or print an error message and return null
	 * if there was an error reading or parsing the file's contents.
	 * 
	 * @param file
	 * @return Array of quad faces on success null otherwise
	 */
	public static ArrayList<QuadFace> readQuadsFromFile(File file) {
		try { 
			ArrayList<QuadFace> faces = new ArrayList<QuadFace>();
			BufferedReader reader = new BufferedReader(new FileReader(file)); //setup reader
			
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.toUpperCase().contains("NORMALS")) //stop reading, we found the normals section
					break;
				
				String[] args = line.split(" "); //split the line by spaces
				Vector3f[] quadVertices = new Vector3f[4];
				try {
					for (int i = 0;i < 3 * 4;i += 3) { //read 4 vertices, with 3 floats
						float x = Float.parseFloat(args[i]); //read the values
						float y = Float.parseFloat(args[i + 1]);
						float z = Float.parseFloat(args[i + 2]);
						quadVertices[i / 3] = new Vector3f(x, y, z);
					}
				} catch (Exception e) { //bad value from parseFloat
					System.out.println("Error parsing file");
					reader.close();
					return null;
				}
				faces.add(new QuadFace(quadVertices));
			}
			
			reader.close();
			return faces;
		} catch (IOException e) { 
			System.out.println("Could not read from file: " + file);
			return null;
		}
	}

	/**
	 * Given an array of QuadFaces, normals mapped by vertices, 
	 * and an output file, this function writes the data to the file
	 * if the output file doesn't exist
	 * 
	 * @param sub_faces array of faces
	 * @param normals map of normals where the normals are mapped to vertices
	 * @param outFile
	 */
	public static void writeQuadsToFile(ArrayList<QuadFace> sub_faces,
			HashMap<Vector3f, Vector3f> normals, File outFile) {
		
		try {
			if (outFile.exists()) { //file exists
				System.out.println("File already exists... did not overwrite file");
				return;
			}
			
			outFile.createNewFile(); //create file
			BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
			for (QuadFace face : sub_faces) {
				for (int i = 0;i < 4;i ++) { //write vertices
					writer.write(face.vertices[i].x + " " + face.vertices[i].y + " " + face.vertices[i].z + " "); 
				}
				writer.write("\n");
				writer.flush();
			}
			
			writer.write("NORMALS\n"); //start writing normal section
			for (QuadFace face : sub_faces) {
				for (int i = 0;i < 4;i ++) {
					Vector3f normal = normals.get(face.vertices[i]); //get the normal for this vertex
					writer.write(normal.x + " " + normal.y + " " + normal.z + " "); //write the normals
				}
				writer.write("\n");
				writer.flush();
			}
			writer.close();
		} catch (IOException e) { 
			System.out.println("Could not write to file: " + outFile);
		}
	}
	
}
