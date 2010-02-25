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

import org.niue.vm.IVmOperation;
import org.niue.vm.Vm;
import org.niue.vm.VmException;
import org.niue.vm.DataStackElement;
import org.niue.vm.ByteCode;

// Implements the conditional branching words - if, when and unless. 

public final class If implements IVmOperation {
    
    public enum Cond { WHEN, UNLESS, IF, ELIF, ELSE };

    public If (Cond cond) {
	this.cond = cond;
    }
    
    // The semantics of when and unless:
    // `condition block_or_word_to_execute when|unless'
    // `condition' should be a boolean value.  If it is true
    // 'block_or_word_to_execute' will be executed by `when'. 
    // If it is false, `block_or_word_to_execute' will be 
    // executed by `unless'.  
    // The semantics of if else:
    // `condition block_or_word_to_execute1 if block_or_word_to_execute2 else
    // `block_or_word_to_execute1' is executed if the value left by
    // `condition' is true.  `block_or_word_to_execute2' is executed
    // otherwise. 

    public void execute (Vm vm) throws VmException {
	DataStackElement c = vm.at (1);
	if (c.getType () != ByteCode.Type.BOOLEAN) {
	    VmException.raiseUnexpectedValueOnStack ();
	}
	DataStackElement block = vm.at (0);
	ByteCode.Type type = block.getType ();
	if (type != ByteCode.Type.VM && type != ByteCode.Type.STRING) {
	    VmException.raiseUnexpectedValueOnStack ();
	}
	boolean exec = shouldExecute (c, vm);
	vm.pop ();
	vm.pop ();	
	if (exec) {
	    if (type == ByteCode.Type.VM) {
		vm.runChildVm (block.getElement (), true);
	    } else {
		vm.executeWord (block.getElement ());
	    }
	}
	pushIfNeeded (vm, c, exec);
    }

    private void pushIfNeeded (Vm vm, DataStackElement c,
			       boolean exec) throws VmException {
	if (cond == Cond.IF) {
	    vm.push (c);
	} else if (cond == Cond.ELIF) {
	    DataStackElement prevc = vm.at (0);
	    if (prevc.getType () != ByteCode.Type.BOOLEAN) {
		VmException.raiseUnexpectedValueOnStack ();
	    }
	    if (prevc.getElement () == 0) {
		vm.pop ();
		vm.push (c);
	    }
	}
    }

    private boolean shouldExecute (DataStackElement c, Vm vm) 
	throws VmException {
	boolean exec = false;
	switch (cond) {
	case WHEN:
	case IF:
	    exec = (c.getElement () == 1);
	    break;
	case ELIF:
	    {
		DataStackElement prevc = vm.at (2);
		if (prevc.getType () != ByteCode.Type.BOOLEAN) {
		    VmException.raiseUnexpectedValueOnStack ();
		}
		exec = (prevc.getElement () == 0 && c.getElement () == 1);
		break;
	    }
	case UNLESS:
	case ELSE:
	    exec = (c.getElement () == 0);
	    break;
	}
	return exec;
    }

    private Cond cond;
}