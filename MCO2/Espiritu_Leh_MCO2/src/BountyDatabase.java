import java.util.ArrayList;

public class BountyDatabase {
    private final ArrayList<CaptureRecord> captureRecords;

    /* CONSTRUCTOR
        Purpose: Sets up empty group lists.
    */
    public BountyDatabase(){
        captureRecords = new ArrayList<>();
    }

    private boolean validateCaptor(Character captor, boolean isDead){
        return !(captor instanceof Pirate); // the captor CANNOT be a pirate
    }

    private void processTargetStatus(Pirate target, boolean isDead){
        Status newStatus = isDead ? Status.DEAD:Status.CAPTURED;
        target.setStatus(newStatus);

        if(isDead && target.getPirateCrew() != null){
            target.getPirateCrew().goodbyeMember(target);
        }
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
        CaptureRecord captureRecord = new CaptureRecord(target, captor);
        captureRecords.add(captureRecord);
    }

    public boolean registerCapture(Pirate target, Character captor, boolean isDead){
        if (validateCaptor(captor, isDead) == true){
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
