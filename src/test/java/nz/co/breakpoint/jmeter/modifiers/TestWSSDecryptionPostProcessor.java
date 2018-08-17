package nz.co.breakpoint.jmeter.modifiers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.containsString;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.jmeter.assertions.AssertionResult;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.util.JMeterUtils;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

public class TestWSSDecryptionPostProcessor extends TestWSSSecurityPreProcessorBase {
    private WSSDecryptionPostProcessor mod = null;
    private SampleResult result = null;

    @ClassRule
    public static final JMeterPropertiesResource props = new JMeterPropertiesResource();

    @Before
    public void setUp() throws Exception {
        context = JMeterContextService.getContext();
        mod = new WSSDecryptionPostProcessor();
        mod.setThreadContext(context);
        mod.setKeystoreFile("src/test/resources/keystore.jks");
        mod.setKeystorePassword("changeit");
        mod.setCertPassword("changeit");
        result = new SampleResult();
        result.setSuccessful(true);
    }

    @Test
    public void testDecryption() throws Exception {
        result.setResponseData(Files.readAllBytes(Paths.get("src/test/resources/encrypted-body.xml")));
        context.setPreviousResult(result);
        mod.process();
        String decrypted = result.getResponseDataAsString();
        assertThat(decrypted, containsString("<add xmlns=\"http://ws.apache.org/counter/counter_port_type\">"));
    }

    @Test
    public void testFailureOnWSSException() throws Exception {
        result.setResponseData("<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" >"
            +   "<SOAP-ENV:Header>"
            +       "<wsse:Security xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" >"
            +           "<ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" >"
            +               "<ds:KeyInfo />"
            +           "</ds:Signature>"
            +       "</wsse:Security>"
            +   "</SOAP-ENV:Header>"
            +   "<SOAP-ENV:Body />"
            +"</SOAP-ENV:Envelope>",
            "UTF-8");
        context.setPreviousResult(result);

        JMeterUtils.setProperty(AbstractWSSecurityPostProcessor.FAIL_ON_WSS_EXCEPTION, "false");
        mod.process();
        assertTrue(result.isSuccessful());
        assertEquals(0, result.getAssertionResults().length);

        JMeterUtils.setProperty(AbstractWSSecurityPostProcessor.FAIL_ON_WSS_EXCEPTION, "true");
        mod.process();
        assertFalse(result.isSuccessful());
        AssertionResult[] assertionResults = result.getAssertionResults();
        assertEquals(1, assertionResults.length);
        assertEquals("WSSecurityException", assertionResults[0].getName());
        assertTrue(assertionResults[0].isError());
        assertThat(assertionResults[0].getFailureMessage(), containsString("Any SIG_KEY_INFO MUST contain exactly one child element"));
    }

    @Test
    public void testNoFailureOnOtherException() throws Exception {
        JMeterUtils.setProperty(AbstractWSSecurityPostProcessor.FAIL_ON_WSS_EXCEPTION, "true");
        result.setResponseData("<dummy />", "UTF-8");
        context.setPreviousResult(result);

        mod.process();

        assertTrue(result.isSuccessful());
        AssertionResult[] assertionResults = result.getAssertionResults();
        assertEquals(0, assertionResults.length);
    }

    @Test
    public void testSignatureValidation() throws Exception {
        result.setResponseData(Files.readAllBytes(Paths.get("src/test/resources/signed-body.xml")));
        context.setPreviousResult(result);

        mod.process();

        assertTrue(result.isSuccessful());
        assertEquals(0, result.getAssertionResults().length);
    }
}
