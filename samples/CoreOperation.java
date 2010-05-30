// Adds a new core VM operation.

import org.niue.Niue;
import org.niue.vm.Vm;
import org.niue.vm.IVmOperation;
import org.niue.vm.DataStackElement;
import org.niue.vm.VmException;

public class CoreOperation {
    
    // An operation to square the top element on the data stack.
    static class Square implements IVmOperation {
        
        public void execute (Vm vm) throws VmException {
            DataStackElement elem = vm.pop ();
            int val = elem.getElement ();
            vm.pushInteger (val * val);
        }
    }

    public static void main (String[] args) {
        try {
            Niue niue = new Niue ();
            // Create a new Vm and add the square operation to it.
            Vm vm = new Vm (niue);
            vm.addOperation ("sqr", new Square ());
            niue.run (vm);
        } catch (VmException ex) {
            System.out.println (ex.getMessage ());
        }
    }
}