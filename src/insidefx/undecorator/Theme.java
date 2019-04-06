package insidefx.undecorator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Theme {
	
	private String stageFxml;
	private String utilityStageFxml;
	private String stageCss;
	private String utilityStageCss;
	private String propertiesFileName;
	
	private final Properties properties;
	
	private final Logger logger;
	
	public Theme() {
		String baseName = getClass().getSimpleName();
		this.stageFxml = baseName + ".fxml";
		this.utilityStageFxml = baseName + "Utility.fxml";
		this.stageCss = baseName + ".css";
		this.utilityStageCss = baseName + "Utility.css";
		this.propertiesFileName = baseName + ".properties";
		this.properties = new Properties();
		this.logger = Logger.getLogger(getClass().getName());
	}
	
	private URL asResource(String name) {
		return getClass().getResource(name);
	}

	public URL getStylesheet() { return asResource(stageCss); }

	public URL getDecoration() { return asResource(stageFxml); }  
	
	public URL getUtilityStylesheet() { return asResource(utilityStageCss); }

	public URL getUtilityDecoration() { return asResource(utilityStageFxml); }
	
	protected void loadProperties() throws IOException {
		URL resource = asResource(propertiesFileName);
		try (InputStream inputStream = resource.openStream()){
			this.properties.load(inputStream);
		}
	}
	
	protected void tryLoadingProperties() {
		try {
			loadProperties();
		} catch (IOException e) {
			logger.log(Level.WARNING, "Cannot not load properties from: " + propertiesFileName);
		}
	}
	
	public Properties loadAndGetProperties() throws IOException {
		loadProperties();		
		return this.properties;
		
	}
	
	public int getProperty(ThemeProperty key, int defaultValue) {
		String value = properties.getProperty(key.toString(), String.valueOf(defaultValue));
		try {
			return Integer.parseInt(value);	
		} catch (NumberFormatException nfe) {
			return defaultValue;
		}
	}
	
	public static enum ThemeProperty {
		
		WINDOW_SHADOW_WIDTH("window-shadow-width"),
		
		WINDOW_RESIZE_PADDING("window-resize-padding");
		
		private final String key;
		
		private ThemeProperty(String propertyKey) {
			this.key = propertyKey;
		}
		
		@Override
		public String toString() {
			return this.key;
		}
	}
}

