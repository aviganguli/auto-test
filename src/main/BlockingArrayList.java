package main;

import java.util.ArrayList;

/**
 * 
 * @author StrawHatJedi
 *
 * Generic blocking list : blocks if user desires else behaves as an ArrayList
 * Defaults to non-block when constructed
 *
 * @param <E> Paremtric type to be specified
 */
public class BlockingArrayList<E> extends ArrayList<E> {
	/**
	 * Boiler Plate
	 */
	private static final long serialVersionUID = 1L;
	private boolean isBlocked;
	
	/**
	 * Uses ArrayList constructor
	 */
	public BlockingArrayList() {
		super();
		this.isBlocked = false;
	}
	
	/**
	 * @param e element to be added
	 * @return false if add was blocked and true otherwise
	 * 
	 * adds to list if not currently blocking
	 * 
	 */
	@Override
	public boolean add(E e) {
		if (!isBlocked) {
			return super.add(e);
		}
		else {
			return false;
		}
	}
	
	/**
	 * setter for is blocked
	 * @param true if user wants to block and false otherwise
	 */
	public void setBlock(boolean isBlocked) {
		this.isBlocked = isBlocked;
	}
	
	/**
	 * getter for isBlocked
	 * @return true if list is set to block and false otherwise
	 */
	public boolean getBlock() {
		return this.isBlocked;
	}
	
	
}
