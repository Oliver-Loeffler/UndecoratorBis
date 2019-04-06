package skin.touch;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import insidefx.undecorator.Theme;
import insidefx.undecorator.ThemeProperty;

class TouchThemeTest {

	private Theme classUnderTest = new TouchTheme();
	
	@Test
	void decoration() {
		assertThat("TouchTheme.fxml", classUnderTest.getDecoration());		
	}

	@Test
	void styleSheet() {
		assertThat("TouchTheme.css", classUnderTest.getStylesheet());		
	}
	
	@Test
	void utilityDecoration() {
		assertThat("TouchThemeUtility.fxml", classUnderTest.getUtilityDecoration());		
	}
	
	@Test
	void utilityStyleSheet() {
		assertThat("TouchThemeUtility.css", classUnderTest.getUtilityStylesheet());		
	}
	
	@Test
	void loadAndGetProperties() throws IOException {
		
		Properties props = classUnderTest.loadAndGetProperties();
		assertNotNull(props);
		
		assertEquals(15, classUnderTest.getProperty(ThemeProperty.WINDOW_SHADOW_WIDTH, 0));
		assertEquals(7, classUnderTest.getProperty(ThemeProperty.WINDOW_RESIZE_PADDING, 0));
		
	}

	void assertThat(String resourceName, URL source) {
		
		URL expected = getClass().getResource(resourceName);
		
		assertNotNull(source);
		assertNotNull(expected);
		assertEquals(expected, source);
		
	}
}
