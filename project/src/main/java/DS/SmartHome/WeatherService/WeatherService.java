package DS.SmartHome.WeatherService;

import io.grpc.stub.StreamObserver;

import java.util.Random;
import java.util.logging.Logger;

public class WeatherService extends WeatherServiceGrpc.WeatherServiceImplBase {
    private static final Logger logger = Logger.getLogger(WeatherService.class.getName());
    static Random rand = new Random();

    // server streaming rpc
    @Override
    public void getWeather(WeatherRequest req, StreamObserver<WeatherReply> responseObserver)  {
        logger.info("Calling gRPC server streaming type (from the server side)");
        WeatherReply reply = WeatherReply.newBuilder().setWeatherForecast(WeatherForecast.newBuilder()
                                                                                            .setHumidity(String.valueOf(new Random().nextInt(60 - 30 + 1) + 30) + "%")
                                                                                            .setTemperature(String.valueOf(new Random().nextInt(45)) + " Celsius")
                                                                                            .setRainChance(String.valueOf(new Random().nextInt(100)) + "% of chance to rain")
                                                                                         .build())
                                                                                .build();
        responseObserver.onNext(reply);

        // send a stream of messages to the client
        for (int i = 0; i < rand.nextInt(10); i++) {
            reply = WeatherReply.newBuilder().setWeatherForecast(WeatherForecast.newBuilder()
                            .setHumidity(String.valueOf(new Random().nextInt(60 - 30 + 1) + 30) + "%")
                            .setTemperature(String.valueOf(new Random().nextInt(45)) + " Celsius")
                            .setRainChance(String.valueOf(new Random().nextInt(100)) + "% of chance to rain")
                            .build())
                    .build();
            responseObserver.onNext(reply);
        }

        // no more messages
        responseObserver.onCompleted();
    }
}