package extensions.translation;

import gearth.extensions.ExtensionForm;
import gearth.extensions.ExtensionInfo;
import gearth.extensions.extra.tools.PacketInfoSupport;
import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import gearth.ui.GEarthController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.nio.charset.StandardCharsets;

@ExtensionInfo(
        Title = "Translation bot",
        Description = "Crossing fingers.",
        Version = "0.1",
        Author = "Tripical"
)

public class Translation extends ExtensionForm {

    private PacketInfoSupport packetInfoSupport;
    private String userName = "";
    public Button active;
    private boolean _isEnabled = false;
    public int userid;
    public ComboBox comboColor;
    public String TransLang;

    //initialize javaFX elements
    public void initialize() {
        comboColor.getItems().addAll(
                "English",
                "Arabic",
                "Chinese",
                "French",
                "German",
                "Hindi",
                "Indonesian",
                "Irish",
                "Italian",
                "Japanese",
                "Korean",
                "Polish",
                "Portuguese",
                "Russian",
                "Spanish",
                "Turkish",
                "Vietnamese"
        );
        comboColor.getSelectionModel().selectFirst();
    }

    @Override
    public ExtensionForm launchForm(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Translation.class.getResource("form.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Translation Bot");
        primaryStage.setScene(new Scene(root));
        primaryStage.getScene().getStylesheets().add(GEarthController.class.getResource("/gearth/ui/bootstrap3.css").toExternalForm());
        primaryStage.setAlwaysOnTop(true);

        return loader.getController();
    }

    @Override
    protected void initExtension() {
        packetInfoSupport = new PacketInfoSupport(this);
        try {

            packetInfoSupport.intercept(HMessage.Direction.TOCLIENT, "Shout", message -> {
                HPacket packet = message.getPacket();
                userid = packet.readInteger();
                String msg = packet.readString();

                if (msg.trim().equals("")) {
                    writeToConsole("Blocking space message.");
                    return;
                }
                if (msg.toLowerCase().startsWith(this.userName.toLowerCase()) || msg.toLowerCase().startsWith(": " + this.userName.toLowerCase()) || msg.toLowerCase().startsWith("usmhelper") || msg.toLowerCase().startsWith("helper") || msg.toLowerCase().endsWith(this.userName.toLowerCase()) || msg.toLowerCase().endsWith(": " + this.userName.toLowerCase()) || msg.toLowerCase().endsWith("usmhelper") || msg.toLowerCase().endsWith("helper")) {
                    if (this._isEnabled) {
                        processMessage(msg, userid);
                    }
                }
            });

            // RoomUserTalk
            packetInfoSupport.intercept(HMessage.Direction.TOCLIENT, "Chat", message -> {
                HPacket packet = message.getPacket();
                userid = packet.readInteger();
                String msg = packet.readString();

                if (msg.trim().equals("")) {
                    writeToConsole("Blocking space message.");
                    return;
                }
                if (msg.toLowerCase().startsWith(this.userName.toLowerCase()) || msg.toLowerCase().startsWith(": " + this.userName.toLowerCase()) || msg.toLowerCase().startsWith("usmhelper") || msg.toLowerCase().startsWith("helper") || msg.toLowerCase().endsWith(this.userName.toLowerCase()) || msg.toLowerCase().endsWith(": " + this.userName.toLowerCase()) || msg.toLowerCase().endsWith("usmhelper") || msg.toLowerCase().endsWith("helper")) {
                    if (this._isEnabled) {
                        processMessage(msg, userid);
                    }
                }
            });

        } catch (Exception e) {
        }
    }


    private void processMessage(String message, int userid) {
        try {

                String tempMessage = message.toLowerCase();
                tempMessage = tempMessage.replaceAll("\\p{Punct}", "");
                String[] msgArr = tempMessage.split(" ");
                    sendToExternalBot(message, userid);
        } catch (Exception e) {

        }
    }

    private void sendToExternalBot(String message, int userid) {
        switch ((String) comboColor.getValue()) {
            case "English":
                TransLang = "en";
                break;
            case "Arabic":
                TransLang = "ar";
                break;
            case "Chinese":
                TransLang = "zh";
                break;
            case "French":
                TransLang = "fr";
                break;
            case "German":
                TransLang = "de";
                break;
            case "Hindi":
                TransLang = "hi";
                break;
            case "Indonesian":
                TransLang = "id";
            case "Irish":
                TransLang = "ga";
                break;
            case "Italian":
                TransLang = "it";
                break;
            case "Japanese":
                TransLang = "ja";
                break;
            case "Korean":
                TransLang = "ko";
                break;
            case "Polish":
                TransLang = "pl";
                break;
            case "Portuguese":
                TransLang = "pt";
                break;
            case "Russian":
                TransLang = "ru";
                break;
            case "Spanish":
                TransLang = "es";
                break;
            case "Turkish":
                TransLang = "tr";
                break;
            case "Vietnamese":
                TransLang = "vi";
                break;

            default:
                TransLang = "en";
        }
        try {
            message = new String(message.getBytes(StandardCharsets.UTF_8));
            Document doc = Jsoup.connect("https://translate.argosopentech.com/translate")
                    .data("q", message)
                    .data("source", "auto")
                    .data("target", TransLang)
                    .data("api_key", "")
                    .userAgent("Mozilla")
                    .header("Referer", "https://translate.argosopentech.com/")
                    .header("Origin", "https://translate.argosopentech.com")
                    .ignoreContentType(true)
                    .post();

            JSONObject response = new JSONObject(doc.body().text());
                String msg = (String) response.getString("translatedText");
            sendMessageToServer(msg, userid);

        } catch (Exception ex) {
            writeToConsole(ex.getMessage());
        }

    }

    public void sendMessageToServer(String msg, int userid) {
        try {

            packetInfoSupport.sendToClient("Whisper", userid, msg, 0, 1, 0, 0);
        } catch (Exception ex) {
        }
    }
    public void onClickButton(ActionEvent actionEvent) {
        if (_isEnabled) {
            active.setText("Start");
            _isEnabled = false;
        } else {
            active.setText("Stop");
            _isEnabled = true;
        }
    }

    public static void main(String[] args) {
        runExtensionForm(args, Translation.class);
    }

}
