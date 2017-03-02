package nz.co.breakpoint.jmeter.modifiers;

import javax.xml.parsers.ParserConfigurationException;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.message.WSSecBase;
import org.apache.wss4j.dom.message.WSSecHeader;
import org.apache.wss4j.dom.message.WSSecSignature;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.signature.XMLSignature;
import org.w3c.dom.Document;

public class WSSSignaturePreProcessor extends AbstractWSSecurityPreProcessor { 

	private static final long serialVersionUID = 1L;

	transient private WSSecSignature secBuilder;

	/* Currently supported attributes are listed below.
	 * The first value for each will be displayed in the GUI as default.
	 */
	static final String[] keyIdentifiers = new String[]{
		getKeyIdentifierLabelForType(WSConstants.BST_DIRECT_REFERENCE),
		getKeyIdentifierLabelForType(WSConstants.ISSUER_SERIAL),
		getKeyIdentifierLabelForType(WSConstants.X509_KEY_IDENTIFIER),
		getKeyIdentifierLabelForType(WSConstants.SKI_KEY_IDENTIFIER),
		getKeyIdentifierLabelForType(WSConstants.THUMBPRINT_IDENTIFIER),
		getKeyIdentifierLabelForType(WSConstants.KEY_VALUE),
	};

	static final String[] signatureCanonicalizations = new String[]{
		WSConstants.C14N_EXCL_OMIT_COMMENTS, WSConstants.C14N_EXCL_WITH_COMMENTS,
		WSConstants.C14N_OMIT_COMMENTS,      WSConstants.C14N_WITH_COMMENTS,
	};

	static final String[] signatureAlgorithms = new String[]{
		XMLSignature.ALGO_ID_SIGNATURE_RSA,           XMLSignature.ALGO_ID_SIGNATURE_DSA,
		XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA1,    XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA256,
		XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA384,  XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA512,
		XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1,      XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256,
		XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA384,    XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA512,
		XMLSignature.ALGO_ID_SIGNATURE_RSA_RIPEMD160,
	};

	static final String[] digestAlgorithms = new String[]{
		MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1,      MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA256,
		MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA384,    MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA512,
		MessageDigestAlgorithm.ALGO_ID_DIGEST_RIPEMD160,
	};

	public WSSSignaturePreProcessor() throws ParserConfigurationException {
		super();
	}

	@Override
	protected void initSecBuilder() {
		secBuilder = new WSSecSignature();
	}

	@Override
	protected WSSecBase getSecBuilder() {
		return secBuilder;
	}

	@Override
	protected Document build(Document document, Crypto crypto, WSSecHeader secHeader) throws WSSecurityException {
		return secBuilder.build(document, crypto, secHeader);
	}

	// Accessors
	public String getSignatureAlgorithm() {
		return secBuilder.getSignatureAlgorithm();
	}

	public void setSignatureAlgorithm(String signatureAlgorithm) {
		secBuilder.setSignatureAlgorithm(signatureAlgorithm);
	}

	public String getSignatureCanonicalization() {
		return secBuilder.getSigCanonicalization();
	}

	public void setSignatureCanonicalization(String signatureCanonicalization) {
		secBuilder.setSigCanonicalization(signatureCanonicalization);
	}

	public String getDigestAlgorithm() {
		return secBuilder.getDigestAlgo();
	}

	public void setDigestAlgorithm(String digestAlgorithm) {
		secBuilder.setDigestAlgo(digestAlgorithm);
	}

	public boolean getUseSingleCertificate() {
		return secBuilder.isUseSingleCertificate();
	}

	public void setUseSingleCertificate(boolean useSingleCertificate) {
		secBuilder.setUseSingleCertificate(useSingleCertificate);
	}
}
