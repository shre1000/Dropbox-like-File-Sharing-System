import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Hashtable;
import java.util.Scanner;


public class Server{

	public static void main(String [] args){
		int port = 6789;
		
		ServerSocket welcomeSocket=null;
		try {
			welcomeSocket = new ServerSocket(port);
			System.out.println("Server started on port: "+port);
		} catch (IOException e1) {
			System.out.println("Sorry can not run server on port "+port);
			e1.printStackTrace();
		}
		while(true){
			
			Socket ClientSocket=null;
			try {
				ClientSocket = welcomeSocket.accept();
				System.out.println("Connection with client established");
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
			Implementation request = new Implementation(ClientSocket);
			
			Thread thread = new Thread(request);
			
			thread.start();

		}

	}
}

final class Implementation implements Runnable{
	Socket ClientSocket;
	Hashtable<String,String> hashTable;
	BufferedReader inFromClient = null;
	DataOutputStream outToClient = null;
	InputStream inFromClientByte = null;
	OutputStream outToClientByte = null;

	
	public Implementation(Socket ClientSocket){
		this.ClientSocket = ClientSocket;
		hashTable = new Hashtable<String,String>();
		try{
			inFromClient = new BufferedReader(new InputStreamReader(ClientSocket.getInputStream()));
			outToClient = new DataOutputStream(ClientSocket.getOutputStream());
			inFromClientByte = ClientSocket.getInputStream();
			outToClientByte = ClientSocket.getOutputStream();
		}catch(Exception e){
			System.out.println("Something wrong happen creating stream...");
			return;
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{
			processRequest();
		}
		catch(Exception e){
			System.out.println(e);
		}
	}

	public void processRequest(){
		try {
			String input=null;
			while((input = inFromClient.readLine())!="exit"){
			if(input.equals("regNew()")){
				NewReg();
			}
			else if(input.equals("login()")){
				loginAcc();
			}
			}
			inFromClient.close();
			inFromClientByte.close();
			outToClient.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loginAcc(){
		hashTable.put("shre", "password");
		String value = null;
		String dirName = null;
		String logout = null;
		try{
			String receiveLine = inFromClient.readLine();
			dirName = receiveLine;
		
			if(!hashTable.contains(receiveLine)){
				outToClient.writeBytes("true\n");
				outToClient.flush();
				value = hashTable.get(receiveLine);
				
				String tempPass = inFromClient.readLine();
				if(tempPass.equals(value)){
					outToClient.writeBytes("true\n");
					outToClient.flush();
					System.out.println("\n\n----->User "+receiveLine+" logged in<--------\n\n");
					
					String optionForLoggedin = null;
					
					while(true){
						optionForLoggedin = inFromClient.readLine();
						
						if(optionForLoggedin.equals("seeFiles()")){
						
							showFiles(dirName);
						}
						else if(optionForLoggedin.equals("uploadFiles()")){
							String fileName = null;
							
							uploadFile(dirName);
						}
						else if(optionForLoggedin.equals("download()")){
							downloadFiles(dirName);
						}
						else if(optionForLoggedin.equals("sync()")){
							syncFiles(dirName);
						}
						else if(optionForLoggedin.equals("logout()")){
							
							outToClient.writeBytes("true\n");
							outToClient.flush();
							System.out.println("\n\n-------->"+dirName+" successfully logged out<--------\n\n");
							break;
						}
						else if(optionForLoggedin.equals("fileShare()")){
							fileShare(dirName);
						}
						else if(optionForLoggedin.equals("deleteFiles()")){
							deleteFiles(dirName);
						}
					}

				}
				else{
					outToClient.writeBytes("false\n");
					outToClient.flush();
					
				}
			}
			else{
				outToClient.writeBytes("false\n");
				outToClient.flush();
				System.out.println("Server responded for email not exist");
			}

		}
		catch(IOException e){
			System.out.println("IO Exception");
		}
	}
	
	

	public void deleteFiles(String dirName){
		String pathForCurrentUser = "/home/Project/Server/".concat(dirName);
		String fileName;
		
		try {
			fileName = inFromClient.readLine();
			System.out.println(fileName);
			File source = new File(pathForCurrentUser+"/"+fileName);
			Files.delete((source).toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void fileShare(String dirName){
		try {
			String newDir = inFromClient.readLine();
			System.out.println("New User Name is: "+newDir);
			String pathForCurrentUser = "/home/Project/Server/".concat(dirName);
			String pathForNewUser = "/home/Project/Server/".concat(newDir);
			System.out.println(pathForCurrentUser+"\n"+pathForNewUser);
			String fileName = inFromClient.readLine();
			System.out.println("file Name is: "+fileName);
			File source = new File(pathForCurrentUser+"/"+fileName);
			File dest = new File(pathForNewUser+"/"+fileName);
			dest.createNewFile();
			try {
			    Files.copy(source.toPath(),dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
			    e.printStackTrace();
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void syncFiles(String dirName){
	
		System.out.println("Inside syncFiles");
		File path = new File("/home/Project/Server/".concat(dirName));
	
		 String bool ="";
		    File [] files = path.listFiles();
		    int length = files.length;
		    String slength = Integer.toString(length);
		    System.out.println("slength is: "+slength);
		    try {
		    	System.out.println("Sent slength: "+slength+"\n");
				outToClient.writeBytes(slength.concat("\n"));
				outToClient.flush();
				System.out.println("Server sent no of files");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println("Opps Exception");
	
			}
		    for (int i = 0; i < files.length; i++){
		        if (files[i].isFile()){ 
		            try {
						outToClient.writeBytes(files[i].getName()+"\n");
						outToClient.flush();
						bool = inFromClient.readLine();
						
						if(bool.equals("true")){
							System.out.println("Exist");
						}
						else{
							System.out.println("Not Exist");
							String path1 = "/home/Project/Server/".concat(dirName);
							path1 = path1.concat("/");
							path1 = path1.concat(files[i].getName());
							System.out.println(path1);
							File fileToSend = new File(path1);
							double lengthOfFile = fileToSend.length();
							System.out.println("Lenght of File is: "+lengthOfFile);
							String lengthOfFileString = Double.toString(lengthOfFile);
							System.out.println("Length in String is: "+lengthOfFileString);
							outToClient.writeBytes(lengthOfFileString+"\n");
							outToClient.flush();
							FileInputStream fis = new FileInputStream(fileToSend);
							byte[] buffer = new byte[512];
							int bytes = 0;
							
							Scanner scan1 = new Scanner(new FileReader(path1));
							while(scan1.hasNext()){
								String temp = scan1.nextLine();
								outToClient.writeBytes(temp+"\n");
								outToClient.flush();
							}
							System.out.println("Outside Loop");
							
						}
					
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.println("Exception is happening here");
						
					}

		        }
		    }
		    System.out.println("Sync completed");

	}
	
	public void downloadFiles(String dirName){
		System.out.println("Inside download files");
		String fileName = "";
		byte[] bytes = new byte[1000];
	
		try {
			fileName = inFromClient.readLine();
			System.out.println("Server received filename");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Client entered this line: "+fileName);
		String path = "/home/Project/Server/".concat(dirName);
		path = path.concat("/");
		path = path.concat(fileName);
		File file = new File(path);
		boolean fileExist = file.exists();
		if(fileExist == true){
			try {
				outToClient.writeBytes("true\n");
				outToClient.flush();
				System.out.println("Server sent bol");
				double length = file.length();
				String slength = Double.toString(length);
				System.out.println("length is: "+slength);
				outToClient.writeBytes(slength+"\n");
				outToClient.flush();
				System.out.println("Server sent slength");
				int count;
				double length1 = length;
				FileInputStream fis = new FileInputStream(file);
				
				int counter = 0;
				while ((count = fis.read(bytes)) !=-1) {
		    		System.out.println("Sending " + count);
		    		outToClientByte.write(bytes, 0, count);
		    	
		    		length1 = length1 - count;
		    		counter++;
		    		if(length == 0)
		    			break;
		    	}
				outToClientByte.flush();
				System.out.println("Counter is: "+counter);
				fis.close();

				System.out.println("Out side loop");
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}else{
			try {
				outToClient.writeBytes("false\n");
				outToClient.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
	
	public void uploadFile(String dirName){
		System.out.println("inside upload Files");
		byte[] Bytes = new byte[100];
		
		try{
			String fileName = inFromClient.readLine();
			String path = "/home/Project/Server/".concat(dirName);
			path = path.concat("/");
			path = path.concat(fileName);
			File fout = new File(path);
			OutputStream fileWriter = new FileOutputStream(fout);
			int count =0;
			String slength = inFromClient.readLine();
			System.out.println("Length is: " +slength);
			double length = Double.parseDouble(slength);
			System.out.println("length in souble is: "+length);
			InputStream in = ClientSocket.getInputStream();
			while((count = in.read(Bytes))>=0&& count!=-1 && length>0 ){
				fileWriter.write(Bytes,0,count);
				System.out.println("receiving : "+count);
				length = length - count;
				if(length==0){
					break;
				}
				System.out.println("length is: " +length);
			}
			fileWriter.close();
			System.out.println("File successfully uploaded");
			System.out.println("Returning to while loop");
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Sorry something went wrong");
		}
	}
	
	public void showFiles(String dirName){
		
		 File path = new File("/home/Project/Server/".concat(dirName));
		
		 String fileList ="";
		    File [] files = path.listFiles();
		    int length = files.length;
		    String slength = Integer.toString(length);
		    
		    try {
		    	
				outToClient.writeBytes(slength.concat("\n"));
				outToClient.flush();
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println("Opps Exception");
				
			}
		    System.out.println("\n\n------->"+dirName+" requested to see Files<--------\n\n");
		    for (int i = 0; i < files.length; i++){
		        if (files[i].isFile()){ 
		            try {
						outToClient.writeBytes(files[i].getName()+"\n");
						outToClient.flush();
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.println("Exception is happening here");
						
					}

		        }
		    }
		    System.out.println("\n\n-------->Server showed Files to "+dirName+"<--------\n\n");
		   

	}

	public void NewReg(){
		Scanner scan = new Scanner(System.in);
		
		try{
			String receiveLine = inFromClient.readLine();
			
			if(hashTable.contains(receiveLine)){
				outToClient.writeBytes("false\n");
				outToClient.flush();
				
			}
			else{
				outToClient.writeBytes("true\n");
				outToClient.flush();
				
			}
			String receiveLine1="";
			receiveLine1 = inFromClient.readLine();
			hashTable.put(receiveLine, receiveLine1);
			outToClient.writeBytes("true\n");
			outToClient.flush();
			String path = "/home/Project/Server/";
			path = path.concat(receiveLine);
			File theDir = new File(path);
			if (!theDir.exists()) {
			    System.out.println("\n-------->creating directory: " + receiveLine+"<--------\n");
			    boolean result = false;

			    try{
			        theDir.mkdir();
			        result = true;
			    }
			    catch(SecurityException se){
			        System.out.println("Sorry something went wrong creating dir");
			    }
			    if(result) {
			        System.out.println("\n-------->DIR created<--------\n");
			    }
			}
			

			System.out.println("\n-------->Account Created for "+receiveLine+"<--------\n");
		}
		catch(IOException e){
			System.out.println("IO Exception");
		}
		
	}
}
