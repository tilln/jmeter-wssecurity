package nz.co.breakpoint.jmeter.modifiers;

public class CryptoWSSecurityPostProcessorBeanInfo extends AbstractWSSecurityPostProcessorBeanInfo {

    public CryptoWSSecurityPostProcessorBeanInfo(Class<? extends CryptoWSSecurityPostProcessor> clazz) {
        super(clazz);

        createCertificateProperties();
    }
}
