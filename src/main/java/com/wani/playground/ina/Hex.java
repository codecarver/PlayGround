package com.wani.playground.ina;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

public final class Hex {

    public static final String Encoding = "ISO8859_1";
    public static final int PadLeft = 0;
    public static final int PadRight = 1;

    /* ---------------------------------------------------------------------- */
    /* Convert hexadecimal into byte array.                                   */
    /* ---------------------------------------------------------------------- */
    public static byte[] hex2Byte( String h ) {
    
        if( ( h.length() % 2 ) != 0 ) {
        
            h = "0" + h;
        }

        int l    = ( int ) h.length() / 2;

        byte[] r = new byte[ l ];

        for( int i = 0, j = 0, k = h.length(); i < k; i += 2, j++ ) {
        
            r[ j ] = Short.valueOf( h.substring( i, i+2 ),16 ).byteValue();
        }

        return r;
    }

    /* ---------------------------------------------------------------------- */
    /* Convert hexadecimal into ascii string                                  */
    /* ---------------------------------------------------------------------- */
    public static String hex2String( String h ) {
    
        return ( new String( hex2Byte( h ) ) );
    }

    /* ---------------------------------------------------------------------- */
    /* Convert byte array into hexadecimal string.                            */
    /* ---------------------------------------------------------------------- */
    public static String byte2Hex( byte[] b ) {
    
        StringBuffer sbuf = new StringBuffer();

        for( int i = 0, n = b.length; i < n; i++ ) {
        
            byte hiByte = ( byte ) ( ( b[ i ] & 0xF0 ) >> 4 );
            byte loByte = ( byte )   ( b[ i ] & 0x0F );

            sbuf.append( Character.forDigit( hiByte, 16 ) );
            sbuf.append( Character.forDigit( loByte, 16 ) );
        }

        return sbuf.toString();
    }

    /* ---------------------------------------------------------------------- */
    /* Convert ascii string into hexadecimal string.                          */
    /* ---------------------------------------------------------------------- */
    public static String String2Hex( String s ) {
    
    	return ( byte2Hex( s.getBytes(  ) ) );
    }

    /* ---------------------------------------------------------------------- */
    /* Convert ascii int into binary string representation                    */
    /* ---------------------------------------------------------------------- */
    public static String str2bcd( int argInt ) {
    
        try {
        
            ByteArrayOutputStream o = new ByteArrayOutputStream();
            o.write( argInt >> 8 );
            o.write( argInt );

            return o.toString( Hex.Encoding );
        }
        catch( UnsupportedEncodingException e ) {

            e.printStackTrace();
        }
        
        return "0";
    }
    
    /* ---------------------------------------------------------------------- */
    /* Convert binary int into ascii int representation                       */
    /* ---------------------------------------------------------------------- */
    public static int bcd2str( String argInt ) {
    
        try {
        
            byte[] b = argInt.getBytes( Hex.Encoding );

            return ( ( ( ( int ) b[ 0 ] ) & 0xFF ) << 8 ) | ( ( ( int ) b[ 1 ] ) & 0xFF );
        }
        catch( UnsupportedEncodingException e ) {
        
            e.printStackTrace();
        }
        
        return 0;
    }

    /* ---------------------------------------------------------------------- */
    /* Convert binary int into ascii int representation                       */
    /* ---------------------------------------------------------------------- */
    public static int bcd2str( byte[] argInt ) {
    
        try {
        
            return ( ( ( ( int ) argInt[ 0 ] ) & 0xFF ) << 8 ) | ( ( ( int ) argInt[ 1 ] ) & 0xFF );
        }
        catch( Exception e ) {
        
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /* ---------------------------------------------------------------------- */
    /* pad the string with specified pad character and length in total        */
    /* ---------------------------------------------------------------------- */
    public static String pad( String m, int pos, String with, int total ) {
    
    	if( m.length() < total ) {
    		
    		StringBuffer s = new StringBuffer( m );
    		
    		while( s.length() < total ) {
    			if( pos == Hex.PadLeft ) {
    		
    				s.insert( 0, with );
    			}
    			else if( pos == Hex.PadRight ) {
    				
    				s.append( with );
    			}
    		}
    		
    		return s.toString();
    	}
    	else {
    		if( pos == Hex.PadLeft ) {
    	    
    			return m.substring( m.length() - total );
    		}
    		else if( pos == Hex.PadRight ) {
    	    	
    			return m.substring( 0, total );
    		}
    	}
    	
    	return m.substring( 0, total );
    }

    /* ---------------------------------------------------------------------- */
    /* pad the string with specified pad character and length in total        */
    /* ---------------------------------------------------------------------- */
    public static String pad( String m, String pos, String with, int total ) {
    
    	if( m.length() < total ) {
    		
    		StringBuffer s = new StringBuffer( m );
    		
    		while( s.length() < total ) {
    		
    			if( "L".equals( pos ) ) {
    				s.insert( 0, with );
    			}
    			else if( "R".equals( pos ) ) {
    				s.append( with );
    			}
    			else if( "X".equals( pos ) ) {
    				return s.toString();
    			}
    		}
    		return s.toString();
    	}
    	else {
    		if( "L".equals( pos ) ) {
    			
    	    	return m.substring( m.length() - total );
    		}
    		else if( "R".equals( pos ) ) {
    	    	
    			return m.substring( 0, total );
    		}
    	}
    	
    	return m.substring( 0, total );
    }
    
    /* ---------------------------------------------------------------------- */
    /* decode PDU string into ASCII string representation                     */
    /* ---------------------------------------------------------------------- */
	public static String decode7bit( String src ) {
	
		String result = null;
		String temp = null;
		byte left = 0;
		
		if( ( src != null ) && ( src.length() % 2 == 0 ) ) {
			
			result = "";
			int[] b = new int[ src.length() / 2 ];
			temp = src + "0";
			
			int i = 0; int j = 0;
			
			for( int k = 0; i < temp.length() - 2; j++ ) {
				
				b[j] = Integer.parseInt( temp.substring( i, i + 2 ), 16 );
				
				k = j % 7;
				byte srcAscii = ( byte ) ( b[j] << k & 0x7F | left );
				result = result + ( char ) srcAscii;
				left = ( byte ) ( b[j] >>> 7 - k );

				if( k == 6 ) {
					
					result = result + ( char ) left;
					left = 0;
				}
				
				if( j == src.length() / 2 )
					result = result + ( char )left;
				
				i += 2;
			}
		}
		
		return result.trim();
	}
	
	/**
     * xor two hexadecimal string
     * @param a first hexadecimal
     * @param b second hexadecimal
     * @return xor result
     */
	public static String xorHex( String a, String b ) {
	    char[] chars = new char[ a.length() ];

	    for( int i = 0; i < chars.length; i++ ) {
	        chars[i] = Hex.toHex( Hex.fromHex( a.charAt(i) ) ^ Hex.fromHex( b.charAt(i) ) );
	    }

	    return new String( chars );
	}
	
	private static int fromHex( char c ) {
	    if( c >= '0' && c <= '9' )
	        return c - '0';

	    if( c >= 'A' && c <= 'F' )
	        return c - 'A' + 10;

	    if( c >= 'a' && c <= 'f' )
	        return c - 'a' + 10;

	    throw new IllegalArgumentException( "not.a.hexadecimal("+ c +")" );
	}

	private static char toHex( int nibble ) {
	    if( nibble < 0 || nibble > 15 ) {
	        throw new IllegalArgumentException( "not.a.hexadecimal("+ nibble +")" );
	    }

	    return "0123456789ABCDEF".charAt( nibble );
	}

}

