package nz.co.breakpoint.jmeter.modifiers;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

import org.apache.jmeter.protocol.http.sampler.HTTPSamplerFactory;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.junit.Before;
import org.junit.Test;

public class TestWSSSecurityPreProcessorBase {
	protected JMeterContext context = null;
	
	protected static final String SAMPLE_SOAP_MSG = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" 
        + "<SOAP-ENV:Envelope "
        +   "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" "
        +   "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
        +   "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" 
        +   "<SOAP-ENV:Body>" 
        +       "<add xmlns=\"http://ws.apache.org/counter/counter_port_type\">" 
        +           "<value xmlns=\"\">15</value>" 
        +       "</add>" 
        +   "</SOAP-ENV:Body>" 
        + "</SOAP-ENV:Envelope>";	

	HTTPSamplerBase createHTTPSampler() {
		HTTPSamplerBase sampler = HTTPSamplerFactory.newInstance();
		sampler.addNonEncodedArgument("", SAMPLE_SOAP_MSG, "");
		sampler.setPostBodyRaw(true);
		return sampler;
	}
	
	void initCertSettings(AbstractWSSecurityPreProcessor mod) {
		mod.setKeystoreFile("src/test/resources/wss40.jks");
		mod.setKeystorePassword("security");
		mod.setCertAlias("wss40");
		mod.setCertPassword("security");
	}
}
