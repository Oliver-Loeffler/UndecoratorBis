package insidefx.undecorator.classic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import insidefx.undecorator.Theme;
import insidefx.undecorator.Theme.ThemeProperty;
import insidefx.undecorator.classic.ClassicTheme;

class ClassicThemeTest {
	
	private Theme classUnderTest = new ClassicTheme();
	
	@Test
	void decoration() {
		assertThat("ClassicTheme.fxml", classUnderTest.getDecoration());		
	}

	@Test
	void styleSheet() {
		assertThat("ClassicTheme.css", classUnderTest.getStylesheet());		
	}
	
	@Test
	void utilityDecoration() {
		assertThat("ClassicThemeUtility.fxml", classUnderTest.getUtilityDecoration());		
	}
	
	@Test
	void utilityStyleSheet() {
		assertThat("ClassicThemeUtility.css", classUnderTest.getUtilityStylesheet());		
	}

	@Test
	void loadAndGetProperties() throws IOException {
		
		Properties props = classUnderTest.loadAndGetProperties();
		assertNotNull(props);
		
		assertEquals(15, classUnderTest.getProperty(ThemeProperty.WINDOW_SHADOW_WIDTH, 0));
		assertEquals(7, classUnderTest.getProperty(ThemeProperty.WINDOW_RESIZE_PADDING, 0));
		
	}
	
	@Test
	void fullscreenEnabled() throws IOException {
		Properties props = classUnderTest.loadAndGetProperties();
		assertNotNull(props);
		
		assertTrue(classUnderTest.fullscreenEnabled());
	}
	
	void assertThat(String resourceName, URL source) {
		
		URL expected = getClass().getResource(resourceName);
		
		assertNotNull(source);
		assertNotNull(expected);
		assertEquals(expected, source);
		
	}

}