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

package org.niue;

import java.io.PrintStream;
import java.io.InputStream;
import java.io.IOException;
import org.niue.vm.Vm;
import org.niue.vm.VmException;

public final class Niue {
    public static void main(String[] args) throws Exception {
	run (new Vm ());
    }

    public static void run (Vm vm) throws VmException {
	run (vm, System.in, System.out);
    }

    public static void run (Vm vm, InputStream in) throws VmException {
	run (vm, in, System.out);
    }

    public static void run (Vm vm, InputStream in, PrintStream out) 
	throws VmException {
	Reader parser = new Reader (in);
	vm.setOutput (out);
	String token = null;
	try {
	    while ((token = parser.getToken ()) != null) {
		try {
		    token = token.trim ();
		    if (token.length() > 0) {
			vm.execute (token.trim());		
		    }
		} catch (VmException ex) {
		    // ex.printStackTrace ();
		    out.println (ex.getMessage ());
		}
		if (vm.isStopped ()) {
		    break;
		}
	    }
	} catch (ReaderException ex) {
	    throw new VmException (ex);
	} catch (IOException ex) {
	    throw new VmException (ex);
	}
    }
}
