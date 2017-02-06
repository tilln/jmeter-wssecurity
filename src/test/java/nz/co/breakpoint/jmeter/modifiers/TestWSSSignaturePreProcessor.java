package nz.co.breakpoint.jmeter.modifiers;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.junit.Before;
import org.junit.Test;

public class TestWSSSignaturePreProcessor extends TestWSSSecurityPreProcessorBase {
	private WSSSignaturePreProcessor mod = null;

	@Before
	public void setUp() throws Exception {
		context = JMeterContextService.getContext();
		mod = new WSSSignaturePreProcessor();
		mod.setThreadContext(context);
		initCertSettings(mod);
	}

	@Test
	public void testDefaultSignature() throws Exception {
		HTTPSamplerBase sampler = createHTTPSampler();
		context.setCurrentSampler(sampler);
		mod.process();
		String signedContent = sampler.getArguments().getArgument(0).getValue();
		assertThat(signedContent, containsString("\"http://www.w3.org/2000/09/xmldsig#\""));
	}
}
