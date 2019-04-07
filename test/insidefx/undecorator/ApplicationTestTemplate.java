package insidefx.undecorator;

import static org.junit.jupiter.api.Assertions.fail;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;

public class ApplicationTestTemplate extends ApplicationTest {
    
    private AtomicReference<Throwable> errorInUI = new AtomicReference<>(null);

    @BeforeEach
    public void resetUiError() {
        errorInUI.set(null);
    }
    
    @AfterEach
    public void checkForUiExceptions() throws Throwable {
        if (errorInUI.get() != null) {
            fail(errorInUI.get().getMessage());
        }
    }
    
    public void catchUiError(Thread thread, Throwable error) {
        this.errorInUI.set(error);
    }
    
    public void captureImage(Node put, String filename) throws IOException {
        BufferedImage bImage = SwingFXUtils.fromFXImage(capture(put).getImage(), null);
        Path imageRoot = createImageRootWhenNeeded();
        ImageIO.write(bImage, "png", imageRoot.resolve(filename).toFile());
    }

    private Path createImageRootWhenNeeded() throws IOException {
        String rootDir = "testfx";
        Path imageRoot = Paths.get(rootDir);
        if (!Files.exists(imageRoot)) {
            Files.createDirectories(imageRoot);    
        }
        return imageRoot;
    }   
}
