import java.util.concurrent.Semaphore;
import java.util.Random;

public class ElevatorSynchro extends Thread{
	public static int n;
	public static int m;
	public static int k;
	public static int t;
	public static int id;
	public static int cust;
	public static int custid;
	public static int curFloor;
	public static int[] numPeople = new int[5];
	public static boolean[] called = new boolean[5];
	public static int[] numGoTo = new int[5];
	public static boolean goUp;
	public static ElevatorSynchro es;
	public static Semaphore mutex = new Semaphore(1, false);
	public static Semaphore[] floors = new Semaphore[5];
	public static Customer[] custers = new Customer[20];
	public static int[] nextCalls = new int[3];
	public static Random rand = new Random();
	
	public ElevatorSynchro(int i, int c){
		id = i;
		cust = c;
	}
	
	public static void main(String[] args){
		System.out.println("Initializing Elevator");
		n = 20;
		m = 5;
		k = 3;
		t = 2;
		curFloor = 0;
		goUp = true;
		for (int i = 0; i < 5; i++){
			floors[i] = new Semaphore(1, false);
		}
		for (int p = 0; p < n; p++){
			custers[p] = new Customer(0, 0, -1, false);
		}
		for (int i = 0; i < n; i++){
			custers[i].id = i;
		}
		for (int z = 0; z < k; z++){
			nextCalls[z] = 0;
		}
		
		Random ayn = new Random();
		int rp = n;
		int lastPeep = 0;
		for (int i = 0; i < m; i++){
			if (i == m - 1){
				for (int j = lastPeep; j < n; j++){
					custers[j].curFlo = i;
				}
				numPeople[i] = rp;
			} else {
				int peeps = ayn.nextInt(rp);
				numPeople[i] = peeps;
				for (int j = lastPeep; j < lastPeep + peeps; j++){
					custers[j].curFlo = i;
				}
				lastPeep = lastPeep + peeps;
				rp = rp - peeps;
			}
		}
		es = new ElevatorSynchro(0, 0);
		es.start();
	}
	
	public void run(){
		while (true){
			ElevatorSynch(id);
		}
	}
	
	public static void sim(){
		for (int i = 0; i < m; i++){
			if (numPeople[i] != 0){
				called[i] = true;
			} else if (numGoTo[i] != 0){
				called[i] = true;
			} else {
				called[i] = false;
			}
		}
	}
	
	public static void ElevatorSynch(int n){
		System.out.println("******THE ELEVATOR IS CURRENTLY ON FLOOR " + curFloor + " *******");
		if ((curFloor == m-1) && goUp){
			goUp = false;
		}
		if ((curFloor == 0) && !goUp){
			goUp = true;
		}
		for (int i = 0; i < 20; i++){
			custers[i].justLeft = false;
		}
		checkFloors();
		sim();
		load();
		checkForSems();
	}
	
	public static void load(){
		System.out.println("***LETTING PEOPLE IN AND OUT OF THE ELEVATOR");
		if (numGoTo[curFloor] != 0 && cust >= numGoTo[curFloor]){
			numPeople[curFloor] = numPeople[curFloor] + numGoTo[curFloor];
			cust = cust - numGoTo[curFloor];
			numGoTo[curFloor] = 0;
			for (int i = 0; i < n; i++){
				try{
					mutex.acquire();
				} catch (InterruptedException e){}
				System.out.println("A customer is leaving the elevator....");
				if(custers[i].curFlo == -1 && custers[i].nextFlo == curFloor){
					custers[i].curFlo = curFloor;
					custers[i].nextFlo = -1;
					custers[i].justLeft = true;
					try{
						custers[i].sleep(t * 1000);
					} catch (InterruptedException e){
					}
				}
				mutex.release();
			}
		} else if (numGoTo[curFloor] != 0 && cust < numGoTo[curFloor]){
			if (cust != 0){
				numPeople[curFloor] = numPeople[curFloor] + cust;
				numGoTo[curFloor] = numGoTo[curFloor] - cust;
				cust = 0;
				for (int i = 0; i < n; i++){
					try{
						mutex.acquire();
					} catch (InterruptedException e){}
					if(custers[i].curFlo == -1 && custers[i].nextFlo == curFloor){
						custers[i].curFlo = curFloor;
						custers[i].nextFlo = -1;
						custers[i].justLeft = true;
						try{
							custers[i].sleep(t * 1000);
						} catch (InterruptedException e){
						}
					}
					mutex.release();
				}
			}
		}
		
		int numRemoved = 0;
		int removedPeople = 0;
		int availPeople = numPeople[curFloor];
		for (int i = 0; i < 20; i++){
			if (custers[i].curFlo == curFloor && custers[i].justLeft){
				availPeople--;
				removedPeople++;
			}
		}
		
		if (called[curFloor] && cust < k){
			if (availPeople > k - cust){
				for (int i = 0; i < (k - cust); i++){
					try{
						mutex.acquire();
					} catch (InterruptedException e){}
					nextCalls[i] = call(curFloor);
					mutex.release();
				}
				numRemoved = k - cust;
				numPeople[curFloor] = (numPeople[curFloor] - (k - cust)) + removedPeople;
				cust = 3;
			} else {
				if(availPeople == k - cust){
					for (int i = 0; i < (k - cust); i++){
						try{
							mutex.acquire();
						} catch (InterruptedException e){}
						nextCalls[i] = call(curFloor);
						mutex.release();
					}
					numRemoved = availPeople;
					numPeople[curFloor] = removedPeople;
					cust = 3;
				} else if (availPeople < k - cust){
					for (int i = 0; i < numPeople[curFloor]; i++) {
						try{
							mutex.acquire();
						} catch (InterruptedException e){}
						nextCalls[i] = call(curFloor);
						mutex.release();
					}
					numRemoved = availPeople;
					cust = cust + availPeople;
					numPeople[curFloor] = removedPeople;
				}
			}
			int numLeft = 0;
			for (int j = 0; j < n; j++){
				if (custers[j].curFlo == curFloor && !custers[j].justLeft){
					custers[j].curFlo = -1;
					custers[j].nextFlo = nextCalls[numLeft];
					System.out.println("Customer " + custers[j].id + " now wants to go to floor " + custers[j].nextFlo);
					numLeft++;
					if (numLeft == numRemoved){
						break;
					}
				}
			}
		}
	}
	
	public static void checkFloors(){
		for (int q = 0; q < n; q++){
			if (custers[q].nextFlo != -1){
				System.out.println("****Customer " + custers[q].id + " is currently in the elevator and wants to go to floor " + custers[q].nextFlo);
			} else {
				System.out.println("****Customer " + custers[q].id + " is currently on floor " + custers[q].curFlo);
			}
		}
		for (int i = 0; i < m; i++){
			System.out.println("**Number of people on floor " + i + ": " + numPeople[i] + " with " + numGoTo[i] + " people wanting to go there.");
		}
		System.out.println("People currently in the elevator: " + cust);
	}
	
	public static int call(int i){
		Random amy = new Random();
		int pond = i;
		while (pond == i) {
			pond = amy.nextInt(4);
		}
		numGoTo[pond]++;
		return pond;
	}
	
	public static void checkForSems(){
		int check = curFloor;
		if (goUp){
			for (int i = curFloor + 1; i < m; i++){
				if (called[i]){
					try{
						floors[i].acquire();
					} catch (InterruptedException e){}
					curFloor = i;
					floors[i].release();
					break;
				}
			}
		}
		if (!goUp){
			for (int i = curFloor - 1; i > -1; i--){
				if (called[i]){
					try{
						floors[i].acquire();
					} catch (InterruptedException e){}
					curFloor = i;
					floors[i].release();
					break;
				}
			}
		}
		if (check != curFloor){
			System.out.println("The elevator is going to floor " + curFloor);
			try{
				es.sleep(Math.abs(check-curFloor) * 1000);
			} catch (InterruptedException e){
				
			}
		} else {
			System.out.println("The elevator needs five minutes alone.");
		}
	}
}