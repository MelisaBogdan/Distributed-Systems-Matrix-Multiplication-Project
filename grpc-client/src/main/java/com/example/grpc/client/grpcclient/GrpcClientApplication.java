
 
Pull requests
Issues
Marketplace
Explore
 
@MelisaBogdan 
jdoyle314
/
ECS656U-796PLab2StartingPoint
Public
 Watch 1 
Fork 27
 Starred 3
Code
Issues
Pull requests
Actions
Projects
Wiki
Security
Insights
 main 
ECS656U-796PLab2StartingPoint/grpc-client/src/main/java/com/example/grpc/client/grpcclient/GrpcClientApplication.java  / Jump to 
Go to file

@jdoyle314
jdoyle314 Initial Commit
Latest commit b1a61ef on 21 Feb
 History
 1 contributor
51 lines (38 sloc)  1.57 KB
Raw Blame
     
package com.example.grpc.client.grpcclient;

import com.example.grpc.server.grpcserver.HelloRequest;
import com.example.grpc.server.grpcserver.HelloResponse;
import com.example.grpc.server.grpcserver.HelloServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


@SpringBootApplication
public class GrpcClientApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(GrpcClientApplication.class, args);
		/*ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
				.usePlaintext()
				.build();
		HelloServiceGrpc.HelloServiceBlockingStub stub
				= HelloServiceGrpc.newBlockingStub(channel);
		HelloResponse helloResponse = stub.hello(HelloRequest.newBuilder()
				.setFirstName("Baeldung")
				.setLastName("gRPC")
				.build());
		System.out.println(helloResponse);
		channel.shutdown();*/

		/*ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
				.usePlaintext()
				.build();
		HelloServiceGrpc.HelloServiceBlockingStub stub
				= HelloServiceGrpc.newBlockingStub(channel);
		HelloResponse helloResponse = stub.hello(HelloRequest.newBuilder()
				.setFirstName("Baeldung")
				.setLastName("gRPC")
				.build());
		channel.shutdown();*/
//		SpringApplication.run(GrpcClientApplication.class, args);
	}

}







// package com.example.grpc.client.grpcclient;

// import com.example.grpc.server.grpcserver.HelloRequest;
// import com.example.grpc.server.grpcserver.HelloResponse;
// import com.example.grpc.server.grpcserver.HelloServiceGrpc;
// import io.grpc.ManagedChannel;
// import io.grpc.ManagedChannelBuilder;
// import net.devh.boot.grpc.client.inject.GrpcClient;
// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

// import org.springframework.context.annotation.Bean;
// import com.example.uploadingfiles.storage.StorageProperties;
// import com.example.uploadingfiles.storage.StorageService;
// package com.example.uploadingfiles;

// import org.springframework.boot.CommandLineRunner;
// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.boot.context.properties.EnableConfigurationProperties;
// import org.springframework.context.annotation.Bean;

// import com.example.uploadingfiles.storage.StorageProperties;
// import com.example.uploadingfiles.storage.StorageService;

// @SpringBootApplication
// public class GrpcClientApplication extends SpringBootServletInitializer {

// 	public static void main(String[] args) {
// 		SpringApplication.run(GrpcClientApplication.class, args);
// 		/*ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
// 				.usePlaintext()
// 				.build();
// 		HelloServiceGrpc.HelloServiceBlockingStub stub
// 				= HelloServiceGrpc.newBlockingStub(channel);
// 		HelloResponse helloResponse = stub.hello(HelloRequest.newBuilder()
// 				.setFirstName("Baeldung")
// 				.setLastName("gRPC")
// 				.build());
// 		System.out.println(helloResponse);
// 		channel.shutdown();*/

// 		/*ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
// 				.usePlaintext()
// 				.build();
// 		HelloServiceGrpc.HelloServiceBlockingStub stub
// 				= HelloServiceGrpc.newBlockingStub(channel);
// 		HelloResponse helloResponse = stub.hello(HelloRequest.newBuilder()
// 				.setFirstName("Baeldung")
// 				.setLastName("gRPC")
// 				.build());
// 		channel.shutdown();*/
// //		SpringApplication.run(GrpcClientApplication.class, args);
// 	}
	

// }

