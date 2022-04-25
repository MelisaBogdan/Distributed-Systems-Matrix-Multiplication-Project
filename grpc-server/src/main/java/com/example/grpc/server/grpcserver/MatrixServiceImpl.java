package com.example.grpc.server.grpcserver;


import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
@GrpcService

class MatrixServiceImpl extends MatrixServiceGrpc.MatrixServiceImplBase {
    @Override
    public void addBlock(MatrixRequest request, StreamObserver<MatrixReply> reply)
	{
		int C00=request.getA00()+request.getB00();
		MatrixReply response = MatrixReply.newBuilder().setC00(C00).build();
		reply.onNext(response);
		reply.onCompleted();
	}
    
	@Override
    public void multiplyBlock(MatrixRequest request, StreamObserver<MatrixReply> reply)
    {
		int C00=request.getA00()*request.getB00();
		MatrixReply response = MatrixReply.newBuilder().setC00(C00).build();
		reply.onNext(response);
		reply.onCompleted();
    }
}
