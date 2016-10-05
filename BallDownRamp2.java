package ballDownRamp2;

import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.*;
import javafx.util.Duration;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Animation.Status;
import javafx.application.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;

public class BallDownRamp2 extends Application{

	double height = 550;
	double width = 1102;
	double blockSize = 40;
	
	double blockX;
	double blockY;
	double blockAx;
	double blockAy;
	
	double timeStart;
	double timeRunning;
	
	double mass;
	double grav;
	double theta;
	double mkf;
	double mkfMax;
	
	double topOfRamp;
	double endOfRamp;
	
	public static void main(String[] args){
		Application.launch(args);
	}//main
	
	public void start(Stage primaryStage){//1152, 648 -- Make Total Size, 20-24, Make Program that Poops
		primaryStage.setTitle("Block Down Ramp");
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		//grid.setGridLinesVisible(true);
		
		Scene primaryScene = new Scene(grid, 1152, 658);
		
		Label title = new Label("Block Down Ramp");
		title.setFont(Font.font("Tahoma", 20));
		grid.add(title, 0, 0);
		
		HBox inputs = new HBox();
		inputs.setSpacing(10);
		
		Label massLabel = new Label("Mass (kg):");
		massLabel.setFont(Font.font("Tahoma", 14));
		Label gravLabel = new Label("Gravity (m/s^2):");
		gravLabel.setFont(Font.font("Tahoma", 14));
		Label thetaLabel = new Label("Theta (degrees):");
		thetaLabel.setFont(Font.font("Tahoma", 14));
		Label mkfLabel = new Label("Coefficient of Kinetic Friction:");
		mkfLabel.setFont(Font.font("Tahoma", 14));
		
		TextField massInput = new TextField();
		massInput.setPrefSize(75, 15);
		TextField gravInput = new TextField();
		gravInput.setPrefSize(75, 15);
		TextField thetaInput = new TextField();
		thetaInput.setPrefSize(75, 15);
		TextField mkfInput = new TextField();
		mkfInput.setPrefSize(75, 15);
		
		Button go = new Button("Go");
		go.setPrefSize(126, 15);
		go.setFont(Font.font("Tahoma", 14));
		
		Button reset = new Button("Reset");
		reset.setPrefSize(126, 15);
		reset.setFont(Font.font("Tahoma", 14));
		
		inputs.getChildren().addAll(massLabel, massInput, gravLabel, gravInput, thetaLabel, thetaInput, mkfLabel, mkfInput, go, reset);
		grid.add(inputs, 0, 1);

		//Canvas:
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.AQUAMARINE);
        gc.fillRect(0, 0, width, height);
        
        DoubleProperty x = new SimpleDoubleProperty();
        DoubleProperty y = new SimpleDoubleProperty();
        
        AnimationTimer timer = new AnimationTimer() {
        	
        	boolean firstLoop = true;
        	double startTime = 0.00000000000;
        	double runTime = 0.00000000000;
        	
        	public void handle(long now) {
            	if(x.doubleValue() < endOfRamp){
            		if(firstLoop){
            			startTime = System.nanoTime();//nanos to seconds
            			firstLoop = false;
            		}
            		runTime = (System.nanoTime() - startTime)/1000000000;
	                gc.setFill(Color.AQUAMARINE);
	                gc.fillRect(0, 0, width, height);//resets background drawing
	                gc.setFill(Color.GREEN);
	                drawTriangle(gc, 50, endOfRamp, 50, height, height, topOfRamp);//resets triangle drawing
	                
	                x.setValue((50) + (blockAx * Math.pow(runTime, 2))/2);//x = 1/2at^2
	                y.setValue((topOfRamp - blockSize) + (-blockAy * Math.pow(runTime, 2))/2);//x = 1/2at^2
	        		
	                gc.setFill(Color.RED);
	                gc.fillRect(x.doubleValue(), y.doubleValue(), blockSize, blockSize);
	                
	                
	                gc.setFill(Color.BLACK);
	                gc.fillText("Time (seconds): " + runTime, width - 250, 50);
	                gc.fillText("Length of Ramp (meters): " + (height - 50), width - 250, 75);
	                gc.fillText("X Length (meters): " + (endOfRamp - 50), width - 250, 100);
	                gc.fillText("Y Length (meters): " + (height - topOfRamp), width - 250, 125);
	                gc.fillText("Max Coeff. of Friction: " + mkfMax, width - 250, 150);
            	}else{
            		firstLoop = true;
            	}
            }//handle
        };//AnimationTimer
        
        go.setOnAction(
        new EventHandler<ActionEvent>() {
        	public void handle(ActionEvent e) {
        		timer.stop();
        		try{
        			calculateValues(gc, massInput.getText(), gravInput.getText(), thetaInput.getText(), mkfInput.getText());
        		}catch(Exception e2){
        			gc.setFill(Color.AQUAMARINE);
	                gc.fillRect(0, 0, width, height);//resets background drawing
        			gc.setFill(Color.BLACK);
        			gc.fillText("The inputs entered were invalid.", 200, 50);
        			return;
        		}
        		if(grav <= 0){
        			gc.setFill(Color.AQUAMARINE);
	                gc.fillRect(0, 0, width, height);//resets background drawing
        			gc.setFill(Color.BLACK);
        			gc.fillText("The input for acceleation due to gravity must be greater than 0.", 200, 50);
        			return;
        		}
        		if(mass <= 0){
        			gc.setFill(Color.AQUAMARINE);
	                gc.fillRect(0, 0, width, height);//resets background drawing
        			gc.setFill(Color.BLACK);
        			gc.fillText("The input for mass must be greater than 0.", 200, 50);
        			return;
        		}
        		if((Math.toDegrees(theta) >= 90) || (Math.toDegrees(theta) <= 0)){
        			gc.setFill(Color.AQUAMARINE);
	                gc.fillRect(0, 0, width, height);//resets background drawing
        			gc.setFill(Color.BLACK);
        			gc.fillText("The angle of elevation of the ramp must be between 0 and 90 degrees.", 200, 50);
        			return;
        		}
        		if((mkf < 0)){
        			gc.setFill(Color.AQUAMARINE);
	                gc.fillRect(0, 0, width, height);//resets background drawing
        			gc.setFill(Color.BLACK);
        			gc.fillText("The coefficient of kinetic friction can't be negative.", 200, 50);
        			return;
        		}
        		if(mkf >= mkfMax){
        			gc.setFill(Color.AQUAMARINE);
	                gc.fillRect(0, 0, width, height);//resets background drawing
        			gc.setFill(Color.BLACK);
        			gc.fillText("The Coefficient of Kinetic Friction entered is greater than or equal to the Maximum Coefficient of Kinetic Friction for an angle of " + thetaInput.getText() + " degrees.", 200, 50);
        			gc.fillText("The Maximum Coefficient of Kinetic Friction for angle of " + thetaInput.getText() + " degrees is " + mkfMax + ".", 200, 75);
        			return;
        		}
        		x.setValue(50);
        		y.setValue(topOfRamp);
        		timer.start();
            }//handle
        }//EventHandler Class
        );
        
        reset.setOnAction(
        new EventHandler<ActionEvent>() {
        	public void handle(ActionEvent e) {
        		timer.stop();
        		gc.setFill(Color.AQUAMARINE);
                gc.fillRect(0, 0, width, height);
        		massInput.setText("");
        		gravInput.setText("");
        		thetaInput.setText("");
        		mkfInput.setText("");
        		blockX = 0;
        		blockY = 0;
        		blockAx = 0;
        		blockAy = 0;
        		timeStart = 0;
        		timeRunning = 0;
        		mass = 0;
        		grav = 0;
        		theta = 0;
        		mkf = 0;
        		mkfMax = 0;
        		topOfRamp = 0;
        		endOfRamp = 0;
        	}//handle
        }//EventHandler class
        );
        
        grid.add(canvas, 0, 2);
		
        primaryStage.setScene(primaryScene);
        primaryStage.show();
	}//starts
	
	private void calculateValues(GraphicsContext gc, String massText, String gravText, String thetaText, String mkfText) {
		mass = Double.parseDouble(massText);
		grav = Double.parseDouble(gravText);
		theta = Math.toRadians(Double.parseDouble(thetaText));
		mkf = Double.parseDouble(mkfText);

		topOfRamp = height - ((height - 50)*Math.sin(theta));
		endOfRamp = 50 + (height-50)*Math.cos(theta);
		
		blockAx = ((grav * Math.cos(theta)) * (Math.sin(theta) - mkf * Math.cos(theta)));
		blockAy = ((grav * Math.sin(theta)) * (Math.sin(theta) - mkf * Math.cos(theta))) * -1;//*-1 because it's in the down direction
		
		mkfMax = Math.tan(theta);
	}//calculateValues
	
	private void drawTriangle(GraphicsContext gc, double x1, double x2, double x3, double y1, double y2, double y3){
		gc.fillPolygon(new double[] {x1, x2, x3}, new double[] {y1, y2, y3}, 3);
	}//drawTriangle
}//class
