package Presentation;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.GridLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import net.miginfocom.swing.MigLayout;

public class MainFrame extends JFrame implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField TextFile;
	private JPanel FilePane, ActionPane;
	private JLabel lblFile;
	private JButton BtnFile, BtnCancel, BtnAccept;
	private File file,Sfile;
	private Controller control;
	private JButton BtnSaveFile;
	private JPanel MessagesText;
	private int numberFiles;
	private JLabel lblSaveFile;
	private JTextField TextSave;
	private JLabel labelProgressing;
	private JLabel label;
	private JLabel label_1;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 400, 200);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		FilePane = new JPanel();
		contentPane.add(FilePane, BorderLayout.NORTH);
		
		lblFile = new JLabel("File:");
		FilePane.add(lblFile);
		
		TextFile = new JTextField();
		FilePane.add(TextFile);
		TextFile.setColumns(20);
		TextFile.setEditable(false);
		
		BtnFile = new JButton("Browse");
		BtnFile.addActionListener(this);
		FilePane.add(BtnFile);
		
		ActionPane = new JPanel();
		contentPane.add(ActionPane, BorderLayout.SOUTH);
		
		BtnCancel = new JButton("Cancel");
		BtnCancel.addActionListener(this);
		ActionPane.add(BtnCancel);
		
		BtnAccept = new JButton("Process");
		BtnAccept.setEnabled(false);
		BtnAccept.addActionListener(this);
		ActionPane.add(BtnAccept);
		
		MessagesText = new JPanel();
		contentPane.add(MessagesText, BorderLayout.CENTER);
		MessagesText.setLayout(new MigLayout("", "[128px][128px][128px]", "[23px][97px]"));
		
		lblSaveFile = new JLabel("Save File:");
		MessagesText.add(lblSaveFile, "cell 0 0,alignx right,aligny center");
		
		TextSave = new JTextField();
		TextSave.setEditable(false);
		MessagesText.add(TextSave, "cell 1 0,alignx center,aligny center");
		TextSave.setColumns(20);
		
		BtnSaveFile = new JButton("Save");
		MessagesText.add(BtnSaveFile, "cell 2 0,alignx center,aligny center");
		
		labelProgressing = new JLabel("");
		MessagesText.add(labelProgressing, "cell 0 1,alignx center,aligny center");
		
		label = new JLabel("");
		MessagesText.add(label, "cell 1 1,alignx center,aligny center");
		
		label_1 = new JLabel("");
		MessagesText.add(label_1, "cell 2 1,grow");
		BtnSaveFile.addActionListener(this);
		numberFiles = 0;
		control = null;
		this.setResizable(false);
	}
	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource().equals(BtnCancel)){
			this.dispose();
		}else{
			if(event.getSource().equals(BtnAccept)){
				AcceptAction();
			}else{
				if(event.getSource().equals(BtnFile)){
					SelectionAction();
				}else {
					if(event.getSource().equals(BtnSaveFile)) {
						SaveAction();
					}
				}
			}
		}
	}
	/*
	 * Selection of the file or directory to process
	 */
	private void SelectionAction() {
		Path currentPath = Paths.get(".").toAbsolutePath();
		JFileChooser fileChooser = new JFileChooser(currentPath.toString());
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Java", "java");
		fileChooser.setFileFilter(filter);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		int returnVal = fileChooser.showOpenDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION){
			TextFile.setText(fileChooser.getSelectedFile().getAbsolutePath());
			file = fileChooser.getSelectedFile();
			if(control != null) {
				int clearList = JOptionPane.showConfirmDialog(this, "You want to Start a new review process?", "New Process", JOptionPane.YES_NO_OPTION);
				if(clearList==JOptionPane.YES_OPTION) {
					control.clearList();
				}
			}
			if(control == null) {
				control = new Controller();
				control.addPropertyChangeListener(new PropertyChangeListener() {
					
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if("progress".equals(evt.getPropertyName())) {
							label_1.setText("Files Processed : "+(Integer)evt.getNewValue()+"%");
						}
					}
				});
			}
			System.out.println("Exploration Begins");
			control.Explore(file);
			System.out.println("Explation ends");
			numberFiles = control.numberFiles();
			System.out.println(String.valueOf(numberFiles)+" Files");
		}
	}
	/*
	 * Event to process the file file or directory
	 */
	private void AcceptAction() {
		try {
			ModifyMessage();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			JOptionPane.showConfirmDialog(this,"File could not been Found"+ file.getAbsolutePath());
		} catch (BadLocationException e) {
			System.out.println("Problems inserting string in document");
			e.printStackTrace();
		}
		BtnAccept.setEnabled(false);
	}
	private void SaveAction() {
		Path currentPath = Paths.get(".").toAbsolutePath();
		JFileChooser fileChooser = new JFileChooser(currentPath.toString());
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Text", "txt");
		fileChooser.setFileFilter(filter);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		int returnVal = fileChooser.showSaveDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION){
			Sfile = fileChooser.getSelectedFile();
			try {
				if(!Sfile.exists()) {
					Sfile.createNewFile();
				}
				TextSave.setText(Sfile.getName());
				BtnAccept.setEnabled(true);
			} catch (IOException e) {
				JOptionPane.showConfirmDialog(this,"An error Occurred "+e.getMessage());
				e.printStackTrace();
			} 
		}
	}
	private void ModifyMessage() throws FileNotFoundException, BadLocationException {
		List<String> statements = new ArrayList<String>();
		FileWriter 	writer = null;
		BufferedWriter buffer = null;
		control.execute();
		try {
			System.out.println(control.get().size());
			statements = control.get();
			System.out.println("Statements: "+statements.size());
			writer = new FileWriter(Sfile,true);
			buffer = new BufferedWriter(writer);
			for(String stat:statements) {
				buffer.write(stat);
			}
			buffer.close();
			writer.close();
		} catch (InterruptedException | ExecutionException e) {
			JOptionPane.showMessageDialog(this, "Interrumped process");
			e.printStackTrace();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Exception Writing");
			e.printStackTrace();
		}
		
	}
}
