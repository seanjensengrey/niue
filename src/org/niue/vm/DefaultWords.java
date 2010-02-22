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

import java.util.Hashtable;
import org.niue.vm.operation.*;

final class DefaultWords {
    
    @SuppressWarnings("unchecked") 
	static Hashtable <Integer, IVmOperation> getDefaultOperation () {
        if (vmOperations == null) {
            vmOperations = new Hashtable<Integer, IVmOperation> ();
            // Contract utilities, VM control.
            vmOperations.put (DOT_S, new PrintStack ());
            vmOperations.put (EMIT, new Emit ());
            vmOperations.put (NEWLINE, new Cr ());
            vmOperations.put (DOT_QUIT, new Quit ());
            vmOperations.put (DOT, new Pop (true));
            vmOperations.put (DISCARD, new Pop (false));
            vmOperations.put (FORGET, new Forget ());
            vmOperations.put (RUN, new Run ());
            vmOperations.put (DEF_VAR, new DefVar ());
            vmOperations.put (SPAWN, new Spawn ());
            vmOperations.put (SLEEP, new Sleep ());
            vmOperations.put (RECEIVE, new Receive ());
            vmOperations.put (SELF, new Pid (Pid.Type.SELF));
            vmOperations.put (SUPER, new Pid (Pid.Type.SUPER));
            vmOperations.put (LOAD, new Load ());

            // Arithmetic
            vmOperations.put (ADD, new Arith (Arith.Operator.ADD));
            vmOperations.put (SUB, new Arith (Arith.Operator.SUB));
            vmOperations.put (MULT, new Arith (Arith.Operator.MULT));
            vmOperations.put (DIV, new Arith (Arith.Operator.DIV));
            vmOperations.put (MOD, new Arith (Arith.Operator.MOD));
            vmOperations.put (DIV_MOD, new Arith (Arith.Operator.DIV_MOD));

            // Comparison
            vmOperations.put (EQUALS, new Cmpr (Cmpr.Operator.EQUALS));
            vmOperations.put (EQ, new Cmpr (Cmpr.Operator.EQ));
            vmOperations.put (LT, new Cmpr (Cmpr.Operator.LT));
            vmOperations.put (GT, new Cmpr (Cmpr.Operator.GT));
            vmOperations.put (LT_EQ, new Cmpr (Cmpr.Operator.LT_EQ));
            vmOperations.put (GT_EQ, new Cmpr (Cmpr.Operator.GT_EQ));

	    // Logical
            vmOperations.put (AND, new Logical (Logical.Operator.AND));
            vmOperations.put (OR, new Logical (Logical.Operator.OR));
            vmOperations.put (NEGATE, new Logical (Logical.Operator.NEGATE));

            // Stack manipulation
            vmOperations.put (LEN, new StackManip (StackManip.Operator.LEN));
            vmOperations.put (SWAP, new StackManip (StackManip.Operator.SWAP));
            vmOperations.put (DUP, new StackManip (StackManip.Operator.DUP));
            vmOperations.put (OVER, new StackManip (StackManip.Operator.OVER));
            vmOperations.put (ROT, new StackManip (StackManip.Operator.ROT));
            vmOperations.put (DROP, new StackManip (StackManip.Operator.DROP));
            vmOperations.put (TWO_SWAP, new StackManip 
			      (StackManip.Operator.TWO_SWAP));
            vmOperations.put (TWO_DUP, new StackManip 
			      (StackManip.Operator.TWO_DUP));
            vmOperations.put (TWO_OVER, new StackManip 
			      (StackManip.Operator.TWO_OVER));
            vmOperations.put (TWO_DROP, new StackManip 
			      (StackManip.Operator.TWO_DROP));
            vmOperations.put (PUSH_TO_PARENT, new StackManip 
			      (StackManip.Operator.PUSH_SYNC));
            vmOperations.put (DONE_PUSH_TO_PARENT, new StackManip 
			      (StackManip.Operator.DONE_PUSH_SYNC));

	    // Control flow
            vmOperations.put (IF_TRUE, new If (If.Cond.IF_TRUE));
            vmOperations.put (ELSE, new If (If.Cond.ELSE));
            vmOperations.put (WHEN_FALSE, new If (If.Cond.WHEN_FALSE));
            vmOperations.put (WHEN_TRUE, new If (If.Cond.WHEN_TRUE));
            vmOperations.put (WHILE, new Loop (Loop.Type.WHILE));
            vmOperations.put (TIMES, new Loop (Loop.Type.TIMES));
            vmOperations.put (TIMES_BY, new Loop (Loop.Type.TIMES_BY));

	    // String operations
            vmOperations.put (STR_LEN, new StringOprs 
			      (StringOprs.Operator.STR_LEN));
            vmOperations.put (STR_AT, new StringOprs 
			      (StringOprs.Operator.STR_AT));
            vmOperations.put (STR_EQ, new StringOprs 
			      (StringOprs.Operator.STR_EQ));
            vmOperations.put (STR_EQI, new StringOprs 
			      (StringOprs.Operator.STR_EQI));
            vmOperations.put (STR_TOLOWER, new StringOprs 
			      (StringOprs.Operator.STR_TOLOWER));
            vmOperations.put (STR_TOUPPER, new StringOprs
			      (StringOprs.Operator.STR_TOUPPER));
            vmOperations.put (STR_TRIM, new StringOprs 
			      (StringOprs.Operator.STR_TRIM));
            vmOperations.put (SUBSTR, new StringOprs 
			      (StringOprs.Operator.SUBSTR));
	    
	    // List operations on the stack
	    vmOperations.put (AT, new ListOprs (ListOprs.Operator.AT));
	    vmOperations.put (REMOVE, new ListOprs 
			      (ListOprs.Operator.REMOVE));
	    vmOperations.put (REMOVE_ALL, new ListOprs 
			      (ListOprs.Operator.REMOVE_ALL));
	    vmOperations.put (GET, new ListOprs (ListOprs.Operator.GET));
        }
        return (Hashtable<Integer, IVmOperation>) vmOperations.clone ();
    }

    // Console utilities, VM control.
    static final int NEWLINE = "newline".hashCode ();
    static final int DOT_QUIT = ".q".hashCode ();
    static final int DOT_S = ".s".hashCode ();
    static final int EMIT = "emit".hashCode ();
    static final int DOT = ".".hashCode ();
    static final int DISCARD = ",".hashCode ();
    static final int FORGET = "forget".hashCode ();
    static final int RUN = "!".hashCode ();
    static final int DEF_VAR = ";".hashCode ();
    static final int SPAWN = "!!".hashCode ();
    static final int SLEEP = "sleep".hashCode ();
    static final int RECEIVE = "receive".hashCode ();
    static final int SELF = "self".hashCode ();
    static final int SUPER = "super".hashCode ();
    static final int LOAD = "load".hashCode ();

    // Arithmetic
    static final int ADD = "+".hashCode ();
    static final int SUB = "-".hashCode ();
    static final int MULT = "*".hashCode ();
    static final int DIV = "/".hashCode ();
    static final int MOD = "mod".hashCode ();
    static final int DIV_MOD = "/mod".hashCode ();

    // Comparison
    static final int EQUALS = "equals".hashCode ();
    static final int EQ = "=".hashCode ();
    static final int LT = "<".hashCode ();
    static final int GT = ">".hashCode ();
    static final int LT_EQ = "<=".hashCode ();
    static final int GT_EQ = ">=".hashCode ();

    // Logical
    static final int AND = "and".hashCode ();
    static final int OR = "or".hashCode ();
    static final int NEGATE = "not".hashCode ();

    // Stack manipulation
    static final int LEN = "len".hashCode ();
    static final int SWAP = "swap".hashCode ();
    static final int DUP = "dup".hashCode ();
    static final int OVER = "over".hashCode ();
    static final int ROT = "rot".hashCode ();
    static final int DROP = "drop".hashCode ();
    static final int TWO_SWAP = "2swap".hashCode ();
    static final int TWO_DUP = "2dup".hashCode ();
    static final int TWO_OVER = "2over".hashCode ();
    static final int TWO_DROP = "2drop".hashCode ();
    static final int PUSH_TO_PARENT = "<<".hashCode ();
    static final int DONE_PUSH_TO_PARENT = ">>".hashCode ();

    // Control flow
    static final int IF_TRUE = "if".hashCode ();
    static final int ELSE = "else".hashCode ();
    static final int WHEN_FALSE = "unless".hashCode ();
    static final int WHEN_TRUE = "when".hashCode ();
    static final int WHILE = "while".hashCode ();
    static final int TIMES = "times".hashCode ();
    static final int TIMES_BY = "times-by".hashCode ();

    // String operations 
    static final int STR_LEN = "str-len".hashCode ();
    static final int STR_AT = "str-at".hashCode ();
    static final int STR_EQ = "str-eq".hashCode ();
    static final int STR_EQI = "str-eqi".hashCode ();
    static final int STR_TOLOWER = "str-tolower".hashCode ();
    static final int STR_TOUPPER = "str-toupper".hashCode ();
    static final int STR_TRIM = "str-trim".hashCode ();
    static final int SUBSTR = "substring".hashCode ();

    // List operations on the stack
    static final int AT = "at".hashCode ();
    static final int REMOVE = "remove".hashCode ();
    static final int REMOVE_ALL = "remove-all".hashCode ();
    static final int GET = "get".hashCode (); // Treats the stack as a plist.

    private static Hashtable<Integer, IVmOperation> vmOperations = null;
}
