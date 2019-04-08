package insidefx.undecorator;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.matcher.control.LabeledMatchers;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.raumzeitfalle.fx.undecorator.wxflat.WxFlatTheme;

class UndecoratorWxFlatTest extends ApplicationTestTemplate {

	private Undecorator classUnderTest; 
	
	private String expectedDecoratedStageTitle = "TestFxStage with WXFlat Theme";
	
	private Stage stage;
	
	@Override
	public void start(Stage stage) {
		
		this.stage = stage;
		
		Thread.currentThread().setUncaughtExceptionHandler(this::catchUiError);
		Label label = new Label("TestFX - with WXFlat Theme");
		StackPane root = new StackPane(label);
		
		classUnderTest = new Undecorator(stage, root, new WxFlatTheme(), StageStyle.UNDECORATED);
		
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
			.captureImage(classUnderTest, "UndecoratorWXFlat.png")
			.maximizeOrRestore()
			.assertWindowSizeMaximized()
			.maximizeOrRestore()
			.assertWindowSizeRestored();

	}
	
	@Test
	void maximizeIcon() {
		
		Function<Labeled,Node> mapping = l->l.getGraphic();
		
		UndecoratorViewAdapter
		    .using(this,classUnderTest)
		    .assertStageIsNotMaximized()
		    .assertChildNodeHasStyleClass("glyph-icon", "#maximize", mapping)
		    .assertChildNodeHasStyleClass("decoration-glyph-maximize", "#maximize", mapping)
		    .assertChildNodeHasNotStyleClass("decoration-button-restore", "#maximize", mapping)
		    .assertChildNodeHasNotStyleClass("decoration-button-maximize", "#maximize", mapping);

	}
	
	@Test
	void maximizeAndRestoreIconChangesProperly() throws IOException {
		
		Function<Labeled,Node> mapping = l->l.getGraphic();
		
		UndecoratorViewAdapter
		    .using(this,classUnderTest)
		    .maximizeOrRestore()
		    .assertStageIsMaximized()
		    .captureImage(classUnderTest, "UndecoratorWXFlatMaximized.png")
		    .assertChildNodeHasStyleClass("glyph-icon", "#maximize", mapping)
		    .assertChildNodeHasStyleClass("decoration-glyph-restore", "#maximize", mapping)
		    .assertChildNodeHasNotStyleClass("decoration-button-restore", "#maximize", mapping)
		    .assertChildNodeHasNotStyleClass("decoration-button-maximize", "#maximize", mapping);

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
