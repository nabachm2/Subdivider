package subdivider.ui;




import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import subdivider.QuadSubdivider;
import subdivider.ui.util.EffectsManager;
import subdivider.util.QuadFace;
import subdivider.util.math.Vector3f;


 
/**
 * @author Nicholas
 *
 *Contains all the methods required to render an openGL context, which are called internally,
 *by GLCanvas when specific events are triggered, such as the window resizing.
 *This class is responsible for maintaining the I mesh and and subdividing
 *
 *This class can be broken into 3 important methods init, display and reshape
 *where display is called every time the view needs to be updated, reshape when the
 *main window is resized, and init, which is called once
 *
 * NOTE: The light source is always looking with the camera (ie is not transformed) so you will always see
 * a lit side, this was intentional
 *
 */
public class Renderer implements GLEventListener {
    
    private GLU glu;
    
    private ArrayList<QuadFace> mesh;
    private HashMap<Vector3f, Vector3f> normals;
    
    private Vector3f eyeLocation;

    public Renderer(ArrayList<QuadFace> mesh){
    	eyeLocation = new Vector3f();
    	glu = new GLU();
    	this.mesh = mesh;
    	normals = QuadSubdivider.calculateNormals(mesh);
   }
    
    /**
     * called when display() is called on GLCanvas, this is where opengl does its rendering 
     */
    public void display(GLAutoDrawable gLDrawable) {
        final GL2 gl = gLDrawable.getGL().getGL2();  
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT); //clear buffers
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();	

        EffectsManager.getManager().updateLights(gl);
        //eye positition is set by Animator class, it looks at 0, 0, 0
        glu.gluLookAt(eyeLocation.x, eyeLocation.y, eyeLocation.z, 0, 0, 0, 0, 1, 0);
        
        gl.glPushMatrix();
        gl.glBegin(GL2.GL_QUADS);
        
        Random rand = new Random(100);
        
        for (QuadFace f : mesh) {
        	for (Vector3f v : f.getVertices()) {
        		Vector3f norm = normals.get(v);
        		gl.glColor3f(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
        		gl.glNormal3f(norm.x, norm.y, norm.z);
        		gl.glVertex3f(v.x, v.y, v.z);
        	}
        }
        gl.glEnd();
        gl.glPopMatrix();
        gl.glFlush();    
    }
 
    public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged, boolean deviceChanged) {
    }
    
    /**
     * called when GLCanvas is created and a GLEventListener is assigned
     */
    public void init(GLAutoDrawable gLDrawable) {
        GL2 gl = gLDrawable.getGL().getGL2();
        
        gl.glClearDepth(1.0f); // Depth Buffer Setup
        gl.glEnable(GL2.GL_DEPTH_TEST);  // Enables Depth Testing
        gl.setSwapInterval(1); // enable v-synch @ 60fps
        
        gl.glEnable(GL2.GL_CULL_FACE);
        
        gl.glColorMaterial (GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE);
        gl.glEnable (GL2.GL_COLOR_MATERIAL);
        
        // create a light source at 0, 100, 100
        EffectsManager.getManager().createLight(gl, new Vector3f(0, 100, 100), new Vector3f(), new Vector3f(1f, 1f, 1f), new Vector3f());        
    }
 
    /**
     * Called whenever the window changes size
     */
    public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, int height) {
        final GL2 gl = gLDrawable.getGL().getGL2();
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, (float) width / (float) height, 1f, 400);
    }
 
	public void dispose(GLAutoDrawable arg0) { }

	public void setEyePosition(float x, float y, float z) {
		eyeLocation.set(x, y, z);
	}
	
}
