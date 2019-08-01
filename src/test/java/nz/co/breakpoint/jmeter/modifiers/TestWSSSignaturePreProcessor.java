package nz.co.breakpoint.jmeter.modifiers;

import static nz.co.breakpoint.jmeter.modifiers.CryptoWSSecurityPreProcessor.getKeyIdentifierLabelForType;
import static nz.co.breakpoint.jmeter.modifiers.WSSSignaturePreProcessor.*;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.wss4j.dom.WSConstants;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

public class TestWSSSignaturePreProcessor extends TestWSSSecurityPreProcessorBase {
    private WSSSignaturePreProcessor mod = null;

    @Before
    public void setUp() throws Exception {
        mod = new WSSSignaturePreProcessor();
        mod.setThreadContext(JMeterContextService.getContext());
        initCertSettings(mod);
    }

    @Test
    public void testAllSignatureCombinations() {
        for (String ki : keyIdentifiers) {
            for (String sc : signatureCanonicalizations) {
                for (String sa : signatureAlgorithms) {
                    for (String da : digestAlgorithms) {
                        for (boolean us : new boolean[]{true, false}) {
                            if (WSSSignaturePreProcessor.isSymmetricKeyIdentifier(ki) !=
                                    WSSSignaturePreProcessor.isSymmetricSignatureAlgorithm(sa)) continue;
                            initCertSettings(mod, sa);
                            mod.setKeyIdentifier(ki);
                            mod.setSignatureCanonicalization(sc);
                            mod.setSignatureAlgorithm(sa);
                            mod.setDigestAlgorithm(da);
                            mod.setUseSingleCertificate(us);
                            HTTPSamplerBase sampler = createHTTPSampler();
                            mod.process();
                            String signedContent = SamplerPayloadAccessor.getPayload(sampler);
                            assertThat(signedContent, containsString("\"http://www.w3.org/2000/09/xmldsig#\""));
                            assertThat(signedContent, containsString(sc));
                            assertThat(signedContent, containsString(sa));
                            assertThat(signedContent, containsString(da));
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testSignedBinarySecurityToken() {
        HTTPSamplerBase sampler = createHTTPSampler();
        SecurityPart bstPart = new SecurityPart();
        bstPart.setName(WSConstants.BINARY_TOKEN_LN);
        bstPart.setNamespace(WSConstants.WSSE_NS);
        mod.setPartsToSecure(Collections.singletonList(bstPart));
        mod.setKeyIdentifier(getKeyIdentifierLabelForType(WSConstants.BST_DIRECT_REFERENCE));
        mod.setSignatureCanonicalization(signatureCanonicalizations[0]);
        mod.setSignatureAlgorithm(signatureAlgorithms[0]);
        mod.setDigestAlgorithm(digestAlgorithms[0]);

        mod.process();
        String signedContent = SamplerPayloadAccessor.getPayload(sampler);
        assertThat(signedContent, containsString("<ds:Reference URI=\"#X509-"));
        assertThat(signedContent, containsString("<wsse:BinarySecurityToken "));
    }
}
