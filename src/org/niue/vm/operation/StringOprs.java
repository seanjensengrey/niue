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

public final class StringOprs implements IVmOperation {
    
    public enum Operator { STR_LEN, STR_AT, STR_EQ, STR_EQI,
	    STR_TOUPPER, STR_TOLOWER, STR_TRIM, SUBSTR };
    
    public StringOprs (Operator opr) {
        operator = opr;
    }

    public void execute (Vm vm) throws VmException {
	String str1 = null;
	if (operator != Operator.STR_AT 
	    && operator != Operator.SUBSTR) {
	    str1 = vm.popString ();
	}
	switch (operator) {
	case STR_LEN:
	    {
		vm.pushInteger (str1.length ());
		break;
	    }
	case STR_AT:
	    {
		int idx = vm.popInteger ();
		str1 = vm.popString ();
		vm.pushInteger (Character.getNumericValue (str1.charAt (idx)));
		break;
	    }
	case STR_EQ:
	case STR_EQI:
	    {
		String str2 = vm.popString ();
		if (operator == Operator.STR_EQ)
		    vm.pushBoolean (str1.equals (str2));
		else
		    vm.pushBoolean (str1.equalsIgnoreCase (str2));
		break;
	    }
	case STR_TOUPPER:
	    {
		vm.pushString (str1.toUpperCase ());
		break;
	    }
	case STR_TOLOWER:
	    {
		vm.pushString (str1.toLowerCase ());
		break;
	    }
	case STR_TRIM:
	    {
		vm.pushString (str1.trim ());
		break;
	    }
	case SUBSTR:
	    {
		int idxEnd = vm.popInteger ();
		int idxStart = vm.popInteger ();
		str1 = vm.popString ();
		vm.pushString (str1.substring (idxStart, idxEnd));
		break;
	    }
	}
    }
    
    private Operator operator;
}
