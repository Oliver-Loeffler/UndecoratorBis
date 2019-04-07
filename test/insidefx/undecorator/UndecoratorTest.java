package insidefx.undecorator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ResourceBundle;

import org.junit.jupiter.api.Test;


class UndecoratorTest {
	
	@Test
	void loadResourceBundle() {
		
		ResourceBundle bundle = Undecorator.loadResourceBundle("localization");
		assertNotNull(bundle);
		
	}

}
