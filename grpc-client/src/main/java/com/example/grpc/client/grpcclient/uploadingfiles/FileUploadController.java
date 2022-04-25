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
				grpcC(matrix1, matrix2, redirectAttributes);
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
	
	public void grpcC(int[][]A, int[][]B, RedirectAttributes redirectAttributes){

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
		
		// keep track of stubs in an array list
		ArrayList<MatrixServiceGrpc.MatrixServiceBlockingStub> stubss = new ArrayList<MatrixServiceGrpc.MatrixServiceBlockingStub>();
                stubss.add(stub1);
                stubss.add(stub2);
                stubss.add(stub3);
                stubss.add(stub4);
                stubss.add(stub5);
                stubss.add(stub6);
                stubss.add(stub7);
                stubss.add(stub8);
		
		// stub index in array list
		int index = 0;
		
		// intilialize deadline
		int deadline=10;

//                 // Length row
                int l = A.length;

//                 // use a random stub from the stub array to calculate footprint 
                DecimalFormat d = new DecimalFormat("#.#"); 
                Random r = new Random();
//                 int low = 0;
//                 int high = 8;
                int random = r.nextInt(8); 
// 			+ low;
		
		// calculate the footprint
		double f= footPrint(stubss.get(random), A[0][0], A[l-1][l-1]);
                double footprint = Double.valueOf(d.format(f));
                
                 // calculate execution time and number of servers that we need
                int nocalls = (int) Math.pow(l, 2);
		
		// formula to calc execution time
                double execution_time = nocalls * footprint;
		
		// calculate no. of servers
                double noserver = execution_time/10;

                
                System.out.println("Estimated number of servers: " + noserver);
               
                if((number_of_server_needed > 7) ){
                        noserver = 8;
                        
                }

                int noServersUsed = (int) Math.round(noserver);
                System.out.println("Number of used servers: " + noServersUsed);
		System.out.println("Footprint is: " + footprint + " seconds");
       
                int C[][] = new int[l][l];

		// Start the matrix calculation and print the result onto client 
                for (int i = 0; i < l; i++) {
                        for (int j = 0; j < l; j++) {
                            for (int k = 0; k < l; k++) {
                                
                                MatrixReply temp=stubss.get(index).multiplyBlock(MatrixRequest.newBuilder().setA00(A[i][k]).setB00(B[k][j]).build());
                                if(index == noServersUsed-1) 
					index = 0;
                                else 
					index++;
				    
                                MatrixReply temp2=stubss.get(index).addBlock(MatrixRequest.newBuilder().setA00(C[i][j]).setB00(temp.getC00()).build());
                                C[i][j] = temp2.getC00();
                                if(index == noServersUsed-1) 
					index = 0;
                                else 
					index++;
                            }
                        }
                    }
		

                    // Print result matrix
		String s="[  ";
		for (int i = 0; i < A.length; i++) {
                        for (int j = 0; j < A[0].length; j++) {
                            System.out.print(C[i][j] + " ");
			    s= s+ " "+ C[i][j];
                        }
                        System.out.println("");
			s=s+ "     ,     " ;
                    }
		s=s+ "  ]" ;
		
		redirectAttributes.addFlashAttribute("resultMult",
						"Multiplication result is:" +" "+ s +" !!");
//                 // Close channels
                channel1.shutdown();
                channel2.shutdown();
                channel3.shutdown();
                channel4.shutdown();
                channel5.shutdown();
                channel6.shutdown();
                channel7.shutdown();
                channel8.shutdown();
                
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




