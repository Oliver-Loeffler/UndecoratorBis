/*
 * Copyright 2014-2016 Arnaud Nouard. All rights reserved.
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

import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import insidefx.undecorator.Theme.ThemeProperty;
import insidefx.undecorator.classic.ClassicTheme;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * This class, with the UndecoratorController, is the central class for the decoration of Transparent Stages. The Stage
 * Undecorator TODO: manage Quit (main stage)
 *
 * Bugs (Mac only?): Accelerators + Fullscreen crashes JVM KeyCombination does not respect keyboard's locale. Multi
 * screen: On second screen JFX returns wrong value for MinY (300)
 */
public class Undecorator extends StackPane {

	private int shadowWidth = 15;
    private int savedShadowWidth = 15;
    
    private int resizePadding = 7;
    
    // private int feedbackStroke = 4;
    
    private double roundedDelta = 0;
    
    private final Logger logger;

    public static ResourceBundle LOC;
    
    private final StageStyle stageStyle;
    
    private boolean fullscreenEnabled = false;
    
    @FXML
    private Button menu;
    
    @FXML
    private Button close;
    
    @FXML
    private Button maximize;
    
    @FXML
    private Button minimize;
    
    @FXML
    private Button resize;
    
    @FXML
    private Button fullscreen;
    
    @FXML
    private Label title;
    
    @FXML
    private Pane decorationRoot;
    
    @FXML
    private ContextMenu contextMenu;
    
    @FXML
	private Pane titleanchor;
   
    private final MenuItem maximizeMenuItem;
    private final CheckMenuItem fullScreenMenuItem;
    
    private final Region clientArea;
    
    private Pane stageDecoration = null;
    private Rectangle shadowRectangle;
    private Pane glassPane;
    private Rectangle dockFeedback;
    private FadeTransition dockFadeTransition;
    private Stage dockFeedbackPopup;
    private Effect dsFocused;
    private Effect dsNotFocused;
    private UndecoratorController undecoratorController;
    private Rectangle backgroundRect;
    
    private final Stage stage;
    private final SimpleBooleanProperty maximizeProperty;
    private final SimpleBooleanProperty minimizeProperty;
    private final SimpleBooleanProperty closeProperty;
    private final SimpleBooleanProperty fullscreenProperty;
    
    private final String shadowBackgroundStyleClass = "decoration-shadow";
    
    private final String decorationBackgroundStyle = "decoration-background";
    
    TranslateTransition fullscreenButtonTransition;
    
    final Rectangle internal = new Rectangle();
   
    final Rectangle external = new Rectangle();

    public SimpleBooleanProperty maximizeProperty() {
        return maximizeProperty;
    }

    public SimpleBooleanProperty minimizeProperty() {
        return minimizeProperty;
    }

    public SimpleBooleanProperty closeProperty() {
        return closeProperty;
    }

    public SimpleBooleanProperty fullscreenProperty() {
        return fullscreenProperty;
    }

    public Undecorator(Stage stage, Region root) {
        this(stage, root, new ClassicTheme(), StageStyle.UNDECORATED);
    }

    public Undecorator(Stage stage, Region clientArea, Theme theme, StageStyle stageStyle) {
    	this.stageStyle = Objects.requireNonNull(stageStyle, "stageStyle must not be null");
    	this.stage = Objects.requireNonNull(stage, "stage must not be null");
    	this.clientArea = Objects.requireNonNull(clientArea, "client area must not be null");
    	    	
    	this.logger = Logger.getLogger(Undecorator.class.getName());
    	
    	this.maximizeProperty = new SimpleBooleanProperty(false);
    	this.minimizeProperty = new SimpleBooleanProperty(false);
    	this.closeProperty = new SimpleBooleanProperty(false);
    	this.fullscreenProperty = new SimpleBooleanProperty(false);
    	
    	this.maximizeMenuItem = new MenuItem("Maximize");
    	this.fullScreenMenuItem = new CheckMenuItem("FullScreen");
    	this.applyTheme(theme);
    }
    
    private void applyTheme(Theme theme) {
		loadConfig(theme);
		decorateWith(theme);
	}

    protected void decorateWith(Theme theme) {       
        maximizeProperty.addListener((o,a,b)->getController().maximizeOrRestore());
		minimizeProperty.addListener((o,a,b)->getController().minimize());
		closeProperty.addListener((o,a,b)->getController().close());
		fullscreenProperty.addListener((o,a,b)->getController().setFullScreen(!stage.isFullScreen()));

        undecoratorController = new UndecoratorController(this);
        
        installDropShadow();       
        loadTheme(theme);
        initDecoration();

        undecoratorController.setStageResizableWith(stage, decorationRoot, resizePadding, shadowWidth);
        undecoratorController.setAsStageDraggable(stage, clientArea);
        undecoratorController.setAsStageDraggable(stage, titleanchor);
        
        // If not resizable (quick fix)
        if (fullscreen
                != null) {
            fullscreen.setVisible(stage.isResizable());
        }
        if (resize != null) {
            resize.setVisible(stage.isResizable());
        }
        if (maximize
                != null) {
            maximize.setVisible(stage.isResizable());
        }
        if (minimize
                != null && !stage.isResizable()) {
            AnchorPane.setRightAnchor(minimize, 34d);
        }

        // Glass Pane
        glassPane = new Pane();

        glassPane.setMouseTransparent(true);
        buildDockFeedbackStage();

        title.getStyleClass().add("undecorator-label-titlebar");
        shadowRectangle.getStyleClass().add(shadowBackgroundStyleClass);
//        resizeRect.getStyleClass().add(resizeStyleClass);
        // Do not intercept mouse events on stage's shadow
        shadowRectangle.setMouseTransparent(true);

        // Is it possible to apply an effect without affecting decendent?
        super.setStyle("-fx-background-color:transparent;");
        // Or this:
//        super.setStyle("-fx-background-color:transparent;-fx-border-color:white;-fx-border-radius:30;-fx-border-width:1;-fx-border-insets:"+SHADOW_WIDTH+";");
//        super.setEffect(dsFocused);
//          super.getChildren().addAll(clientArea,stageDecoration, glassPane);

        backgroundRect = new Rectangle();
        backgroundRect.getStyleClass().add(decorationBackgroundStyle);
        backgroundRect.setMouseTransparent(true);

        // Add all layers
        super.getChildren().addAll(shadowRectangle, backgroundRect, clientArea, stageDecoration, glassPane);
//        super.getChildren().addAll(shadowRectangle, backgroundRect);

        
        /*
         * Focused stage
         */
        stage.focusedProperty().addListener((o,a,b)->setShadowFocused(b));
        
        
        /*
         * Fullscreen
         */
        if (fullscreen
                != null) {
//            fullscreen.setOnMouseEntered(new EventHandler<MouseEvent>() {
//                @Override
//                public void handle(MouseEvent t) {
//                    if (stage.isFullScreen()) {
//                        fullscreen.setOpacity(1);
//                    }
//                }
//            });
//
//            fullscreen.setOnMouseExited(new EventHandler<MouseEvent>() {
//                @Override
//                public void handle(MouseEvent t) {
//                    if (stage.isFullScreen()) {
//                        fullscreen.setOpacity(0.4);
//                    }
//                }
//            });

        	stage.fullScreenProperty().addListener(this::onFullScreenToggle);
        	
        }

        computeAllSizes();
    }
    
    private void onFullScreenToggle(ObservableValue<? extends Boolean> ov, Boolean t, Boolean fullscreenState) {
    	if (fullscreenEnabled) {
    		toggleFullscreen(ov, t, fullscreenState);
    	}
    }
    
    private void toggleFullscreen(ObservableValue<? extends Boolean> ov, Boolean t, Boolean fullscreenState) {
    	setShadow(!fullscreenState.booleanValue());
        fullScreenMenuItem.setSelected(fullscreenState.booleanValue());
        maximize.setVisible(!fullscreenState.booleanValue());
        minimize.setVisible(!fullscreenState.booleanValue());
        if (resize != null) {
            resize.setVisible(!fullscreenState.booleanValue());
        }
        if (fullscreenState.booleanValue()) {
            // String and icon
            fullscreen.getStyleClass().add("decoration-button-unfullscreen");
            fullscreen.setTooltip(new Tooltip(LOC.getString("Restore")));

            undecoratorController.saveFullScreenBounds();
            if (fullscreenButtonTransition != null) {
                fullscreenButtonTransition.stop();
            }
            // Animate the fullscreen button
            fullscreenButtonTransition = new TranslateTransition();
            fullscreenButtonTransition.setDuration(Duration.millis(2000));
            fullscreenButtonTransition.setToX(80);
            fullscreenButtonTransition.setNode(fullscreen);
            fullscreenButtonTransition.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    fullscreenButtonTransition = null;
                }
            });
            fullscreenButtonTransition.play();
          //  fullscreen.setOpacity(0.2);
        } else {
            // String and icon
            fullscreen.getStyleClass().remove("decoration-button-unfullscreen");
            fullscreen.setTooltip(new Tooltip(LOC.getString("FullScreen")));

            undecoratorController.restoreFullScreenSavedBounds(stage);
          //  fullscreen.setOpacity(1);
            if (fullscreenButtonTransition != null) {
                fullscreenButtonTransition.stop();
            }
            // Animate the change
            fullscreenButtonTransition = new TranslateTransition();
            fullscreenButtonTransition.setDuration(Duration.millis(1000));
            fullscreenButtonTransition.setToX(0);
            fullscreenButtonTransition.setNode(fullscreen);
            fullscreenButtonTransition.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    fullscreenButtonTransition = null;
                }
            });

            fullscreenButtonTransition.play();
        }
    }

	private void installDropShadow() {
		// Focus drop shadows: radius, spread, offsets
        dsFocused = new DropShadow(BlurType.THREE_PASS_BOX, Color.BLACK, shadowWidth, 0.1, 0, 0);
        dsNotFocused = new DropShadow(BlurType.THREE_PASS_BOX, Color.DARKGREY, shadowWidth, 0, 0, 0);
        shadowRectangle = new Rectangle();
        shadowRectangle.layoutBoundsProperty().addListener(this::toggleShadowRectangle);
	}
	
	private void toggleShadowRectangle(ObservableValue<? extends Bounds> ov, Bounds oldBounds, Bounds newBounds) {
		if (shadowWidth != 0) {
            shadowRectangle.setVisible(true);
            setShadowClip(newBounds);
        } else {
            shadowRectangle.setVisible(false);
        }
	}

    /**
     * Compute the needed clip for stage's shadow border
     *
     * @param newBounds
     * @param shadowVisible
     */
    private void setShadowClip(Bounds newBounds) {
        external.relocate(newBounds.getMinX() - shadowWidth, newBounds.getMinY() - shadowWidth);
        internal.setX(shadowWidth);
        internal.setY(shadowWidth);
        internal.setWidth(newBounds.getWidth());
        internal.setHeight(newBounds.getHeight());
        internal.setArcWidth(shadowRectangle.getArcWidth());    // shadowRectangle CSS cannot be applied on this
        internal.setArcHeight(shadowRectangle.getArcHeight());

        external.setWidth(newBounds.getWidth() + shadowWidth * 2);
        external.setHeight(newBounds.getHeight() + shadowWidth * 2);
        Shape clip = Shape.subtract(external, internal);
        shadowRectangle.setClip(clip);

    }
    
    private boolean isUtilityStage() {
    	return StageStyle.UTILITY.equals(this.stageStyle);
    }
    
    
    private void loadTheme(Theme theme) {
    	if (isUtilityStage()) {
    		loadTheme(theme.getUtilityDecoration(), theme.getUtilityStylesheet());
    	} else {
    		loadTheme(theme.getDecoration(), theme.getStylesheet());
    	}
    }
    
    private void loadTheme(URL fxml, URL css) {
    	try {
    		FXMLLoader loader = new FXMLLoader(fxml);
    		loader.setController(this);
    		stageDecoration = (Pane) loader.load();
    		this.getStylesheets().add(css.toExternalForm());			
    	} catch (Exception error) {
    		
    		StringBuilder message = new StringBuilder("Cannot load theme! ");
    		message.append("Please verify content and location of FXML and CSS.");
    		message.append(System.lineSeparator());
    		message.append("FXML: ");
    		message.append(String.valueOf(fxml));
    		message.append(System.lineSeparator());
    		message.append("CSS: ");
    		message.append(String.valueOf(css));
    		
    		logger.log(Level.SEVERE, message.toString(), error);
    	}
    }

    /**
     * Install default accelerators
     *
     * @param scene
     */
    public void installAccelerators(Scene scene) {
        if (stage.isResizable()) {
            scene.getAccelerators()
            	.put(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN, KeyCombination.SHORTCUT_DOWN), ()->switchFullscreen());
        }
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.M, KeyCombination.SHORTCUT_DOWN), ()->switchMinimize());
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN), ()->switchClose());
    }

    /**
     * Init the minimum/pref/max sizes in order to be reflected in the primary stage
     */
    private void computeAllSizes() {
        double minWidth = minWidth(getWidth());
        setMinWidth(minWidth);
        double minHeight = minHeight(getHeight());
        setMinHeight(minHeight);

        double prefWidth = prefWidth(getWidth());
        setPrefWidth(prefWidth);
        setWidth(prefWidth);

        double prefHeight = prefHeight(getHeight());
        setPrefHeight(prefHeight);
        setHeight(prefHeight);

        double maxWidth = maxWidth(getWidth());
        if (maxWidth > minWidth) {
            setMaxWidth(maxWidth);
        }
        double maxHeight = maxHeight(getHeight());
        if (maxHeight > minHeight) {
            setMaxHeight(maxHeight);
        }
    }
    /*
     * The sizing is based on client area's bounds.
     */

    @Override
    protected double computePrefWidth(double d) {
        return clientArea.getPrefWidth() + shadowWidth * 2 + resizePadding * 2;
    }

    @Override
    protected double computePrefHeight(double d) {
        return clientArea.getPrefHeight() + shadowWidth * 2 + resizePadding * 2;
    }

    @Override
    protected double computeMaxHeight(double d) {
        double maxHeight = clientArea.getMaxHeight();
        if (maxHeight > 0) {
            return maxHeight + shadowWidth * 2 + resizePadding * 2;
        }
        return maxHeight;
    }

    @Override
    protected double computeMinHeight(double d) {
        double d2 = super.computeMinHeight(d);
        d2 += shadowWidth * 2 + resizePadding * 2;
        return d2;
    }

    @Override
    protected double computeMaxWidth(double d) {
        double maxWidth = clientArea.getMaxWidth();
        if (maxWidth > 0) {
            return maxWidth + shadowWidth * 2 + resizePadding * 2;
        }
        return maxWidth;
    }

    @Override
    protected double computeMinWidth(double d) {
        double d2 = super.computeMinWidth(d);
        d2 += shadowWidth * 2 + resizePadding * 2;
        return d2;
    }

    public StageStyle getStageStyle() {
        return stageStyle;
    }

    /**
     * Activate fade in transition on showing event
     */
    public void setFadeInTransition() {
        super.setOpacity(0);
        stage.showingProperty().addListener((ov,t,t1)->{
                if (t1.booleanValue()) {
                    FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), Undecorator.this);
                    fadeTransition.setToValue(1);
                    fadeTransition.play();
                }});
    }

    /**
     * Launch the fade out transition. Must be invoked when the application/window is supposed to be closed
     */
    public void setFadeOutTransition() {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), Undecorator.this);
        fadeTransition.setToValue(0);
        fadeTransition.play();
        fadeTransition.setOnFinished(actionEvent->{
                stage.hide();
                if (dockFeedbackPopup != null && dockFeedbackPopup.isShowing()) {
                    dockFeedbackPopup.hide();
                }
            });
    }

    public void removeDefaultBackgroundStyleClass() {
        shadowRectangle.getStyleClass().remove(shadowBackgroundStyleClass);
    }

    public Rectangle getShadowNode() {
        return shadowRectangle;
    }

    public Rectangle getBackgroundRectangle() {
        return backgroundRect;
    }

    /**
     * Background opacity
     *
     * @param opacity
     */
    public void setBackgroundOpacity(double opacity) {
        shadowRectangle.setOpacity(opacity);
    }

    /**
     * Manage buttons and menu items
     */
    private void initDecoration() {
        MenuItem minimizeMenuItem = null;
        // Menu
        contextMenu.setAutoHide(true);
        if (minimize != null) { // Utility Stage
            minimizeMenuItem = new MenuItem(LOC.getString("Minimize"));
            minimizeMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.M, KeyCombination.SHORTCUT_DOWN));

            minimizeMenuItem.setOnAction(actionEvent->switchMinimize());
            contextMenu.getItems().add(minimizeMenuItem);
        }
        if (maximize != null && stage.isResizable()) { // Utility Stage type
            maximizeMenuItem.setText(LOC.getString("Maximize"));
            maximizeMenuItem.setOnAction(actionEvent->{switchMaximize();contextMenu.hide();});
            contextMenu.getItems().addAll(maximizeMenuItem, new SeparatorMenuItem());
        }

        // Fullscreen
        if (stageStyle != StageStyle.UTILITY && stage.isResizable()) {
            fullScreenMenuItem.setText(LOC.getString("FullScreen"));
            fullScreenMenuItem.setOnAction(actionEvent -> switchFullscreen());
            fullScreenMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN, KeyCombination.SHORTCUT_DOWN));
            contextMenu.getItems().addAll(fullScreenMenuItem, new SeparatorMenuItem());
        }

        // Close
        MenuItem closeMenuItem = new MenuItem(LOC.getString("Close"));
        closeMenuItem.setOnAction(actionEvent->switchClose());
        closeMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN));

        contextMenu.getItems().add(closeMenuItem);
        menu.setOnMousePressed(mouseEvent-> {
                if (contextMenu.isShowing()) {
                    contextMenu.hide();
                } else {
                    contextMenu.show(menu, Side.BOTTOM, 0, 0);
                }
            });

        // Close button
        close.setTooltip(new Tooltip(LOC.getString("Close")));
        close.setOnAction(actionEvent->switchClose());

        // Maximize button
        maximizeProperty().addListener(this::maximizeRestoreFromContextMenu);

        if (maximize != null) { // Utility Stage
            maximize.setTooltip(new Tooltip(LOC.getString("Maximize")));
            maximize.setOnAction(actionEvent->switchMaximize());
        }
        
        if (fullscreen != null) { // Utility Stage
            fullscreen.setTooltip(new Tooltip(LOC.getString("FullScreen")));
            fullscreen.setOnAction(actionEvent->switchFullscreen());
        }

        // Minimize button
        if (minimize != null) { // Utility Stage
            minimize.setTooltip(new Tooltip(LOC.getString("Minimize")));
            minimize.setOnAction(actionEvent->switchMinimize());
        }
        
        title.textProperty().bind(stage.titleProperty());
    }
    
    protected ReadOnlyStringProperty titleProperty( ) {
    	return this.title.textProperty();
    }
    
    private void maximizeRestoreFromContextMenu(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
    	 if (!stage.isResizable()) {
             return;
         }
         Tooltip tooltip = maximize.getTooltip();
         if (tooltip.getText().equals(LOC.getString("Maximize"))) {
             tooltip.setText(LOC.getString("Restore"));
             maximizeMenuItem.setText(LOC.getString("Restore"));
             maximize.getStyleClass().add("decoration-button-restore");
             if (resize != null) {
                 resize.setVisible(false);
             }
             
			 Node icon = maximize.getGraphic();
			 if (null != icon) {
			     icon.getStyleClass().add("decoration-glyph-restore");
			     icon.getStyleClass().remove("decoration-glyph-maximize");
			 }

         } else {
             tooltip.setText(LOC.getString("Maximize"));
             maximizeMenuItem.setText(LOC.getString("Maximize"));
             maximize.getStyleClass().remove("decoration-button-restore");
             if (resize != null) {
                 resize.setVisible(true);
             }
             
             Node icon = maximize.getGraphic();
			 if (null != icon) {
				 icon.getStyleClass().add("decoration-glyph-maximize");
			     icon.getStyleClass().remove("decoration-glyph-restore");
			 }
         }
    }

    public void switchFullscreen() {
        // Invoke runLater even if it's on EDT: Crash apps on Mac
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                undecoratorController.setFullScreen(!stage.isFullScreen());
            }
        });
    }

    public void switchMinimize() {
        minimizeProperty().set(!minimizeProperty().get());
    }

    public void switchMaximize() {
        maximizeProperty().set(!maximizeProperty().get());
    }

    public void switchClose() {
        closeProperty().set(!closeProperty().get());
    }

    /**
     * Bridge to the controller to enable the specified node to drag the stage
     *
     * @param stage
     * @param node
     */
    public void setAsStageDraggable(Stage stage, Node node) {
        undecoratorController.setAsStageDraggable(stage, node);
    }

    /**
     * Switch the visibility of the window's drop shadow
     */
    protected void setShadow(boolean shadow) {
        // Already removed?
        if (!shadow && shadowRectangle.getEffect() == null) {
            return;
        }
        // From fullscreen to maximize case
        if (shadow && maximizeProperty.get()) {
            return;
        }
        if (!shadow) {
            shadowRectangle.setEffect(null);
            savedShadowWidth = shadowWidth;
            shadowWidth = 0;
        } else {
            shadowRectangle.setEffect(dsFocused);
            shadowWidth = savedShadowWidth;
        }
    }

    /**
     * Set on/off the stage shadow effect
     *
     * @param toggle
     */
    protected void setShadowFocused(boolean toggle) {
        // Do not change anything while maximized (in case of dialog closing for instance)
        if (stage.isFullScreen()) {
            return;
        }
        if (maximizeProperty().get()) {
            return;
        }
        if (toggle) {
            shadowRectangle.setEffect(dsFocused);
        } else {
            shadowRectangle.setEffect(dsNotFocused);
        }
    }

    /**
     * Set the layout of different layers of the stage
     */
    @Override
    public void layoutChildren() {
        Bounds b = super.getLayoutBounds();
        double w = b.getWidth();
        double h = b.getHeight();
        ObservableList<Node> list = super.getChildren();
        for (Node node : list) {
            if (node == shadowRectangle) {
                shadowRectangle.setWidth(w - shadowWidth * 2);
                shadowRectangle.setHeight(h - shadowWidth * 2);
                shadowRectangle.setX(shadowWidth);
                shadowRectangle.setY(shadowWidth);
            } else if (node == backgroundRect) {
                backgroundRect.setWidth(w - shadowWidth * 2);
                backgroundRect.setHeight(h - shadowWidth * 2);
                backgroundRect.setX(shadowWidth);
                backgroundRect.setY(shadowWidth);
            } else if (node == stageDecoration) {
                stageDecoration.resize(w - shadowWidth * 2 - roundedDelta * 2, h - shadowWidth * 2 - roundedDelta * 2);
                stageDecoration.setLayoutX(shadowWidth + roundedDelta);
                stageDecoration.setLayoutY(shadowWidth + roundedDelta);
            } //            else if (node == resizeRect) {
            //                resizeRect.setWidth(w - SHADOW_WIDTH * 2);
            //                resizeRect.setHeight(h - SHADOW_WIDTH * 2);
            //                resizeRect.setLayoutX(SHADOW_WIDTH);
            //                resizeRect.setLayoutY(SHADOW_WIDTH);
            //            } 
            else {
                node.resize(w - shadowWidth * 2 - roundedDelta * 2, h - shadowWidth * 2 - roundedDelta * 2);
                node.setLayoutX(shadowWidth + roundedDelta);
                node.setLayoutY(shadowWidth + roundedDelta);
//                node.resize(w - SHADOW_WIDTH * 2 - RESIZE_PADDING * 2, h - SHADOW_WIDTH * 2 - RESIZE_PADDING * 2);
//                node.setLayoutX(SHADOW_WIDTH + RESIZE_PADDING);
//                node.setLayoutY(SHADOW_WIDTH + RESIZE_PADDING);
            }
        }
    }

    public int getShadowBorderSize() {
        return shadowWidth * 2 + resizePadding * 2;
    }

    public UndecoratorController getController() {
        return undecoratorController;
    }

    public Stage getStage() {
        return stage;
    }

    protected Pane getGlassPane() {
        return glassPane;
    }

    public void addGlassPane(Node node) {
        glassPane.getChildren().add(node);
    }

    public void removeGlassPane(Node node) {
        glassPane.getChildren().remove(node);
    }

    /**
     * Returns the decoration (buttons...)
     *
     * @return
     */
    public Pane getStageDecorationNode() {
        return stageDecoration;
    }

    /**
     * Prepare Stage for dock feedback display
     */
    void buildDockFeedbackStage() {
        dockFeedbackPopup = new Stage(StageStyle.TRANSPARENT);
        dockFeedback = new Rectangle(0, 0, 100, 100);
        dockFeedback.setArcHeight(10);
        dockFeedback.setArcWidth(10);
        dockFeedback.setFill(Color.TRANSPARENT);
        dockFeedback.setStroke(Color.BLACK);
        dockFeedback.setStrokeWidth(2);
        dockFeedback.setCache(true);
        dockFeedback.setCacheHint(CacheHint.SPEED);
        dockFeedback.setEffect(new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));
        dockFeedback.setMouseTransparent(true);
        BorderPane borderpane = new BorderPane();
        borderpane.setStyle("-fx-background-color:transparent"); //J8
        borderpane.setCenter(dockFeedback);
        Scene scene = new Scene(borderpane);
        scene.setFill(Color.TRANSPARENT);
        dockFeedbackPopup.setScene(scene);
        dockFeedbackPopup.sizeToScene();
    }

    /**
     * Activate dock feedback on screen's bounds
     *
     * @param x
     * @param y
     */
    public void setDockFeedbackVisible(double x, double y, double width, double height) {
        dockFeedbackPopup.setX(x);
        dockFeedbackPopup.setY(y);

        dockFeedback.setX(shadowWidth);
        dockFeedback.setY(shadowWidth);
        dockFeedback.setHeight(height - shadowWidth * 2);
        dockFeedback.setWidth(width - shadowWidth * 2);

        dockFeedbackPopup.setWidth(width);
        dockFeedbackPopup.setHeight(height);

        dockFeedback.setOpacity(1);
        dockFeedbackPopup.show();

        dockFadeTransition = new FadeTransition();
        dockFadeTransition.setDuration(Duration.millis(200));
        dockFadeTransition.setNode(dockFeedback);
        dockFadeTransition.setFromValue(0);
        dockFadeTransition.setToValue(1);
        dockFadeTransition.setAutoReverse(true);
        dockFadeTransition.setCycleCount(3);

        dockFadeTransition.play();

    }

    public void setDockFeedbackInvisible() {
        if (dockFeedbackPopup.isShowing()) {
            dockFeedbackPopup.hide();
            if (dockFadeTransition != null) {
                dockFadeTransition.stop();
            }
        }
    }

    void loadConfig(Theme theme) {
        
    	theme.tryLoadingProperties();
    	
    	shadowWidth = theme.getProperty(ThemeProperty.WINDOW_SHADOW_WIDTH, 15);
    	resizePadding = theme.getProperty(ThemeProperty.WINDOW_RESIZE_PADDING, 7);
    	roundedDelta = theme.getProperty(ThemeProperty.WINDOW_ROUNDING_DELTA, 0);
    	fullscreenEnabled = theme.fullscreenEnabled();
    	
    	LOC = loadResourceBundle("localization");
    	
    	fullScreenMenuItem.setDisable(!fullscreenEnabled);
    	fullscreenProperty.set(fullscreenEnabled);

    }

	protected static final ResourceBundle loadResourceBundle(String bundleName) {
        return ResourceBundle.getBundle(
        		(Undecorator.class.getPackage().getName()+"."+bundleName).replace('.', '/'), 
        		Locale.getDefault());
	}

}
