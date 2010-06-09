// Redirects streams for a VM.

import org.niue.Niue;
import java.io.*;

public class StreamTest {
    
    public static void main (String[] args) {
        InputStream in = null;
        PrintStream out = null;
        try {
            File inputFile = new File (args[0]);
            in = new FileInputStream (inputFile);
            File outputFile = new File (args[1]);
            out = new PrintStream (new FileOutputStream (outputFile));
            Niue niue = new Niue (in, out);
            niue.run ();
        } catch (Exception ex) {
            System.out.println (ex.getMessage ());
        } finally {
            try {
                in.close ();
            } catch (Exception ex) { }
            try {
                out.close ();
            } catch (Exception ex) { }
        }
    }
}