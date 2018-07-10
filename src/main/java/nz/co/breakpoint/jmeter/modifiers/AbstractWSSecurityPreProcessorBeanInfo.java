package nz.co.breakpoint.jmeter.modifiers;

import java.beans.PropertyDescriptor;
import org.apache.jmeter.testbeans.BeanInfoSupport;

public class AbstractWSSecurityPreProcessorBeanInfo extends BeanInfoSupport {

    public AbstractWSSecurityPreProcessorBeanInfo(Class<? extends AbstractWSSecurityPreProcessor> clazz) {
        super(clazz);

        createPropertyGroup("Header", new String[]{
            "actor", "mustUnderstand",
        });
        PropertyDescriptor p;

        p = property("actor");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");

        p = property("mustUnderstand");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, Boolean.TRUE);
    }
}
