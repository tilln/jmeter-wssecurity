package nz.co.breakpoint.jmeter.modifiers;

import org.apache.jmeter.processor.PreProcessor;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.crypto.CryptoFactory;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.common.util.XMLUtils;
import org.apache.wss4j.dom.message.WSSecHeader;
import org.apache.wss4j.dom.message.WSSecBase;
import org.apache.wss4j.dom.WSConstants;
import org.w3c.dom.Document;

import static org.apache.wss4j.common.crypto.Merlin.PREFIX;
import static org.apache.wss4j.common.crypto.Merlin.KEYSTORE_FILE;
import static org.apache.wss4j.common.crypto.Merlin.KEYSTORE_PASSWORD;
import static org.apache.wss4j.common.crypto.Merlin.KEYSTORE_TYPE;

public abstract class AbstractWSSecurityPreProcessor extends AbstractTestElement implements PreProcessor, TestBean { 

	private static final Logger log = LoggingManager.getLoggerForClass();

	private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	static { factory.setNamespaceAware(true); }

	transient private final DocumentBuilder docBuilder; // Handles the XML document

	private final Properties cryptoProps; // Holds configured attributes for crypto instance

	private String certAlias, certPassword; // Certificate alias and password (if private cert)

	private List<SecurityPart> partsToSecure; // Holds the names of XML elements to secure (e.g. SOAP Body)

	static final Map<String, Integer> keyIdentifierMap = new HashMap<String, Integer>();
	static {
		keyIdentifierMap.put("Binary Security Token",         WSConstants.BST_DIRECT_REFERENCE);
		keyIdentifierMap.put("Issuer Name and Serial Number", WSConstants.ISSUER_SERIAL);
		keyIdentifierMap.put("X509 Certificate",              WSConstants.X509_KEY_IDENTIFIER);
		keyIdentifierMap.put("Subject Key Identifier",        WSConstants.SKI_KEY_IDENTIFIER);
		keyIdentifierMap.put("Thumbprint SHA1 Identifier",    WSConstants.THUMBPRINT_IDENTIFIER);
		keyIdentifierMap.put("Encrypted Key SHA1",            WSConstants.ENCRYPTED_KEY_SHA1_IDENTIFIER); // only for encryption (symmetric signature not implemented yet - would require UI fields for setSecretKey or setEncrKeySha1value)
		keyIdentifierMap.put("Custom Key Identifier",         WSConstants.CUSTOM_KEY_IDENTIFIER); // not implemented yet (requires UI fields for setCustomTokenId and setCustomTokenValueType)
		keyIdentifierMap.put("Key Value",                     WSConstants.KEY_VALUE); // only for signature
		keyIdentifierMap.put("Endpoint Key Identifier",       WSConstants.ENDPOINT_KEY_IDENTIFIER); // not supported by Merlin https://ws.apache.org/wss4j/apidocs/org/apache/wss4j/common/crypto/Merlin.html#getX509Certificates-org.apache.wss4j.common.crypto.CryptoType-
	}

	public AbstractWSSecurityPreProcessor() throws ParserConfigurationException {
		super();
		docBuilder = factory.newDocumentBuilder();
		cryptoProps = new Properties();
		cryptoProps.setProperty("org.apache.wss4j.crypto.provider", "org.apache.wss4j.common.crypto.Merlin");
		cryptoProps.setProperty(PREFIX+KEYSTORE_TYPE, "jks");
		initSecBuilder();
	}

	/* Reverse lookup for above keyIdentifierMap. Mainly used for populating the GUI dropdown.
	 */
	protected static String getKeyIdentifierLabelForType(int keyIdentifierType) {
		for (Map.Entry<String, Integer> id : keyIdentifierMap.entrySet()) {
			if (id.getValue() == keyIdentifierType)
				return id.getKey();
		}
		return null;
	}

	protected Sampler getSampler() {
		return getThreadContext().getCurrentSampler();
	}

	protected String getSamplerPayload() {
		log.debug("Getting sampler payload");
		return SamplerPayloadAccessor.getPayload(getSampler());
	}

	protected void setSamplerPayload(String payload) {
		log.debug("Setting sampler payload");
		SamplerPayloadAccessor.setPayload(getSampler(), payload);
	}

	/* Subclasses are to instantiate and initialize an appropriate instance.
	 */
	protected abstract WSSecBase getSecBuilder();
	protected abstract void initSecBuilder();

	/* Subclasses are to implement the actual creation of the signature or encryption,
	 * as WSSecBase does not define a build method.
	 */
	protected abstract Document build(Document document, Crypto crypto, WSSecHeader secHeader)
		throws WSSecurityException;

	/* The main method that is called before the sampler.
	 * This will get, parse, secure (sign or encrypt) and then replace
	 * the sampler's payload.
	 * A new crypto instance needs to be created for every iteration
	 * as the config could contain variables which may change.
	 */
	@Override
	public void process() {
		String xml = getSamplerPayload();
		if (xml == null) return;

		try {
			log.debug("Parsing xml payload");
			Document doc = docBuilder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));

			log.debug("Initializing WSS header");
			WSSecHeader secHeader = new WSSecHeader(doc);
			secHeader.insertSecurityHeader();

			log.debug("Getting crypto instance");
			Crypto crypto = CryptoFactory.getInstance(cryptoProps);

			log.debug("Building WSS header");
			doc = this.build(doc, crypto, secHeader); // Delegate in abstract method

			setSamplerPayload(XMLUtils.prettyDocumentToString(doc));
		}
		catch (Exception e) { 
			log.error(e.toString());
		}
		/* There is no cleanup method in the wss4j API, so we have to discard the old instance
		 * and create a fresh one during the next iteration.
		 * To do that at the beginning of PreProcessor.process() would be too late as the Bean properties are (re)applied
		 * immediately before, and passed through to the secBuilder instance.
		 * (@see https://github.com/apache/jmeter/blob/v3_1/src/core/org/apache/jmeter/threads/JMeterThread.java#L787)
		 * Therefore the only way to do this is *after* the instance is used.
		 */
		initSecBuilder();
	}

	// Accessors
	public String getKeystoreFile() {
		return cryptoProps.getProperty(PREFIX+KEYSTORE_FILE); 
	}

	public void setKeystoreFile(String keystoreFile) {
		cryptoProps.setProperty(PREFIX+KEYSTORE_FILE, keystoreFile); 
	}

	public String getKeystorePassword() {
		return cryptoProps.getProperty(PREFIX+KEYSTORE_PASSWORD);
	}

	public void setKeystorePassword(String keystorePassword) {
		cryptoProps.setProperty(PREFIX+KEYSTORE_PASSWORD, keystorePassword);
	}

	public String getCertAlias() {
		return certAlias;
	}

	public void setCertAlias(String certAlias) {
		getSecBuilder().setUserInfo(this.certAlias = certAlias, certPassword);
	}

	public String getCertPassword() {
		return certPassword;
	}

	public void setCertPassword(String certPassword) {
		getSecBuilder().setUserInfo(certAlias, this.certPassword = certPassword);
	}
	
	public String getKeyIdentifier() {
		return getKeyIdentifierLabelForType(getSecBuilder().getKeyIdentifierType());
	}

	public void setKeyIdentifier(String keyIdentifier) {
		getSecBuilder().setKeyIdentifierType(keyIdentifierMap.get(keyIdentifier));
	}

	public List<SecurityPart> getPartsToSecure() {
		return partsToSecure;
	}

	public void setPartsToSecure(List<SecurityPart> partsToSecure) {
		this.partsToSecure = partsToSecure;
		getSecBuilder().getParts().clear();
		for (SecurityPart part : partsToSecure) {
			getSecBuilder().getParts().add(part.getPart());
		}
	}	
}
