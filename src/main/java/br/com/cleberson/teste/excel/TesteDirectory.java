package br.com.cleberson.teste.excel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TesteDirectory {

	public static void main(String[] args) throws IOException, InterruptedException {
		
		listFilesAndFilesSubDirectories("D:\\Downloads");
		
	}
	
	public static void listFilesAndFilesSubDirectories(String directoryName) throws FileNotFoundException, IOException, InterruptedException{
        File directory = new File(directoryName);
        //get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList){
            if (file.isFile()){
            	Thread.sleep(1L);
                System.out.println(file.getAbsolutePath() + "; " + file.getParent().substring(file.getParent().lastIndexOf("\\") + 1));
                TesteExcel.lerExcel(file.getAbsolutePath());
            } else if (file.isDirectory()){
                listFilesAndFilesSubDirectories(file.getAbsolutePath());
            }
        }
    }
	
}
