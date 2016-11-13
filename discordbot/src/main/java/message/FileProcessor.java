package message;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import de.btobastian.javacord.entities.message.MessageAttachment;
import window.Window;

public class FileProcessor {
	
	public FileProcessor(){
		System.setProperty("http.agent", "Chrome");
		System.out.println("Jar location: " + Window.getJarLocation());
	}

	public boolean addFile(String directoryName, String fileName, MessageAttachment messageAttachment){ //inputstream
		if(getFullFilename(directoryName, fileName) == null){
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
				new File(Window.getJarLocation() + "/img/" + directoryName).mkdir();
				//write file
				FileOutputStream fos = new FileOutputStream(Window.getJarLocation() + "/img/" + directoryName + "/" + fileName + "." + extension);
				fos.write(response);
				fos.close();
				return true;
			} catch (IOException e1) {
				//check if no files in directory
				String directory = getFilesInDirectory(directoryName);
				if(directory.equals(directoryName + "\n")){
					File directoryToDelete = new File(Window.getJarLocation() + "/img/" + directoryName);
					directoryToDelete.delete();
				}
				e1.printStackTrace();
			}		
		}
		return false;
	}
	
	public void removeFile(String directoryName, String fileName){
		directoryName = checkDirectory(directoryName);
		fileName = getFullFilename(directoryName, fileName);
		//create directory
		File fileToDelete = new File(Window.getJarLocation() + "/img/" + directoryName + "/" + fileName);
		fileToDelete.delete();
		
		//check if no files in directory
		String directory = getFilesInDirectory(directoryName);
		if(directory.equals(directoryName + "\n")){
			File directoryToDelete = new File(Window.getJarLocation() + "/img/" + directoryName);
			directoryToDelete.delete();
		}
	}
	
	public File getRandomFile(){
		//make list of all file locations in \img\
		ArrayList<File> imgList = new ArrayList<File>(); 
		 
		File[] dirList = new File(Window.getJarLocation() + "/img").listFiles();
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
		File directory = new File(Window.getJarLocation() + "/img/" + directoryName);
		File[] imgArray = new File(directory.getAbsolutePath()).listFiles();
		
		//pick a random file
		return imgArray[(int)(Math.random()*imgArray.length)];
	}
	
	public File getFile(String directoryName, String fileName){
		directoryName = checkDirectory(directoryName);
		fileName = getFullFilename(directoryName, fileName);
		directoryName = checkDirectory(directoryName);
		File requestedFile = new File(Window.getJarLocation() + "/img/" + directoryName + "/" + fileName);
		return requestedFile;	
	}
	
	public String getFilesInDirectory(String directoryName){
		directoryName = checkDirectory(directoryName);
		String list = directoryName + "\n";
		File directory = new File(Window.getJarLocation() + "/img/" + directoryName);
		File[] imgArray = new File(directory.getAbsolutePath()).listFiles();
		for(File img : imgArray){
			String imgName = img.getAbsolutePath().substring((Window.getJarLocation() + "/img/" + directoryName + "/").length());
			list += "    " + imgName + "\n";
		}
		return list;
	}
	
	public ArrayList<InputStream> getZippedFolders(String directoryName){
		directoryName = checkDirectory(directoryName);
		ArrayList<InputStream> zippedFolderList = new ArrayList<InputStream>();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try{
			ZipOutputStream zos = new ZipOutputStream(baos);
			File directory = new File(Window.getJarLocation() + "/img/" + directoryName);
			File[] imgArray = new File(directory.getAbsolutePath()).listFiles();
			int byteCounter = 0;
			for(File img : imgArray){
		        byte[] bFile = new byte[(int) img.length()];
		        //check if zip will exceed 10mb filesize limit, make new zip if it will
			    if(byteCounter + (int) img.length() >= 8388608){
			    	zos.close();
			    	zippedFolderList.add(new ByteArrayInputStream(baos.toByteArray()));
			    	baos = new ByteArrayOutputStream();
			    	zos = new ZipOutputStream(baos);
			    	byteCounter = 0;
		    	}
	            //convert file into array of bytes
			    FileInputStream fileInputStream = new FileInputStream(img);
			    fileInputStream.read(bFile);
			    fileInputStream.close();
			    zos.putNextEntry(new ZipEntry(img.getName()));
			    zos.write(bFile);
			    byteCounter += bFile.length;
			    zos.closeEntry();
			}
			zos.close();
			zippedFolderList.add(new ByteArrayInputStream(baos.toByteArray()));
		} catch(IOException e) {
			e.printStackTrace();
	    }
		return zippedFolderList;
	}
	
	public String getFileList(){
		String list = "";
		File[] directories = new File(Window.getJarLocation() + "/img").listFiles(File::isDirectory);
		for(File directory : directories){
			String directoryName = directory.getAbsolutePath().substring((Window.getJarLocation() + "/img/").length()); //relative path
			list += directoryName + "\n";
			File[] imgArray = new File(directory.getAbsolutePath()).listFiles();
			for(File img : imgArray){
				String imgName = img.getAbsolutePath().substring((Window.getJarLocation() + "/img/" + directoryName + "/").length());
				list += "    " + imgName + "\n";
			}
		}
		return list;
	}
	
	public String checkDirectory(String directoryName){
		return directoryName.replace("../", "").replace("/", "").replace("\\", "");
	}
	
	public String getFullFilename(String directoryName, String fileName){
		String fullFileName = null;
		
		File directory = new File(Window.getJarLocation() + "/img/" + directoryName);
		File[] files = null;
		if(directory.exists()){
			files = directory.listFiles();
		}else return null;
		
		for(File file : files){
			if(file.getName().substring(0, file.getName().lastIndexOf(".")).equals(fileName)){
				fullFileName = file.getName();
			}	
		}
		return fullFileName;
	}
}
