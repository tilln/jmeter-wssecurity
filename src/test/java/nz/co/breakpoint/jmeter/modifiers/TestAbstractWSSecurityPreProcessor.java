package nz.co.breakpoint.jmeter.modifiers;

import static org.junit.Assert.assertEquals;

import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
import org.apache.jmeter.protocol.jms.sampler.JMSSampler;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.junit.Before;
import org.junit.Test;

public class TestAbstractWSSecurityPreProcessor extends TestWSSSecurityPreProcessorBase {
	private WSSSignaturePreProcessor mod = null;

	@Before
	public void setUp() throws Exception {
		context = JMeterContextService.getContext();
		mod = new WSSSignaturePreProcessor();
		mod.setThreadContext(context);
		initCertSettings(mod);
	}

	@Test
	public void testGetPayloadOfOtherSampler() throws Exception {
		JMSSampler sampler = new JMSSampler();
		sampler.setContent(SAMPLE_SOAP_MSG);

		context = JMeterContextService.getContext();
		mod = new WSSSignaturePreProcessor();
		mod.setThreadContext(context);
		context.setCurrentSampler(sampler);
		
		String payload = mod.getSamplerPayload(); 
		assertEquals(SAMPLE_SOAP_MSG, payload);
	}

	@Test
	public void testSetPayloadOfOtherSampler() throws Exception {
		JMSSampler sampler = new JMSSampler();

		context = JMeterContextService.getContext();
		mod = new WSSSignaturePreProcessor();
		mod.setThreadContext(context);
		context.setCurrentSampler(sampler);
		
		mod.setSamplerPayload(SAMPLE_SOAP_MSG);
		String payload = sampler.getContent();
		assertEquals(SAMPLE_SOAP_MSG, payload);
	}
}
