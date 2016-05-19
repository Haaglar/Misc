import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
//A basic XPath tester
//Results separated with newline
public class XPathTestGUI {

	private JFrame frmXpathtestgui;
	private JMenu mnFileMenu;
	private JMenuItem mntmOpen;
	private JMenuItem mntmExit;
	private JScrollPane scrollPaneInput;
	private JScrollPane scrollPaneOutput;
	private JTextArea textAreaInput;
	private JTextField textFieldQuery;
	private JTextArea textAreaOutput;
	private final JFileChooser fileChooser;
	private JButton btnGo;
	
	//XML
	private DocumentBuilderFactory documentBuilderFactory;
	private DocumentBuilder documentBuilder;
	private XPathFactory xPathFactory;
	private XPath xPath;
	private JButton buttonFill;
	private JTextField textFieldXPathOutput;
	private JLabel lblXpathOutput;
	private JLabel lblInputxml;
	private JLabel lblNewLabel;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					XPathTestGUI window = new XPathTestGUI();
					window.frmXpathtestgui.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public XPathTestGUI() {
		fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileFilter() {
			
			@Override
			public String getDescription() {
				return "XML Files (*.xml)";
			}
			
			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
			           return true;
			       } else {
			           String filename = f.getName().toLowerCase();
			           return filename.endsWith(".xml");
			       }
			}
		});
		initializeXML();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmXpathtestgui = new JFrame();
		frmXpathtestgui.setTitle("XPathTestGUI");
		frmXpathtestgui.setResizable(false);
		frmXpathtestgui.setBounds(100, 100, 768, 576);
		frmXpathtestgui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmXpathtestgui.getContentPane().setLayout(null);
		
		initializeMenu();
		initializeInputArea();
		
		textAreaOutput = new JTextArea();
		textAreaOutput.setEditable(false);
		scrollPaneOutput = new JScrollPane(textAreaOutput);
		scrollPaneOutput.setBounds(384, 83, 347, 422);
		frmXpathtestgui.getContentPane().add(scrollPaneOutput);
		
		btnGo = new JButton("Go ->");
		btnGo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String inputXML = textAreaInput.getText();
				String inputXPath = textFieldQuery.getText();
				if(inputXML.isEmpty() || inputXPath.isEmpty())
				{
					JOptionPane.showMessageDialog(null, "Query or XPath Missing");
					return;
				}
				try {
					Document doc = documentBuilder.parse(new InputSource(new StringReader(inputXML)));
					XPathExpression express = xPath.compile(inputXPath);
					NodeList nodeList = (NodeList) express.evaluate(doc, XPathConstants.NODESET);
					StringBuilder output = new StringBuilder();
					for(int i=0; i< nodeList.getLength(); i++)
					{
						Node node = nodeList.item(i);
						output.append(nodeToString(node));
						output.append(System.lineSeparator());
					}
					textAreaOutput.setText(output.toString());
					textFieldXPathOutput.setText(escapeStringToJava(inputXPath));
				} catch (SAXException | IOException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, "Failed to parse supplied xml data");
					e1.printStackTrace();
				} catch (XPathExpressionException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, "Failed to parse supplied XPath");
					e1.printStackTrace();
				} catch (TransformerException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, "Failed to convert result XML to string");
					e1.printStackTrace();
				}
				
			}
		});
		btnGo.setBounds(310, 138, 64, 23);
		frmXpathtestgui.getContentPane().add(btnGo);
		
		buttonFill = new JButton("<- Fill");
		buttonFill.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String text = textAreaOutput.getText();
				if(!text.isEmpty())
				{
					textAreaInput.setText(text);
					textAreaOutput.setText("");
				}
			}
		});
		buttonFill.setBounds(310, 172, 64, 23);
		frmXpathtestgui.getContentPane().add(buttonFill);
		
		JLabel lblXpath = new JLabel("XPath Input");
		lblXpath.setBounds(10, 16, 75, 20);
		frmXpathtestgui.getContentPane().add(lblXpath);
		
		textFieldXPathOutput = new JTextField();
		textFieldXPathOutput.setBounds(467, 16, 264, 20);
		frmXpathtestgui.getContentPane().add(textFieldXPathOutput);
		textFieldXPathOutput.setColumns(10);
		
		lblXpathOutput = new JLabel("XPath Output");
		lblXpathOutput.setBounds(382, 19, 75, 14);
		frmXpathtestgui.getContentPane().add(lblXpathOutput);
		
		lblInputxml = new JLabel("Input XML");
		lblInputxml.setBounds(10, 65, 290, 14);
		frmXpathtestgui.getContentPane().add(lblInputxml);
		
		lblNewLabel = new JLabel("Output XML");
		lblNewLabel.setBounds(384, 65, 347, 14);
		frmXpathtestgui.getContentPane().add(lblNewLabel);
		
	}
	private void initializeMenu() {
		JMenuBar menuBar = new JMenuBar();
		frmXpathtestgui.setJMenuBar(menuBar);
			
		mnFileMenu = new JMenu("File");
		menuBar.add(mnFileMenu);
		
		mntmOpen = new JMenuItem("Open");
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int res = fileChooser.showOpenDialog(null);
				if (res == JFileChooser.APPROVE_OPTION) {
				    File xmlFile = fileChooser.getSelectedFile();
				    try {
						Document doc = documentBuilder.parse(xmlFile);
						textAreaInput.setText(nodeToString(doc.getDocumentElement()));
					} catch (SAXException | IOException | TransformerException e1) {
						JOptionPane.showMessageDialog(null, "Failed to read xml data");
					}
				}
			}
		});
		mnFileMenu.add(mntmOpen);
		
		mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnFileMenu.add(mntmExit);
	}
	
	private void initializeInputArea() {

		textAreaInput = new JTextArea();
		scrollPaneInput = new JScrollPane(textAreaInput);
		scrollPaneInput.setBounds(10, 83, 290, 422);
		frmXpathtestgui.getContentPane().add(scrollPaneInput);
		
		textFieldQuery = new JTextField();
		textFieldQuery.setBounds(82, 16, 218, 20);
		frmXpathtestgui.getContentPane().add(textFieldQuery);
		textFieldQuery.setColumns(10);
	
		
	}
	
	private void initializeXML()
	{
		documentBuilderFactory = DocumentBuilderFactory.newInstance();
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		xPathFactory = XPathFactory.newInstance();
		xPath = xPathFactory.newXPath();
	}
	
	
	//Yay Stack overflow
	private String nodeToString(Node node) throws TransformerException
	{
	  StringWriter sw = new StringWriter();
	  Transformer t = TransformerFactory.newInstance().newTransformer();
	  t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	  t.transform(new DOMSource(node), new StreamResult(sw));
	  return sw.toString();
	}
	
	//Modified version of escapeJavaStyleString from Apache commons
	//Modified to use string builder, removed unused parameters and return a String instead
	//Licensed under Apache license 2.0
	private static String escapeStringToJava(String str){
	    if (str == null) {
	        return "";
	    }
	    int sz;
	    sz = str.length();
	    StringBuilder output = new StringBuilder();
	    for (int i = 0; i < sz; i++) 
	    {
	        char ch = str.charAt(i);
	
	        // handle unicode
	        if (ch > 0xfff) {
	            output.append("\\u" + hex(ch));
	        } else if (ch > 0xff) {
	            output.append("\\u0" + hex(ch));
	        } else if (ch > 0x7f) {
	            output.append("\\u00" + hex(ch));
	        } else if (ch < 32) {
	            switch (ch) {
	                case '\b' :
	                    output.append('\\');
	                    output.append('b');
	                    break;
	                case '\n' :
	                    output.append('\\');
	                    output.append('n');
	                    break;
	                case '\t' :
	                    output.append('\\');
	                    output.append('t');
	                    break;
	                case '\f' :
	                    output.append('\\');
	                    output.append('f');
	                    break;
	                case '\r' :
	                    output.append('\\');
	                    output.append('r');
	                    break;
	                default :
	                    if (ch > 0xf) {
	                        output.append("\\u00" + hex(ch));
	                    } else {
	                        output.append("\\u" + hex(ch));
	                    }
	                    break;
	            }
	        } else {
	            switch (ch) {
	                case '"' :
	                    output.append('\\');
	                    output.append('"');
	                    break;
	                case '\\' :
	                    output.append('\\');
	                    output.append('\\');
	                    break;
	                default :
	                    output.append(ch);
	                    break;
	            }
	        }
	    }
	    return output.toString();
    }
	//Taken from apache commons
	//Licensed under Apache license 2.0
    private static String hex(char ch)
    {
        return Integer.toHexString(ch).toUpperCase(Locale.ENGLISH);
    }
}
