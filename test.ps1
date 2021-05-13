$errorCount = $error.count
$name = "TestSuite"
javac -cp  ".;junit5.jar" "$name.java"
if ($error.count -eq $errorCount -and $lastExitCode -eq 0) { # The number of errors has not increased
    java -jar junit5.jar -cp . --scan-classpath
}