class SubKeysGenerator{		// generates 52 subkeys by given 128 bit input key
	private char[] key = new char[8];
	public void setKey(char[] key) {
		if(key.length != 8){
			System.out.println("Key /" + key + "/ doesn't strictly consists of 128 bits");
			System.exit(0);
		}
		this.key = key;
	}

	public void setKey(String key){
		if((key.toCharArray()).length != 8){
			System.out.println("Key /" + key + "/ doesn't strictly consist of 128 bits.");
			System.exit(0);
		}
		this.key = key.toCharArray();
	}

	public long[][] getSubKeys(){
		
		char[] tempSubs = this.key;
		char[] res = new char[52];
		for(int i = 0; i < 8; i++){
			res[i] = tempSubs[i];			
		}

		// we already have 8 starting keys, now let's produce other
		byte subKeyNum = 8;
		for(int j = 0; j < 5; j++){
			long[] ltempSubs = new long[8];
			for(int i = 0; i < 8; i++){
				ltempSubs[i] = (long) (tempSubs[i]);
			}

			long lHalf = 0;
			long rHalf = 0;
			lHalf = (ltempSubs[0] << 48) | (ltempSubs[1] << 32) | (ltempSubs[2] << 16) | ltempSubs[3]; 
			rHalf = (ltempSubs[4] << 48) | (ltempSubs[5] << 32) | (ltempSubs[6] << 16) | ltempSubs[7]; 


			long mask25 = 0xFFFFFF8000000000L;
			long bitsFromLeft = (lHalf & (mask25)) >>> 39;
			long bitsFromRight = (rHalf & (mask25)) >>> 39;

			lHalf = (lHalf << 25) | bitsFromRight;
			rHalf = (rHalf << 25) | bitsFromLeft;


			// now we need to decompose our key into chars
			long mask16 = 0xFFFF000000000000L;
			// lHalf:
			for(int i = 0; i < 4; i++){
				tempSubs[i] = (char) ((lHalf & mask16) >>> 48);
				lHalf = lHalf << 16;
				res[subKeyNum] = tempSubs[i]; 
				subKeyNum++;
			}

			//rHalf:
			for(int i = 4; i < 8; i++){
				tempSubs[i] = (char) ((rHalf & mask16) >>> 48);
				rHalf = rHalf << 16;
				res[subKeyNum] = tempSubs[i]; 
				subKeyNum++;
			}
		}
		
		// final 4 sub keys

		long[] ltempSubs = new long[8];
		for(int i = 0; i < 8; i++){
			ltempSubs[i] = (long) (tempSubs[i]);
		}

		long lHalf = 0;
		long rHalf = 0;
		lHalf = (ltempSubs[0] << 48) | (ltempSubs[1] << 32) | (ltempSubs[2] << 16) | ltempSubs[3]; 
		rHalf = (ltempSubs[4] << 48) | (ltempSubs[5] << 32) | (ltempSubs[6] << 16) | ltempSubs[7]; 


		long mask25 = 0xFFFFFF8000000000L;
		long bitsFromLeft = (lHalf & (mask25)) >>> 39;
		long bitsFromRight = (rHalf & (mask25)) >>> 39;

		lHalf = (lHalf << 25) | bitsFromRight;
		rHalf = (rHalf << 25) | bitsFromLeft;


		// now we need to decompose our key into chars
		long mask16 = 0xFFFF000000000000L;
		// lHalf:
		for(int i = 0; i < 4; i++){
			tempSubs[i] = (char) ((lHalf & mask16) >>> 48);
			lHalf = lHalf << 16;
			res[subKeyNum] = tempSubs[i]; 
			subKeyNum++;
		}

		/* transforming one-dimensional array into two-dimensional,
		which will consist of round keys(8 + 1) */

		char[][] keysArr = new char[9][6];
		byte resIdx = 0;
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 6; j++){
				keysArr[i][j] = res[resIdx];
				resIdx++;
				if(resIdx == 52) break;
			}
		}
		
		long[][] longKeysArr = new long[9][6];
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 6; j++){
				longKeysArr[i][j] = (long) keysArr[i][j];
			}
		}

		for(int i = 0; i < 4; i++){
			longKeysArr[8][i] = (long) keysArr[8][i];
		}
		return longKeysArr;
	}
}