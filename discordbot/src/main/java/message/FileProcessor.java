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

import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.message.MessageAttachment;
import window.Window;

public class FileProcessor {
	String imgLocation = null;
	public FileProcessor(){
		imgLocation = Window.rainbotProperties.getProperty("imgLocation");
		if(imgLocation == null || imgLocation.isEmpty()){
			imgLocation = Window.getJarLocation();
		}
		imgLocation += "/img/";
		
		System.setProperty("http.agent", "Chrome");
		System.out.println("Jar location: " + Window.getJarLocation());
		System.out.println(imgLocation);
	}

	public boolean addFile(Server server, String directoryName, String fileName, MessageAttachment messageAttachment){ //inputstream
		if(getFullFilename(server, directoryName, fileName) == null){
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
				new File(imgLocation + server.getId() + "/" + directoryName).mkdir();
				//write file
				System.out.println(imgLocation + server.getId() + "/" + directoryName + "/" + fileName + "." + extension);
				FileOutputStream fos = new FileOutputStream(imgLocation + server.getId() + "/" + directoryName + "/" + fileName + "." + extension);
				fos.write(response);
				fos.close();
				return true;
			} catch (IOException e1) {
				//check if no files in directory
				String directory = getFilesInDirectory(server, directoryName);
				if(directory.equals(directoryName + "\n")){
					File directoryToDelete = new File(imgLocation + directoryName);
					directoryToDelete.delete();
				}
				e1.printStackTrace();
			}		
		}
		return false;
	}
	
	public void removeFile(Server server, String directoryName, String fileName){
		directoryName = checkDirectory(directoryName);
		fileName = getFullFilename(server, directoryName, fileName);
		//create directory
		File fileToDelete = new File(imgLocation + server.getId() + "/" + directoryName + "/" + fileName);
		fileToDelete.delete();
		
		//check if no files in directory
		String directory = getFilesInDirectory(server, directoryName);
		if(directory.equals(directoryName + "\n")){
			File directoryToDelete = new File(imgLocation + server.getId() + "/" + directoryName);
			directoryToDelete.delete();
		}
	}
	
	public File getRandomFile(Server server){
		//make list of all file locations in \img\
		ArrayList<File> imgList = new ArrayList<File>(); 
		 
		File[] dirList = new File(imgLocation + server.getId() + "/").listFiles();
		for(File directory : dirList){
			File[] tempImgList = new File(directory.getAbsolutePath()).listFiles();
			for(File img : tempImgList)
			imgList.add(img);
		}
		return imgList.get((int)(Math.random()*imgList.size()));
	}
	
	public File getRandomFile(Server server, String directoryName){
		directoryName = checkDirectory(directoryName);
		
		//make list of all file locations in directory
		File directory = new File(imgLocation + server.getId() + "/" + directoryName);
		File[] imgArray = new File(directory.getAbsolutePath()).listFiles();
		
		//pick a random file
		return imgArray[(int)(Math.random()*imgArray.length)];
	}
	
	public File getFile(Server server, String directoryName, String fileName){
		directoryName = checkDirectory(directoryName);
		fileName = getFullFilename(server, directoryName, fileName);
		directoryName = checkDirectory(directoryName);
		File requestedFile = new File(imgLocation + server.getId() + "/" + directoryName + "/" + fileName);
		return requestedFile;	
	}
	
	public String getFilesInDirectory(Server server, String directoryName){
		directoryName = checkDirectory(directoryName);
		String list = directoryName + "\n";
		File directory = new File(imgLocation + server.getId() + "/" + directoryName);
		directory.mkdirs();
		File[] imgArray = new File(directory.getAbsolutePath()).listFiles();
		for(File img : imgArray){
			String imgName = img.getAbsolutePath().substring((imgLocation + server.getId() + "/" + directoryName + "/").length());
			list += "    " + imgName + "\n";
		}
		return list;
	}
	
	public ArrayList<InputStream> getZippedFolders(Server server, String directoryName){
		directoryName = checkDirectory(directoryName);
		ArrayList<InputStream> zippedFolderList = new ArrayList<InputStream>();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try{
			ZipOutputStream zos = new ZipOutputStream(baos);
			File directory = new File(imgLocation + server.getId() + "/" + directoryName);
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
	
	public String getFileList(Server server){
		String list = "";
		File[] directories = new File(imgLocation + server.getId() + "/").listFiles(File::isDirectory);
		for(File directory : directories){
			String directoryName = directory.getAbsolutePath().substring((imgLocation + server.getId() + "/").length()); //relative path
			list += directoryName + "\n";
			System.out.println("d" + directory.getAbsolutePath());
			File[] imgArray = new File(directory.getAbsolutePath()).listFiles();
			for(File img : imgArray){
				System.out.println("a " + (imgLocation + server.getId() + "/" + directoryName + "/"));
				System.out.println("b " + img.getAbsolutePath());
				String imgName = img.getAbsolutePath().substring((imgLocation + server.getId() + "/" + directoryName + "/").length());
				list += "    " + imgName + "\n";
			}
		}
		return list;
	}
	
	public String checkDirectory(String directoryName){
		return directoryName.replace("../", "").replace("/", "").replace("\\", "");
	}
	
	public String getFullFilename(Server server, String directoryName, String fileName){
		String fullFileName = null;
		
		File directory = new File(imgLocation + server.getId() + "/" + directoryName);
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
