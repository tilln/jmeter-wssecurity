package nz.co.breakpoint.jmeter.modifiers;

import org.apache.jmeter.processor.PreProcessor;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import java.io.StringReader;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.common.util.XMLUtils;
import org.apache.wss4j.dom.message.WSSecHeader;
import org.apache.wss4j.dom.message.WSSecBase;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Abstract base class for any preprocessor that creates/modifies a SOAP WSS header in the sampler payload.
 * Subclasses need to provide an actual wss4j WSSecBase instance and implement some wrapper methods.
 */
public abstract class AbstractWSSecurityPreProcessor extends AbstractTestElement implements PreProcessor, TestBean { 

	private static final Logger log = LoggingManager.getLoggerForClass();

	private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	static { factory.setNamespaceAware(true); }

	transient private final DocumentBuilder docBuilder; // Handles the XML document

	private String username, password;

	public AbstractWSSecurityPreProcessor() throws ParserConfigurationException {
		super();
		docBuilder = factory.newDocumentBuilder();
		initSecBuilder();
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
	protected abstract Document build(Document document, WSSecHeader secHeader)
		throws WSSecurityException;

	/* The main method that is called before the sampler.
	 * This will get, parse, secure (sign or encrypt) and then replace
	 * the sampler's payload.
	 */
	@Override
	public void process() {
		String xml = getSamplerPayload();
		if (xml == null) return;

		try {
			log.debug("Parsing xml payload");
			Document doc = docBuilder.parse(new InputSource(new StringReader(xml)));

			log.debug("Initializing WSS header");
			WSSecHeader secHeader = new WSSecHeader(doc);
			secHeader.insertSecurityHeader();

			log.debug("Building WSS header");
			doc = this.build(doc, secHeader); // Delegate in abstract method

			setSamplerPayload(XMLUtils.prettyDocumentToString(doc));
		}
		catch (Exception e) {
			log.error("Processing failed! ", e);
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

	// Accessors (protected for subclasses to use but hidden from bean introspector, or they would show on subclass GUIs) 
	protected String getUsername() {
		return username;
	}

	protected void setUsername(String username) {
		getSecBuilder().setUserInfo(this.username = username, password);
	}

	protected String getPassword() {
		return password;
	}

	protected void setPassword(String password) {
		getSecBuilder().setUserInfo(username, this.password = password);
	}
}
