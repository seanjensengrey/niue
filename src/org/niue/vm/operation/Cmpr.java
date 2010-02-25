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
import java.math.BigInteger;

// Implements the comparison operators. 

public final class Cmpr implements IVmOperation {
    
    public enum Operator { EQUALS, EQ, LT, GT, LT_EQ, GT_EQ };

    public Cmpr (Operator opr) {
	operator = opr;
    }

    public void setOperator (Operator opr) {
	operator = opr;
    }
    
    public void execute (Vm vm) throws VmException {
	DataStackElement elem2 = vm.pop ();
	DataStackElement elem1 = vm.pop ();
	execute (vm, elem1, elem2);
    }

    public void execute (Vm vm, DataStackElement elem1,
			 DataStackElement elem2) 
	throws VmException {
	switch (operator) {
	case EQUALS:
	    equals (elem1, elem2, vm, true);
	    break;
	case EQ:
	    eq (elem1, elem2, vm);
	    break;
	case LT:
	    lt (elem1, elem2, vm);
	    break;
	case GT:
	    gt (elem1, elem2, vm);
	    break;
	case LT_EQ:
	    ltEq (elem1, elem2, vm);
	    break;
	case GT_EQ:
	    gtEq (elem1, elem2, vm);
	    break;
	}
    }

    // The coarsest definition of equality.  Puts true on the stack if
    // the string representations of elem1 and elem2 are the same.  This
    // means, both [ 1 1 = ] and [ 1 "1" = ] will return true.  
    public static boolean equals (DataStackElement elem1, 
				  DataStackElement elem2,
				  Vm vm, boolean pushResult) 
	throws VmException {
	boolean b = vm.getDataStackElementValue (elem1).equals 
	    (vm.getDataStackElementValue (elem2));	
	if (pushResult) {
	    vm.pushBoolean (b);
	}
	return b;
    }

    private void eq (DataStackElement elem1, DataStackElement elem2,
		     Vm vm) throws VmException {
	vm.pushBoolean (elem1.getElement () == elem2.getElement ());
    }    

    private void lt (DataStackElement elem1, DataStackElement elem2,
		     Vm vm) throws VmException {
	applyOpr (Operator.LT, elem1, elem2, vm);
    }

    private void gt (DataStackElement elem1, DataStackElement elem2,
		     Vm vm) throws VmException {
	applyOpr (Operator.GT, elem1, elem2, vm);
    }

    private void ltEq (DataStackElement elem1, DataStackElement elem2,
			Vm vm) throws VmException {
	applyOpr (Operator.LT_EQ, elem1, elem2, vm);
    }

    private void gtEq (DataStackElement elem1, DataStackElement elem2,
			Vm vm) throws VmException {
	applyOpr (Operator.GT_EQ, elem1, elem2, vm);
    }

    private void applyOpr (Operator opr, DataStackElement elem1, 
			    DataStackElement elem2, Vm vm) 
	throws VmException {
	ByteCode.Type type1 = elem1.getType ();
	ByteCode.Type type2 = elem2.getType ();
	if ((type1 == ByteCode.Type.INTEGER 
	     && type2 == ByteCode.Type.INTEGER)
	    || (type1 == ByteCode.Type.BOOLEAN 
		&& type2 == ByteCode.Type.BOOLEAN)) {
	    if (applyOprInt (opr, elem1, elem2, vm))
		return;
	} else {
	    if (type1 == ByteCode.Type.STRING
		|| type2 == ByteCode.Type.STRING) {
		if (applyOprString (opr, elem1, elem2, vm))
		    return;
	    } else if (type1 == ByteCode.Type.BIGINTEGER
		       || type2 == ByteCode.Type.BIGINTEGER) {
		if (applyOprBigInt (opr, elem1, elem2, vm))
		    return;
	    } else if (type1 == ByteCode.Type.DOUBLE
		       || type2 == ByteCode.Type.DOUBLE) {
		if (applyOprDouble (opr, elem1, elem2, vm))
		    return;
	    }
	}
	VmException.raiseUnexpectedValueOnStack();
    }
    
    private boolean applyOprInt (Operator opr, DataStackElement elem1,
				 DataStackElement elem2, Vm vm)
	throws VmException {
	switch (opr) {
	case LT:
	    vm.pushBoolean (elem1.getElement () < elem2.getElement ());
	    break;
	case GT:
	    vm.pushBoolean (elem1.getElement () > elem2.getElement ());
	    break;
	case LT_EQ:
	    vm.pushBoolean (elem1.getElement () <= elem2.getElement ());
	    break;
	case GT_EQ:
	    vm.pushBoolean (elem1.getElement () >= elem2.getElement ());
	    break;
	default:
	    return false;
	}
	return true;
    }
    
    private boolean applyOprString (Operator opr, DataStackElement elem1,
				    DataStackElement elem2, Vm vm)
	throws VmException {
	String s1 = vm.getDataStackElementValue (elem1);
	String s2 = vm.getDataStackElementValue (elem2);
	switch (opr) {
	case LT:
	    vm.pushBoolean ((s1.compareTo (s2)) < 0);
	    break;
	case GT:
	    vm.pushBoolean ((s1.compareTo (s2)) > 0);
	    break;
	case LT_EQ:
	    vm.pushBoolean ((s1.compareTo (s2)) <= 0);
	    break;
	case GT_EQ:
	    vm.pushBoolean ((s1.compareTo (s2)) >= 0);
	    break;
	default:
	    return false;
	}	
	return true;
    }

    private boolean applyOprBigInt (Operator opr, DataStackElement elem1,
				    DataStackElement elem2, Vm vm)
	throws VmException {
	BigInteger s1 = new BigInteger (vm.getDataStackElementValue 
					(elem1));
	BigInteger s2 = new BigInteger (vm.getDataStackElementValue 
					(elem2));
	switch (opr) {
	case LT:
	    vm.pushBoolean ((s1.compareTo (s2)) < 0);
	    break;
	case GT:
	    vm.pushBoolean ((s1.compareTo (s2)) > 0);
	    break;
	case LT_EQ:
	    vm.pushBoolean ((s1.compareTo (s2)) <= 0);
	    break;
	case GT_EQ:
	    vm.pushBoolean ((s1.compareTo (s2)) >= 0);
	    break;
	default:
	    return false;
	}
	return true;
    }	
    
    private boolean applyOprDouble (Operator opr, DataStackElement elem1,
				    DataStackElement elem2, Vm vm)
	throws VmException {
	Double s1 = new Double (vm.getDataStackElementValue (elem1));
	Double s2 = new Double (vm.getDataStackElementValue (elem2));
	switch (opr) {
	case LT:
	    vm.pushBoolean ((s1.compareTo (s2)) < 0);
	    break;
	case GT:
	    vm.pushBoolean ((s1.compareTo (s2)) > 0);
	    break;
	case LT_EQ:
	    vm.pushBoolean ((s1.compareTo (s2)) <= 0);
	    break;
	case GT_EQ:
	    vm.pushBoolean ((s1.compareTo (s2)) >= 0);
	    break;
	default:
	    return false;
	}
	return true;
    }	
    private Operator operator;
}
