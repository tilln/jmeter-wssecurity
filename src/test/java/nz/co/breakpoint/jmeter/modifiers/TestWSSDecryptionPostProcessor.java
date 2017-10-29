package nz.co.breakpoint.jmeter.modifiers;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.junit.Before;
import org.junit.Test;

public class TestWSSDecryptionPostProcessor {
    private WSSDecryptionPostProcessor mod = null;
    private JMeterContext context = null;

    @Before
    public void setUp() throws Exception {
        context = JMeterContextService.getContext();
        mod = new WSSDecryptionPostProcessor();
        mod.setThreadContext(context);
        mod.setKeystoreFile("src/test/resources/keystore.jks");
        mod.setKeystorePassword("changeit");
        mod.setCertPassword("changeit");
    }

    @Test
    public void testDecryption() throws Exception {
        SampleResult result = new SampleResult();
        result.setResponseData(Files.readAllBytes(Paths.get("src/test/resources/encrypted-body.xml")));
        context.setPreviousResult(result);
        mod.process();
        String decrypted = result.getResponseDataAsString();
        assertThat(decrypted, containsString("<add xmlns=\"http://ws.apache.org/counter/counter_port_type\">"));
    }
}
