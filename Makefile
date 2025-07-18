# Cambia esta ruta a donde realmente está tu javafx-sdk
PATH_TO_FX="/Users/ignacio/Downloads/javafx-sdk-24.0.1/lib"
SRC=.
BIN=./bin

SOURCES=$(wildcard $(SRC)/*.java)

# Asegúrate de que el JDK esté en el PATH, por ejemplo:
# export PATH="/c/Program Files/OpenJDK/jdk-22.0.2/bin:$$PATH"

compile:
	mkdir -p $(BIN)
	javac --module-path $(PATH_TO_FX) --add-modules javafx.controls -d $(BIN) $(SOURCES)

run:
	java --module-path $(PATH_TO_FX) --add-modules javafx.controls,javafx.fxml -cp $(BIN) Main

clean:
	@find $(BIN) -name "*.class" -type f -delete


all: compile run
