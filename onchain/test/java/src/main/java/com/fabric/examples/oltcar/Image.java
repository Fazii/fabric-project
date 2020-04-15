package com.fabric.examples.oltcar;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

@DataType()
public final class Image {
	
	@Property()
	private final String millis;
	
	@Property()
	private final String base64Image;
	
	public String getMillis() {
		return millis;
	}
	
	public String getBase64Image() {
		return base64Image;
	}
	
	
	public Image(@JsonProperty("millis") final String millis, @JsonProperty("base64Image") final String base64Image) {
		this.millis = millis;
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
		
		return Objects.deepEquals(new String[]{getMillis(), getBase64Image()},
				new String[]{other.getMillis(), other.getBase64Image()});
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getMillis(), getBase64Image());
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode())
				+ " millis=" + getMillis() + ", base64Image=" + base64Image + "]";
	}
}
