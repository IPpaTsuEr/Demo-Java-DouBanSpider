package spider;


import javax.swing.JFrame;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;


public class UI {

	private JFrame frame;
	private JTextField txtPath;
	private JTextArea txtrTextareazone;
	private JFileChooser jc=new JFileChooser();
	private JTextPane textPane ;
	private String selectedPath="";
	private String selectedFile="";
	private boolean stop=false,sleep=false,add=false;
	

	
	
	public void initial() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}
		
		frame = new JFrame();
		frame.setBounds(100, 100, 600, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
			}
		});
		JPanel panel_1 = new JPanel();
		
		txtPath = new JTextField();
		txtPath.setColumns(10);
		
		JButton btnPathbut = new JButton("path");
		btnPathbut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				jc.setFileSelectionMode(JFileChooser.OPEN_DIALOG | JFileChooser.DIRECTORIES_ONLY);  
				jc.showDialog(null,null);  
				File d=jc.getSelectedFile();
				if(d!=null){
					selectedPath=d.getAbsolutePath();
					if(selectedPath!="")txtPath.setText(selectedPath);
				}
				
			}
		});
		
		JButton btnSleep = new JButton("sleep");
		btnSleep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				sleep=!sleep;
				putStatu("按下了Sleep----->"+sleep+"\n");
			}
		});
		
		JButton btnClear = new JButton("clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				txtrTextareazone.setText("");
				putStatu("按下了Clear-----\n");
			}
		});
		
		JButton btnAdd = new JButton("add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				jc.setFileSelectionMode(JFileChooser.OPEN_DIALOG | JFileChooser.FILES_ONLY);  
				jc.showDialog(null,null);  
				File f=jc.getSelectedFile();
				if(f!=null){
					add=true;
					selectedFile=f.getAbsolutePath();
					putStatu("指定文件----->"+selectedFile+"\n");
				}
			}
		});
		
		JButton btnStop = new JButton("stop");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				stop=!stop;
				putStatu("按下了Stop----->"+stop+"\n");
			}
		});
		
		JPanel panel = new JPanel();
		
		JScrollPane scrollPane = new JScrollPane();
		
		 txtrTextareazone = new JTextArea();
		 txtrTextareazone.setLineWrap(true);
		 
		 txtrTextareazone.getDocument().addDocumentListener(new DocumentListener(){
		 public void changedUpdate(DocumentEvent e) {
		 }
		 public void insertUpdate(DocumentEvent e) {
			 if(txtrTextareazone.getLineCount()>6666){
				 txtrTextareazone.setText("");
			 }
		 }
		 public void removeUpdate(DocumentEvent e) {
		 }
		 }); 
		scrollPane.setViewportView(txtrTextareazone);
		
		JToolBar toolBar = new JToolBar();
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
						.addComponent(toolBar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(panel_1, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 574, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(7)
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 68, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel, GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(toolBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(8))
		);
		
		textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setEnabled(false);
		toolBar.add(textPane);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addComponent(scrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addComponent(scrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
		);
		panel.setLayout(gl_panel);
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGap(6)
					.addComponent(txtPath, GroupLayout.PREFERRED_SIZE, 369, GroupLayout.PREFERRED_SIZE)
					.addGap(25)
					.addComponent(btnPathbut)
					.addGap(31)
					.addComponent(btnSleep))
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGap(306)
					.addComponent(btnClear)
					.addGap(31)
					.addComponent(btnAdd, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE)
					.addGap(31)
					.addComponent(btnStop, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGap(7)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addGap(1)
							.addComponent(txtPath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(btnPathbut)
						.addComponent(btnSleep))
					.addGap(7)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(btnClear)
						.addComponent(btnAdd)
						.addComponent(btnStop)))
		);
		panel_1.setLayout(gl_panel_1);
		frame.getContentPane().setLayout(groupLayout);
	
		frame.setVisible(true);
		
	}

	public void putStatu(String str){
		textPane.setText(str);
	}
	public void putTextArea(String str){
		txtrTextareazone.append(str+"\n");
		txtrTextareazone.setCaretPosition(txtrTextareazone.getText().length());  
	}
	public void setFiled(String str){
		txtPath.setText(str);
	}

	public String getSelectedPath() {
		return selectedPath;
	}

	public void setSelectedPath(String selectedPath) {
		this.selectedPath = selectedPath;
	}

	public String getSelectedFile() {
		return selectedFile;
	}
	
	public void setSelectedFile(String selectedFile) {
		this.selectedFile = selectedFile;
	}
	public int getLine(){
		return txtrTextareazone.getColumns();
	}
	public void clearLine(){
		txtrTextareazone.setText("");
	}

	
	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

	public boolean isSleep() {
		return sleep;
	}

	public void setSleep(boolean sleep) {
		this.sleep = sleep;
	}
	public boolean isAdd() {
		return add;
	}

	public void setAdd(boolean add) {
		this.add = add;
	}
}
