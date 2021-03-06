/* Copyright 2019 Oliver Loeffler. All rights reserved.
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
		this.tryLoadingProperties();
	}
	
	private URL asResource(String name) {
		return getClass().getResource(name);
	}

	public URL getStylesheet() { return asResource(stageCss); }

	public URL getDecoration() { return asResource(stageFxml); }  
	
	public URL getUtilityStylesheet() { return asResource(utilityStageCss); }

	public URL getUtilityDecoration() { return asResource(utilityStageFxml); }
	
	public boolean fullscreenEnabled() {
		return getProperty(ThemeProperty.FULLSCREEN_ENABLED, true);
	}
	
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
	
	public boolean getProperty(ThemeProperty key, boolean defaultValue) {
		String value = properties.getProperty(key.toString(), String.valueOf(defaultValue));
		return Boolean.parseBoolean(value);
	}
	
	public enum ThemeProperty {
		
		WINDOW_SHADOW_WIDTH("window-shadow-width"),
		
		WINDOW_RESIZE_PADDING("window-resize-padding"),
		
		WINDOW_ROUNDING_DELTA("window-rounding-delta"),
		
		FULLSCREEN_ENABLED("fullscreen-enabled");
		
		private final String key;
		
		private ThemeProperty(String propertyKey) {
			this.key = propertyKey;
		}
		
		@Override
		public String toString() {
			return this.key;
		}
	}
	
	public int getShadowWidth() {
		return getProperty(ThemeProperty.WINDOW_SHADOW_WIDTH, 15);
	}
	
	public int getWindowResizePadding() {
		return getProperty(ThemeProperty.WINDOW_RESIZE_PADDING, 7);
	}
	
	public double getWindowRoundingDelta() {
		return getProperty(ThemeProperty.WINDOW_ROUNDING_DELTA, 0);
	}
}

