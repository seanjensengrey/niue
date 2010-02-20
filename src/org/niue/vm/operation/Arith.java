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

public final class Arith implements IVmOperation {
    
    public enum Operator { ADD, SUB, MULT, DIV, MOD, DIV_MOD };

    public Arith (Operator opr) {
	operator = opr;
    }
    
    public void execute (Vm vm) throws VmException {
	DataStackElement elem2 = vm.pop ();
	DataStackElement elem1 = vm.pop ();
	try {
	    switch (operator) {
	    case ADD:
		add (elem1, elem2, vm);
		break;
	    case SUB:
		sub (elem1, elem2, vm);
		break;
	    case MULT:
		mult (elem1, elem2, vm);
		break;
	    case DIV:
		div (elem1, elem2, vm);
		break;
	    case MOD:
		mod (elem1, elem2, vm);
		break;
	    case DIV_MOD:
		divMod (elem1, elem2, vm);
		break;
	    }
	} catch (VmException ex) {
	    throw ex;
	}
    }

    private void add (DataStackElement elem1, DataStackElement elem2,
		      Vm vm) throws VmException {
	ByteCode.Type type1 = elem1.getType ();
	ByteCode.Type type2 = elem2.getType ();
	if (type1 == ByteCode.Type.STRING || type2 == ByteCode.Type.STRING) {
	    addStrings (elem1, elem2, vm);
	} else {
	    if (type1 == ByteCode.Type.INTEGER 
		&& type2 == ByteCode.Type.INTEGER) {
		addInts (elem1, elem2, vm);
	    } else if (type1 == ByteCode.Type.BIGINTEGER
		       || type2 == ByteCode.Type.BIGINTEGER) {
		addBigInts (elem1, elem2, vm);
	    } else if (type1 == ByteCode.Type.DOUBLE
		       || type2 == ByteCode.Type.DOUBLE) {
		addDoubles (elem1, elem2, vm);
	    } else {
		VmException.raiseUnexpectedValueOnStack();
	    }
	}
    }
    
    private void addStrings (DataStackElement elem1, DataStackElement elem2,
			     Vm vm) throws VmException {
	vm.pushString ((vm.getDataStackElementValue (elem1) + 
			vm.getDataStackElementValue (elem2)),
		       true);
    }

    private void addInts (DataStackElement elem1, DataStackElement elem2,
			  Vm vm) {
	vm.pushInteger (elem1.getElement () + elem2.getElement ());
    }

    private void addBigInts (DataStackElement elem1, DataStackElement elem2,
			     Vm vm) throws VmException {
	BigInteger bi1 = new BigInteger (vm.getDataStackElementValue (elem1));
	BigInteger bi2 = new BigInteger (vm.getDataStackElementValue (elem2));
	vm.pushBigInteger (bi1.add (bi2));
    }
    
    private void addDoubles (DataStackElement elem1, DataStackElement elem2,
			     Vm vm) throws VmException {
	Double d1 = new Double (vm.getDataStackElementValue (elem1));
	Double d2 = new Double (vm.getDataStackElementValue (elem2));
	Double d3 = new Double (d1.doubleValue () + d2.doubleValue ());
	vm.pushDouble (d3);
    }

    private void sub (DataStackElement elem1, DataStackElement elem2,
		      Vm vm) throws VmException {
	ByteCode.Type type1 = elem1.getType ();
	ByteCode.Type type2 = elem2.getType ();
	if (type1 == ByteCode.Type.INTEGER 
	    && type2 == ByteCode.Type.INTEGER) {
	    subInts (elem1, elem2, vm);
	} else if (type1 == ByteCode.Type.BIGINTEGER
		   || type2 == ByteCode.Type.BIGINTEGER) {
	    subBigInts (elem1, elem2, vm);
	} else if (type1 == ByteCode.Type.DOUBLE
		   || type2 == ByteCode.Type.DOUBLE) {
	    subDoubles (elem1, elem2, vm);
	} else {
	    VmException.raiseUnexpectedValueOnStack();
	}	
    }

    private void subInts (DataStackElement elem1, DataStackElement elem2,
			  Vm vm) {
	vm.pushInteger (elem1.getElement () - elem2.getElement ());
    }

    private void subBigInts (DataStackElement elem1, DataStackElement elem2,
			     Vm vm) throws VmException {
	BigInteger bi1 = new BigInteger (vm.getDataStackElementValue (elem1));
	BigInteger bi2 = new BigInteger (vm.getDataStackElementValue (elem2));
	vm.pushBigInteger (bi1.subtract (bi2));
    }
    
    private void subDoubles (DataStackElement elem1, DataStackElement elem2,
			     Vm vm) throws VmException {
	Double d1 = new Double (vm.getDataStackElementValue (elem1));
	Double d2 = new Double (vm.getDataStackElementValue (elem2));
	Double d3 = new Double (d1.doubleValue () - d2.doubleValue ());
	vm.pushDouble (d3);
    }

    private void mult (DataStackElement elem1, DataStackElement elem2,
		      Vm vm) throws VmException {
	ByteCode.Type type1 = elem1.getType ();
	ByteCode.Type type2 = elem2.getType ();
	if (type1 == ByteCode.Type.INTEGER 
	    && type2 == ByteCode.Type.INTEGER) {
	    multInts (elem1, elem2, vm);
	} else if (type1 == ByteCode.Type.BIGINTEGER
		   || type2 == ByteCode.Type.BIGINTEGER) {
	    multBigInts (elem1, elem2, vm);
	} else if (type1 == ByteCode.Type.DOUBLE
		   || type2 == ByteCode.Type.DOUBLE) {
	    multDoubles (elem1, elem2, vm);
	} else {
	    VmException.raiseUnexpectedValueOnStack();
	}	
    }

    private void multInts (DataStackElement elem1, DataStackElement elem2,
			  Vm vm) {
	vm.pushInteger (elem1.getElement () * elem2.getElement ());
    }

    private void multBigInts (DataStackElement elem1, DataStackElement elem2,
			     Vm vm) throws VmException {
	BigInteger bi1 = new BigInteger (vm.getDataStackElementValue (elem1));
	BigInteger bi2 = new BigInteger (vm.getDataStackElementValue (elem2));
	vm.pushBigInteger (bi1.multiply (bi2));
    }
    
    private void multDoubles (DataStackElement elem1, DataStackElement elem2,
			     Vm vm) throws VmException {
	Double d1 = new Double (vm.getDataStackElementValue (elem1));
	Double d2 = new Double (vm.getDataStackElementValue (elem2));
	Double d3 = new Double (d1.doubleValue () * d2.doubleValue ());
	vm.pushDouble (d3);
    }

    private void div (DataStackElement elem1, DataStackElement elem2,
		      Vm vm) throws VmException {
	ByteCode.Type type1 = elem1.getType ();
	ByteCode.Type type2 = elem2.getType ();
	if (type1 == ByteCode.Type.INTEGER 
	    && type2 == ByteCode.Type.INTEGER) {
	    divInts (elem1, elem2, vm);
	} else if (type1 == ByteCode.Type.BIGINTEGER
		   || type2 == ByteCode.Type.BIGINTEGER) {
	    divBigInts (elem1, elem2, vm);
	} else if (type1 == ByteCode.Type.DOUBLE
		   || type2 == ByteCode.Type.DOUBLE) {
	    divDoubles (elem1, elem2, vm);
	} else {
	    VmException.raiseUnexpectedValueOnStack();
	}	
    }

    private void divInts (DataStackElement elem1, DataStackElement elem2,
			  Vm vm) {
	vm.pushInteger (elem1.getElement () / elem2.getElement ());
    }

    private void divBigInts (DataStackElement elem1, DataStackElement elem2,
			     Vm vm) throws VmException {
	BigInteger bi1 = new BigInteger (vm.getDataStackElementValue (elem1));
	BigInteger bi2 = new BigInteger (vm.getDataStackElementValue (elem2));
	vm.pushBigInteger (bi1.divide (bi2));
    }
    
    private void divDoubles (DataStackElement elem1, DataStackElement elem2,
			     Vm vm) throws VmException {
	Double d1 = new Double (vm.getDataStackElementValue (elem1));
	Double d2 = new Double (vm.getDataStackElementValue (elem2));
	Double d3 = new Double (d1.doubleValue () / d2.doubleValue ());
	vm.pushDouble (d3);
    }

    private void mod (DataStackElement elem1, DataStackElement elem2,
		      Vm vm) throws VmException {
	ByteCode.Type type1 = elem1.getType ();
	ByteCode.Type type2 = elem2.getType ();
	if (type1 == ByteCode.Type.INTEGER 
	    && type2 == ByteCode.Type.INTEGER) {
	    modInts (elem1, elem2, vm);
	} else if (type1 == ByteCode.Type.BIGINTEGER
		   || type2 == ByteCode.Type.BIGINTEGER) {
	    modBigInts (elem1, elem2, vm);
	} else if (type1 == ByteCode.Type.DOUBLE
		   || type2 == ByteCode.Type.DOUBLE) {
	    modDoubles (elem1, elem2, vm);
	} else {
	    VmException.raiseUnexpectedValueOnStack();
	}	
    }

    private void modInts (DataStackElement elem1, DataStackElement elem2,
			  Vm vm) {
	vm.pushInteger (elem1.getElement () % elem2.getElement ());
    }

    private void modBigInts (DataStackElement elem1, DataStackElement elem2,
			     Vm vm) throws VmException {
	BigInteger bi1 = new BigInteger (vm.getDataStackElementValue (elem1));
	BigInteger bi2 = new BigInteger (vm.getDataStackElementValue (elem2));
	vm.pushBigInteger (bi1.mod (bi2));
    }
    
    private void modDoubles (DataStackElement elem1, DataStackElement elem2,
			     Vm vm) throws VmException {
	Double d1 = new Double (vm.getDataStackElementValue (elem1));
	Double d2 = new Double (vm.getDataStackElementValue (elem2));
	Double d3 = new Double (d1.doubleValue () % d2.doubleValue ());
	vm.pushDouble (d3);
    }

    private void divMod (DataStackElement elem1, DataStackElement elem2,
			 Vm vm) throws VmException {
	ByteCode.Type type1 = elem1.getType ();
	ByteCode.Type type2 = elem2.getType ();
	if (type1 == ByteCode.Type.INTEGER 
	    && type2 == ByteCode.Type.INTEGER) {
	    divModInts (elem1, elem2, vm);
	} else if (type1 == ByteCode.Type.BIGINTEGER
		   || type2 == ByteCode.Type.BIGINTEGER) {
	    divModBigInts (elem1, elem2, vm);
	} else if (type1 == ByteCode.Type.DOUBLE
		   || type2 == ByteCode.Type.DOUBLE) {
	    divModDoubles (elem1, elem2, vm);
	} else {
	    VmException.raiseUnexpectedValueOnStack();
	}	
    }

    private void divModInts (DataStackElement elem1, DataStackElement elem2,
			  Vm vm) {
	int e1 = elem1.getElement ();
	int e2 = elem2.getElement ();
	int q = e1 / e2;
	int r = e1 % e2;
	vm.pushInteger (r);
	vm.pushInteger (q);
    }

    private void divModBigInts (DataStackElement elem1, DataStackElement elem2,
			     Vm vm) throws VmException {
	BigInteger bi1 = new BigInteger (vm.getDataStackElementValue (elem1));
	BigInteger bi2 = new BigInteger (vm.getDataStackElementValue (elem2));
	BigInteger q = bi1.divide (bi2);
	BigInteger r = bi1.mod (bi2);
	vm.pushBigInteger (r);
	vm.pushBigInteger (q);
    }
    
    private void divModDoubles (DataStackElement elem1, DataStackElement elem2,
			     Vm vm) throws VmException {
	Double d1 = new Double (vm.getDataStackElementValue (elem1));
	Double d2 = new Double (vm.getDataStackElementValue (elem2));
	double dv1 = d1.doubleValue ();
	double dv2 = d2.doubleValue ();
	Double q = new Double (dv1 / dv2);
	Double r = new Double (dv1 % dv2);
	vm.pushDouble (r);
	vm.pushDouble (q);
    }

    private Operator operator = Operator.ADD;
}
