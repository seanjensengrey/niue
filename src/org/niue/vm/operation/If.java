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

public final class If implements IVmOperation {
    
    public enum Cond { WHEN_TRUE, WHEN_FALSE, IF_TRUE, ELSE };

    public If (Cond cond) {
	this.cond = cond;
    }
    
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
	boolean exec = shouldExecute (c);
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
			       boolean exec) {
	if (cond == Cond.IF_TRUE)
	    vm.push (c);
    }

    private boolean shouldExecute (DataStackElement c) {
	boolean exec = false;
	switch (cond) {
	case WHEN_TRUE:
	case IF_TRUE:
	    exec = (c.getElement () == 1);
	    break;
	case WHEN_FALSE:
	case ELSE:
	    exec = (c.getElement () == 0);
	    break;
	}
	return exec;
    }

    private Cond cond;
}