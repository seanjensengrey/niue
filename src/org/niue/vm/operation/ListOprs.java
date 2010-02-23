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
import java.util.List;
import java.util.Stack;
import org.niue.vm.IVmOperation;
import org.niue.vm.Vm;
import org.niue.vm.VmException;
import org.niue.vm.DataStackElement;

// Implements some words that treats the data stack as a 
// linked list. 

public final class ListOprs implements IVmOperation {
    
    public enum Operator { AT, REMOVE, REMOVE_ALL, GET,
	    REVERSE, BSEARCH, SORT};
    
    public ListOprs (Operator opr) {
        operator = opr;
    }

    public void execute (Vm vm) throws VmException {
	switch (operator) {
	case AT:
	    {
		at (vm);
		break;
	    }
	case REMOVE:
	    {
		remove (vm);
		break;
	    }
	case REMOVE_ALL:
	    {
		remove_all (vm);
		break;
	    }
	case GET:
	    {
		get (vm);
		break;
	    }
	case REVERSE:
	    {
		reverse (vm);
		break;
	    }
	case SORT:
	    {
		sort (vm);
		break;
	    }
	case BSEARCH:
	    {
		bsearch (vm);
		break;
	    }
	}
    }

    private void at (Vm vm) throws VmException {
	Stack<DataStackElement> dataStack = vm.getDataStack ();
	vm.push (dataStack.get (vm.popInteger ()));
    }

    private void remove (Vm vm) throws VmException {
	Stack<DataStackElement> dataStack = vm.getDataStack ();
	dataStack.remove (vm.popInteger ());
    }

    private void remove_all (Vm vm) throws VmException {
	Stack<DataStackElement> dataStack = vm.getDataStack ();
	dataStack.removeAllElements ();
    }

    private void get (Vm vm) throws VmException {
	Stack<DataStackElement> dataStack = vm.getDataStack ();
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
    }

    private void reverse (Vm vm) throws VmException {
	Stack<DataStackElement> dataStack = vm.getDataStack ();
	Collections.reverse (dataStack);
    }

    @SuppressWarnings("unchecked") private void sort (Vm vm) 
	throws VmException {
	Stack<DataStackElement> dataStack = vm.getDataStack ();
	Collections.sort (dataStack);
    }

    @SuppressWarnings("unchecked") private void bsearch (Vm vm) 
	throws VmException {
	Stack<DataStackElement> dataStack = vm.getDataStack ();
	try {
	    int idx = Collections.binarySearch ((List) dataStack,
						(Object) 
						dataStack.pop ());
	    vm.pushInteger (idx);	    
	} catch (ClassCastException ex) {
	    throw new VmException (ex.getMessage ());
	}
    }

    private Operator operator;
}
