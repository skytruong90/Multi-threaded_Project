import java.util.concurrent.Semaphore;
import java.util.ArrayList;

public class Cars
{
    public static void main(String[] args) throws InterruptedException
    {
        Semaphore sem = new Semaphore(1);

        Manager manEven = new Manager(sem,"Left",0);
        System.out.println("Left-bound car 0 wants to enter the tunnel.");
        Manager manOdd = new Manager(sem,"Right",1);
        System.out.println("Right-bound car 1 wants to enter the tunnel.");

        CarAdder carAdder = new CarAdder(manEven, manOdd);
        //starting up hell tunnel, give them strength
        manEven.start();
        manOdd.start();
        carAdder.start();
    }
}
//write class that implements runnable
//this class with
class Manager extends Thread{

    static Semaphore sem;
    String threadName;
    int counter = 0;
    int num;
    public ArrayList<Integer> queue = new ArrayList<>();

    public Manager(Semaphore sem, String threadName,int num) {
        this.sem = sem;
        this.threadName = threadName;
        this.num = num;
        queue.add(num);
        /*TESTING
        for(int i = 0; i < 99; i++)
        {
            num += 2;
            queue.add(num);
        }*/
    }

    //Runs for left manager thread
    private void critLeft(int firstCarIndex) throws InterruptedException
    {
        //let cars from this side enter the tunnel
        while(counter < queue.size() - 1)
        {
            System.out.println("Left-bound car " + queue.get(counter) + " is in the tunnel.");
            counter++;
            Thread.sleep(1000);
            if(counter + 1 < queue.size())
            {
                continue;
            }
        }
        //reset counter to the first car that entered the tunnel and let them all leave in sequence
        counter = firstCarIndex;
        while(counter < queue.size() - 1)
        {
            System.out.println("Left-bound car " + queue.get(counter) + " has left the tunnel.");
            counter++;
        }
    }
    //Runs for right manager thread
    private void critRight(int firstCarIndex) throws InterruptedException
    {
        //let cars from this side enter the tunnel
        while(counter < queue.size() - 1)
        {
            System.out.println("Right-bound car " + queue.get(counter) + " is in the tunnel.");
            counter++;
            Thread.sleep(500);
            if(counter + 1 < queue.size())
            {
                continue;
            }
        }
        //reset counter to the first car that entered the tunnel and let them all leave in sequence
        counter = firstCarIndex;
        while(counter < queue.size() - 1)
        {
            System.out.println("Right-bound car " + queue.get(counter) + " has left the tunnel.");
            counter++;
        }
    }

    @Override
    public void run() {
        try
        {
            int firstCarIndex;

            //infinite loop
            while (true)
            {
                //TESTING
                // System.out.println("Hi, I'm trying to access my queue -" + threadName);
                //check queue to see if the counter position exists
                if (counter < queue.size())
                {
                    //System.out.println(threadName + " has passed its check queuesize check");

                        //System.out.println(threadName + " is trying to acquire lock");
                        //get semaphore lock
                        sem.acquire();

                        //Set firstCar to counter
                        firstCarIndex = counter;

                        //System.out.println(threadName + " has acquired the lock");

                        //ENTER CRITICAL SECTION---------------------------------------
                        if (threadName.equals("Right"))
                        {
                            critRight(firstCarIndex);
                        } else
                            critLeft(firstCarIndex);
                        //EXIT CRITICAL SECTION---------------------------------------

                        //System.out.println(threadName + " is trying to release the lock");
                        //release lock
                        sem.release();
                        //System.out.println(threadName + " has released the lock");
                }
            }
        }
        catch(Exception e)
        {
            System.out.println("Something went wrong on " + threadName);
            System.err.println(e);
        }
    }
}

class CarAdder extends Thread
{
    Manager left;
    Manager right;

    public CarAdder(Manager left, Manager right)
    {
        this.left = left;
        this.right = right;
    }

    public void run()
    {
        int random;
        //enter infinite loop
        while(true)
        {
            random = (int)(Math.random() * 2);

            if(random == 0)
            {
                addCar(left, random);
            }
            else
            {
                addCar(right, random);
            }
            random = (int)(Math.random() * 1901 + 100);

            try
            {
                Thread.sleep(random);
            }
            catch(Exception e)
            {
                System.err.println(e);
            }
        }
    }

    //Adds a car to the queue of left or right
    public static void addCar(Manager manager, int leftRight)
    {
        int carNum = manager.queue.get(manager.queue.size() - 1) + 2;
        manager.queue.add(carNum);

        //print message
        if(leftRight == 0)
        {
            System.out.println("Left-bound car " + carNum + " wants to enter the tunnel.");
        }
        else
        {
            System.out.println("Right-bound car " + carNum + " wants to enter the tunnel.");
        }
    }
}