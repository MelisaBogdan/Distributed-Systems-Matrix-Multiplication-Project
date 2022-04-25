package com.example.grpc.server.grpcserver;


import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
@GrpcService

class MatrixServiceImpl extends MatrixServiceGrpc.MatrixServiceImplBase {
    @Override
    public void addBlock(MatrixRequest request, StreamObserver<MatrixReply> reply)
	{
// 		System.out.println("Request received from client:\n" + request);
// 		int C00=request.getA00()+request.getB00();

// 		MatrixReply response = MatrixReply.newBuilder().setC00(C00).build();
// 		reply.onNext(response);
// 		reply.onCompleted();
	    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
				.usePlaintext()
				.build();		
		ArrayList<MatrixReply>rep = new ArrayList<>();
		MatrixServiceGrpc.MatrixServiceBlockingStub stub = MatrixServiceGrpc.newBlockingStub(channel);
		//loop through list of blocks
		for(int counter =0; counter<m1Blocked.size();counter++)
		{
			//retrieve each block to be added
			int[][] takeBlock1 = m1Blocked.get(counter);
			int[][] takeBlock2 = m2Blocked.get(counter);
			MatrixReply A = stub.addBlock(MatrixRequest.newBuilder()
				.setA00(takeBlock1[0][0])
				.setA01(takeBlock1[0][1])
				.setA10(takeBlock1[1][0])
				.setA11(takeBlock1[1][1])
				.setB00(takeBlock2[0][0])
				.setB01(takeBlock2[0][1])
				.setB10(takeBlock2[1][0])
				.setB11(takeBlock2[1][1])
				.build());
				rep.add(A);
				//build a matrixreply arraylist of blocks
				
		}
		String resp = getResponse(rep);
		return resp;
	}
    
	@Override
    public void multiplyBlock(MatrixRequest request, StreamObserver<MatrixReply> reply)
    {
// 		System.out.println("Request received from client:\n" + request);
// 		int C00=request.getA00()*request.getB00();

// 		MatrixReply response = MatrixReply.newBuilder().setC00(C00).build();
// 		reply.onNext(response);
// 		reply.onCompleted();
	        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
				.usePlaintext()
				.build();
		MatrixServiceGrpc.MatrixServiceBlockingStub stub = MatrixServiceGrpc.newBlockingStub(channel);
		MatrixReply A = stub.multiplyBlock(MatrixRequest.newBuilder()
				.setA00(m1[0][0])
				.setA01(m1[0][1])
				.setA10(m1[1][0])
				.setA11(m1[1][1])
				.setB00(m2[0][0])
				.setB01(m2[0][1])
				.setB10(m2[1][0])
				.setB11(m2[1][1])
				.build());
		//works using 2x2 matrices
		String resp = A.getC00() + A.getC01() + A.getC10() + A.getC11() + "";
		print(resp);
		
		return resp;
    }
}

// public class MatrixServiceImpl extends MatrixServiceGrpc.MatrixServiceImplBase
// {
// 	@Override
// 	public void addBlock(MatrixRequest request, StreamObserver<MatrixReply> reply)
// 	{
// 		System.out.println("Request received from client:\n" + request);
// 		int C00=request.getA00()+request.getB00();
//     		int C01=request.getA01()+request.getB01();
// 		int C10=request.getA10()+request.getB10();
// 		int C11=request.getA11()+request.getB11();
// 		MatrixReply response = MatrixReply.newBuilder().setC00(C00).setC01(C01).setC10(C10).setC11(C11).build();
// 		reply.onNext(response);
// 		reply.onCompleted();
// 	}
// 	@Override
//     	public void multiplyBlock(MatrixRequest request, StreamObserver<MatrixReply> reply)
//     	{
//         	System.out.println("Request received from client:\n" + request);
//         	int C00=request.getA00()*request.getB00()+request.getA01()*request.getB10();
// 		int C01=request.getA00()*request.getB01()+request.getA01()*request.getB11();
// 		int C10=request.getA10()*request.getB00()+request.getA11()*request.getB10();
// 		int C11=request.getA10()*request.getB01()+request.getA11()*request.getB11();
//         MatrixReply response = MatrixReply.newBuilder().setC00(C00).setC01(C01).setC10(C10).setC11(C11).build();
//         reply.onNext(response);
//         reply.onCompleted();
//     }
// }
