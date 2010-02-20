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

import java.util.ArrayList;
import java.util.Iterator;

public final class ProcessController extends Thread {

    public ProcessController (Vm parent) {
        this.parent = parent;
    }

    class VmMap {
        int byteCodeIndex = 0;
        Vm vm = null;

        VmMap (Vm vm) {
            this.vm = vm;
        }

        boolean isDone () {
            return (byteCodeIndex >= vm.getByteCodes ().size ());
        }
    }
    
    public void add (Vm vm) {
        vms.add (new VmMap (vm));
        if (!started) {
            this.start ();
            started = true;
        }
    }

    public void run () {
        Iterator<VmMap> iter = vms.iterator ();
        int idx = 0;
        while (!vms.isEmpty ()) {
            VmMap vmMap = iter.next ();
            ++idx;
            runVm (vmMap);
            if (vmMap.isDone ()) {
                vms.remove (idx - 1);
                iter = vms.iterator ();
                idx = 0;
            }
        }
    }

    private void runVm (VmMap vmMap) {
        Vm vm = vmMap.vm;
        ByteCodes byteCodes = vm.getByteCodes ();
        for (int i = 0; i < BYTE_CODES_TO_RUN; ++i) {
            if (!vmMap.isDone ()) {
                // TODO: Handle a blocked vm.
                try {
                    vm.executeByteCode (byteCodes.at (vmMap.byteCodeIndex++));
                } catch (VmException ex) {
                    parent.writeLine (ex.getMessage () + " in process " + vm);
                }                                         
            }
        }
    }

    private Vm parent = null;
    private boolean started = false;
    private ArrayList<VmMap> vms = new ArrayList<VmMap> ();
    private static final int BYTE_CODES_TO_RUN = 10;
}

