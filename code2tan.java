/*************************
 CS 4345
 Summer 2022
 Coding 2
 David Tan
 *************************/
/***************************************************************************************
 Disclaimer: I used some Websites as well as some notes to help me code this assignment
 and to help me understand the overall assignment better.
 https://tinyurl.com/2f55xvkt
 https://tinyurl.com/97umunvj
 https://tinyurl.com/4yhpxawe
 https://tinyurl.com/yc3up83h
 https://tinyurl.com/2p8vvvx8
 https://tinyurl.com/mtzrufb6
 https://tinyurl.com/yc2y494t
 **************************************************************************************/

//This is my package
package OS;
import java.util.concurrent.*;
import java.util.concurrent.Semaphore;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

//This is my code2tan class
public class code2tan{
	public static void main(String[] args){
		// TODO Auto-generated method stub
		int random;
		Synchronizes sync = new Synchronizes();
		Thread west = new Thread(new Scheduler(0, sync));
		Thread east = new Thread(new Scheduler(1, sync));
		west.start(); 
		east.start();
	}
}

//This function runs the Vehicles
class Scheduler implements Runnable{
	 //Initialize some variables
	private int vechicleId;
	private Synchronizes sync;

	//This start is the initial vechicleId mean that 0 for west and 1 for east vehicles
	public Scheduler(int start, Synchronizes sync){
		this.vechicleId = start;
		this.sync = sync;
	}
	
	@Override
	public void run(){
		// TODO Auto-generated method stub
		Random random = new Random();
		while (true){
			
            //This function create new vehicle and run it
			Thread thrd = new Thread(new Vehicle(vechicleId, sync, random.nextInt(500)));
			vechicleId ++;
			vechicleId ++;

			thrd.start();
			
            //This function wait for the next one
			try{
				Thread.sleep(random.nextInt(500));
			} catch (InterruptedException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

class Synchronizes{
	
	//This is semaphore for critical section
	private Semaphore critical;
	
    //This tell which side vehicle is currently in the repair zone: 0 for west, 1 for east, -1 for zone is empty
	private int currently_in = -1;
	
	//This is the queue of vehicles waiting to enter & to exit repair zone
	private Queue<Vehicle> qIn, qOut;
	public Synchronizes(){
		qIn = new LinkedList<Vehicle>();
		qOut = new LinkedList<Vehicle>();
		critical = new Semaphore(1);
	}
	
	//This invoke my print statement
	void printMsg(Vehicle v, String action){
		String s = (v.vechicleId % 2 == 0 ? "West" : "East"); 
		System.out.printf("%s-bound vehicle %d %s the repair zone.\n", s, v.vechicleId, action);
	}
	
	 //Vehicle v wants to enter the repair zone
	void goIn(Vehicle v) throws InterruptedException{
		critical.acquire();
		printMsg(v, "wants to enter");
		if (currently_in == v.vechicleId % 2 || currently_in == -1){
			
            //This set the type of vehicle currently in
			currently_in = v.vechicleId % 2;
			
            //This print in message and add vehicle to the exit queue
			printMsg(v, "is in");
			qOut.add(v);
			critical.release();
		}
		else{
            //When the repair zone is occupied by different vehicle and then add to the waiting queue and release critical section
			qIn.add(v);
			v.waiting = true;
			critical.release();
			
            //This function wait until the repair zone is not free again
			v.toWait.acquire();
		}
	}
	
	//Vehicle v wants to exit the repair zone
	void goOut(Vehicle v) throws InterruptedException{
		critical.acquire();
		
		if (qOut.peek() == v){
            //If it is on the top of the exit queue prints the exit message and remove from the queue
			printMsg(v, "exits");
			qOut.remove();

            //If the out queue is not empty, remove all vehicles from it is top wishing to exit the repair zone
			while (!qOut.isEmpty()){
				Vehicle v1 = qOut.peek();
				if (v1.waiting){
					
                    //If the vehicle at the top of the queue is waiting to exit remove it, then print the message and release the semaphore
					v1.waiting = false;
					qOut.remove();
					printMsg(v1, "exits");
					v1.toWait.release();
				}
                //vehicle at the top of the queue still do not wish to exit
				else break;	
			}
			//This is my if-else statement
			if (qOut.isEmpty()){
				if (qIn.isEmpty()){
					
                    //If no vehicle is waiting to enter then set currently_in to -1
					currently_in = -1;
				}
				else{
					
                    //Otherwise, set currently_in to the opposite value
					currently_in = 1 - currently_in; 
					
                    //This is my while loop to let every vehicle waiting in the in queue to enter the repair zone
					while (!qIn.isEmpty()){
						Vehicle v1 = qIn.remove();
						printMsg(v1, "is in");
						
                        //This invoke function to add it to the exit queue and release the semaphore
						qOut.add(v1);
						v1.waiting = false;
						v1.toWait.release();
					}
				}
			}
			
            //This function release the critical section
			critical.release();
		}
		else{
			
            //Vehicle v is not at the top of the out queue
			v.waiting = true;
			
            //This function release critical section and wait until all vehicles in the front wishes to exit
			critical.release();
			v.toWait.acquire();
		}
	}	
}

//This class implement the vehicle
class Vehicle implements Runnable{
	public int vechicleId, delay;
	private Synchronizes sync;
	public Semaphore toWait;
	public boolean waiting;
	
	//This is the vechicleId, Synchronizes object and delay
	public Vehicle(int vechicleId, Synchronizes sync, int delay){
		this.delay = delay;
		this.vechicleId = vechicleId;
		this.sync = sync;
		this.waiting = false;
		toWait = new Semaphore(0);
	}
	
	@Override
	public void run(){
		// TODO Auto-generated method stub
	
		try{
            //Vehicle try to go in
			sync.goIn(this);
			
            //This let the vehicle wait for some time in the repair zone
			Thread.sleep(delay);
			
			//Vehicle try to go out
			sync.goOut(this);
		} catch (InterruptedException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
