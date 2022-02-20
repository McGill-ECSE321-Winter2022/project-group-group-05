package mcgill.ecse321.grocerystore.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Item {

	@Id
	private String name;
	//  Item Attributes
	private double price;
	private int inventory;
	private boolean canDeliver;
	private boolean canPickUp;
	private boolean isDiscontinued;
	

	// This class has no association
	
	
	
    //getter methods
	//--------------
	public String getName() {
		return this.name;
	}

	public double getPrice() {
		return this.price;
	}

	public int getInventory() {
		return this.inventory;

	}

	public boolean getcanDeliver() {
		return this.canDeliver;
	}

	public boolean getcanPickUp() {
		return this.canPickUp;
	}

	public boolean getisDiscontinued() {
		return this.isDiscontinued;
	}
    //setter methods
	//--------------
	public void setName(String name) {
		this.name = name;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setInventory(int inventory) {
		this.inventory = inventory;
	}

	public void setcanDeliver(boolean canDeliver) {
		this.canDeliver = canDeliver;
	}

	public void setcanPickUp(boolean canPickUp) {
		this.canPickUp = canPickUp;
	}

	public void setisDiscontinued(boolean isDiscontinued) {
		this.isDiscontinued = isDiscontinued;
	}

}
