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

import java.util.ArrayList;

// A list of ByteCodes. 

public final class ByteCodes {
    public void add (ByteCode bc) {
	byteCodes.add (bc);
    }

    // Returns the next ByteCode, based on an internally
    // maintained index.  Returns null if the list is done. 

    public ByteCode next () {
	if (index >= byteCodes.size ()) {
	    return null;
	} else {
	    return byteCodes.get (index++);
	}
    }

    // Resets the internal index to 0. 

    public void resetIndex () {
	index = 0;
    }

    // Retruns the number of ByteCodes in the list. 

    public int size () {
	return byteCodes.size ();
    }
    
    // Returns the ByteCode at `i'.  Returns null if the
    // index is invalid. 

    public ByteCode at (int i) {
	try {
	    return byteCodes.get (i);
	} catch (IndexOutOfBoundsException ex) {
	    return null;
	}
    }

    // Makes a copy of this object. 

    public @SuppressWarnings("unchecked") ByteCodes copy () {
	ByteCodes bc = new ByteCodes ();
	bc.wordId = wordId;
	bc.index = 0;
	bc.byteCodes = (ArrayList<ByteCode>) byteCodes.clone ();
	return bc;
    }

    private int wordId = -1;
    private ArrayList<ByteCode> byteCodes = new ArrayList<ByteCode> ();
    private int index = 0;
}


