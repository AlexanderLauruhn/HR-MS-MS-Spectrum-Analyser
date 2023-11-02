import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * This program helps to identify and to remove false-positive data from
 * high-resolution tandem mass spectra by calculating all theoretically possible
 * fragments based on the molecules atoms. This class contains the graphical user interface
 * and the programs logik.
 */

public class Main  extends JFrame{
	static Main main = new Main();
	private JTextField maxField= new JTextField(); //enter the sum formula of your compound here
	private JTextField minField= new JTextField(); //for  minimum sum formula
	private JTextField deviationTextField =new JTextField("1"); //enter the deviation allowed for comparison
	private JTextField chargeField= new JTextField("1"); //positively and negatively charges allowed
	private JButton calculateButton = new JButton("Calculate"); //start calculation of all fragments
	private JButton importButton = new JButton("import File"); // import mass spectra as csv or txt
	private JButton exportButton = new JButton("export File"); //export csv
	private JButton zoomInButton = new JButton("<- ->"); //zoom into ms spectrum
	private JButton zoomOutButton = new JButton("-> <-");// enlarge ms spectrum
	private JButton[] colorButtons = new JButton[16];// choose color for current action in spectrum
	private ActionListener actionListener = new ActionHandler(); //actionlistener for buttons
	private JTable fragmentTable = new JTable(), spectrumTable = new JTable(); //tables for
	private GraphicSpectrum graphic= new GraphicSpectrum();// shows the imported spectrum
	private YAxis yScale = new YAxis();//YAxis additional JPanel to not shift axis by zooming
	private JScrollPane scrollPaneFragmentTable = new JScrollPane(fragmentTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	private JScrollPane scrollSpectrumTable = new JScrollPane(spectrumTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	private JScrollPane scrollGraphik = new JScrollPane(graphic, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	private JLabel minLabel = new JLabel("Minimum");//Labels for textfield
	private JLabel maxLabel = new JLabel("Maximum"); //Label for textfield
	private JLabel deviationLabel = new JLabel("Deviation:");  //error in part per million, user input
	private JLabel chargeLabel = new JLabel("Charge");//Label for textfield
	private JLabel ionisation = new JLabel("Ionisation");
	private JLabel messageLabel = new JLabel(); // to show if data are incorrect
	private JLabel mzLabel = new JLabel("m/z"); //Label for x Axis
	private JLabel filenameLabel = new JLabel(); //Label for filename in spectrum
	private JRadioButton plusRadioButton = new JRadioButton("+"); //positive ionisation
	private JRadioButton minusRadioButton = new JRadioButton("-"); //negative ionisation
	private JLayeredPane layer = new JLayeredPane();
	private DefaultTableModel model = (DefaultTableModel) fragmentTable.getModel();
	private DefaultTableModel model2 = (DefaultTableModel) fragmentTable.getModel();
	private Border smallBorder = BorderFactory.createLineBorder (Color.BLACK, 0);
	private Border bigBorder = BorderFactory.createLineBorder (Color.BLACK, 3);
	private ButtonGroup plusminusButtonGroup = new ButtonGroup();//positive or negative ionisation
	private JCheckBox removeImplausiblesCheckBox = new JCheckBox("remove implausibles");
	private static int charge = 1; //multiple molecule charges (more than one additional electron)
	private static ArrayList<Molecule> allCompounds = new ArrayList<Molecule>();

	public Main() {
		//setup fragment table
        fragmentTable.setModel(new DefaultTableModel(  //setup tabel in columns
                new Object[][]{},
                new String[]{"Nr.", "formula", "mass"}));
        fragmentTable.getColumnModel().getColumn(0).setPreferredWidth(17);
        fragmentTable.setEnabled(false); //writing is not allowed
        fragmentTable.setPreferredScrollableViewportSize(new Dimension(450,63));
        fragmentTable.setFillsViewportHeight(true);
		//setup spectrumtable
        spectrumTable.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Nr.", "mass","rel. int. [%]", "corresponding", "deviation [ppm]"}));
        spectrumTable.getColumnModel().getColumn(0).setPreferredWidth(17);
        spectrumTable.setEnabled(false);
        spectrumTable.setPreferredScrollableViewportSize(new Dimension(450,63));
        spectrumTable.setFillsViewportHeight(true);
		//setup scrollbar
        getContentPane().add(scrollPaneFragmentTable, BorderLayout.CENTER);
        scrollPaneFragmentTable.setViewportView(fragmentTable);
        getContentPane().add(scrollSpectrumTable, BorderLayout.CENTER);
        model = (DefaultTableModel) fragmentTable.getModel();
        model2 = (DefaultTableModel) spectrumTable.getModel();
        scrollSpectrumTable.setViewportView(spectrumTable);

        deviationTextField.setBounds(340,90,80,30);
		deviationLabel.setBounds(270,90,80,30);
		messageLabel.setBounds(20,200,550,40);
		messageLabel.setFont(new Font("Arial Black", Font.PLAIN, 19));
		messageLabel.setVisible(false);
		layer.setBounds(10,250,550,350);
		//setup settings for ionisation
		chargeLabel.setBounds(270,140,80,30);
		chargeField.setBounds(340,140,80,30);
		ionisation.add(plusRadioButton);
		ionisation.add(minusRadioButton);
		plusminusButtonGroup.add(plusRadioButton);
		plusminusButtonGroup.add(minusRadioButton);
		removeImplausiblesCheckBox.setBounds(10,170,200,30);
		add(removeImplausiblesCheckBox);
		plusRadioButton.setToolTipText("positive ionisation mode");
		minusRadioButton.setToolTipText("negative ionisation mode");
		//setup spectrum
		zoomInButton.setBounds(10,5,70,30);
		zoomInButton.setBackground(Color.WHITE);
		zoomInButton.addActionListener(actionListener);
		zoomOutButton.setBackground(Color.WHITE);
		zoomOutButton.addActionListener(actionListener);
		zoomInButton.setFont(new Font("Consolas", Font.PLAIN, 12));
		zoomOutButton.setFont(new Font("Consolas", Font.PLAIN, 12));
		zoomOutButton.setBounds(90,5,70,30);
		//setup fields for molecule information
		minField.setBounds(80,10,150,30);
		minField.setToolTipText("choose the smallest fragment you are searching for");
		maxField.setBounds(80,50,150,30);
		maxField.setToolTipText("fill in your parent compound");
		minLabel.setBounds(10,10,90,30);
		maxLabel.setBounds(10,50,90,30);
		plusRadioButton.setBounds(280,40,50,30);
		plusRadioButton.setSelected(true);
		minusRadioButton.setBounds(340,40,50,30);
		ionisation.setBounds(280,10,90,30);
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				colorButtons[i * 4 + j]= new JButton();
				colorButtons[i * 4 + j].setBounds(400+j*25,10+i*18,25,18);
				colorButtons[i * 4 + j].setBorder(smallBorder);
				colorButtons[i * 4 + j].setBackground(setColorByValue(i*4+j));
				colorButtons[i * 4 + j].addActionListener(actionListener);
				colorButtons[i * 4 + j].setToolTipText("choose the color in which you want to show fitting signals");
				add(colorButtons[i * 4 + j]);
			}
		}		
		colorButtons[4].setBorder(bigBorder);
		calculateButton.setBounds(10,90,100,30);
		calculateButton.addActionListener(actionListener);
		calculateButton.setToolTipText("Start the calculation of all possible fragments");
		calculateButton.setBackground(Color.WHITE);
		//import and export of files by buttons
		importButton.setBounds(130,90,100,30);
		importButton.addActionListener(actionListener);
		importButton.setBackground(Color.WHITE);
		exportButton.setBounds(130,140,100,30);
		exportButton.addActionListener(actionListener);
		exportButton.setBackground(Color.WHITE);
		scrollPaneFragmentTable.setBounds(600,10,400,290);
		scrollSpectrumTable.setBounds(600,310,600,290);;
		add(scrollPaneFragmentTable);
		add(scrollSpectrumTable);
		add(maxField);
		add(minField);
		add(minLabel);
		add(maxLabel);
		add(plusRadioButton);
		add(minusRadioButton);
		add(calculateButton);
		add(ionisation);
		add(importButton);
		add(exportButton);
		add(deviationTextField);
		add(deviationLabel);
		add(chargeLabel);
		add(chargeField);
		scrollGraphik.setBounds(0,0,550,350);
		mzLabel.setBounds(265,285,30,30);
		yScale.setBounds(1,1,55,310);
		layer.add(scrollGraphik,JLayeredPane.DEFAULT_LAYER);
		layer.add(zoomInButton, JLayeredPane.DRAG_LAYER);
		layer.add(zoomOutButton, JLayeredPane.DRAG_LAYER);
		layer.add(mzLabel, JLayeredPane.DRAG_LAYER);
		layer.add(yScale, JLayeredPane.DRAG_LAYER);
		layer.add(filenameLabel,JLayeredPane.DRAG_LAYER);
		graphic.setPreferredSize(new Dimension(545, 350));
		add(messageLabel);
		add(layer);
		add(new JLabel());
		setFocusable(true);
		setTitle("HR-MS/MS Spectrum Cleaner");
		setSize(1250,650);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public static void main(String[] args) {}
	/**
	 * read the input from the textfield and converts the string to Molecule
	 * @param input
	 * @return
	 */
	public static ArrayList<Element> readInput(String input) {
		ArrayList<String> elements= new ArrayList<String>();
		int counter = 0;
		for (int i = 1; i < input.length(); i++) {
			if (Character.isUpperCase(input.toCharArray()[i])) {//each element start with upperCase letter
				elements.add(input.substring(counter,i));
				counter = i;
			}
		}
		elements.add(input.substring(counter,input.toCharArray().length));
		ArrayList<Element> formula = new ArrayList<Element>();
		for (String e : elements) {
			int elementCount = 1; //count the elements of molecule
			String elementName = "";
			for (int i = e.length() - 1; i > -1; i--) {
				if (!Character.isDigit(e.charAt(i)) && elementName == ""){
					elementName = e.substring(0, i + 1);}
				try {
					elementCount=Integer.parseInt(e.substring(i + 1, e.length()));
				}
				catch (NumberFormatException ignored){}
			}
			formula.add(new Element(elementName, elementCount)); //fill up the list with elements
		}
		return formula;
	}
	/**
	 * calculates all combinations without order of the atoms in the Molecule
	 * @param molecule
	 * @return
	 */
	public static ArrayList<Molecule>  calculateFragments(Molecule molecule, Molecule tiniestFragment){//molecule is target molecule, tiniestFragment is smallest wanted fragment
		allCompounds.clear();
		tiniestFragment.compressMolecule();
		molecule.compressMolecule();
		Molecule firstMolecule = tiniestFragment;
		allCompounds.add(firstMolecule);//to get a first empty Molecule
		for (Element element: molecule.getElements()) {//for all Elements that are in the given molecule
			int size=allCompounds.size();
			for (int j=1; j<=element.getCount()-tiniestFragment.getCountOfElement(element); j++) {//for the count of this element
				Element newElement = new Element(element.getName(), j);
				for (int i = 0; i < size; i++) {//copy the elements from a previous Molecule
					ArrayList<Element> previous= new ArrayList<Element>();
					Molecule newMolecule= new Molecule(previous,charge);
					for (Element p: allCompounds.get(i).getElements()) {
						previous.add(p);}//add the new Element to this Molecule
					newMolecule.addElement(newElement);
					newMolecule.removeEmptyElements();
					newMolecule.compressMolecule();
					if (tiniestFragment.getAtomCount()>0 && !tiniestFragment.generateString().equals("")) {
						allCompounds.add(newMolecule);
					}
				}
			}
		}	
		sortbyMass(allCompounds);
		if (tiniestFragment.calculateMass() < 1 && allCompounds.size() > 0) {
			allCompounds.remove(0);
		}//remove the first and empty molecule
		if (Main.main.removeImplausiblesCheckBox.isSelected()) {
			removeImplausibles();
		}
		return allCompounds;
	}

	/**
	 * sort Molecules by total mass
	 * @param moleculeList
	 * @return
	 */
	public static ArrayList<Molecule> sortbyMass(ArrayList<Molecule> moleculeList) {
		moleculeList.sort((molecule1,molecule2) ->molecule1.calculateMass().compareTo(molecule2.calculateMass()));
        return moleculeList;
    } 
	/**
	 * errormessage: informs the user on screen in case of invalid input 
	 * @param message as string with error information
	 */
	public static void errorMessage(String message) {
		Main.main.messageLabel.setVisible(true);
		Main.main.messageLabel.setText(message);
	}

	/**
	 * set color for color buttom array (user can choose a color by button)
	 * @param i as key for choosen color
	 * @return color
	 */
	public static Color setColorByValue(int i) {
		switch (i) 	{
			case 0:return new Color(0,0,150);
			case 1:return Color.BLUE; 
			case 2:return Color.MAGENTA;
			case 3:return Color.PINK;
			case 4:return Color.RED;
			case 5:return Color.ORANGE;
			case 6:return Color.YELLOW;
			case 7:return new Color(155,255,0);
			case 8:return Color.GREEN;
			case 9:return new Color(0,150,0);
			case 10:return Color.CYAN;
			case 11:return new Color(0,150,150);
			case 12:return new Color(150,100,50);
			case 13:return Color.BLACK;
			case 14:return Color.GRAY;
			default: return Color.WHITE;
		}
	}

	/**
	 * remove implausible molecules based on chemical knowledge
	 * e.g. molecules that cannot exist like C3H3
	 */
	public static void removeImplausibles() {
		for (int i = 0; i < allCompounds.size(); i++) {
			Molecule molecule= allCompounds.get(i);
			while ((molecule.getElements().size() < 2 ||
				molecule.getAtomCount("C") * 2 > molecule.getAtomCount() ||
				molecule.getAtomCount("H") * 3 / 2 > molecule.getAtomCount() ||
				molecule.getAtomCount("O") * 2 > molecule.getAtomCount() ||
				molecule.getAtomCount("N") * 2 > molecule.getAtomCount() ||
				molecule.getAtomCount("C") * 2 > molecule.getAtomCount()) &&
				allCompounds.size() > 0) {
					allCompounds.remove(i);
					molecule= allCompounds.get(i);
			}
		}
	}

	public JTextField getMaxField(){
		return maxField;
	}
	public JTextField getMinField(){
		return minField;
	}
	public JTextField getDeviationTextField(){
		return deviationTextField;
	}
	public JTextField getChargeField(){
		return chargeField;
	}
	public JButton getCalculateButton(){
		return calculateButton;
	}
	public JButton getImportButton(){
		return importButton;
	}

	public JButton getExportButton(){
		return exportButton;
	}

	public JButton getZoomInButton(){
		return zoomInButton;
	}

	public JButton getZoomOutButton(){
		return zoomOutButton;
	}

	public JButton getColorButtons(int i){
		return colorButtons[i];
	}
	public GraphicSpectrum getGraphic(){
		return graphic;
	}

	public JLabel getFilenameLabel(){
		return filenameLabel;
	}
	public JLabel getMessageLabel(){
		return messageLabel;
	}
	public JRadioButton getPlusRadioButton(){
		return plusRadioButton;
	}
	public JRadioButton getMinusRadioButton(){
		return minusRadioButton;
	}

	public DefaultTableModel getModel(){
		return model;
	}
	public DefaultTableModel getModel2(){
		return model2;
	}

	public Border getSmallBorder(){
		return smallBorder;
	}
	public Border getBigBorder(){
		return bigBorder;}
	public void setBigBorder(Border border){
		this.bigBorder = border;
	}

	public ArrayList<Molecule> getAllCompounds(){
		return allCompounds;
	}

	public int getCharge(){
		return charge;
	}
	public void setCharge(int charge){
		this.charge = charge;
	}
}
