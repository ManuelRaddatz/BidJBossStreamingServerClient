package org.cpm42.websocket;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.xml.bind.DatatypeConverter;

import org.jboss.logging.Logger;

@ServerEndpoint("/imageStream")
public class ImageStreamEndpoint implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static final Logger log = Logger.getLogger(ImageStreamEndpoint.class.getName());
	
	private static ArrayList<Session> sessions = new ArrayList<>();
	
	@OnOpen
	public void onOpen(Session session, EndpointConfig endpointConfig) {
		log.info("Connection is opened: "+session.getId());
		session.setMaxBinaryMessageBufferSize(1024*512);
		sessions.add(session);
	}
	
	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		log.info("Connction closed with ID: "+session.getId()+" REASON: "+closeReason.getReasonPhrase());
		sessions.remove(session);
	}
	
	@OnMessage
	public void onMessage(String message) {
		//empty function
	}
	
	/**
	 * Method for sending the Data from Controller-Bean
	 * @param msg
	 */
	public static void sendText(String msg){
		for(Session s : sessions){
			try{
				s.getAsyncRemote().sendText(msg);
			}catch(Exception e){
				log.error("Error while sending the StringImageData to client: "+e.getMessage());
			}
		}
	}
	
	/**
	 * Method for converting and sending the received base64String to view.
	 * @param base64String
	 */
	public static void sendBinaryImageData(String base64String){
		byte[] imageData = DatatypeConverter.parseBase64Binary(base64String);
		BufferedImage image;
		try {
			image = ImageIO.read(new ByteArrayInputStream(imageData));
			ImageIO.write(image, "png", new File("C:\\temp\\"+UUID.randomUUID().toString()+".png"));
		} catch (IOException e1) {
			log.error("Cannot read byteArray:  "+e1.getLocalizedMessage());
		}
		for(Session s : sessions){
			try{
				ByteBuffer buffer = ByteBuffer.wrap(imageData);
				s.getAsyncRemote().sendBinary(buffer);
			}catch(Exception e){
				log.error("Error while sending the data to client: "+e.getMessage());
			}
		}
	}
}
