import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.io.IOException;
import javax.swing.JFileChooser;

public class ImExport {
	/**
	 * this class handels the import (loading ms data file) and the export (saving a cleaned ms file on disk)
	 */
	static String exportname=""; //the file´s path as string

	/**
	 * export ms file as txt document
	 */
	public static void export() {
		String output = "";//all current data into this file
		for (Signal s : Main.main.getGraphic().getSignals()) {
			if (!s.getHitBy().equals("")) {
				output+=s.getMass() + " " + s.getIntensity() + " " + s.getHitBy() + " " + s.getError() + "\n";
			}
		}
		try { //write document with data
			Path path = Paths.get(exportname.substring(0, exportname.length()-4)+"CleanedUp.txt");
			Files.write(path, output.getBytes());}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * load a ms file with signals
	 */
	public static void readFile() {
		JFileChooser jfc = new JFileChooser(); //choose file
		jfc.showOpenDialog(null);
		try {
			Path f = Paths.get(jfc.getSelectedFile().getPath());
			String name = jfc.getSelectedFile().getName();
			exportname = f.toString();
			Main.main.getFilenameLabel().setText(name.substring(0, name.length() - 4));
			Main.main.getFilenameLabel().setBounds(550 - ( 7 * name.length()),-7,name.length() * 7, 30);
			double maxInt = 0;
			Scanner scan = new Scanner(f);
			clearPreviousData(); //delete current data befor loading
			int counter = 0;// needed to find any line with incorrect or missing data
			while (scan.hasNext()) {
				counter++;  //count lines
				String number=removeSignsAtBegin(scan.nextLine());//get the next line without letters at the beginnning
				String first = "", second = "";// first= mass, second = intensity
				boolean found=false; //search for separator
				for (int i = 0; i < number.length(); i++) {
					Character c = number.toCharArray()[i];
					if (!Character.isDigit(c) && c != '.' && !found) {// search for any separator between mass and intensity value
						found=true;
						first=number.substring(0, i);
						second= removeSignsAtEnd(number.substring(i + 1, number.length()));
					}
				}
				if (first.equals("") || second.equals("")) {//to be save in case a line is not read correctly
					first = "0";
					second = "0";
					Main.errorMessage("Incorrect Data in line " + counter); // throw error on screen
				}
				try {
					if (Main.main.getGraphic().convertToValue(second) > maxInt) {
						maxInt = Main.main.getGraphic().convertToValue(second);
					}
					if(!second.equals("0") && !first.equals("0")) {// do not add cero-values to list
						Main.main.getGraphic().getSignals().add(new Signal
								(Main.main.getGraphic().convertToValue(second), Main.main.getGraphic().convertToValue(first)));
					}
				}
				catch(NumberFormatException e){e.printStackTrace();} //error allready thrown above
			}
			for (Signal signal: Main.main.getGraphic().getSignals()) {//rounding depending on intesity digit cound
				if ((signal.getIntensity() * 100) / maxInt >= 10) {//convert to raw intensity signals to percent values and adjust digit count
					signal.setIntensity((double)(int)(signal.getIntensity() * 1000 / maxInt) / 10);}
				else if ((signal.getIntensity() * 100) / maxInt >= 1) {
					signal.setIntensity((double)(int)(signal.getIntensity() * 10000 / maxInt) / 100);}
				else if ((signal.getIntensity() * 100) / maxInt >= 0.1) {
					signal.setIntensity((double)(int)(signal.getIntensity() * 100000 / maxInt) / 1000);}
				else {
					signal.setIntensity((double)(int)(signal.getIntensity() * 1000000 / maxInt) / 10000);}
			}
			Main.main.getGraphic().setSpectrumTable();
			scan.close();
		}
		catch (Exception e) {e.printStackTrace();}
	}
	/**
	 * removes all non-digit chars at the start of a string
	 * @param a
	 * @return
	 */
	public static String removeSignsAtBegin(String a) {
		String b = "";
		char[] c = a.toCharArray();
		boolean isDigit = false;
		for (int i = 0; i < a.length(); i++) {
			if (Character.isDigit(c[i])) {
				isDigit=true;
			}
			if(isDigit) {
				b += c[i];
			}
		}
		return b;}
	/**
	 * removes all non-digit values at the start and the end of a string
	 * @param input
	 * @return output without non-numeric ending
	 */
	public static String removeSignsAtEnd(String input) {
		String output = "";
		char[] c = removeSignsAtBegin(input).toCharArray();//without non-digit at beginning
		boolean isDigit=true;
		for (int i=0; i<c.length; i++) {
			if (!Character.isDigit(c[i]) && c[i] != '.') {
				isDigit=false;
			}
			if(isDigit) {output += c[i];}
		}
		return output;}

	/**
	 * clear data from previous file
	 */
	private static void clearPreviousData(){
		while (Main.main.getModel2().getRowCount() > 0) {
			Main.main.getModel2().removeRow(0);//remove lines from table
		}
		Main.main.getGraphic().getSignals().clear();//remove signals from gui
	}
}
