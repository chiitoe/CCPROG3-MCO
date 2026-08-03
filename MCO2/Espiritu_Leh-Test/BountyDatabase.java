// ===== MODEL LAYER =====
import java.util.ArrayList;

/** BountyDatabase
 * Purpose: Manages the register of all capture records.
 */
public class BountyDatabase {

    private final ArrayList<CaptureRecord> captureRecords;

    /** CONSTRUCTOR
     * Purpose: Sets up an empty capture register.
     */
    public BountyDatabase(){
        captureRecords = new ArrayList<>();
    }

    /**
     * Purpose: Runs a capture and files the resulting record.
     * @param target the pirate being captured
     * @param captor the character making the capture
     * @param isDead true if the target was killed rather than taken alive
     * @return the filed record
     * @throws InvalidCaptorException if the captor is null or a pirate
     * @throws IllegalArgumentException if the target is null or is not FREE
     */
    public CaptureRecord registerCapture(Pirate target, Character captor, boolean isDead)
            throws InvalidCaptorException {

        CaptureRecord captureRecord = CaptureRecord.attemptCapture(target, captor, isDead);
        captureRecords.add(captureRecord);
        return captureRecord;
    }

    /** Purpose: Prints every capture record on file. */
    public void viewAllCaptures(){
        System.out.println("=== All Captures ===");
        if(captureRecords.isEmpty()){
            System.out.println("  (no captures on record)");
            return;
        }
        for(CaptureRecord record : captureRecords){
            record.displayCaptureRecord();
        }
    }

    /**
     * Purpose: Finds a capture record by ID.
     * @param id the capture ID to search for
     * @return the matching record, or null if none exists
     */
    public CaptureRecord findCaptureById(int id){
        for(CaptureRecord record : captureRecords){
            if(record.getCaptureId() == id) return record;
        }
        return null;
    }

    /** @return a defensive copy of the capture register */
    public ArrayList<CaptureRecord> getCaptureRecords(){
        return new ArrayList<>(this.captureRecords);
    }
}
