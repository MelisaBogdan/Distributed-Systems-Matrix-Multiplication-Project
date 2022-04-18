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
	private File dest;

        @Value("${matrix.symbols}")
        private String matrixSymbols;
	

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

	
	
	@PostMapping("/")
	public String handleFileUpload(@RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) {

		String filePathServer = "/home/melisa_bogdan/CW-DS";
		dest = new File(filePathServer + '/' + file.getOriginalFilename());
		try { 
			file.transferTo(dest); 
		} catch (Exception e) { 
			redirectAttributes.addFlashAttribute("message",
					"File is not provided, please add a file!! " + file.getOriginalFilename() + "!!");
		
		}
// 		CHECK IF FILE EMPTY
		if (file.isEmpty()) {
            		 redirectAttributes.addFlashAttribute("message",
				"File " + file.getOriginalFilename() + " is empty! Upload again. ");
           	}else {
// 			
			String matrixOne= txt2String(dest).split(matrixSymbols)[0];
			String matrixTwo = txt2String(dest).split(matrixSymbols)[1];
			
			redirectAttributes.addFlashAttribute("message",
					"You successfully uploaded " +" "+ file.getOriginalFilename() + " and results is "+matrixOne + " and "+ matrixTwo +" !!");
			
// 			redirectAttributes.addFlashAttribute("matrix",
// 					"Matrices are: " +" "+ matrixOne + " and "+ matrixTwo +" !!");
		}

		return "redirect:/";
	}

	
	
	
	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}
	
// 	// Get matrix string from the file
        public static String txt2String(File file) {
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
	
	 public static int[][] convertToMatrix(String m){

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
