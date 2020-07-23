package com.wani.playground.ina;

import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 
 * @author Siwi, Andrianus
 * 
 * Sample test class to provide 3DES with CDC method
 * 
 */
public class Test3DesCbc {
	
	private static String KEY_1 = "1FF4EFF767A715EC";
	private static String KEY_2 = "31A2371AFDD0EA43";
	private static String IV = "0000000000000000";
	private static final String TRANSFORMATION = "DES/CBC/NoPadding";
	private static final String TRANSFORMATION_E = "DES/ECB/NoPadding";
	
    public static void main(String[] args) throws Exception  {
    	
    	String plainText = "3475BED9D68939F629E177A2E3EF2C9D";	// 2 block
//    	String plainText = "3475BED9D68939F6";	// 1 block
//    	String plainText = "3475BED9D68939F629E177A2E3EF2C9D3475BED9D68939F6";	// 3 block
//    	String plainText = "3475BED9D68939F629E177A2E3EF2C9D3475BED9D68939F63475BED9D6893123";	// 4 block
    	System.out.println("plain: " + plainText);
    	
    	byte[] encrypted = encrypt3DesCbc(Hex.hex2Byte(IV), Hex.hex2Byte(plainText));	// recursive version
    	System.out.println("encrypted: " + Hex.byte2Hex(encrypted).toUpperCase());
    	
    	byte[] decrypted = decrypt3DesCbc(Hex.hex2Byte(IV), encrypted, null);	// recursive version
    	System.out.println("decrypted: " + Hex.byte2Hex(decrypted).toUpperCase());
    	
    }

    private static byte[] encrypt3DesCbc(byte[] iv, byte[] message) throws Exception {
        
        try {
		
        	byte[] currentBlock = new byte[8];
        	System.arraycopy(message, 0, currentBlock, 0, 8);
        	
        	// xor
        	String hexIv = Hex.byte2Hex(iv);
        	String xorBlock1 = Hex.xorHex(Hex.byte2Hex(currentBlock), hexIv.substring(hexIv.length()-16));
        	
        	// encrypt
        	byte[] encryptedBlock1 = encrypt3DesEcb(Hex.hex2Byte(xorBlock1));
        	
        	if(message.length/currentBlock.length == 1){
        		
        		if(Arrays.equals(iv, Hex.hex2Byte(IV)))
        			return encryptedBlock1;
        			
        		byte[] result = ByteBuffer.allocate(iv.length + encryptedBlock1.length)
                        .put(iv)
                        .put(encryptedBlock1)
                        .array();
        		
        		return result;
        	}
        	
        	// recursive
        	else{
        		
        		byte[] nextBlock = new byte[message.length - currentBlock.length];
        		System.arraycopy(message, currentBlock.length, nextBlock, 0, message.length - currentBlock.length);
        		
        		byte[] nextIv = new byte[iv.length];
        		
        		if(Arrays.equals(iv, Hex.hex2Byte(IV))){
        			
        			System.arraycopy(encryptedBlock1, 0, nextIv, 0, encryptedBlock1.length);
        		}
        		else{
        			
        			nextIv = new byte[iv.length + 8];
        			
        			System.arraycopy(iv, 0, nextIv, 0, iv.length);
        			System.arraycopy(encryptedBlock1, 0, nextIv, iv.length, encryptedBlock1.length);
        		}
        		
        		return encrypt3DesCbc(nextIv, nextBlock);
        	}
        
        	
		} catch (Exception e) {
		
			System.out.println(e.getMessage());
		}
        	
        return null;
    }
    
    private static byte[] decrypt3DesCbc(byte[] iv, byte[] message, byte[] xor) throws Exception {
        
    	try {
    		
    		// latest reminding block, or the only one block
        	if(message.length/8 == 1){
        		
        		byte[] decryptedBlock1 = decrypt3DesEcb(message);
        		String xorBlock = Hex.xorHex(Hex.byte2Hex(iv), Hex.byte2Hex(decryptedBlock1));
        		
        		// xor null, data only 1 block
        		if(xor == null)
        			return decryptedBlock1;
        		
        		if(xor != null){
	        		
        			byte[] result = ByteBuffer.allocate(Hex.hex2Byte(xorBlock).length + xor.length)
	                        .put(Hex.hex2Byte(xorBlock))
	                        .put(xor)
	                        .array();
	        		
	        		return result;
        		}
        	}
        	
        	// recursive mode
        	else{
        	
        		// reminding block
        		byte[] block0 = new byte[message.length-8];            	
            	System.arraycopy(message, 0, block0, 0, message.length-8);
            	
            	// one block before current block
        		byte[] block1 = new byte[8];            	
            	System.arraycopy(message, message.length-16, block1, 0, 8);
            	
            	// current block
        		byte[] block2 = new byte[8];
        		System.arraycopy(message, message.length-8, block2, 0, 8);
        	
        		// decrypt
        		byte[] decryptedBlock2 = decrypt3DesEcb(block2);
        		
        		// xor
        		String xorBlock2 = Hex.xorHex(Hex.byte2Hex(block1), Hex.byte2Hex(decryptedBlock2));
        		
        		if(xor != null)
        			xorBlock2 = xorBlock2 + Hex.byte2Hex(xor);
        		
        		return decrypt3DesCbc(iv, block0, Hex.hex2Byte(xorBlock2));
        	}
        
        	
		} catch (Exception e) {
		
			System.out.println(e.getMessage());
		}
        	
        return null;
    }

    private static byte[] encrypt3DesEcb(byte[] message) throws Exception {
        
    	Cipher encr1, decr2, encr3;
        SecretKeySpec kL, kR;

        kL = new SecretKeySpec(Hex.hex2Byte(KEY_1),
                "DES"
        );
        kR = new SecretKeySpec(Hex.hex2Byte(KEY_2),
            "DES"
        );
            
        encr1 = Cipher.getInstance(TRANSFORMATION_E);
        decr2 = Cipher.getInstance(TRANSFORMATION_E);
        encr3 = Cipher.getInstance(TRANSFORMATION_E);

        encr1.init(Cipher.ENCRYPT_MODE, kL);
        decr2.init(Cipher.DECRYPT_MODE, kR);
        encr3.init(Cipher.ENCRYPT_MODE, kL);

        return encr3.doFinal( decr2.doFinal( encr1.doFinal(message) ) );
    }
    
    private static byte[] decrypt3DesEcb(byte[] message) throws Exception {
        Cipher decr1, encr2, decr3;
        SecretKeySpec kL, kR;

        kL = new SecretKeySpec(Hex.hex2Byte(KEY_1),
            "DES"
        );
        kR = new SecretKeySpec(Hex.hex2Byte(KEY_2),
            "DES"
        );

        decr1 = Cipher.getInstance(TRANSFORMATION);
        encr2 = Cipher.getInstance(TRANSFORMATION);
        decr3 = Cipher.getInstance(TRANSFORMATION);

        final IvParameterSpec iv = new IvParameterSpec(Hex.hex2Byte(IV));
        decr1.init(Cipher.DECRYPT_MODE, kL, iv);
        encr2.init(Cipher.ENCRYPT_MODE, kR, iv);
        decr3.init(Cipher.DECRYPT_MODE, kL, iv);

        return decr3.doFinal( encr2.doFinal( decr1.doFinal(message) ) );
    }
    
    @Deprecated
    @SuppressWarnings("unused")
    private static byte[] decrypt3DesCbc(byte[] message) throws Exception {
        
    	if(message.length == 16){
            
    		byte[] encryptedBlock1 = new byte[8];
        	byte[] encryptedBlock2 = new byte[8];
        	
        	System.arraycopy(message, 0, encryptedBlock1, 0, 8);
        	System.arraycopy(message, 8, encryptedBlock2, 0, 8);
        	
        	byte[] plainBlock1 = decrypt3DesEcb(encryptedBlock1);
        	byte[] ebcBlock2 = decrypt3DesEcb(encryptedBlock2);
        	
        	String xorBlock2 = Hex.xorHex(Hex.byte2Hex(encryptedBlock1), Hex.byte2Hex(ebcBlock2));
        	
        	byte[] result = ByteBuffer.allocate(plainBlock1.length + Hex.hex2Byte(xorBlock2).length)
                    .put(plainBlock1)
                    .put(Hex.hex2Byte(xorBlock2))
                    .array();
        	
        	return result;
        }
        
        return null;
    }
    
    @SuppressWarnings("unused")
	@Deprecated
    private static byte[] encrypt3DesCbc(byte[] message) throws Exception {
        
        if(message.length == 16){
        
        	byte[] plainBlock1 = new byte[8];
        	byte[] plainBlock2 = new byte[8];
        	
        	System.arraycopy(message, 0, plainBlock1, 0, 8);
        	System.arraycopy(message, 8, plainBlock2, 0, 8);
        	
        	byte[] ebcBlock1 = encrypt3DesEcb(plainBlock1);
        	
        	String xorBlock2 = Hex.xorHex(Hex.byte2Hex(ebcBlock1), Hex.byte2Hex(plainBlock2));
        	byte[] ebcBlock2 = encrypt3DesEcb(Hex.hex2Byte(xorBlock2));
        	
        	byte[] result = ByteBuffer.allocate(ebcBlock1.length + ebcBlock2.length)
                    .put(ebcBlock1)
                    .put(ebcBlock2)
                    .array();
        	
        	return result;
        }
        
        return null;
    }
}

