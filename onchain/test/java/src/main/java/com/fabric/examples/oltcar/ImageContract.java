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

import java.util.ArrayList;
import java.util.Iterator;
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
	
	@Transaction()
	public Image addImage(final Context ctx, final String timestamp, final String base64Image) {
		ChaincodeStub stub = ctx.getStub();
		
		Image image = new Image(timestamp, base64Image);
		String imageState = new Genson().serialize(image);
		stub.putStringState(timestamp, imageState);
		
		return image;
	}
	
	@Transaction()
	public Image[] getImagesBetweenDates(final Context ctx, final String startTimestamp, String endTimestamp) {
		ChaincodeStub stub = ctx.getStub();
		
		List<Image> images = new ArrayList<>();
		
		final QueryResultsIterator<KeyValue> results = stub.getStateByRange(startTimestamp, endTimestamp);

		Iterator<KeyValue> iter = results.iterator();
		while (iter.hasNext()) {
			final KeyValue next = iter.next();
			Image image = new Genson().deserialize(next.getValue(), Image.class);
			images.add(image);
		}
		try {
			results.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return images.toArray(new Image[0]);
	}
}
