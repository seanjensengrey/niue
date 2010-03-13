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
	    TWO_SWAP, TWO_DUP, TWO_OVER, TWO_DROP, PUSH_TO,
            PUSH_ALL_TO, POP_FROM, POP_ALL_FROM, 
            SUPER_PUSH_TO, SUPER_PUSH_ALL_TO, SUPER_POP_FROM,
            SUPER_POP_ALL_FROM, SET_NEW_STACK, CLR };

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
            case PUSH_TO:
                pushTo (vm, false);
                break;
            case PUSH_ALL_TO:
                pushTo (vm, true);
                break;
            case POP_FROM:
                popFrom (vm, false);
                break;
            case POP_ALL_FROM:
                popFrom (vm, true);
                break;
            case SUPER_PUSH_TO:
                superPushTo (vm, false);
                break;
            case SUPER_PUSH_ALL_TO:
                superPushTo (vm, true);
                break;
            case SUPER_POP_FROM:
                superPopFrom (vm, false);
                break;
            case SUPER_POP_ALL_FROM:
                superPopFrom (vm, true);
                break;
            case SET_NEW_STACK:
                setNewStack (vm);
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

    static void len (Vm vm) {
        vm.pushInteger (vm.getDataStack ().size ());
    }

    static void swap (Vm vm) throws VmException {
	DataStackElement elem2 = vm.at (0);
	DataStackElement elem1 = vm.at (1);
	vm.pop ();
	vm.pop ();
	vm.push (elem2);
	vm.push (elem1);
    }

    static void swap_at (Vm vm) throws VmException {
	int j = vm.popInteger ();
	int i = vm.popInteger ();
	Collections.swap (vm.getDataStack (), i, j);
    }

    void dup (Vm vm) throws VmException {
	DataStackElement elem1 = vm.peek ();
	vm.push (elem1);
    }

    void over (Vm vm) throws VmException {
	DataStackElement elem2 = vm.at (1);
	vm.push (elem2);
    }

    void rot (Vm vm) throws VmException {
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

    void drop (Vm vm) throws VmException {
	vm.pop ();
    }

    void twoSwap (Vm vm) throws VmException {
	DataStackElement elem1p1 = vm.at (3);
	DataStackElement elem2p1 = vm.at (2);
	DataStackElement elem1p2 = vm.at (1);
	DataStackElement elem2p2 = vm.at (0);
	vm.pop (); vm.pop ();
	vm.pop (); vm.pop ();
	vm.push (elem1p2); vm.push (elem2p2);
	vm.push (elem1p1); vm.push (elem2p1);
    }

    void twoDup (Vm vm) throws VmException {
	DataStackElement elem1 = vm.at (1);
	DataStackElement elem2 = vm.at (0);
	vm.push (elem1); 
	vm.push (elem2);
    }

    void twoOver (Vm vm) throws VmException {
	DataStackElement elem1 = vm.at (2);
	DataStackElement elem2 = vm.at (3);
	vm.push (elem2); 
	vm.push (elem1);
    }

    void twoDrop (Vm vm) throws VmException {
	vm.pop ();
	vm.pop ();
    }

    void pushTo (Vm vm, boolean all) throws VmException {
        Vm targetVm = findVmByProcId (vm);
        if (all) {
            pushAllTo (vm, targetVm);
        } else {
            pushTo (vm, targetVm);
        }
    }

    void popFrom (Vm vm, boolean all) throws VmException {
        Vm srcVm = findVmByProcId (vm);
        if (all) {
            popAllFrom (vm, srcVm);
        } else {
            popFrom (vm, srcVm);
        }
    }

    void setNewStack (Vm vm) throws VmException {
        DataStackElement elem = vm.peek ();
        if (elem.getType () != ByteCode.Type.VM) {
            throw new VmException ("Expected code block.");
        }
        vm.setNewStack (elem.getElement ());
    }

    void clear (Vm vm) {
	vm.getDataStack ().clear ();
    }

    private void pushAllTo (Vm vm, Vm targetVm) {
        while (pushTo (vm, targetVm)) ;
    }

    private boolean pushTo (Vm vm, Vm targetVm) {
        try {
            boolean synced = (targetVm.isSpawned () || vm.isSpawned ());
            DataStackElement elem = vm.localPop (synced);
            if (synced) {
                targetVm.syncedPush (elem);
            } else {
                targetVm.push (elem);
            }
            return true;
        } catch (VmException ex) {
            return false;
        }
    }

    private void popAllFrom (Vm vm, Vm srcVm) {
        while (popFrom (vm, srcVm)) ;
    }

    private boolean popFrom (Vm vm, Vm srcVm) {
        try {
            boolean synced = (srcVm.isSpawned () || vm.isSpawned ());
            DataStackElement elem = srcVm.localPop (synced);
            if (synced) {
                vm.syncedPush (elem);
            } else {
                vm.push (elem);
            }
            return true;
        } catch (VmException ex) {
            return false;
        }
    }

    void superPushTo (Vm vm, boolean all) throws VmException {        
        Vm parentVm = getParentVm (vm);
        if (all) {
            pushAllTo (vm, parentVm);
        } else {
            pushTo (vm, parentVm);
        }
    }

    void superPopFrom (Vm vm, boolean all) throws VmException {        
        Vm parentVm = getParentVm (vm);
        if (all) {
            popAllFrom (vm, parentVm);
        } else {
            popFrom (vm, parentVm);
        }
    }

    private Vm findVmByProcId (Vm vm) throws VmException {
        int pid = vm.popInteger ();
        Vm targetVm = vm.getProcess (pid);
        if (targetVm == null) {
            throw new VmException ("Invalid process id.");
        }
        return targetVm;
    }

    private Vm getParentVm (Vm vm) throws VmException {
        Vm parentVm = vm.getParentVm ();
        if (parentVm == null) {
            throw new VmException ("No parent virtual machine found.");
        }
        return parentVm;
    }
    
    private Operator operator;
}
