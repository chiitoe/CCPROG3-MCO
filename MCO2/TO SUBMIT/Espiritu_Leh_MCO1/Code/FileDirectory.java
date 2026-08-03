import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/** FileManager
 * Purpose: Handles all disk persistence for the Grand Line Registry.
 * Character streams (FileWriter / Scanner) are used for the human-readable
 * text exports; byte streams (FileInputStream / FileOutputStream) are used for
 * the raw archive copy, since a byte-for-byte duplicate must not be re-encoded.
 * Every stream is opened in a try-with-resources block so it closes itself.
 */
public class FileDirectory {

    private static final String DATA_DIRECTORY = "registry_data";
    private static final String CHARACTER_FILE = "characters.txt";
    private static final String FRUIT_FILE = "devil_fruits.txt";
    private static final String GROUP_FILE = "affiliations.txt";
    private static final String CAPTURE_LOG = "capture_log.txt";

    /**
     * Purpose: Makes sure the data directory exists and returns the full path to a file inside it.
     * @param fileName the bare file name, e.g. "characters.txt"
     * @return a File pointing at that name inside the data directory
     */
    private static File resolve(String fileName){
        File directory = new File(DATA_DIRECTORY);
        if(!directory.exists()){
            directory.mkdirs();     // Creates the folder if this is the first run
        }
        return new File(directory, fileName);
    }

    /**
     * Purpose: Writes a batch of already-formatted record lines to a file, overwriting it.
     * @param file the destination file
     * @param header a title line written above the records
     * @param records the lines to write, one per element
     * @throws DataIOException if the file cannot be written
     */
    private static void writeRecords(File file, String header, ArrayList<String> records)
            throws DataIOException{

        // try-with-resources: the FileWriter is closed automatically, even on failure
        try(FileWriter writer = new FileWriter(file)){
            writer.write("# " + header + "\n");
            for(String record : records){
                writer.write(record + "\n");
            }
        }
        catch(IOException e){
            throw new DataIOException("Could not write to " + file.getName(), e);
        }
    }

    /**
     * Purpose: Exports the whole character roster to characters.txt.
     * @param characters the roster to save
     * @throws DataIOException if the file cannot be written
     */
    public static void saveCharacters(ArrayList<Character> characters) throws DataIOException {
        ArrayList<String> lines = new ArrayList<>();
        for(Character c : characters){
            lines.add(c.toRecord());
        }
        writeRecords(resolve(CHARACTER_FILE), "TYPE|ID|NAME|ALIAS|ORIGIN|STATUS|FRUIT|WALLET", lines);
    }

    /**
     * Purpose: Exports the devil fruit registry to devil_fruits.txt.
     * @param fruits the registry to save
     * @throws DataIOException if the file cannot be written
     */
    public static void saveDevilFruits(ArrayList<DevilFruit> fruits) throws DataIOException {
        ArrayList<String> lines = new ArrayList<>();
        for(DevilFruit f : fruits){
            lines.add(f.toRecord());
        }
        writeRecords(resolve(FRUIT_FILE), "TYPE|ID|NAME|CATEGORY|ABILITY|OWNER|PAST_OWNERS", lines);
    }

    /**
     * Purpose: Exports every crew and corps to affiliations.txt.
     * @param crews the pirate crews to save
     * @param corpsUnits the marine corps units to save
     * @throws DataIOException if the file cannot be written
     */
    public static void saveAffiliations(ArrayList<PirateCrew> crews, ArrayList<MarineCorps> corpsUnits)
            throws DataIOException {

        ArrayList<String> lines = new ArrayList<>();
        for(PirateCrew c : crews){ lines.add(c.toRecord()); }
        for(MarineCorps m : corpsUnits){ lines.add(m.toRecord()); }
        writeRecords(resolve(GROUP_FILE), "TYPE|ID|NAME|LOCATION|LEADER|SIZE|FUNDS_OR_BOUNTY", lines);
    }

    /**
     * Purpose: Appends one capture to the running log rather than overwriting it.
     * The 'true' second argument to FileWriter switches it into append mode, so
     * the log survives across program runs.
     * @param record the capture to log
     * @throws DataIOException if the log cannot be appended to
     */
    public static void appendCapture(CaptureRecord record) throws DataIOException {
        File logFile = resolve(CAPTURE_LOG);

        try(FileWriter writer = new FileWriter(logFile, true)){
            writer.write(record.toRecord() + "\n");
        }
        catch(IOException e){
            throw new DataIOException("Could not append to the capture log", e);
        }
    }

    /**
     * Purpose: Reads the capture log back off disk and prints it line by line.
     * @throws DataIOException if the log exists but cannot be read
     */
    public static void readCaptureLog() throws DataIOException {
        File logFile = resolve(CAPTURE_LOG);

        if(!logFile.exists()){
            System.out.println("No capture log on disk yet.");
            return;
        }

        try(Scanner reader = new Scanner(logFile)){
            System.out.println("--- Capture Log (" + logFile.length() + " bytes) ---");
            while(reader.hasNextLine()){
                System.out.println(reader.nextLine());
            }
            System.out.println("--- End of log ---");
        }
        catch(FileNotFoundException e){
            throw new DataIOException("The capture log vanished mid-read", e);
        }
    }

    /**
     * Purpose: Prints filesystem metadata for every file in the data directory.
     */
    public static void showFileInfo(){
        File directory = new File(DATA_DIRECTORY);

        if(!directory.exists()){
            System.out.println("No data has been saved yet.");
            return;
        }

        System.out.println("=== Registry Files ===");
        System.out.println("Location: " + directory.getAbsolutePath());

        File[] files = directory.listFiles();
        if(files == null || files.length == 0){
            System.out.println("  (directory is empty)");
            return;
        }

        for(File f : files){
            System.out.println("--------------------------------------------------");
            System.out.println("Name       : " + f.getName());
            System.out.println("Size       : " + f.length() + " bytes");
            System.out.println("Readable   : " + f.canRead());
            System.out.println("Writable   : " + f.canWrite());
        }
    }

    /**
     * Purpose: Makes a byte-for-byte archive copy of a saved file.
     * Byte streams are used rather than FileWriter so the copy is exact regardless
     * of the source file's encoding or contents.
     * @param fileName the bare name of the file to copy, e.g. "characters.txt"
     * @throws DataIOException if the source is missing or the copy fails
     */
    public static void archiveFile(String fileName) throws DataIOException {
        File source = resolve(fileName);
        File backup = resolve(fileName.replace(".txt", "_backup.txt"));

        if(!source.exists()){
            throw new DataIOException("Nothing to archive: " + fileName + " does not exist.", null);
        }

        // Both streams are declared in the same try-with-resources header
        try(FileInputStream inStream = new FileInputStream(source);
            FileOutputStream outStream = new FileOutputStream(backup)){

            byte[] buffer = new byte[1024];     // 1KB chunks
            int bytesRead;

            // read() returns -1 once the end of the file is reached
            while((bytesRead = inStream.read(buffer)) != -1){
                outStream.write(buffer, 0, bytesRead);      // Write only what was actually read
            }

            System.out.println("Archived " + source.getName() + " to " + backup.getName());
        }
        catch(IOException e){
            throw new DataIOException("Archive of " + fileName + " failed", e);
        }
    }

    /**
     * Purpose: Deletes the capture log from disk.
     * @return true if the log was deleted, false if it was missing or locked
     */
    public static boolean clearCaptureLog(){
        File logFile = resolve(CAPTURE_LOG);
        return logFile.delete();
    }
}