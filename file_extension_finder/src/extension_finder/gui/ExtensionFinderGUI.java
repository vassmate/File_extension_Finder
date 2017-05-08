package extension_finder.gui;

import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import extension_finder.FileExtensionFinder;

public class ExtensionFinderGUI {

	private JFrame frame;
	private JTextField fieldPath;
	private JTextField fieldExtension;
	private HashMap<String, ArrayList<String>> searchResult;
	private TextArea textAreaErrorLog;
	private JTextField fieldResultCount;
	private JScrollPane resultScrollPane;

	/**
	 * Launch the application.
	 */
	public static void launchExtFinderGUI() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ExtensionFinderGUI window = new ExtensionFinderGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ExtensionFinderGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		assembleFrame();
		addPathLabel();
		addExtensionLabel();
		addResultLabel();
		addErrorLabel();
		addPathTextField();
		addExtensionTextField();
		addSearchButton();
		addResultCountField();
		addErrorLogTextArea();
		addSeparators();
		addResultScrollPane();
	}

	private void assembleFrame() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 1280, 720);
		frame.setIconImage(Toolkit.getDefaultToolkit()
				.getImage("icons\\search_32x32.png"));
		frame.setTitle("Extension Finder");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane()
				.setLayout(null);
	}

	private void addPathLabel() {
		JLabel lblPathToSearch = new JLabel("Path to search:");
		lblPathToSearch.setBounds(10, 15, 140, 14);
		frame.getContentPane()
				.add(lblPathToSearch);
	}

	private void addExtensionLabel() {
		JLabel lblExtensionToFind = new JLabel("Extension to find:");
		lblExtensionToFind.setBounds(10, 61, 140, 14);
		frame.getContentPane()
				.add(lblExtensionToFind);
	}

	private void addResultLabel() {
		JLabel lblResult = new JLabel("Result:");
		lblResult.setBounds(359, 15, 80, 14);
		frame.getContentPane()
				.add(lblResult);
	}

	private void addErrorLabel() {
		JLabel lblErrorLog = new JLabel("Error log:");
		lblErrorLog.setBounds(10, 431, 140, 14);
		frame.getContentPane()
				.add(lblErrorLog);
	}

	private void addPathTextField() {
		fieldPath = new JTextField();
		fieldPath.setBounds(10, 30, 310, 20);
		frame.getContentPane()
				.add(fieldPath);
		fieldPath.setColumns(10);
	}

	private void addExtensionTextField() {
		fieldExtension = new JTextField();
		fieldExtension.setBounds(10, 75, 100, 20);
		frame.getContentPane()
				.add(fieldExtension);
		fieldExtension.setColumns(10);
	}

	private void addSearchButton() {
		JButton btnRunSearch = new JButton("Run search");
		btnRunSearch.setBounds(10, 124, 100, 23);
		btnRunSearch.addActionListener(event -> actionOnButtonPress(event));
		Container btnCont = frame.getContentPane();
		btnCont.add(btnRunSearch);
	}

	private void actionOnButtonPress(ActionEvent event) {
		String path = fieldPath.getText();
		String extension = fieldExtension.getText();
		setSearchResult(path, extension);
		fillResultScrollPane();
	}

	private void addResultCountField() {
		fieldResultCount = new JTextField();
		fieldResultCount.setEditable(false);
		fieldResultCount.setBounds(406, 13, 50, 17);
		frame.getContentPane()
				.add(fieldResultCount);
		fieldResultCount.setColumns(10);
	}

	private void addResultScrollPane() {
		resultScrollPane = new JScrollPane();
		resultScrollPane.setBounds(359, 30, 895, 640);
		frame.getContentPane()
				.add(resultScrollPane);
	}

	private void addErrorLogTextArea() {
		textAreaErrorLog = new TextArea();
		textAreaErrorLog.setBounds(10, 451, 310, 219);
		Font newFont = makeNewFont("font_types\\consola.ttf", (float) 10);
		textAreaErrorLog.setFont(newFont);
		frame.getContentPane()
				.add(textAreaErrorLog);
	}

	private void addSeparators() {
		JSeparator searchSeparator = new JSeparator();
		searchSeparator.setBounds(10, 111, 310, 2);
		frame.getContentPane()
				.add(searchSeparator);

		JSeparator errorSeparator = new JSeparator();
		errorSeparator.setBounds(10, 418, 310, 2);
		frame.getContentPane()
				.add(errorSeparator);

	}

	private void fillResultScrollPane() {
		DefaultMutableTreeNode resultDirectoriesTreeNode;
		if (searchResult != null && !searchResult.isEmpty()) {
			resultDirectoriesTreeNode = new DefaultMutableTreeNode("Result Directories");
			int fileCount = 0;
			for (String currentDir : searchResult.keySet()) {
				DefaultMutableTreeNode currentDirTreeNode = new DefaultMutableTreeNode(currentDir);
				ArrayList<String> fileNames = searchResult.get(currentDir);
				for (String fileName : fileNames) {
					DefaultMutableTreeNode fileNameTreeNode = new DefaultMutableTreeNode(fileName);
					currentDirTreeNode.add(fileNameTreeNode);
					fileCount++;
				}
				resultDirectoriesTreeNode.add(currentDirTreeNode);
			}
			fieldResultCount.setText(Integer.toString(fileCount));
			resultScrollPane.setViewportView(new JTree(resultDirectoriesTreeNode));
		} else {
			resultDirectoriesTreeNode = new DefaultMutableTreeNode("No Files Found");
			resultScrollPane.setViewportView(new JTree(resultDirectoriesTreeNode));
		}
	}

	private void setSearchResult(String path, String extension) {
		try {
			FileExtensionFinder extensionFinder = new FileExtensionFinder(path, extension);
			searchResult = extensionFinder.getMappedFiles();
		} catch (Error e) {
			textAreaErrorLog.append(e.getMessage() + "\n");
		}
		fillResultScrollPane();
	}

	private Font makeNewFont(String pathToFontStyle, float fontSize) {
		File fontFile = new File(pathToFontStyle);
		Font newFont = null;
		try {
			newFont = Font.createFont(0, fontFile)
					.deriveFont(fontSize);
		} catch (FontFormatException e) {
			textAreaErrorLog.append(e.getMessage());
		} catch (IOException e) {
			textAreaErrorLog.append(e.getMessage());
		}
		return newFont;
	}
}
