package subdivider;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import subdivider.io.BasicQuadReaderWriter;
import subdivider.ui.Animator;
import subdivider.ui.Renderer;
import subdivider.util.QuadFace;
import subdivider.util.math.Vector3f;

/**
 *
 * @author Nicholas
 *
 *
 * Entry point for the application, see readme on how to actually launch 
 * the program, with arguments
 */
public class Subdivider {

	public static void main(String[] args) {
		//I wish getop was in java :(
		ArrayList<String> p_args = new ArrayList<String>();
		
		for (int i = 0;i < args.length;i ++) {
			p_args.add(args[i]);
		}
		
		//manually parse through the options...
		int inFileIndex = 0;
		if (p_args.size() == 0) { //incorrect input...
			printUsage();
			return;//exit
		}
		
		ArrayList<QuadFace> faces = BasicQuadReaderWriter.readQuadsFromFile(new File(p_args.get(inFileIndex)));
		if (faces == null) { //could not read, error message was printed by the reader
			return;
		}
		
		int subdivisionLevel = 0;
		try { 
			int numSubdivisionsIndex = p_args.indexOf("-subdivisions") + 1;
			if (subdivisionLevel != -1) //found this option
				subdivisionLevel = Integer.parseInt(p_args.get(numSubdivisionsIndex));
		} catch (Exception ex) { //bad value, must exit
			printUsage();
			return;
		}
		
		ArrayList<QuadFace> sub_faces = faces;
		try { 
			for (int i = 0;i < subdivisionLevel;i ++)
				sub_faces = QuadSubdivider.subdivideFaces(sub_faces); //subdivide the mesh to the given level
		} catch (NullPointerException ex) { 
			System.out.println("Model was too small, or too many subdivisions were specified :(");
			return;
		}
		
		int show = p_args.indexOf("-show"); //optional argument
		if (show != -1) //if argument exists build the gui
			setUpGUI(sub_faces);
		
		int outFileIndex = p_args.indexOf("-outfile") + 1;
		if (outFileIndex == p_args.size()) { //gave the -outfile argument with no value...
			printUsage();
			return;
		} else if (outFileIndex != 0) { //its an option, so if it exists...
			HashMap<Vector3f, Vector3f> normals = QuadSubdivider.calculateNormals(sub_faces);
			//Note that if the file already exists, or is not a file this will print an error
			BasicQuadReaderWriter.writeQuadsToFile(sub_faces, normals, new File(p_args.get(outFileIndex)));
		}
		
	}

	/**
	 * Sets up a basic GUI for viewing the final subdivision mesh, which 
	 * allows the user to rotate the view and look at what the program has
	 * created
	 * 
	 * @param sub_faces the faces to show
	 */
	private static void setUpGUI(ArrayList<QuadFace> sub_faces) {
		// setup OpenGL Version 2
    	GLProfile profile = GLProfile.get(GLProfile.GL2);
    	GLCapabilities capabilities = new GLCapabilities(profile);
    	capabilities.setDepthBits(24);
    	// The canvas is the widget that's drawn in the JFrame
    	GLCanvas glcanvas = new GLCanvas(capabilities);
    	//See Renderer, for a description of what this represents
    	Renderer renderer = new Renderer(sub_faces);
    	glcanvas.addGLEventListener(renderer);
    	glcanvas.setPreferredSize(new Dimension(300, 300));//set size of canvas
    	glcanvas.setMaximumSize(new Dimension(700, 700));
    	
    	Animator animator = new Animator(glcanvas, renderer);
    	
    	//create window with "Subdivider" as title 
        JFrame frame = new JFrame("Subdivider");
        frame.add(glcanvas);
        glcanvas.addKeyListener(animator);
        
        frame.add(glcanvas);
        
        // shutdown the program on windows close event
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                System.exit(0);
            }
        });
 
        frame.setSize(700, 500);
        frame.setVisible(true);
        animator.start();
	}

	/**
	 * Prints the correct usage for this program
	 * 
	 */
	private static void printUsage() {
		System.out.println("Usage: Subdivider infile [-subdivisions divisions] [-outfile outfile] [-show] ");
	}
	
}
