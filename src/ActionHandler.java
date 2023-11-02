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
		Main.main.message.setVisible(false);
		JButton src = (JButton) e.getSource();
		if(src==Main.main.zoomInButton) {
			adjustSpectrumSize((int)(Main.main.graphic.getSpectrumSize()*zoomValue));//zoom
		}
		else if(src==Main.main.zoomOutButton) {//booleans cannot be combined to one if-clause, otherwise the frame of colobutton disappears
			if (Main.main.graphic.getSpectrumSize()>Constants.defaultSpectrumSize) {
			adjustSpectrumSize((int)(Main.main.graphic.getSpectrumSize()/zoomValue));
			}
		}
		else if (src==Main.main.calculateButton) {
			Main.main.calculateButton.setText("wait");//in case of long during calculation
			try {
				Main.charge=Integer.parseInt(Main.main.chargeField.getText());
			}
			catch (NumberFormatException ex){
				Main.errorMessage("Insert a numeric integer charge");
			}
			setFragmentTable(Main.main.maxField.getText(), Main.main.minField.getText());
			Main.main.graphic.compareSpectra();
			Main.main.calculateButton.setText("calculate");
		}
		else if (src==Main.main.importButton) {  //import high resolution ms spectrum
			ImExport.readFile();
			adjustSpectrumSize(Constants.defaultSpectrumSize);
			}
		else if (src==Main.main.exportButton) {
			ImExport.export();  //create a new .csv file with cleaned spectrum
		}
		else {
			for (int i=0; i<16; i++) { //choose color for
				Main.main.colorButtons[i].setBorder(Main.main.smallBorder);
				if (src==Main.main.colorButtons[i]) {
					Main.main.graphic.setNewColor(Main.setColorByValue(i));
					if (i==13) {
						Main.main.bigBorder = BorderFactory.createLineBorder (Color.WHITE, 3);
					}//choose white frame for black button
					else {
						Main.main.bigBorder = BorderFactory.createLineBorder (Color.BLACK, 3);
					}
					Main.main.colorButtons[i].setBorder(Main.main.bigBorder);
				}
			}
		}
		Main.main.graphic.repaint();
		Main.main.requestFocus();
	}

	/**setup Fragment table based on input in textfields with sum formula
	 * @param molecule
	 * @param sumFormula
	 */
	public void setFragmentTable(String molecule, String sumFormula) {
		if (molecule.matches("([A-Z][a-z]?([(][0-9]{1,3}[)])?[0-9]*)+") &&
				sumFormula.matches("([A-Z][a-z]?([(][0-9]{1,3}[)])?[0-9]*)*")) {//regex checks for molecular formular
			while(Main.main.model.getRowCount()>0) {Main.main.model.removeRow(0);} //clear tables
			while(Main.main.model2.getRowCount()>0) {Main.main.model2.removeRow(0);}
			int c=0;
			for (Molecule fragment: Main.calculateFragments(new Molecule(Main.readInput(molecule), 1),new Molecule(Main.readInput(sumFormula), 1))) {
				Main.main.model.addRow(new Object[]{String.valueOf(++c),fragment.generateString(),String.valueOf(fragment.calculateMass())});
			}
		}
		else {Main.errorMessage("Please enter a correct chemical formula!");;}
	}

	/**
	 * adjust the spectrum, if zoom function is used or if the m/z range is very broad
	 * @param newSize
	 */
	public void adjustSpectrumSize(int newSize) {
		Main.main.graphic.setSpectrumSize(newSize); //reset size
		Main.main.graphic.setPreferredSize(new Dimension(Main.main.graphic.getSpectrumSize()+95,400));
		Main.main.graphic.setSize(Main.main.graphic.getSpectrumSize()+100,400);
	}
}