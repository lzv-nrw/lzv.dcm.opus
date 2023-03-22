package opus;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.*;

/**
 * Generate Gui
 * 
 * @author ogan
 *
 */
public class ClientGui {
	
	JFrame guiFrame;
	
	Color panelColor = new Color(219, 244, 255);
	Color tabColor = new Color(240, 130, 35);
	URL urlBgImg, urlIcImg;
	ImageIcon backgroundImg, iconImg;
	
	static GridBagConstraints constraints;
	GridBagLayout layout;
	
	JButton getMetadataButton, openXMLDirButton, createBagitButton, createMetsButton,  openBagitDirButton, openMetsDirButton;
	JCheckBox bagSelectAllId, metsSelectAllId;
	JFileChooser chooseFolder;
	JLabel labelOPUS, labelResultXML, labelResultBagits, labelResultMets, labelSelectOpusId, labelSelectChecksum;
	JPanel extractPanel, bagitPanel, metsPanel;
	JTabbedPane tabPane;
	JTextField bagOpusIdField, metsOpusIdField;

	String[] arrayOPUS;
	JComboBox<String> instancesOPUS, instancesBagit, instancesMETS, selectChecksum;
	
	static int selectedIndex;
	static String[] selectedInstance;
	String selectedChecksum;
	File xmlDirectory, bagitDirectory, metsDirectory;
	String xmlDirectoryPath, bagitDirectoryPath, bagitPath, metsPath;

	public void generateGui() throws Exception {

		layout = new GridBagLayout();
		constraints = new GridBagConstraints();
		
		// Create Frame	
		guiFrame = new JFrame("OPUS extractor & BagIt creator");
		
		urlBgImg = ClientGui.class.getResource("/conf/background.png");
		backgroundImg = new ImageIcon(urlBgImg);
		
		urlIcImg = ClientGui.class.getResource("/conf/icon.png");
		iconImg = new ImageIcon(urlIcImg);
		
		guiFrame.setIconImage(iconImg.getImage());
		guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		guiFrame.setSize(600,420);
		guiFrame.setResizable(false);
		guiFrame.setLocationRelativeTo(null);
		guiFrame.getContentPane().setBackground(panelColor);
		
		// Set Font
		Font normalFont = new Font(Font.SANS_SERIF, Font.BOLD,  12);
		Font bigFont = new Font(Font.SANS_SERIF, Font.BOLD,  13);
		Font biggerFont = new Font(Font.SANS_SERIF, Font.BOLD,  14);

		UIManager.put("Label.font", biggerFont);
		UIManager.put("TabbedPane.font", bigFont);
		UIManager.put("ComboBox.font", normalFont);
		UIManager.put("Button.font", bigFont);
		UIManager.put("CheckBox.font", bigFont);
		UIManager.put("TextField.font", bigFont);

		// Create panels
		this.extractPanel();
		this.bagitPanel();
		this.metsPanel();
        		
		// Create JTabbedPane
		UIManager.put("TabbedPane.selected", tabColor);
		tabPane = new JTabbedPane(JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT );

		// Add JPanels as tabs
        tabPane.addTab("Extrahiere Metadaten aus OPUS", extractPanel);
        tabPane.addTab("Erstelle BagIts", bagitPanel);
        //tabPane.addTab("Erstelle Limited-METS", metsPanel);
		guiFrame.add(tabPane);

        // Visibility of the frame
		guiFrame.setVisible(true);
		guiFrame.repaint();
	}
	
	// Panel for extracting Metadata
	@SuppressWarnings("serial")
	public void extractPanel() throws Exception {

		constraints = new GridBagConstraints();
		layout = new GridBagLayout();
		extractPanel = new JPanel() {
			protected void paintComponent(Graphics g)
			{
				g.drawImage((backgroundImg).getImage(), -40, -95, null);
				super.paintComponent(g);
			}
		};
		extractPanel.setOpaque(false);
		layout.setConstraints(extractPanel, constraints);
		extractPanel.setLayout(layout);
		// ComboBox with Label for OPUS instances
		labelOPUS = new JLabel("Bitte wählen Sie eine OPUS Instanz aus");    
		labelOPUS.setHorizontalAlignment(JLabel.CENTER);
		labelOPUS.setVerticalAlignment(JLabel.BOTTOM);

		ClientGui.setContrain(0, 0, 0, 10, 2, 4);
		constraints.insets = new Insets(-60, 0, 0, 0);
		extractPanel.add(labelOPUS, constraints);
		
		arrayOPUS = ClientGui.instanceNames();
		instancesOPUS = new JComboBox<String>(arrayOPUS);
		instancesOPUS.setBackground(Color.WHITE);

		ClientGui.setContrain(0, 1, 0, 10, 1, 4);
		constraints.weighty = 0;
		constraints.insets = new Insets(0, 0, 0, 0);
		extractPanel.add(instancesOPUS, constraints);

		// Button for summit ComboBox
		getMetadataButton = new JButton("Extrahieren");
		ClientGui.setContrain(1, 1, 10, 10, 1, 4);
		constraints.insets = new Insets(0, 10, 0, 0);
		extractPanel.add(getMetadataButton, constraints);

		// Label for XML result
		labelResultXML = new JLabel("Ergebnis: ");
		labelResultXML.setHorizontalAlignment(JLabel.CENTER);
		labelResultXML.setVerticalAlignment(JLabel.TOP);

		ClientGui.setContrain(0, 2, 5, 5, 2, 4);
		constraints.insets = new Insets(60, 0, 0, 0);
		extractPanel.add(labelResultXML, constraints);
		
		// Button for open XML directory
		openXMLDirButton = new JButton(" ");
		openXMLDirButton.setBackground(panelColor);
		openXMLDirButton.setForeground(panelColor);
		openXMLDirButton.setBorderPainted(false);
		openXMLDirButton.setEnabled(false);
		
		ClientGui.setContrain(0, 3, 5, 10, 2, 4);
		constraints.insets = new Insets(90, 0, 0, 0);
		extractPanel.add(openXMLDirButton, constraints);	
		
		// Button action for extract
		getMetadataButton.addActionListener(new ExtractListner());
		// Button action for open XML directory
		openXMLDirButton.addActionListener(new XmlDirectoryListner());	
	}
	
	// Button action for extract
	class ExtractListner implements ActionListener{
		public void actionPerformed(ActionEvent e){
        	selectedIndex = instancesOPUS.getSelectedIndex() ;
        	extractPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        	try {
				selectedInstance = ClientGui.instanceValues(selectedIndex);
	        	// Extract metadata from Opus and save in file opusMetaData_i.xml
				GetMetaData.getRequest(selectedInstance[1], selectedInstance[0]);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		ClientGui.setLook();
    		labelResultXML.setText("Ergebnis: XML-Dateien mit Metadaten erfolgreich erstellt. "); 
    		openXMLDirButton.setText("XML-Verzeichnis öffnen");
    		openXMLDirButton.setBackground(null);
    		openXMLDirButton.setForeground(Color.black);
    		openXMLDirButton.setBorderPainted(true);
    		openXMLDirButton.setEnabled(true);	
    		extractPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}

	// Button action for open XML directory
	class XmlDirectoryListner implements ActionListener{
		public void actionPerformed(ActionEvent e){
			try {
				selectedInstance = ClientGui.instanceValues(selectedIndex);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String dirInstance = selectedInstance[0];
        	File directory = new File("opus_resources\\" + dirInstance + "\\metadata");
    	    //Open directory
        	Desktop desktop = null;
        	try {
        	      if (Desktop.isDesktopSupported()) {
        	         desktop = Desktop.getDesktop();
        	         desktop.open(directory);
        	      }
        	      else {
        	         System.out.println("Desktop is not supported");
        	      }
        	}	
        	catch (IOException e2){  }
		}
	}
	
	// Panel for creating BagIts
	@SuppressWarnings("serial")
	public void bagitPanel() throws Exception {
		constraints = new GridBagConstraints();
		layout = new GridBagLayout();
        bagitPanel = new JPanel() {
			protected void paintComponent(Graphics g)
			{
				g.drawImage((backgroundImg).getImage(), -40, -95, null);
				super.paintComponent(g);
			}
		};
		bagitPanel.setOpaque(false);
		layout.setConstraints(bagitPanel, constraints);
		bagitPanel.setLayout(layout);
		
		// ComboBox with Label for OPUS instances	
		labelOPUS = new JLabel("Bitte wählen Sie eine OPUS Instanz aus");    
		labelOPUS.setHorizontalAlignment(JLabel.CENTER);
		labelOPUS.setVerticalAlignment(JLabel.BOTTOM);

		ClientGui.setContrain(1, 0, 0, 10, 3, 6);
		constraints.insets = new Insets(-20, 0, 0, 0);
		bagitPanel.add(labelOPUS, constraints);
		
		arrayOPUS = ClientGui.instanceNames();
		instancesBagit = new JComboBox<String>(arrayOPUS);
		instancesBagit.setBackground(Color.WHITE);
		
		ClientGui.setContrain(1, 1, 0, 10, 3, 6);
		constraints.weighty = 0;
		constraints.insets = new Insets(20, 0, 0, 0);
		bagitPanel.add(instancesBagit, constraints);
		
		// Label for selecting Opus-Id
		labelSelectOpusId = new JLabel("OPUS-Id: ");
		
		ClientGui.setContrain(1, 2, 0, 10, 3, 6);
		constraints.insets = new Insets(70, -140, 0, 0);
		bagitPanel.add(labelSelectOpusId, constraints);		
		
		// TextField to select Opus-Id
		bagOpusIdField = new JTextField(5);

		ClientGui.setContrain(2, 2, 0, 10, 3, 6);
		constraints.insets = new Insets(70, -10, 0, 0);
		bagOpusIdField.setEditable(true);
		bagitPanel.add(bagOpusIdField, constraints);
		
		// CheckBox to select all Opus-Id
		bagSelectAllId = new JCheckBox("Alle");
		bagSelectAllId.setBackground(Color.white);
		bagSelectAllId.setEnabled(true);
		bagSelectAllId.setOpaque(true);
		
		ClientGui.setContrain(3, 2, 0, 5, 3, 6);
		constraints.insets = new Insets(70, 120, 0, 0);
		bagitPanel.add(bagSelectAllId, constraints);
		
		// Label for selecting checksum
		labelSelectChecksum = new JLabel("Checksummen-Verfahren: ");

		ClientGui.setContrain(2, 3, 0, 10, 3, 6);
		constraints.insets = new Insets(120, -240, 0, 0);
		bagitPanel.add(labelSelectChecksum, constraints);			
		
		// ComboBox for checksum
		String[] arrayChecksum = {"SHA1", "SHA256", "MD5"};
		selectChecksum = new JComboBox<String>(arrayChecksum);
		selectChecksum.setBackground(Color.WHITE);

		ClientGui.setContrain(1, 3, 0, 10, 3, 6);
		constraints.insets = new Insets(120, 40, 0, 0);
		bagitPanel.add(selectChecksum, constraints);
		
		// Button for create BagIts
		createBagitButton = new JButton("BagIts erstellen");
		
		ClientGui.setContrain(3, 3, 0, 10, 3, 6);
		constraints.insets = new Insets(120, 290, 0, 0);
		createBagitButton.setEnabled(true);
		bagitPanel.add(createBagitButton, constraints);
		
		// Label for BagIt result
		labelResultBagits = new JLabel("Ergebnis: ");
		labelResultBagits.setHorizontalAlignment(JLabel.CENTER);
		labelResultBagits.setVerticalAlignment(JLabel.TOP);
		labelResultBagits.setForeground(null);

		ClientGui.setContrain(1, 5, 0, 10, 3, 6);
		constraints.insets = new Insets(180, 0, 0, 0);
		bagitPanel.add(labelResultBagits, constraints);	
		
		// Button for open BagIt directory
		openBagitDirButton = new JButton(" ");
		openBagitDirButton.setBackground(panelColor);
		openBagitDirButton.setForeground(panelColor);
		openBagitDirButton.setBorderPainted(false);
		openBagitDirButton.setEnabled(false);

		ClientGui.setContrain(1, 6, 0, 10, 3, 6);
		constraints.insets = new Insets(200, 0, 0, 0);
		bagitPanel.add(openBagitDirButton, constraints);
		
		// Button action for creating BagIts
		createBagitButton.addActionListener(new BagitListner());
		// Button action for open BagIt directory
		openBagitDirButton.addActionListener(new BagitDirectoryListner());
	}
	
	class BagitListner implements ActionListener{
		public void actionPerformed(ActionEvent e){
        	bagitPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        	selectedIndex = instancesBagit.getSelectedIndex();
        	try {
				selectedInstance = ClientGui.instanceValues(selectedIndex);
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
        	String dir = selectedInstance[0];
			
        	bagitPath = "opus_resources\\" + dir + "\\bagits";
			
			// Get number of XML-files
        	String xmlPath = "opus_resources\\" + dir + "\\metadata"; 
        	xmlDirectory = new File(xmlPath);
			String xmlFiles [] = xmlDirectory.list();
			
			//  Get value of opusIdField
			String opusId = bagOpusIdField.getText();
			if (opusId.equals("") && bagSelectAllId.isSelected()){
				opusId = null;
			}
			
			selectedIndex = selectChecksum.getSelectedIndex() ;
			String[] arrayChecksum = {"SHA1", "SHA256", "MD5"};
			selectedChecksum = arrayChecksum[selectedIndex];

			// Creates BagIts with help of the XML-files, one or all
			try {
				for (int i = 0; i < xmlFiles.length; i++ ) {
				
					File test = null;
					DownloadFile.download(dir, opusId, i);
					BagInfo.writeBagInfo(dir, opusId, i);
					BagitTxt.bagitText(dir, opusId, i);
					FileChecksum.generateFileChecksum(dir, opusId, i, selectedChecksum);
					
					if (opusId != null) {
						test = new File(bagitPath + "\\opus_" + opusId);
					}
					if(opusId == null || test.exists() ) {
						ClientGui.setLook();
						labelResultBagits.setForeground(Color.black);
						bagitDirectory = new File(bagitPath);	
						openBagitDirButton.setText("BagIt-Verzeichnis öffnen");
						openBagitDirButton.setBackground(null);
						openBagitDirButton.setForeground(Color.black);
						openBagitDirButton.setBorderPainted(true);
						openBagitDirButton.setEnabled(true);
						if (opusId == null) {
							labelResultBagits.setText("Ergebnis: BagIts erfolgreich erstellt.");
						} 
						else {						
							labelResultBagits.setText("Ergebnis: BagIt von OPUS-Id " + opusId + " erfolgreich erstellt.");
						}
					}
					else {
						labelResultBagits.setForeground(Color.red);
						labelResultBagits.setText("Ergebnis: Bitte Eingabe überprüfen.");
					}
				}        	
			} catch (Exception e1) {
				labelResultBagits.setForeground(Color.red);
				labelResultBagits.setText("Ergebnis: Bitte Eingabe überprüfen.");
				e1.printStackTrace();
			} 
			bagitPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	class BagitDirectoryListner implements ActionListener{
		public void actionPerformed(ActionEvent e){		
    	    //Open directory
        	Desktop desktop = null;
        	try {
        	      if (Desktop.isDesktopSupported()) {
        	         desktop = Desktop.getDesktop();
        	         desktop.open(bagitDirectory);
        	      }
        	      else {
        	         System.out.println("Desktop is not supported");
        	      }
        	}	
        	catch (IOException e2){  }
		}
	}
	
	// Panel for creating METS
	@SuppressWarnings("serial")
	public void metsPanel() throws Exception {
		constraints = new GridBagConstraints();
		layout = new GridBagLayout();
		metsPanel = new JPanel() {
			protected void paintComponent(Graphics g)
			{
				g.drawImage((backgroundImg).getImage(), -40, -95, null);
				super.paintComponent(g);
			}
		};
		metsPanel.setOpaque(false);
		layout.setConstraints(metsPanel, constraints);
		metsPanel.setLayout(layout);
			
		// ComboBox with Label for OPUS instances	
		labelOPUS = new JLabel("Bitte wählen Sie eine OPUS Instanz aus");    
		labelOPUS.setHorizontalAlignment(JLabel.CENTER);
		labelOPUS.setVerticalAlignment(JLabel.BOTTOM);

		ClientGui.setContrain(1, 0, 0, 10, 3, 4);
		constraints.insets = new Insets(-80, 0, 0, 0);
		metsPanel.add(labelOPUS, constraints);
		
		arrayOPUS = ClientGui.instanceNames();
		instancesMETS = new JComboBox<String>(arrayOPUS);
		instancesMETS.setBackground(Color.WHITE);
		
		ClientGui.setContrain(1, 1, 0, 10, 3, 4);
		constraints.weighty = 0;
		constraints.insets = new Insets(-15, 0, 0, 0);
		metsPanel.add(instancesMETS, constraints);
		
		// Label for selecting Opus-Id
		labelSelectOpusId = new JLabel("OPUS-Id: ");
		
		ClientGui.setContrain(0, 2, 0, 10, 3, 4);
		constraints.insets = new Insets(30, 70, 0, 0);
		metsPanel.add(labelSelectOpusId, constraints);		
		
		// TextField to select Opus-Id
		metsOpusIdField = new JTextField(5);

		ClientGui.setContrain(1, 2, 0, 10, 3, 4);
		constraints.insets = new Insets(30, -210, 0, 0);
		metsOpusIdField.setEditable(true);
		metsPanel.add(metsOpusIdField, constraints);
		
		// CheckBox to select all Opus-Id
		metsSelectAllId = new JCheckBox("Alle");
		metsSelectAllId.setBackground(Color.white);
		metsSelectAllId.setEnabled(true);
		metsSelectAllId.setOpaque(true);
		
		ClientGui.setContrain(2, 2, 0, 5, 3, 4);
		constraints.insets = new Insets(30, -80, 0, 0);
		metsPanel.add(metsSelectAllId, constraints);
		
		// Button for create METS
		createMetsButton = new JButton("METS erstellen");
		
		ClientGui.setContrain(1, 2, 0, 10, 3, 4);
		constraints.insets = new Insets(30, 290, 0, 0);
		createMetsButton.setEnabled(true);
		metsPanel.add(createMetsButton, constraints);
		
		// Label for METS result
		labelResultMets = new JLabel("Ergebnis: ");
		labelResultMets.setForeground(null);

		ClientGui.setContrain(1, 4, 0, 10, 3, 4);
		constraints.insets = new Insets(90, 0, 0, 0);
		metsPanel.add(labelResultMets, constraints);	
		
		// Button for open METS directory
		openMetsDirButton = new JButton(" ");
		openMetsDirButton.setBackground(panelColor);
		openMetsDirButton.setForeground(panelColor);
		openMetsDirButton.setBorderPainted(false);
		openMetsDirButton.setEnabled(false);

		ClientGui.setContrain(1, 5, 0, 10, 3, 4);
		constraints.insets = new Insets(110, 0, 0, 0);
		metsPanel.add(openMetsDirButton, constraints);

		// Button action for creating METS
		createMetsButton.addActionListener(new MetsListener()); 		
		// Button action for open METS directory
		openMetsDirButton.addActionListener(new MetsDirectoryListner());
	}
	
	class MetsListener implements ActionListener{
		public void actionPerformed(ActionEvent e){	
			metsPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        	selectedIndex = instancesMETS.getSelectedIndex();
        	try {
				selectedInstance = ClientGui.instanceValues(selectedIndex);
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			//  Get value of opusIdField
			String opusId = metsOpusIdField.getText();
			if (opusId.equals("") && metsSelectAllId.isSelected()){
				opusId = null;
			}
								
			// Creates METS
			try {				
			CopyFilesMETS.copyFiles(selectedInstance[0], opusId);
			BuildMets.generateMets(selectedInstance[0], opusId);
			
			ClientGui.setLook();
			labelResultMets.setForeground(Color.black);
			openMetsDirButton.setText("METS-Verzeichnis öffnen");
			openMetsDirButton.setBackground(null);
			openMetsDirButton.setForeground(Color.black);
			openMetsDirButton.setBorderPainted(true);
			openMetsDirButton.setEnabled(true);
			if (opusId == null && metsSelectAllId.isSelected()) {
				labelResultMets.setText("Ergebnis: Limited-METS erfolgreich erstellt.");	
			} 
			else {
				labelResultMets.setText("Ergebnis: Limited-METS von OPUS-Id " + opusId + " erfolgreich erstellt.");
			}
			
			metsPath = "opus_resources\\" + selectedInstance[0] + "\\mets\\";
			metsDirectory = new File(metsPath);
			
			} catch (Exception e1) {
				labelResultMets.setForeground(Color.red);
				labelResultMets.setText("Ergebnis: Bitte Eingabe überprüfen.");
				e1.printStackTrace();
			}
			metsPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	class MetsDirectoryListner implements ActionListener{
		public void actionPerformed(ActionEvent e){		
    	    //Open directory
        	Desktop desktop = null;
        	try {
        	      if (Desktop.isDesktopSupported()) {
        	         desktop = Desktop.getDesktop();
        	         desktop.open(metsDirectory);
        	      }
        	      else {
        	         System.out.println("Desktop is not supported");
        	      }
        	}	
        	catch (IOException e2){  }
		}
	}
	
	// Load properties file
	public static LinkedHashMap<String,String> loadProperties() throws Exception {
        LinkedHashMap<String,String> lhMap = new LinkedHashMap<>();
       
        InputStreamReader isr = new InputStreamReader(ClientGui.class.getResourceAsStream("/conf/config.properties"), "UTF8");
		BufferedReader br = new BufferedReader(isr);
		String line = null;
		while((line=br.readLine())!=null) {
			String[] keyValue = line.split("=");
			lhMap.put(keyValue[0], keyValue[1]);
		}
		br.close();
		return lhMap;
	}
	
	/**
	 * @param index
	 * @return instanceValue array: dir + url for instances
	 * @throws Exception
	 */
	public static String[] instanceValues(int index) throws Exception {
		
		LinkedHashMap<String,String> lhMap = ClientGui.loadProperties();
		
		ArrayList<String> dirList = new ArrayList<String>();
		ArrayList<String> urlList = new ArrayList<String>();
	
		for (String key : lhMap.keySet()) {
			dirList.add(key);
			urlList.add(StringCutter.cutFront(lhMap.get(key), "_", 1));			
		}
		String[] instanceValue = {dirList.get(index), urlList.get(index)};
		return instanceValue;
	}
	
	// Get names for instances from properties file
	public static String [] instanceNames() throws Exception {
		
		LinkedHashMap<String,String> lhMap = ClientGui.loadProperties();
		
		ArrayList<String> nameList = new ArrayList<String>();
	
		for (String key : lhMap.keySet()) {
			nameList.add(StringCutter.cutBack(lhMap.get(key), "_", 1));			
		}
		String[] namesArr = new String[nameList.size()];
		namesArr = nameList.toArray(namesArr);
		return namesArr;
	}
	
	public static void setLook() {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	// Method to set constraints values
	public static GridBagConstraints setContrain(int gridx, int gridy, int ipadx, int ipady, int gridWidth, int gridheight) {
		constraints = new GridBagConstraints();
		constraints.gridx = gridx;
		constraints.gridy = gridy;
		constraints.ipadx = ipadx;
		constraints.ipady = ipady;
		constraints.gridwidth = gridWidth;
		constraints.gridheight = gridheight;
		return constraints;
	}
}
