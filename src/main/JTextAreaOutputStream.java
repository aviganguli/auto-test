package main;

import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JTextArea;

public class JTextAreaOutputStream extends OutputStream {

	private JTextArea destination;

	public JTextAreaOutputStream(JTextArea destination) {
		if (destination == null)
			throw new IllegalArgumentException("Destination is null");
		this.destination = destination;
	}

	@Override
	public void write(int b) throws IOException {
		destination.append(String.valueOf((char)b));
	}

}