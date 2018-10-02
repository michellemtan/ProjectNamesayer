package model.views;

        import javafx.fxml.FXML;
        import javafx.scene.Scene;
        import javafx.scene.control.Button;
        import javafx.scene.control.ListView;
        import javafx.scene.control.SplitPane;
        import javafx.scene.input.MouseEvent;
        import javafx.scene.text.Text;
        import javafx.stage.Stage;
        import model.resources.SetUp;

        import java.io.IOException;

public class EnterNamesController {

    @FXML
    private Text backButton;

    @FXML
    private SplitPane splitPane;

    @FXML
    private Button searchButton;

    @FXML
    private ListView<?> databaseNamesLIstView;

    @FXML
    private Button hideButton;

    @FXML
    private Button addButton;

    @FXML
    private ListView<?> practiceNamesListView;

    @FXML
    private Button expandButton;

    @FXML
    private Button practiceButton;

    @FXML
    void addButtonClicked(MouseEvent event) {

    }

    @FXML
    void backButtonClicked(MouseEvent event) throws IOException {
        Scene scene = SetUp.getInstance().databaseSelectMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);

    }

    @FXML
    void expandButtonClicked(MouseEvent event) {
        splitPane.setDividerPositions(1);

    }

    @FXML
    void hideButtonClicked(MouseEvent event) {
        splitPane.setDividerPositions(0.005);
    }

    @FXML
    void practiceButtonClicked(MouseEvent event) throws IOException {
        Scene scene = SetUp.getInstance().practiceMenu;
        Stage window = (Stage) backButton.getScene().getWindow();
        window.setScene(scene);
    }

    @FXML
    void searchButtonClicked(MouseEvent event) {

    }

}
