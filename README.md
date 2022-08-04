## This is a project that I started in order to understand Synchronized and using deadlock because I was taking Operating System and these two concepts are very importants in OS.

## The Task: 
Write a program that synchronizes movement of vehicles through the repair zone in such a way that prevents the deadlock. You may use any synchronization technique. For example, semaphores would be a good choice. You can assume that there will be steady stream of vehicles from each side, however, there can be more than one vehicle from one side before we see a vehicle from the opposite side. The controllers allow more than one vehicle passing through the repair zone, but only in one direction. Vehicles do not need to have same speed (the time they spend to pass the repair zone). You can also assume, once a particular vehicle enters and leaves the repair zone, it does not come back again to enter the repair zone. That is, the vehicle numbers do not repeat!

### Live Demo:


## Why is Synchronized and deadlock important?
Deadlock occurs when multiple threads need the same locks but obtain them in different order. A Java multithreaded program may suffer from the deadlock condition because the synchronized keyword causes the executing thread to block while waiting for the lock, or monitor, associated with the specified object.

Synchronized methods enable a simple strategy for preventing thread interference and memory consistency errors: if an object is visible to more than one thread, all reads or writes to that object's variables are done through synchronized methods.

## How to install and run the program? 
1. Go to `code2tan.java`
2. copy the code in there.
3. paste to an Java IDE or use an online compiler and run the code.
