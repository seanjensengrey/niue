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

import java.util.Stack;
import java.util.EmptyStackException;
import java.util.Hashtable;
import java.io.PrintStream;
import java.math.BigInteger;

import org.niue.vm.operation.ByteCodeExecutor;

public final class Vm {

    public Vm () {
	initVmOperations ();
	dataStack = new Stack<DataStackElement> ();
	if (vmStack.size () == 0) 
	    vmStack.push (this);
    }

    public Vm (Vm parent) {
	initVmOperations ();
	parentVm = parent;
        dataStack = parentVm.dataStack;
	out = parent.out;
    }

    public void execute (String token) throws VmException {
	executeFor (vmStack.peek (), token);
    }

    public void run () throws VmException {
	assertNotStopped (this);
	if (byteCodes != null) {
	    ByteCodeExecutor executor = new ByteCodeExecutor (byteCodes);
	    executor.execute (this);
	}
    }

    public static void assertNotStopped (Vm vm) throws VmException {
	if (vm.state.stopped) {
	    throw new VmException ("Virtual Machine has been stopped.");
	}
    }

    public static void executeFor (Vm vm, String token) throws VmException {
	assertNotStopped (vm);
	try {
	    vm.executeToken (token);		
	} catch (Exception ex) {
	    throw new VmException (ex);
	}
    }
    
    public void addOperation (String name, IVmOperation opr) {
	vmOperations.put (name.hashCode (), opr);
    }	

    public void setOutput (PrintStream out) {
	this.out = out;
    }

    public boolean isStopped () {
	return state.stopped;
    }

    public Stack<DataStackElement> getDataStack () {
	return dataStack;
    }

    public DataStackElement peek () throws VmException {
	try {
	    return dataStack.peek ();
	} catch (EmptyStackException ex) {
	    throw new VmException (EMPTY_STACK_MSG);
	}
    }

    public DataStackElement at (int i) throws VmException {
	try {
	    return dataStack.elementAt (dataStack.size () - (i + 1));
	} catch (ArrayIndexOutOfBoundsException ex) {
	    throw new VmException ("Invalid stack index.");
	}
    }

    public int popNumber () throws VmException {
	DataStackElement elem = pop ();
	if (elem.getType () != ByteCode.Type.INTEGER)
	    throw new VmException ("Not a number.");
	return elem.getElement ();
    }

    public void push (DataStackElement i) {
	dataStack.push (i);
    }

    public DataStackElement pop () throws VmException {
	try {
	    return dataStack.pop ();
	} catch (EmptyStackException ex) {
            if (parentVm != null) {
                return parentVm.pop ();
            } else {
                throw new VmException (EMPTY_STACK_MSG);
            }
	}
    }

    public void executeWord (int wordId) throws VmException {
	if (!executeVar (wordId)) {
	    IVmOperation opr = getOperation (wordId);
	    if (opr == null) {
		throw new VmException ("Invalid VM operation.");
	    }
	    opr.execute (this);
	}
    }

    public void write (String s) {
	if (out != null) {
	    out.print (s);
	}
    }

    public void writeLine (String s) {
	if (out != null) {
	    out.println (s);
	}
    }

    public void writeLine () {
	if (out != null) {
	    out.println ();
	}
    }

    public void stop () {        
	cleanup ();
	state.stopped = true;
    }

    public String getDataStackElementValue (DataStackElement elem) 
	throws VmException {
	switch (elem.getType ()) {
	case BOOLEAN:
	    {
		if (elem.getElement () == 1)
		    return "true";
		else
		    return "false";
	    }
	case INTEGER:
	    return Integer.toString (elem.getElement ());
	case BIGINTEGER:
	case DOUBLE:
	    return getNumber (elem.getElement ()).toString ();
	case STRING:
	    return getString (elem.getElement ());
	case VM:
	    return getVm (elem.getElement ()).toString ();
	}
	return "";
    }

    public void pushBoolean (boolean b) {
	if (b) 
	    pushBoolean (1);
	else 
	    pushBoolean (0);
    }

    public void pushBoolean (int i) {
	push (i, ByteCode.Type.BOOLEAN);
    }
	
    public void pushInteger (int i) {
	push (i, ByteCode.Type.INTEGER);
    }

    public void pushNumber (int hc, ByteCode.Type type) 
	throws VmException {
	getNumber (hc); // Just to make sure that Number was interned.
	push (hc, type);
    }

    public void pushNumber (Number n, ByteCode.Type type,
			    boolean intern) {
	int hc = 0;
	if (intern) {
	    hc = internNumber (n);
	} else {
	    hc = n.hashCode ();
	}
	push (hc, type);
    }

    public void pushBigInteger (BigInteger bi) {
	pushNumber (bi, ByteCode.Type.BIGINTEGER, true);
    }

    public void pushDouble (Double d) {
	pushNumber (d, ByteCode.Type.DOUBLE, true);
    }

    public void pushString (int hc) throws VmException {
	getString (hc); // Just to make sure that string was interned.
	push (hc, ByteCode.Type.STRING);
    }

    public void pushString (String str, boolean intern) {
	int hc = 0;
	if (intern)
	    hc = internString (str);
	else
	    hc = str.hashCode ();
	push (hc, ByteCode.Type.STRING);
    }

    private boolean isFact (int hc) {
	return (hc == "fact".hashCode ());
    }

    public void executeByteCode (ByteCode bc) throws VmException {
	switch (bc.type) {
	case WORD:
	    {	
		executeWord (bc.code);
		break;
	    }
	case BOOLEAN:
	    pushBoolean (bc.code);
	    break;
	case INTEGER:
	    pushInteger (bc.code);
	    break;
	case BIGINTEGER:
	case DOUBLE:
	    pushNumber (bc.code, bc.type);
	    break;
	case STRING:
	    pushString (bc.code);
	    break;
	case VM:
	    pushVm (bc.code);
	    break;
	}
    }

    public void forget (String varName) {
	int hc = varName.hashCode ();
	DataStackElement elem = vars.get (hc);
	if (elem != null) {
	    removeVar (hc, elem);
	}
    }

    public void setCompilationMode (boolean b) {
	compilationMode = b;
	if (compilationMode) byteCodes = null;
    }

    public void runChildVm (int vmId, boolean discard) 
	throws VmException {
	Vm vm = vmTable.get (vmId);
	vm.run ();
	if (discard && parentVm == null) {
	    discardChildVm (vm, vmId);
	}
    }

    public void discardChildVm (int vmId) {
	Vm vm = vmTable.get (vmId);
	if (parentVm == null) {
	    discardChildVm (vm, vmId);
	}
    }

    public void putVar (int hc, DataStackElement var) {
	boolean put = false;
	if (parentVm == null) {
	    put = true;
	} else {
	    put = !parentVm.updateVar (hc, var);
	}
	if (put) {
	    vars.put (hc, var);
	}    
    }

    public Vm getParentVm () {
        return parentVm;
    }

    public void spawn (int vmId) {
	Vm vm = vmTable.get (vmId);
        vm.dataStack = createFrom (this.dataStack);
        if (procController == null || !procController.isAlive ()) {
            procController = new ProcessController (this);
        }
        pushInteger (procController.add (vm));
    }

    public ByteCodes getByteCodes () {
        return byteCodes;
    }

    private boolean updateVar (int hc, DataStackElement var) {
	boolean check = false;
	if (parentVm == null) check = true;
	else {
	    check = !parentVm.updateVar (hc, var);
	}
	if (check) {
	    if (vars.get (hc) != null) {
		vars.put (hc, var);
		return true;
	    }
	}
	return false;
    }

    private void removeVar (int hc, DataStackElement var) {
	vars.remove (hc);
	switch (var.getType ()) {
	case BOOLEAN:
	case INTEGER:
	    break;
	case STRING:
	    stringTable.remove (hc);
	    break;
	case BIGINTEGER:
	case DOUBLE:
	    numberTable.remove (hc);
	    break;
	case VM:
	    vmTable.remove (hc);
	    break;	 
	}
    }

    private void discardChildVm (Vm vm, int vmId) {
	vm.cleanup ();
	vmTable.remove (vmId);
    }

    private void cleanup () {
        stopProcessController ();
	dataStack = null;
	vars.clear ();
	vars = null;	
	stringTable.clear ();
	stringTable = null;
	numberTable.clear ();
	numberTable = null;
	vmTable.clear ();
	vmTable = null;
    }

    private void stopProcessController () {
        if (procController != null && procController.isAlive ()) {
            procController.shutdown ();
            try {
                procController.yield ();
                procController.join (10);
            } catch (InterruptedException ex) { }
        }
    }

    private void push (int hc, ByteCode.Type type) {
	push (new DataStackElement (hc, type));
    }

    private boolean executeVar (int varId) throws VmException {		    
	DataStackElement var = vars.get (varId);
	if (var == null) {
	    if (parentVm != null) {
		return parentVm.executeVar (varId);
	    } else {
		return false;
	    }
	}

	if (var.getType () == ByteCode.Type.VM) {
	    runChildVm (var.getElement (), false);
	} else {
	    push (var);
	}
	return true;
    }

    private IVmOperation getOperation (int id) {
	IVmOperation opr = vmOperations.get (id);
	if (opr == null) {
	    if (parentVm != null) {
		opr = parentVm.getOperation (id);
	    }
	}
	return opr;
    }    

    private String getString (int hc) throws VmException {
	String s = stringTable.get (hc);
	if (s == null) {
	    if (parentVm != null) {
		s = parentVm.getString (hc);
	    }
	}
	if (s == null)
	    throw new VmException ("String was not interned.");
	return s;
    }

    private Number getNumber (int hc) throws VmException {
	Number n = numberTable.get (hc);
	if (n == null) {
	    if (parentVm != null) {
		n = parentVm.getNumber (hc);
	    }
	}
	if (n == null)
	    throw new VmException ("Number was not interned.");
	return n;
    }

    private Vm getVm (int hc) {
	Vm vm = vmTable.get (hc);
	if (vm == null) {
	    if (parentVm != null) {
		vm = parentVm.getVm (hc);
	    }
	}
	return vm;
    }

    private void executeToken (String token) throws VmException {
	int hc = token.hashCode ();
	if (hc == BLOCK_START) {
	    blockStart ();
	} else if (hc == BLOCK_END) {
	    blockEnd ();	
	} else {
	    ByteCode bc = compileToken (token);
	    if (compilationMode) {
		addByteCode (bc);
	    } else {
		executeByteCode (bc);
	    }
	}
    }

    private void blockStart () {
	Vm currentVm = new Vm (this);
	currentVm.setCompilationMode (true);
	vmStack.push (currentVm);
    }

    private void blockEnd () {
	Vm currentVm = vmStack.pop ();
	currentVm.setCompilationMode (false);
	parentVm.pushVm (currentVm);
    }

    private void addByteCode (ByteCode bc) {
	if (byteCodes == null)
	    byteCodes = new ByteCodes ();
	byteCodes.add (bc);
    }

    private boolean isString (String token) {
	return (token.charAt (0) == '\"');
    }

    private boolean isBoolean (String token) {
	return (token.equals ("true") || token.equals ("false"));
    }

    private Integer parseInt (String token) {
	if (VmNumber.hasLargeNumberPrefix (token))
	    return null;
	try {
	    if (token.charAt (0) == '0' && token.length () > 1) {
		char c = token.charAt (1);
		if (c == 'x' || c == 'X')
		    return Integer.parseInt (token.substring (2), 16);
		else
		    return Integer.parseInt (token, 8);
	    }
	    return Integer.parseInt (token);
	} catch (NumberFormatException ex) {
	    return null;
	}
    }

    private int internString (String s) {
	String str = s;
	if (s.charAt (0) == '\"')
	    str = s.substring (1, s.length () - 1);
	int hc = str.hashCode ();
	if (stringTable.get (hc) == null) {
	    stringTable.put (hc, str);
	    if (parentVm != null)
		parentVm.internString (s);
	}
	return hc;
    }

    private int internNumber (Number n) {
	int hc = n.hashCode ();
	if (numberTable.get (hc) == null) {
	    numberTable.put (hc, n);
	    if (parentVm != null)
		parentVm.internNumber (n);
	}
	return hc;
    }

    private int internVm (Vm vm) {
	if (childVmCount >= (Integer.MAX_VALUE - 1))
	    childVmCount = 0;
	int hc = childVmCount++;
	vmTable.put (hc, vm);
	return hc;
    }

    private void pushVm (Vm vm) {
	int vmId = internVm (vm);
	if (compilationMode) {
	    addByteCode (new ByteCode (ByteCode.Type.VM, vmId));
	} else {
	    pushVm (vmId);
	}
    }

    private void pushVm (int vmId) {
	push (vmId, ByteCode.Type.VM);
    }

    private ByteCode compileToken (String token) throws VmException {
	ByteCode bc = new ByteCode ();
	Integer i = parseInt (token);
	if (i != null) {
	    bc.type = ByteCode.Type.INTEGER;
	    bc.code = i;
	} else {
	    VmNumber vmNum = VmNumber.parse (token);
	    if (vmNum != null) {
		bc.code = internNumber (vmNum.getNumber ());
		bc.type = vmNum.getType ();
	    } else {
		if (isString (token)) {
		    int hc = internString (token);
		    bc.type = ByteCode.Type.STRING;
		    bc.code = hc;
		} else if (isBoolean (token)) {		    
		    bc.type = ByteCode.Type.BOOLEAN;
		    bc.code = (token.charAt (0) == 't' ? 1 : 0);
		} else {
		    bc.type = ByteCode.Type.WORD;
		    bc.code = token.hashCode ();
		}
	    }
	}
	return bc;
    }

    private void initVmOperations () {
	vmOperations = DefaultWords.getDefaultOperation ();
    }

    private static Stack<DataStackElement> createFrom (Stack<DataStackElement> stack) {
        Stack<DataStackElement> s = new Stack<DataStackElement> ();
        int sz = stack.size ();
        for (int i = 0; i < sz; ++i) {
            s.push (stack.elementAt (i));
        }
        return s;
    }

    private VmState state = new VmState ();
    private boolean compilationMode = false;
    private ByteCodes byteCodes = null;
    private Stack<DataStackElement> dataStack = null;
    private PrintStream out = null;
    private Hashtable<Integer, IVmOperation> vmOperations = null;
    private Hashtable<Integer, String> stringTable = 
	new Hashtable<Integer, String> ();
    private Hashtable<Integer, Number> numberTable = 
	new Hashtable<Integer, Number> (); 
    private Hashtable<Integer, Vm> vmTable = new Hashtable<Integer, Vm> ();
    private static Stack<Vm> vmStack = new Stack<Vm> ();
    private Vm parentVm = null;
    private int childVmCount = 0;
    private Hashtable<Integer, DataStackElement> vars = 
	new Hashtable<Integer, DataStackElement> ();
    private ProcessController procController = null;

    public static final String EMPTY_STACK_MSG = "<empty-stack>";
    static final int COLON_DEF = ":".hashCode ();
    static final int COLON_DEF_END = ";".hashCode ();
    static final int BLOCK_START = "[".hashCode ();
    static final int BLOCK_END = "]".hashCode ();
}
