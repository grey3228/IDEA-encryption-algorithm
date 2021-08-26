import java.io.*;
import java.util.ArrayList;
class OFBDirectoryCryptor{
	public static void encrypt(long[][] subKeys, String targetDir) throws IOException, FileNotFoundException,UnsupportedEncodingException{		
		PathCollector pc = new PathCollector();
		pc.collectPaths(targetDir, targetDir);
		ArrayList<String> paths = pc.getPaths();

		// generating name of file to write there directory structure
		int pointPos = -1;
		String outFile = targetDir;
		for(int i = outFile.length() - 1; i > -1; i--){
			if(outFile.codePointAt(i) == 46){
				pointPos = i;
				break;
			}
		}
		if(pointPos != -1) outFile = outFile.substring(0, pointPos) + "_encrypted" + outFile.substring(pointPos);
		else outFile += "_encrypted";

		File oF = new File(outFile);
		try{
			oF.createNewFile();
		} catch(SecurityException ex){
			System.out.println("Not enough permissions to create files here!");
			System.exit(1);
		}

		Packer p = new Packer();	
		p.pack(paths, outFile); 
		
    	FileReader reader = new FileReader();
		ArrayList<char[]> charBlocks = reader.readFile(outFile);

		int textLength = charBlocks.size();
		long[][] longBlocks = new long[textLength][4];		
		for(int i = 0; i < charBlocks.size(); i++){
			for(int j = 0; j < 4; j++){
				longBlocks[i][j] = (long) charBlocks.get(i)[j];
			}
		}
		// deleting all data in outFile to write there encrypted data then
		PrintWriter pw = new PrintWriter(outFile);
		pw.close();
		OFBFileCryptor.encryptBlocks(subKeys, longBlocks, outFile);  
	}
}