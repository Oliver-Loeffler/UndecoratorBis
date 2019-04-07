package insidefx.undecorator;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.matcher.control.LabeledMatchers;

import insidefx.undecorator.touch.TouchTheme;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

class UndecoratorTouchTest extends ApplicationTestTemplate {

	private Undecorator classUnderTest; 
	
	private String expectedDecoratedStageTitle = "TestFxStage with Touch Theme";
	
	private Stage stage;
	
	@Override
	public void start(Stage stage) {
		
		this.stage = stage;
		
		Thread.currentThread().setUncaughtExceptionHandler(this::catchUiError);
		Label label = new Label("TestFX - with Touch Theme");
		StackPane root = new StackPane(label);
		
		classUnderTest = new Undecorator(stage, root, new TouchTheme(), StageStyle.UNDECORATED);
		
		stage.setTitle(expectedDecoratedStageTitle);
		stage.setWidth(600);
		stage.setHeight(200);
		stage.setResizable(true);
		
		Scene scene = new Scene(classUnderTest);
		stage.setScene(scene);
		
		stage.show();
	}
	
	@Test
	void showAndMaximize() throws IOException {
		
		UndecoratorViewAdapter
			.using(this,classUnderTest)
			.captureImage(classUnderTest, "UndecoratorTouch.png")
			.maximizeOrRestore()
			.assertWindowSizeMaximized()
			.maximizeOrRestore()
			.assertWindowSizeRestored();

	}
		
	@Test
	void titleBinding() throws IOException {
		
		Label result = this.from(classUnderTest)
						      .lookup("#TitleLabel")
						      .query();
		
		FxAssert.verifyThat("#TitleLabel", 
				LabeledMatchers.hasText(expectedDecoratedStageTitle));
		
		assertTrue(result.textProperty()
					     .isEqualTo(stage.titleProperty())
					     .get());
				
	}
	
}
