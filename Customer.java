public class Customer extends Thread{
	int id;
	int curFlo;
	int nextFlo;
	boolean justLeft;
	
	public Customer(int i, int c, int n, boolean j){
		id = i;
		curFlo = c;
		nextFlo = n;
		justLeft = j;
	}
	
	public int getIdentity(){
		return id;
	}
	
	public int getCurrentFloor(){
		return curFlo;
	}
	
	public int getNextFloor(){
		return nextFlo;
	}
}