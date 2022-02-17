package mcgill.ecse321.grocerystore.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Item {

	@Id
	private String name;
	// Attributes
	private double price;
	private int inventory;
	private boolean canDeliver;
	private boolean canPickUp;
	private boolean isDiscontinued;
	// aewffaewfawef

	// This class has no association
	public Item() {
		this.price = 0.0;
		this.name = null;
		this.inventory = 0;
		this.canDeliver = false;
		this.canPickUp = false;
		this.isDiscontinued = false;

	}

	public String getname() {
		return this.name;
	}

	public double getprice() {
		return this.price;
	}

	public int getinventory() {
		return this.inventory;

	}

	public boolean getcanDeliver() {
		return this.canDeliver;
	}

	public boolean getcanPickUp() {
		return this.canPickUp;
	}

	public boolean getisAvailable() {
		return this.isDiscontinued;
	}

	public void setname(String name) {
		this.name = name;
	}

	public void setprice(double price) {
		this.price = price;
	}

	public void setinventory(int inventory) {
		this.inventory = inventory;
	}

	public void setcanDeliver(boolean canDeliver) {
		this.canDeliver = canDeliver;
	}

	public void setcanPickUp(boolean canPickUp) {
		this.canPickUp = canPickUp;
	}

	public void setisAvailable(boolean isAvailable) {
		this.isDiscontinued = isAvailable;
	}
}
