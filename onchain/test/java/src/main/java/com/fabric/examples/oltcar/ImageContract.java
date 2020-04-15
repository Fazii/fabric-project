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
import org.hyperledger.fabric.shim.ledger.KeyModification;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

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
	public Image addImage(final Context ctx, final String id, final String timestamp, final String base64Image) {
		ChaincodeStub stub = ctx.getStub();
		
		Image image = new Image(id, timestamp, base64Image);
		String imageState = genson.serialize(image);
		stub.putStringState(id, imageState);
		
		return image;
	}
	
	@Transaction()
	public Image[] queryImageHistory(final Context ctx, final String id) {
		ChaincodeStub stub = ctx.getStub();
		
		List<Image> images = new ArrayList<>();
		
		QueryResultsIterator<KeyModification> results = stub.getHistoryForKey(id);
		
		for (KeyModification result : results) {
			Image image = genson.deserialize(result.getStringValue(), Image.class);
			images.add(image);
		}
		
		return images.toArray(new Image[0]);
	}
	
	@Transaction()
	public Image queryImage(final Context ctx, final String id) {
		ChaincodeStub stub = ctx.getStub();
		byte[] result = stub.getState(id);
		return genson.deserialize(result, Image.class);
	}
}
