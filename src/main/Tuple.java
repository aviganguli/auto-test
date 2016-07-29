package main;

/**
 * 
 * @author AvishekGanguli
 * Generic Tuple Class for two element tuples
 * @param <X> first element in tuple
 * @param <Y> second element in tuple
 */
public class Tuple<X,Y> {
	private X first;
	private Y second;
	
	/**
	 * Constructor for tuple
	 * @param first element
	 * @param second element
	 */
	public Tuple(X first, Y second) {
		this.first = first;
		this.second = second;
	}
	
	/**
	 * getter for first element
	 * @return first element
	 */
	public X getFirst() {
		return first;
	}
	
	/**
	 * getter for second element
	 * @return second element
	 */
	public Y getSecond() {
		return second;
	}
	
	/**
	 * toString override
	 */
	@Override
	public String toString() {
		return "(" + first.toString() + "," + second.toString() + ")" ;
	}
}
