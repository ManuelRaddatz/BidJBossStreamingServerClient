package org.cpm42.grpc;

import org.cpm42.protobuf3.generated.StreamRequest;
import org.cpm42.protobuf3.generated.StreamState;

public class StreamClientHandler {

	private StreamClient streamClient;
	
	private StreamRequest startRequest;
	private StreamRequest stopRequest;
	
	private String serverHost;
	private int serverPort;
	
	/**
	 * Constructor for StreamClientHandler. It initialize the StreamClient-Object with the given Server and Port.
	 * @param String serverHost
	 * @param int serverPort
	 * @throws Exception 
	 */
	public StreamClientHandler(String serverHost, int serverPort) throws Exception {
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		streamClient = new StreamClient(serverHost, serverPort);
		startRequest = StreamRequest.newBuilder().setStreamState(StreamState.START).build();
		stopRequest = StreamRequest.newBuilder().setStreamState(StreamState.STOP).build();
	}
	
	/**
	 * This Method ist sending a stop Request to the Server for stopping the Stream.
	 */
	public void stopStreamClient(){
		if(this.streamClient!=null & this.stopRequest!=null){
			streamClient.imageStream(this.stopRequest);
		}
	}
	
	public void startStreamClient() throws Exception{
		if(this.streamClient!=null & this.startRequest!=null){
			streamClient.imageStream(startRequest);
		}else if(streamClient==null){
			this.streamClient = new StreamClient(serverHost, serverPort);
		}
	}

	/**
	 * Getter for Returning the actual StreamClient for adding to observer. The Streamclient is the observable.
	 * @return StreamClient
	 */
	public StreamClient getStreamClient() {
		return streamClient;
	}
}
