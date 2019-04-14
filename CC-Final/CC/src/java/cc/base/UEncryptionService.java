package cc.base;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sun.misc.BASE64Encoder;
import sun.misc.CharacterEncoder;

public final class UEncryptionService {

    private static UEncryptionService instance;

    private static Object lock = new Object();

    private UEncryptionService()
    {
    }

    public String encrypt(String plaintext)
    {
	MessageDigest md = null;

	try {
	    md = MessageDigest.getInstance("SHA"); //step 2
	} catch(NoSuchAlgorithmException e) {
	    return null;
	}

	try {
	    md.update(plaintext.getBytes("UTF-8")); //step 3
	} catch(UnsupportedEncodingException e) {
	    return null;
	}

	byte raw[] = md.digest(); //step 4
	String hash = (new BASE64Encoder()).encode(raw); //step 5
	return hash; //step 6
    }

    public static UEncryptionService getInstance() //step 1
    {
	if(instance == null) {
	    synchronized(lock) {
		// Check if someone set the instance already
		// while the current thread was waiting on the lock
		if(instance == null)
		    instance = new UEncryptionService();
	    }
	}

	return instance;
    }
}
