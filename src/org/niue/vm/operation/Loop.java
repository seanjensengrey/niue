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

public final class Loop implements IVmOperation {
    
    public enum Type { TIMES, TIMES_BY, WHILE };

    public Loop (Type type) {
	this.type = type;
    }
    
    public void execute (Vm vm) throws VmException {
	switch (type) {
	case TIMES:
	case TIMES_BY:
	    times (vm);
	    break;
	case WHILE:
	    whileLoop (vm);
	    break;
	}
    }

    private void times (Vm vm) throws VmException {    
	DataStackElement c = vm.at (0);
	if (c.getType () != ByteCode.Type.INTEGER) {
	    VmException.raiseUnexpectedValueOnStack ();
	}
	int blockIndex = 1;
	int incrementBy = 1;
	if (type == Type.TIMES_BY)  {
	    blockIndex = 2;
	    DataStackElement i = vm.at (0);
	    if (i.getType () != ByteCode.Type.INTEGER) {
		VmException.raiseUnexpectedValueOnStack ();
	    }
	    incrementBy = i.getElement ();
	}
	DataStackElement block = vm.at (blockIndex);
	ByteCode.Type tp = block.getType ();
	if (tp != ByteCode.Type.VM && tp != ByteCode.Type.STRING) {
	    VmException.raiseUnexpectedValueOnStack ();
	}

	vm.pop ();
	vm.pop ();
	if (type == Loop.Type.TIMES_BY) vm.pop ();

	int t = c.getElement ();
	int id = block.getElement ();
	if (tp == ByteCode.Type.VM) {
	    try {
		for (int i = 0; i < t; i += incrementBy) {
		    vm.pushInteger (i);
		    vm.runChildVm (id, false);	
		}
	    } catch (VmException ex) {
		throw ex;
	    } finally {
		vm.discardChildVm (id);
	    }
	} else {
	    for (int i = 0; i < t; i += incrementBy) {
		vm.pushInteger (i);
		vm.executeWord (id);		
	    }
	}
    }

    private void whileLoop (Vm vm) throws VmException {
	DataStackElement block = vm.at (0);
	ByteCode.Type type = block.getType ();
	if (type != ByteCode.Type.VM && type != ByteCode.Type.STRING) {
	    VmException.raiseUnexpectedValueOnStack ();
	}
	boolean exec = shouldExecute (vm.at (1));
	vm.pop ();
	vm.pop ();
	if (exec) {
	    int id = block.getElement ();
	    if (type == ByteCode.Type.VM) {
		try {
		    while (exec) {
			vm.runChildVm (id, false);
			exec = shouldExecute (vm.pop ());
		    }
		} catch (VmException ex) {
		    throw ex;
		} finally {
		    vm.discardChildVm (id);
		}
	    } else {
		while (exec) {
		    vm.executeWord (id);
		    exec = shouldExecute (vm.pop ());
		}
	    }
	}
    }

    private boolean shouldExecute (DataStackElement c) 
	throws VmException {
        ByteCode.Type t = c.getType ();
	if (t != ByteCode.Type.BOOLEAN) {
	    VmException.raiseUnexpectedValueOnStack ();
	}
	return (c.getElement () == 1);
    }

    private Type type;
}