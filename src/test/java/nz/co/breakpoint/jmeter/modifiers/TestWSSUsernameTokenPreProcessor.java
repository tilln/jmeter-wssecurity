package nz.co.breakpoint.jmeter.modifiers;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;

import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.wss4j.dom.WSConstants;
import org.junit.Before;
import org.junit.Test;

public class TestWSSUsernameTokenPreProcessor extends TestWSSSecurityPreProcessorBase {
    private WSSUsernameTokenPreProcessor mod = null;
    private final static String USERNAME = "BREAKPOINT";
    private final static String PASSWORD = "JMETERROCKS";
    private JMeterContext context = null;
    
    @Before
    public void setUp() throws Exception {
        context = JMeterContextService.getContext();
    }

    @Test
    public void testAllUsernameTokenCombinations() throws Exception {
        for (String pt : WSSUsernameTokenPreProcessor.passwordTypes) {
            boolean isDigest = WSConstants.PASSWORD_DIGEST.equals(WSSUsernameTokenPreProcessor.passwordTypeMap.get(pt));
            boolean isText = WSConstants.PASSWORD_TEXT.equals(WSSUsernameTokenPreProcessor.passwordTypeMap.get(pt));
            for (boolean an : new boolean[]{true, false}) {
                for (boolean ac : new boolean[]{true, false}) {
                    mod = new WSSUsernameTokenPreProcessor();
                    mod.setThreadContext(context);
                    mod.setUsername(USERNAME);
                    mod.setPassword(PASSWORD);
                    mod.setPasswordType(pt);
                    mod.setAddNonce(an);
                    mod.setAddCreated(ac);
                    HTTPSamplerBase sampler = createHTTPSampler();
                    context.setCurrentSampler(sampler);
                    mod.process();
                    String content = SamplerPayloadAccessor.getPayload(sampler);
                    assertThat(content, containsString(":UsernameToken"));
                    assertThat(content, containsString(">"+USERNAME+"<"));
                    assertThat(content, containsString(":Password"));
                    assertThat(content, isText ? containsString(PASSWORD) 
                        : not(containsString(PASSWORD)));
                    assertThat(content, an || isDigest ? containsString(":Nonce") 
                        : not(containsString(":Nonce")));
                    assertThat(content, ac || isDigest ? containsString(":Created") 
                        : not(containsString(":Created")));
                }
            }
        }
    }
}
