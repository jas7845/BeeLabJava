package world;

import bee.Drone;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The queen's chamber is where the mating ritual between the queen and her
 * drones is conducted.  The drones will enter the chamber in order.
 * If the queen is ready and a drone is in here, the first drone will
 * be summoned and mate with the queen.  Otherwise the drone has to wait.
 * After a drone mates they perish, which is why there is no routine
 * for exiting (like with the worker bees and the flower field).
 *
 * @author Sean Strout @ RIT CS
 * @author Julie Sojkowski
 */
public class QueensChamber {

    private ConcurrentLinkedQueue<Drone> chamber;
    private boolean queenReady;
    private boolean canMate;

    public QueensChamber(){
         chamber = new ConcurrentLinkedQueue<>();
         queenReady = false;
         canMate =true;
    }

    /**
     * A drone enters the chamber.
     * @param d drone that enters the chamber
     */
    public synchronized void enterChamber(Drone d){
        chamber.add(d);
        System.out.println("*QC* " + d + " has entered the chamber" );
        while (!queenReady){ //&& chamber.peek().equals(d)){
            try{
                this.wait();
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        if (canMate){
            if (queenReady && chamber.peek().equals(d)){
                queenReady = false;
                d.setMated();
            }
        }
    }

    /**
     * When the queen is ready, they will summon the next drone from the collection (if at least one is there).
     */
    public synchronized void summonDrone(){
        queenReady = true;
        System.out.println("*QC* mates with " + chamber.peek()); // added
        this.notifyAll();
    }

    /**
     * At the end of the simulation the queen uses this routine
     * repeatedly to dismiss all the drones that were waiting to mate. #rit_irl...
     */
    public synchronized void dismissDrone(){
        queenReady = true;
        canMate = false;
        this.notifyAll();
        System.out.println("*QC* " + chamber.peek() +" leaves chamber");
        chamber.poll();
        queenReady=false;
    }

    /**
     * The queen uses this to check if she can mate
     * @return boolean if the chamber is empty
     */
    public synchronized boolean hasDrone(){
        return !chamber.isEmpty();
    }
}