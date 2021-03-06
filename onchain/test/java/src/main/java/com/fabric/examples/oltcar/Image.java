package com.fabric.examples.oltcar;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

@DataType()
public final class Image {

	@Property()
	private final String timestamp;

	@Property()
	private final String base64Image;
	
	public String getTimestamp() {
		return timestamp;
	}
	
	public String getBase64Image() {
		return base64Image;
	}
	
	
	public Image(@JsonProperty("timestamp") final String timestamp, @JsonProperty("base64Image") final String base64Image) {
		this.timestamp = timestamp;
		this.base64Image = base64Image;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		
		Image other = (Image) obj;
		
		return Objects.deepEquals(new String[]{getTimestamp(), getBase64Image()},
				new String[]{other.getTimestamp(), other.getBase64Image()});
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getTimestamp(), getBase64Image());
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode())
				+ " timestamp=" + getTimestamp() + ", base64Image=" + base64Image + "]";
	}
}
