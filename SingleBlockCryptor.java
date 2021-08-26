import java.util.ArrayList;
import java.math.BigInteger;
abstract class SingleBlockCryptor{		// encrypts/decrypts single 64 bit blocks 

	 public static long mul(long a, long b){
		
		// multiplying in IDEA

		BigInteger A = BigInteger.valueOf(a);
		BigInteger B = BigInteger.valueOf(b);
		BigInteger mod = BigInteger.valueOf(65537);			

		if(A.intValue() == 0 && B.intValue() != 0){
			A = BigInteger.valueOf(65536);
			return (((A.multiply(B)).mod(mod)).intValue() == 65536) ? 0 : (long) (((A.multiply(B)).mod(mod)).intValue());
		} else if(B.intValue() == 0 && A.intValue() != 0){
			B = BigInteger.valueOf(65536);
			return (((A.multiply(B)).mod(mod)).intValue() == 65536) ? 0 : (long) (((A.multiply(B)).mod(mod)).intValue());
		} else if(A.intValue() == 0 && B.intValue() == 0){
			return 1;
		} else if(((A.multiply(B)).mod(mod)).intValue() == 65536) return 0;
		  else return (long) (((A.multiply(B)).mod(mod)).intValue());

	}

	public static long[] encrypt(long[] block, long[][] subKeys){

		long[][] roundGamma = new long[10][4];
		for(int j = 0; j < 4; j++){
			roundGamma[0][j] = block[j];
		}
		
		for(int j = 1; j < 9; j++){

				long a = 0, b = 0, c = 0, d = 0, e = 0, f = 0;
 				
				a = SingleBlockCryptor.mul( roundGamma[j - 1][0], subKeys[j - 1][0]);
				b = (roundGamma[j - 1][1] + subKeys[j - 1][1]) % 65536;
				c = (roundGamma[j - 1][2] + subKeys[j - 1][2]) % 65536; 
				d = SingleBlockCryptor.mul( roundGamma[j - 1][3], subKeys[j - 1][3]);
				e = a ^ c;
				f = b ^ d;
				roundGamma[j][0] = a ^ SingleBlockCryptor.mul( (f + SingleBlockCryptor.mul(e, subKeys[j - 1][4]) ) % 65536, subKeys[j - 1][5]);
				roundGamma[j][1] = c ^ SingleBlockCryptor.mul( (f + SingleBlockCryptor.mul(e, subKeys[j - 1][4]) ) % 65536, subKeys[j - 1][5]);
				roundGamma[j][2] = b ^ ((SingleBlockCryptor.mul(e, subKeys[j - 1][4]) + SingleBlockCryptor.mul(((f + SingleBlockCryptor.mul(e, subKeys[j - 1][4])) % 65536), subKeys[j - 1][5]) ) % 65536);
				roundGamma[j][3] = d ^ ((SingleBlockCryptor.mul(e, subKeys[j - 1][4]) + SingleBlockCryptor.mul(((f + SingleBlockCryptor.mul(e, subKeys[j - 1][4])) % 65536), subKeys[j - 1][5]) ) % 65536);
			} 

			roundGamma[9][0] = SingleBlockCryptor.mul(roundGamma[8][0], subKeys[8][0]);
			roundGamma[9][1] = (roundGamma[8][2] + subKeys[8][1]) % 65536;
			roundGamma[9][2] = (roundGamma[8][1] + subKeys[8][2]) % 65536;
			roundGamma[9][3] = SingleBlockCryptor.mul(roundGamma[8][3], subKeys[8][3]);

			return roundGamma[9];
	}

	private static long[][] getDecryptionSubKeys(long[][] encryptionSubKeys){
		long[][] decryptionSubKeys = new long[9][6];
		byte k = 8;  // keys quantity
		long tempSubKey;
		for(int i = 0; i <= 8; i++){
			
			tempSubKey = encryptionSubKeys[k - i][0];
			BigInteger subKey = BigInteger.valueOf(encryptionSubKeys[k - i][0]);
			BigInteger mod = BigInteger.valueOf(65537);			
			for(int j = 1; j < 65537; j++){
					BigInteger temp = BigInteger.valueOf(j);

					BigInteger mul = subKey.multiply(temp);

					BigInteger res = mul.mod(mod);
					if(res.intValue() == 1){
						decryptionSubKeys[i][0] = temp.intValue();							
						break;
				}
			}

			if(i == 0 || i == 8){
				tempSubKey = encryptionSubKeys[k - i][1];
				decryptionSubKeys[i][1] = (65536 - tempSubKey);

				tempSubKey = encryptionSubKeys[k - i][2];
				decryptionSubKeys[i][2] = (65536 - tempSubKey);

			} else {
				tempSubKey = encryptionSubKeys[k - i][2];
				decryptionSubKeys[i][1] = (65536 - tempSubKey);

				tempSubKey = encryptionSubKeys[k - i][1];
				decryptionSubKeys[i][2] = (65536 - tempSubKey);
			}
			
			subKey = BigInteger.valueOf(encryptionSubKeys[k - i][3]);
			mod = BigInteger.valueOf(65537);			
			for(int j = 1; j < 65537; j++){
					BigInteger temp = BigInteger.valueOf(j);

					BigInteger mul = subKey.multiply(temp);

					BigInteger res = mul.mod(mod);
					if(res.intValue() == 1){
						decryptionSubKeys[i][3] = temp.intValue();							
						break;
				}
			}
			if(i != 8){

				decryptionSubKeys[i][4] = encryptionSubKeys[k - i - 1][4];
				decryptionSubKeys[i][5] = encryptionSubKeys[k - i - 1][5];

			} else break; 
		}
		return decryptionSubKeys;
	}

	public static long[] decrypt(long[] block, long[][] encryptionSubKeys){
		long[][] decryptionSubKeys = SingleBlockCryptor.getDecryptionSubKeys(encryptionSubKeys);
		return SingleBlockCryptor.encrypt(block, decryptionSubKeys);
	}
}