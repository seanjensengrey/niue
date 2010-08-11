// Loads the configuration from config.niue

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.niue.Niue;
import org.niue.vm.Vm;
import org.niue.vm.VmException;

public class ConfigTest {
    
    public static void main (String[] args) {
        InputStream in = null;
        try {
            in = new FileInputStream ("config.niue");            
            Niue niue = new Niue (in);
            Vm vm = new Vm (niue);
            niue.run (vm);
            Properties props = (Properties) vm.popObject ();
            System.out.println ("DB-NAME: " + props.getProperty ("DB-NAME"));          
            System.out.println ("PORT: " + props.getProperty ("PORT"));          
        } catch (IOException ex) {
            System.out.println (ex.getMessage ());
        } catch (VmException ex) {
            System.out.println (ex.getMessage ());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ex) { }
            }
        }
    }
}