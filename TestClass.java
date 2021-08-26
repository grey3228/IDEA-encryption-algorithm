class TestClass{		// tests correctness of algorithm in OFB mode
	private static long[] initV = {0xc121, 0xa1b0, 0x50d8, 0x286c};
	private static char[] key = {0x729a, 0x27ed, 0x8f5c, 0x3e8b, 0xaf16, 0x560d, 0x14c9, 0x0b43};
	private static long[] inputData = {0xd53f, 0xabbf, 0x94ff, 0x8b5f};
	private static long[] encryptionResult = {0xe423, 0x23ca, 0xf932, 0xb933};

	public static void main(String[] args) {
		SubKeysGenerator skg = new SubKeysGenerator();
		skg.setKey(key);
		long[][] subKeys = skg.getSubKeys();
		long[] gamma = SingleBlockCryptor.encrypt(initV, subKeys);
		for(int i = 0; i < 4; i++){
			gamma[i] = gamma[i] ^ inputData[i];
			if(gamma[i] != encryptionResult[i]){
				System.out.println("Checking is failed");
				System.exit(1);
			}
		}
		System.out.println("Checking is completed successfully");
	}
}