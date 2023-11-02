import java.util.ArrayList;

public class Molecule {
	private final ArrayList<Element> elementList;  //List of atoms in molecule (or molecule fragment)
	private final int ionCount;  //multi charging in positive and negative mode
	private static final double ELECTRONMASS = 0.00054858;//mass of electron


	public Molecule(ArrayList<Element> elementList, int ionCount) {
		this.elementList=elementList;
		this.ionCount=ionCount;} //Constructor, list of Elements is given
	public ArrayList<Element> getElements() {return elementList;}

	/**
	 * create a String of the sum formula, e.g. C4H7O5
	 * @return
	 */
	public String generateString() {
		String name="";
		for (Element element: elementList) {//enlarge String with atoms and atom counts
			name+=element.getName();
			if(element.getCount()>1) {
				name+=element.getCount();
			};
		}
		name+="(";
		if(Main.charge!=1) {
			name+=Main.charge; //add charge count if not ==1
		}
		if(Main.main.plusRadioButton.isSelected()) {
			name+="+)"; //add  positive charge
		}
		else {name+="-)";}
		
		return name;
	}
	public Double calculateMass() {
		double mass=-ELECTRONMASS *Main.charge;//electron mass
		if (Main.main.minusRadioButton.isSelected()) {
			mass= ELECTRONMASS *Main.charge; //
		}//negative ionisation
		for (Element element:elementList) {mass+=element.getMass();}//calculate the total mass of all elements
		return (double)(int)(mass/Main.charge*1000000)/(1000000);//with 6 digits after decimal separator
	}

	/**
	 * count all atoms of a molecute
	 * @return
	 */

	public int getAtomCount() {
		int atomCount=0;
		for (Element element:elementList) {
			atomCount+=element.getCount();
		}
		return atomCount;
	}
	public void addElement(Element element) {
		elementList.add(element);
	}

	/**
	 * this method adds two element atom counts, if the element is the same
	 */
	public void compressMolecule() {
		for (int i=0; i<elementList.size()-1; i++) {
			for (int j=i; j<elementList.size(); j++) {
				if (elementList.get(i).getName().equals(elementList.get(j).getName()) && i!=j) {
					Element n = new Element(elementList.get(i).getName(), elementList.get(i).getCount()+elementList.get(j).getCount());
					elementList.set(i, n);
					elementList.remove(j);
				}
			}
		}
	}
	/**
	 * returns the count of an individual element in a molecule
	 * @param element
	 * @return
	 */
	public int getCountOfElement(Element element) {
		int countOfElement=0;
		for (Element searchElement: elementList) {
			if(searchElement.getName().equals(element.getName())) {
				countOfElement=searchElement.getCount();
			}
		}
		return countOfElement;}

	/**
	 * a mocule does not need any element with atom count below 1.
	 * Therefore, these elements are removed with this method
	 */
	public void removeEmptyElements() {
		for (int i=0; i<elementList.size();i++) {
			while (elementList.get(i).getCount()<1 || elementList.get(i).getName().equals("")) {
				elementList.remove(i);
			}
		}	
	}

	/**
	 * count all atoms of a molecule of the same element
	 * @param name of element
	 * @return count of atoms of given element
	 */
	public int getAtomCount(String name) {
		int atomCount=0;
		for (Element element: elementList) {
			if (element.getName().equals(name)) {
				atomCount=element.getCount();//sum up the atom count
			}
		}
		return atomCount;
	}
}
