import java.util.ArrayList;
import java.io.*;

abstract class OFBFileCryptor{	  	// encrypts/decrypts whole files 
	public static void encrypt(long[][] subKeys, String targetFile) throws IOException, FileNotFoundException{

		FileReader reader = new FileReader();
		ArrayList<char[]> charBlocks = reader.readFile(targetFile);

		int textLength = charBlocks.size();
		long[][] longBlocks = new long[textLength][4];		
		for(int i = 0; i < charBlocks.size(); i++){
			for(int j = 0; j < 4; j++){
				longBlocks[i][j] = (long) charBlocks.get(i)[j];
			}
		}

		int pointPos = -1;
		String outFile = targetFile;
		for(int i = outFile.length() - 1; i > -1; i--){
			if(outFile.codePointAt(i) == 46){
				pointPos = i;
				break;
			}
		}

		if(pointPos != -1) outFile = outFile.substring(0, pointPos) + "_encrypted" + outFile.substring(pointPos);
		else outFile += "_encrypted";

		long[] initVector = new long[4]; 
		initVector = InitVectorGenerator.getVector();
		ArrayList<char[]> gamma = new ArrayList<char[]>();
		long[] tempBlock = new long[4];
		for(int i = 0; i <= longBlocks.length; i++){
			long[] currentBlock = new long[4];

			if(i == 0 || i == textLength){ 
				for(int j = 0; j < 4; j++){
					currentBlock[j] = initVector[j];
				}
			}
			 else{
				for(int j = 0; j < 4; j++){
					currentBlock[j] = tempBlock[j];
				}
			}

			if(i != textLength){
				currentBlock = SingleBlockCryptor.encrypt(currentBlock, subKeys);
					for(int j = 0; j < 4; j++){
						tempBlock[j] = currentBlock[j];
					}
					for(int k = 0; k < 4; k++){
						currentBlock[k] = currentBlock[k] ^ longBlocks[i][k];
				}	
			}

			char[] gammaBlock = new char[4];
			for(int k = 0; k < 4; k++){
				gammaBlock[k] = (char) currentBlock[k];

			}

			gamma.add(gammaBlock);

		}

		DataOutputStream dos = new DataOutputStream(new FileOutputStream(outFile));
		for(int i = 0; i < gamma.size(); i++){
			for(int j = 0; j < 4; j++){
				dos.writeChar((int) gamma.get(i)[j]);		
			}
		}
		dos.close();
	}

	public static void encryptBlocks(long[][] subKeys, long[][] blocks, String outFile) throws IOException, FileNotFoundException{
				
		long[] initVector = new long[4]; 
		initVector = InitVectorGenerator.getVector();

		ArrayList<char[]> gamma = new ArrayList<char[]>();
		long[] tempBlock = new long[4];

		for(int i = 0; i <= blocks.length; i++){
			long[] currentBlock = new long[4];
			if(i == 0 || i == blocks.length){ 
				for(int j = 0; j < 4; j++){
					currentBlock[j] = initVector[j];
				}
			}
			 else{
				for(int j = 0; j < 4; j++){
					currentBlock[j] = tempBlock[j];
				}
			}

			if(i != blocks.length){
				currentBlock = SingleBlockCryptor.encrypt(currentBlock, subKeys);
				for(int j = 0; j < 4; j++){
					tempBlock[j] = currentBlock[j];
				}
				for(int k = 0; k < 4; k++){
					currentBlock[k] = currentBlock[k] ^ blocks[i][k];
				}	
			}

			char[] gammaBlock = new char[4];
			for(int k = 0; k < 4; k++){
				gammaBlock[k] = (char) currentBlock[k];

			}
			gamma.add(gammaBlock);
		}

		DataOutputStream dos = new DataOutputStream(new FileOutputStream(outFile));
		for(int i = 0; i < gamma.size(); i++){
			for(int j = 0; j < 4; j++){
				dos.writeChar((int) gamma.get(i)[j]);		
			}
		}
		dos.close();
	}

	public static String decrypt(long[][] subKeys, String targetFile) throws IOException, FileNotFoundException{
		
		ArrayList<char[]> openText = new ArrayList<char[]>();

		FileReader reader = new FileReader();
		ArrayList<char[]> charBlocks = reader.readCipherFile(targetFile);
		int textLength = charBlocks.size();

		String outFile = targetFile;
		if(outFile.contains("_encrypted")){
			outFile = outFile.substring(0, outFile.lastIndexOf("_encrypted")) + "_decrypted" + outFile.substring(outFile.lastIndexOf("_encrypted") + 10);
		} else outFile += "_decrypted";


		long[][] longBlocks = new long[textLength][4];
		ArrayList<char[]> gamma = new ArrayList<char[]>();

		for(int i = 0; i < charBlocks.size(); i++){
			for(int j = 0; j < 4; j++){
				longBlocks[i][j] = (long) charBlocks.get(i)[j];
			}
		}

		// firstly, we need to find initialization vector

		long[] initV = new long[4];
		long[] currentBlock = new long[4];						

		for(int j = 0; j < 4; j++){
			currentBlock[j] = longBlocks[longBlocks.length - 1][j]; 
		}

		initV = currentBlock;  

		long[] tempBlock = new long[4];
		for(int i = 0; i < textLength - 1; i++){
			if(i == 0){
				for(int j = 0; j < 4; j++){
					currentBlock[j] = initV[j];
				}
			}
			else{
				for(int j = 0; j < 4; j++){
					currentBlock[j] = tempBlock[j];
				}	
			}
			currentBlock = SingleBlockCryptor.encrypt(currentBlock, subKeys);
			for(int j = 0; j < 4; j++){
				tempBlock[j] = currentBlock[j];
			}
			for(int k = 0; k < 4; k++){
				currentBlock[k] = currentBlock[k] ^ longBlocks[i][k];
			}
			char[] textBlock = new char[4];
			for(int k = 0; k < 4; k++){
				textBlock[k] = (char) currentBlock[k];
		
			}
			openText.add(textBlock);
		}
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(outFile));
		// checking indicator:

		if( ((int) (openText.get(openText.size() - 1)[0]) == 55555) && 
			((int) (openText.get(openText.size() - 1)[1]) == 0) && 
			((int) (openText.get(openText.size() - 1)[2]) == 0)){

			for(int i = 0; i < openText.size() - 1; i++){
				if(i == (openText.size() - 2)){  // last char with only 1 byte
					for(int j = 0; j < 4; j++){
						if( j == ((int) openText.get(openText.size() - 1)[3])) { 
							dos.write((int) (openText.get(i)[j]) >>> 8);
							break;
						}
						else dos.writeChar((int) openText.get(i)[j]);
					}

				}
				else {
					for(int j = 0; j < 4; j++){
						dos.writeChar((int) openText.get(i)[j]);

					}						
				}
			}
		} else if( ((int) (openText.get(openText.size() - 1)[0]) == 0) && 
			((int) (openText.get(openText.size() - 1)[1]) == 55555) &&  
			((int) (openText.get(openText.size() - 1)[3]) == 0) ) {

			for(int i = 0; i < openText.size() - 1; i++){

				if(i != openText.size() - 2){
		
					for(int j = 0; j < 4; j++){
						dos.writeChar((int) openText.get(i)[j]);
					}	

				} 
				else{
					for(int k = 0; k < ((int) openText.get(openText.size() - 1)[2]); k++){
						dos.writeChar((int) openText.get(i)[k]);
					}
				} 
			}	
		}
		
		dos.close();
		return outFile;
	}
}