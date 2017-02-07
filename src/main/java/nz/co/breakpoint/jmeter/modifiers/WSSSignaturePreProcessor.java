package nz.co.breakpoint.jmeter.modifiers;

import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.message.WSSecHeader;
import org.apache.wss4j.dom.message.WSSecSignature;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.signature.XMLSignature;
import org.w3c.dom.Document;

public class WSSSignaturePreProcessor extends AbstractWSSecurityPreProcessor { 

	private static final long serialVersionUID = 1L;

	static {
		keyIdentifiers.put("Binary Security Token",   		WSConstants.BST_DIRECT_REFERENCE);
		keyIdentifiers.put("Issuer Name and Serial Number",	WSConstants.ISSUER_SERIAL);
		keyIdentifiers.put("X509 Certificate",				WSConstants.X509_KEY_IDENTIFIER);
		keyIdentifiers.put("Subject Key Identifier",		WSConstants.SKI_KEY_IDENTIFIER);
		keyIdentifiers.put("Thumbprint SHA1 Identifier",	WSConstants.THUMBPRINT_IDENTIFIER);
	}
	
	static final String[] signatureCanonicalizations = new String[]{
		WSConstants.C14N_EXCL_OMIT_COMMENTS, 	WSConstants.C14N_EXCL_WITH_COMMENTS, 
		WSConstants.C14N_OMIT_COMMENTS, 		WSConstants.C14N_WITH_COMMENTS,
	};

	static final String[] signatureAlgorithms = new String[]{
		XMLSignature.ALGO_ID_SIGNATURE_RSA, 		XMLSignature.ALGO_ID_SIGNATURE_DSA, 
		XMLSignature.ALGO_ID_MAC_HMAC_SHA1, 		XMLSignature.ALGO_ID_MAC_HMAC_SHA256,
		XMLSignature.ALGO_ID_MAC_HMAC_SHA384, 		XMLSignature.ALGO_ID_MAC_HMAC_SHA512,
		XMLSignature.ALGO_ID_MAC_HMAC_RIPEMD160, 	XMLSignature.ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5,
		XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA1, 	XMLSignature.ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5,
		XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1, 	XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256,
		XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA384, 	XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA512,
		XMLSignature.ALGO_ID_SIGNATURE_RSA_RIPEMD160,
	};
	
	static final String[] digestAlgorithms = new String[]{
		MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1,			MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA256, 
		MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA384,		MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA512, 
		MessageDigestAlgorithm.ALGO_ID_DIGEST_RIPEMD160,	MessageDigestAlgorithm.ALGO_ID_DIGEST_NOT_RECOMMENDED_MD5,
	};
	
	public WSSSignaturePreProcessor() throws ParserConfigurationException {
		super();
		secBuilder = new WSSecSignature();
	}
	
	@Override
	protected Document build(Document document, Crypto crypto, WSSecHeader secHeader) throws WSSecurityException {
		document = ((WSSecSignature)secBuilder).build(document, crypto, secHeader);
		secBuilder = new WSSecSignature(); // discard old instance and create a fresh one for next iteration
		return document;
	}
	
	// Accessors
	public String getSignatureAlgorithm() {
		return ((WSSecSignature)secBuilder).getSignatureAlgorithm();
	}

	public void setSignatureAlgorithm(String signatureAlgorithm) {
		((WSSecSignature)secBuilder).setSignatureAlgorithm(signatureAlgorithm);
	}

	public String getSignatureCanonicalization() {
		return ((WSSecSignature)secBuilder).getSigCanonicalization();
	}

	public void setSignatureCanonicalization(String signatureCanonicalization) {
		((WSSecSignature)secBuilder).setSigCanonicalization(signatureCanonicalization);
	}

	public String getDigestAlgorithm() {
		return ((WSSecSignature)secBuilder).getDigestAlgo();
	}

	public void setDigestAlgorithm(String digestAlgorithm) {
		((WSSecSignature)secBuilder).setDigestAlgo(digestAlgorithm);
	}
	
	public boolean getUseSingleCertificate() {
		return ((WSSecSignature)secBuilder).isUseSingleCertificate();
	}

	public void setUseSingleCertificate(boolean useSingleCertificate) {
		((WSSecSignature)secBuilder).setUseSingleCertificate(useSingleCertificate);
	}
}