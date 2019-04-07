package insidefx.undecorator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.testfx.api.FxAssert;
import org.testfx.api.FxRobotInterface;
import org.testfx.matcher.control.LabeledMatchers;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.stage.Screen;

final class UndecoratorViewAdapter {
	
	private final FxRobotInterface robot;
	
	private final Node nodeUnderTest;
	
	private double initialHeight = 0;
	
	private double initialWidth = 0;
	
	private double lastHeight = 0;
	
	private double lastWidth = 0;
		
	public static UndecoratorViewAdapter using(FxRobotInterface testRobot, Undecorator nodeUnderTest) {
		return new UndecoratorViewAdapter(testRobot, nodeUnderTest);
	}

	private UndecoratorViewAdapter(FxRobotInterface testRobot, Node nodeUnderTest) {
		this.robot = testRobot;
		this.nodeUnderTest = nodeUnderTest;
		getInitialDimensions(nodeUnderTest);
	}

	private void getInitialDimensions(Node nodeUnderTest) {
		this.initialWidth = nodeUnderTest.getScene().getWindow().getWidth();
		this.initialHeight = nodeUnderTest.getScene().getWindow().getHeight();
	}
	
	private void getLastDimensions(Node nodeUnderTest) {
		this.lastWidth = nodeUnderTest.getScene().getWindow().getWidth();
		this.lastHeight = nodeUnderTest.getScene().getWindow().getHeight();
	}

	private final FxRobotInterface getRobot() {
		return this.robot;
	}
	
	UndecoratorViewAdapter captureImage(String node, String filename) throws IOException {
		Node desiredNode = getRobot().lookup(node).query();
		return captureImage(desiredNode, filename);
	}
	
	UndecoratorViewAdapter captureImage(Node put, String filename) throws IOException {
		
		BufferedImage bImage = SwingFXUtils.fromFXImage(getRobot().capture(put).getImage(), null);
		Path imageRoot = createImageRootWhenNeeded();
		ImageIO.write(bImage, "png", imageRoot.resolve(filename).toFile());

		return this;
	}
	
	private Path createImageRootWhenNeeded() throws IOException {
		String rootDir = "testfx";
		Path imageRoot = Paths.get(rootDir);
		if (!Files.exists(imageRoot)) {
			Files.createDirectories(imageRoot);
		}
		return imageRoot;
	}

	public UndecoratorViewAdapter assertTitle(String title) {
		FxAssert.verifyThat("title", LabeledMatchers.hasText(title));
		return this;
	}

	public UndecoratorViewAdapter waitForMillis(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt(); 
		}
		return this;
	}
	
	public UndecoratorViewAdapter maximizeOrRestore() {
		
		Button maximize = getRobot().from(nodeUnderTest).lookup("#maximize").query();
		
		if (null != maximize) {
			getRobot().clickOn("#maximize", MouseButton.PRIMARY);
			getLastDimensions(nodeUnderTest);
		}
				
		return this;
	}

	public UndecoratorViewAdapter assertWindowSizeMaximized() {

		Rectangle2D visible = Screen.getPrimary().getVisualBounds();
		Rectangle2D actual = new Rectangle2D(0, 0, lastWidth, lastHeight);
		
		assertEquals(visible,actual);
		
		return this;
	}
	
	public UndecoratorViewAdapter assertWindowSizeRestored() {

		Rectangle2D expectedRestored = new Rectangle2D(0, 0, initialWidth, initialHeight);
		Rectangle2D actual = new Rectangle2D(0, 0, lastWidth, lastHeight);
		
		assertEquals(expectedRestored,actual);
		
		return this;
	}

	public UndecoratorViewAdapter assertNodeGraphicEquals(Node node) {
		// FxAssert.verifyThat(nodeQuery, nodeMatcher);
		fail("Not implemented yet");
		return this;
	}
}
