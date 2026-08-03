// ===== MODEL LAYER =====
/** CaptureRecord
 * Purpose: Holds the immutable record of a single capture.
 */
public class CaptureRecord {

    private static int autoID = 1;
    private final int captureId;
    private final Pirate capturedPirate;
    private final Character captor;
    private final Status status;
    private final long bountyClaimed;

    /** CONSTRUCTOR
     * Purpose: Initializes a new capture record.
     * Private on purpose - records may only be produced by attemptCapture, which
     * guarantees the arguments have already been validated and are never null.
     * @param capturedPirate the pirate who was taken or killed
     * @param captor the non-pirate who claimed them
     * @param status the target's resulting status
     * @param bountyClaimed the reward paid out for this capture
     */
    private CaptureRecord(Pirate capturedPirate, Character captor, Status status, long bountyClaimed){
        this.captureId = autoID++;
        this.capturedPirate = capturedPirate;
        this.captor = captor;
        this.status = status;
        this.bountyClaimed = bountyClaimed;
    }

    // Getters
    public int getCaptureId(){ return this.captureId; }
    public Pirate getCapturedPirate(){ return this.capturedPirate; }
    public Character getCaptor(){ return this.captor; }
    public Status getStatus(){ return this.status; }
    public long getBountyClaimed(){ return this.bountyClaimed; }

    /**
     * Purpose: Enforces the rule that a pirate may never claim a bounty.
     * @param captor the character attempting the capture
     * @throws InvalidCaptorException if the captor is null or is a Pirate
     */
    private static void validateCaptor(Character captor) throws InvalidCaptorException {
        if(captor == null){
            throw new InvalidCaptorException("A capture requires a captor.");
        }
        if(captor instanceof Pirate){
            throw new InvalidCaptorException(captor.getName() + " is a pirate and cannot claim a bounty.");
        }
    }

    /**
     * Purpose: Applies the outcome of the capture to the target.
     * A dead pirate is struck from their crew roster; a captured one keeps their
     * berth but stops contributing to the crew's active bounty total.
     * @param target the pirate being captured
     * @param isDead true if the target was killed rather than taken alive
     */
    private static void processTargetStatus(Pirate target, boolean isDead){
        Status newStatus = isDead ? Status.DEAD : Status.CAPTURED;
        target.setStatus(newStatus);

        if(isDead && target.getPirateCrew() != null){
            target.getPirateCrew().goodbyeMember(target);
        }
    }

    /**
     * Purpose: Pays out the reward and clears the target's bounty so it cannot be claimed twice.
     * A marine's reward goes to their corps' operational funds; an unaffiliated
     * marine is paid personally instead.
     * @param target the captured pirate whose bounty is being claimed
     * @param captor the character receiving the reward
     * @return the amount paid out in Berries
     */
    private static long claimBounty(Pirate target, Character captor){
        long reward = target.getBounty();

        if(captor instanceof Marine marine && marine.getMarineCorps() != null){
            marine.getMarineCorps().addOpFunds(reward);
        }
        else{
            captor.addWallet(reward);
        }

        if(captor instanceof PirateHunter hunter){
            hunter.incrementConfirmedCaptures();
        }

        target.setBounty(0);    // Prevents the same head being sold twice
        return reward;
    }

    /**
     * Purpose: Runs a full capture - validation, status change, and payout - in the correct order.
     * @param target the pirate being captured, rejected if null or not currently FREE
     * @param captor the character making the capture
     * @param isDead true if the target was killed rather than taken alive
     * @return the resulting capture record
     * @throws InvalidCaptorException if the captor is null or a pirate
     * @throws IllegalArgumentException if the target is null or is not FREE
     */
    public static CaptureRecord attemptCapture(Pirate target, Character captor, boolean isDead)
            throws InvalidCaptorException {

        if(target == null){
            throw new IllegalArgumentException("A capture requires a target.");
        }
        if(target.getStatus() != Status.FREE){
            throw new IllegalArgumentException(target.getName() + " is already " + target.getStatus() + ".");
        }
        validateCaptor(captor);

        processTargetStatus(target, isDead);
        long reward = claimBounty(target, captor);
        return new CaptureRecord(target, captor, target.getStatus(), reward);
    }

    /** Purpose: Prints out the information of a capture record. */
    public void displayCaptureRecord(){
        System.out.println("=================================================="); //  50
        System.out.println("Capture ID      : " + this.captureId);
        System.out.println("Captured        : " + this.capturedPirate.getName());
        System.out.println("Captor          : " + this.captor.getName());
        System.out.println("Bounty Claimed  : " + this.bountyClaimed + " Berries");
        System.out.println("Status          : " + this.status);
    }

    /**
     * Purpose: Builds a single-line, pipe-delimited record of this capture for file storage.
     * @return one CSV-style line, no trailing newline
     */
    public String toRecord(){
        return "CAPTURE|" + captureId + "|" + capturedPirate.getName() + "|"
                + captor.getName() + "|" + status + "|" + bountyClaimed;
    }
}
