import java.io.*;
import java.util.Random;
class Unpacker{ 	/* restores from output file the whole directory structure. 
					We use it after decrypting output file */

	private final String OS;
	private final String delimiter; 
	private boolean _decrypted = false;  /* indicates if we will change "someDir" to "someDir__decrypted"
										  in all paths */
	
	public Unpacker(){
		this.OS = System.getProperty("os.name");
		if(this.OS.contains("Windows")) this.delimiter = "\\";
		else this.delimiter = "/";
	}

	public void unpack(String packed) throws FileNotFoundException, IOException{
		RandomAccessFile raf = new RandomAccessFile(packed, "rw");
		// reading first record about root directory

		raf.seek(4); 	// such indent because of dir indicator
		String receivedType = "";
		for(int i = 0; i < 4; i++){
			receivedType += (char) raf.read();
		}

		int addrLenReceived = raf.readInt();
	
		byte[] addrBytes = new byte[addrLenReceived];

		for(int i = 0; i < addrLenReceived; i++){
			addrBytes[i] = raf.readByte();
		}

		String receivedAddr = new String(addrBytes, "UTF-8");

		File baseDir = new File(receivedAddr);
		String baseName = receivedAddr; 
		if(baseDir.exists()){
			receivedAddr += "__decrypted";
			
			baseDir = new File(receivedAddr);
			this._decrypted = true;
		}

		// creating a base(root) directory:

		try{
			boolean flag = baseDir.mkdir();

		} catch(SecurityException e){
			System.out.println("Named dirs cannot be created here(Not enough permissions)!");
			System.exit(1); 
		}

		// reading records in packed dir
		while(raf.getFilePointer() < (new File(packed).length())){
			receivedType = "";
			for(int i = 0; i < 4; i++){
				receivedType += (char) raf.read();
			}
		
			addrLenReceived = raf.readInt();
		
			addrBytes = new byte[addrLenReceived];
			for(int i = 0; i < addrLenReceived; i++){
				addrBytes[i] = raf.readByte();
			}
			receivedAddr = new String(addrBytes, "UTF-8"); 

			// changing '/' to '\' if necessary
			
			if(!receivedAddr.contains(this.delimiter)){
				if(this.delimiter.equals("\\")){
					receivedAddr.replace("/", this.delimiter);
				} else receivedAddr.replace("\\", this.delimiter);
			}

			// changing "someDir" to "someDir__decrypted" in addr
			if(this._decrypted){
				receivedAddr = baseName + "__decrypted" + receivedAddr.substring(baseName.length());
			}

			File f = new File(receivedAddr);

			if(receivedType.equals("dir_")){
				try{
				boolean flag = f.mkdir();
				} catch(SecurityException e){
					System.out.println("Named dirs cannot be created here(Not enough permissions)!");
					System.exit(1); 
				}
			} else{
				boolean flag = f.createNewFile();
				RandomAccessFile raf1 = new RandomAccessFile(f, "rw");
				long receivedFSize = raf.readLong();
	
				int[] receivedContent = new int[(int) receivedFSize];
				int byteIdx = 0;
				while(byteIdx < receivedFSize){

					receivedContent[byteIdx] = raf.read();
					byteIdx++;
				}

				for(int j = 0; j < receivedContent.length; j++){
					raf1.write(receivedContent[j]);
				}
				raf1.close();	
			}
		}

		// then we write trash into file with our "file system" and delete it 

		raf.seek(0);
		File p = new File(packed);					
		byte[] trash = new byte[(int) p.length()];
		Random random = new Random();
		random.nextBytes(trash);
		raf.write(trash);
		raf.close();
		p.delete();
	}
}
		