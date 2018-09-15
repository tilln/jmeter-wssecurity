package nz.co.breakpoint.jmeter.modifiers;

import java.util.ResourceBundle;
import org.apache.jmeter.testbeans.BeanInfoSupport;

public class AbstractWSSecurityTestElementBeanInfo extends BeanInfoSupport {

    public AbstractWSSecurityTestElementBeanInfo(Class<? extends AbstractWSSecurityTestElement> clazz) {
        super(clazz);
        // setIcon("padlock.png"); // FIXME somehow overwrites all other elements' disabled icon!
    }

    // Convenience method for localized headers of TableEditor columns
    protected String[] getTableHeadersWithDefaults(String resourceName, String[] defaults) {
        ResourceBundle rb = (ResourceBundle)getBeanDescriptor().getValue(RESOURCE_BUNDLE);
        return rb != null && rb.containsKey(resourceName) ? 
            rb.getString(resourceName).split("\\|") : 
            defaults;
    }
}
