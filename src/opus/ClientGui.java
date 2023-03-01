package opus;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class ClientGui {
	
	JFrame guiFrame;
	
	Color barColor = new Color(25, 45, 70);
	Color panelColor = new Color(219, 244, 255);
	Color tabColor = new Color(240, 130, 35);
	URL urlBgImg, urlIcImg, urlLoadImg;
	ImageIcon backgroundImg, iconImg, loadImg;
	
	GridBagConstraints constraints;
	GridBagLayout layout;
	
	JButton getMetadaButton, openXMLDirButton, selectXMLDirButton, createBagitButton, openBagitDirButton;
	JCheckBox selectAllId;
	JFileChooser chooseFolder;
	JLabel labelOPUS, labelResultXML, labelResultBagits, labelSelectetXmlDir, labelSelectOpusId;
	JMenuBar bar;
	JPanel extractPanel, bagitPanel;
	JTabbedPane tabPane;
	JTextField opusIdField;

	String[] arrayOPUS;
	JComboBox<String> instancesOPUS, selectChecksum;
	
	static int selectedIndex;
	static String[] selectedInstance;
	String selectedChecksum;
	File xmlDirectory;
	File bagitDirectory;
	String xmlDirectoryPath;
	String bagitPath;

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
		
		// Create panels
		this.extractPanel();
		this.bagitPanel();
        		
		// Create JTabbedPane
		UIManager.put("TabbedPane.selected", tabColor);
		tabPane = new JTabbedPane(JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT );

		// Add JPanels as tabs
        tabPane.addTab("Extrahiere Metadaten aus OPUS", extractPanel);
        tabPane.addTab("Erstelle BagIts", bagitPanel);
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
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		constraints.ipady = 10;
		constraints.insets = new Insets(5, 0, 10, 0);
		extractPanel.add(labelOPUS, constraints);
		
		arrayOPUS = ClientGui.getOPUSArray();
		instancesOPUS = new JComboBox<String>(arrayOPUS);
		instancesOPUS.setBackground(Color.WHITE);
		constraints.gridwidth = 1;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.ipady = 10;
		constraints.weighty = 0;
		constraints.insets = new Insets(5, 5, 5, 5);
		extractPanel.add(instancesOPUS, constraints);

		// Button for summit ComboBox
		getMetadaButton = new JButton("Extrahieren");
		constraints.gridx = 1;
		constraints.gridy = 1;
		extractPanel.add(getMetadaButton, constraints);

		// Label for XML result
		labelResultXML = new JLabel("Ergebnis: ");
		labelResultXML.setHorizontalAlignment(JLabel.CENTER);
		labelResultXML.setVerticalAlignment(JLabel.TOP);
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 2;
		constraints.ipady = 5;
		constraints.weighty = 0;
		constraints.insets = new Insets(20, 0, 10, 0);
		extractPanel.add(labelResultXML, constraints);
		
		// Button for open XML directory
		openXMLDirButton = new JButton("");
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 2;
		constraints.ipady = 10;
		constraints.insets = new Insets(0, 0, 70, 0);
		openXMLDirButton.setBackground(panelColor);
		openXMLDirButton.setForeground(panelColor);
		openXMLDirButton.setBorderPainted(false);
		openXMLDirButton.setEnabled(false);
		extractPanel.add(openXMLDirButton, constraints);	
		
		
		
		// Button action for extract
		getMetadaButton.addActionListener(new java.awt.event.ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	selectedIndex = instancesOPUS.getSelectedIndex() ;
	        	selectedInstance = ClientGui.selectedInstance();
	        	// Extract metadata from Opus and save in file opusMetaData_i.xml
	    		try {
					GetMetaData.getRequest(selectedInstance[0], selectedInstance[1]);
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
	        }
	      });

		// Button action for open XML directory
		openXMLDirButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedInstance = ClientGui.selectedInstance();
				String dirInstance = selectedInstance[1];
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
		});		
	}
	
	// Panel for creating BagIts
	@SuppressWarnings("serial")
	public void bagitPanel() {
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
		
		// Button for directory select
		selectXMLDirButton = new JButton("XML-Verzeichnis wählen");
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		constraints.ipady = 10;
		constraints.gridwidth = 1;
		constraints.insets = new Insets(20, 0, 5, 0);
		bagitPanel.add(selectXMLDirButton, constraints);
		
		// Label for selecting Opus-Id
		labelSelectOpusId = new JLabel("OPUS-Id wählen: ");
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.ipadx = 0;
		constraints.ipady = 10;
		constraints.gridwidth = 2;
		constraints.insets = new Insets(0, 0, 0, 175);
		bagitPanel.add(labelSelectOpusId, constraints);		
		
		// TextField to select Opus-Id
		opusIdField = new JTextField(5);
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.ipady = 10;
		constraints.insets = new Insets(0, 0, 0, 0);
		opusIdField.setEditable(false);
		bagitPanel.add(opusIdField, constraints);
		
		// CheckBox to select Opus-Id
		selectAllId = new JCheckBox("Alle");
		constraints.gridx = 2;
		constraints.gridy = 2;
		constraints.gridwidth = 2;
		constraints.ipady = 5;
		constraints.insets = new Insets(20, -100, 20, 0);
		selectAllId.setBackground(Color.white);
		selectAllId.setEnabled(false);
		selectAllId.setOpaque(true);
		bagitPanel.add(selectAllId, constraints);
		
		// Label for selecting checksum
		labelSelectOpusId = new JLabel("Wähle Checksummen-Verfahren: ");
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.ipadx = 0;
		constraints.ipady = 10;
		constraints.insets = new Insets(0, 0, 0, 65);
		bagitPanel.add(labelSelectOpusId, constraints);			
		
		// ComboBox for checksum
		String[] arrayChecksum = {"SHA1", "SHA-256", "MD5"};
		selectChecksum = new JComboBox<String>(arrayChecksum);
		selectChecksum.setBackground(Color.WHITE);
		constraints.gridwidth = 1;
		constraints.gridx = 1;
		constraints.gridy = 3;
		constraints.ipady = 5;
		constraints.insets = new Insets(0, 250, 0, 0);
		bagitPanel.add(selectChecksum, constraints);
		
		// Button for create BagIts
		createBagitButton = new JButton("BagIts erstellen");
		constraints.gridx = 1;
		constraints.gridy = 4;
		constraints.gridwidth = 1;
		constraints.ipady = 10;
		constraints.insets = new Insets(20, 0, 20, 0);
		createBagitButton.setEnabled(false);
		bagitPanel.add(createBagitButton, constraints);
		
		// Label for BagIt result
		labelResultBagits = new JLabel("Ergebnis: ");
		labelResultBagits.setHorizontalAlignment(JLabel.CENTER);
		labelResultBagits.setVerticalAlignment(JLabel.TOP);
		labelResultBagits.setForeground(null);
		constraints.gridx = 1;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		constraints.insets = new Insets(0, 0, 0, 0);
		bagitPanel.add(labelResultBagits, constraints);	
		
		// Button for open BagIt directory
		openBagitDirButton = new JButton("");
		constraints.gridx = 1;
		constraints.gridy = 6;
		constraints.gridwidth = 2;
		constraints.ipady = 10;
		constraints.gridwidth = 1;
		constraints.insets = new Insets(10, 0, 20, 0);
		openBagitDirButton.setBackground(panelColor);
		openBagitDirButton.setForeground(panelColor);
		openBagitDirButton.setBorderPainted(false);
		openBagitDirButton.setEnabled(false);
		bagitPanel.add(openBagitDirButton, constraints);
		
		// Button action for select XML directory
		selectXMLDirButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String chooserTitle = "XML-Verzeichnis wählen";
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
						| UnsupportedLookAndFeelException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				chooseFolder = new JFileChooser(); 
				chooseFolder.setCurrentDirectory(new java.io.File("./opus_resources/"));
				chooseFolder.setDialogTitle(chooserTitle);
				chooseFolder.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooseFolder.setAcceptAllFileFilterUsed(true);
				
				if (chooseFolder.showOpenDialog(selectXMLDirButton) == JFileChooser.APPROVE_OPTION) {					
					xmlDirectory = chooseFolder.getSelectedFile();
					xmlDirectoryPath = xmlDirectory.getPath();
				}
				ClientGui.setLook();
				createBagitButton.setEnabled(true);
				opusIdField.setEditable(true);
				selectAllId.setEnabled(true);
			}
		});
		
		// Button action for creating BagIts
		createBagitButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {

				bagitPath = cutBack(xmlDirectoryPath, "metadata", 1) + "bagits";
				File bagitOutput = new File(bagitPath);
				bagitOutput.mkdirs();
				
				// Get number of XML-files
				int fileCount = xmlDirectory.list().length;
				
				//  Get value of opusIdField
				String opusId = opusIdField.getText();
				
				selectedIndex = selectChecksum.getSelectedIndex() ;
				selectedChecksum = arrayChecksum[selectedIndex];
				
				// Creates BagIts with help of the XML-files, one or all
				for (int i = 0; i < fileCount; i++ ) {
					if (!opusId.equals("")) {
						try {
							DownloadFile.downloadOne(opusId, i, xmlDirectoryPath, bagitPath);
							BagInfo.writeBagInfoOne(opusId, i, xmlDirectoryPath, bagitPath);
							BagitTxt.bagitTextOne(opusId, i, xmlDirectoryPath, bagitPath);
							FileChecksum.generateFileChecksumOne(opusId, i, selectedChecksum, xmlDirectoryPath, bagitPath);						
							File test = new File(bagitPath + "\\opus_" + opusId);
							if(test.exists()) {
								ClientGui.setLook();
								labelResultBagits.setText("Ergebnis: BagIt von OPUS-Id " + opusId + " erfolgreich erstellt.");
								labelResultBagits.setForeground(Color.black);
								bagitDirectory = new File(bagitPath);	
								openBagitDirButton.setText("BagIt-Verzeichnis öffnen und beenden");
								openBagitDirButton.setBackground(null);
								openBagitDirButton.setForeground(Color.black);
								openBagitDirButton.setBorderPainted(true);
								openBagitDirButton.setEnabled(true);
							}
							else {
								labelResultBagits.setText("Ergebnis: Bitte gültige OPUS-Id eingeben.");
							}
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else if(selectAllId.isSelected() && opusId.equals("")){
						try {
							DownloadFile.downloadAll(i, xmlDirectoryPath, bagitPath);
							BagInfo.writeBagInfoAll(i, xmlDirectoryPath, bagitPath);
							BagitTxt.bagitTextAll(i, xmlDirectoryPath, bagitPath);
							FileChecksum.generateFileChecksumAll(i, selectedChecksum, xmlDirectoryPath, bagitPath);
							ClientGui.setLook();
							labelResultBagits.setForeground(Color.black);
							bagitDirectory = new File(bagitPath);	
							openBagitDirButton.setText("BagIt-Verzeichnis öffnen und beenden");
							openBagitDirButton.setBackground(null);
							openBagitDirButton.setForeground(Color.black);
							openBagitDirButton.setBorderPainted(true);
							openBagitDirButton.setEnabled(true);
							labelResultBagits.setText("Ergebnis: BagIts erfolgreich erstellt.");	
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

				}

			}
		});
		
		// Button action for open BagIt directory
		openBagitDirButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
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
	        	guiFrame.dispose();
		       }
		});	
	}
	
	public void loadDialog() throws Exception {
		JDialog loadDialog = new JDialog();
		loadDialog.setLocationRelativeTo(null);
		loadDialog.setTitle("Dialog");
		loadDialog.setBackground(Color.white);
		loadDialog.setSize(250, 150);
		loadDialog.setVisible(true);
		urlLoadImg = ClientGui.class.getResource("/conf/loading.gif");
		loadImg = new ImageIcon(urlLoadImg);
		loadDialog.add(new JLabel(loadImg));
		loadDialog.setVisible(true);
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
	
	
	// Set url and dir for selected index
	public static String[] selectedInstance() {
		String url = null;
		String dir = null;
    	switch (selectedIndex) {
        case 0:
        	url = "https://hbz.opus.hbz-nrw.de/";
        	dir = "hbz";
            break;
        case 1:
        	url = "https://whge.opus.hbz-nrw.de/";
        	dir = "whge";
            break;
        case 2:
        	url = "https://kola.opus.hbz-nrw.de/";
        	dir = "kola";
            break;
        case 3:
        	url = "https://bast.opus.hbz-nrw.de/";
        	dir = "bast";
            break;
        case 4:
        	url = "https://opus.hfm-detmold.de/";
        	dir = "hfm-detmold";
            break;
        case 5:
        	url = "https://ubt.opus.hbz-nrw.de/";
        	dir = "ubt";
            break;
        case 6:
        	url = "https://hst.opus.hbz-nrw.de/";
        	dir = "hst";
            break;
    }
		String[] instance = {url , dir};
		return instance;
	}
	
	
	// Get list of instances 
	public static String[] getOPUSArray() throws Exception {
		
		String[] arrayOPUS = {"OPUS Repository des hbz",
				"OPUS Repository der Westfälischen Hochschule",
				"OPUS Universität Koblenz-Landau",
				"OPUS BASt",
				"OPUS Hochschulschriftenserver der Hochschule für Musik Detmold",
				"OPUS UB Trier",
				"OPUS Hochschule Trier"};
		
		return arrayOPUS;
	}
	
	// The method cut a given String returns a partial string starting from a certain position of a separator sign.
    public String cutBack(String text, String sign, int number) {
        for (int i = 0; i < number; i++) {
        	text = text.substring(0, text.lastIndexOf(sign));
        }
        return text;
    }
}
