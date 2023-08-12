package DS.SmartHome.Client;

import DS.SmartHome.SecurityService.*;
import DS.SmartHome.Server.JmDnsServiceDiscovery;
import DS.SmartHome.ThermostatService.Thermostat;
import DS.SmartHome.ThermostatService.ThermostatReply;
import DS.SmartHome.ThermostatService.ThermostatRequest;
import DS.SmartHome.ThermostatService.ThermostatServiceGrpc;
import DS.SmartHome.WeatherService.WeatherReply;
import DS.SmartHome.WeatherService.WeatherRequest;
import DS.SmartHome.WeatherService.WeatherService;
import DS.SmartHome.WeatherService.WeatherServiceGrpc;
import io.grpc.*;
import io.grpc.stub.StreamObserver;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
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

	  private static final Logger logger = Logger.getLogger(GrpcClient.class.getName());
	  private final ThermostatServiceGrpc.ThermostatServiceBlockingStub blockingStubThermostatService; // unary rpc
	  private final SecurityServiceGrpc.SecurityServiceBlockingStub blockingStubSecurityService; // server-stream rpc
	  private final WeatherServiceGrpc.WeatherServiceBlockingStub blockingStubWeatherService; // server-stream rpc
	  private final SecurityServiceGrpc.SecurityServiceStub asyncSecurityServiceStub; // client-stream rpc
	  static Random rand = new Random();

	  /** Construct client for accessing HelloWorld server using the existing channel. */
	  public GrpcClient(Channel channel) {
	    // The sync calls (blocking)
		  blockingStubThermostatService = ThermostatServiceGrpc.newBlockingStub(channel);
		  blockingStubSecurityService = SecurityServiceGrpc.newBlockingStub(channel);
		  blockingStubWeatherService = WeatherServiceGrpc.newBlockingStub(channel);

	      asyncSecurityServiceStub = SecurityServiceGrpc.newStub(channel);	// async calls (for client-streaming)

	  }

	  public static void main(String[] args) throws Exception {
		  String target;

		  // Service discovery part
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
		  client.clientSideGetSecurityStatus();			// server-streaming type
		  client.clientSidePerformSecurityProtocol();	// client-streaming type
		  client.clientSideGetWeather();			// server-streaming type
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

	// Run build from ThermostatService (Unary RPC)
	private void build() {

		JFrame frame = new JFrame("Smart Home Controller");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set the panel to add buttons
		JPanel panel = new JPanel();

		// Set the BoxLayout to be X_AXIS: from left to right
		BoxLayout boxlayout = new BoxLayout(panel, BoxLayout.Y_AXIS);

		panel.setLayout(boxlayout);

		// Set border for the panel
		panel.setBorder(new EmptyBorder(new Insets(50, 100, 50, 100)));

		panel.add( getThermostatJPanel() );

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

			// preparing message to send
			ThermostatRequest entry1 = ThermostatRequest.newBuilder().setThermostat(Thermostat.newBuilder()
							.setTemperature("")
							.setHumidity("")
							.build())
					.build();

			// retrieving reply from service
			ThermostatReply response = blockingStubThermostatService.getThermostat(entry1);

			reply1.setText( String.valueOf(response.getThermostat()) );

		}
	}

	// run GetSecurityStatus from SecurityService (Server streaming RPC)
	public void clientSideGetSecurityStatus(){
		logger.info("Calling gRPC server streaming type (from the client side)");

		try {
			SecurityStatusRequest request = SecurityStatusRequest.newBuilder().setRun(true).build();

			Iterator<SecurityStatusReply> reply = blockingStubSecurityService
					.withDeadlineAfter(1, TimeUnit.SECONDS)
					.getSecurityStatus(request);
			while(reply.hasNext()) {
				System.out.println(reply.next());		// print all messages from the server
			}
			logger.info("End of server streaming");
		} catch (StatusRuntimeException e) {
			logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
		}
	}

	public void clientSideGetWeather(){
		logger.info("Calling gRPC server streaming type (from the client side)");

		try {
			WeatherRequest request = WeatherRequest.newBuilder().setRun(true).build();

			Iterator<WeatherReply> reply = blockingStubWeatherService
					.withDeadlineAfter(1, TimeUnit.SECONDS)
					.getWeather(request);
			while(reply.hasNext()) {
				System.out.println(reply.next());		// print all messages from the server
			}
			logger.info("End of server streaming");
		} catch (StatusRuntimeException e) {
			logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
		}
	}

	  // run performSecurityProtocol from SecurityService (Client streaming RPC)
	  public void clientSidePerformSecurityProtocol() {
		  logger.info("Calling gRPC client streaming type (from the client side)");

		  StreamObserver<SecurityProtocolReply> responseObserver = new StreamObserver<SecurityProtocolReply>() {
			  @Override
			  public void onNext(SecurityProtocolReply value) {
				  System.out.println("Received: " + value.getResult());
			  }

			  @Override
			  public void onError(Throwable t) {
				  logger.log(Level.SEVERE, "RPC failed: {0}", t.getMessage());
			  }

			  @Override
			  public void onCompleted() {
				  System.out.println("Stream completed");
			  }
		  };

		  // send a stream of messages back to the server
		  StreamObserver<SecurityProtocolRequest> requestObserver = asyncSecurityServiceStub.performSecurityProtocol(responseObserver);
		  requestObserver.onNext(SecurityProtocolRequest.newBuilder().setSecurityProtocol(SecurityProtocol.newBuilder()
																										  .setArmSystem(true)
																										  .setLockAllDoors(true)
																										  .build())
																								  .build());
		  for (int i=0; i<rand.nextInt(10); i++){
			  requestObserver.onNext(SecurityProtocolRequest.newBuilder()
														    .setSecurityProtocol(SecurityProtocol.newBuilder()
																					  .setArmSystem(true)
																					  .setLockAllDoors(true)
																					  .setLockBackDoor(true)
																					  .setLockFrontDoor(true)
																					  .build())
														    .build());
		  }

		  requestObserver.onCompleted();
	  }

}