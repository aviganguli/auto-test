package gui;

import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;

public class FadeAnimation {
	    private static final int TIME = 200;
	    private static final int MILLIS_PER_FRAME = 33;
	    private static final float DELTA = MILLIS_PER_FRAME / (float)TIME; //how much the opacity will change on each tick
	    private static boolean DONE = false;
	    
	    /**
	     * @param frame the frame to fade in or out
	     */
	    public static void fade(final JFrame frame)
	    {
	        //frame.setOpacity(1f); // set opacity to 1 as we're fading out
	        final Timer timer = new Timer();
	        TimerTask timerTask = new TimerTask()
	        {
	            float opacity =  1f;
	            float delta = -DELTA;
	            
	            @Override
	            public void run()
	            {
	                opacity += delta; //tweak the opacity
	                if (opacity < 0) //we're invisible now
	                {
	                	frame.dispose(); //faded so dispose of frame
	                    timer.cancel(); //stop the timer
	                    FadeAnimation.DONE = true;
	                    
	                }
	                else
	                    frame.setOpacity(opacity);
	            }
	        };
	        timer.scheduleAtFixedRate(timerTask, MILLIS_PER_FRAME, MILLIS_PER_FRAME);
	        while (!DONE) {
	        }
	        DONE = false;
	    }
	}
