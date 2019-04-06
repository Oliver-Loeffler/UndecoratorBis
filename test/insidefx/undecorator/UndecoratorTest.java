package insidefx.undecorator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.jupiter.api.Test;

class UndecoratorTest {

	@Test
	void resourceBundleReading() {
		
		String name = (Undecorator.class.getPackage().getName()+".localization").replace('.', '/');
		ResourceBundle bundle = ResourceBundle.getBundle(name, Locale.getDefault(), this.getClass().getClassLoader());
		
		assertNotNull(bundle);
		
	}

}
