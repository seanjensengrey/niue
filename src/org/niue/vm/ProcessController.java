// Copyright 2010 Vijay Mathew Pandyalakal. All rights reserved.

// Redistribution and use in source and binary forms, with or 
// without modification, are permitted provided that the following 
// conditions are met:

//    1. Redistributions of source code must retain the above copyright 
//       notice, this list of conditions and the following disclaimer.

//    2. Redistributions in binary form must reproduce the above copyright 
//       notice, this list of conditions and the following disclaimer in the 
//       documentation and/or other materials provided with the distribution.

// THIS SOFTWARE IS PROVIDED BY VIJAY MATHEW PANDYALAKAL ``AS IS'' AND ANY 
// EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
// DISCLAIMED. IN NO EVENT SHALL VIJAY MATHEW PANDYALAKAL OR CONTRIBUTORS BE 
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
// THE POSSIBILITY OF SUCH DAMAGE.

package org.niue.vm;

import java.util.LinkedList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Manages the sub-process of a virtual machine.  Uses
// a thread pool to run many concurrent processes. 

public final class ProcessController extends Thread {

    public ProcessController (Vm parent) {
        this.parent = parent;
    }

    // Adds a process to the pool. 

    public int add (Vm vm) {
        int procId = vm.getNiue ().nextProcId ();
        vm.setProcId (procId);
        vms.add (vm);
        if (!started) {
            this.start ();
            started = true;
        }
        return procId;
    }

    // Runs the processes in the pool. 

    public void run () {
        executors = Executors.newCachedThreadPool ();
        Vm vm = null;
        while ((vm = vms.poll ()) != null) {
            executors.execute (new ExecuteVm (vm));
        }
    }

    // Stops all running processes. 

    public void shutdown () {
        if (executors != null)
            executors.shutdownNow ();
    }
    
    // Callback for the executor. 

    public class ExecuteVm implements Runnable {
        ExecuteVm (Vm vm) {
            this.vm = vm;
        }

        public void run () {
            try {
                vm.run ();
            } catch (VmException ex) {
                parent.writeLine (ex.getMessage () + " in process " + vm);
            } catch (Exception ex) { 
                parent.writeLine (ex.getMessage () + " in process " + vm);
            }
            vm.getNiue ().removeProcess (vm.getProcId ());
	    vm.getParentVm ().discardChildVm (vm.getVmId ());
        }

        private Vm vm = null;
    }

    private Vm parent = null;
    private boolean started = false;
    private LinkedList<Vm> vms = new LinkedList<Vm> ();    
    private ExecutorService executors = null;
    private static final int BYTE_CODES_TO_RUN = 10;
}

