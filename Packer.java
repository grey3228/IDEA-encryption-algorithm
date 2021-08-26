import java.io.*;
import java.util.ArrayList;
class Packer{   	//makes packed directory, which include records about every file or directory inside   
	public void pack(ArrayList<String> paths, String outputFileName) throws UnsupportedEncodingException, IOException{
		
		File out = new File(outputFileName);
		RandomAccessFile raf = new RandomAccessFile(outputFileName, "rw");
		int dirIndicator = 55555;
		raf.writeInt(dirIndicator);

		for(int i = 0; i < paths.size(); i++){
			File path = new File(paths.get(i));
			String type;
			if(!path.isDirectory()){
				type = "file";
				int addrLen = paths.get(i).length(); 			
				String addr = paths.get(i); 
				long fileSize = path.length();
				raf.writeBytes(type);

				addrLen = (addr.getBytes("UTF-8")).length;
				raf.writeInt(addrLen);
				byte[] addrBytes = new byte[addrLen];
				addrBytes = addr.getBytes("UTF-8");
				for(int j = 0; j < addrLen; j++){
					raf.write(addrBytes[j]);
				}				

				raf.writeLong(fileSize);
								
				RandomAccessFile tempRaf = new RandomAccessFile(addr, "r");
				int[] content = new int[(int) fileSize];
				int byteIdx = 0;

				while(byteIdx < fileSize){

					content[byteIdx] = tempRaf.read();
					byteIdx++;
				}

				tempRaf.close();

				for(int j = 0; j < content.length; j++){
					raf.write(content[j]);
				}
				
			} else{
				type = "dir_";
				raf.writeBytes(type);

				String addr = paths.get(i); 
				int addrLen = (addr.getBytes("UTF-8")).length;

				raf.writeInt(addrLen);
				if(i == paths.size() - 1) System.out.println(paths.get(i) + " " + addrLen);
				byte[] addrBytes = new byte[addrLen];
				addrBytes = addr.getBytes("UTF-8");
				for(int j = 0; j < addrLen; j++){
					raf.write(addrBytes[j]);
				}
			} 			
		}
		raf.close();
	}
}