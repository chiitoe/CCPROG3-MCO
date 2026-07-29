import java.util.ArrayList;

public class BountyDatabase {
    private final ArrayList<CaptureRecord> captureRecords;

    /* CONSTRUCTOR
        Purpose: Sets up empty group lists.
    */
    public BountyDatabase(){
        captureRecords = new ArrayList<>();
    }

    private boolean validateCaptor(Character captor){
        return !(captor instanceof Pirate); // the captor CANNOT be a pirate
    }

    // MUST be called BEFORE claimBounty and logStatus methods
    // or alternatively, simply use the registerCapture helper method to automatically call all methods in the right order.
    private void processTargetStatus(Pirate target, boolean isDead){
        Status newStatus = isDead ? Status.DEAD:Status.CAPTURED;
        target.setStatus(newStatus);

        if(isDead && target.getPirateCrew() != null){
            target.getPirateCrew().goodbyeMember(target);
        }

        // bounty deduction from ALIVE && CAPTURED pirates are handled implicitly in the PirateCrew class: total bounty is counted only from ALIVE Pirates
    }

    private void claimBounty(Pirate target, Character captor){
        if(captor instanceof Marine){
            ((Marine)captor).getMarineCorps().addOpFunds(target.getBounty());  // casts since we use captor is broadly a character
        }
        else{
            captor.addWallet(target.getBounty());
        }
    }

    private void logStatus(Pirate target, Character captor){
        CaptureRecord captureRecord = new CaptureRecord(target, captor, target.getStatus());
        captureRecords.add(captureRecord);
    }

    public boolean registerCapture(Pirate target, Character captor, boolean isDead){
        if (validateCaptor(captor)){
            processTargetStatus(target, isDead);
            claimBounty(target, captor);
            logStatus(target, captor);
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
