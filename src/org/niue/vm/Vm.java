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

// The simple Niue virtual machine.  Executes its own compiled
// byte codes.  A virtual machine can have many child virtual machines
// which could be executed in the same thread, or spawned to be 
// executed in a separate thread.  N number of spawned virtual machines
// do not take up N number of system threads, instead threads are shared.

public final class Vm {

    // Used to create a root virtual machine.

    public Vm () {
	initVmOperations ();
	dataStack = new Stack<DataStackElement> ();
	if (vmStack.size () == 0) 
	    vmStack.push (this);
    }

    // Used to create a child virtual machine

    private Vm (Vm parent) {
	initVmOperations ();
	parentVm = parent;
        dataStack = parentVm.dataStack;
	out = parent.out;
    }

    // Compiles and executes a token for the virtual machine
    // that is on top of the global stack of virtual machines.
    // There is no text interpretation.  All tokens are compiled
    // before execution.  If the token represents a string or a 
    // large number, it is interned into special tables for future
    // reuse.  

    public void execute (String token) throws VmException {
	executeFor (vmStack.peek (), token);
    }

    // Runs the pre-compiled byte codes.

    public void run () throws VmException {
	assertNotStopped (this);
	if (byteCodes != null) {
	    ByteCodeExecutor executor = new ByteCodeExecutor (byteCodes);
	    executor.execute (this);
	}
    }

    // Makes sure that the virtual machine is in a runnable state.  

    public static void assertNotStopped (Vm vm) throws VmException {
	if (vm.state.stopped) {
	    throw new VmException ("Virtual Machine has been stopped.");
	}
    }


    // Compiles and executes the token within the virtual machine `vm'.

    public static void executeFor (Vm vm, String token) throws VmException {
	assertNotStopped (vm);
	try {
	    vm.executeToken (token);		
	} catch (Exception ex) {
	    throw new VmException (ex);
	}
    }
    
    // Adds a system operation to the virtual machines core set of words.  
    // A system operation is represented as an implementation of IVmOperation.  
    // The virtual machine identifies it with `name'.  This is the main
    // mechanism by which Niue is interfaced with the underlying JVM.  See the
    // classes in org/niue/vm/operation for the default IVmOperation 
    // implementations.  DefaultWords.java deals with the details of adding
    // these words to a new virtual machine.  

    public void addOperation (String name, IVmOperation opr) {
	vmOperations.put (name.hashCode (), opr);
    }	

    // Redirects the virtual machines output stream.  Errors and messages
    // are send there.  

    public void setOutput (PrintStream out) {
	this.out = out;
    }

    // Returns the virtual machin's output stream.  

    public PrintStream getOutput () {
	return out;
    }

    // Returns true if the virtual machines is in the stopped state.

    public boolean isStopped () {
	return state.stopped;
    }

    // Returns a reference to the virtual machine's data stack. 

    public Stack<DataStackElement> getDataStack () {
	return dataStack;
    }

    // Returns the element at the top of the data stack. 

    public DataStackElement peek () throws VmException {
	try {
	    return dataStack.peek ();
	} catch (EmptyStackException ex) {
	    throw new VmException (EMPTY_STACK_MSG);
	}
    }

    // Returns the element at the position `i' of the data stack.  
    // The index is reversed before calling elementAt (). 

    public DataStackElement at (int i) throws VmException {
	try {
	    return dataStack.elementAt (dataStack.size () - (i + 1));
	} catch (ArrayIndexOutOfBoundsException ex) {
	    throw new VmException ("Invalid stack index.");
	}
    }

    // Pops an element from the data stack.  Throws an exception if
    // it is not an integer.  Returns the integer value.  Note that
    // integers are stored directly on the stack.  They need not be 
    // interned like strings or large numbers. 
    
    public int popInteger () throws VmException {
	DataStackElement elem = pop ();
	if (elem.getType () != ByteCode.Type.INTEGER) {
	    VmException.raiseUnexpectedValueOnStack ();
	}
	return elem.getElement ();
    }    

    // Pops an element from the data stack.  Throws an exception if
    // the element do not point to a valid index in the string table. 
    // Returns the string value that is pointed to. 

    public String popString () throws VmException {
	DataStackElement elem = pop ();
	if (elem.getType () != ByteCode.Type.STRING) {
	    VmException.raiseUnexpectedValueOnStack ();
	}
	return getString (elem.getElement ());
    }    

    // Pushes a raw element to the data stack. 

    public void push (DataStackElement i) {
        if (syncedPush) {
            synchronized (this) {
                dataStack.push (i);
            }
        } else {
            dataStack.push (i);
        }
    }

    // Pops a raw element from the data stack. 

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

    // Executes a word within the context of virtual machine. 
    // First the user-defined word list is checked.  This could be a
    // variable or a system word (a IVmOperation implementation).  
    // A variable can represent simple values like integers, booleans
    // and strings.  Simple values are just pushed to the data stack. 
    // If the variable represents a child virtual machine (or a code 
    // block), it is executed.  This child virtual machine will share 
    // the data stack and the variables table with the parent.  

    public void executeWord (int wordId) throws VmException {
	if (!executeVar (wordId)) {
	    IVmOperation opr = getOperation (wordId);
	    if (opr == null) {
		throw new VmException ("Invalid VM operation.");
	    }
	    opr.execute (this);
	}
    }

    // Writes the string to the virtual machine's output stream.

    public void write (String s) {
	if (out != null) {
	    out.print (s);
	}
    }

    // Writes the string and a newline to the virtual machine's 
    // output stream.

    public void writeLine (String s) {
	if (out != null) {
	    out.println (s);
	}
    }

    // Writes a newline to the virtual machine's output stream. 

    public void writeLine () {
	if (out != null) {
	    out.println ();
	}
    }

    // Stops the virtual machine after cleaning up its internal
    // data structures. 

    public void stop () {        
	cleanup ();
	state.stopped = true;
    }

    // Returns the string representation of the data stack element. 
    // Useful printing, doing the loose equality operation etc.

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

    // Pushes a boolean to the data stack.  
    // true is represented as 1 and fase as 0. 

    public void pushBoolean (boolean b) {
	if (b) 
	    pushBoolean (1);
	else 
	    pushBoolean (0);
    }

    // Pushes a boolean to the data stack. 

    public void pushBoolean (int i) {
	push (i, ByteCode.Type.BOOLEAN);
    }

    // Pushes an integer to the data stack. 
	
    public void pushInteger (int i) {
	push (i, ByteCode.Type.INTEGER);
    }

    // Pushes a big integer or a double to the data stack. 
    // Throws an exception if the number was not previously 
    // interned. 

    public void pushNumber (int hc, ByteCode.Type type) 
	throws VmException {
	getNumber (hc); // Just to make sure that Number was interned.
	push (hc, type);
    }

    // Pushes a big integer or a double to the data stack. 
    // There is an option to intern new numbers. 

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

    // Pushes a big integer to the data stack. 

    public void pushBigInteger (BigInteger bi) {
	pushNumber (bi, ByteCode.Type.BIGINTEGER, true);
    }

    // Pushes a double to the data stack. 

    public void pushDouble (Double d) {
	pushNumber (d, ByteCode.Type.DOUBLE, true);
    }

    // Pushes a string to the data stack. 
    // Throws and exception if the string was not previously
    // interned. 

    public void pushString (int hc) throws VmException {
	getString (hc); // Just to make sure that string was interned.
	push (hc, ByteCode.Type.STRING);
    }

    // Interns and pushes a string. 

    public void pushString (String str) {
	pushString (str, true);
    }

    // Pushes a string to the data stack.  There is an option
    // to intern it. 

    public void pushString (String str, boolean intern) {
	int hc = 0;
	if (intern)
	    hc = internString (str);
	else
	    hc = str.hashCode ();
	push (hc, ByteCode.Type.STRING);
    }

    // Executes a byte code.  Words are executed and values are
    // interened and pushed. 

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

    // Removes a variable mapping from the variables table. 

    public void forget (String varName) {
	int hc = varName.hashCode ();
	DataStackElement elem = vars.get (hc);
	if (elem != null) {
	    removeVar (hc, elem);
	}
    }

    // Sets the compilation mode.  If this is true, tokens are
    // only compiled and added to the byte codes list.  They are
    // not executed.  The compiled byte codes can be later executed
    // by calling run () on this virtual machine.  This is used mostly
    // for code blocks.  

    public void setCompilationMode (boolean b) {
	compilationMode = b;
	if (compilationMode) byteCodes = null;
    }

    // Retrieves the virtual machines identified by vmId from the
    // child vm table and executes it. 

    public void runChildVm (int vmId, boolean discard) 
	throws VmException {
	Vm vm = vmTable.get (vmId);
	vm.dataStack = this.dataStack;
	vm.run ();
	if (discard && parentVm == null) {
	    discardChildVm (vm, vmId);
	}
    }

    // Removes a child from the virtual machine's table.

    public void discardChildVm (int vmId) {
	Vm vm = vmTable.get (vmId);
	if (parentVm == null) {
	    discardChildVm (vm, vmId);
	}
    }

    // Adds a variable mapping.  A child, if not running as a different
    // process, can modify mappings in the parent's variables table.  This
    // rule is not applicable for mapped code blocks.  

    public void putVar (int hc, DataStackElement var) {
	boolean put = false;
	if (parentVm == null) {
	    put = true;
	} else {
	    if (!spawned) put = !parentVm.updateVar (hc, var);
	    else put = true;		
	}
	if (put) {
	    vars.put (hc, var);
	}    
    }

    // Returns a reference to the parent. 

    public Vm getParentVm () {
        return parentVm;
    }

    // Runs a child virtual machine in a new process.  It will
    // have a new data stack, with the values of the parent's data
    // stack copied.  A unique process ID is pushed to the parent's
    // data stack.  This ID can be used to push values to the child's
    // stack using the << and >> special operators.  Thus Niue supports
    // basic inter-process communication. 

    public void spawn (int vmId) {
	Vm vm = vmTable.get (vmId);
        vm.dataStack = createFrom (this.dataStack);
	vm.spawned = true;
        if (procController == null || !procController.isAlive ()) {
            procController = new ProcessController (this);
        }
        int procId = procController.add (vm);
        // The new process is mapped into a process table.  
        addProcess (procId, vm);
        pushInteger (procId);
    }

    // Returns the compiled byte codes. 

    public ByteCodes getByteCodes () {
        return byteCodes;
    }

    // Gets the virtal machine identified by the process ID. 

    public Vm getProcess (int procId) {
        if (procId == 0) {
            return getRootProcess ();
        } else {
	    Vm vm = processTable.get (procId);
            if (vm == null) {
                if (parentVm != null) {
                    return parentVm.getProcess (procId);
                }
            } else {
		return vm;
	    }
        }
	return null;
    }

    // Removes a process from the process table. 

    public void removeProcess (int procId) {
        if (processTable != null) {
            synchronized (this) {
                if (processTable.remove (procId) == null) {
                    if (parentVm != null) {
                        parentVm.removeProcess (procId);
                    }
                }
            }
        }
    }

    // Sets this virtual machine's process ID. 

    public void setProcId (int pid) {
        procId = pid;
    }

    // Returns this virtual machine's process ID.

    public int getProcId () {
        return procId;
    }

    // Re-maps the data stack.  Used for inter-process communication. 

    public void setTempDataStack (Stack<DataStackElement> stack) {
        if (stack == null) {
            dataStack = oldDataStack;
            oldDataStack = null;
        } else {
            oldDataStack = dataStack;
            dataStack = stack;
        }
    }

    // Sets the syncedPush mode.  If true, all pushes to the data stack 
    // will be synchronized. 

    public void setSyncedPush (boolean b) {
        syncedPush = b;
    }

    // The process ID into which data is being pushed as part of
    // inter-process communication. 

    public int getPushingInto () {
        return pushingInto;
    }

    // Sets the process ID to push data as part of inter-process communication. 

    public void setPushingInto (int i) {
        pushingInto = i;
    }

    // Returns the process ID. 

    public int getPid () {
	return procId;
    }

    // Gets the process ID of the root process. 

    private Vm getRootProcess () {
        if (parentVm == null) {
            return this;
        } else {
            return parentVm.getRootProcess ();
        }
    }

    // Maps a process to the process table. 

    private void addProcess (int procId, Vm vm) {
        if (processTable == null) {
            processTable = new Hashtable<Integer, Vm> ();
        }
        processTable.put (procId, vm);
    }

    // Updats the mapping of a variable.  If the variable is mapped in
    // the parent virtual machine, that is updated. 

    private boolean updateVar (int hc, DataStackElement var) {
	boolean check = false;
	if (parentVm == null) check = true;
	else {
	    if (!spawned) check = !parentVm.updateVar (hc, var);
	    else check = true;
	}
	if (check) {
	    if (vars.get (hc) != null) {
		vars.put (hc, var);
		return true;
	    }
	}
	return false;
    }

    // Removes a variable from the table, including its interned value. 

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

    // Removes a child virtual machine from the table. 

    private void discardChildVm (Vm vm, int vmId) {
	vm.cleanup ();
	vmTable.remove (vmId);
    }

    // Cleans up the virtual machine's data structures.

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

    // Stops the process controller. 

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
    private Stack<DataStackElement> oldDataStack = null;
    private boolean syncedPush = false;
    private int pushingInto = -1;
    private boolean spawned = false;
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
    private Hashtable<Integer, Vm> processTable = null;
    private int procId = 0;

    public static final String EMPTY_STACK_MSG = "<empty-stack>";
    static final int COLON_DEF = ":".hashCode ();
    static final int COLON_DEF_END = ";".hashCode ();
    static final int BLOCK_START = "[".hashCode ();
    static final int BLOCK_END = "]".hashCode ();
}
