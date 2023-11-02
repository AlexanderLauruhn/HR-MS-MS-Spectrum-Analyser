import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;

public class ActionHandler implements ActionListener{
	/**
	 * this actionListener handels all button actions
	 * @param e the event to be processed
	 */
	float zoomValue = Constants.zooomValue; //Value of spectrum zoom width
	public void actionPerformed (ActionEvent e) {
		Main.main.getMessageLabel().setVisible(false);
		JButton src = (JButton) e.getSource();
		if(src == Main.main.getZoomInButton()) {
			adjustSpectrumSize((int)(Main.main.getGraphic().getSpectrumSize() * zoomValue));//zoom
		}
		else if(src == Main.main.getZoomOutButton()) {//booleans cannot be combined to one if-clause, otherwise the frame of colobutton disappears
			if (Main.main.getGraphic().getSpectrumSize() > Constants.defaultSpectrumSize) {
			adjustSpectrumSize((int)(Main.main.getGraphic().getSpectrumSize() / zoomValue));
			}
		}
		else if (src == Main.main.getCalculateButton()) {
			Main.main.getCalculateButton().setText("wait");//in case of long during calculation
			try {
				Main.main.setCharge(Integer.parseInt(Main.main.getChargeField().getText()));
			}
			catch (NumberFormatException ex){
				Main.errorMessage("Insert a numeric integer charge");
			}
			setFragmentTable(Main.main.getMaxField().getText(), Main.main.getMinField().getText());
			Main.main.getGraphic().compareSpectra();
			Main.main.getCalculateButton().setText("calculate");
		}
		else if (src == Main.main.getImportButton()) {  //import high resolution ms spectrum
			ImExport.readFile();
			adjustSpectrumSize(Constants.defaultSpectrumSize);
			}
		else if (src == Main.main.getExportButton()) {
			ImExport.export();  //create a new .csv file with cleaned spectrum
		}
		else {
			for (int i = 0; i < 16; i++) { //choose color for
				Main.main.getColorButtons(i).setBorder(Main.main.getSmallBorder());
				if (src==Main.main.getColorButtons(i)) {
					Main.main.getGraphic().setNewColor(Main.setColorByValue(i));
					if (i == 13) {
						Main.main.setBigBorder(BorderFactory.createLineBorder (Color.WHITE, 3));
					}//choose white frame for black button
					else {
						Main.main.setBigBorder(BorderFactory.createLineBorder (Color.BLACK, 3));
					}
					Main.main.getColorButtons(i).setBorder(Main.main.getBigBorder());
				}
			}
		}
		Main.main.getGraphic().repaint();
		Main.main.requestFocus();
	}

	/**setup Fragment table based on input in textfields with sum formula
	 * @param molecule
	 * @param sumFormula
	 */
	public void setFragmentTable(String molecule, String sumFormula) {
		if (molecule.matches("([A-Z][a-z]?([(][0-9]{1,3}[)])?[0-9]*)+") &&
				sumFormula.matches("([A-Z][a-z]?([(][0-9]{1,3}[)])?[0-9]*)*")) {//regex checks for molecular formular
			while(Main.main.getModel().getRowCount() > 0) {Main.main.getModel().removeRow(0);} //clear tables
			while(Main.main.getModel2().getRowCount() > 0) {Main.main.getModel2().removeRow(0);}
			int counter = 0;
			for (Molecule fragment: Main.calculateFragments(new Molecule(Main.readInput(molecule), 1),new Molecule(Main.readInput(sumFormula), 1))) {
				Main.main.getModel().addRow(new Object[]{String.valueOf(++counter),fragment.generateString(),String.valueOf(fragment.calculateMass())});
			}
		}
		else {Main.errorMessage("Please enter a correct chemical formula!");;}
	}

	/**
	 * adjust the spectrum, if zoom function is used or if the m/z range is very broad
	 * @param newSize
	 */
	public void adjustSpectrumSize(int newSize) {
		Main.main.getGraphic().setSpectrumSize(newSize); //reset size
		Main.main.getGraphic().setPreferredSize(new Dimension(Main.main.getGraphic().getSpectrumSize()+95, 400));
		Main.main.getGraphic().setSize(Main.main.getGraphic().getSpectrumSize() + 100, 400);
	}
}