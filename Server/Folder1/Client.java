import java.awt.RenderingHints.Key;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

//This is code for Client side java
//1.Implement do while loop for options/
//Implemnt options one by one
//samajyo ne loda?

public class Client{
	public static Socket connectionSocket= null;
	public static DataOutputStream outToServer = null;
	public static BufferedReader inFromServer = null;
	public static InputStream in=null;
	//public static OutputStream outToServerByte = null;
	public static Scanner KeyboardInput = new Scanner(System.in);

	public static void main(String [] args){
		try {
			connectionSocket = new Socket("localhost",6789);
			outToServer = new DataOutputStream(connectionSocket.getOutputStream());
			inFromServer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			in = connectionSocket.getInputStream();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//DataOutputStream outToServer = null;
		int inputFromUser;
		do{
			System.out.println("Welcome to Dropbox\nPlease Enter Your Choice:\n1.Register a new Account\n2.Login using existing account\n3.Exit");
			inputFromUser = KeyboardInput.nextInt();
			switch(inputFromUser){
				case 1: regNewAccount();
					break;
				case 2: logIntoAccount();
					break;
				case 3:printExit();
				try {
					KeyboardInput.close();
					outToServer.close();
					//outToServerByte.close();
					inFromServer.close();
					connectionSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					break;
				default:System.out.println("Wrong Entry Try Again");
					break;
			}
		}while(inputFromUser!=3);
	}
	//For registering new Account Should ask user for information
	public static void regNewAccount(){
		int choice;
		boolean isTerminate = true;
		String Email="",Password="";
		try {
			outToServer.writeBytes("regNew()\n");
			outToServer.flush();
			System.out.println("New User Name:");
			Email = KeyboardInput.next();
			outToServer.writeBytes(Email+"\n");
			outToServer.flush();
			//System.out.println("Client flushed data");
			String bool=inFromServer.readLine();
			if(bool.equals("false")){
				System.out.println("Email Already Exist try with different email");
				return;
			}
			else{
				System.out.println("Password for "+Email+":");
				Password = KeyboardInput.next();
				outToServer.writeBytes(Password+"\n");
				outToServer.flush();
				//System.out.println("Client has sent password");
				String bool1=inFromServer.readLine();
				//System.out.println("bool is " +bool1);
				if(bool1.equals("true")){
					System.out.println("\n\nRegistration successfull...!!!\n\n");
					//isTerminate = false;
				}
				else{
					System.out.println("Something went wrong try again...");
					return;
				}
			}
			}catch(IOException e){
				e.printStackTrace();
			}
	}



	//For Log In to existing account should ask user for information

	public static void logIntoAccount(){
		int choice;
		boolean isTerminate = true;
		String Email="",Password="";
		try {
			outToServer.writeBytes("login()\n");
			outToServer.flush();
			System.out.println("Enter existing User Name:");
			Email = KeyboardInput.next();
			outToServer.writeBytes(Email+"\n");
			outToServer.flush();
			//System.out.println("Client flushed data");
			String bool=inFromServer.readLine();
			if(bool.equals("true")){
				System.out.println("Password for "+Email+":");
				Password = KeyboardInput.next();
				outToServer.writeBytes(Password+"\n");
				outToServer.flush();
				//System.out.println("Client has sent password");
				String bool1=inFromServer.readLine();
				//System.out.println("bool is " +bool1);
				if(bool1.equals("true")){
					System.out.println("\nLogin successfull...!!!\n\n\n");
					do{
						System.out.println("1.See Your File\n2.Upload file to Dropbox\n3.Download File from Dropbox\n4.Share File\n5.Delete File\n6.Logout or Exit");
						System.out.println("\nPlease enter your choice:\n");
						choice = KeyboardInput.nextInt();
						switch(choice){
							case 1:
								outToServer.writeBytes("seeFiles()\n");
								outToServer.flush();
								seeFiles(Email);
								break;
							case 2:
								System.out.println("In main function before upload");
								uploadFiles();
								System.out.println("Here in main function");
								break;
							case 3:downloadFile(Email);
								break;
							case 0: sync(Email);
								break;
							case 4:
								shareFile();
								break;
							case 5:
								deleteFile();
								break;
							case 6:
								logout();
								break;
						}
					}while(choice!=6);
						//isTerminate = false;
				}
				else{
					System.out.println("Username  Pass combination not working...");
					return;
				}
			}
			else{
				System.out.println("E-mail doesn't exist");
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	
	
	
	//for deleting file
	public static void deleteFile(){
		Scanner Input = new Scanner(System.in);
		try {
			outToServer.writeBytes("deleteFiles()\n");
			outToServer.flush();
			System.out.println("Enter Name of file you want to delete:");
			String fileName = Input.next();
			outToServer.writeBytes(fileName+"\n");
			outToServer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	//for syncFiles duplicate version
	//implementation and polishing
	/*public static void syncFiles(String dirName){
		System.out.println("Inside sync Files: ");
		String fName="";
		try{
			outToServer.writeBytes("sync()\n");
			outToServer.flush();
		}
	}*/
	//for sync
	//implementation and polishing
	//baki chhe haji 
	//ins kem nai work kartu???
	public static void sync(String dirName) throws IOException{
		System.out.println("Inside sync");
		String fName="";
		outToServer.writeBytes("sync()\n");
		outToServer.flush();
		ArrayList<String> fileNamesClientSide = new ArrayList<>();
		ArrayList<String> fileNamesServerSide = new ArrayList<>();
		File pathAtClient = new File("/home/shirish/workspace/CSCI561FinalProj/Client/".concat(dirName));
		//File path = new File(dirName);
		String temp ="";
		File [] files = pathAtClient.listFiles();
		int length = files.length;
		for(int i=0;i<length;i++){
			temp = files[i].getName();
			fileNamesClientSide.add(temp);
		}
		System.out.println("Files on client side:\n");
		for(int i=0;i<fileNamesClientSide.size();i++){
			System.out.println(i+". "+fileNamesClientSide.get(i));
		}
		String sloop = inFromServer.readLine();
		int loop = Integer.parseInt(sloop);
		System.out.println("Client received no of files: "+loop);
		while(loop>0){
			String temp1 = inFromServer.readLine();
			fileNamesServerSide.add(temp1);
			System.out.println("File name: "+temp1);
			if(fileNamesClientSide.contains(temp1)){
				outToServer.writeBytes("true\n");
				outToServer.flush();
			}
			else{
				outToServer.writeBytes("false\n");
				outToServer.flush();
				String fileLenString = inFromServer.readLine();
				//System.out.println("File len string: "+fileLenString);
				double fileLen = Double.parseDouble(fileLenString);
				//System.out.println("Length in String: " + fileLenString);
				System.out.println("Length in double: "+fileLen);
				String path1 = "/home/shirish/workspace/CSCI561FinalProj/Client/".concat(dirName);
				path1 = path1.concat("/");
				path1 = path1.concat(temp1);
				System.out.println(path1);
				File file = new File(path1);
				if (file.createNewFile()){
			        System.out.println("File is created!");
			      }else{
			        System.out.println("File already exists.");
			      }
				
				int count = 0;
				byte[] Bytes = new byte[1024];
				System.out.println("Checkpoint 1");
				FileOutputStream fileWriter = new FileOutputStream(file);
				System.out.println("CheckPoint 2");
			//	InputStream ins = connectionSocket.getInputStream();
				//String something = inFromServer.readLine();
				//System.out.println(something);
				String temp2 = "";
				FileWriter fw = new FileWriter(path1);
				while((temp2 = inFromServer.readLine())!=null){
					System.out.println("CheckPoint 3");
					fw.write(temp2);
					System.out.println(file.length());
					if(file.length() == fileLen){
						System.out.println(file.length());
						break;
					}
				}
				fw.close();
				/*while((count = in.read(Bytes))>=0 &&fileLen>0 ){
					fileWriter.write(Bytes,0,count);
					System.out.println("Count is: "+count);
					fileLen = fileLen - count;
					System.out.println("length is: " +length);
				}*/
				fileWriter.close();
			}
			loop--;
		}		
		
		System.out.println("\n\nFiles on Server Side:\n");
		for(int i =0;i<fileNamesServerSide.size();i++){
			System.out.println(i+". "+fileNamesServerSide.get(i));
		}
		
		System.out.println("Syc Completed\n\n\n");
	}
	
	public static void shareFile(){
		Scanner Input = new Scanner(System.in);
		System.out.println("Inside File sharing");
		try {
			outToServer.writeBytes("fileShare()\n");
			outToServer.flush();
			System.out.println("Enter Name of User with whom you want to share file:");
			String userName = Input.next();
			outToServer.writeBytes(userName+"\n");
			outToServer.flush();
			System.out.println("Enter File name that you want to share");
			String fileName = Input.next();
			outToServer.writeBytes(fileName+"\n");
			outToServer.flush();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	//Implementation baki chhe aanu Yaad rakhje loda
	//3times run thay pachhi banth thay jay atki jay bc
	public static void downloadFile(String dirName){
		Scanner Input = new Scanner(System.in);
		String fileName = null;
		byte[] Bytes = new byte[1000];
		try {
			outToServer.writeBytes("download()\n");
			outToServer.flush();
			System.out.println("Client send download()");
			//seeFiles();
			System.out.println("Client called see files");
			System.out.println("\n\n Enter file name you want to download: ");
			fileName = Input.next();
			System.out.println("You have entered "+fileName);
			outToServer.writeBytes(fileName.concat("\n"));
			outToServer.flush();
			System.out.println("Client sent file name to download");
			String bool = inFromServer.readLine();
			System.out.println("CLient received bool with "+bool);
			if(bool.equals("true")){
				System.out.println("in if");
				//String fileName = inFromClient.readLine();
				String path = "/home/shirish/workspace/CSCI561FinalProj/Client/".concat(dirName);
				path = path.concat("/");
				path = path.concat(fileName);
				File fout = new File(path);
				//File fout = new File(fileName);
				FileOutputStream fileWriter = new FileOutputStream(fout);
				int count =0;
				String slength = inFromServer.readLine();
				System.out.println("Client received file length");
				System.out.println("Length is: " +slength);
				double length = Double.parseDouble(slength);
				System.out.println("length in souble is: "+length);
//				count = in.read(Bytes);
//				System.out.println("Count: "+count);
//				count = in.read(Bytes);
//				System.out.println("Count: "+count);
//				
//				count = in.read(Bytes);
//				System.out.println("Count: "+count);
//				
//				count = in.read(Bytes);
//				System.out.println("Count: "+count);
//				
//				count = in.read(Bytes);
//				System.out.println("Count: "+count);
//				
//				count = in.read(Bytes);
//				System.out.println("Count: "+count);
//				
//				count = in.read(Bytes);
//				System.out.println("Count: "+count);
//				
//				count = in.read(Bytes);
//				System.out.println("Count: "+count);
//				
//				count = in.read(Bytes);
//				System.out.println("Count: "+count);
//				
//				count = in.read(Bytes);
//				System.out.println("Count: "+count);
//				
//				count = in.read(Bytes);
//				System.out.println("Count: "+count);
				//count = in.read(Bytes);
				if(length!=0){
					System.out.println("Inside if");
					int counter = 0;
					count = in.read(Bytes);
					while(count>=0 &&length>0 ){
						//System.out.println("Inside loop");
						counter++;
						System.out.println("Receiving : "+count);
						fileWriter.write(Bytes,0,count);
						//fileWriter.flush();
						System.out.println("Receiving : "+count);
						length = length - count;
						//System.out.println("length is: " +length);
						if(length==0){
							break;
						}
						else
							count = in.read(Bytes);
					}
				//	System.out.println("Counter is: "+counter);
					fileWriter.flush();
					fileWriter.close();
				}
				System.out.println("After loop");
			}
			else
			{
				System.out.println("else");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void seeFiles(String dirName){
		try{
			//System.out.println("calling see files");
			String slength = null;
			slength = inFromServer.readLine();
			//System.out.println("CLient received slength");
			//System.out.println("slength at client side is: "+slength);
			int length = Integer.parseInt(slength);
			//System.out.println("Length is: "+length);
			ArrayList<String> fileNames = new ArrayList<>();
			while(length>0){
				fileNames.add(inFromServer.readLine());
				//System.out.println("Client received file name");
				length--;
			}
			
			System.out.println("\n\nFiles on Server Side are: \n");
			for(int i =0;i<fileNames.size();i++){
				System.out.println(i+1+". "+fileNames.get(i));
			}
			/*System.out.println("____________________________________________________\n\nFiles on Client Side are: \n");
			ArrayList<String> fileNamesClientSide = new ArrayList<>();
			File pathAtClient = new File("/home/shirish/workspace/CSCI561FinalProj/Client/".concat(dirName));
			//File path = new File(dirName);
			String temp ="";
			File [] files = pathAtClient.listFiles();
			int length1 = files.length;
			for(int i=0;i<length1;i++){
				temp = files[i].getName();
				fileNamesClientSide.add(temp);
			}
			for(int i=0;i<fileNamesClientSide.size();i++){
				System.out.println(i+". "+fileNamesClientSide.get(i));
			}*/
			System.out.println("____________________________________________________\n\n");
		}catch(IOException e){
			e.printStackTrace();
			System.out.println("Something wrong happen while creating streams");
		}
	}

	//For uploading files
	public static void uploadFiles(){
		byte[] bytes = new byte[100];
		Scanner KeyboardInput = new Scanner(System.in);
		OutputStream outToServerByte = null;
		try{
			outToServerByte = connectionSocket.getOutputStream();
			outToServer.writeBytes("uploadFiles()\n");
			outToServer.flush();
			System.out.println("Server sent upload files files");
			System.out.println("Enter the name of file you want to upload");
			String fileName = KeyboardInput.nextLine();
			outToServer.writeBytes(fileName+"\n");
			outToServer.flush();
			System.out.println("Sending Data to Server...");
			int count = 0;
			File file = new File(fileName);
			double length = file.length();
			String slength = Double.toString(length);
			System.out.println("length is: "+slength);
			outToServer.writeBytes(slength+"\n");
			outToServer.flush();
			InputStream fis = new FileInputStream(file);
			while ((count = fis.read(bytes)) >= 0&& count!=-1) {
	    		System.out.println("Sending " + count);
	    		//String toServer = new String(bytes);
	    		//System.out.println("Sending "+toServer);
	    		//outToServer.writeBytes(toServer);
	    		outToServerByte.write(bytes, 0, count);
	    		outToServerByte.flush();
	    	}

			System.out.println("Out side loop");
			outToServer.flush();
			System.out.println("File sent returning to main function");
		}catch(IOException e){
			System.out.println("Something wrong happen while creating streams");
		}
	}
	//For Printing thanks message

	public static void printExit(){
		try {
			outToServer.writeBytes("exit\n");
			outToServer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Thanks for visiting Dropbox\n\n");
	}

	//for logging out
	public static void logout(){
		String response = null;
		try {
			outToServer.writeBytes("logout()\n");
			outToServer.flush();
			response = inFromServer.readLine();
			if(response.equals("true")){
				System.out.println("You are successfully logged out of your Account...\n\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
