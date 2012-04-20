package de.unimainz.imbei.mzid;

import java.util.Random;

/**
 * Here go all the mathematics involved in generating, checking and correcting PIDs.
 * Methods here are private to the package. A user should call the static functions of class PID.
 * 
 * @author Martin Lablans
 */
public class PIDGenerator {
	private int key1, key2, key3;
	private int counter = 1;
	private Random rand;
	private static int NN   = 1073741824; /** 2^30 = module for calc  */	
	private static int NN_1 = 1073741823; /** 2^30 - 1 */ 
	
	/** Least bit for randomization  
	rndfact = 2^(30-rndwidth)         */
	private int rndfact; 
	
	/** Upper limit for random numbers   
    rndlim = 2^(rndwidth)             */
	private int rndlim;   
	
	/** Set to 1 after first call to rndsetup */
	private boolean RSET = false;
	/** alphabet for PIDs
	 *  
	 *  A codeword is transformed into a PID by replacing each of
	 *  the 5-bit-integers by the corresponding character of
	 *  the alphabet.                                           
	*/
	static char sigma[] = "0123456789ACDEFGHJKLMNPQRTUVWXYZ".toCharArray();
	
	private PIDGenerator(int key1, int key2, int key3, int rndwidth){
		this.key1 = key1;
		this.key2 = key2;
		this.key3 = key3;
		this.rndsetup(rndwidth);
	}
	
	private String createPIDString(int counter){
		//TODO: PID unter Verwendung des Counters erzeugen...
		return PIDgen(counter);
	}
	
	/**
	 * Returns an instance of a PIDGenerator used to create PIDs.
	 * 
	 * @param key1 First encryption key.
	 * @param key2 Second encryption key.
	 * @param key3 Third encryption key.
	 */
	public static PIDGenerator init(int key1, int key2, int key3, int rndwidth){
		return new PIDGenerator(key1, key2, key3, rndwidth);
	}
	
	/**
	 * Returns the next PID according to this factory's internal counter.
	 */
	public String getNextPIDString(){
		return createPIDString(counter++);
	}

	/** Check PID.
	 * Description in psx-pgi.h
	 */
	static int PIDcheck(String s, String out)
	{
		//TODO: portieren
		return 0;
	}

	/** Initialize random generator.                                
	 * Set global variables rndfact, rndlim.                     
	 * Must be called before first use of rndext.           
	 *                  	 
	 * Error handling: Values > 12 of rndwith are treated as = 12  
	 * 
	 * @param rndwidth The desired random width. 
	 */	
	private void rndsetup(int rndwidth)
	{
		if (RSET) return;
		
		RSET = true;
		rand = new Random();
		rand.setSeed(System.currentTimeMillis()); /* set seed */

		if (rndwidth > 12) rndwidth = 12;
		rndfact = 1 << (30 - rndwidth);
		rndlim  = 1 << rndwidth;
	}	
	
	/** Randomize a number                                          
	 *                                                             
	 * Replace first rndwidth bits of x with random bits  
	 * 
	 * Error handling: If x >= rndfact, overflowing bits are dropped.                                                  
	 * 
	 * Used in encr                                          
	 * 
	 * @param x Integer, 0 <= x < rndfact.
	 * @return x with first rndwidth bits of x replaced by random bits
	 */
	private int rndext(int x)
	{
	  int r;
	  int rr;
	  double r1;

	  x = x & (rndfact - 1); 
	  r1 = rand.nextDouble(); /* 0 <= r1 < 1 */
	  rr = (int) (rndlim * r1);
	  r = rr*rndfact + x;
	  return r;
	}
	
	/** Multiply x and y mod 2^30                                  
	 * used in encr                                           
	 */
	private static int mult30(int x, int y)
	{
	  int z;
	  z = x*y;             /* multiply, dropping long int overflow */
	  z = z & NN_1;                   /* reduce mod 2^30           */
	  return z;
	}
	
	/** Rotate x cyclically by 6 bits to the right.                
	 *   Bit 29 becomes bit 23, ..., bit 6 becomes bit 0,          
	 *   bit 5 becomes bit 29, ..., bit 0 becomes bit 24.          
	 * Error handling: If x is not in the required range,          
	 *   overflowing bits are dropped.                             
	 * Used in encr                                           
	*/
	static int rot30_6(int x)
	{
	  int y, z;
	  y = x & 63;                   /* preserve last 6 bits        */
	  y = y << 24;                  /* shift them to the left      */
	  z = x >> 6;                   /* remaining bits to the right */
	  z = z & 16777215;             /* clear overflowing bits      */
	                                /* 16777215 = 2^24 - 1         */
	  z = z | y;                    /* prefix preserved 6 bits     */
	  return z;
	}
	
	
	/** Nonlinear transform of x.                                  
	 *   Split the input x into five 6-bit-chunks [e|d|c|b|a],     
	 *   replace a with the quadratic expression                   
	 *   a + b*e + c*d mod 2^6.                                    
	 *   Because of an error the transformation is                 
	 *   a + b*d + c*d mod 2^6 instead.                            
	 * This transformation is bijective on 30-bit-integers.        
	 * Error handlicng: If x is not in the required range,         
	 *   overflowing bits are dropped.                             
	 * Used in encr
	 */
	private static int NLmix(int x)
	{
	  int y;
	  int  a, b, c, d, e;
	  a = x & 63;             /* extract last 6 bits               */
	  y = x >> 6;             /* shift remaining bits to the right */
	  b = y & 63;             /* extract last 6 bits               */
	  y = y >> 6;             /* shift remaining bits to the right */
	  c = y & 63;             /* extract last 6 bits               */
	  y = y >> 6;             /* shift remaining bits to the right */
	  d = y & 63;             /* extract last 6 bits               */
	  e = y >> 6;             /* get remaining 6 bits              */
	      /*** This should have been y = y >> 6. This error      ***/
	      /*** must NEVER be corrected. It changes the intended  ***/
	      /*** cryptographic transformation into another one     ***/
	      /*** that is perfectly valid for its own, but probably ***/
	      /*** somewhat weaker.                                  ***/
	  e = y & 63;             /* clear overflowing bits            */
	  
	  a = (a + b*e + c*d) & 63;    /* quadratic expression mod 2^6 */
	  y = x & 1073741760;          /* AND 2^30 - 2^6 =             */
	                               /* preserve bits 6 to 29        */
	  y = y | a;                   /* and append new 6 bits        */
	  return y;
	}

	
	/**
	 * Permutation of the bits of x. 
	 * 
	 * Split the input x into six 5-bit-chunks, permute each 
	 * chunk with the same fixed permutation This transformation 
	 * is bijective on 30-bit-integers. Error handling: If x is 
	 * not in the required range, overflowing bits are dropped. 
	 * Used in encr. 
	 * @param x The value which to permute, assumed to be an unsigned 
	 * 30-bit Integer.
	 * @return The permuted value.
	 */
	private static int bitmix(int x)
	{
		int p[] = new int[5];
		int xx[] = new int[5];
		int yy[] = new int[5];
		int y;
		int i;

		p[0] = 34636833;    /* 2^25 + 2^20 + 2^15 + 2^10 + 2^5 + 2^0 */
		for (i=1; i <= 4; i++) 
			p[i] = p[i-1] << 1;
		for (i=0; i <= 4; i++) 
			xx[i] = x & p[i];    /* every 5th bit */
		yy[0] = xx[3] >> 3;		/* permute       */
		yy[1] = xx[0] << 1;
		yy[2] = xx[4] >> 2;
		yy[3] = xx[2] << 1;
		yy[4] = xx[1] << 3;
		y = yy[0] | yy[1] | yy[2] | yy[3] | yy[4];  /* and glue      */
	
		return y;
	}

	/**
	 * Encrypt x with keys k1, k2, k3, and k4 = k1+k2+k3 mod 2^30.
	 * 
	 * The encryption consists of 4 rounds; each round consists of:
	 * <ul>
	 * 	<li> first rot30_6,
	 * 	<li> then NLmix,
	 * 	<li> finally multiply with ki mod 2^30. 
	 * </ul>
	 * Between rounds 2 and 3 apply bitmix. This transformation is 
	 * bijective on 30-bit-integers, if all ki are odd. 
	 * 
	 * Error handling: If ki is even, it's replaced with ki + 1. 
	 * If x is not in the required range, overflowing bits are dropped.
	 * 
	 * Used in PIDgen 
	 * @param x The value to encrypt. Assumed to be an unsigned 30-bit-Integer.
	 * @return The encrypted value, in the range of an unsigned 30-bit-Integer.
	 */
	private int encr(int x)
	{
		  int w, y, z, k1, k2, k3, k4;
		  k1 = this.key1 | 1;                 /* k1 may be even - make it odd */
		  k2 = this.key2 | 1;                 /* k2 may be even - make it odd */
		  k3 = this.key3 | 1;                 /* k3 may be even - make it odd */
		  k4 = (k1 + k2 + k3) & NN_1;  /* Key for round 4              */

		  y = rot30_6(x);               /* round 1                     */
		  w = NLmix(y);
		  z = mult30(k1, w);
		  y = rot30_6(z);               /* round 2                     */
		  w = NLmix(y);
		  z = mult30(k2, w);
		  w = bitmix(z);                /* permutation                 */
		  y = rot30_6(z);               /* round 3                     */
		  w = NLmix(y);
		  z = mult30(k3, w);
		  y = rot30_6(z);               /* round 4                     */
		  w = NLmix(y);
		  z = mult30(k4, w);
		  return z;
		
	}
	
	/** Split a 30-bit integer x into an array p of six 5-bit-integers.
	 * 
	 * Error handling: If x is not in the required range,
	 * overflowing bits are dropped.                   
	 * Used in PIDgen                               
	 */
	private static int[] u2pcw(int x)
	{
	  int y;
	  int p[] = new int[6];
	  
	  p[5] = x & 31;           /* extract last 5 bits              */
	  y = x >> 5;              /* shift remaining bit to the right */
	  p[4] = y & 31;           /* extraxt last 5 bits              */
	  y = y >> 5;              /* shift remaining bit to the right */
	  p[3] = y & 31;           /* extraxt last 5 bits              */
	  y = y >> 5;              /* shift remaining bit to the right */
	  p[2] = y & 31;           /* extraxt last 5 bits              */
	  y = y >> 5;              /* shift remaining bit to the right */
	  p[1] = y & 31;           /* extraxt last 5 bits              */
	  y = y >> 5;              /* shift remaining bit to the right */
	  p[0] = y & 31;           /* extraxt last 5 bits              */
	  
	  return p;
	}
	
	/** Arithmetic in the Galois field F_32.
	 * 
	 *   t is a primitive element with t^5 = t^2 + 1.              
	 * Multiply the 5 bit input x = (x4, x3, x2, x1, x0) with t^e 
	 *   where 0 <= e <= 3;                                        
	 *   the algorithm is described in the documentation.          
	 * 
	 * Error handling: If x has more then 5 bits, overflowing bits 
	 *   are dropped.                                              
	 *   If e is < 0 or > 3, it's treated as 0, i. e. the function 
	 *   returns x.                                                
	 * 
	 * Used in multf32          
	 */
	static int mult0f32(int x, int e)
	{
	  int u, v, w, s;
	  x = x & 31;                  /* drop overflowing bits        */
	  if (e == 0) return x;        /* catch trivial case and       */
	  if (e > 3)  return x;        /* unwanted values              */

	  u = x >> (5-e);
	  v = u << 2;
	  w = (x << e) & 31;
	  s = (u ^ v) ^ w;
	  return s;
	}
	
		
	
	/** 
	 * Omitted from original source code: wsum
	 * (Was not used).                                        
	 */

	/**
	 * Transform to codeword.
	 * 
	 * Transform an array p of six 5-bit-integers into a codeword, 
	 * consisting of eight 5-bit-integers. Used in PIDgen. 
	 * @param p Array of six Integers, assumed to be unsigned 5-bit.
	 * @return Array of eight Integers in the range of unsigned 5-bit-Numbers.
	 */
	private static int[] encode(int p[])
	{
		int c[] = new int[8];
		int i;
		for (i=0; i<=5; i++) c[i] = p[i]; /* preserve input elements */
		c[6] = PIDGenerator.wsum1(p);                     /* weighted sum in F_32 */
		c[7] = PIDGenerator.wsum2(p);                     /* weighted sum in F_32 */	
		
		return c;
	}
	
	
	/** Generate PID.
	 * 
	 */
	private String PIDgen(int x)
	{
	  int y, z;
	  int j;               /* loop counter                    */
	  int pcw[];          /* intermediate result             */
	  int cw[];              /* intermediate result             */
	  char p[] = "00000000".toCharArray();  /* output variable            */

	  if (x == 0) return new String(p);     /* x outside required range        */
	  if (x >= NN) return new String(p);    /* x outside required range        */

	  // rndsetup(rndwidth); // moved to constructor
	  y = rndext(x);            /* randomize (or not)              */
	  z = encr(y);              /* encrypt                         */
	  pcw = u2pcw(z);            /* split                           */
	  cw = encode(pcw);          /* encode                          */
	  
	  for (j=0; j<=7; j++) p[j] = PIDGenerator.sigma[cw[j]]; /* rewrite as PID  */
	  return new String(p);
	}

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
	  for (i=0; i<=5; i++) s = s ^ PIDGenerator.multf32(p[i],i+1);
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
	  for (i=0; i<=5; i++) s = s ^ PIDGenerator.multf32(p[i],2*i+2);
	  return s;
	}

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
	    x = mult0f32(mult0f32(x,2),2); /* multiply by t^4          */
	    e = e-4;
	    }
	  x = mult0f32(x,e);
	  return x;
	}
	
}
