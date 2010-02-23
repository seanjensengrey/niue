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

// Represents a compiled byte code. 

public final class ByteCode {
    
    public enum Type { BOOLEAN, INTEGER, BIGINTEGER, DOUBLE, STRING, 
	    WORD, VM, IF };
    
    public Type type;
    public int code;

    public ByteCode () {
	type = Type.BOOLEAN;
	code = 0;
    }

    public ByteCode (Type type, int code) {
	this.type = type;
	this.code = code;
    }

    public String toString () {
	StringBuilder sb = new StringBuilder ();
	sb.append ("type=").append (type).append (", code").append (code);
	return sb.toString ();
    }
}
