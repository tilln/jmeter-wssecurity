package nz.co.breakpoint.jmeter.modifiers;

import java.beans.PropertyDescriptor;
import java.util.ResourceBundle;
import org.apache.jmeter.testbeans.BeanInfoSupport;
import org.apache.jmeter.testbeans.gui.FileEditor;
import org.apache.jmeter.testbeans.gui.PasswordEditor;

import static org.apache.jmeter.util.JMeterUtils.getJMeterVersion;

public class AbstractWSSecurityTestElementBeanInfo extends BeanInfoSupport {

    public AbstractWSSecurityTestElementBeanInfo(Class<? extends AbstractWSSecurityTestElement> clazz) {
        super(clazz);
        // custom icon depends on fix https://github.com/apache/jmeter/pull/399
        if (getJMeterVersion().compareTo("5.1") >= 0) {
            setIcon("padlock.png");
        }
    }

    // Convenience method for localized headers of TableEditor columns
    protected String[] getTableHeadersWithDefaults(String resourceName, String[] defaults) {
        ResourceBundle rb = (ResourceBundle)getBeanDescriptor().getValue(RESOURCE_BUNDLE);
        return rb != null && rb.containsKey(resourceName) ? 
            rb.getString(resourceName).split("\\|") : 
            defaults;
    }

    protected void createCertificateProperties(boolean includeCertificateCredentials) {
        createPropertyGroup("Certificate", includeCertificateCredentials ?
            new String[]{ "keystoreFile", "keystorePassword", "certAlias", "certPassword"} :
            new String[]{ "keystoreFile", "keystorePassword" });
        PropertyDescriptor p;

        p = property("keystoreFile");
        p.setPropertyEditorClass(FileEditor.class);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");

        p = property("keystorePassword");
        p.setPropertyEditorClass(PasswordEditor.class);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");

        if (includeCertificateCredentials) {
            p = property("certAlias");
            p.setValue(NOT_UNDEFINED, Boolean.TRUE);
            p.setValue(DEFAULT, "");

            p = property("certPassword");
            p.setPropertyEditorClass(PasswordEditor.class);
            p.setValue(NOT_UNDEFINED, Boolean.TRUE);
            p.setValue(DEFAULT, "");
        }
    }
}
