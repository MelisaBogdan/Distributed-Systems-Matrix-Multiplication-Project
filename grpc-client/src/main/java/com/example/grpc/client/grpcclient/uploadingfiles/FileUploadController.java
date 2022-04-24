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


@Controller
public class FileUploadController {

	private final StorageService storageService;
	private File destination;
	public int[][] matrix1;
	public int[][] matrix2;

        @Value("${matrix.symbols}")
        private String matrixS;
	

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

		return "uploadForm";
	}

	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

		Resource file = storageService.loadAsResource(filename);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + file.getFilename() + "\"").body(file);
	}
	
// 	@PostMapping("/")
// 	@RequestMapping(value="/handle", params="action=multiply")
// 	public String handleFileUpload2( @RequestParam(value="action", required=true) String action, RedirectAttributes redirectAttributes) {
		
// 		if (action.equals("multiply")) {
// 			redirectAttributes.addFlashAttribute("message", "You pressed multiply button! BOYAAA");   
// 		}
// 		return "redirect:/";
// 	}
	
// 	@PostMapping("/")
	@RequestMapping(value="/matrixMultip", params="multiply", method=RequestMethod.POST)
	public String handleFileUpload2(RedirectAttributes redirectAttributes) {
			System.out.println("should work");
			redirectAttributes.addFlashAttribute("message", "Here is your multiplication: " + matrix1);   
		return "redirect:/";
	}
	
	
	@PostMapping("/")
	public String handleFileUpload(@RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) {
	
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
			
			// CHECK IF MATRIX FORMAT IS RIGHT (SQUARE)
			if(matrix1.length != matrix1[0].length || matrix2.length != matrix2[0].length){
				redirectAttributes.addFlashAttribute("message",
				"Matrices in file " + file.getOriginalFilename() + " are not square!! ");
			}else if(matrix1.length % 4 !=0 || matrix2.length % 4 !=0 ){
				redirectAttributes.addFlashAttribute("message",
				"Matrices in file " + file.getOriginalFilename() + " are not accepted (length not multiple by 4) !! ");
			}else{
// 			all clear
				redirectAttributes.addFlashAttribute("message",
						"You successfully uploaded " +" "+ file.getOriginalFilename() +" !!");
				redirectAttributes.addFlashAttribute("matrix1",
						"Matrix 1 from file is: " +" "+ matrixOne);
				redirectAttributes.addFlashAttribute("matrix2",
						"Matrix 2 from file is: " +" "+ matrixTwo);
			grpcClient(matrix1, matrix2);
			}
		}
		
		
		return "redirect:/";
	}

	public void grpcClient(int[][]a, int[][]b){
                System.out.println("\n=====================================");
//                 System.out.println("Deadline: " + deadline + " seconds"); 

                // Different AWS private IP's
                String aws1 = ""; 
                String aws2 = ""; 
                String aws3 = ""; 
                String aws4 = ""; 
                String aws5 = "";
                String aws6 = ""; 
                String aws7 = ""; 
                String aws8 = "";
		
		// Different channels for each AWS 
//                 ManagedChannel channel1 = ManagedChannelBuilder.forAddress(aws1, 9090).usePlaintext().build();  
//                 ManagedChannel channel2 = ManagedChannelBuilder.forAddress(aws2, 9090).usePlaintext().build();  
//                 ManagedChannel channel3 = ManagedChannelBuilder.forAddress(aws3, 9090).usePlaintext().build();  
//                 ManagedChannel channel4 = ManagedChannelBuilder.forAddress(aws4, 9090).usePlaintext().build();  
//                 ManagedChannel channel5 = ManagedChannelBuilder.forAddress(aws5, 9090).usePlaintext().build();  
//                 ManagedChannel channel6 = ManagedChannelBuilder.forAddress(aws6, 9090).usePlaintext().build();  
//                 ManagedChannel channel7 = ManagedChannelBuilder.forAddress(aws7, 9090).usePlaintext().build();  
//                 ManagedChannel channel8 = ManagedChannelBuilder.forAddress(aws8, 9090).usePlaintext().build(); 
	}
	
	
	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}
	
// 	// Get matrix string from the file
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
		
// 		ArrayList<String> result = new ArrayList<>();
// 		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
//    			 while (br.ready()) {
//         			result.add(br.readLine());
//     			}
// 		} catch (Exception e) {
// 			e.printStackTrace();
//             	}
// 		return result.toString();
        }
	
	 public static int[][] matrix_conversion(String m){

                // split matrices row and col number from actual matrix data
                String[] data = m.split(";"); // get matrix data 
                String row_col[] = data[0].split(","); // get matrix row and cl 
                // Get row and col number into int var. 
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
// 	@Bean 
// 	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
// 		return new PropertySourcesPlaceholderConfigurer();
// 	}
}
