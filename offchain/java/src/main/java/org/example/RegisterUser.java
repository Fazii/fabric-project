package org.example;

import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallet.Identity;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.Properties;
import java.util.Set;

public class RegisterUser {
	
	static {
		System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
	}
	
	public static void registerUser() throws Exception {
		
		Properties props = new Properties();
		props.put("pemFile",
				"../../basic-network/crypto-config/peerOrganizations/org1.example.com/ca/ca.org1.example.com-cert.pem");
		props.put("allowAllHostNames", "true");
		HFCAClient caClient = HFCAClient.createNewInstance("http://localhost:7054", props);
		CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
		caClient.setCryptoSuite(cryptoSuite);
		
		Wallet wallet = Wallet.createFileSystemWallet(Paths.get("wallet"));
		
		boolean userExists = wallet.exists("user");
		if (userExists) {
			System.out.println("An identity for the user  already exists in the wallet");
			return;
		}
		
		userExists = wallet.exists("admin");
		if (!userExists) {
			System.out.println("\"admin\" needs to be enrolled and added to the wallet first");
			return;
		}
		
		Identity adminIdentity = wallet.get("admin");
		User admin = new User() {
			
			@Override
			public String getName() {
				return "admin";
			}
			
			@Override
			public Set<String> getRoles() {
				return null;
			}
			
			@Override
			public String getAccount() {
				return null;
			}
			
			@Override
			public String getAffiliation() {
				return "org1.department1";
			}
			
			@Override
			public Enrollment getEnrollment() {
				return new Enrollment() {
					
					@Override
					public PrivateKey getKey() {
						return adminIdentity.getPrivateKey();
					}
					
					@Override
					public String getCert() {
						return adminIdentity.getCertificate();
					}
				};
			}
			
			@Override
			public String getMspId() {
				return "Org1MSP";
			}
			
		};
		
		RegistrationRequest registrationRequest = new RegistrationRequest("user");
		registrationRequest.setAffiliation("org1.department1");
		registrationRequest.setEnrollmentID("user");
		String enrollmentSecret = caClient.register(registrationRequest, admin);
		Enrollment enrollment = caClient.enroll("user", enrollmentSecret);
		Identity user = Identity.createIdentity("Org1MSP", enrollment.getCert(), enrollment.getKey());
		wallet.put("user", user);
		System.out.println("Successfully enrolled user and imported it into the wallet");
	}
}
