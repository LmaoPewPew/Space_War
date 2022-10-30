module com.example.space_war {
    requires javafx.controls;
    requires javafx.fxml;


    opens sn.thu.space_war to javafx.fxml;
    exports sn.thu.space_war;
}