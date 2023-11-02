import java.awt.Color;

/**
 * this class contains all information of a high resolution tandem mass spectrum signal
 */
public class Signal {
	private double intensity; // the signal´s intensity; the electrig amplitude of the measurement
	private final double mass; // the mass to charge ratio, which is used to calculate to sum formular
	private String hitBy=""; //saves all molecule information, that have a similar mass like this signal (match)
	private float error=0;
	private Color color; // the individual color in which the gui should show the signal in the spectrum
	public Signal(double intensity, double mass) {
		this.intensity=intensity;
		this.mass=mass;
		color= Main.main.graphic.getNewColor();
	}
	public double getMass() {return mass;}
	public double getIntensity() {return intensity;}
	public void setIntensity(double intensity) {this.intensity=intensity;}
	public Color getColor() {return color;}
	public void setColor (Color color) {this.color=color;}
	public void setHitBy(String hitBy) {this.hitBy=hitBy;}
	public String getHitBy () {return hitBy;}
	public void setError(float error) {this.error=error;}
	public float getError () {return error;}
}
