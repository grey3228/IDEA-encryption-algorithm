import java.io.*;
import java.util.ArrayList;

class FileReader{ 	// reads file and return 64 bit blocks
	private ArrayList<char[]> blocks = new ArrayList<char[]>();
	
	public ArrayList<char[]> readFile(String fileName) throws IOException, FileNotFoundException{
		RandomAccessFile raf = new RandomAccessFile(fileName, "r");
		while(true){
			char[] block = new char[4];
			long currPos = 0;
			for(int i = 0; i < 4; i++){
				char c;
				try{
					currPos = raf.getFilePointer();
					c = raf.readChar();
				} catch(EOFException ex1){		// situation when there is no more chars in file
					int lastChar = 0;
					int firstByteOfLastChar = 0;
					raf.seek(currPos);
					try{
						firstByteOfLastChar = raf.readByte();	/*  it's a situtation when 
						in the end of file stays half of char(byte) */
					} catch(EOFException ex2){			
						raf.close();
						this.blocks.add(block);	
						char[] lastBlockLenIndicator = new char[4];
						lastBlockLenIndicator[1] = (char) 55555;
						lastBlockLenIndicator[2] = (char) i;
						this.blocks.add(lastBlockLenIndicator);
						return this.blocks;	
					}
					// if we're here then we have only half char in last block
					lastChar = firstByteOfLastChar;
					block[i] = (char) (lastChar << 8);
					this.blocks.add(block);
					// adding indicator to the end : 55555 or d903
					char[] indicator = new char[4];
					indicator[0] = (char) 55555;
					indicator[3] = (char) i;  // order of char with only 1 byte 
					this.blocks.add(indicator);
					raf.close();
					return this.blocks;
				}
				block[i] = c;
			}
			this.blocks.add(block);

		}
	}

	public ArrayList<char[]> readCipherFile(String fileName) throws IOException, FileNotFoundException{
		RandomAccessFile raf = new RandomAccessFile(fileName, "r");	
		while(true){
			char[] block = new char[4];
			for(int i = 0; i < 4; i++){
				char c;
				try{
					c = raf.readChar();
				} catch(EOFException ex1){		// situation when there is no more chars in file
						raf.close();
						if(i > 0) this.blocks.add(block);
						return this.blocks;	
				}
				block[i] = c;
			}
			this.blocks.add(block);

		}
	}
}