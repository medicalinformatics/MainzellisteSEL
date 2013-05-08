/*
 * Copyright (C) 2013 Martin Lablans, Andreas Borg, Frank Ückert
 * Contact: info@mainzelliste.de

 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free 
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License 
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 *
 * If you modify this Program, or any covered work, by linking or combining it 
 * with Jersey (https://jersey.java.net) (or a modified version of that 
 * library), containing parts covered by the terms of the General Public 
 * License, version 2.0, the licensors of this Program grant you additional 
 * permission to convey the resulting work.
 */
package de.unimainz.imbei.mzid;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import de.unimainz.imbei.mzid.PhonetRules;

/**
 * This class provides functions for generation of phonetic codes.
 * @author warnecke
 *
 */
public class Phonet {

	 /**
     * Version string
     *
     */
    private static final String PHONET_VERSION = "version 1";

    /**
     * Use first rule set for conversion
     *
     */
    private static final int FIRST_RULES       = 0;

    /**
     * Use second rule set for conversion
     *
     */
    private static final int SECOND_RULES      = 10000;

    /**
     * Use german conversion rules
     *
     */
    public static final int PHONET_GERMAN    = 1;

    /**
     * Use english conversion rules
     *
     */
    public static final int PHONET_ENGLISH   = 2;
    
    /**
     * Which language to use if none was specified
     *
     */
    public static final int PHONET_DEFAULT_LANGUAGE = PHONET_GERMAN;

    /**
     * constants for runMode
     *
     */
    private static final int IS_INITIALIZED   = 1;
    public  static final int DO_TRACE         = 2;
    public  static final int CHECK_RULES      = 4;

    /**
     * length of one char (256=8bit)
     *
     */
    public static final int HASH_COUNT = 256;

    /**
     * list of "normal" letters and umlauts, with upper case
     *
     */
    public static final String letters_a_to_z  = "abcdefghijklmnopqrstuvwxyz";
    public static final String letters_A_to_Z  = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String umlaut_lower    = "àáâãåäæçðèéêëìíîïñòóôõ";
    public static final String umlaut_upper    = "ÀÁÂÃÅÄÆÇÐÈÉÊËÌÍÎÏÑÒÓÔÕ";

    
    /**
     * String representation of current language
     *
     */
    private static       String   phonetLanguage = "unknown";

    /**
     * init flag
     *
     */
    private static       int      phonetInit     = -1;

    /**
     * hashing arrays
     *
     */
    private static       int[]    phonetHash     = null;
    private static       int[][]  phonetHash1    = null;
    private static       int[][]  phonetHash2    = null;

    /**
     * reference to current used rule-set
     *
     */
    private static       String[] phonetRules    = null;

    /**
     * run mode (init, trace, check_rules)
     *
     */
    private static int  runMode = 0;

    /**
     * which rules (first or second rules)
     *
     */
    private static int  lastRuleSet = -SECOND_RULES;

    /**
     * letter indexing arrays
     *
     */
    private static int[]  alphaPos = new int[HASH_COUNT];
	private static char[] upperChar = new char[HASH_COUNT];
	private static int[]  isLetter  = new int[HASH_COUNT];

    /**
     * Output trace info
     * 
     * @param text Text to print
     * @param n    Rule-number
     * @param err_text Error text to print
     *
     */
    private static void traceInfo (String  text, int n, String err_text) {
        
      String s,s2,s3;
      s  = (getPhonetRules()[n] == null)  ?  "(NULL)" : getPhonetRules()[n];
      s2 = (getPhonetRules()[n+1] == null) ? "(NULL)" : getPhonetRules()[n+1];
      s3 = (getPhonetRules()[n+2] == null) ? "(NULL)" : getPhonetRules()[n+2];

      System.out.println(text + " " + ((n/3)+1) + ":  \"" + s + " \" " + s2 + " \" " + s3 + " \" " + err_text);

    }

    /**
     * static initializer
     * 
     *
     */
    static {
        String s, s2;
        int k, i, ip, si;
        int p_hash1[], p_hash2[];

        // check helper arrays
        if (letters_a_to_z.length() > 26) {
            System.err.println("Error: strlen (letters_a_to_z) > 26  is not allowed");
        }
        if (letters_a_to_z.length() != letters_A_to_Z.length()) {
            System.err.println("Error: strlen(letters_a_to_z) != strlen(letters_a_to_z)  is not allowed");
        }
        if (umlaut_lower.length() != umlaut_upper.length()) {
            System.err.println("Error: strlen(umlaut_lower) != strlen(umlaut_upper)  is not allowed");
        }

        //  generate arrays "alpha_pos", "upperchar" and "isletter"
        for (i=0; i< HASH_COUNT; i++) {
            alphaPos[i] = 0;
            isLetter[i] = 0;
            upperChar[i] = (char) i;
        }

        for (k=-1; k<1; k++) {
            
            if (k == -1) {
                // German and international umlauts
                s = umlaut_lower;
                s2 = umlaut_upper;

            } else {
                // "normal" letters ('a'-'z' and 'A'-'Z')
                s = letters_a_to_z;
                s2 = letters_A_to_Z;

            }
            
            for (i=0; i < s.length() ; i++) {
                
                if (k == -1)
                    ip = k;
                else
                    ip = i;

                alphaPos [(short)s.charAt(i)] = ip + 2;
                alphaPos [(short)s2.charAt(i)] = ip + 2;
                isLetter [(short)s.charAt(i)] = 1;
                isLetter [(short)s2.charAt(i)] = 1;
                upperChar [(short)s.charAt(i)] = s2.charAt(i);
                upperChar [(short)s2.charAt(i)] = s2.charAt(i);
            } // for i
        
        } // for k
        
        setPhonetLanguage(Phonet.PHONET_GERMAN);
        
        runMode = runMode | IS_INITIALIZED;

        if (phonetInit != -1  &&  phonetHash != null  &&  getPhonetRules() != null) {

            phonetInit = phonetInit | IS_INITIALIZED;
            
            for (i=0; i < HASH_COUNT; i++) {
                phonetHash[i] = -1;
            }
            
            for (i=0; i<26; i++) {
                for (k=0; k<28; k++) {
                    phonetHash1[i][k] = -1;
                    phonetHash2[i][k] = -1;
                    
                }
            }
            
            for (i=0; getPhonetRules()[i] != PhonetRules.PHONET_END; i += 3) {
             
                if ( (s = getPhonetRules()[i]) != null) {
                    
                    si = 0;
                    char[] sc = new String(s + "\0").toCharArray();
                    
                    //  calculate first hash value
                    k = (short) sc[si];

                    if (phonetHash[k] < 0 && (getPhonetRules()[i+1] != null  ||  getPhonetRules()[i+2] != null)) {
                        phonetHash[k] = i;
                    }
                    
                    // calculate second hash values
                    if (k != 0  &&  alphaPos[k] >= 2) {

                        k = alphaPos[k];

                        p_hash1 = phonetHash1[k-2];
                        p_hash2 = phonetHash2[k-2];
                        si++;

                        if (sc[si] == '(') {
                            si++;
                        } else if (sc[si] == '\0') {
                            sc = new String(" \0").toCharArray();
                            si = 0;
                        } else {
                            sc = new String(sc[si] + "\0").toCharArray();
                            si = 0;
                        }

                        while (sc[si] != '\0'  &&  sc[si] != ')') {
                            
                            k = alphaPos [(short)sc[si]];

                            if (k > 0) {

                                // add hash value for this letter
                                if (p_hash1[k] < 0) {
                                    p_hash1[k] = i;
                                    p_hash2[k] = i;
                                }
                                
                                if (p_hash2[k] >= i - 30) {
                                    p_hash2[k] = i;
                                } else {
                                    k = -1;
                                }
                            }
                            
                            if (k <= 0) {
                                // add hash value for all letters
                                if (p_hash1[0] < 0) {
                                    p_hash1[0] = i;
                                }
                                p_hash2[0] = i;
                            }
                            si++;
                        }
                    } // end if calc second hash

                } // end if phonetRules != null
                
            } // end for phonetRules...

        } // end if (phonetInit != -1  &&  phonetHash != null  &&  phonetRules != null)

    }

    /**
     * Wrapper for first rule-conversion
     * 
     * @param src String to convert with first rules
     *
     * @return Converted String
     */
    public static String phonetic1(String src) {
        char[] result = new char[255];
        int retCode = Phonet.phonet(src, result, 255, FIRST_RULES);

        String resultString = new String(result);
        int resIndex = resultString.indexOf('\0');
        return resultString.substring(0, resIndex);
        
     
    }
    /**
     * Wrapper for first rule-conversion
     * 
     * @param src String to convert with first rules
     *
     * @return Converted PlainTextField
     */
    public static PlainTextField phonetic1P(PlainTextField src) {
        char[] result = new char[255];
        String str = src.getValue();
        int retCode = Phonet.phonet(str, result, 255, FIRST_RULES);

        String resultString = new String(result);
        int resIndex = resultString.indexOf('\0');
        
        return new PlainTextField(new String(resultString.substring(0, resIndex)));

    }
    /**
     * Wrapper for second rule-conversion
     * 
     * @param src String to convert with second rules
     *
     * @return Converted String
     */
    public static String phonetic2(String src) {
        char[] result = new char[255];
        int retCode = Phonet.phonet(src, result, 255, SECOND_RULES);

        String resultString = new String(result);
        int resIndex = resultString.indexOf('\0');
        return resultString.substring(0, resIndex);
    }

    /**
     * Function for phonetic conversions
     * 
     * @param src String to convert with second rules
     * @param dest space for text after conversion
     * @param len Length of String
     * @param mode switch for language and first/second rules
     *
     * @return Conversion success code
     */
    public static int phonet(String src, char dest[], int len, int mode) {
        int  i,j=0,k,n,p,z;
        int  k0,n0,p0,z0;
        int  start1=0,end1=0,start2=0,end2=0;
        int  start3=0,end3=0,start4=0,end4=0;
        String msg;
        int p_hash1[], p_hash2[];
        char c,c0=' ';
        char[] src_2, sc;
        int si;
        
        //runMode = runMode | DO_TRACE;
        
        if ((runMode & DO_TRACE) != 0) {
            System.out.println(">phonet(" + src + ", dest, " + len + ", " + mode);
        }
        
        if (src == null) {
            
            // wrong arg's
            System.out.println ("Error: wrong arguments.\n");
            return (-1);
        }
        
        // select language
        i = 0;
       

        // toUppercase workaround
        src_2 = new char[src.length()+1];
        for(int cnt=0;cnt<src.length();cnt++) {
            src_2[cnt] = Character.toUpperCase(src.charAt(cnt));
        }
        src_2[src_2.length-1] = '\0';

        String strMode;
        if (mode < SECOND_RULES) {
            mode = 1;
            strMode = "first";
        } else {
            mode = 2;
            strMode = "second";
        }
        
      if ((runMode & DO_TRACE) != 0) {
          System.out.println("phonetic conversion for  :  \""+new String(src_2)+"\"");
          System.out.println("(" + strMode +" rules)");
      }

        //  check "src"
        i = 0;
        j = 0;
        z = 0;

        while ((c = src_2[i]) != '\0') {

            if ((runMode & DO_TRACE) != 0) {
                System.out.println("check position "+j+":  src = \""+new String(src_2, i, src_2.length-i-1)+"\",");
                String tmpDest = new String(dest);
                System.out.println("  dest = [" + tmpDest.substring(0, tmpDest.indexOf('\0')) + "]");

            }
            
            n = alphaPos[(short) c];

            if (n >= 2) {
                
                p_hash1 = phonetHash1[n-2];
                p_hash2 = phonetHash2[n-2];
                
                n = alphaPos[(short) src_2[i+1]];
                start1 = p_hash1[n];
                start2 = p_hash1[0];
                end1 = p_hash2[n];
                end2 = p_hash2[0];
                
                // preserve rule priorities
                if (start2 >= 0 && (start1 < 0  ||  start2 < start1)) {
                    n = start1;
                    start1 = start2;
                    start2 = n;
                    n = end1;
                    end1 = end2;
                    end2 = n;
                }
                
                if (end1 >= start2  &&  start2 >= 0) {
                    if (end2 > end1) {
                        end1 = end2;
                    }
                    start2 = -1;
                    end2 = -1;
                }
            } else {
                n = phonetHash[(short) c];
                start1 = n;
                end1 = 10000;
                start2 = -1;
                end2 = -1;
            } // end if n >= 2
            
            n = start1;
            z0 = 0;

            if (n >= 0) {
                
                //  check rules for this char
                while (getPhonetRules()[n] == null  || getPhonetRules()[n].length() == 0  ||  getPhonetRules()[n].charAt(0) == c ) {
                //while (phonetRules[n] == null ||  (phonetRules[n].length() != 0 && phonetRules[n].charAt(0) == c0)) {

                    if (n > end1) {
                        
                        if (start2 > 0) {
                            n = start2;
                            start1 = start2;  start2 = -1;
                            end1 = end2;  end2 = -1;
                            continue;
                        }
                        break;
                    } // if n > end1

                    if (getPhonetRules()[n] == null  ||  getPhonetRules()[n+mode] == null) {
                        //no conversion rule available
                        n += 3;
                        continue;
                    }

                    if ((runMode & DO_TRACE) != 0) {
                        traceInfo ("> rule no.", n, "is being checked");
                    }
                    
                    //  check whole string
                    k = 1;   //  no. of matching letters
                    p = 5;   //  default priority
                    sc = (getPhonetRules()[n]+"\0").toCharArray();
                    si = 1;     // needed by "*(s-1)" below
                    
                    while (sc[si] != '\0'  &&
                            src_2[i+k] == sc[si] &&
                            !Character.isDigit(sc[si]) &&
                            "(-<^$".indexOf(sc[si]) == -1) { //strchr ("(-<^$", *s) == NULL) {
                        k++;
                        si++;
                    }
              
                    if ((runMode & CHECK_RULES) != 0) {
                        
                        //  we do "check_rules"
                        while (sc[si] != '\0'  &&  src_2[i+k] == sc[si]) {
                            k++;
                            si++;
                        }
                    }
              
                    if (sc[si] == '(') {
                        
                        //  check an array of letters
                        if ( (isLetter[(short) src_2[i+k]] != 0) && (strchr(sc, si+1, src_2[i+k]) != -1) ) {
                            
                            k++;
                            while (sc[si] != '\0'  &&  sc[si] != ')') {
                                si++;
                            }
                            if (sc[si] == ')') {
                                si++;
                            }
                        }
                    } // end if *s == '(' 

                    p0 = (short) sc[si];
                    k0 = k;
                    while (sc[si] == '-'  &&  k > 1) {
                        k--;
                        si++;
                    }
                    if (sc[si] == '<') {
                        si++;
                    }
                    if (Character.isDigit(sc[si] )) {
                        //  read priority
                        p = sc[si] - '0';
                        si++;
                    }
                    
                    if (sc[si] == '^'  &&  sc[si+1] == '^') {
                        si++;
                        if ((runMode & CHECK_RULES) != 0  &&  isLetter [(short) src_2[i+k0]] == 0) {
                            //  we do "check_rules"
                            si = si-2;
                        }
                    }
                    
                    if ( sc[si] == '\0'  ||
                         (sc[si] == '^'  &&
                         (i == 0  ||  isLetter[(short)src_2[i-1]] == 0) &&
                         (sc[si+1] != '$' ||
                         (isLetter[(short) src_2[i+k0]] == 0 &&  src_2[i+k0] != '.'))) ||
                         (sc[si] == '$'  &&  i > 0  &&  isLetter[(short) src_2[i-1]] != 0 &&
                         (isLetter[(short) src_2[i+k0]] == 0 &&  src_2[i+k0] != '.')))
                    {
                        //  look for continuation, if:
                        //  k > 1  and  NO '-' in first string
                        n0 = -1;
                        
                        if (k > 1  &&  src_2[i+k] != '\0'  &&  p0 != (short) '-') {
                            c0 = src_2[i+k-1];
                            n0 = alphaPos[(short) c0];
                            
                            if (n0 >= 2  &&  src_2[i+k] != '\0') {

                                p_hash1 = phonetHash1[n0-2];
                                p_hash2 = phonetHash2[n0-2];
                                n0 = alphaPos[(short) src_2[i+k]];
                                start3 = p_hash1 [n0];
                                start4 = p_hash1 [0];
                                end3 = p_hash2 [n0];
                                end4 = p_hash2 [0];
                                
                                //  preserve rule priorities
                                if (start4 >= 0 && (start3 < 0  ||  start4 < start3)) {
                                    n0 = start3;
                                    start3 = start4;
                                    start4 = n0;
                                    n0 = end3;
                                    end3 = end4;
                                    end4 = n0;
                                }
                                
                                if (end3 >= start4  &&  start4 >= 0) {
                                    if (end4 > end3) {
                                        end3 = end4;
                                    }
                                    start4 = -1;
                                    end4 = -1;
                                }
                            } else {
                                n0 = phonetHash [(short) c0];
                                start3 = n0;
                                end3 = 10000;
                                start4 = -1;
                                end4 = -1;
                            }
                            
                            n0 = start3;
                        } // end if look for continuation
                        
                        if (n0 >= 0) {
                            // check continuation rules for "src[i+k]"
                            while (getPhonetRules()[n0] == null ||  (getPhonetRules()[n0].length() != 0 && getPhonetRules()[n0].charAt(0) == c0)) {
                                if (n0 > end3) {
                                    if (start4 > 0) {
                                        n0 = start4;
                                        start3 = start4;  start4 = -1;
                                        end3 = end4;  end4 = -1;
                                        continue;
                                    }
                                    p0 = -1;  // ****  important  ****
                                    break;
                                }
                                
                                if (getPhonetRules() [n0] == null ||  getPhonetRules() [n0+mode] == null) {
                                    // no conversion rule available
                                    if ((runMode & DO_TRACE) != 0) {
                                        traceInfo ("> > no rule found.", n0, "");
                                    }

                                    n0 += 3;
                                    continue;
                                }
                                if ((runMode & DO_TRACE) != 0) {
                                    traceInfo ("> > continuation rule no.", n0, "is being checked");
                                }
                                
                                // check whole string
                                k0 = k;
                                p0 = 5;
                                sc = (getPhonetRules()[n0] + "\0").toCharArray();
                                si = 1;
                                while (sc[si] != '\0'  &&
                                       src_2[i+k0] == sc[si] &&
                                       !Character.isDigit((short) sc[si]) &&
                                       "(-<^$".indexOf(sc[si]) == -1) {
                                    k0++;
                                    si++;
                                }
                                if (sc[si] == '(') {
                                    // check an array of letters
                                    if (isLetter[(short) src_2[i+k0]] != 0 &&
                                        strchr(sc, si+1, src_2[i+k0]) != -1) {
                                        k0++;
                                        while (sc[si] != '\0' &&
                                               sc[si] != ')') {
                                            si++;
                                        }
                                        if (sc[si] == ')') {
                                            si++;
                                        }
                                    }
                                }
                                while (sc[si] == '-') {
                                    // "k0" is NOT decremented
                                    // because of  "if (k0 == k)"
                                    si++;
                                }
                                if (sc[si] == '<') {
                                    si++;
                                }
                                if (Character.isDigit((short) sc[si])) {
                                    p0 = sc[si] - '0';
                                    si++;
                                }
                                
                                // *s == '^' is not possible here
                                if ( sc[si] == '\0' ||
                                     (sc[si] == '$'  &&
                                     isLetter[(short) src_2[i+k0]] == 0 &&
                                     src_2[i+k0] != '.')) {
                                    
                                    if (k0 == k) {
                                        //  this is only a partial string
                                        if ((runMode & DO_TRACE) != 0) {
                                            traceInfo("> > continuation rule no.", n0, "not used (too short)");
                                        }
                                        n0 += 3;
                                        continue;
                                    }
                                    
                                    if (p0 < p) {
                                        //  priority is too low
                                        if ((runMode & DO_TRACE) != 0) {
                                            traceInfo ("> > continuation rule no.", n0, "not used (priority)");
                                        }
                                        n0 += 3;
                                        continue;
                                    }
                                    
                                    // continuation rule found
                                    break;
                                } // end if
                                
                                if ((runMode & DO_TRACE) != 0) {
                                    traceInfo ("> > continuation rule no.", n0, "not used");
                                }
                                n0 += 3;
                            } // end of "while"
                            
                            if (p0 >= p &&
                                (getPhonetRules()[n0] != null  && getPhonetRules()[n0].length()>0 && getPhonetRules()[n0].charAt(0) == c0)) {
                                
                                if ((runMode & DO_TRACE) != 0) {
                                    traceInfo ("> rule no.", n, "");
                                    traceInfo ("> not used because of continuation", n0, "");
                                }
                                n += 3;
                                continue;
                            }
                        } // end if n0 >= 0
                        
                        // replace string
                        if ((runMode & DO_TRACE) != 0) {
                            traceInfo ("Rule no.", n, "is applied");
                        }
                        p0 = (getPhonetRules()[n].charAt(0) != '\0' &&
                              strchr (getPhonetRules()[n].toCharArray(), 1,'<') != -1) ?  1 : 0;
                        
                        sc = (getPhonetRules()[n+mode] + "\0").toCharArray();
                        si = 0;
                        
                        if (p0 == 1  &&  z == 0) {
                            
                            // rule with '<' is applied
                            if ((runMode & DO_TRACE) != 0) {
                                System.out.println("rule with < applied");
                            }

                            if (j > 0  &&
                                sc[si] != '\0' &&
                                (dest[j-1] == c  ||
                                dest[j-1] == sc[si])) {
                                j--;
                            }
                            
                            z0 = 1;
                            z++;
                            k0 = 0;

                            while (sc[si] != '\0'  &&  src_2[i+k0] != '\0') {
                                src_2[i+k0] = sc[si];
                                k0++;
                                si++;
                            }

                            if (k0 < k) {

                                int index = strchr(src_2, 0, '\0');
                                System.arraycopy(src_2, i+k, src_2, i+k0, index + 1 - i - k);

                            }

                            if ((runMode & CHECK_RULES) != 0 &&  (sc[si] != '\0'  ||  k0 > k)) {
                                // we do "check_rules":
                                // replacement string is too long
                                dest[j] = '\0';
                                return (-200);
                            }
                            //  new "current char"
                            c = src_2[i];
                        } else {
                            if (((runMode & CHECK_RULES) != 0) &&  p0 == 1  &&  z > 0) {
                                //  we do "check_rules":
                                // recursion found -> error
                                dest[j] = '\0';
                                return (-100);
                            }
                            
                            i = i+k-1;
                            z = 0;
                            while (sc[si] != '\0' &&  sc[si+1] != '\0'  &&  j < len-1) {
                                if (j == 0  ||  dest[j-1] != sc[si]) {
                                    dest[j] = sc[si];
                                    j++;
                                }
                                si++;
                            }
                            
                            // new "current char"
                            c = sc[si];
                            
                             
                            if (getPhonetRules()[n].charAt(0) != '\0' &&  getPhonetRules()[n].indexOf("^^", 1) != -1) {
                                if (c != '\0') {
                                    dest[j] = c;
                                    j++;
                                }
                                
                                int index = strchr(src_2, 0, '\0');
                                System.arraycopy(src_2, i+1, src_2, 0, (index - (i + 1)) + 1);

                                i = 0;
                                z0 = 1;
                            }
                        }
                        
                        break;
                    }
                    
                    n += 3;
                    if (n > end1  &&  start2 > 0) {
                        n = start2;
                        start1 = start2;
                        end1 = end2;
                        start2 = -1;
                        end2 = -1;
                    }
                    
                } // while (phonet_rules[n] == NULL  ||  phonet_rules[n][0] == c)
                
            } // end if n >= 0
            
            if (z0 == 0) {
                
                if (j < len-1  &&  c != '\0' && (j == 0  ||  dest[j-1] != c)) {
                    
                    // delete multiple letters only
                    dest[j] = c;
                    j++;
                }
                i++;
                z = 0;
                
            } // end if z0 == 0
            
        } // end while iterate src
        
        //if (src_2 != text)
        //  {
        //   free (src_2);
        //  }
        dest[j] = '\0';
        
        if ((runMode & DO_TRACE) != 0) {
            System.out.println("<phonet() : " + new String(dest));
        }
        
        return (j);
        
        
    } // end phonet

    /**
     * Setter for conversion language
     * 
     * @param rule_set language code
     *
     * @return Success code
     */
    public static int setPhonetLanguage (int rule_set) {

        int result = -1;

        switch (rule_set) {

        //Possible future use
            /*case PHONET_ENGLISH :
                phonet_language = "English";
                       phonet_init   = & phonet_init_english;
                       phonet_hash   = phonet_hash_english;
                       phonet_hash_1 = & phonet_hash_english_1;
                       phonet_hash_2 = & phonet_hash_english_2;
                       phonet_rules  = phonet_rules_english;
                result=0;
                break;*/

            case PHONET_GERMAN :
                phonetLanguage = "German";
                phonetInit   = 0;
                phonetHash = new int[HASH_COUNT];
                phonetHash1 = new int[26][28];
                phonetHash2 = new int[26][28];
                setPhonetRules(PhonetRules.phonetRulesGerman);
                
                result=0;
                break;
        }

        return result;
    }

    /**
     * Loads phoneticRules from File
     * 
     * @param rule_set language code
     * 
     * Die Datei muss untereinander erstens den Suchbegriff, danach die erste Regel, danach die zweite Regel beinhalten
     * SIehe auch phonetRulesGerman
     */
    public static void loadPhonetRules(String path) {
    	ArrayList<String> str_list = new ArrayList<String>();
    	
    	try{
    		String zeile;
    		FileReader inputData = new FileReader(path);
    		BufferedReader br = new BufferedReader(inputData);

    		while ((zeile = br.readLine()) != null) 
    		{
    			System.out.println(zeile);
    			str_list.add(zeile);

    		} 
    		br.close();
    	}
    	catch (IOException ei) 
    	{
    			ei.printStackTrace();
    	}

    	
    	if (str_list.size() > 0)
    	{
    		String[] strArr = new String[str_list.size()];
    		strArr = str_list.toArray(strArr);
    		setPhonetRules(strArr);
    	}
    	else
    	{
    		setPhonetRules(null);
    	}
    		
    	
    }
    
    /**
     * Print usage info if launched as console application
     * 
     */
    public static void printUsage() {
    
        // vars
        int  i=-1;
        String s;
            
        System.out.println("Usage:  phonet  <orig>       [ <language> ]  [ -trace ]");
        System.out.println(" or  :  phonet -file  <file>  <FIRST_RULES | SECOND_RULES>  [ <language> ]");
        System.out.println(" or  :  phonet -check_rules  [ <language> ]  [ -trace [<rule_no>] ]");
        System.out.println(" or  :  phonet -write_rules  [ <language> ]");
        System.out.println("");
        System.out.println("Program for phonetic string conversion  ("+PHONET_VERSION+").\n");
        System.out.println("Options:");
        System.out.println("-file <file> :  Phonetically convert the given file.");
        System.out.println("-check_rules :  Check all phonetic rules. If no language is");
        System.out.println("                specified, all rules of all languages are checked.");
        System.out.println("");
        System.out.println("-trace       :  Output trace info. If a rule number is specified");
        System.out.println("                for \"-check_rules\", then only this rule will be");
        System.out.println("                traced.\n");
        System.out.println("Language may be one of the following numbers:");

        // NDM: iterate all possible languages
        for (i=FIRST_RULES; i< SECOND_RULES; i++) {
            
            // NDM: set next lang and check return code
            int setLangResult = setPhonetLanguage(i);

            if (setLangResult >= 0) {
                s = "";
                
                // NDM: mark default lang
                if (i == PHONET_DEFAULT_LANGUAGE) {
                    s = "  (default language)";
                }
                
                System.out.print(" "+i+":  "+phonetLanguage+s+"\n");
                
            } // end if
        } // end for
        
    }
    //Some tools
    public static int strchr(char[] buffer, int fromIndex, int ch) {
        int max = buffer.length;
        for(; fromIndex < max; fromIndex++) {
            if(buffer[fromIndex] == ch) {
                return fromIndex;
            }
        }
        return -1;
    }
	public static String[] getPhonetRules() {
		return phonetRules;
	}
	public static void setPhonetRules(String[] phonetRules) {
		Phonet.phonetRules = phonetRules;
	}
	
}

