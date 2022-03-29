package com.example.grpc.client.grpcclient;

import com.example.grpc.server.grpcserver.PingRequest;
import com.example.grpc.server.grpcserver.PongResponse;
import com.example.grpc.server.grpcserver.PingPongServiceGrpc;
import com.example.grpc.server.grpcserver.MatrixRequest;
import com.example.grpc.server.grpcserver.MatrixReply;
import com.example.grpc.server.grpcserver.MatrixServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
@Service
public class GRPCClientService {
    public String ping() {
        	ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();        
		PingPongServiceGrpc.PingPongServiceBlockingStub stub
                = PingPongServiceGrpc.newBlockingStub(channel);        
		PongResponse helloResponse = stub.ping(PingRequest.newBuilder()
                .setPing("")
                .build());        
		channel.shutdown();        
		return helloResponse.getPong();
    }
    public String add(){
		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",9090)
		.usePlaintext()
		.build();
		MatrixServiceGrpc.MatrixServiceBlockingStub stub
		 = MatrixServiceGrpc.newBlockingStub(channel);
		MatrixReply A=stub.addBlock(MatrixRequest.newBuilder()
			.setA00(1)
			.setA01(2)
			.setA10(5)
			.setA11(6)
			.setB00(1)
			.setB01(2)
			.setB10(5)
			.setB11(6)
			.build());
		String resp= A.getC00()+" "+A.getC01()+"<br>"+A.getC10()+" "+A.getC11()+"\n";
		return resp;
    }

	public FileUploadResponse fileUpload(@RequestParam("file") MultipartFile file){
                
		fileName = file.getOriginalFilename(); // get file name 
		String filePathServer = "/home/melisa_bogdan2/CW-DS/files"; // use to save file for server development 


		uploadFilePath = filePathServer;

		contentType = file.getContentType();
		dest = new File(uploadFilePath + '/' + fileName);

		if (!dest.getParentFile().exists())  dest.getParentFile().mkdirs(); // make directory if doesn't exist
			

		try { file.transferTo(dest); }
		catch (Exception e) { return new FileUploadResponse(fileName, contentType, "File is not provided, please add a file!!! " + e.getMessage()); }

		// Get both matrices from a single file 
		String matrixA_temp = txt2String(dest).split(matrixSymbols)[0];
		String matrixB_temp = txt2String(dest).split(matrixSymbols)[1];

		// Convert each string matrix to int[][]] matrix
		int[][] matrixA = convertToMatrix(matrixA_temp);
		int[][] matrixB = convertToMatrix(matrixB_temp);

		// If not square matrix
		if(matrixA.length != matrixA[0].length || matrixB.length != matrixB[0].length){
				String data  = "Matrix A: " + matrixA.length  + "x" + matrixA[0].length;
					   data += "  Matrix B: " + matrixB.length  + "x" + matrixB[0].length;
				return new FileUploadResponse(fileName, contentType, "Rows and Columns of the Matrices should be equal size!!! " + data);
		}
		// If not even number rows and col
		if(matrixA.length % 4 !=0 || matrixB.length % 4 !=0 ){
				String data  = "Matrix A: " + matrixA.length  + "x" + matrixA[0].length;
					   data += "  Matrix B: " + matrixB.length  + "x" + matrixB[0].length;
				return new FileUploadResponse(fileName, contentType, "Accepted Matrices: nxn where n%4=0!!! " + data);
		}
		grpcClient(matrixA, matrixB);
		return new FileUploadResponse(fileName, contentType, "File Successfully Uploaded");
	}


}
