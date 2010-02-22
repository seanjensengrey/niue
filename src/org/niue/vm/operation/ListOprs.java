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

import java.util.List;
import java.util.Stack;
import org.niue.vm.IVmOperation;
import org.niue.vm.Vm;
import org.niue.vm.VmException;
import org.niue.vm.DataStackElement;

public final class ListOprs implements IVmOperation {
    
    public enum Operator { AT, REMOVE, REMOVE_ALL, GET };
    
    public ListOprs (Operator opr) {
        operator = opr;
    }

    public void execute (Vm vm) throws VmException {
	Stack<DataStackElement> dataStack = vm.getDataStack ();
	switch (operator) {
	case AT:
	    {
		vm.push (dataStack.get (vm.popInteger ()));
		break;
	    }
	case REMOVE:
	    {
		dataStack.remove (vm.popInteger ());
		break;
	    }
	case REMOVE_ALL:
	    {
		dataStack.removeAllElements ();
		break;
	    }
	case GET:
	    {
		DataStackElement key = vm.pop ();
		int sz = dataStack.size ();
		boolean found = false;
		for (int i = 0; i < sz; ++i) {
		    if (found) {
			dataStack.push (dataStack.get (i));
			break;
		    }
		    found = Cmpr.equals (key, dataStack.get (i), vm, false);
		}
		break;
	    }
	}
    }
    
    private Operator operator;
}
