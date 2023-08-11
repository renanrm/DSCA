/*
 * Copyright 2015 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package DS.SmartHome.Server;

import DS.SmartHome.SecurityService.SecurityService;
import DS.SmartHome.ThermostatService.ThermostatService;
import io.grpc.BindableService;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.Random;

/**
 * Server code based on the helloWorld App
 * https://raw.githubusercontent.com/grpc/grpc-java/master/examples/src/main/java/io/grpc/examples/helloworld/HelloWorldServer.java
 */
public class GrpcServer {
	private static final Logger logger = Logger.getLogger(GrpcServer.class.getName());
	static Random rand = new Random();
	private Server server;

	private void start() throws IOException, InterruptedException {
		/* Grpc will find a suitable port to run the services (see "0" below) */
		server = Grpc.newServerBuilderForPort(0, InsecureServerCredentials.create())
				.addService((BindableService) new ThermostatService()) // unary API
				.addService((BindableService) new SecurityService()) // client-stream and server-stream API
//				.addService((BindableService) new MyService3Impl())
				.build()
				.start();
		JmDnsServiceRegistration.register("_gRPCserver._tcp.local.", server.getPort());
		logger.info("Server started, listening on " + server.getPort());
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				// Use stderr here since the logger may have been reset by its JVM shutdown hook.
				System.err.println("*** shutting down gRPC server since JVM is shutting down");
				try {
					GrpcServer.this.stop();
				} catch (InterruptedException e) {
					e.printStackTrace(System.err);
				}
				System.err.println("*** server shut down");
			}
		});
	}

	private void stop() throws InterruptedException {
		if (server != null) {
			server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
		}
	  }

	/**
	 * Await termination on the main thread since the grpc library uses daemon threads.
	 */
	private void blockUntilShutdown() throws InterruptedException {
		if (server != null) {
			server.awaitTermination();
		}
	}

	/**
	 * Main launches the server from the command line.
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		final GrpcServer server = new GrpcServer();
		server.start();
		server.blockUntilShutdown();
	}




//
//
//	static class MyService3Impl extends MyService3Grpc.MyService3ImplBase {
//		/*
//		 * Bidirectional streaming RPCs where both sides send a sequence of messages using a read-write stream
//		 * https://grpc.io/docs/what-is-grpc/core-concepts/
//		 */
//		public StreamObserver<MsgRequest> function1Service3(StreamObserver<MsgReply> responseObserver) {
//			logger.info("Calling gRPC bi-directional streaming type (from the server side)");
//			return new StreamObserver<MsgRequest>() {
//				@Override
//				public void onNext(MsgRequest value) {
//					System.out.println("(Bi-di Server Received: " + value.getMessage() + ")");
//					MsgReply reply = MsgReply.newBuilder().setMessage("(Bi-di Server said: blah, blah, blah)").build();
//					responseObserver.onNext(reply);
//					reply = MsgReply.newBuilder().setMessage("(Bi-di Server said: blah, blah, blah)").build();
//					responseObserver.onNext(reply);
//				}
//
//				@Override
//				public void onError(Throwable t) {
//					t.printStackTrace();
//				}
//
//				@Override
//				public void onCompleted() {
//					responseObserver.onCompleted();
//				}
//			};
//		}
//	}
}