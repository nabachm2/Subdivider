package subdivider.ui.util;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2;

import subdivider.util.math.Vector3f;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;


/**
 * 
 * @author Nicholas
 *
 * Singleton class used to manage effects used by opengl (mainly textures, lights)
 * Textures are loaded using TextureIO, provided by the JOGL api, and are stored
 * in a hashmap, while lights are stored in an arraylist 
 *
 */
public class EffectsManager {

	private static EffectsManager manager;
	
	private HashMap<String, Texture> textures;
	private ArrayList<Light> lights;
	
	public EffectsManager() {
		textures = new HashMap<String, Texture>();
		lights = new ArrayList<Light>();
	}
	
	public static EffectsManager getManager() {
		if (manager == null)
			manager = new EffectsManager();
		
		return manager;
	}
		
	/**
	 * Load a texture (must be png)
	 * 
	 * @param file path to file
	 */
	public void loadTexture(GL2 gl, String file)
	{
		try {
		    ByteArrayOutputStream os = new ByteArrayOutputStream();
		    ImageIO.write(ImageIO.read(new File(file)), "png", os);
		    InputStream fis = new ByteArrayInputStream(os.toByteArray());
		    textures.put(file, TextureIO.newTexture(fis, true, TextureIO.PNG));
		    textures.get(file).enable(gl);
		} catch (IOException ex) { ex.printStackTrace(); }
	}
	
	/**
	 * Bind the texture for use, must be loaded first
	 * @param file file that should be bound
	 */
	public void bindTexture(GL2 gl, String file) {
		Texture t = textures.get(file);
		if (t != null)
			t.bind(gl);
	}
	
	/**
	 * creates a light using the properties provided, so long as GL_MAX_LIGHTS
	 * has not been surpassed
	 * @param gl
	 * @param pos position of light (in this case it is a directional vector)
	 * @param amb ambient color
	 * @param dif diffuse color
	 * @param spec specular color
	 */
	public void createLight(GL2 gl, Vector3f pos, Vector3f amb, Vector3f dif, Vector3f spec) {
		if (lights.size() < GL2.GL_MAX_LIGHTS)
			lights.add(new Light(gl, pos, amb, dif, spec));
	}
	
	public Light getLight(int number) {
		if (number >= lights.size())
			return null;
		
		return lights.get(number);
	}
	
	/**
	 * Sends the lights properties to opengl using glLight
	 * @param gl
	 */
	public void updateLights(GL2 gl) {
		for (Light l : lights)
			l.updateLightParameters(gl);
	}
	
	/**
	 * 
	 * @author Nicholas
	 *
	 * Container class for storing light data
	 */
	public class Light {
		
		public float[] position;
		public float[] ambient;
		public float[] diffuse;
		public float[] specular;
		
		private int lightNumber;
		
		public Light(GL2 gl, Vector3f pos, Vector3f amb, Vector3f dif, Vector3f spec) {
			position = new float[] { pos.x, pos.y, pos.z, 1};
			ambient = new float[] { amb.x, amb.y, amb.z, 1};
			diffuse = new float[] { dif.x, dif.y, dif.z, 1};
			specular = new float[] { spec.x, spec.y, spec.z, 1};
			gl.glEnable(GL2.GL_LIGHT0 + lightNumber);
			if (lightNumber == 0) //if this is the first light enable lighting
				gl.glEnable(GL2.GL_LIGHTING);
			
			updateLightParameters(gl);
		}
		
		public void updateLightParameters(GL2 gl) {
			gl.glLightfv(GL2.GL_LIGHT0 + lightNumber, GL2.GL_AMBIENT, ambient, 0);
	        gl.glLightfv(GL2.GL_LIGHT0 + lightNumber, GL2.GL_DIFFUSE, diffuse, 0);
	        gl.glLightfv(GL2.GL_LIGHT0 + lightNumber, GL2.GL_SPECULAR, specular, 0);
	        gl.glLightfv(GL2.GL_LIGHT0 + lightNumber, GL2.GL_POSITION, position, 0);
		}
		
	}
	
	
}
