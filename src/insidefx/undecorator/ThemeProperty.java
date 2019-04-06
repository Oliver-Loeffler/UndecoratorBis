package insidefx.undecorator;

public enum ThemeProperty {
	
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
