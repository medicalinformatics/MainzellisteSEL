package de.unimainz.imbei.mzid;

/**
 * PIDBackend provides backend functions used for
 * PID generation, check and correction.
 * @author borg
 *
 */
final class PIDBackend {

	/** Transform a string s into a codeword c.  
	 *        
	 *   If s has length != 8 or contains a character not in the   
	 *   alphabet sigma, the function returns 0 and the output     
	 *   variable c is meaningless.                                
	 *     Note: Lowercase letters are converted to uppercase.     
	 *   Otherwise c is the codeword corresponding to s and the    
	 *   function returns 1.                                       
	 * ---> used in PIDcheck                                       
	 */
	static int[] PID2c(String s)
	{
		if (s.length() != 8) return null;
		int i;
		char p[] = s.toCharArray();
		int c[] = new int[8]; 
	
		for (i=0; i<8; i++) c[i] = 0;
		for (i=0; i<8; i++) {     /* convert chars to 5-bit-integers */
			switch (p[i]) {
		      case '0': case '1': case '2': case '3': case '4':
		      case '5': case '6': case '7': case '8': case '9':
		        c[i] = p[i] - '0';
				break;
		      case 'A': case 'a': c[i] = 10;
		      	break;
		      case 'C': case 'D': case 'E': case 'F': case 'G': case 'H':
		        c[i] = p[i] - 'C' + 11;
		        break;
		      case 'c': case 'd': case 'e': case 'f': case 'g': case 'h':
		        c[i] = p[i] - 'c' + 11;
		        break;
		      case 'J': case 'K': case 'L': case 'M': case 'N':
		        c[i] = p[i] - 'J' + 17;
		        break;
		      case 'j': case 'k': case 'l': case 'm': case 'n':
		        c[i] = p[i] - 'j' + 17;
		        break;
		      case 'P': case 'Q': case 'R':
		        c[i] = p[i] - 'P' + 22;
		        break;
		      case 'p': case 'q': case 'r':
		        c[i] = p[i] - 'p' + 22;
		        break;
		      case 'T': case 'U': case 'V': case 'W': case 'X':
		      case 'Y': case 'Z':
		        c[i] = p[i] - 'T' + 25;
		        break;  /*** missing break inserted 17. April 2005 KP ***/
		      case 't': case 'u': case 'v': case 'w': case 'x':
		      case 'y': case 'z':
		        c[i] = p[i] - 't' + 25;
		        break;
		      default:                       /* invalid character found */
		        return null;
	      }
	    }
	  return c;
	}

	/** Output weighted sum.
	 * 
	 * Output the weighted sum                                     
	 *  t p[0] + t^2 p[1] + t^3 p[2] + t^4 p[3] + t^5 p[4]         
	 *  + t^6 p[5] 
	 *   in F_32.                                                  
	 * Used in encode and PIDcheck.                           
	 */
	static int wsum1(int p[])
	{
	  int s;
	  int i;
	  s = 0;
	  for (i=0; i<=5; i++) s = s ^ PIDBackend.multf32(p[i],i+1);
	  return s;
	}

	/** Output weighted sum.
	 * 
	 * Output the weighted sum                                     
	 *  t^2 p[0] + t^4 p[1] + t^6 p[2] + t^8 p[3] + t^10 p[4]      
	 *                                                 + t^12 p[5] 
	 *   in F_32.                                                  
	 * Used in encode and PIDcheck.                           
	 */
	static int wsum2(int p[])
	{
	  int s;
	  int i;
	  s = 0;
	  for (i=0; i<=5; i++) s = s ^ PIDBackend.multf32(p[i],2*i+2);
	  return s;
	}

	/** alphabet for PIDs
	 *  
	 *  A codeword is transformed into a PID by replacing each of
	 *  the 5-bit-integers by the corresponding character of
	 *  the alphabet.                                           
	*/
	static char sigma[] = "0123456789ACDEFGHJKLMNPQRTUVWXYZ".toCharArray();

	/** Arithmetic in the Galois field F_32.
	 * 
	 *   t is a primitive element with t^5 = t^2 + 1.              
	 * Multiply the 5 bit input x = (x4, x3, x2, x1, x0) with t^e  
	 *   where e is an unsigned integer.                           
	 * Error handling: If x has more then 5 bits, overflowing bits 
	 *   are dropped.                                              
	 * 
	 * Used in wsum1, wsum2, and PIDcheck.                    
	 */
	static int multf32(int x, int e)
	{
	  x = x & 31;                  /* drop overflowing bits        */
	  while (e >= 4) {
	    x = PIDGenerator.mult0f32(PIDGenerator.mult0f32(x,2),2); /* multiply by t^4          */
	    e = e-4;
	    }
	  x = PIDGenerator.mult0f32(x,e);
	  return x;
	}

}
