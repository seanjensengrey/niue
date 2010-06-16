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

import java.util.Stack;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import org.niue.vm.IVmOperation;
import org.niue.vm.Vm;
import org.niue.vm.VmException;
import org.niue.vm.DataStackElement;
import org.niue.vm.ByteCode;

// Pops a string identifier which should be a method name. 
// Pops an Object from the stack and invokes the method on
// the Object with the rest of the elements on the stack being
// arguments.  Any return value, is pushed to the stack. 

// Sample usage from a Niue REPL:
// > 'java.lang.StringBuffer new 'sb ;
// > 'hello sb 'append @
// > sb .
//   hello
// > 'world sb 'append @
// > sb .
//   helloworld

public final class InvokeMethod implements IVmOperation {
    
    public void execute (Vm vm) throws VmException {
	String methodName = vm.popString ();
        Object obj = vm.popObject ();
        Class cls = obj.getClass ();
        ArgInfo argInfo = constructArgumetTypes (vm);
        Method method = findMethod (cls, methodName, argInfo.types);
        try {
            Object ret = method.invoke (obj, argInfo.args);
            if (ret != null) {
                vm.pushObject (ret);
            }
        } catch (IllegalAccessException ex) {
            throw new VmException ("IllegalAccessException. " + ex.getMessage ());
        } catch (IllegalArgumentException ex) {
            throw new VmException ("IllegalArgumentException. " + ex.getMessage ());
        } catch (InvocationTargetException ex) {
            throw new VmException ("InvocationTargetException. " + ex.getMessage ());
        }
    }

    private static class ArgInfo {
        private Class[] types;
        private Object[] args;
        private ArgInfo (Class[] types, Object[] args) {
            this.types = types;
            this.args = args;
        }
    }

    private static ArgInfo constructArgumetTypes (Vm vm) throws VmException {
        int argc = vm.getDataStack ().size ();
        if (argc == 0)
            return null;
        Class[] types = new Class[argc];
        Object[] args = new Object[argc];
        for (int i = 0; i < argc; ++i) {
            Object obj = convertToObject (vm);
            types[i] = obj.getClass ();
            args[i] = obj;
        }
        return new ArgInfo (types, args);
    }

    private static Object convertToObject (Vm vm) throws VmException {
        DataStackElement elem = vm.top ();
        switch (elem.getType ()) {
        case BOOLEAN:
            return new Boolean (vm.popBoolean ());
        case INTEGER:
            return new Integer (elem.getElement ());
        case BIGINTEGER:
            return vm.popBigInteger ();
        case DOUBLE:
            return vm.popDouble ();
        case STRING:
            return vm.popString ();
        case OBJECT:
            return vm.popObject ();
        case WORD:
        case VM: 
        case IF:
            throw new VmException ("The value on stack cannot be passed to " +
                                   "a generic Java method.");
        }
        return null;
    }

    @SuppressWarnings("unchecked") private static Method findMethod (Class cls, 
                                                                     String methodName, 
                                                                     Class[] types) 
        throws VmException {
        try {
            return cls.getMethod (methodName, types);
        } catch (NoSuchMethodException ex) {
            throw new VmException ("NoSuchMethodException. " + ex.getMessage ());
        } catch (SecurityException ex) {
            throw new VmException ("SecurityException. " + ex.getMessage ());
        }
    }
}
