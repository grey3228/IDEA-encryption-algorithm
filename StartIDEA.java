import java.io.*;
class StartIDEA{
	public static void main(String[] args) throws UnsupportedEncodingException, IOException{
		if(args.length != 3){
			System.out.println("Arguments aren't given correctly. Use scheme: \nkey file_name flag");
			System.exit(1);
		}
		if(!args[2].equals("-e") && !args[2].equals("-d")){
			System.out.println("Flag / " + args[2] + " / isn't recognized. Use -e / -d");
			System.exit(1);			
		}
		File inputFile = new File(args[1]);
		if(!inputFile.exists()){
			System.out.println("File / " + args[1] + " / isn't exist");
			System.exit(1);
		}

		SubKeysGenerator generator = new SubKeysGenerator(); 
	    generator.setKey(args[0]);
	    long[][] keysArr = generator.getSubKeys();

		if(args[2].equals("-e")){
			if(!inputFile.isDirectory()) OFBFileCryptor.encrypt(keysArr, args[1]);
			else{
				OFBDirectoryCryptor.encrypt(keysArr, args[1]);
			}
		}
		
		else if(args[2].equals("-d")){
			String decryptedFile = OFBFileCryptor.decrypt(keysArr, args[1]);
			RandomAccessFile raf = new RandomAccessFile(decryptedFile, "rw");
			raf.seek(0);
			int possibleIndicator = raf.readInt();
			raf.close();		
			if(possibleIndicator == 55555){
				Unpacker u = new Unpacker();
				u.unpack(decryptedFile);
			}	
		}
	}
}