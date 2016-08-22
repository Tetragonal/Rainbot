package message;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import de.btobastian.javacord.entities.message.MessageAttachment;

public class FileProcessor {
	
	public FileProcessor(){
		System.setProperty("http.agent", "Chrome");
	}

	public boolean addFile(String directoryName, String fileName, MessageAttachment messageAttachment){ //inputstream
		if(getFileWithName(directoryName, fileName) == null){
			directoryName = checkDirectory(directoryName);
			InputStream in;
			try {
				//transfer file
				in = new BufferedInputStream(messageAttachment.getUrl().openStream());
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buf = new byte[1024];
				int n = 0;
				while (-1!=(n=in.read(buf)))
				{
				   out.write(buf, 0, n);
				}
				out.close();
				in.close();
				byte[] response = out.toByteArray();	
				//get file extension
				String extension = "";
				int i = messageAttachment.getFileName().lastIndexOf('.');
				if (i > 0) {
				    extension = messageAttachment.getFileName().substring(i+1);
				}
				//create directory
				new File("img/" + directoryName).mkdir();
				//write file
				FileOutputStream fos = new FileOutputStream("img/" + directoryName + "/" + fileName + "." + extension);
				fos.write(response);
				fos.close();
				return true;
			} catch (IOException e1) {
				//check if no files in directory
				String directory = getFilesInDirectory(directoryName);
				if(directory.equals(directoryName + "\n")){
					File directoryToDelete = new File("img/" + directoryName);
					directoryToDelete.delete();
				}
				e1.printStackTrace();
			}		
		}
		return false;
	}
	
	public void removeFile(String directoryName, String fileName){
		directoryName = checkDirectory(directoryName);
		fileName = getFileWithName(directoryName, fileName);
		//create directory
		File fileToDelete = new File("img/" + directoryName + "/" + fileName);
		fileToDelete.delete();
		
		//check if no files in directory
		String directory = getFilesInDirectory(directoryName);
		if(directory.equals(directoryName + "\n")){
			File directoryToDelete = new File("img/" + directoryName);
			directoryToDelete.delete();
		}
	}
	
	public File getRandomFile(){
		//make list of all file locations in \img\
		String localPath = null;
		 try {
			 localPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
		ArrayList<File> imgList = new ArrayList<File>(); 
		 
		File[] dirList = new File(localPath + "/img").listFiles();
		for(File directory : dirList){
			File[] tempImgList = new File(directory.getAbsolutePath()).listFiles();
			for(File img : tempImgList)
			imgList.add(img);
		}
		return imgList.get((int)(Math.random()*imgList.size()));
	}
	
	public File getRandomFile(String directoryName){
		directoryName = checkDirectory(directoryName);
		
		//make list of all file locations in directory
		String localPath = null;
		 try {
			 localPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		File directory = new File(localPath + "/img/" + directoryName);
		File[] imgArray = new File(directory.getAbsolutePath()).listFiles();
		
		//pick a random file
		return imgArray[(int)(Math.random()*imgArray.length)];
	}
	
	public File getFile(String directoryName, String fileName){
		directoryName = checkDirectory(directoryName);
		fileName = getFileWithName(directoryName, fileName);
		directoryName = checkDirectory(directoryName);
		String localPath = null;
		 try {
			 localPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		File requestedFile = new File(localPath + "/img/" + directoryName + "/" + fileName);
		return requestedFile;	
	}
	
	public String getFilesInDirectory(String directoryName){
		directoryName = checkDirectory(directoryName);
		String list = directoryName + "\n";
		String localPath = null;
		 try {
			 localPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		File directory = new File(localPath + "/img/" + directoryName);
		File[] imgArray = new File(directory.getAbsolutePath()).listFiles();
		for(File img : imgArray){
			String imgName = img.getAbsolutePath().substring((localPath + "/img/" + directoryName + "/").length());
			list += "    " + imgName + "\n";
		}
		return list;
	}
	
	public String getFileList(){
		String list = "";
		String localPath = null;
		 try {
			 localPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		File[] directories = new File(localPath + "/img").listFiles(File::isDirectory);
		for(File directory : directories){
			String directoryName = directory.getAbsolutePath().substring((localPath + "/img/").length()); //relative path
			list += directoryName + "\n";
			File[] imgArray = new File(directory.getAbsolutePath()).listFiles();
			for(File img : imgArray){
				String imgName = img.getAbsolutePath().substring((localPath + "/img/" + directoryName + "/").length());
				list += "    " + imgName + "\n";
			}
		}
		return list;
	}
	
	public String checkDirectory(String directoryName){
		return directoryName.replace("../", "").replace("/", "").replace("\\", "");
	}
	
	public String getFileWithName(String directoryName, String fileName){
		String localPath = null;
		String fullFileName = null;
		 try {
			 localPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		 
		File directory = new File(localPath + "/img/" + directoryName);
		File[] files = null;
		if(directory.exists()){
			files = directory.listFiles();
		}else return null;
		
		for(File file : files){
			if(file.getName().substring(0, file.getName().lastIndexOf(".")).equals(fileName)){
				String extension = "";
				int i = file.getName().lastIndexOf('.');
				if (i >= 0) {
				    extension = file.getName().substring(i+1);
				}
				fullFileName = fileName + "." + extension;
			}	
		}
		return fullFileName;
	}
	
}
