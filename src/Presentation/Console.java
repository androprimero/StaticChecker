package Presentation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class Console implements Runnable {
	private JTextArea pane;
	private BufferedReader reader;
	private Console(JTextArea pane,PipedOutputStream pos) {
		this.pane = pane;
		try {
			PipedInputStream pis = new PipedInputStream(pos);
			reader = new BufferedReader(new InputStreamReader(pis));
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public void run() {
		String line = null;
		try {
			line = reader.readLine();
			while(line != null) {
				pane.append(line + "\n");
				pane.setCaretPosition(pane.getDocument().getLength());
				line = reader.readLine();
			}
			
		}catch(IOException e) {
			JOptionPane.showMessageDialog(null, "Error redirecting Output "+e.getMessage());
		}
	}
	public static void redirectOut(JTextArea text) {
		PipedOutputStream pos = new PipedOutputStream();
		System.setOut(new PrintStream(pos,true));
		Console console = new Console(text,pos);
		new Thread(console).start();
	}
}
