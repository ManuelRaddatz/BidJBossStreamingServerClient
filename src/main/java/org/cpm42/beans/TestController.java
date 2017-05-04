package org.cpm42.beans;

import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ValueChangeEvent;

import org.cpm42.grpc.StreamClientHandler;
import org.cpm42.websocket.ImageStreamEndpoint;
import org.jboss.logging.Logger;

@ManagedBean
@SessionScoped
public class TestController implements Serializable, Observer {

	private static final long serialVersionUID = 1L;
	
	private static final Logger log = Logger.getLogger(TestController.class.getName());
	
	private StreamClientHandler streamClientHandler;
	private boolean activatedStream = false;
	
	private String filter;
	
	/**
	 * Constructor for TestController. During call the StreamClientHandler will be initialized.
	 */
	public TestController() {
		log.info("Controller for start Streaming Action.....");
		try {
			streamClientHandler = new StreamClientHandler("localhost", 8990);
		} catch (Exception e) {
			log.error("Error while creating the StreamClientHandler and StreamClient with Host: localhost and Port: 8990.: "+e.getLocalizedMessage());
		}
	}
	
	/**
	 * Button Action for starting the Stream
	 */
	public void startStream(){
		if(streamClientHandler!=null && activatedStream==false){
			try {
				streamClientHandler.getStreamClient().addObserver(this);
				streamClientHandler.startStreamClient();
			} catch (Exception e) {
				log.error("Error while starting the streaming action: "+e.getLocalizedMessage());
			}
		}else{
			log.error("StreamClientHandler null or stream is activated.");
		}
	}
	

	public String getTestMessage(){
		return "Hello from JSF in Wildfly-Swarm";
	}
	

	@Override
	public synchronized void update(Observable o, Object arg) {
		//ImageStreamEndpoint.sendBinaryImageData(arg.toString());
		//the streampoint get the imagedata and the choosen filter or operators for opencv
		ImageStreamEndpoint.sendText(arg.toString());	
	}
	
	
	//////////////Getter + Setter + Filter ////////////
	
	public void filterValueChangeListener(ValueChangeEvent event){
		log.info("Filter hat sich geändert: "+event.getNewValue().toString());
	}
	

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}
	
}
