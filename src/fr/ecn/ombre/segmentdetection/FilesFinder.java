package fr.ecn.ombre.segmentdetection;
/**
 * 
 */

/**
 * 
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Vector;

public class FilesFinder {

	public FilesFinder() {
		super();
	}
	
	public static Vector<String> findFiles(String directoryPath) throws FileNotFoundException{
		Vector<String> list = new Vector<String>();
		File directory = new File(directoryPath);
		if(!directory.exists()){
			throw new FileNotFoundException();
		}else if(!directory.isDirectory()){
			list.add(directoryPath);
		}else{
			File[] subfiles = directory.listFiles();
			String message = "Le r�pertoire '"+directoryPath+"' contient "+ subfiles.length+" fichier"+(subfiles.length>1?"s":"");
			System.out.println(message);
			for(int i=0 ; i<subfiles.length; i++){
				list.add(subfiles[i].getName());
			}
		}
		return list;
	}

}
