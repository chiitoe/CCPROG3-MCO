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
    public CaptureRecord(Pirate capturedPirate, Character captor, Status status){
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

    /** Purpose: Prints out the information of a capture record
     */
    public void displayCaptureRecord(){
        System.out.println("=================================================="); //  50
        System.out.println("Capture ID      : " + this.captureId);
        System.out.println("Captured        : " + this.capturedPirate.getName());
        System.out.println("Captor          : " + this.captor.getName());
        System.out.println("\nStatus          : " + this.status);
    }
}
