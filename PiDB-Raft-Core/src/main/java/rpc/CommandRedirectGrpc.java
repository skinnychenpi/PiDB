package rpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * This RPC service only represents the redirection of commands from raft node to raft node.
 * Note that it doesn't represent the redirection of commands from client to raft node or to raft cluster.
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.54.1)",
    comments = "Source: RaftRPC.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class CommandRedirectGrpc {

  private CommandRedirectGrpc() {}

  public static final String SERVICE_NAME = "raft.CommandRedirect";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<rpc.RaftProto.RedirectRequest,
      rpc.RaftProto.RedirectResponse> getRedirectCommandMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "redirectCommand",
      requestType = rpc.RaftProto.RedirectRequest.class,
      responseType = rpc.RaftProto.RedirectResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<rpc.RaftProto.RedirectRequest,
      rpc.RaftProto.RedirectResponse> getRedirectCommandMethod() {
    io.grpc.MethodDescriptor<rpc.RaftProto.RedirectRequest, rpc.RaftProto.RedirectResponse> getRedirectCommandMethod;
    if ((getRedirectCommandMethod = CommandRedirectGrpc.getRedirectCommandMethod) == null) {
      synchronized (CommandRedirectGrpc.class) {
        if ((getRedirectCommandMethod = CommandRedirectGrpc.getRedirectCommandMethod) == null) {
          CommandRedirectGrpc.getRedirectCommandMethod = getRedirectCommandMethod =
              io.grpc.MethodDescriptor.<rpc.RaftProto.RedirectRequest, rpc.RaftProto.RedirectResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "redirectCommand"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  rpc.RaftProto.RedirectRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  rpc.RaftProto.RedirectResponse.getDefaultInstance()))
              .setSchemaDescriptor(new CommandRedirectMethodDescriptorSupplier("redirectCommand"))
              .build();
        }
      }
    }
    return getRedirectCommandMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static CommandRedirectStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CommandRedirectStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CommandRedirectStub>() {
        @java.lang.Override
        public CommandRedirectStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CommandRedirectStub(channel, callOptions);
        }
      };
    return CommandRedirectStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static CommandRedirectBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CommandRedirectBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CommandRedirectBlockingStub>() {
        @java.lang.Override
        public CommandRedirectBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CommandRedirectBlockingStub(channel, callOptions);
        }
      };
    return CommandRedirectBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static CommandRedirectFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CommandRedirectFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CommandRedirectFutureStub>() {
        @java.lang.Override
        public CommandRedirectFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CommandRedirectFutureStub(channel, callOptions);
        }
      };
    return CommandRedirectFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * This RPC service only represents the redirection of commands from raft node to raft node.
   * Note that it doesn't represent the redirection of commands from client to raft node or to raft cluster.
   * </pre>
   */
  public interface AsyncService {

    /**
     */
    default void redirectCommand(rpc.RaftProto.RedirectRequest request,
        io.grpc.stub.StreamObserver<rpc.RaftProto.RedirectResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRedirectCommandMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service CommandRedirect.
   * <pre>
   * This RPC service only represents the redirection of commands from raft node to raft node.
   * Note that it doesn't represent the redirection of commands from client to raft node or to raft cluster.
   * </pre>
   */
  public static abstract class CommandRedirectImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return CommandRedirectGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service CommandRedirect.
   * <pre>
   * This RPC service only represents the redirection of commands from raft node to raft node.
   * Note that it doesn't represent the redirection of commands from client to raft node or to raft cluster.
   * </pre>
   */
  public static final class CommandRedirectStub
      extends io.grpc.stub.AbstractAsyncStub<CommandRedirectStub> {
    private CommandRedirectStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CommandRedirectStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CommandRedirectStub(channel, callOptions);
    }

    /**
     */
    public void redirectCommand(rpc.RaftProto.RedirectRequest request,
        io.grpc.stub.StreamObserver<rpc.RaftProto.RedirectResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRedirectCommandMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service CommandRedirect.
   * <pre>
   * This RPC service only represents the redirection of commands from raft node to raft node.
   * Note that it doesn't represent the redirection of commands from client to raft node or to raft cluster.
   * </pre>
   */
  public static final class CommandRedirectBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<CommandRedirectBlockingStub> {
    private CommandRedirectBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CommandRedirectBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CommandRedirectBlockingStub(channel, callOptions);
    }

    /**
     */
    public rpc.RaftProto.RedirectResponse redirectCommand(rpc.RaftProto.RedirectRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRedirectCommandMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service CommandRedirect.
   * <pre>
   * This RPC service only represents the redirection of commands from raft node to raft node.
   * Note that it doesn't represent the redirection of commands from client to raft node or to raft cluster.
   * </pre>
   */
  public static final class CommandRedirectFutureStub
      extends io.grpc.stub.AbstractFutureStub<CommandRedirectFutureStub> {
    private CommandRedirectFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CommandRedirectFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CommandRedirectFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<rpc.RaftProto.RedirectResponse> redirectCommand(
        rpc.RaftProto.RedirectRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRedirectCommandMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REDIRECT_COMMAND = 0;

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
        case METHODID_REDIRECT_COMMAND:
          serviceImpl.redirectCommand((rpc.RaftProto.RedirectRequest) request,
              (io.grpc.stub.StreamObserver<rpc.RaftProto.RedirectResponse>) responseObserver);
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
          getRedirectCommandMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              rpc.RaftProto.RedirectRequest,
              rpc.RaftProto.RedirectResponse>(
                service, METHODID_REDIRECT_COMMAND)))
        .build();
  }

  private static abstract class CommandRedirectBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    CommandRedirectBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return rpc.RaftProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("CommandRedirect");
    }
  }

  private static final class CommandRedirectFileDescriptorSupplier
      extends CommandRedirectBaseDescriptorSupplier {
    CommandRedirectFileDescriptorSupplier() {}
  }

  private static final class CommandRedirectMethodDescriptorSupplier
      extends CommandRedirectBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    CommandRedirectMethodDescriptorSupplier(String methodName) {
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
      synchronized (CommandRedirectGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new CommandRedirectFileDescriptorSupplier())
              .addMethod(getRedirectCommandMethod())
              .build();
        }
      }
    }
    return result;
  }
}
