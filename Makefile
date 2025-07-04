# Cambia esta ruta a donde está javafx-sdk
PATH_TO_FX="C:\Users\DIEXT\Documents\JavaFx\openjfx-24.0.1_windows-x64_bin-sdk\javafx-sdk-24.0.1\lib"
SRC=.
BIN=./bin

SOURCES=$(wildcard $(SRC)/*.java)

# Asegúrate de que el JDK esté en el PATH, por ejemplo:
# export PATH="/c/Program Files/OpenJDK/jdk-22.0.2/bin:$$PATH"

compile:
	if not exist $(BIN) mkdir $(BIN)
	javac --module-path $(PATH_TO_FX) --add-modules javafx.controls,javafx.fxml -d $(BIN) $(SOURCES)
	if exist modern.css copy modern.css $(BIN)\modern.css

run:
	java --module-path $(PATH_TO_FX) --add-modules javafx.controls,javafx.fxml -cp $(BIN) Main

clean:
	if exist $(BIN) rmdir /s /q $(BIN)

all: compile run
