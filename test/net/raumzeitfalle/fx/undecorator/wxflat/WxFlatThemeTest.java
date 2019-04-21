package net.raumzeitfalle.fx.undecorator.wxflat;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import insidefx.undecorator.Theme;
import insidefx.undecorator.Theme.ThemeProperty;

class WxFlatThemeTest {

	private Theme classUnderTest = new WxFlatTheme();
	
	@Test
	void decoration() {
		assertThat("WxFlatTheme.fxml", classUnderTest.getDecoration());		
	}

	@Test
	void styleSheet() {
		assertThat("WxFlatTheme.css", classUnderTest.getStylesheet());		
	}
	
	@Test
	void utilityDecoration() {
		assertThat("WxFlatThemeUtility.fxml", classUnderTest.getUtilityDecoration());		
	}
	
	@Test
	void utilityStyleSheet() {
		assertThat("WxFlatThemeUtility.css", classUnderTest.getUtilityStylesheet());		
	}

	@Test
	void loadAndGetProperties() throws IOException {
		
		Properties props = classUnderTest.loadAndGetProperties();
		assertNotNull(props);
		
		assertEquals(30, classUnderTest.getProperty(ThemeProperty.WINDOW_SHADOW_WIDTH, 0));
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
