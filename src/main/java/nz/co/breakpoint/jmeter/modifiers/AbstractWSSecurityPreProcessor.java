package nz.co.breakpoint.jmeter.modifiers;

import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.processor.PreProcessor;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
import org.apache.jmeter.protocol.jms.sampler.JMSSampler;
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
import org.w3c.dom.Document;

import static org.apache.wss4j.common.crypto.Merlin.PREFIX;
import static org.apache.wss4j.common.crypto.Merlin.KEYSTORE_FILE;
import static org.apache.wss4j.common.crypto.Merlin.KEYSTORE_PASSWORD;
import static org.apache.wss4j.common.crypto.Merlin.KEYSTORE_TYPE;

public abstract class AbstractWSSecurityPreProcessor extends AbstractTestElement implements PreProcessor, TestBean { 

	private static final Logger log = LoggingManager.getLoggerForClass();

	private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	static { factory.setNamespaceAware(true); }
	
	private final DocumentBuilder docBuilder; // Handles the XML document

	protected WSSecBase secBuilder; // Subclasses are to instantiate an appropriate instance
	
	private final Properties cryptoProps; // Holds configured attributes for crypto instance
	
	private String certAlias, certPassword; // Certificate alias and password (if private cert)
	
	static final Map<String, Integer> keyIdentifiers = new HashMap<String, Integer>(); // Subclasses are to populate an appropriate set
	
	private List<SecurityPart> partsToSecure; // Holds the names of XML elements to secure (e.g. SOAP Body)

	public AbstractWSSecurityPreProcessor() throws ParserConfigurationException {
		super();
		docBuilder = factory.newDocumentBuilder();
		cryptoProps = new Properties();
		cryptoProps.setProperty("org.apache.wss4j.crypto.provider", "org.apache.wss4j.common.crypto.Merlin");
		cryptoProps.setProperty(PREFIX+KEYSTORE_TYPE, "jks");
	}
	
	static String getKeyIdentifierLabelForType(int keyIdentifierType) {
		for (Map.Entry<String, Integer> id : keyIdentifiers.entrySet()) {
			if (id.getValue() == keyIdentifierType)
				return id.getKey();
		}
		return null;
	}
	
	protected Sampler getSampler() {
		return getThreadContext().getCurrentSampler();
	}
	
	/* The JMeter API lacks an interface for samplers that have a content or payload,
	 * so we have to use class specific methods.
	 */
	protected String getSamplerContent() {
		Sampler sampler = getSampler();

		if (sampler instanceof HTTPSamplerBase) {
			HTTPSamplerBase httpSampler = ((HTTPSamplerBase)sampler);
			if (!httpSampler.getPostBodyRaw()) {
				log.error("Raw post body required.");
				return null; 
			}
			return httpSampler.getArguments().getArgument(0).getValue(); 
		}
		else if (sampler instanceof JMSSampler) {
			return ((JMSSampler)sampler).getContent();
		}
		log.warn("Cannot get sampler content of "+sampler.getName());
		return null;
	}
	
	protected void setSamplerContent(String content) {
		Sampler sampler = getSampler();
				
		if (sampler instanceof HTTPSamplerBase) {
			((HTTPSamplerBase)sampler).getArguments().getArgument(0).setValue(content);
		}
		else if (sampler instanceof JMSSampler) {
			((JMSSampler)sampler).setContent(content);
		}
		else {
			log.warn("Cannot set sampler content of "+sampler.getName());
		}
	}
	
	/* The main method that is called before the sampler.
	 * This will get, parse, secure (sign or encrypt) and then replace 
	 * the sampler's payload.
	 * A new crypto instance needs to be created for every iteration 
	 * as the config could contain variables which may change. 
	 */
	@Override
	public void process() {
		String xml = getSamplerContent();
		if (xml == null) return;

		try {
			Document doc = docBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
		
			WSSecHeader secHeader = new WSSecHeader(doc);
			secHeader.insertSecurityHeader();

			Crypto crypto = CryptoFactory.getInstance(cryptoProps);

			doc = this.build(doc, crypto, secHeader); // Delegate in abstract method

			setSamplerContent(XMLUtils.prettyDocumentToString(doc));
		}
		catch (Exception e) { 
			log.error(e.toString());
		}
	}
	
	// Subclasses are to implement the actual creation of the signature or encryption, 
	// as WSSecBase does not define a build method.
	protected abstract Document build(Document document, Crypto crypto, WSSecHeader secHeader) 
		throws WSSecurityException;
	
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
		secBuilder.setUserInfo(this.certAlias = certAlias, certPassword); 
	}

	public String getCertPassword() {
		return certPassword;
	}

	public void setCertPassword(String certPassword) {
		secBuilder.setUserInfo(certAlias, this.certPassword = certPassword); 
	}
	
	public String getKeyIdentifier() {
		return getKeyIdentifierLabelForType(secBuilder.getKeyIdentifierType());
	}

	public void setKeyIdentifier(String keyIdentifier) {
		secBuilder.setKeyIdentifierType(keyIdentifiers.get(keyIdentifier));
	}

	public List<SecurityPart> getPartsToSecure() {
		return partsToSecure;
	}
	
	public void setPartsToSecure(List<SecurityPart> partsToSecure) {
		this.partsToSecure = partsToSecure;
		secBuilder.getParts().clear();
		for (SecurityPart part : partsToSecure) {
			secBuilder.getParts().add(part.getPart());
		}
	}	
}