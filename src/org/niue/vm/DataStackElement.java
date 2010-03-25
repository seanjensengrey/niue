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

package org.niue.vm;

import org.niue.vm.operation.Cmpr;

// Represents an entry on the data stack. 

public class DataStackElement implements Comparable {

    public DataStackElement (Vm vm) {
	element = 0;
	type = ByteCode.Type.INTEGER;
	this.vm = vm;
    }

    public DataStackElement (int e, ByteCode.Type t, Vm vm) {
	element = e;
	type = t;
	this.vm = vm;
    }

    public int getElement () { return element; }
    public ByteCode.Type getType () { return type; }

    public void set (DataStackElement elem) {
        this.element = elem.element;
        this.type = elem.type;
        this.vm = elem.vm;
    }

    @Override public int compareTo (Object obj) {
	DataStackElement e = (DataStackElement) obj;
	Cmpr.Operator opr = Cmpr.Operator.EQ;
	if (e.type == ByteCode.Type.STRING 
	    && type == ByteCode.Type.STRING) {
	    opr = Cmpr.Operator.EQUALS;
	}
	Cmpr c = new Cmpr (opr);
	try {
	    c.execute (vm, this, e);
	    if (vm.popBoolean ()) return 0;
	    c.setOperator (Cmpr.Operator.LT);
	    c.execute (vm, this, e);
	    if (vm.popBoolean ()) return -1;
	    c.setOperator (Cmpr.Operator.GT);
	    c.execute (vm, this, e);
	    if (vm.popBoolean ()) return 1;
	} catch (VmException ex) {
	    return -2; // Not a proper way to handle this exception. 
	} catch (ClassCastException ex) {
	    return -3;
	}
	return 0;
    }

    @Override public boolean equals (Object obj) {
	if (obj == null) return false;
	if (obj == this) return true;
	if (obj instanceof DataStackElement) {
	    DataStackElement e = (DataStackElement)obj;
	    return (compareTo (e) == 0);
	}
	return false;
    }

    private int element;
    private ByteCode.Type type;
    private Vm vm = null;
}
