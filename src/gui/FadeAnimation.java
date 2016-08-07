package gui;


import java.awt.Frame;

import javax.swing.JFrame;

public class FadeAnimation {
	    private static final int TIME = 200;
	    private static final int MILLIS_PER_FRAME = 33;
	    private static final float DELTA = MILLIS_PER_FRAME / (float)TIME; //how much the opacity will change on each tick
	    public volatile static boolean DONE = false;
	    
	    /**
	     * @param frame the frame to fade in or out
	     */
	    public static void fade(final JFrame frame)
	    {

	    	float opacity =  1f;
	        float delta = -DELTA;
	        while (true) {
	        	 opacity += delta; //tweak the opacity
		        if (opacity < 0.1) //we're practically invisible now
		        {
		
		        	frame.dispose(); 
		        	System.out.println("FADE" + Thread.currentThread());
		            return;
		        }
		        else {
		            frame.setOpacity(opacity);
		            try {
						Thread.sleep(75);
					} catch (InterruptedException e) {
						throw new IllegalStateException("Animation failure!");
					}
		        }
	        }
	    }
	}
