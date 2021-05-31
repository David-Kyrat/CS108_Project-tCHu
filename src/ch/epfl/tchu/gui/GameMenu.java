package ch.epfl.tchu.gui;

import javafx.application.*;
import javafx.beans.property.*;
import javafx.css.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.scene.transform.*;
import javafx.stage.*;

import java.util.*;
import java.util.function.*;

import static ch.epfl.tchu.gui.CTransform.Direction.*;
import static ch.epfl.tchu.gui.ConstantsGUI.*;
import static ch.epfl.tchu.gui.GraphicalPlayer.*;
import static ch.epfl.tchu.gui.Nodes.*;
import static javafx.geometry.Pos.*;
import static javafx.scene.paint.Color.*;

public class GameMenu /*extends Application*/ {

    final static String ICON_PATH = imgPath("menu2.jpg");
    final static String BTN_IMG_PATH = imgPath("btn.png");
    final static String TITLE = "CS-108 - Semester Project : tCHu";
    private final static List<String> FONTS_STYLESHEETS =
            List.of("GameMenu.css",
                    "debug.css",
                    "https://fonts.googleapis.com/css2?family=Fondamento&family=Niconne&display=swap");

    public final static double SCENE_W = Screen.getPrimary().getVisualBounds().getWidth();
    public final static double SCENE_H = Screen.getPrimary().getVisualBounds().getHeight();

    private final static String BACKGROUND_PATH = imgPath("gameMenu.jpg");

    private final CTransform trans = new CTransform(Y_AXIS, 260, -60);
    private final double aspectRatio = 930.0 / 1300; //TODO: multiply by ratio scale resize
    private final double percentage = 0.55;

    private final double cardH = SCENE_H * percentage;
    private final double cardW = cardH * aspectRatio;

    private enum Choice {
        HOST, CLIENT;
        private static Choice[] choices = values();

        private static Choice get(int i) {return choices[i];}
    }

    ;

    private final String defaultMsg = "Which Player are you ?";
    private final Text text = new Text(defaultMsg);

    private HBox textBox;

    private final BooleanProperty isPlayerChosenProperty = new SimpleBooleanProperty(false);
    private boolean isGameConfigured = false;
    private Choice playerChoice;

    private final List<Color> colors = List.of(
            rgb(51, 153, 255),
            //rgb(255, 255, 204),
            rgb(255, 31, 72),
            Color.MEDIUMSEAGREEN);
    /** Blue, Yellow/White, Red, Green */

    private final float shadowSpread = 0.5f;
    private String[] hostArgs = new String[2];
    private String[] clientArgs = new String[2];
    private Stage configStage;
    private Stage coPendingStage;
    //final private Text playerNbText = new Text("Player " + (playerNb + 1) + " :");

   /* public static void main(String[] args) {
        launch(args);
    }*/

    public Scene scene(Stage primaryStage) {

        List<ImagePattern> choicesIcon = List.of(new ImagePattern(new Image(imgPath("host.png"))),
                                                 new ImagePattern(new Image(imgPath("server.png"))));

        primaryStage.setTitle(TITLE);
        primaryStage.getIcons().add(new Image(ICON_PATH));

        VBox root = setUpNewVBox(30, CENTER, true);
        HBox subRoot = setUpNewHBox(200, CENTER, false);
        subRoot.getStyleClass().add("subroot");

        setUpCardChoices(choicesIcon, subRoot);

        Rectangle background = new Rectangle(SCENE_W, SCENE_H, new ImagePattern(new Image(BACKGROUND_PATH)));
        /* background.setFitWidth(SCENE_W); background.setFitHeight(SCENE_H);*/
        StackPane backgroundSupport = new StackPane(background, root);
        backgroundSupport.setAlignment(TOP_CENTER);
        background.setLayoutY(-background.getLayoutBounds().getMaxY());

        Scene menuScene = new Scene(backgroundSupport, primaryStage.getWidth(), primaryStage.getHeight(), BROWN);
        menuScene.getStylesheets().addAll(FONTS_STYLESHEETS);
        root.getStyleClass().add("menu");

        primaryStage.setFullScreenExitHint("Press ' f ' to exit/enter fullScreen");

        VBox textBoxP = setUpTextAndParent();

        VBox acceptBox = setUpNewVBox(0, Pos.CENTER_RIGHT, false);
        acceptBox.setMinHeight(50); acceptBox.getStyleClass().add("acceptBox");
/*        mainButton = setUpPlayBtnAction(primaryStage);
        mainButton.disableProperty().bind(chosen.not());*/

        acceptBox.getChildren().addAll(setUpStackPaneBtn("Play", primaryStage));
        textBoxP.getChildren().add(acceptBox);
        root.getChildren().addAll(subRoot, textBoxP);

        Nodes.setShowCenter(primaryStage, menuScene, true);
        primaryStage.setFullScreen(true);
        menuScene.setOnKeyPressed(keyEvent -> handle(keyEvent, primaryStage));

        return menuScene;
    }

    private void handle(KeyEvent keyEvent, Stage stage) {
        switch (keyEvent.getCode()) {
            case ENTER:
                try {
                    //  if (chosen.get() && nameField1Text != null && nameField2Text != null)
                    mainButtonAction(stage);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case F:
                stage.setFullScreen(!stage.isFullScreen());
                break;
        }
    }

    private void setUpConfigStage(Stage primaryStage, String title, String generalTextLabel,
                                  String[] inputFieldValuesToAssign, BiConsumer<Stage, ActionEvent> onReselect,
                                  String[] promptMessages) {
               /* if (choiceIndex == 0) {TestServer.main(new String[]{});} else {TestClient.main(new String[]{});}
        System.out.println( choiceIndex == 0 ?  "TestServer" : "TestClient");*/
        Stage configStage = initModalStage(primaryStage);// new Stage();
        configStage.setTitle(title);
        Text text = new Text(generalTextLabel);
        text.setFill(WHITE);
        text.setFont(new Font(primaryStage.getHeight() / 30));

        Button chooseBtn = new Button("choose");
        TextField field1 = new TextField();
        TextField field2 = new TextField();
        chooseBtn.setOnAction(event -> {
            configStage.close();
            inputFieldValuesToAssign[0] = field1.getText();
            inputFieldValuesToAssign[1] = field2.getText();
            isGameConfigured = true;
            try {
                mainButtonAction(primaryStage);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });

        Button reselectButton = new Button("Re-select");
        reselectButton.setOnAction(event -> {
            onReselect.accept(configStage, event);
            isGameConfigured = false;
        });

        String nameField1PromptText = playerChoice == Choice.HOST ? promptMessages[0] : promptMessages[1];
        String nameField2PromptText = playerChoice == Choice.CLIENT ? promptMessages[0] : promptMessages[1];
        field1.setPromptText(nameField1PromptText);
        field2.setPromptText(nameField2PromptText);

        PseudoClass empty = PseudoClass.getPseudoClass("empty");
        field1.pseudoClassStateChanged(empty, true);
        field1.textProperty().addListener((obs, oldText, newText) -> field1.pseudoClassStateChanged(empty, newText.isEmpty()));
        //
        //   chooseBtn.e
        VBox buttonBox = setUpNewVBox(0, CENTER, true);
        VBox modalRoot = new VBox(text, withChildren(buttonBox, field1, field2),
                                  withChildren(setUpNewHBox(30, TOP_CENTER, true), reselectButton, chooseBtn));

        buttonBox.spacingProperty().bind(configStage.heightProperty().divide(6));
        //  modalRoot.getStyleClass().add("boxR");

        field1.minWidthProperty().bind(configStage.widthProperty().subtract(30));
        field2.minWidthProperty().bind(configStage.widthProperty().subtract(30));

        field1.minHeightProperty().bind(configStage.heightProperty().divide(9));
        field2.minHeightProperty().bind(configStage.heightProperty().divide(9));


        Scene modalScene = new Scene(new StackPane(createBackground(configStage, imgPath("1_4.jpg")), modalRoot),
                                     primaryStage.getWidth() / 2.8, primaryStage.getHeight() / 2.5);
        //  modalScene.getStylesheets().add("debug.css");
        modalScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) chooseBtn.getOnAction().handle(new ActionEvent());
        });
        modalScene.getStylesheets().add(CHOOSER_CSS);
        setShow(configStage, modalScene);
        chooseBtn.requestFocus();
        this.configStage = configStage;
    }

    private void connectionPending(Stage primaryStage) {
        //  Stage stage = initModalStage(primaryStage);
        coPendingStage = new Stage();
        coPendingStage.setTitle("Please wait, connection Pending...");
        coPendingStage.getIcons().add(new Image(ICON_PATH));
        VBox root = setUpNewVBox(20, CENTER, true);
        root.getStyleClass().add("pendingBgEmpty");
        String text = "Waiting for";
        String text2 = "Client connection...";
        double factor = 2.5; String className = "textWait" ;

        Text connectionPendingText = withClass(new Text(text), className);
        Text connectionPendingText2 = withClass(new Text(text2), className);
        connectionPendingText.wrappingWidthProperty().bind(primaryStage.widthProperty().divide(factor));
        connectionPendingText2.wrappingWidthProperty().bind(primaryStage.widthProperty().divide(factor));
        connectionPendingText.setTextAlignment(TextAlignment.CENTER);
        connectionPendingText2.setTextAlignment(TextAlignment.CENTER);

        root.getChildren().addAll(connectionPendingText, connectionPendingText2);

        String bgPath = imgPath("1_3.jpg");
        Scene scene = new Scene(new StackPane(createBackground(coPendingStage, bgPath), root),
                                primaryStage.getWidth()/2.5, primaryStage.getHeight()/3);
        scene.getStylesheets().add("GameMenu.css");
        addDebug(scene);
        root.minWidthProperty().bind(scene.widthProperty());
        root.minHeightProperty().bind(scene.heightProperty());

        // configStage.setTitle("Connection Pending");

        setShow(coPendingStage, scene);
    }
    private ImageView createBackground(Stage stage, String path){
        //String path = imgPath("1_3.jpg");
        ImageView imageView = new ImageView(path);
        imageView.fitHeightProperty().bind(stage.heightProperty());
        imageView.fitWidthProperty().bind(stage.widthProperty());
        return imageView;
    }

    /**
     * Sets up how the image View defined below will appear on the scene instead of the acceptButton (button that handle scene change)
     * and call setUpBtnImage to handle how the image should trigger the acceptButton depending on the MouseEvent it receives.
     * Also Sets up the text and other things related to "making the image look like a button"
     * @param btnText text inside button
     * @return StackPane containing the image and the text over it
     */
    static StackPane styleFantasyBtnImage(String btnText) {
        final double width = 210;
        final Text text = new Text(btnText + "   ");
        //Image that will "represent" the button
        final ImageView fakeButton = new ImageView(BTN_IMG_PATH);

        fakeButton.setFitWidth(width);
        fakeButton.setSmooth(true);
        fakeButton.setPreserveRatio(true);

        fakeButton.setEffect(setUpShadow(BLACK, 45, 0.66f));
        //Arrow image added right next to the text "Play"
        final ImageView arrow = new ImageView(imgPath("arrow.png"));
        arrow.setFitWidth(50);
        arrow.setFitHeight(50);
        arrow.setSmooth(true);
        arrow.setPreserveRatio(false);

        //StackPane to register the image, the arrow and the text
        StackPane stackPane = new StackPane();
        final double width1 = 160, height = 65;
        stackPane.setMinSize(width1, height);
        stackPane.setMaxSize(width1, height);
        stackPane.setAlignment(CENTER);
        VBox.setMargin(stackPane, new Insets(0, 15, 0, 0));

        text.getStyleClass().add("playText");

        //Box containing the text and the arrow
        HBox fBox = setUpNewHBox(0, Pos.CENTER_RIGHT, false);
        fBox.getChildren().addAll(arrow, text);
        stackPane.getChildren().addAll(fakeButton, fBox);

        stackPane.setOnMousePressed(e -> {
            DropShadow ds = (DropShadow) fakeButton.getEffect();
            fakeButton.setEffect(setUpShadow(ds.getColor(), ds.getRadius() / 2.5, 0.8f));
            stackPane.getTransforms().add(Transform.translate(0, -5));

            e.consume();
        });


        return stackPane;
    }

    static void setUpBtnClickEffect(StackPane stackPane) {
        ImageView fakeButton = (ImageView) stackPane.getChildren().get(0);
        stackPane.getTransforms().clear();
        DropShadow ds = (DropShadow) fakeButton.getEffect();
        fakeButton.setEffect(setUpShadow(ds.getColor(), ds.getRadius() * 2.5, 0.66f));
    }

    /**
     * Links the <code>onMouseReleased</code> event of the stackPane having
     * the main button image / graphical representation, to what the main button should actually do. <br/>
     * i.e. just call the corresponding method when stackPane is pressed
     * @param text text inside button
     * @return StackPane containing the image and the text over it acting as a button
     */
    private StackPane setUpStackPaneBtn(String text, Stage primaryStage) {
        //Reset image to normal
        StackPane stackPane = styleFantasyBtnImage(text);
        //stackPane.disableProperty().bind(chosen.not());
        stackPane.setOnMouseReleased(e -> {
            setUpBtnClickEffect(stackPane);
            e.consume();
            if (isPlayerChosenProperty.get()) {
                try {
                    mainButtonAction(primaryStage);
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
        return stackPane;
    }

    /**
     * Defines what happens when accept Button is clicked / "actioned"
     */
    private void mainButtonAction(Stage primaryStage) {
        if (playerChoice == Choice.HOST) {

            if (isGameConfigured && isPlayerChosenProperty.get()) {
                ServerMain serverMain = new ServerMain(hostArgs[0], hostArgs[1]);
                connectionPending(primaryStage);
                Platform.runLater(() -> {
                    try {
                        serverMain.start(primaryStage);
                        coPendingStage.close();
                        System.out.println("Closed stage");
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                System.out.println("should have started");
            }
            else if (isPlayerChosenProperty.get()) {
                setUpConfigStage(primaryStage, "Menu - Name Configuration", "Enter Both Names",
                                 hostArgs,
                                 (configStage, reSelectEvent) -> {
                                     configStage.close();
                                     hostArgs[0] = null;
                                     hostArgs[1] = null;
                                 }, new String[]{"Your Name", "Opponent's Name"});
            }
        }
        else {
            if (isGameConfigured && isPlayerChosenProperty.get()) {
                try {
                    new ClientMain(clientArgs[0], clientArgs[1]).start(primaryStage);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (isPlayerChosenProperty.get()) {
                setUpConfigStage(primaryStage, "Menu - Client Configuration", "Enter ProxyName and Port",
                                 hostArgs,
                                 (configStage, reSelectEvent) -> {
                                     clientArgs[0] = null;
                                     clientArgs[1] = null;
                                     configStage.close();
                                 },
                                 new String[]{"Proxy Name", "PORT"});
            }
        }
    }
    /**
     * Style card to give it a "pushed" effect like a button and trigger the "chosen" boolean
     * @param card Card
     */
    private void onCardPressed(GraphicalCard card, MouseEvent mouseEvent) {
        final Rectangle rectangle = (Rectangle) ((Pane) card.getChildren().get(0)).getChildren().get(0);
        final DropShadow ds = (DropShadow) rectangle.getEffect();

        rectangle.setEffect(setUpShadow(ds.getColor(), ds.getRadius() / 2.5, 0.8f));
        card.getTransforms().add(Transform.translate(0, -5));

        mouseEvent.consume();
    }

    /**
     * Style back the card to give from the "pushed" effect to how it was before
     * @param card Card
     */
    private void onCardReleased(GraphicalCard card, MouseEvent mouseEvent) {
        final Rectangle rectangle = (Rectangle) ((Pane) card.getChildren().get(0)).getChildren().get(0);
        final DropShadow ds = (DropShadow) rectangle.getEffect();
        final int idx = Integer.parseInt(card.getStyleClass().get(0));

        rectangle.setEffect(setUpShadow(ds.getColor(), ds.getRadius() * 2.5, shadowSpread));

        card.getTransforms().remove(card.getTransforms()
                                        .stream()
                                        .filter(transform -> transform instanceof Translate)
                                        .findFirst()
                                        .orElseThrow()
                /* .orElse(null)*/);
//TODO: check here
        isPlayerChosenProperty.set(true);
        mouseEvent.consume();
    }

    private void setUpCardChoices(List<ImagePattern> choicesIcon, HBox subRoot) {
        for (int k = 0; k < 2; k++) {
            final int i = k; //ugly but mandatory for mouseEventHandlers that forces use of lambda which require  variables

            StackPane sp = new StackPane();
            sp.setAlignment(CENTER);
            VBox box = setUpNewVBox(0, cardW, cardH, CENTER, true);

            Rectangle innerRectangle = new Rectangle(0, 0, cardW, cardH);
            innerRectangle.setArcWidth(30); innerRectangle.setArcHeight(30);
            innerRectangle.setFill(grayRgb(230, 0.8));
            double scale = i == 1 ? 0.8 : 1;
            Rectangle iconSupport = new Rectangle(0, 0, (cardW * scale), (cardH * scale));
            iconSupport.setFill(choicesIcon.get(i));
            sp.getChildren().addAll(innerRectangle, iconSupport, box);

            GraphicalCard card = new GraphicalCard(Y_AXIS, sp);

            card.setOnMouseEntered(e -> {
                if (!isPlayerChosenProperty.get()) trans.handleHover(e, card, CTransform.MouseState.ENTER,
                                                                     (currentCard -> additionalHoverEffect(e, currentCard, i)),
                                                                     null);
            });

            card.setOnMouseExited(e -> {
                if (!isPlayerChosenProperty.get()) trans.handleHover(e, card, CTransform.MouseState.EXIT, null,
                                                                     (currentCard -> reverseAdditionalHoverEffect(e, currentCard))
                                                                    );
            });

            card.setOnMousePressed(mouseEvent -> onCardPressed(card, mouseEvent));
            card.setOnMouseReleased(mouseEvent -> {
                if (isPlayerChosenProperty.get()) {
                    onCardReleased(card, mouseEvent);
                    isPlayerChosenProperty.set(false);
                    playerChoice = null;
                }
                else {
                    onCardReleased(card, mouseEvent);
                }

            });
            card.getStyleClass().add(0, String.valueOf(i));
            subRoot.getChildren().add(card);
        }

    }


    /**
     * Update the text in playerNbText to match the actual playerNb
     */
//    private void updatePlayerNbText() {playerNbText.setText("Player " + (playerNb + 1) + " :");}

    /**
     * SetUp the big text that will appear under the cards to indicate which faction is currently chosen
     */
    private VBox setUpTextAndParent() {
        final VBox textBoxCtnr = setUpNewVBox(0, CENTER, true);
        HBox textBoxParent = setUpNewHBox(80, CENTER, false);

        textBox = setUpNewHBox(20, Pos.BOTTOM_CENTER, false);
        final double minHeight = SCENE_H * 0.18;
        textBox.setMinHeight(minHeight);
        textBox.getStyleClass().add("notPicked");

        text.setFill(WHITE);
        text.setStroke(BLACK);

        //  final VBox playerNbBox = setUpNewVBox(0, Pos.BOTTOM_CENTER, true);
        textBox.getChildren().addAll(text);

        /*playerNbBox.getStyleClass().add("playerNbBox");
        playerNbBox.getChildren().add(playerNbText);*/
        textBoxParent.getChildren().addAll(/*playerNbBox,*/ textBox);
        textBoxCtnr.getChildren().add(textBoxParent);
        //  playerNbText.getStyleClass().add("playerNb");

        return textBoxCtnr;
    }

    /**
     * Action to execute in addition to the hover effect
     * @param e          MouseEvent
     * @param card       Card
     * @param colorIndex int
     */
    private void additionalHoverEffect(MouseEvent e, GraphicalCard card, int colorIndex) {
        this.playerChoice = Choice.get(Integer.parseInt(card.getStyleClass().get(0)));

        updateOnHover(e, colorIndex);
        setCardShadow(card, true, colorIndex);

        text.getStyleClass().add("picked");
        textBox.getStyleClass().remove("notPicked");
        textBox.getStyleClass().add("noUnderline");

        //  faction[playerNb] = getFaction(cards.indexOf(card));
    }

    /**
     * Reverse Action to execute to come back to how things were before the call of additionalHoverEffect()
     * @param e    MouseEvent
     * @param card Card
     */
    private void reverseAdditionalHoverEffect(MouseEvent e, GraphicalCard card) {
        text.setText(defaultMsg);

        setCardShadow(card, false, null);

        text.setFill(WHITE);
        text.setStroke(BLACK);
        text.setEffect(null);

        text.getStyleClass().remove("picked");
        textBox.getStyleClass().add("notPicked");
        textBox.getStyleClass().remove("noUnderline");

        if (e != null) e.consume();
        playerChoice = null;
    }

    /**
     * Update text and "card" Styling  (used when "card" is hovered)
     * @param e          MouseEvent
     * @param colorIndex int, see list colors
     */
    private void updateOnHover(MouseEvent e, int colorIndex) {
        this.text.setText((playerChoice == Choice.HOST ? "Host" : "Client") + " ?");

        if (text.getStroke() != colors.get(colorIndex)) {
            this.text.setStroke(colors.get(colorIndex));
            this.text.setFill(WHITE);
        }

        this.text.setEffect(setUpShadow(colorIndex));
        if (e != null) e.consume();
    }

    /**
     * enable or disable shadow on "card"
     * @param card       Card
     * @param on         boolean
     * @param colorIndex Integer !! can be null if on = false
     */
    private void setCardShadow(GraphicalCard card, boolean on, Integer colorIndex) {
        Rectangle rect = (Rectangle) ((Pane) card.getChildren().get(0)).getChildren().get(0);
        if (on) {
            rect.setEffect(setUpShadow(colorIndex));
        }
        else {
            rect.setEffect(null);
        }
    }

    /**
     * @param colorIndex int
     * @return a gaussian shadow of color colors.get(colorIndex)
     */
    private DropShadow setUpShadow(int colorIndex) {
        return setUpShadow(colors.get(colorIndex), 50, shadowSpread);
    }

    /**
     * @param color  Color
     * @param radius double
     * @param spread float (0 <= spread <= 1)
     * @return a gaussian shadow with the given parameters
     */
    static DropShadow setUpShadow(Color color, double radius, float spread) {
        DropShadow ds = new DropShadow(radius, color);
        ds.setBlurType(BlurType.GAUSSIAN);
        ds.setSpread(spread);

        return ds;
    }

}
