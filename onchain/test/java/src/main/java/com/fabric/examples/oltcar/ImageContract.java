package com.fabric.examples.oltcar;

import com.owlike.genson.Genson;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Contract(
		name = "ImageContract",
		info = @Info(
				title = "Image contract",
				description = "Contract for storing and fetching images",
				version = "0.0.1",
				license = @License(
						name = "Apache 2.0 License",
						url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
				contact = @Contact(
						email = "krzysztof95.nowakowski@student.uj.edu.pl",
						name = "Krzysztof Nowakowski",
						url = "krzysztof95.nowakowski@student.uj.edu.pl")))

@Default
public final class ImageContract implements ContractInterface {
	
	private final Genson genson = new Genson();
	
	@Transaction()
	public Image addImage(final Context ctx, final String millis, final String base64Image) {
		ChaincodeStub stub = ctx.getStub();
		
		Image image = new Image(Long.parseLong(millis), base64Image);
		String imageState = genson.serialize(image);
		stub.putStringState(millis, imageState);
		
		return image;
	}
	
	public Image[] getImagesBetweenDates(final Context ctx, final String startDate, String endDate) {
		ChaincodeStub stub = ctx.getStub();
		
		List<Image> images = new ArrayList<>();
		final String startTime = String.valueOf(Timestamp.valueOf(startDate).getTime());
		final String endTime = String.valueOf(Timestamp.valueOf(endDate).getTime());
		
		final QueryResultsIterator<KeyValue> results = stub.getStateByRange(startTime, endTime);
		
		for (KeyValue result : results) {
			Image image = genson.deserialize(result.getStringValue(), Image.class);
			images.add(image);
		}
		
		return images.toArray(new Image[0]);
	}
}
