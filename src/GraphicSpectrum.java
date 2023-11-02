import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GraphicSpectrum extends JPanel {
	private ArrayList<Signal> signals=new ArrayList<Signal>();
	private double maxI=0;//highest intensity
	private double maxM=0;//hightest mass
	private Color newColor=Color.RED;// the color each signal
	private int spectrumSize=Constants.defaultSpectrumSize;//length of the spectrum
	private int verticalDistance =Constants.verticalSpectrumDistance;//distance from bottom border
	private int horizontalDistance=Constants.horizontalSpectrumDistance;//distance from left border


	/**
	 * draw the spectrum with legend and signals as line plot
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(3));
		g.setColor(Color.WHITE);//white background
		g.fillRect(0,0,spectrumSize+100,350);
		for (Signal s: getSignals()){
			g.setColor(s.getColor()); //get color of signal
			g.drawLine(horizontalDistance+(int)(s.getMass()*(double)spectrumSize/maxM), verticalDistance,horizontalDistance+(int)(s.getMass()*(double)spectrumSize/maxM), verticalDistance -(int)(s.getIntensity()*200/maxI)-5);
			if (s.getIntensity()==maxI) {//show the mass of the hightest signal
				g.setColor(Color.BLACK);
				g2.setStroke(new BasicStroke(1));
				String value=String.valueOf(s.getMass());
				int length=value.length()*7;
				g.drawRect(horizontalDistance-length/2+(int)(s.getMass()*(double)spectrumSize/maxM),38,length+5,15);
				g.drawString(value,horizontalDistance-length/2+5+(int)(s.getMass()*(double)spectrumSize/maxM),50);
				g2.setStroke(new BasicStroke(3));
			}
		}
		g.setColor(Color.BLACK);
		if(maxM>0) {//draw all interceptions on x axis in legend-width
			float legend= generateInterceptionForLegend();
			for (float i=legend; i<=maxM*1.1; i+=legend) {
				g.drawLine((int)(horizontalDistance+i*(double)spectrumSize/maxM), verticalDistance,(int)(horizontalDistance+i*(double)spectrumSize/maxM), verticalDistance +7);
				if (legend>=1) {;
					String value= String.valueOf((int)i);
					int space=horizontalDistance-value.length()*3;
					g.drawString(value,(int)(space+i*(double)spectrumSize/maxM), verticalDistance +20);}
				if (legend<1) {
					String value= roundString(i, legend);
					int space=horizontalDistance-value.length()*3;  //write the mass value to interceptions
					g.drawString(value,(int)(space+i*(double)spectrumSize/maxM), verticalDistance +20);}
			}
		}
		g.drawLine(horizontalDistance-10, verticalDistance,spectrumSize+100, verticalDistance);
	}
	/**convert an read input from String to double Signal value
	 *
	 * @param number
	 * @return
	 */
	public double convertToValue(String number) {
		double value=0;
		number.replace(',','.');//if comma as decimal separator
		try {value=Double.parseDouble(number);}
		catch (NumberFormatException ex){Main.errorMessage("incorrect Data imported");}
		return value;
	}
	/**
	 * find the maxima values of intensity and mass to adjust the scale of the plot
	 */
	public void findMax() {
		maxM=0;
		maxI=0;
		for (Signal signal: getSignals()) {
			if (signal.getIntensity()>maxI) {maxI=signal.getIntensity();}
			if (signal.getMass()>maxM) {maxM=signal.getMass();}
		}
	}
	/**
	 * compare the imported spectrum with the calculated fragments
	 */
	public void compareSpectra() {
		float ppm=1;
		try {ppm=Float.parseFloat(Main.main.errorTextField.getText().replace(',', '.'));}
		catch (NumberFormatException ex){
			Main.errorMessage("Please enter a numeric error value");
		}
		for (Signal signal: getSignals()) {//compare each signal with each molecule
			double error=ppm*signal.getMass()/1000000;// ppm= part per million
			int i=0;
			Molecule molecule = Main.allCompounds.get(i);
			while ((signal.getMass()>molecule.calculateMass() || match(signal,molecule,error ))&& i<Main.allCompounds.size() ) {//compare until matches to save runtime (allows several matches)
				if (match(signal,molecule,error)) {
					signal.setColor(newColor);
					if (signal.getHitBy().equals("")||(!signal.getHitBy().equals("")&& signal.getError()> ppm(molecule,signal))) {
						signal.setHitBy(molecule.generateString());
						signal.setError(ppm(molecule,signal));
					}
				}
				i++;
				if (i<Main.allCompounds.size()){//
					molecule = Main.allCompounds.get(i);//get next molecule
				}
			}
		}
		setSpectrumTable();
	}
	/**
	 * round a value to several digits after  part per million decimal separator
	 * @param molecule
	 * @param signal
	 * @return
	 */
	public float ppm (Molecule molecule, Signal signal) {//rounded for two digit after decimal separator
		return (float) (int)(Math.abs((signal.getMass()-molecule.calculateMass())/signal.getMass())*100000000)/100;
	}
	/**
	 * setup of the Spectrum table on the screen
	 */
	public void setSpectrumTable() {
		int c=0;
		for (Signal s: getSignals()) {
			String ppm="";
			if (!s.getHitBy().equals("")) {ppm=String.valueOf(s.getError());}
			Main.main.model2.addRow(new Object[]{  //add new Signal to table
					String.valueOf(++c),
					String.valueOf(s.getMass()),
					String.valueOf(s.getIntensity()),
					s.getHitBy(),
					ppm});
		}
		findMax(); //find new maximum position
	}


	/**
	 * returns the value for the legend of the spectrum
	 * @return
	 */
	public float generateInterceptionForLegend() {
		float segment=(float)(450*(double)(maxM/(5*spectrumSize)));
		int i=1;
		while(i<segment) {
			segment-=segment%i;
			i*=10;
		}
		if (segment>=1) {
			segment=setComfortableValue((int)segment);
		}
		else {
			i=1;
			while(segment<1) {
				segment*=10;
				i*=10;
			}
			segment=(int)(segment);
			segment=(float)(setComfortableValue((int)(segment))/(i));
		}
		return segment;}
	/**
	 * returns a suitable value to label the shown spectrum with values ending on 1, 2 or 5
	 * @param segment
	 * @return
	 */
	public float setComfortableValue(float segment) {
		if (String.valueOf(segment).charAt(0)=='3') {segment=segment*2/3;}
		else if (String.valueOf(segment).charAt(0)=='4') {segment=segment*5/4;}
		else if (String.valueOf(segment).charAt(0)=='6') {segment=segment*5/6;}
		else if (String.valueOf(segment).charAt(0)=='7') {segment=segment*5/7;}
		else if (String.valueOf(segment).charAt(0)=='8') {segment=segment*10/8;}
		else if (String.valueOf(segment).charAt(0)=='9') {segment=segment*10/9;}
		return segment;}

	/**
	 * returns a rounded number as string to draw in the spectrum
	 * @param value
	 * @param legend
	 * @return
	 */
	public String roundString(float value, float legend) {
		int k=String.valueOf(legend).toCharArray().length-2;//amount of wanted digits
		value =(int)(value*Math.pow(10,k));
		value/=Math.pow(10,k);
		return String.valueOf(value);
	}
	/**
	 * determines if a signal fits to a molecule in given reeor range
	 * @param signal
	 * @param molecule
	 * @param error
	 * @return
	 */
	public boolean match(Signal signal, Molecule molecule, double error) {
		return (signal.getMass()<molecule.calculateMass()+error && signal.getMass()>molecule.calculateMass()-error);
	}

	public void setNewColor(Color newColor){
		this.newColor = newColor;
	}

	public Color getNewColor(){
		return newColor;
	}

	public ArrayList<Signal> getSignals() {
		return signals;
	}
	public int getSpectrumSize() {
		return spectrumSize;
	}
	public void setSpectrumSize(int size) {
		this.spectrumSize=size;
	}

}
