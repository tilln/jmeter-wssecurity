package nz.co.breakpoint.jmeter.modifiers;

import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

public class TestWSSSamlPreProcessorBeanInfo extends TestWSSSecurityPreProcessorBase{
    private WSSSamlPreProcessor mod = null;
    private final static String SAML_ASSERTION = "\t<Assertion AssertionID=\"e4746fd5bf565f4990bf86d58b9f295c\" IssueInstant=\"2018-06-06T15:16:21.248Z\" Issuer=\"urn:be:fgov:ehealth:sts:1_0\" MajorVersion=\"1\" MinorVersion=\"1\" xmlns=\"urn:oasis:names:tc:SAML:1.0:assertion\" xmlns:saml=\"urn:oasis:names:tc:SAML:1.0:assertion\" xmlns:samlp=\"urn:oasis:names:tc:SAML:1.0:protocol\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "\t\t\t\t<Conditions NotBefore=\"2018-06-11T11:19:57.966+0200\" NotOnOrAfter=\"2018-06-11T11:24:57.988+0200\"/>\n" +
            "\t\t\t\t<AuthenticationStatement AuthenticationInstant=\"2018-06-06T15:16:21.248Z\" AuthenticationMethod=\"urn:oasis:names:tc:SAML:1.0:am:X509-PKI\">\n" +
            "\t\t\t\t\t<Subject>\n" +
            "\t\t\t\t\t\t<NameIdentifier Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName\" NameQualifier=\"TEST\">C=BE, SERIALNUMBER=00000000000, SURNAME=XXXX, GIVENNAME=XXXX, CN=XXXX</NameIdentifier>\n" +
            "\t\t\t\t\t\t<SubjectConfirmation>\n" +
            "\t\t\t\t\t\t\t<ConfirmationMethod>urn:oasis:names:tc:SAML:1.0:cm:sender-vouches</ConfirmationMethod>\n" +
            "\t\t\t\t\t\t\t<SubjectConfirmationData>\n" +
            "\t\t\t\t\t\t\t\t<Assertion AssertionID=\"_bbd9fa8460bda3c92814d74bb5dc4966\" IssueInstant=\"2011-05-30T10:48:20.031Z\" Issuer=\"test\" MajorVersion=\"1\" MinorVersion=\"1\">\n" +
            "\t\t\t\t\t\t\t\t\t<Conditions NotBefore=\"2018-06-11T11:19:58.005+0200\" NotOnOrAfter=\"2018-06-11T11:24:58.023+0200\"/>\n" +
            "\t\t\t\t\t\t\t\t\t<AttributeStatement>\n" +
            "\t\t\t\t\t\t\t\t\t\t<Subject>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<NameIdentifier Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName\" NameQualifier=\"test\">test</NameIdentifier>\n" +
            "\t\t\t\t\t\t\t\t\t\t</Subject>\n" +
            "\t\t\t\t\t\t\t\t\t\t<Attribute AttributeName=\"urn:be:fgov:ehealth:1.0:certificateholder:person:ssin\" AttributeNamespace=\"urn:be:fgov:identification-namespace\">\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<AttributeValue>61120226570</AttributeValue>\n" +
            "\t\t\t\t\t\t\t\t\t\t</Attribute>\n" +
            "\t\t\t\t\t\t\t\t\t\t<Attribute AttributeName=\"urn:be:fgov:person:ssin\" AttributeNamespace=\"urn:be:fgov:identification-namespace\">\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<AttributeValue>61120226570</AttributeValue>\n" +
            "\t\t\t\t\t\t\t\t\t\t</Attribute>\n" +
            "\t\t\t\t\t\t\t\t\t</AttributeStatement>\n" +
            "\t\t\t\t\t\t\t\t</Assertion>\n" +
            "\t\t\t\t\t\t\t</SubjectConfirmationData>\n" +
            "\t\t\t\t\t\t</SubjectConfirmation>\n" +
            "\t\t\t\t\t</Subject>\n" +
            "\t\t\t\t</AuthenticationStatement>\n" +
            "\t\t\t\t<AttributeStatement>\n" +
            "\t\t\t\t\t<Subject>\n" +
            "\t\t\t\t\t\t<NameIdentifier Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName\" NameQualifier=\"O=Federal Government, OU=eHealth-platform Belgium, C=BE, CN=TRAIL eHealth CA TEST\">C=BE, SERIALNUMBER=00000000000, SURNAME=xxxx, GIVENNAME=xxx CN=xxx xxx</NameIdentifier>\n" +
            "\t\t\t\t\t</Subject>\n" +
            "\t\t\t\t\t<Attribute AttributeName=\"urn:anOtherAttribute\" AttributeNamespace=\"urn:be:fgov:identification-namespace\">\n" +
            "\t\t\t\t\t\t<AttributeValue>00000000000</AttributeValue>\n" +
            "\t\t\t\t\t</Attribute>\n" +
            "\t\t\t\t\t<Attribute AttributeName=\"urn:anAttribute\" AttributeNamespace=\"urn:be:fgov:identification-namespace\">\n" +
            "\t\t\t\t\t\t<AttributeValue>00000000000</AttributeValue>\n" +
            "\t\t\t\t\t</Attribute>\n" +
            "\t\t\t\t\t<Attribute AttributeName=\"urn:be:fgov:person:ssin:ehealth:1.0:doctor:nihii11\" AttributeNamespace=\"urn:be:fgov:certified-namespace:ehealth\">\n" +
            "\t\t\t\t\t\t<AttributeValue>00000000000</AttributeValue>\n" +
            "\t\t\t\t\t</Attribute>\n" +
            "\t\t\t\t\t<Attribute AttributeName=\"urn:be:fgov:person:ssin:doctor:boolean\" AttributeNamespace=\"urn:be:fgov:certified-namespace:ehealth\">\n" +
            "\t\t\t\t\t\t<AttributeValue>true</AttributeValue>\n" +
            "\t\t\t\t\t</Attribute>\n" +
            "\t\t\t\t</AttributeStatement>\n" +
            "\t\t\t</Assertion>";
    private JMeterContext context = null;

    @Before
    public void setUp() throws Exception {
        context = JMeterContextService.getContext();
        mod = new WSSSamlPreProcessor();
        mod.setThreadContext(context);
        mod.setSaml(SAML_ASSERTION);
    }

    @Test
    public void testTimestamp() throws Exception {
        HTTPSamplerBase sampler = createHTTPSampler();
        context.setCurrentSampler(sampler);
        mod.process();
        String content = SamplerPayloadAccessor.getPayload(sampler);
        assertThat(content, containsString("Assertion"));
    }

}