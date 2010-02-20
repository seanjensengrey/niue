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
import org.niue.vm.ByteCode;
import org.niue.vm.DataStackElement;

public final class Logical implements IVmOperation {
    
    public enum Operator { AND, OR, NEGATE };

    public Logical (Operator opr) {
	operator = opr;
    }
    
    public void execute (Vm vm) throws VmException {
	if (operator == Operator.NEGATE) {
	    negate (vm);
	} else {
	    DataStackElement elem2 = vm.pop ();
	    DataStackElement elem1 = vm.pop ();
	    if (elem1.getType () == ByteCode.Type.BOOLEAN
		&& elem2.getType () == ByteCode.Type.BOOLEAN) {
		boolean v1 = elem1.getElement () == 1 ? true : false;
		boolean v2 = elem2.getElement () == 1 ? true : false;
		switch (operator) {
		case AND:
		    vm.pushBoolean (v1 && v2);
		    break;
		case OR:
		    vm.pushBoolean (v1 || v2);
		    break;
		}
	    } else {
		VmException.raiseUnexpectedValueOnStack ();
	    }
	}
    }

    private void negate (Vm vm) throws VmException {
	DataStackElement elem1 = vm.pop ();
	if (elem1.getType () == ByteCode.Type.BOOLEAN) {
	    boolean v1 = elem1.getElement () == 1 ? true : false;
	    vm.pushBoolean (!v1);
	} else {
	    VmException.raiseUnexpectedValueOnStack ();
	}
    }

    private Operator operator;
}
