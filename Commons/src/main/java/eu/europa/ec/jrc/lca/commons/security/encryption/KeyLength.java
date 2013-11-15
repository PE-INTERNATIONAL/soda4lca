package eu.europa.ec.jrc.lca.commons.security.encryption;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum KeyLength {
	_512(512), _1024(1024), _2048(2048);

	private int size;

	private KeyLength(int size) {
		this.size = size;
	}
	
	private static final Map<Integer, KeyLength> mapping = new HashMap<Integer, KeyLength>();
	static{
		for(KeyLength kl: EnumSet.<KeyLength>allOf(KeyLength.class)){
			mapping.put(kl.size, kl);
		}
	}
	
	public static KeyLength get(int i){
		return mapping.get(i);
	}

	public int getSize() {
		return size;
	}
	
}