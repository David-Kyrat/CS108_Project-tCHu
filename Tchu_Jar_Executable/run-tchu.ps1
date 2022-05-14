[String] $fxPath = $env:JAVAFX_HOME.ToString()

if (-not ($fxPath.Trim() -and (Test-Path $env:JAVAFX_HOME/javafx.controls.jar))) {
	$fxPath = Read-Host "Enter your JAVAFX_HOME path (lib directory not bin)"
}

java --module-path $fxPath --add-modules "javafx.controls,javafx.fxml,javafx.graphics" -jar ./CS108_Project-tCHu.jar
