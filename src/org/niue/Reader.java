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

import java.io.InputStream;
import java.io.IOException;

// Parses an input stream into tokens of strings.  Space is the
// default delimitter.  Knows to parse quoted strings and comments
// enclosed in ( and ).

public final class Reader {
    public Reader (InputStream in) {
        this.in = in;
    } 

    public String getToken () throws IOException, ReaderException {
        StringBuffer ret = new StringBuffer ();
        int i = 0;
	boolean inComment = false;
        while ((i = in.read()) != -1) {
            char c = (char)i;
	    if (inComment) {
		if (c == ')')
		    inComment = false;
		continue;
	    }	
            if (Character.isWhitespace (c)) {
                return ret.toString ();
	    } else if (c == '\"') {
		return readString (true);
	    } else if (c == '\'') {
		return readString (false); // quoted string
	    } else if (c == '(') {
		inComment = true;
            } else {
                ret.append (c);
            }
        } 
	if (ret.length () > 0) {
	    return ret.toString ();
	} else {
	    return null;
	}
    }

    private String readString (boolean isNormal) 
	throws IOException, ReaderException {
	int i = 0;
	char prev = 0;
	StringBuffer ret = new StringBuffer ("\"");
	boolean done = false;
        while ((i = in.read()) != -1) {
            char c = (char)i;
	    if (isNormal) {
		if (c == '\"') {
		    if (prev != '\\') {
			done = true;
		    }
		}
	    } else {
		if (Character.isWhitespace (c)) {
		    done = true;
		}
	    }
	    if (done) break;
	    ret.append (c);
	    if (!isNormal) prev = c;
	}
	if (done) {
	    ret.append ("\"");
	    return ret.toString ();
	} else {	    
	    throw new ReaderException ("String not properly terminated.");
	}
    }

    private InputStream in;
}
