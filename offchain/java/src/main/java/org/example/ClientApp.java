package org.example;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientApp {
	
	static {
		System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
	}
	
	public static void main(String[] args) throws Exception {
		Path walletPath = Paths.get("wallet");
		Wallet wallet = Wallet.createFileSystemWallet(walletPath);
		Path networkConfigPath = Paths.get(ClassLoader.getSystemResource("connection.yaml").toURI());
		
		Gateway.Builder builder = Gateway.createBuilder();
		builder.identity(wallet, "user1").networkConfig(networkConfigPath).discovery(true);
		
		try (Gateway gateway = builder.connect()) {
			
			Network network = gateway.getNetwork("mychannel");
			Contract contract = network.getContract("carcc");
			
			byte[] result;

			result = contract.evaluateTransaction("queryCarsHistory", "1234567");
			System.out.println(new String(result));
			
			contract.submitTransaction("addCarRepair", "1234567", "30000", "Wheel change 2");
			
			result = contract.evaluateTransaction("queryCarsHistory", "1234567");
			System.out.println(new String(result));
		}
	}
}
