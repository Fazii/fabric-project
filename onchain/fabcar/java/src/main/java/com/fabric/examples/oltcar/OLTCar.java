//package com.fabric.examples.oltcar;
//
//import com.owlike.genson.Genson;
//import org.hyperledger.fabric.contract.Context;
//import org.hyperledger.fabric.contract.ContractInterface;
//import org.hyperledger.fabric.contract.annotation.Contact;
//import org.hyperledger.fabric.contract.annotation.Contract;
//import org.hyperledger.fabric.contract.annotation.Default;
//import org.hyperledger.fabric.contract.annotation.Info;
//import org.hyperledger.fabric.contract.annotation.License;
//import org.hyperledger.fabric.contract.annotation.Transaction;
//import org.hyperledger.fabric.shim.ChaincodeStub;
//import org.hyperledger.fabric.shim.ledger.KeyModification;
//import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Contract(
//		name = "OLTCar",
//		info = @Info(
//				title = "OLTCar contract",
//				description = "OLT example contract",
//				version = "0.0.1",
//				license = @License(
//						name = "Apache 2.0 License",
//						url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
//				contact = @Contact(
//						email = "krzysztof95.nowakowski@student.uj.edu.pl",
//						name = "Krzysztof Nowakowski",
//						url = "krzysztof95.nowakowski@student.uj.edu.pl")))
//
//@Default
//public final class OLTCar implements ContractInterface {
//	
//	private final Genson genson = new Genson();
//	
//	@Transaction()
//	public Car addCarRepair(final Context ctx, final String vin, final Integer mileage, final String repairName) {
//		ChaincodeStub stub = ctx.getStub();
//		
//		Car car = new Car(mileage, repairName);
//		String carState = genson.serialize(car);
//		stub.putStringState(vin, carState);
//		
//		return car;
//	}
//	
//	@Transaction()
//	public Car[] queryCarsHistory(final Context ctx, final String vin) {
//		ChaincodeStub stub = ctx.getStub();
//		
//		List<Car> cars = new ArrayList<>();
//		
//		QueryResultsIterator<KeyModification> results = stub.getHistoryForKey(vin);
//		
//		for (KeyModification result : results) {
//			Car car = genson.deserialize(result.getStringValue(), Car.class);
//			cars.add(car);
//		}
//		
//		return cars.toArray(new Car[0]);
//	}
//}
