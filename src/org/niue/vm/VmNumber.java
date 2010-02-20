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

import java.text.NumberFormat;
import java.text.ParseException;
import java.math.BigInteger;

public class VmNumber {
    public VmNumber (Number n, ByteCode.Type t) {
	number = n;
	type = t;
    }
    
    public Number getNumber () { return number; }
    public ByteCode.Type getType () { return type; }

    public static VmNumber parse (String token) {
	if (hasLargeNumberPrefix (token)) 
	    token = token.substring (1);
	NumberFormat nf = NumberFormat.getInstance ();
	try {
	    Number n = null;
	    ByteCode.Type type = ByteCode.Type.BIGINTEGER;
	    if (token.indexOf ('.') >= 0) {
		n = nf.parse (token);
		type = ByteCode.Type.DOUBLE;
	    } else {
		n = new BigInteger (token);
	    }
	    return new VmNumber (n, type);
	} catch (ParseException ex) {
	    return null;
	} catch (NumberFormatException ex) {
	    return null;
	}
    }

    public static boolean hasLargeNumberPrefix (String token) {
	return (token.charAt (0) == 'L');
    }
    
    private Number number;
    private ByteCode.Type type;
}
