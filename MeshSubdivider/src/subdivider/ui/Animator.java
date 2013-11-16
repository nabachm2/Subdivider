package subdivider.ui;


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.media.opengl.awt.GLCanvas;

import subdivider.util.math.FastMath;


/**
 * @author Nicholas
 *
 * Responsible for controlling the animation of the of the scene.
 * This includes rotating the scene in accordance with keypresses.
 * This class also maintains a max fps limiter of 60fps.
 *
 */
public class Animator extends Thread implements KeyListener {

	private static final int maxFPS = 60;
	
	private GLCanvas canvas;
	private Renderer renderer;
	
	//camera constraints 
	private float cameraDistance;
	private float cameraDistanceStep;
	private float viewRotationVertical;
	private float viewRotationVerticalStep;
	private float viewRotationHorizontal;
	private float viewRotationHorizontalStep;
	
	private float runTime;
	private double lastRenderTime;
	
	public Animator(GLCanvas canvas, final Renderer renderer) {
		this.renderer = renderer;
		this.canvas = canvas;
		
		cameraDistance = 15;
		viewRotationVertical = FastMath.PI / 4;
	}

	/**
	 * The method that gets called when the Thread starts
	 */
	@Override
	public void run() {
		while (true) {
			long startTime = System.nanoTime();
	     
			cameraDistance += cameraDistanceStep;
			viewRotationVertical += viewRotationVerticalStep * lastRenderTime;
			viewRotationHorizontal += viewRotationHorizontalStep
					* lastRenderTime;
			float eyeX = cameraDistance * FastMath.sin(viewRotationVertical)
					* FastMath.cos(viewRotationHorizontal);
			float eyeY = cameraDistance * FastMath.cos(viewRotationVertical);
			float eyeZ = cameraDistance * FastMath.sin(viewRotationVertical)
					* FastMath.sin(viewRotationHorizontal);

			// limit camera distance, and vertical rotation
			cameraDistance = Math.min(Math.max(cameraDistance, 5), 30);
			// viewRotationVertical = Math.min(Math.max(viewRotationVertical,
			// -FastMath.PI / 2.2f), FastMath.PI / 2.2f);
			renderer.setEyePosition(eyeX, eyeY, eyeZ);
			
			canvas.display();	
			long endTime = System.nanoTime();	
			//this is the frame limiter logic, works by finding how much time as passed to update/render
			//and then calculates how much time the thread needs to sleep to achieve the desired fps
			long sleepTime = Math.max(0, (long) ((1.0f / maxFPS * 1000) - (endTime - startTime) / 1.0E6));//IN MILIS
			lastRenderTime = (endTime - startTime) / 1.0E9 + sleepTime / 1000.0;
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) { e.printStackTrace(); }
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		//on key presses we update the step value of 
		//the particular camera constraint, to achieve smooth animation
		if (arg0.getKeyCode() == KeyEvent.VK_LEFT)
			viewRotationHorizontalStep = -1;
		else if (arg0.getKeyCode() == KeyEvent.VK_RIGHT)
			viewRotationHorizontalStep = 1;
		else if (arg0.getKeyCode() == KeyEvent.VK_UP)
			viewRotationVerticalStep = 1;
		else if (arg0.getKeyCode() == KeyEvent.VK_DOWN)
			viewRotationVerticalStep = -1;
		else if (arg0.getKeyCode() == KeyEvent.VK_S)
			cameraDistanceStep = 0.1f;
		else if (arg0.getKeyCode() == KeyEvent.VK_W)
			cameraDistanceStep = -0.1f;
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		viewRotationVerticalStep = 
				viewRotationHorizontalStep = 
				cameraDistanceStep = 0;
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
