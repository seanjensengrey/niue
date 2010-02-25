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

package org.niue.vm.operation;

import java.util.Collections;
import org.niue.vm.IVmOperation;
import org.niue.vm.Vm;
import org.niue.vm.VmException;
import org.niue.vm.ByteCode;
import org.niue.vm.DataStackElement;
import java.math.BigInteger;

public final class StackManip implements IVmOperation {
    
    public enum Operator { LEN, SWAP, SWAP_AT, DUP, OVER, ROT, DROP,
	    TWO_SWAP, TWO_DUP, TWO_OVER, TWO_DROP, PUSH_SYNC,
            DONE_PUSH_SYNC, CLR };

    public StackManip (Operator opr) {
	operator = opr;
    }

    public void execute (Vm vm) throws VmException {
	try {
	    switch (operator) {
            case LEN:
                len (vm);
                break;
	    case SWAP:
		swap (vm);
		break;
	    case SWAP_AT:
		swap_at (vm);
		break;
	    case DUP:
		dup (vm);
		break;
	    case OVER:
		over (vm);
		break;
	    case ROT:
		rot (vm);
		break;
	    case DROP:
		drop (vm);
		break;
	    case TWO_SWAP:
		twoSwap (vm);
		break;
	    case TWO_DUP:
		twoDup (vm);
		break;
	    case TWO_OVER:
		twoOver (vm);
		break;
	    case TWO_DROP:
		twoDrop (vm);
		break;
            case PUSH_SYNC:
                pushSync (vm);
                break;
            case DONE_PUSH_SYNC:
                donePushSync (vm);
                break;
	    case CLR:
		clear (vm);
		break;
	    }
	} catch (VmException ex) {
	    throw ex;
	} catch (Exception ex) {
	    throw new VmException (ex);
	}
    }

    // Stack manipulation methods make separate at () and pop () 
    // calls to ensure that the stack will be in an unmodified state
    // even if there is an underflow exception.

    private static void len (Vm vm) {
        vm.pushInteger (vm.getDataStack ().size ());
    }

    public static void swap (Vm vm) throws VmException {
	DataStackElement elem2 = vm.at (0);
	DataStackElement elem1 = vm.at (1);
	vm.pop ();
	vm.pop ();
	vm.push (elem2);
	vm.push (elem1);
    }

    public static void swap_at (Vm vm) throws VmException {
	int j = vm.popInteger ();
	int i = vm.popInteger ();
	Collections.swap (vm.getDataStack (), i, j);
    }

    private void dup (Vm vm) throws VmException {
	DataStackElement elem1 = vm.peek ();
	vm.push (elem1);
    }

    private void over (Vm vm) throws VmException {
	DataStackElement elem2 = vm.at (1);
	vm.push (elem2);
    }

    private void rot (Vm vm) throws VmException {
	DataStackElement elem1 = vm.at (2);
	DataStackElement elem2 = vm.at (1);
	DataStackElement elem3 = vm.at (0);
	vm.pop ();
	vm.pop ();
	vm.pop ();
	vm.push (elem2);
	vm.push (elem3);
	vm.push (elem1);
    }

    private void drop (Vm vm) throws VmException {
	vm.pop ();
    }

    private void twoSwap (Vm vm) throws VmException {
	DataStackElement elem1p1 = vm.at (3);
	DataStackElement elem2p1 = vm.at (2);
	DataStackElement elem1p2 = vm.at (1);
	DataStackElement elem2p2 = vm.at (0);
	vm.pop (); vm.pop ();
	vm.pop (); vm.pop ();
	vm.push (elem1p2); vm.push (elem2p2);
	vm.push (elem1p1); vm.push (elem2p1);
    }

    private void twoDup (Vm vm) throws VmException {
	DataStackElement elem1 = vm.at (1);
	DataStackElement elem2 = vm.at (0);
	vm.push (elem1); 
	vm.push (elem2);
    }

    private void twoOver (Vm vm) throws VmException {
	DataStackElement elem1 = vm.at (2);
	DataStackElement elem2 = vm.at (3);
	vm.push (elem2); 
	vm.push (elem1);
    }

    private void twoDrop (Vm vm) throws VmException {
	vm.pop ();
	vm.pop ();
    }

    private void pushSync (Vm vm) throws VmException {
        if (vm.getPushingInto () != -1) {
            throw new VmException ("Already pushing data to another process.");
        }
        int pid = vm.popInteger ();
        Vm targetVm = vm.getProcess (pid);
        if (targetVm == null) {
            throw new VmException ("Invalid process id.");
        }
        vm.setPushingInto (pid);
        targetVm.setSyncedPush (true);
        vm.setTempDataStack (targetVm.getDataStack ());
    }

    private void donePushSync (Vm vm) throws VmException {
        int pid = vm.getPushingInto ();
        Vm targetVm = vm.getProcess (pid);
        targetVm.setSyncedPush (false);
        vm.setPushingInto (-1);
        vm.setTempDataStack (null);
    }

    private void clear (Vm vm) {
	vm.getDataStack ().clear ();
    }

    private Operator operator;
}
