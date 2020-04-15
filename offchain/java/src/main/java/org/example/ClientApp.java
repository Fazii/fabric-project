package org.example;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientApp {
	
	private static String user = "user6";
	
	static {
		System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
		try {
			EnrollAdmin.enrollAdmin();
			RegisterUser.registerUser(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		Path walletPath = Paths.get("wallet");
		Wallet wallet = Wallet.createFileSystemWallet(walletPath);
		Path networkConfigPath = Paths.get(ClassLoader.getSystemResource("connection.yaml").toURI());
		
		Gateway.Builder builder = Gateway.createBuilder();
		builder.identity(wallet, user).networkConfig(networkConfigPath).discovery(true);
		
		try (Gateway gateway = builder.connect()) {
			
			Network network = gateway.getNetwork("mychannel");
			Contract contract = network.getContract("image");
			
			byte[] result;
			
			contract.submitTransaction("addImage", "1", "blabla--");
			
//			result = contract.evaluateTransaction("getImagesBetweenDates", "0", "1");
//			System.out.println(new String(result));
//			
			result = contract.evaluateTransaction("getImages");
			System.out.println(new String(result));
			
			result = contract.evaluateTransaction("getImageByMillis", "1");
			System.out.println(new String(result));
		}
	}
}
