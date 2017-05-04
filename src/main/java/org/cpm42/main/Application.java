package org.cpm42.main;

import org.cpm42.grpc.StreamClient;
import org.cpm42.grpc.StreamClientHandler;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.asset.NamedAsset;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.undertow.WARArchive;
import org.yaml.snakeyaml.extensions.compactnotation.PackageCompactConstructor;

public class Application {

	public static void main(String[] args) throws Exception {
		
		Swarm swarm = new Swarm();
		
		WARArchive archive = ShrinkWrap.create(WARArchive.class);
		
		//archive.addClass(StreamClientHandler.class);
		//archive.addClass(StreamClient.class);
		//add grpc implementation to deployment
		archive.addPackage("org.cpm42.grpc");
		//add beans to Deployment
		archive.addPackage("org.cpm42.beans");
		//add websocket to Deployment
		archive.addPackage("org.cpm42.websocket");
		//add grpc-generated sources
		archive.addPackage("org.cpm42.protobuf3.generated");

		archive.staticContent("/");
		
		archive.addAsWebInfResource(new ClassLoaderAsset("WEB-INF/web.xml", Application.class.getClassLoader()), "web.xml");
		
		swarm.start();
		
		swarm.deploy(archive);
	}

}
