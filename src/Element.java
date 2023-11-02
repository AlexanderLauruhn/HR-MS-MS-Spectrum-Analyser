public class Element {
	/**
	 * this class represents an element, which is  part of an element.
	 * An Element can consist of several same atoms
	 */
	private final int count;//counts how many atom of this element are in the molecule
	private final String name;//name from periodic table
	private float mass;//mass of the isotope times count
	
	public Element(String name, int count) {
		this.name=name;
		this.count=count;
		switch (name) {//carbon, hydrogen, deuterium, nitrogen, oxygen, flourine, phosphorous
		case "C", "C(12)": mass=12.0f;
			break;
		case "H": mass=1.007825f;
			break;
		case "D": mass=2.014102f;
			break;
		case "N": mass=14.003074f;
			break;
		case "O": mass=15.994915f;
			break;
		case "F": mass=18.998403f;
			break;
		case "Cl": mass=34.968853f;
			break;
		case "Br": mass=78.918338f;
			break;
		case "S": mass=31.972071f;
			break;
		case "P": mass=30.973762f;
			break;
		case "B": mass=11.009305f;
		}
		mass*=count;	
	}
	
	public int getCount() {return count;}
	public String getName() {return name;}
	public float getMass() {return mass;}
}
