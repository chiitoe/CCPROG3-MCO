/** CaptureRecord
 * Purpose: Holds information for each capture
 */

public class CaptureRecord {

    private static int autoID = 1;  /** Auto-generated id */

    // Attributes
    private final int captureId;    /** a capture's unique ID */
    private final Pirate capturedPirate;   /** the captured pirate*/
    private final Character captor;  /** the character that captures the pirate */
    private final Status status;     /** the captured pirate's status: eg. DEAD, FREE, CAPTURED */

    /** CONSTRUCTOR
     * Purpose: initializes a new capture record
     * @param capturedPirate no fallback since this should never be null
     * @param captor no fallback since this should never be null
     */
    private CaptureRecord(Pirate capturedPirate, Character captor, Status status){
            this.captureId = autoID++;

            this.capturedPirate = capturedPirate;
            this.captor = captor;
            this.status = status;
    }

    /** Getters */
    public int getCaptureId(){ return this.captureId; }
    public Pirate getCapturedPirate(){ return this.capturedPirate; }
    public Character getCaptor(){ return this.captor; }
    public Status getStatus(){ return this.status; }

    /** Methods */

    /** Purpose: Checks if the captor is a pirate
     * @param captor
     * @return
     */
    private static boolean validateCaptor(Character captor){
        return !(captor instanceof Pirate); // the captor CANNOT be a pirate
    }

    // MUST be called BEFORE claimBounty and logStatus methods
    // or alternatively, simply use the registerCapture helper method to automatically call all methods in the right order.
    private static void processTargetStatus(Pirate target, boolean isDead){
        Status newStatus = isDead ? Status.DEAD:Status.CAPTURED;
        target.setStatus(newStatus);

        if(isDead && target.getPirateCrew() != null){
            target.getPirateCrew().goodbyeMember(target);
        }

        // bounty deduction from ALIVE && CAPTURED pirates are handled implicitly in the PirateCrew class: total bounty is counted only from ALIVE Pirates
    }

    private static void claimBounty(Pirate target, Character captor){
        if(captor instanceof Marine){
            ((Marine)captor).getMarineCorps().addOpFunds(target.getBounty());  // casts since we use captor is broadly a character
        }
        else{
            captor.addWallet(target.getBounty());
        }

        if(captor instanceof PirateHunter){
            ((PirateHunter)captor).incrementConfirmedCaptures();  // casts since we use captor is broadly a character
        }
    }

    public static CaptureRecord attemptCapture(Pirate target, Character captor, boolean isDead){
        if(!validateCaptor(captor)){
            return null;
        }

        processTargetStatus(target, isDead);
        claimBounty(target, captor);
        return new CaptureRecord(target, captor, target.getStatus());
    }

    /** Purpose: Prints out the information of a capture record */
    public void displayCaptureRecord(){
        System.out.println("=================================================="); //  50
        System.out.println("Capture ID      : " + this.captureId);
        System.out.println("Captured        : " + this.capturedPirate.getName());
        System.out.println("Captor          : " + this.captor.getName());
        System.out.println("\nStatus          : " + this.status);
    }
}
