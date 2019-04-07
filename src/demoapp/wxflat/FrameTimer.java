package demoapp.wxflat;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FrameTimer {
	private final long[] frameTimes = new long[100];
    private int frameTimeIndex = 0 ;
    private boolean arrayFilled = false ;
    
    private StringProperty fps = new SimpleStringProperty("");;
    
    private AnimationTimer timer;
    
    public FrameTimer() {   	
    	this.timer = new AnimationTimer() {
			
			@Override
			public void handle(long now) {
				long oldFrameTime = frameTimes[frameTimeIndex] ;
                frameTimes[frameTimeIndex] = now ;
                
                frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length ;
                if (frameTimeIndex == 0) {
                    arrayFilled = true ;
                }
                if (arrayFilled) {
                    long elapsedNanosPerFrame = (now - oldFrameTime) / frameTimes.length ;
                    double frameRate = 1_000_000_000.0 / elapsedNanosPerFrame ;
                    Platform.runLater(()->fps.set(String.format("(%.2f fps)", frameRate)));
                }
			}
		};
    	
    }
    
    public ReadOnlyStringProperty framesPerSecond() {
    	return this.fps;
    }
    
    public void start() {
    	this.timer.start();
    }
    
    
    
}
