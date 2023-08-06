package com.shpp.p2p.cs.bkuznichuk.assignment4;

import acm.graphics.*;
import acm.util.RandomGenerator;
import com.shpp.cs.a.graphics.WindowProgram;

import javax.sound.sampled.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Objects;


/**
 * The program emulates the well-known game “Breakout”.
 * You have a racket at the bottom of the screen, which you can move horizontally.
 * In the upper part of the screen there are 10 rows of bricks.
 * The main goal of the game is to use the ball to knock down all the bricks.
 * You have 3 attempts for this.
 */
public class Breakout extends WindowProgram {
    /**
     * Width and height of application window in pixels
     */
    public static final int APPLICATION_WIDTH = 400;
    public static final int APPLICATION_HEIGHT = 600;

    /**
     * Dimensions of game board (usually the same)
     */
    private static final int WIDTH = APPLICATION_WIDTH;
    private static final int HEIGHT = APPLICATION_HEIGHT;

    /**
     * Dimensions of the paddle
     */
    private static final int PADDLE_WIDTH = 60;
    private static final int PADDLE_HEIGHT = 10;

    /**
     * Offset of the paddle up from the bottom
     */
    private static final int PADDLE_Y_OFFSET = 50;

    /**
     * Number of bricks per row
     */
    private static final int N_BRICKS_PER_ROW = 10;

    /**
     * Number of rows of bricks
     */
    private static final int N_BRICK_ROWS = 10;
    /**
     * The total number of bricks on the screen
     */
    private static final int N_BRICK = N_BRICK_ROWS * N_BRICKS_PER_ROW;

    /**
     * Separation between bricks
     */
    private static final int BRICK_SEP = 4;

    /**
     * Width of a brick
     */
    private static final int BRICK_WIDTH =
            (WIDTH - (N_BRICKS_PER_ROW - 1) * BRICK_SEP) / N_BRICKS_PER_ROW;

    /**
     * Height of a brick
     */
    private static final int BRICK_HEIGHT = 8;

    /**
     * Radius of the ball in pixels
     */
    private static final int BALL_RADIUS = 10;

    /**
     * Offset of the top brick row from the top
     */
    private static final int BRICK_Y_OFFSET = 70;
    /**
     * Angular degree for finding points on a circle
     */
    public static final int DEGREE = 360;
    /**
     * Variable that counts the number of bricks on the screen
     */
    private int calculateBricksOnTable = N_BRICK;
    /**
     * A constant responsible for the frame rate
     */
    private static final double PAUSE_TIME = 1000.0 / 60 / 2;
    /**
     * A constant responsible for the number of attempts
     */
    private static final int ROUNDS = 3;
    /**
     * The dimensions of the picture with the image of the heart
     */
    private static final double heartWidth = 20;
    private static final double heartHeight = 20;
    /**
     * A Label type object for displaying the number of earned points on the screen
     */
    private GLabel scoreOnTable;
    /**
     * object of type GRect - describes the racket
     */
    private GRect paddle;
    /**
     * GRect type object - describes the ball
     */
    private GOval ball;
    /**
     * GObject type objects - display the number of attempts for the user
     */
    private GObject heart1;
    private GObject heart2;
    private GObject heart3;
    /**
     * Change to display the number of points scored
     */
    private int score;
    /**
     * Variables that characterize the direction of movement of the ball on the playing field
     */
    private double vx;
    private double vy = 3.0;


    /**
     * The main method. The entire game starts from it,
     * and the basic logic of the interaction of the basic methods is prescribed.
     */
    public void run() {
        try {
            playStart();                                        //Play the sound od star
        } catch (IOException | UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        }
        GLabel start = getGLabelStart();                        //Starts the start page before starting the game
        grateBricks();                                          //Fills the playing field with bricks
        addPaddleInATable();                                    //Creates a racket and describes its behavior
        grateLife();
        runGame(start);
        for (int i = 0; i < ROUNDS; i++) {                      //Until three attempts are used, we continue the game

            startTheGame();                                     //main logic of game
            kickLife(i);                                        // if loose in one attempt kick one life
            remove(scoreOnTable);                               //remove score
        }
        endGame();                                              //Display a notification about the end of the game
    }

    /**
     * The method describes the creation of bricks and adds them to the screen
     */
    private void grateBricks() {
        for (int i = 0; i < N_BRICK_ROWS; i++) {
            for (int j = 0; j < N_BRICKS_PER_ROW; j++) {
                GRect brick = getBrick(i, j);
                paintTheBricks(i, brick);

            }
        }

    }

    /**
     * The method describes the process of painting bricks
     *
     * @param i     numbers bricks row
     * @param brick situated in this row
     */
    private static void paintTheBricks(int i, GRect brick) {
        if (i == 0 || i == 1) {
            brick.setColor(Color.RED);
        } else if (i == 2 || i == 3) {
            brick.setColor(Color.ORANGE);
        } else if (i == 4 || i == 5) {
            brick.setColor(Color.YELLOW);
        } else if (i == 6 || i == 7) {
            brick.setColor(Color.GREEN);
        } else brick.setColor(Color.CYAN);
    }

    /**
     * The method adds a brick to the screen
     *
     * @param i the parameter for the serial number of the brick horizontally affects its coordinates
     * @param j the parameter for the serial number of the brick vertically affects its coordinates
     * @return brick
     */
    private GRect getBrick(int i, int j) {
        GRect brick = new GRect(
                (getWidth() - (N_BRICKS_PER_ROW * BRICK_WIDTH) - (N_BRICKS_PER_ROW - 1) * BRICK_SEP) / 2.0
                        + j * (BRICK_WIDTH + BRICK_SEP),
                BRICK_Y_OFFSET + i * BRICK_HEIGHT + i * BRICK_SEP,
                BRICK_WIDTH,
                BRICK_HEIGHT);
        brick.setFilled(true);
        add(brick);
        return brick;
    }

    /**
     * Place the racket on the playing field and turn physics motion
     */
    public void addPaddleInATable() {
        gratePaddle();
        addMouseListeners();
    }

    /**
     * Create a racket and display its image on the playing field
     */
    public void gratePaddle() {
        paddle = new GRect((getWidth() - PADDLE_WIDTH) / 2.0,
                getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
        paddle.setFilled(true);
        paddle.setFillColor(Color.BLACK);
        add(paddle);
    }

    /**
     * Describes the interaction between the racket and the user's mouse
     *
     * @param me the event to be processed
     */
    public void mouseDragged(MouseEvent me) {
        paddle.setLocation(me.getX() - PADDLE_WIDTH / 2.0, getHeight() - PADDLE_Y_OFFSET);
        if (paddle.getX() <= 0)
            paddle.setLocation(0, getHeight() - PADDLE_Y_OFFSET);
        if (paddle.getX() >= getWidth() - PADDLE_WIDTH)
            paddle.setLocation(getWidth() - PADDLE_WIDTH, getHeight() - PADDLE_Y_OFFSET);
    }

    /**
     * Display the number of lives on the screen
     */
    private void grateLife() {
        add(heart1 = new GImage("src/com/shpp/p2p/cs/bkuznichuk/assignment4/heart.png",
                getWidth() - 1 * heartWidth,
                getHeight() - heartHeight));
        add(heart2 = new GImage("src/com/shpp/p2p/cs/bkuznichuk/assignment4/heart.png",
                getWidth() - 2 * heartWidth,
                getHeight() - heartHeight));
        add(heart3 = new GImage("src/com/shpp/p2p/cs/bkuznichuk/assignment4/heart.png",
                getWidth() - 3 * heartWidth,
                getHeight() - heartHeight));

    }

    /**
     * If lost - take one life
     *
     * @param i the number of lives
     */
    private void kickLife(int i) {
        if (i == 0) remove(heart1);
        if (i == 1) remove(heart2);
        if (i == 2) remove(heart3);
    }


    /**
     * The method describes the behavior of the game before it starts
     */

    private void runGame(GLabel start) {
        waitForClick();
        remove(start);                                                      //kill label "Start game"
    }

    /**
     * Display the label on the start of the game
     *
     * @return label "Start game"
     */
    private GLabel getGLabelStart() {
        GLabel start = new GLabel("PLEASE CLICK TO START THE GAME!");   //add label "Start game"
        start.setFont("Veranda -20");
        start.setLocation((getWidth() - start.getWidth()) / 2.0, (getHeight() + start.getHeight()) / 2.0);
        start.setColor(Color.DARK_GRAY);
        add(start);
        return start;
    }


    /**
     * The method adds a ball to the playing field and starts the game process
     */
    private void startTheGame() {
        grateBall();
        if (calculateBricksOnTable != 0) {
            waitForClick();
        }
        gameLogic();
    }

    /**
     * Create a ball and add it to the screen
     */
    private void grateBall() {
        ball = new GOval(
                getWidth() / 2.0 - BALL_RADIUS,
                getHeight() / 2.0 - BALL_RADIUS,
                2 * BALL_RADIUS,
                2 * BALL_RADIUS);
        ball.setFilled(true);
        ball.setFillColor(Color.BLACK);
        add(ball);
    }

    /**
     * The method describes the basic logic of the game
     */
    private void gameLogic() {

        scoreInATable();
        firstBallMove();
        ballMove();

    }

    /**
     * The method describes when the ball can move,
     * and in what cases it must stop
     */
    private void ballMove() {
        while (calculateBricksOnTable != 0) {
            ballBehavior();
            if (stopRounds()) break;
        }
    }

    /**
     * Pause the game or start the next round
     *
     * @return boolean
     */
    private boolean stopRounds() {
        if (ball.getY() >= getHeight() + PADDLE_Y_OFFSET) {
            remove(ball);
            return true;
        }
        return false;
    }

    /**
     * Describes the movement of the ball before the first hit with the racket
     */
    private void firstBallMove() {
        RandomGenerator speedGen = RandomGenerator.getInstance();
        vx = speedGen.nextDouble(1.0, 3.0);
        if (speedGen.nextBoolean(0.5)) {
            vx = -vx;
        }
    }

    /**
     * Display the bill on the screen
     */
    private void scoreInATable() {
        scoreOnTable = new GLabel("Your score: " + score);
        add(scoreOnTable, 0, getHeight() - scoreOnTable.getAscent());
    }

    /**
     * A method that describes the complete behavior of the ball
     * and the logic of the game entire
     */
    private void ballBehavior() {
        ball.move(vx, vy);
        GObject collider = getCollidingObject(ball);
        if (collider == paddle) {
            hitHorizontalPlane(collider);     //Condition at which the ball enters the lower or upper wall of the object
            hitVerticalPlane(collider);       //Condition at which the ball gets into the side wall of the object
        }
        if (ball.getY() <= 0) {
            vy = -vy;
        }
        if ((ball.getX() <= 0) || (ball.getX() + (2 * BALL_RADIUS)) >= getWidth()) {
            vx = -vx;
        }
        if ((collider != null) && (collider != paddle) && (collider != scoreOnTable)
                && (collider != heart1) && (collider != heart2) && (collider != heart3)) {
            remove(collider);
            try {
                playKickBricks();                //play sound if hit brick
            } catch (IOException | UnsupportedAudioFileException e) {
                throw new RuntimeException(e);
            }
            hitHorizontalPlane(collider);   //Condition at which the ball enters the lower or upper wall of the object
            hitVerticalPlane(collider);     //Condition at which the ball gets into the side wall of the object
            score++;                                            //calculate score
            calculateBricksOnTable--;                           //calculate bricks
            scoreOnTable.setLabel("Your score: " + score);      //print score
        }
        pause(PAUSE_TIME);
    }

    /**
     * Condition at which the ball gets into the side wall of the object
     *
     * @param collider object that is in certain coordinates
     */
    private void hitVerticalPlane(GObject collider) {
        for (int j = 0; j <= Objects.requireNonNull(collider).getWidth(); j += collider.getWidth()) {
            for (int i = 0; i <= collider.getHeight(); i++) {
                if (getElementAt(collider.getX() + j, collider.getY() + i) != null &&
                        getElementAt(collider.getX() + j, collider.getY() + i) != collider) {
                    if (j == 0 && vx > 0) vx = -vx;
                    if (j == collider.getWidth() && vx < 0) vx = -vx;
                }
            }
        }
    }

    /**
     * Condition at which the ball enters the lower or upper wall of the object
     *
     * @param collider object that is in certain coordinates
     */
    private void hitHorizontalPlane(GObject collider) {
        for (int j = 0; j <= Objects.requireNonNull(collider).getHeight(); j += collider.getHeight()) {
            for (int i = 0; i <= collider.getWidth(); i++) {
                if (getElementAt(collider.getX() + i, collider.getY() + j) != null &&
                        getElementAt(collider.getX() + i, collider.getY() + j) != collider) {
                    if (j == 0 && vy > 0) vy = -vy;
                    if (j == collider.getHeight() && vy < 0) vy = -vy;
                }
            }
        }
    }

    /**
     * Find the coordinates of the circle of the ball, and return the object to the same coordinates
     *
     * @param ball object hwo we play
     * @return null or object in a ball coordinates
     */
    private GObject getCollidingObject(GObject ball) {
        double ballCenterX = ball.getX() + BALL_RADIUS;
        double ballCenterY = ball.getY() + BALL_RADIUS;
        double pointX, pointY;
        for (double i = 0; i < DEGREE; i++) {
            pointX = ballCenterX + BALL_RADIUS * Math.cos(i);
            pointY = ballCenterY + BALL_RADIUS * Math.sin(i);
            if (getElementAt(pointX, pointY) != null && getElementAt(pointX, pointY) != ball)
                return getElementAt(pointX, pointY);

        }
        return null;
    }

    /**
     * The method describes the behavior of the game after the user has used all attempts
     */
    private void endGame() {
        try {
            playFinish();
        } catch (IOException | UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        }
        if (calculateBricksOnTable > 0) {            //Display a notification about the loss and the result of the game
            removeAll();
            printGameOver();
        }
        if (calculateBricksOnTable == 0) {           //Display a notification about the win and the result of the game
            removeAll();
            printWinner();
        }
    }

    /**
     * Description of objects created when the game was lost
     */
    private void printGameOver() {
        youEndScoreLabel(Color.RED);
        youFinishLabel("GAME OVER", Color.RED);
    }

    /**
     * Description of objects created when the game was win
     */
    private void printWinner() {
        youEndScoreLabel(Color.GREEN);
        youFinishLabel("YOU WIN", Color.GREEN);
    }

    private GLabel createLabel(String text, Color color, double x, double y) {
        GLabel label = new GLabel(text);
        label.setColor(color);
        label.setFont("Veranda-40");
        add(label, x, y);
        return label;
    }

    private void youFinishLabel(String finishStatus, Color color) {
        double x = (getWidth() - createLabel(finishStatus, color, 0, 0).getWidth()) / 2.0;
        double y = (getHeight() - createLabel(finishStatus, color, 0, 0).getAscent()) / 2.0;
        createLabel(finishStatus, color, x, y);
    }

    private void youEndScoreLabel(Color color) {
        double x = (getWidth() - createLabel("YOUR SCORE:" + score, color, 0, 0).getWidth()) / 2.0;
        double y = getHeight() / 2.0 + createLabel("YOUR SCORE:" + score, color, 0, 0).getAscent();
        createLabel("YOUR SCORE:" + score, color, x, y);
    }

    /**
     * Play sound from file
     *
     * @param filePath path to sound file
     */
    private void playSound(String filePath) throws IOException, UnsupportedAudioFileException {
        try {
            File soundFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.setFramePosition(0);
            clip.start();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Play sound if take a brick
     */
    private void playKickBricks() throws IOException, UnsupportedAudioFileException {
        playSound("src/com/shpp/p2p/cs/bkuznichuk/assignment4/brickKick.wav");
    }

    /**
     * Play sound when start game
     */
    private void playStart() throws IOException, UnsupportedAudioFileException {
        playSound("src/com/shpp/p2p/cs/bkuznichuk/assignment4/start.wav");
    }

    /**
     * Play sound if user finish
     */
    private void playFinish() throws IOException, UnsupportedAudioFileException {
        playSound("src/com/shpp/p2p/cs/bkuznichuk/assignment4/victory.wav");
    }


}