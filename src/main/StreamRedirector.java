package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * 
 * Redirects stream output to another output stream
 * @author AvishekGanguli
 */
public class StreamRedirector extends Thread {
	    private InputStream is;
	    private OutputStream os;

	    // reads everything from is until empty. 
	    public StreamRedirector(InputStream is, OutputStream os) {
	        this.is = is;
	        this.os=os;
	    }

	    /**
	     *
	     * Redirects stream from input to output. 
	     * 
	     */
	    public void run() {
	        try {
	            InputStreamReader isr = new InputStreamReader(is);
	            BufferedReader br = new BufferedReader(isr);
	            PrintWriter printWriter = new PrintWriter(os, true);
	            String line=null;
	            while ( (line = br.readLine()) != null)
	                printWriter.println(line);    
	        } catch (IOException ioe) {
	            ioe.printStackTrace();  
	            throw new IllegalStateException("Stream redirection should not "
	            		+ "result in an error.");
	        }
	    }
}


