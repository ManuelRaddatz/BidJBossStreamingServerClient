package org.cpm42.grpc;

import java.util.Observable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.cpm42.protobuf3.generated.StreamRequest;
import org.cpm42.protobuf3.generated.StreamResponse;
import org.cpm42.protobuf3.generated.StreamImageDataGrpc;
import org.cpm42.protobuf3.generated.StreamImageDataGrpc.StreamImageDataStub;
import org.jboss.logging.Logger;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

/**
 * The StreamClient is responsible for receiving the images from Server. It is the Observable in the Oberserver-Pattern.
 * If the client receives an image during onNext()-Method the Observers are notified.
 * @author Manuel Raddatz
 *
 */
public class StreamClient extends Observable {
	
	private static final Logger log = Logger.getLogger(StreamClient.class.getName());
	
	private ManagedChannel channel;
	private StreamImageDataStub asyncStub;
	private boolean channelShutdown = true;
	
	/**
	 * Constructor for Client. The provided parameter ar the Serverhost and the ServerPort
	 * @param String serverHost
	 * @param int serverPort 
	 */
	public StreamClient(String serverHost, int serverPort) throws Exception {
		this(ManagedChannelBuilder.forAddress(serverHost, serverPort).usePlaintext(true));
	}
	
	/**
	 * This Constructor is for intern use to Create the Channel for Connection between Server and Client
	 * Channel in grpc provides an abstraction of the transport layer
	 * @param channelBuilder
	 */
	private StreamClient(ManagedChannelBuilder<?> channelBuilder) throws Exception {
		channel= channelBuilder.build();
		channelShutdown= false;
		asyncStub = StreamImageDataGrpc.newStub(channel);
	}
	
	/**
	 * The shutdown Method terminates the connection between Server and Client. The Termination is down
	 * by the channel. No new Request can be send. 
	 * @throws InterruptedException
	 */
	public void shutdown() throws InterruptedException{
		channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
		channelShutdown = true;
	}
	
	/**
	 * The imageStream Method ist rpc-Function. It uses the created Stub for calling the Server an receiving the Stream.
	 * The Parameter StreamRequest regulates the behavior of the Stream.
	 * @param StreamRequest request
	 * @return CountDownLatch {@link CountDownLatch}
	 */
	public void imageStream(StreamRequest request){
		
		log.info("Calling imageStream-asnychStub...............");
		
		final CountDownLatch finish = new CountDownLatch(1);
		
		/**
		 *	The asyncStub is calling the rpc-Function with a new StreamObserver for the given Responses from the Server.
		 */
		if(channelShutdown==false){
			StreamObserver<StreamRequest> requestOberserver = asyncStub.streamImagaData(new StreamObserver<StreamResponse>() {

				/**
				 * The onNext Method is getting the imageDate, if it is send
				 */
				@Override
				public void onNext(StreamResponse response) {
					setChanged();
					notifyObservers(response.getImageData());
				}
				
				/**
				 * The onError Method is getting an Exception Object if it is thrown
				 */
				@Override
				public void onError(Throwable t) {
					log.error("Error while streaming ImageData from Server: "+t.getLocalizedMessage());
				}			
				
				/**
				 * The onCompleted is for ending the Stream and reduces the CountDownLatch by one
				 */
				@Override
				public void onCompleted() {
					log.info("Bidirectional Streaming has finished....");
					finish.countDown();
				}
			});
			
			/**
			 * This Block is for sending a StreamRequest to the Server.
			 */
			try{
				log.info("Sending a Streaming-Request to Server with State: "+request.getStreamState().name());
				requestOberserver.onNext(request);
			}catch (Exception ex) {
				log.error("Error sending requst to Server: "+ex.getLocalizedMessage());
				requestOberserver.onError(ex);
			}
			requestOberserver.onCompleted();
		}else{
			log.warn("Cannot open the Imagestream. The channelShutdown Attribut is set to true.");
		}

	}

}
