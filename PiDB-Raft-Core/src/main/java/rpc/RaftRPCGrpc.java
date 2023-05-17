package rpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.54.1)",
    comments = "Source: RaftRPC.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class RaftRPCGrpc {

  private RaftRPCGrpc() {}

  public static final String SERVICE_NAME = "raft.RaftRPC";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<rpc.RaftProto.AppendRequest,
      rpc.RaftProto.AppendResponse> getAppendEntriesMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "appendEntries",
      requestType = rpc.RaftProto.AppendRequest.class,
      responseType = rpc.RaftProto.AppendResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<rpc.RaftProto.AppendRequest,
      rpc.RaftProto.AppendResponse> getAppendEntriesMethod() {
    io.grpc.MethodDescriptor<rpc.RaftProto.AppendRequest, rpc.RaftProto.AppendResponse> getAppendEntriesMethod;
    if ((getAppendEntriesMethod = RaftRPCGrpc.getAppendEntriesMethod) == null) {
      synchronized (RaftRPCGrpc.class) {
        if ((getAppendEntriesMethod = RaftRPCGrpc.getAppendEntriesMethod) == null) {
          RaftRPCGrpc.getAppendEntriesMethod = getAppendEntriesMethod =
              io.grpc.MethodDescriptor.<rpc.RaftProto.AppendRequest, rpc.RaftProto.AppendResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "appendEntries"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  rpc.RaftProto.AppendRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  rpc.RaftProto.AppendResponse.getDefaultInstance()))
              .setSchemaDescriptor(new RaftRPCMethodDescriptorSupplier("appendEntries"))
              .build();
        }
      }
    }
    return getAppendEntriesMethod;
  }

  private static volatile io.grpc.MethodDescriptor<rpc.RaftProto.VoteRequest,
      rpc.RaftProto.VoteResponse> getRequestVoteMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "requestVote",
      requestType = rpc.RaftProto.VoteRequest.class,
      responseType = rpc.RaftProto.VoteResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<rpc.RaftProto.VoteRequest,
      rpc.RaftProto.VoteResponse> getRequestVoteMethod() {
    io.grpc.MethodDescriptor<rpc.RaftProto.VoteRequest, rpc.RaftProto.VoteResponse> getRequestVoteMethod;
    if ((getRequestVoteMethod = RaftRPCGrpc.getRequestVoteMethod) == null) {
      synchronized (RaftRPCGrpc.class) {
        if ((getRequestVoteMethod = RaftRPCGrpc.getRequestVoteMethod) == null) {
          RaftRPCGrpc.getRequestVoteMethod = getRequestVoteMethod =
              io.grpc.MethodDescriptor.<rpc.RaftProto.VoteRequest, rpc.RaftProto.VoteResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "requestVote"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  rpc.RaftProto.VoteRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  rpc.RaftProto.VoteResponse.getDefaultInstance()))
              .setSchemaDescriptor(new RaftRPCMethodDescriptorSupplier("requestVote"))
              .build();
        }
      }
    }
    return getRequestVoteMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static RaftRPCStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RaftRPCStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RaftRPCStub>() {
        @java.lang.Override
        public RaftRPCStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RaftRPCStub(channel, callOptions);
        }
      };
    return RaftRPCStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static RaftRPCBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RaftRPCBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RaftRPCBlockingStub>() {
        @java.lang.Override
        public RaftRPCBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RaftRPCBlockingStub(channel, callOptions);
        }
      };
    return RaftRPCBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static RaftRPCFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RaftRPCFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RaftRPCFutureStub>() {
        @java.lang.Override
        public RaftRPCFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RaftRPCFutureStub(channel, callOptions);
        }
      };
    return RaftRPCFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     * <pre>
     * The AppendEntries RPC defined in Figure 2
     * </pre>
     */
    default void appendEntries(rpc.RaftProto.AppendRequest request,
        io.grpc.stub.StreamObserver<rpc.RaftProto.AppendResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAppendEntriesMethod(), responseObserver);
    }

    /**
     * <pre>
     * The RequestVote RPC defined in Figure 2
     * </pre>
     */
    default void requestVote(rpc.RaftProto.VoteRequest request,
        io.grpc.stub.StreamObserver<rpc.RaftProto.VoteResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRequestVoteMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service RaftRPC.
   */
  public static abstract class RaftRPCImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return RaftRPCGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service RaftRPC.
   */
  public static final class RaftRPCStub
      extends io.grpc.stub.AbstractAsyncStub<RaftRPCStub> {
    private RaftRPCStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RaftRPCStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RaftRPCStub(channel, callOptions);
    }

    /**
     * <pre>
     * The AppendEntries RPC defined in Figure 2
     * </pre>
     */
    public void appendEntries(rpc.RaftProto.AppendRequest request,
        io.grpc.stub.StreamObserver<rpc.RaftProto.AppendResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getAppendEntriesMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * The RequestVote RPC defined in Figure 2
     * </pre>
     */
    public void requestVote(rpc.RaftProto.VoteRequest request,
        io.grpc.stub.StreamObserver<rpc.RaftProto.VoteResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRequestVoteMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service RaftRPC.
   */
  public static final class RaftRPCBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<RaftRPCBlockingStub> {
    private RaftRPCBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RaftRPCBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RaftRPCBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * The AppendEntries RPC defined in Figure 2
     * </pre>
     */
    public rpc.RaftProto.AppendResponse appendEntries(rpc.RaftProto.AppendRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAppendEntriesMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * The RequestVote RPC defined in Figure 2
     * </pre>
     */
    public rpc.RaftProto.VoteResponse requestVote(rpc.RaftProto.VoteRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRequestVoteMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service RaftRPC.
   */
  public static final class RaftRPCFutureStub
      extends io.grpc.stub.AbstractFutureStub<RaftRPCFutureStub> {
    private RaftRPCFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RaftRPCFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RaftRPCFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * The AppendEntries RPC defined in Figure 2
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<rpc.RaftProto.AppendResponse> appendEntries(
        rpc.RaftProto.AppendRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getAppendEntriesMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * The RequestVote RPC defined in Figure 2
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<rpc.RaftProto.VoteResponse> requestVote(
        rpc.RaftProto.VoteRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRequestVoteMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_APPEND_ENTRIES = 0;
  private static final int METHODID_REQUEST_VOTE = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_APPEND_ENTRIES:
          serviceImpl.appendEntries((rpc.RaftProto.AppendRequest) request,
              (io.grpc.stub.StreamObserver<rpc.RaftProto.AppendResponse>) responseObserver);
          break;
        case METHODID_REQUEST_VOTE:
          serviceImpl.requestVote((rpc.RaftProto.VoteRequest) request,
              (io.grpc.stub.StreamObserver<rpc.RaftProto.VoteResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getAppendEntriesMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              rpc.RaftProto.AppendRequest,
              rpc.RaftProto.AppendResponse>(
                service, METHODID_APPEND_ENTRIES)))
        .addMethod(
          getRequestVoteMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              rpc.RaftProto.VoteRequest,
              rpc.RaftProto.VoteResponse>(
                service, METHODID_REQUEST_VOTE)))
        .build();
  }

  private static abstract class RaftRPCBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    RaftRPCBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return rpc.RaftProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("RaftRPC");
    }
  }

  private static final class RaftRPCFileDescriptorSupplier
      extends RaftRPCBaseDescriptorSupplier {
    RaftRPCFileDescriptorSupplier() {}
  }

  private static final class RaftRPCMethodDescriptorSupplier
      extends RaftRPCBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    RaftRPCMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (RaftRPCGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new RaftRPCFileDescriptorSupplier())
              .addMethod(getAppendEntriesMethod())
              .addMethod(getRequestVoteMethod())
              .build();
        }
      }
    }
    return result;
  }
}
