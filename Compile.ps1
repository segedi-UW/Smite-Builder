$name = $args[0]
$javaFxLibPath = "C:\Users\belai\Desktop\clProjects\javaFxLib\openjfx-11.0.2_windows-x64_bin-sdk\javafx-sdk-11.0.2\lib"
javac --module-path $javaFxLibPath --add-modules javafx.controls,javafx.fxml "$name.java" -Xlint:unchecked 
