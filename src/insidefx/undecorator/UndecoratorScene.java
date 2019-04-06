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

import java.util.Objects;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import skin.classic.ClassicTheme;

/**
 *
 * @author In-SideFX
 */
public class UndecoratorScene extends Scene {

    Undecorator undecorator;
       
    /**
     * Basic constructor with built-in behavior
     *
     * @param stage The main stage
     * @param root your UI to be displayed in the Stage
     */
    public UndecoratorScene(Stage stage, Region root) {
        this(stage, root, StageStyle.TRANSPARENT, new ClassicTheme());
    }
    
    public UndecoratorScene(Stage stage, Region root, StageStyle stageStyle) {
        this(stage, root, stageStyle, new ClassicTheme());
    }
    
    /**
     * Constructor which allows adding a custom theme.
     *
     * @param stage The main stage
     * @param root your UI to be displayed in the Stage
     * @param theme Custom theme to be added
     */
    public UndecoratorScene(Stage stage, Region root, Theme theme) {
        this(stage, root, StageStyle.TRANSPARENT, theme);
    }
    
    /**
     * UndecoratorScene constructor
     *
     * @param stage The main stage
     * @param root your UI to be displayed in the Stage
     * @param stageStyle could be StageStyle.UTILITY or StageStyle.TRANSPARENT
     * @param theme Your own Stage decoration theme or null to use the built-in one
     */
    public UndecoratorScene(Stage stage, Region root, StageStyle stageStyle, Theme theme) {
    	super(root);

    	Objects.requireNonNull(stageStyle, "stageStyle must not be null");
        Objects.requireNonNull(theme, "theme must not be null");
        Objects.requireNonNull(stage, "stage must not be null");
        
        this.undecorator = new Undecorator(stage, root, theme, stageStyle);        
        super.setRoot(undecorator);

        // Transparent scene and stage
        if (stage.getStyle() != StageStyle.TRANSPARENT) {
            stage.initStyle(StageStyle.TRANSPARENT);
        }
        super.setFill(Color.TRANSPARENT);

        // Default Accelerators
        undecorator.installAccelerators(this);

        // Forward pref and max size to main stage
        stage.setMinWidth(undecorator.getMinWidth());
        stage.setMinHeight(undecorator.getMinHeight());
        stage.setWidth(undecorator.getPrefWidth());
        stage.setHeight(undecorator.getPrefHeight());
    }

//    public void removeDefaultStylesheet() {
//        undecorator.getStylesheets().remove(STYLESHEET);
//        undecorator.getStylesheets().remove(STYLESHEET_UTILITY);
//    }

    public void addStylesheet(String css) {
        undecorator.getStylesheets().add(css);
    }

    public void setAsStageDraggable(Stage stage, Node node) {
        undecorator.setAsStageDraggable(stage, node);
    }

    public void setBackgroundStyle(String style) {
        undecorator.getShadowNode().setStyle(style);
    }

    public void setBackgroundOpacity(double opacity) {
        undecorator.getShadowNode().setOpacity(opacity);
    }

    public void setBackgroundPaint(Paint paint) {
        undecorator.removeDefaultBackgroundStyleClass();
        undecorator.getShadowNode().setFill(paint);
    }

    public Undecorator getUndecorator() {
        return undecorator;
    }

    public void setFadeInTransition() {
        undecorator.setFadeInTransition();
    }

    public void setFadeOutTransition() {
        undecorator.setFadeOutTransition();
    }

}
