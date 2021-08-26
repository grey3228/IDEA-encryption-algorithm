import java.io.*;
import java.util.ArrayList;

class PathCollector{ 	// returns list of addresses in certain directory recursively
	private ArrayList<String> paths = new ArrayList<String>();
	private final String OS;
	private final String delimiter; 
	private boolean isMainDirAdded = false;

	public PathCollector(){
		this.OS = System.getProperty("os.name");
		if(this.OS.contains("Windows")) this.delimiter = "\\";
		else this.delimiter = "/";
	}
	public void collectPaths(String dirName, String addition){
		if(!this.isMainDirAdded){
			this.paths.add(dirName);
			this.isMainDirAdded = true;
		}
		File dir = new File(dirName);
		String[] currentPaths = dir.list();
		for(String path : currentPaths){
			path = addition + delimiter + path;
			File temp = new File(path);
			if(!temp.isDirectory()){
				this.paths.add(path);
			} else {
				this.paths.add(path);
				this.collectPaths(path, path);

			}
		}
	}

	public ArrayList<String> getPaths(){
		return this.paths;
	}
}