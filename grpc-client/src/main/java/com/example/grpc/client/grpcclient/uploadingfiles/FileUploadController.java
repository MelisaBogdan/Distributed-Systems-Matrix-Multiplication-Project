package com.example.uploadingfiles;

import java.io.IOException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.uploadingfiles.storage.StorageFileNotFoundException;
import com.example.uploadingfiles.storage.StorageService;

import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File; 
import java.io.IOException;
import java.util.*;
import java.util.ArrayList;
import java.text.DecimalFormat;
import java.util.Random;

// channel imports
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.example.grpc.server.grpcserver.MatrixRequest;
import com.example.grpc.server.grpcserver.MatrixReply;
import com.example.grpc.server.grpcserver.MatrixServiceGrpc;



@Controller
public class FileUploadController {

	private final StorageService storageService;
	private File destination;
	public int[][] matrix1;
	public int[][] matrix2;

        @Value("${matrix.symbols}")
        private String matrixS;
	
	@Value("${com.CW-DS.grpc.client.serverAddress}")
	private String serverAddress;

	@Autowired
	public FileUploadController(StorageService storageService) {
		this.storageService = storageService;
	}

	@GetMapping("/")
	public String listUploadedFiles(Model model) throws IOException {

		model.addAttribute("files", storageService.loadAll().map(
				path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
						"serveFile", path.getFileName().toString()).build().toUri().toString())
				.collect(Collectors.toList()));
		
// 		return html form
		return "uploadForm";
	}

	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

		Resource file = storageService.loadAsResource(filename);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + file.getFilename() + "\"").body(file);
	}
	

	

	@RequestMapping(value="/matrixMultip", params="multiply", method=RequestMethod.POST)
	public String handleFileUpload2(RedirectAttributes redirectAttributes) {
			System.out.println("pressed matrix multiplication button");
			redirectAttributes.addFlashAttribute("message", "Here is your multiplication: ");   
		return "redirect:/";
	}
	
	
	@PostMapping("/")
	public String handleFileUpload(@RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) throws Exception{
	
// 		try{
			String file_path = "/home/melisa_bogdan/CW-DS";
			destination = new File(file_path + '/' + file.getOriginalFilename());
			try { 
				file.transferTo(destination); 
			} catch (Exception e) { 
				redirectAttributes.addFlashAttribute("message",
						"File is not provided, please add a file!! " + file.getOriginalFilename() + "!!");

			}
			// CHECK IF FILE EMPTY
			if (file.isEmpty()) {
				 redirectAttributes.addFlashAttribute("message",
					"File " + file.getOriginalFilename() + " is empty! Upload again. ");
			}else {

				String matrixOne= get_string_matrix(destination).split(matrixS)[0];
				String matrixTwo = get_string_matrix(destination).split(matrixS)[1];

				matrix1 = matrix_conversion(matrixOne);
				matrix2 = matrix_conversion(matrixTwo);

				// CHECK IF SQUARE
				if(matrix1.length != matrix1[0].length || matrix2.length != matrix2[0].length){
					redirectAttributes.addFlashAttribute("message",
					"Matrices in file " + file.getOriginalFilename() + " are not square!! ");
				// CHECK IF POWER OF 2
				}else if (checkIfPower2(matrix2.length)== false || checkIfPower2(matrix1.length)== false){
					redirectAttributes.addFlashAttribute("message",
					"Matrices in file " + file.getOriginalFilename() + " are not power of 2!! ");
				}else{
				// All clear we can do multiplication now
	
					redirectAttributes.addFlashAttribute("message",
							"You successfully uploaded " +" "+ file.getOriginalFilename() +" !!");
					redirectAttributes.addFlashAttribute("matrix1",
							"Matrix 1 from file is: " +" "+ matrixOne);
					redirectAttributes.addFlashAttribute("matrix2",
							"Matrix 2 from file is: " +" "+ matrixTwo);
				grpcClient(matrix1, matrix2, redirectAttributes);
				}

			}
// 		} catch (Exception e) { 
// 			throw new Exception("Matrices cannot be multiplied. Incompatible sizes! ");
// 		}
			
		return "redirect:/";
	}
	
	// function for checking if matrix length is power of 2
	public Boolean checkIfPower2(int n){
		while(n!=1)
		{
			n = n/2;
            		if(n%2 != 0 && n != 1){
				System.out.println("not power of 2");
				return false;
			}
		}
		return true;
	}
	
	public void grpcClient(int[][]a, int[][]b, RedirectAttributes redirectAttributes){

		// Deadline based scaling
		
		// initiate channels
                ManagedChannel channel1 = ManagedChannelBuilder.forAddress(serverAddress,  9090).usePlaintext().build();  
                ManagedChannel channel2 = ManagedChannelBuilder.forAddress(serverAddress,  9090).usePlaintext().build();  
                ManagedChannel channel3 = ManagedChannelBuilder.forAddress(serverAddress,  9090).usePlaintext().build();  
                ManagedChannel channel4 = ManagedChannelBuilder.forAddress(serverAddress,  9090).usePlaintext().build();  
                ManagedChannel channel5 = ManagedChannelBuilder.forAddress(serverAddress,  9090).usePlaintext().build();  
                ManagedChannel channel6 = ManagedChannelBuilder.forAddress(serverAddress,  9090).usePlaintext().build();  
                ManagedChannel channel7 = ManagedChannelBuilder.forAddress(serverAddress,  9090).usePlaintext().build();  
                ManagedChannel channel8 = ManagedChannelBuilder.forAddress(serverAddress,  9090).usePlaintext().build(); 
		
		MatrixServiceGrpc.MatrixServiceBlockingStub stub1 = MatrixServiceGrpc.newBlockingStub(channel1);
                MatrixServiceGrpc.MatrixServiceBlockingStub stub2 = MatrixServiceGrpc.newBlockingStub(channel2);
                MatrixServiceGrpc.MatrixServiceBlockingStub stub3 = MatrixServiceGrpc.newBlockingStub(channel3);
                MatrixServiceGrpc.MatrixServiceBlockingStub stub4 = MatrixServiceGrpc.newBlockingStub(channel4);
                MatrixServiceGrpc.MatrixServiceBlockingStub stub5 = MatrixServiceGrpc.newBlockingStub(channel5);
                MatrixServiceGrpc.MatrixServiceBlockingStub stub6 = MatrixServiceGrpc.newBlockingStub(channel6);
                MatrixServiceGrpc.MatrixServiceBlockingStub stub7 = MatrixServiceGrpc.newBlockingStub(channel7);
                MatrixServiceGrpc.MatrixServiceBlockingStub stub8 = MatrixServiceGrpc.newBlockingStub(channel8);
		
		ArrayList<MatrixServiceGrpc.MatrixServiceBlockingStub> stubss = new ArrayList<MatrixServiceGrpc.MatrixServiceBlockingStub>();
                stubss.add(stub1);
                stubss.add(stub2);
                stubss.add(stub3);
                stubss.add(stub4);
                stubss.add(stub5);
                stubss.add(stub6);
                stubss.add(stub7);
                stubss.add(stub8);

                // Counts stubs into the matrix calculation
                int stubs_index = 0;

                // Length row
                int N = a.length;
		int deadline=10;
		
                // use a random stub from the stub array to calculate footprint 
                DecimalFormat df = new DecimalFormat("#.##"); 
                Random r = new Random();
                int low = 0;
                int high = 8;
                int random = r.nextInt(high-low) + low;
                double footprint = Double.valueOf(df.format(footPrint(stubss.get(random), a[0][0], a[N-1][N-1])));
                
                // Get execution time and number of needed servers
                int number_of_calls = (int) Math.pow(N, 2);
                double execution_time = number_of_calls*footprint;
                double number_of_server_needed = execution_time/deadline;


                // if less than one server needed provide one server
                if (number_of_server_needed < 1.00 ) number_of_server_needed = 1.00;
                // if more than one but less than 2 server needed use 2 servers
                if(number_of_server_needed <2.00 && number_of_server_needed > 1.00) number_of_server_needed = 2.00;
                
                System.out.println("Estimated number of servers: " + number_of_server_needed);
               
            
                
                

                if((number_of_server_needed > 8) ){
                        // If more than 8 servers needed to the operation to 8 servers and if the deadline is unrealistick provide 
                        // appropriate mesage and quit 
                        number_of_server_needed = 8;
                        if(deadline <= 50){
                                System.out.println("Footprint: " + footprint + "\nFootprint x number of calls: " + (footprint*number_of_calls));
                                System.out.println("The load exceeds the deadline, multiplication cannot be done!");
                                return;
                        }
                }

                int number_of_servers_in_use = (int) Math.round(number_of_server_needed);
                System.out.println("Number of used servers: " + number_of_servers_in_use);
		 System.out.println("Footprint is: " + footprint + " seconds");
                
                int c[][] = new int[N][N];

                // Start the matrix calculation and print the result onto client 
                for (int i = 0; i < N; i++) { // row
                        for (int j = 0; j < N; j++) { // col
                            for (int k = 0; k < N; k++) {
                                
                                MatrixReply temp=stubss.get(stubs_index).multiplyBlock(MatrixRequest.newBuilder().setA00(a[i][k]).setB00(b[k][j]).build());
                                if(stubs_index == number_of_servers_in_use-1) stubs_index = 0;
                                else stubs_index++;
                                MatrixReply temp2=stubss.get(stubs_index).addBlock(MatrixRequest.newBuilder().setA00(c[i][j]).setB00(temp.getC00()).build());
                                c[i][j] = temp2.getC00();
                                if(stubs_index == number_of_servers_in_use-1) stubs_index = 0;
                                else stubs_index++;
                            }
                        }
                    }

//                     // Print result matrix
//                     for (int i = 0; i < a.length; i++) {
//                         for (int j = 0; j < a[0].length; j++) {
//                             System.out.print(c[i][j] + " ");
//                         }
//                         System.out.println("");
//                     }
		
		String s="[  ";
		for (int i = 0; i < a.length; i++) {
                        for (int j = 0; j < a[0].length; j++) {
                            System.out.print(c[i][j] + " ");
			    s= s+ " "+ c[i][j];
                        }
                        System.out.println("");
			s=s+ "     ,     " ;
                    }
		s=s+ "  ]" ;
		
		redirectAttributes.addFlashAttribute("resultMult","Multiplication result is:" +" "+ s +" !!");
                // Close channels
                channel1.shutdown();
                channel2.shutdown();
                channel3.shutdown();
                channel4.shutdown();
                channel5.shutdown();
                channel6.shutdown();
                channel7.shutdown();
                channel8.shutdown();
                
		
// 		ArrayList<MatrixServiceGrpc.MatrixServiceBlockingStub> stubss = new ArrayList<MatrixServiceGrpc.MatrixServiceBlockingStub>();
//                 stubss.add(stub1);
//                 stubss.add(stub2);
//                 stubss.add(stub3);
//                 stubss.add(stub4);
//                 stubss.add(stub5);
//                 stubss.add(stub6);
//                 stubss.add(stub7);
//                 stubss.add(stub8);
		
// 		int stubs_index = 0;
// 		int deadline=10;

// //                 // Length row
//                 int l = A.length;

// //                 // use a random stub from the stub array to calculate footprint 
//                 DecimalFormat df = new DecimalFormat("#.##"); 
//                 Random r = new Random();
//                 int low = 0;
//                 int high = 8;
//                 int random = r.nextInt(high-low) + low;
		
// 		// calculate the footprint
//                 double footprint = Double.valueOf(df.format(footPrint(stubss.get(random), A[0][0], A[l-1][l-1])));
                
//                  // Get execution time and number of needed servers
//                 int number_of_calls = (int) Math.pow(l, 2);
//                 double execution_time = number_of_calls*footprint;
		
// 		// Calculate no. of servers
//                 double number_of_server_needed = execution_time/10;

                
//                 System.out.println("Estimated number of servers: " + number_of_server_needed);
               
//                 if((number_of_server_needed > 7) ){
//                         number_of_server_needed = 8;
                        
//                 }

//                 int number_of_servers_in_use = (int) Math.round(number_of_server_needed);
//                 System.out.println("Number of used servers: " + number_of_servers_in_use);
// 		System.out.println("Footprint is: " + footprint + " seconds");
       
//                 int C[][] = new int[l][l];

// 		// Start the matrix calculation and print the result onto client 
//                 for (int i = 0; i < l; i++) {
//                         for (int j = 0; j < l; j++) {
//                             for (int k = 0; k < l; k++) {
                                
//                                 MatrixReply temp=stubss.get(stubs_index).multiplyBlock(MatrixRequest.newBuilder().setA00(A[i][k]).setB00(B[k][j]).build());
//                                 if(stubs_index == number_of_servers_in_use-1) 
// 					stubs_index = 0;
//                                 else 
// 					stubs_index++;
				    
//                                 MatrixReply temp2=stubss.get(stubs_index).addBlock(MatrixRequest.newBuilder().setA00(C[i][j]).setB00(temp.getC00()).build());
//                                 C[i][j] = temp2.getC00();
//                                 if(stubs_index == number_of_servers_in_use-1) 
// 					stubs_index = 0;
//                                 else 
// 					stubs_index++;
//                             }
//                         }
//                     }
		

//                     // Print result matrix
// 		String s="[  ";
// 		for (int i = 0; i < A.length; i++) {
//                         for (int j = 0; j < A[0].length; j++) {
//                             System.out.print(C[i][j] + " ");
// 			    s= s+ " "+ C[i][j];
//                         }
//                         System.out.println("");
// 			s=s+ "     ,     " ;
//                     }
// 		s=s+ "  ]" ;
		
// 		redirectAttributes.addFlashAttribute("resultMult",
// 						"Multiplication result is:" +" "+ s +" !!");
// //                 // Close channels
//                 channel1.shutdown();
//                 channel2.shutdown();
//                 channel3.shutdown();
//                 channel4.shutdown();
//                 channel5.shutdown();
//                 channel6.shutdown();
//                 channel7.shutdown();
//                 channel8.shutdown();
                
	}
	
	private static double footPrint(MatrixServiceGrpc.MatrixServiceBlockingStub stub, int A, int B){

                double startTime = System.nanoTime();
                MatrixReply temp=stub.multiplyBlock(MatrixRequest.newBuilder().setA00(A).setB00(B).build());
                double endTime = System.nanoTime();
                double footprint= endTime-startTime;
                return (footprint/1000000000);
        }
	
	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}
	

        public static String get_string_matrix(File file) {
                StringBuilder result = new StringBuilder();
                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String s = null;
                    while ((s = br.readLine()) != null) {
                        result.append(System.lineSeparator() + s);
                    }
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return result.toString();
        }
	
	 public static int[][] matrix_conversion(String m){

                // split matrices row and col number from actual matrix data
                String[] data = m.split(";"); // get matrix data 
                String row_col[] = data[0].split(","); // get matrix row and cl 
            
                int row = Integer.parseInt(row_col[0].replaceAll("[\\n\\t ]", ""));
                int col = Integer.parseInt(row_col[1].replaceAll("[\\n\\t ]", ""));

                String[] matrixData_temp = data[1].split(" "); // get the matrix data into string array 
               
                int[][] matrix = new int[row][col];
                int temp_matrix_index = 0; 
                 
                for(int i = 0; i < row; i++){
                        for(int j = 0; j < col; j++){
                                matrix[i][j] = Integer.parseInt(matrixData_temp[temp_matrix_index].replaceAll("[\\n\\t ]", ""));
                                temp_matrix_index++;
                        }
                }
                return matrix;
        }
	

}





// @Controller
// public class FileUploadController {

// 	private final StorageService storageService;
// 	private File destination;
// 	public int[][] matrix1;
// 	public int[][] matrix2;

//         @Value("${matrix.symbols}")
//         private String matrixS;
	
// 	@Value("${com.CW-DS.grpc.client.serverAddress}")
// 	private String serverAddress;

// 	@Autowired
// 	public FileUploadController(StorageService storageService) {
// 		this.storageService = storageService;
// 	}

// 	@GetMapping("/")
// 	public String listUploadedFiles(Model model) throws IOException {

// 		model.addAttribute("files", storageService.loadAll().map(
// 				path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
// 						"serveFile", path.getFileName().toString()).build().toUri().toString())
// 				.collect(Collectors.toList()));
		
// // 		return html form
// 		return "uploadForm";
// 	}

// 	@GetMapping("/files/{filename:.+}")
// 	@ResponseBody
// 	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

// 		Resource file = storageService.loadAsResource(filename);
// 		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
// 				"attachment; filename=\"" + file.getFilename() + "\"").body(file);
// 	}
	

	

// 	@RequestMapping(value="/matrixMultip", params="multiply", method=RequestMethod.POST)
// 	public String handleFileUpload2(RedirectAttributes redirectAttributes) {
// 			System.out.println("pressed matrix multiplication button");
// 			redirectAttributes.addFlashAttribute("message", "Here is your multiplication: ");   
// 		return "redirect:/";
// 	}
	
	
// 	@PostMapping("/")
// 	public String handleFileUpload(@RequestParam("file") MultipartFile file,
// 			RedirectAttributes redirectAttributes) throws Exception{
	
// 		 // try{
// 			String file_path = "/home/melisa_bogdan/CW-DS";
// 			destination = new File(file_path + '/' + file.getOriginalFilename());
// 			try { 
// 				file.transferTo(destination); 
// 			} catch (Exception e) { 
// 				redirectAttributes.addFlashAttribute("message",
// 						"File is not provided, please add a file!! " + file.getOriginalFilename() + "!!");

// 			}
// 			// CHECK IF FILE EMPTY
// 			if (file.isEmpty()) {
// 				 redirectAttributes.addFlashAttribute("message",
// 					"File " + file.getOriginalFilename() + " is empty! Upload again. ");
// 			}else {

// 				String matrixOne= get_string_matrix(destination).split(matrixS)[0];
// 				String matrixTwo = get_string_matrix(destination).split(matrixS)[1];

// 				matrix1 = matrix_conversion(matrixOne);
// 				matrix2 = matrix_conversion(matrixTwo);

// 				// CHECK IF SQUARE
// 				if(matrix1.length != matrix1[0].length || matrix2.length != matrix2[0].length){
// 					redirectAttributes.addFlashAttribute("message",
// 					"Matrices in file " + file.getOriginalFilename() + " are not square!! ");
// 				// CHECK IF POWER OF 2
// 				}else if (checkIfPower2(matrix2.length)== false || checkIfPower2(matrix1.length)== false){
// 					redirectAttributes.addFlashAttribute("message",
// 					"Matrices in file " + file.getOriginalFilename() + " are not power of 2!! ");
// 				}else{
// 				// All clear we can do multiplication now
	
// 					redirectAttributes.addFlashAttribute("message",
// 							"You successfully uploaded " +" "+ file.getOriginalFilename() +" !!");
// 					redirectAttributes.addFlashAttribute("matrix1",
// 							"Matrix 1 from file is: " +" "+ matrixOne);
// 					redirectAttributes.addFlashAttribute("matrix2",
// 							"Matrix 2 from file is: " +" "+ matrixTwo);
// 				grpcC(matrix1, matrix2, redirectAttributes);
// 				}

// 			}
// // 		} catch (Exception e) { 
// // 			throw new Exception("Matrices cannot be multiplied. Incompatible sizes! ");
// // 		}
			
// 		return "redirect:/";
// 	}
	
// 	// function for checking if matrix length is power of 2
// 	public Boolean checkIfPower2(int n){
// 		while(n!=1)
// 		{
// 			n = n/2;
//             		if(n%2 != 0 && n != 1){
// 				System.out.println("not power of 2");
// 				return false;
// 			}
// 		}
// 		return true;
// 	}
	
// 	public void grpcC(int[][]A, int[][]B, RedirectAttributes redirectAttributes){

// 		// Deadline based scaling
		
// 		// initiate channels
//                 ManagedChannel channel1 = ManagedChannelBuilder.forAddress(serverAddress,  9090).usePlaintext().build();  
//                 ManagedChannel channel2 = ManagedChannelBuilder.forAddress(serverAddress,  9090).usePlaintext().build();  
//                 ManagedChannel channel3 = ManagedChannelBuilder.forAddress(serverAddress,  9090).usePlaintext().build();  
//                 ManagedChannel channel4 = ManagedChannelBuilder.forAddress(serverAddress,  9090).usePlaintext().build();  
//                 ManagedChannel channel5 = ManagedChannelBuilder.forAddress(serverAddress,  9090).usePlaintext().build();  
//                 ManagedChannel channel6 = ManagedChannelBuilder.forAddress(serverAddress,  9090).usePlaintext().build();  
//                 ManagedChannel channel7 = ManagedChannelBuilder.forAddress(serverAddress,  9090).usePlaintext().build();  
//                 ManagedChannel channel8 = ManagedChannelBuilder.forAddress(serverAddress,  9090).usePlaintext().build(); 
// // 		ManagedChannel channel9 = ManagedChannelBuilder.forAddress(serverAddress,  9090).usePlaintext().build();  
// //                 ManagedChannel channel10 = ManagedChannelBuilder.forAddress(serverAddress,  9090).usePlaintext().build();  
// //                 ManagedChannel channel11 = ManagedChannelBuilder.forAddress(serverAddress,  9090).usePlaintext().build();  
// //                 ManagedChannel channel12 = ManagedChannelBuilder.forAddress(serverAddress,  9090).usePlaintext().build(); 
		
// 		MatrixServiceGrpc.MatrixServiceBlockingStub stub1 = MatrixServiceGrpc.newBlockingStub(channel1);
//                 MatrixServiceGrpc.MatrixServiceBlockingStub stub2 = MatrixServiceGrpc.newBlockingStub(channel2);
//                 MatrixServiceGrpc.MatrixServiceBlockingStub stub3 = MatrixServiceGrpc.newBlockingStub(channel3);
//                 MatrixServiceGrpc.MatrixServiceBlockingStub stub4 = MatrixServiceGrpc.newBlockingStub(channel4);
//                 MatrixServiceGrpc.MatrixServiceBlockingStub stub5 = MatrixServiceGrpc.newBlockingStub(channel5);
//                 MatrixServiceGrpc.MatrixServiceBlockingStub stub6 = MatrixServiceGrpc.newBlockingStub(channel6);
//                 MatrixServiceGrpc.MatrixServiceBlockingStub stub7 = MatrixServiceGrpc.newBlockingStub(channel7);
//                 MatrixServiceGrpc.MatrixServiceBlockingStub stub8 = MatrixServiceGrpc.newBlockingStub(channel8);
		
// 		// keep track of stubs in an array list
// 		ArrayList <MatrixServiceGrpc.MatrixServiceBlockingStub> stubs_list = new ArrayList<MatrixServiceGrpc.MatrixServiceBlockingStub>();
//                 stubs_list.add(stub1);
//                 stubs_list.add(stub2);
//                 stubs_list.add(stub3);
//                 stubs_list.add(stub4);
//                 stubs_list.add(stub5);
//                 stubs_list.add(stub6);
//                 stubs_list.add(stub7);
//                 stubs_list.add(stub8);
// // 		stubs_list.add(stub9);
// //                 stubs_list.add(stub10);
// //                 stubs_list.add(stub11);
// //                 stubs_list.add(stub12);
		
// 		// stub index in array list
// 		int index = 0;
		
// 		// intilialize deadline
// 		int deadline=10;

// //                 // Length row
//                 int l = A.length;

// //                 // use a random stub from the stub array to calculate footprint 
//                 DecimalFormat d = new DecimalFormat("#.#"); 
//                 Random r = new Random();
//                 int low = 0;
//                 int high = 7;
//                 int rr = r.nextInt(high-low) + low; 
		
// 		// calculate the footprint
// 		double f= footprint(stubs_list.get(rr), A[0][0], A[l-1][l-1]);
//                 double footprint = Double.valueOf(d.format(f));
                
//                  // calculate execution time and number of servers that we need
//                 int nocalls = (int) Math.pow(l, 2);
		
// 		// formula to calc execution time
//                 double execTime = nocalls * footprint;
		
// 		// calculate no. of servers
//                 double noserver = execTime/10;

// 		if((noserver > 7) ){
//                         noserver = 8;     
//                 }
		
//                 System.out.println("Estimated number of servers: " + noserver);

//                 int noServersUsed = (int) Math.round(noserver);
//                 System.out.println("Number of used servers: " + noServersUsed);
// 		System.out.println("Footprint is: " + footprint + " seconds");
       
//                 int C[][] = new int[l][l];

// 		// Start the matrix calculation and print the result onto client 
//                 for (int i = 0; i < l; i++) {
//                         for (int j = 0; j < l; j++) {
//                             for (int k = 0; k < l; k++) {
                                
//                                 MatrixReply m = stubs_list.get(index).multiplyBlock(MatrixRequest.newBuilder().setA00(A[i][k]).setB00(B[k][j]).build());
//                                 if(index == noServersUsed - 1) 
// 					index = 0;
//                                 else 
// 					index++;
				    
//                                 MatrixReply m2 = stubs_list.get(index).addBlock(MatrixRequest.newBuilder().setA00(C[i][j]).setB00( m.getC00()).build());
// 				// assignnew element for each pos of the new int array    
//                                 C[i][j] = m2.getC00();
//                                 if(index == noServersUsed-1) 
// 					index = 0;
//                                 else 
// 					index++;
//                             }
//                         }
//                     }
		

//                     // Print result matrix
// 		String s="[  ";
// 		for (int i = 0; i < A.length; i++) {
//                         for (int j = 0; j < A[0].length; j++) {
//                             System.out.print(C[i][j] + " ");
// 			    s= s+ " "+ C[i][j];
//                         }
//                         System.out.println("");
// 			s=s+ "     ,     " ;
//                     }
// 		s=s+ "  ]" ;
		
// 		redirectAttributes.addFlashAttribute("resultMult",
// 						"Multiplication result is:" +" "+ s +" !!");
//                 // Close channels
//                 channel1.shutdown();
//                 channel2.shutdown();
//                 channel3.shutdown();
//                 channel4.shutdown();
//                 channel5.shutdown();
//                 channel6.shutdown();
//                 channel7.shutdown();
//                 channel8.shutdown();
                
// 	}
	
// 	private static double footprint(MatrixServiceGrpc.MatrixServiceBlockingStub stub, int A, int B){
// 		// mark the start time
//                 double start = System.nanoTime();
		
// 		// do the multiplication
//                 MatrixReply m = stub.multiplyBlock(MatrixRequest.newBuilder().setA00(A).setB00(B).build());
		
// 		// mark the end time
//                 double end = System.nanoTime();
		
// 		// final time is end-start
//                 double footprint= end-start;
		
// 		// calculate in seconds
//                 return (footprint/1000000000);
//         }
	
// 	@ExceptionHandler(StorageFileNotFoundException.class)
// 	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
// 		return ResponseEntity.notFound().build();
// 	}
	

//         public static String get_string_matrix(File file) {
//                 StringBuilder res = new StringBuilder();
//                 try {
//                     BufferedReader b = new BufferedReader(new FileReader(file));
//                     String s = null;
			
//                     while ((s = b.readLine()) != null) {
//                         res.append(System.lineSeparator() + s);
//                     }
//                     b.close();
// 		// error catch
//                 } catch (Exception e) {
//                     e.printStackTrace();
//                 }
// 		String var= res.toString();
//                 return var;
//         }
	
// 	 public static int[][] matrix_conversion(String matrix){

//                 // split matrice into rows and cols 
//                 String[] m = matrix.split(";"); // get matrix data 
		 
// 		 // matrix of rows and columns plit by ,
//                 String r_c[] = m[0].split(","); 
            	
// 		 // get rid of all new lines and tabs
//                 int r = Integer.parseInt(r_c[0].replaceAll("[\\n\\t ]", ""));
//                 int c = Integer.parseInt(r_c[1].replaceAll("[\\n\\t ]", ""));

//                 String[] matrix_temp = m[1].split(" "); // get the matrix data into string array 
               
// 		 // new matrix which will hold the matrix conersion result from string to int
//                 int[][] matrix1 = new int[r][c];
//                 int matrix1_index = 0; 
                 
//                 for(int i = 0; i < r; i++){
//                         for(int j = 0; j < c; j++){
//                                 matrix1[i][j] = Integer.parseInt( matrix_temp[matrix1_index].replaceAll("[\\n\\t ]", ""));
//                                 matrix1_index++;
//                         }
//                 }
//                 return matrix1;
//         }
	

// }




