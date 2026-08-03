import java.util.ArrayList;

public class BountyDatabase {
    private final ArrayList<CaptureRecord> captureRecords;

    /* CONSTRUCTOR
        Purpose: Sets up empty group lists.
    */
    public BountyDatabase(){
        captureRecords = new ArrayList<>();
    }

    public boolean registerCapture(Pirate target, Character captor, boolean isDead){
        CaptureRecord captureRecord = CaptureRecord.attemptCapture(target, captor, isDead);

        if (captureRecord != null){
            captureRecords.add(captureRecord);
            return true;
        }
        return false;
    }

    public void viewAllCaptures() {
        System.out.println("=== All Captures ===");
        for (CaptureRecord record : captureRecords) {
            System.out.println("- [" + record.getCaptureId() + "] " + record.getCapturedPirate().getName() + " by: " + record.getCaptor().getName());
        }
    }
}
