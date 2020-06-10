package com.nuthan.connect4_app;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import sun.awt.geom.AreaOp;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

	private static final int COLUMNS = 7;
	private static final int ROWS = 6;
	private static final int CIRCLE_DIAMETER = 80;
	private static final String discColor1 = "#24303E";
	private static final String discColor2 = "#4CAA88";

	private static String playerOne = "Player One";
	private static String playerTwo = "Player Two";

	private boolean isPlayerOneTurn = true;
	private boolean isInsertAllowed = true;

	private Disc[] [] insertDiscArray = new Disc[ROWS][COLUMNS];

	@FXML
	public GridPane myGridPane;

	@FXML
	public Pane insertedDiscPane;

	@FXML
	public Label playerLabel;

	@FXML
	public TextField playerOneText, playerTwoText;

	@FXML
	public Button setNamesButton;


	public void createPlayground(){

		Platform.runLater( () -> setNamesButton.requestFocus());


		Shape rectangleWithHoles = createGrid();
		myGridPane.add(rectangleWithHoles,0,1);

		List<Rectangle> rectangleList = CreateClickableColumns();
		for(Rectangle rectangle: rectangleList) {
			myGridPane.add(rectangle, 0, 1);
		}


		setNamesButton.setOnAction(event -> {
			playerOne = playerOneText.getText();
			playerTwo = playerTwoText.getText();
			playerLabel.setText(isPlayerOneTurn? playerOne : playerTwo);

		});

	}



	private Shape createGrid()
		{
			Shape rectangleWithHoles = new Rectangle((COLUMNS+1)*CIRCLE_DIAMETER,(ROWS+0.7)*CIRCLE_DIAMETER);
			for(int row = 0; row <= ROWS; row++) {
				for (int col = 0; col < COLUMNS-1; col++) {
					Circle circle = new Circle();
					circle.setRadius(CIRCLE_DIAMETER / 2);
					circle.setCenterX(CIRCLE_DIAMETER / 2);
					circle.setCenterY(CIRCLE_DIAMETER / 2);
					circle.setSmooth(true);

					circle.setTranslateX(row*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
					circle.setTranslateY(col*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);

					rectangleWithHoles = Shape.subtract(rectangleWithHoles, circle);
				}
			}
			rectangleWithHoles.setFill(Color.WHITE);
			return rectangleWithHoles;

	}
	private List<Rectangle> CreateClickableColumns()
	{
		List<Rectangle> rectangleList = new ArrayList<>();

		for( int col = 0; col < COLUMNS; col++)
		{
			Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER,((ROWS+0.7)*CIRCLE_DIAMETER));
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(col*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
			rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee33")));
			rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));

			final int column = col;
			rectangle.setOnMouseClicked(event -> {
				if(isInsertAllowed) {
					isInsertAllowed = false;
					insertDisc(new Disc(isPlayerOneTurn), column);
				}
			});
			rectangleList.add(rectangle);
		}
		return rectangleList;
	}
	private void insertDisc(Disc disc, int column){

		int row=ROWS-1;
		while ((row >= 0)) {

			if (getDiscIfPresent(row, column) == null)
				break;
			row--;
		}
		if(row < 0)
			return;

		insertDiscArray[row][column] = disc;
		insertedDiscPane.getChildren().add(disc);

		disc.setTranslateX(column*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);

		int currentRow = row;
		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5),disc);
		translateTransition.setToY(row*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/3);
		translateTransition.setOnFinished(event -> {

			isInsertAllowed = true;
			if(gameEnded(currentRow, column)){
				gameOver();
				return;

			}
			isPlayerOneTurn = !isPlayerOneTurn;
			playerLabel.setText(isPlayerOneTurn? playerOne : playerTwo );

		});

		translateTransition.play();

	}

	private boolean gameEnded(int row, int column)
	{
		List<Point2D> verticalPoints = IntStream.rangeClosed(row-3, row+3)
				                       .mapToObj(r -> new Point2D(r,column))
				                       .collect(Collectors.toList());

		List<Point2D> horizontalPoints = IntStream.rangeClosed(column-3, column+3)
				                         .mapToObj(col -> new Point2D(row,col))
				                         .collect(Collectors.toList());

		Point2D startPoint1 = new Point2D(row-3,column+3);
		List<Point2D> diagonal1Ponts = IntStream.rangeClosed(0,6)
				                       .mapToObj(i -> startPoint1.add(i,-i))
				                       .collect(Collectors.toList());

		Point2D startPoint2 = new Point2D(row-3,column-3);
		List<Point2D> diagonal2Ponts = IntStream.rangeClosed(0,6)
				.mapToObj(i -> startPoint2.add(i,i))
				.collect(Collectors.toList());


		boolean isEnded = checkCombinations(verticalPoints) || checkCombinations(horizontalPoints) ||
		                  checkCombinations(diagonal1Ponts) || checkCombinations(diagonal2Ponts);
		return isEnded;
	}

	private boolean checkCombinations(List<Point2D> points) {
		int chain = 0;
		for(Point2D point : points){
			int rowIndexForArray = (int) point.getX();
			int columnIndexForArray = (int) point.getY();

			Disc disc = getDiscIfPresent(rowIndexForArray,columnIndexForArray);

			if(disc != null && disc.isPlayerOneMove == isPlayerOneTurn){

				chain++;
				if(chain == 4) {
					return true;
				}
			} else {
				chain = 0;
			}
		}
		return  false;
	}

	private  Disc getDiscIfPresent(int row, int column){

		if( row >= ROWS || row < 0 || column >= COLUMNS || column < 0)
			return null;

		return insertDiscArray[row][column];
	}

	private void gameOver()
	{
		String winner = isPlayerOneTurn? playerOne : playerTwo;
		System.out.println(winner);
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("connect four");
		alert.setHeaderText("The winner is " + winner);
		alert.setContentText( "Do you want to play again ?");

		ButtonType yesbtn = new ButtonType("Yes");
		ButtonType nobtn = new ButtonType("No,Exit");
		alert.getButtonTypes().setAll(yesbtn,nobtn);

		Platform.runLater( () -> {
			Optional<ButtonType> clicked = alert.showAndWait();
			if(clicked.isPresent() && clicked.get() == yesbtn)
			{
				resetGame();
			} else {
				Platform.exit();
				System.exit(0);
			}
		});

	}

		public void resetGame() {

		insertedDiscPane.getChildren().clear();

		for (int row = 0; row < insertDiscArray.length; row++) {
			for (int col = 0; col < insertDiscArray[row].length; col++) {
				insertDiscArray[row][col] = null;
			}
		}
			isPlayerOneTurn = true;
			playerLabel.setText(playerOne);
			createPlayground();
	}

	private  static class Disc extends Circle{

		private final boolean isPlayerOneMove;
		public Disc(boolean isPlayerOneMove){

			this.isPlayerOneMove = isPlayerOneMove;
			setRadius(CIRCLE_DIAMETER/2);
			setFill(isPlayerOneMove? Color.valueOf(discColor1) : Color.valueOf(discColor2));
			setCenterX(CIRCLE_DIAMETER/2);
			setCenterY(CIRCLE_DIAMETER/2);
		}
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
}




