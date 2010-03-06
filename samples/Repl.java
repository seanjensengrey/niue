// A simple REPL for Niue.

import org.niue.Niue;
import org.niue.vm.VmException;

public class Repl {
    
    public static void main (String[] args) {
        try {
            Niue niue = new Niue ();
            niue.run ();
        } catch (VmException ex) {
            System.out.println (ex.getMessage ());
        }
    }
}