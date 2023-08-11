package DS.SmartHome.SecurityService;

import io.grpc.stub.StreamObserver;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SecurityService extends SecurityServiceGrpc.SecurityServiceImplBase {
    private static final Logger logger = Logger.getLogger(SecurityService.class.getName());
    static Random rand = new Random();

        /*
		 * Server streaming RPCs where the client sends a request to the server and gets a stream to
		 * read a sequence of messages back
		 * https://grpc.io/docs/what-is-grpc/core-concepts/
		 */
	@Override
    public void getSecurityStatus(SecurityStatusRequest req, StreamObserver<SecurityStatusReply> responseObserver) {
        logger.info("Calling gRPC server streaming type (from the server side)");

        SecurityStatusReply reply = SecurityStatusReply.newBuilder()
                                                        .setSecuritySystemStatus(SecuritySystemStatus.newBuilder()
                                                                .setStatus("System running as expected")
                                                                .setDoorsLocked(true)
                                                                .setSystemArmed(true)
                                                                .build())
                                                        .build();
        responseObserver.onNext(reply);

        // send a stream (aka: bunch of messages) back to the client
        for (int i = 0; i < rand.nextInt(10); i++) {
            reply = SecurityStatusReply.newBuilder()
                        .setSecuritySystemStatus(SecuritySystemStatus.newBuilder()
                                .setStatus("System running as expected")
                                .setDoorsLocked(true)
                                .setSystemArmed(true)
                                .build())
                        .build();
            responseObserver.onNext(reply);
        }

        // no more messages
        responseObserver.onCompleted();
    }

    /*
	 * Client streaming RPCs where the client writes a sequence of messages and sends them to the server,
	 * again using a provided stream
	 * https://grpc.io/docs/what-is-grpc/core-concepts/
	 */
    @Override
    public StreamObserver<SecurityProtocolRequest> performSecurityProtocol(StreamObserver<SecurityProtocolReply> responseObserver) {
        logger.info("Calling gRPC client streaming type (from the server side)");
        return new StreamObserver<SecurityProtocolRequest>() {

            @Override
            public void onNext(SecurityProtocolRequest value) {
				System.out.println("Server received: " + value.getSecurityProtocol());
            }

            @Override
            public void onError(Throwable t) {
                logger.log(Level.SEVERE, "RPC failed: {0}", t.getMessage());
            }

            @Override
            public void onCompleted() {
                SecurityProtocolReply reply = SecurityProtocolReply.newBuilder().setResult("All changes were implemented").build();
                responseObserver.onNext(reply);
                responseObserver.onCompleted();
            }
        };
    }
}