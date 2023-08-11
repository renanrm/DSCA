package DS.SmartHome.ThermostatService;

import DS.SmartHome.Server.GrpcServer;
import io.grpc.stub.StreamObserver;

import java.util.Random;
import java.util.logging.Logger;

public class ThermostatService extends ThermostatServiceGrpc.ThermostatServiceImplBase {
    private static final Logger logger = Logger.getLogger(GrpcServer.class.getName());
    /*
     * Unary RPCs where the client sends a single request to the server and gets a single response back
     * https://grpc.io/docs/what-is-grpc/core-concepts/
     */

    @Override
    public void getThermostat(ThermostatRequest req, StreamObserver<ThermostatReply> responseObserver) {
        logger.info("Calling gRPC unary type (from the server side)");
        ThermostatReply reply = ThermostatReply.newBuilder().setThermostat(Thermostat.newBuilder()
                .setTemperature(String.valueOf(new Random().nextInt(30 - 15 + 1) + 15) + " Celsius")
                .setHumidity(String.valueOf(new Random().nextInt(60 - 30 + 1) + 30) + "%")
                .build())
                .build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}