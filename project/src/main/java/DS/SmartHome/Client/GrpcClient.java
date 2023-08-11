package DS.SmartHome.Client;

import DS.SmartHome.Server.JmDnsServiceDiscovery;
import DS.SmartHome.ThermostatService.Thermostat;
import DS.SmartHome.ThermostatService.ThermostatReply;
import DS.SmartHome.ThermostatService.ThermostatRequest;
import DS.SmartHome.ThermostatService.ThermostatServiceGrpc;
import io.grpc.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Client code based on the helloWorld App
 * https://raw.githubusercontent.com/grpc/grpc-java/master/examples/src/main/java/io/grpc/examples/helloworld/HelloWorldClient.java
 */
public class GrpcClient implements ActionListener {

	private JTextField reply1;
//	private JTextField entry2, reply2;
//	private JTextField entry3, reply3;
//	private JTextField entry4, reply4;

	  private static final Logger logger = Logger.getLogger(GrpcClient.class.getName());
	  private final ThermostatServiceGrpc.ThermostatServiceBlockingStub blockingStubThermostatService;
//	  private final MyService2Grpc.MyService2BlockingStub blockingStubMyService2;
//	  private final MyService2Grpc.MyService2Stub asyncService2Stub;
//	  private final MyService3Grpc.MyService3Stub asyncService3Stub;
	  static Random rand = new Random();

	  /** Construct client for accessing HelloWorld server using the existing channel. */
	  public GrpcClient(Channel channel) {
	    // The sync calls (blocking)
          blockingStubThermostatService = ThermostatServiceGrpc.newBlockingStub(channel);
//	    blockingStubMyService2 = MyService2Grpc.newBlockingStub(channel);
//	    //MyService3Grpc.newBlockingStub(channel);
//
//	    asyncService2Stub = MyService2Grpc.newStub(channel);	// async calls (for client-streaming)
//	    asyncService3Stub = MyService3Grpc.newStub(channel);	// async calls (for bidirectional streaming)
	  }



//	  // Run function1Service2 from Service2 (Server streaming RPC)
//	  public void clientSideFunction1Service2() {
//		  logger.info("Calling gRPC server streaming type (from the client side)");
//
//		  try {
//			  MsgRequest request = MsgRequest.newBuilder().setMessage("(Client said: How're you keeping?)").build();
//			  Iterator<MsgReply> reply = blockingStubMyService2
//					  .withDeadlineAfter(1, TimeUnit.SECONDS)
//					  .function1Service2(request);
//				while(reply.hasNext()) {
//					System.out.println(reply.next());		// print all messages from the server
//				}
//			  logger.info("End of server streaming");
//		  } catch (StatusRuntimeException e) {
//			  logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
//			  return;
//		  }
//	  }
//
//	  // Run function2Service2 from Service2 (Client streaming RPC)
//	  public void clientSideFunction2Service2() {
//		  logger.info("Calling gRPC client streaming type (from the client side)");
//
//		  StreamObserver<MsgReply> responseObserver = new StreamObserver<MsgReply>() {
//			  @Override
//			  public void onNext(MsgReply value) {
//				  System.out.println("Received: " + value.getMessage());
//			  }
//
//			  @Override
//			  public void onError(Throwable t) {
//				  t.printStackTrace();
//			  }
//
//			  @Override
//			  public void onCompleted() {
//				  System.out.println("Bye. Stream completed");
//			  }
//		  };
//
//		  // send a stream (aka: bunch of messages) back to the server
//		  StreamObserver<MsgRequest> requestObserver = asyncService2Stub.function2Service2(responseObserver);
//		  requestObserver.onNext(MsgRequest.newBuilder().setMessage("(Client said: How're you keeping?)").build());
//		  for (int i=0; i<rand.nextInt(10); i++){
//			  requestObserver.onNext(MsgRequest.newBuilder().setMessage("(Client said: blah, blah, blah)").build());
//		  }
//
//		  requestObserver.onCompleted();
//	  }
//
//	  // Run function1Service3 from Service3 (Bi-directional streaming RPC)
//	  public void clientSideFunction1Service3() {
//		  logger.info("Calling gRPC bi-directional streaming type (from the client side)");
//		  StreamObserver<MsgRequest> requestObserver =
//				  asyncService3Stub.function1Service3(new StreamObserver<MsgReply>() {
//					@Override
//					public void onNext(MsgReply value) {
//						System.out.println("Bidi Client Received: " + value.getMessage());
//					}
//
//					@Override
//					public void onError(Throwable t) {
//						t.printStackTrace();
//					}
//
//					@Override
//					public void onCompleted() {
//						System.out.println("Bidi Client said: Bye. Stream completed");
//					}
//				  });
//
//		  requestObserver.onNext(MsgRequest.newBuilder().setMessage("(Bidi Client said: What's the craic?)").build());
//		  for (int i=0; i<rand.nextInt(10); i++){
//			  requestObserver.onNext(MsgRequest.newBuilder().setMessage("(Bidi Client said: blah, blah, blah)").build());
//		  }
//
//		  requestObserver.onCompleted();
//	  }

	  /**
	   *
	   */
	  public static void main(String[] args) throws Exception {
		  String target;

		  // Service discovery part (Where's the gRPC server running?)
		  JmDnsServiceDiscovery jmDnsServiceDiscovery = new JmDnsServiceDiscovery();
		  JmDnsServiceDiscovery.find("_gRPCserver._tcp.local.");	// service name
		  do {
			  target = jmDnsServiceDiscovery.getLocGrpc();
			  System.out.println("jmDnsServiceDiscovery: " + target);
		  } while (target.length()<2);		// wait for the service to be discovered

		  ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create())
				  .build();
			  GrpcClient client = new GrpcClient(channel);
			  client.build();			// unary type
////			  client.clientSideFunction1Service2();			// server-streaming type
////			  client.clientSideFunction2Service2();			// client-streaming type
////			  client.clientSideFunction1Service3();			// bi-directional streaming type
	  }

	private JPanel getThermostatJPanel() {

		JPanel panel = new JPanel();

		BoxLayout boxlayout = new BoxLayout(panel, BoxLayout.X_AXIS);

		JButton button = new JButton("Invoke Thermostat");
		button.addActionListener(this);
		panel.add(button);
		panel.add(Box.createRigidArea(new Dimension(10, 0)));

		reply1 = new JTextField("", 30);
		reply1 .setEditable(false);
		panel.add(reply1);

		panel.setLayout(boxlayout);

		return panel;

	}

	// Run function1Service1 from Service1 (Unary RPC)
	private void build() {

		JFrame frame = new JFrame("Service Controller Sample");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set the panel to add buttons
		JPanel panel = new JPanel();

		// Set the BoxLayout to be X_AXIS: from left to right
		BoxLayout boxlayout = new BoxLayout(panel, BoxLayout.Y_AXIS);

		panel.setLayout(boxlayout);

		// Set border for the panel
		panel.setBorder(new EmptyBorder(new Insets(50, 100, 50, 100)));

		panel.add( getThermostatJPanel() );
//		panel.add( getService2JPanel() );
//		panel.add( getService3JPanel() );
//		panel.add( getService4JPanel() );

		// Set size for the frame
		frame.setSize(300, 300);

		// Set the window to be visible as the default to be false
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton button = (JButton)e.getSource();
		String label = button.getActionCommand();

		if (label.equals("Invoke Thermostat")) {
			System.out.println("Thermostat to be invoked ...");


			/*
			 *
			 */

			//preparing message to send
			ThermostatRequest entry1 = ThermostatRequest.newBuilder().setThermostat(Thermostat.newBuilder()
							.setTemperature("")
							.setHumidity("")
							.build())
					.build();

			//retreving reply from service
			ThermostatReply response = blockingStubThermostatService.getThermostat(entry1);

			reply1.setText( String.valueOf(response.getThermostat()) );

//		}else if (label.equals("Invoke Service 2")) {
//			System.out.println("service 2 to be invoked ...");
//
//
//			/*
//			 *
//			 */
//			ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50052).usePlaintext().build();
//			Service2Grpc.Service2BlockingStub blockingStub = Service2Grpc.newBlockingStub(channel);
//
//			//preparing message to send
//			ds.service2.RequestMessage request = ds.service2.RequestMessage.newBuilder().setText(entry2.getText()).build();
//
//			//retreving reply from service
//			ds.service2.ResponseMessage response = blockingStub.service2Do(request);
//
//			reply2.setText( String.valueOf( response.getLength()) );
//
//		}else if (label.equals("Invoke Service 3")) {
//			System.out.println("service 3 to be invoked ...");
//
//
//			/*
//			 *
//			 */
//			ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50053).usePlaintext().build();
//			Service3Grpc.Service3BlockingStub blockingStub = Service3Grpc.newBlockingStub(channel);
//
//			//preparing message to send
//			ds.service3.RequestMessage request = ds.service3.RequestMessage.newBuilder().setText(entry3.getText()).build();
//
//			//retreving reply from service
//			ds.service3.ResponseMessage response = blockingStub.service3Do(request);
//
//			reply3.setText( String.valueOf( response.getLength()) );
//
//		}else if (label.equals("Invoke Service 4")) {
//			System.out.println("service 4 to be invoked ...");
//
//
//			/*
//			 *
//			 */
//			ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50054).usePlaintext().build();
//			Service4Grpc.Service4BlockingStub blockingStub = Service4Grpc.newBlockingStub(channel);
//
//			//preparing message to send
//			ds.service4.RequestMessage request = ds.service4.RequestMessage.newBuilder().setText(entry4.getText()).build();
//
//			//retreving reply from service
//			ds.service4.ResponseMessage response = blockingStub.service4Do(request);
//
//			reply4.setText( String.valueOf( response.getLength()) );

		}
	}
}