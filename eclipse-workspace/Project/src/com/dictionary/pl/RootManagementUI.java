package com.dictionary.pl;

import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.util.List;

import com.dictionary.bo.RootService;
import com.dictionary.dto.RootDTO;

public class RootManagementUI extends Application {

	private static RootService injectedService;
	private RootService rootService;

	private TextField idField;
	private TextField rootLettersField;
	private TableView<RootRow> rootTable;
	private ObservableList<RootRow> rootData = FXCollections.observableArrayList();

	public static class RootRow {
		private final SimpleIntegerProperty id;
		private final SimpleStringProperty rootLetters;

		public RootRow(Integer id, String rootLetters) {
			this.id = new SimpleIntegerProperty(id);
			this.rootLetters = new SimpleStringProperty(rootLetters);
		}

		public Integer getId() {
			return id.get();
		}

		public String getRootLetters() {
			return rootLetters.get();
		}
	}

	@Override
	public void start(Stage primaryStage) {
		this.rootService = injectedService;

		if (rootService == null) {
			System.err.println("FATAL: RootService dependency was null during start(). Check AppRunner setup.");
			throw new IllegalStateException("Critical dependency (RootService) not injected.");
		}

		primaryStage.setTitle("Arabic Root Management (Presentation Layer - JavaFX)");

		BorderPane root = new BorderPane();
		root.setPadding(new Insets(10));

		root.setTop(createFormPane());
		root.setCenter(createTablePane());
		root.setBottom(createButtonPane());

		refreshRootList();

		Scene scene = new Scene(root, 600, 450);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private VBox createFormPane() {
		GridPane formGrid = new GridPane();
		formGrid.setHgap(10);
		formGrid.setVgap(10);
		formGrid.setPadding(new Insets(10));
		formGrid.setAlignment(Pos.CENTER);
		formGrid.add(new Label("Root ID (Update/Delete):"), 0, 0);
		idField = new TextField();
		idField.setEditable(false);
		formGrid.add(idField, 1, 0);
		formGrid.add(new Label("Root Letters (ك ت ب):"), 0, 1);
		rootLettersField = new TextField();
		rootLettersField.setAlignment(Pos.CENTER_RIGHT);
		formGrid.add(rootLettersField, 1, 1);
		return new VBox(formGrid);
	}

	private TableView<RootRow> createTablePane() {
		rootTable = new TableView<>();
		TableColumn<RootRow, Integer> idColumn = new TableColumn<>("ID");
		idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		TableColumn<RootRow, String> lettersColumn = new TableColumn<>("Root Letters");
		lettersColumn.setCellValueFactory(new PropertyValueFactory<>("rootLetters"));
		rootTable.getColumns().addAll(idColumn, lettersColumn);
		rootTable.setItems(rootData);
		rootTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				idField.setText(newSelection.getId().toString());
				rootLettersField.setText(newSelection.getRootLetters());
			}
		});
		return rootTable;
	}

	private HBox createButtonPane() {
		HBox buttonBox = new HBox(10);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setPadding(new Insets(10, 0, 0, 0));
		Button addButton = new Button("Add Root");
		addButton.setOnAction(this::addRootAction);
		Button updateButton = new Button("Update Root");
		updateButton.setOnAction(this::updateRootAction);
		Button deleteButton = new Button("Delete Root");
		deleteButton.setOnAction(this::deleteRootAction);
		Button refreshButton = new Button("Refresh List");
		refreshButton.setOnAction(e -> refreshRootList());
		buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, refreshButton);
		return buttonBox;
	}

	private void addRootAction(ActionEvent e) {
		String letters = rootLettersField.getText().trim();
		if (letters.isEmpty()) {
			showAlert(Alert.AlertType.WARNING, "Input Required", "Please enter Arabic root letters.");
			return;
		}
		try {
			rootService.addRoot(letters);
			showAlert(Alert.AlertType.INFORMATION, "Success", "Root processed successfully.");
			refreshRootList();
			rootLettersField.clear();
		} catch (Exception ex) {
			showAlert(Alert.AlertType.ERROR, "Error Adding Root", "Error: " + ex.getMessage());
		}
	}

	private void updateRootAction(ActionEvent e) {
		String idText = idField.getText();
		String newLetters = rootLettersField.getText().trim();
		if (idText.isEmpty() || newLetters.isEmpty()) {
			showAlert(Alert.AlertType.WARNING, "Input Required", "Select a root and provide new letters.");
			return;
		}
		try {
			int id = Integer.parseInt(idText);
			rootService.updateRootLetters(id, newLetters);
			showAlert(Alert.AlertType.INFORMATION, "Success", "Root ID " + idText + " updated successfully.");
			refreshRootList();
			idField.clear();
			rootLettersField.clear();
		} catch (NumberFormatException ex) {
			showAlert(Alert.AlertType.ERROR, "Error", "Invalid ID format.");
		} catch (Exception ex) {
			showAlert(Alert.AlertType.ERROR, "Error Updating Root", "Error: " + ex.getMessage());
		}
	}

	private void deleteRootAction(ActionEvent e) {
		String idText = idField.getText();
		if (idText.isEmpty()) {
			showAlert(Alert.AlertType.WARNING, "Input Required", "Select a root to delete.");
			return;
		}
		Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
				"Are you sure you want to delete Root ID " + idText + "?", ButtonType.YES, ButtonType.NO);
		confirmation.showAndWait().ifPresent(response -> {
			if (response == ButtonType.YES) {
				try {
					rootService.deleteRoot(Integer.parseInt(idText));
					showAlert(Alert.AlertType.INFORMATION, "Success", "Root ID " + idText + " deleted successfully.");
					refreshRootList();
					idField.clear();
					rootLettersField.clear();
				} catch (Exception ex) {
					showAlert(Alert.AlertType.ERROR, "Error Deleting Root",
							"Error: " + ex.getMessage() + "\n(May be linked to existing Words)");
				}
			}
		});
	}

	private void refreshRootList() {
		rootData.clear();
		try {
			List<RootDTO> roots = rootService.browseAllRoots();
			for (RootDTO root : roots) {
				rootData.add(new RootRow(root.getId(), root.getRootLetters()));
			}
		} catch (Exception e) {
			showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load roots: " + e.getMessage());
		}
	}

	private void showAlert(Alert.AlertType type, String title, String message) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	public static void setRootService(RootService service) {
		if (service == null) {
			throw new IllegalArgumentException("RootService cannot be null during injection.");
		}
		injectedService = service;
	}
}