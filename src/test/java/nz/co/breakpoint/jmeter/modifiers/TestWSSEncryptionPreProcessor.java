package nz.co.breakpoint.jmeter.modifiers;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class TestWSSEncryptionPreProcessor extends TestWSSSecurityPreProcessorBase {
	private WSSEncryptionPreProcessor mod = null;

	@Before
	public void setUp() throws Exception {
		context = JMeterContextService.getContext();
		mod = new WSSEncryptionPreProcessor();
		mod.setThreadContext(context);
		initCertSettings(mod);
	}

	@Test
	public void testDefaultEncrypyion() throws Exception {
		HTTPSamplerBase sampler = createHTTPSampler();
		context.setCurrentSampler(sampler);
		mod.process();
		String encryptedContent = SamplerPayloadAccessor.getPayload(sampler);
		assertThat(encryptedContent, containsString("Type=\"http://www.w3.org/2001/04/xmlenc#Content\""));
	}

	@Test @Ignore("Long running test with lots of log output")
	public void testAllEncryptionCombinations() throws Exception {
		for (String ki : WSSEncryptionPreProcessor.keyIdentifiers) {
			for (String ke : WSSEncryptionPreProcessor.keyEncryptionAlgorithms) {
				for (String se : WSSEncryptionPreProcessor.symmetricEncryptionAlgorithms) {
					for (boolean ek : new boolean[]{true, false}) {
						initCertSettings(mod);
						mod.setKeyIdentifier(ki);
						mod.setKeyEncryptionAlgorithm(ke);
						mod.setSymmetricEncryptionAlgorithm(se);
						mod.setCreateEncryptedKey(ek);
						HTTPSamplerBase sampler = createHTTPSampler();
						context.setCurrentSampler(sampler);
						mod.process();
						String encryptedContent = SamplerPayloadAccessor.getPayload(sampler);
						assertThat(encryptedContent, containsString("Type=\"http://www.w3.org/2001/04/xmlenc#Content\""));
						assertThat(encryptedContent, containsString(se));
					}
				}
			}
		}
	}
}
