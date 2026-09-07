package org.example.dailymeal.PresentationLayer;

import javafx.scene.control.ButtonBar;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import java.util.Optional;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.TransferMode;
import javafx.scene.input.Dragboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import org.example.dailymeal.BusinessLayer.DailyRation;
import org.example.dailymeal.BusinessLayer.MealTime;
import org.example.dailymeal.BusinessLayer.ProductEntry;
import org.example.dailymeal.BusinessLayer.User;
import org.example.dailymeal.DataAccessLayer.Category;
import org.example.dailymeal.DataAccessLayer.Product;
import org.example.dailymeal.ServiceLayer.impl.CatalogService;
import org.example.dailymeal.ServiceLayer.impl.MealPlanService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainController {

    @FXML private TreeView<String> catalogTreeView;
    @FXML private TabPane mealPlanTabPane;
    @FXML private TableView<ProductEntry> breakfastTableView;
    @FXML private TableView<ProductEntry> lunchTableView;
    @FXML private TableView<ProductEntry> dinnerTableView;
    @FXML private Label dailyNormLabel;
    @FXML private Label remainingCaloriesLabel;
    @FXML private Label totalCaloriesLabel;
    @FXML private Label totalProteinsLabel;
    @FXML private Label totalFatsLabel;
    @FXML private Label totalCarbsLabel;
    @FXML private Label breakfastTotalLabel;
    @FXML private Label lunchTotalLabel;
    @FXML private Label dinnerTotalLabel;
    @FXML private TextField searchField;

    private CatalogService catalogService;
    private MealPlanService mealPlanService;
    private DailyRation currentDailyRation;
    private User currentUser;
    private Map<TreeItem<String>, Product> productMap = new HashMap<>();

    public MainController() {
        catalogService = new CatalogService();
        mealPlanService = new MealPlanService();
    }

    @FXML
    public void initialize() {
        setupCatalogTree();
        setupTables();
        setupDragAndDrop();
        setupDefaultMealTimes();
    }

    private void setupCatalogTree() {
        TreeItem<String> rootItem = new TreeItem<>("Каталог продуктов");
        rootItem.setExpanded(true);

        try {
            List<Category> categories = catalogService.getAllCategories();
            for (Category category : categories) {
                TreeItem<String> categoryItem = new TreeItem<>(category.getName());
                if (category.getProducts() != null) {
                    for (Product product : category.getProducts()) {
                        TreeItem<String> productItem = new TreeItem<>(product.getName());
                        productMap.put(productItem, product);
                        categoryItem.getChildren().add(productItem);
                    }
                }
                rootItem.getChildren().add(categoryItem);
            }
        } catch (Exception e) {
            System.out.println("Error:"+ e.getMessage());
        }

        catalogTreeView.setRoot(rootItem);
        catalogTreeView.setShowRoot(true);
    }


    private void setupTables() {
        setupTableColumns(breakfastTableView);
        setupTableColumns(lunchTableView);
        setupTableColumns(dinnerTableView);

        setupEditableWeight(breakfastTableView);
        setupEditableWeight(lunchTableView);
        setupEditableWeight(dinnerTableView);
    }

    private void setupTableColumns(TableView<ProductEntry> table) {
        table.getColumns().clear();

        TableColumn<ProductEntry, String> productColumn = new TableColumn<>("Продукт");
        productColumn.setPrefWidth(150);
        productColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProduct().getName())
        );

        TableColumn<ProductEntry, Double> weightColumn = new TableColumn<>("Вес (г)");
        weightColumn.setPrefWidth(80);
        weightColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getWeight()).asObject()
        );

        TableColumn<ProductEntry, Double> caloriesColumn = new TableColumn<>("Калории");
        caloriesColumn.setPrefWidth(80);
        caloriesColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getCalories()).asObject()
        );

        TableColumn<ProductEntry, Double> proteinColumn = new TableColumn<>("Белки");
        proteinColumn.setPrefWidth(80);
        proteinColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getProteins()).asObject()
        );

        TableColumn<ProductEntry, Double> fatsColumn = new TableColumn<>("Жиры");
        fatsColumn.setPrefWidth(80);
        fatsColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getFats()).asObject()
        );

        TableColumn<ProductEntry, Double> carbsColumn = new TableColumn<>("Углеводы");
        carbsColumn.setPrefWidth(80);
        carbsColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getCarbs()).asObject()
        );

        TableColumn<ProductEntry, Void> actionColumn = new TableColumn<>("Действия");
        actionColumn.setPrefWidth(80);
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button deleteButton = new Button("❌");
            {
                deleteButton.setStyle("-fx-background-color: transparent; -fx-text-fill: red;");
                deleteButton.setOnAction(event -> {
                    ProductEntry entry = getTableView().getItems().get(getIndex());
                    getTableView().getItems().remove(entry);
                    // ДОБАВЛЕНО: удаление из сервиса
                    String mealTimeName = getMealTimeNameByTable(getTableView());
                    mealPlanService.removeProductFromMealTime(mealTimeName, entry);
                    updateTotals();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        table.getColumns().addAll(productColumn, weightColumn, caloriesColumn, proteinColumn, fatsColumn, carbsColumn, actionColumn);
    }

    private void setupEditableWeight(TableView<ProductEntry> table) {
        @SuppressWarnings("unchecked")
        TableColumn<ProductEntry, Double> weightColumn = (TableColumn<ProductEntry, Double>) table.getColumns().get(1);
        weightColumn.setCellFactory(column -> new TextFieldTableCell<>(new DoubleStringConverter()));
        weightColumn.setOnEditCommit(event -> {
            ProductEntry entry = event.getRowValue();
            double newWeight = event.getNewValue();
            if (newWeight > 0 && newWeight <= 5000) {
                entry.setWeight(newWeight);
                String mealTimeName = getMealTimeNameByTable(table);
                mealPlanService.updateProductWeight(mealTimeName, entry, newWeight);
                table.refresh();
                updateTotals();
            }
        });
    }

    private static class DoubleStringConverter extends javafx.util.converter.DoubleStringConverter {
        @Override
        public Double fromString(String s) {
            try {
                return Double.parseDouble(s.replace(',', '.'));
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
    }

    private void syncTableToService(TableView<ProductEntry> table, String mealTimeName) {
        for (ProductEntry entry : table.getItems()) {
            mealPlanService.addProductToMealTime(mealTimeName, entry.getProduct(), entry.getWeight());
        }
    }

    private String getMealTimeNameByTable(TableView<ProductEntry> table) {
        if (table.equals(breakfastTableView)) return "Breakfast";
        if (table.equals(lunchTableView)) return "Lunch";
        if (table.equals(dinnerTableView)) return "Dinner";
        return "";
    }

    private void setupDragAndDrop() {
        catalogTreeView.setOnDragDetected(event -> {
            TreeItem<String> selectedItem = catalogTreeView.getSelectionModel().getSelectedItem();
            if (selectedItem != null && productMap.containsKey(selectedItem)) {
                Dragboard db = catalogTreeView.startDragAndDrop(TransferMode.COPY);
                ClipboardContent content = new ClipboardContent();
                content.putString(selectedItem.getValue());
                db.setContent(content);
                event.consume();
            }
        });

        setupDropTarget(breakfastTableView, "breakfast");
        setupDropTarget(lunchTableView, "lunch");
        setupDropTarget(dinnerTableView, "dinner");
    }

    private void setupDropTarget(TableView<ProductEntry> table, String mealTimeName) {
        table.setOnDragOver(event -> {
            if (event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        table.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                String productName = db.getString();
                Product product = findProductByName(productName);
                if (product != null) {
                    addProductToMealTime(product, mealTimeName);
                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private Product findProductByName(String name) {
        TreeItem<String> root = catalogTreeView.getRoot();
        return findProductInTree(root, name);
    }

    private Product findProductInTree(TreeItem<String> item, String name) {
        if (productMap.containsKey(item) && item.getValue().equals(name)) {
            return productMap.get(item);
        }
        for (TreeItem<String> child : item.getChildren()) {
            Product result = findProductInTree(child, name);
            if (result != null) return result;
        }
        return null;
    }

    private void addProductToMealTime(Product product, String mealTimeName) {
        ProductEntry entry = new ProductEntry(product, 100.0);
        TableView<ProductEntry> targetTable = getTableByMealTime(mealTimeName);
        if (targetTable != null) {
            targetTable.getItems().add(entry);
            mealPlanService.addProductToMealTime(mealTimeName, product, 100.0);
            updateTotals();
        }
    }

    private TableView<ProductEntry> getTableByMealTime(String mealTimeName) {
        switch (mealTimeName.toLowerCase()) {
            case "breakfast": return breakfastTableView;
            case "lunch": return lunchTableView;
            case "dinner": return dinnerTableView;
            default: return null;
        }
    }

    private void setupDefaultMealTimes() {
        breakfastTableView.setItems(FXCollections.observableArrayList());
        lunchTableView.setItems(FXCollections.observableArrayList());
        dinnerTableView.setItems(FXCollections.observableArrayList());

        breakfastTableView.setEditable(true);
        lunchTableView.setEditable(true);
        dinnerTableView.setEditable(true);
    }

    private void updateTotals() {
        mealPlanService.createNewDailyRation();
        if (currentUser != null) {
            mealPlanService.setUser(currentUser);
        }
        syncTableToService(breakfastTableView, "Breakfast");
        syncTableToService(lunchTableView, "Lunch");
        syncTableToService(dinnerTableView, "Dinner");

        double breakfastCal = calculateTotal(breakfastTableView, "calories");
        double breakfastProt = calculateTotal(breakfastTableView, "proteins");
        double breakfastFat = calculateTotal(breakfastTableView, "fats");
        double breakfastCarb = calculateTotal(breakfastTableView, "carbs");

        double lunchCal = calculateTotal(lunchTableView, "calories");
        double lunchProt = calculateTotal(lunchTableView, "proteins");
        double lunchFat = calculateTotal(lunchTableView, "fats");
        double lunchCarb = calculateTotal(lunchTableView, "carbs");

        double dinnerCal = calculateTotal(dinnerTableView, "calories");
        double dinnerProt = calculateTotal(dinnerTableView, "proteins");
        double dinnerFat = calculateTotal(dinnerTableView, "fats");
        double dinnerCarb = calculateTotal(dinnerTableView, "carbs");

        breakfastTotalLabel.setText(String.format("%.0f ккал | %.1fг б | %.1fг ж | %.1fг у",
                breakfastCal, breakfastProt, breakfastFat, breakfastCarb));
        lunchTotalLabel.setText(String.format("%.0f ккал | %.1fг б | %.1fг ж | %.1fг у",
                lunchCal, lunchProt, lunchFat, lunchCarb));
        dinnerTotalLabel.setText(String.format("%.0f ккал | %.1fг б | %.1fг ж | %.1fг у",
                dinnerCal, dinnerProt, dinnerFat, dinnerCarb));

        double totalCal = breakfastCal + lunchCal + dinnerCal;
        double totalProt = breakfastProt + lunchProt + dinnerProt;
        double totalFat = breakfastFat + lunchFat + dinnerFat;
        double totalCarb = breakfastCarb + lunchCarb + dinnerCarb;

        totalCaloriesLabel.setText(String.format("%.0f ккал", totalCal));
        totalProteinsLabel.setText(String.format("%.1f г", totalProt));
        totalFatsLabel.setText(String.format("%.1f г", totalFat));
        totalCarbsLabel.setText(String.format("%.1f г", totalCarb));

        if (currentUser != null) {
            double remaining = currentUser.calculateDailyCaloriesRate() - totalCal;
            remainingCaloriesLabel.setText(String.format("%.1f ккал", remaining));
        }
    }

    private double calculateTotal(TableView<ProductEntry> table, String type) {
        return table.getItems().stream()
                .mapToDouble(entry -> {
                    switch (type) {
                        case "calories": return entry.getCalories();
                        case "proteins": return entry.getProteins();
                        case "fats": return entry.getFats();
                        case "carbs": return entry.getCarbs();
                        default: return 0;
                    }
                })
                .sum();
    }

    @FXML
    private void handleLoadUserData() {
        try {
            java.net.URL url = getClass().getResource("/user-data-dialog.fxml");
            System.out.println("URL файла: " + url);

            if (url == null) {
                showAlert("Ошибка", "Файл user-data-dialog.fxml не найден в resources!", Alert.AlertType.ERROR);
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            DialogPane dialogPane = loader.load();
            UserDataController controller = loader.getController();
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Данные пользователя");

            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    currentUser = controller.getUser();
                    if (currentUser != null && currentUser.validate()) {
                        dailyNormLabel.setText(String.format("%.0f ккал", currentUser.calculateDailyCaloriesRate()));
                        updateTotals();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить диалог: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText();
        if (query != null && !query.isEmpty()) {
            searchInTree(catalogTreeView.getRoot(), query.toLowerCase());
        }
    }

    private void searchInTree(TreeItem<String> item, String query) {
        boolean matches = item.getValue().toLowerCase().contains(query);
        item.setExpanded(matches);
        for (TreeItem<String> child : item.getChildren()) {
            searchInTree(child, query);
        }
    }

    @FXML
    private void handleAddCategory() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Новая категория");
        dialog.setHeaderText("Введите название категории");
        dialog.setContentText("Название:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (!name.isEmpty()) {
                TreeItem<String> newCategory = new TreeItem<>(name);
                catalogTreeView.getRoot().getChildren().add(newCategory);

                Category category = new Category();
                category.setName(name);
                catalogService.addCategory(category);

                showAlert("Успех", "Категория \"" + name + "\" добавлена", Alert.AlertType.INFORMATION);
            }
        });
    }

    @FXML
    private void handleAddProduct() {
        TreeItem<String> selectedItem = catalogTreeView.getSelectionModel().getSelectedItem();

        if (selectedItem == null || selectedItem == catalogTreeView.getRoot()) {
            showAlert("Ошибка", "Сначала выберите категорию, в которую хотите добавить продукт", Alert.AlertType.ERROR);
            return;
        }

        if (productMap.containsKey(selectedItem)) {
            showAlert("Ошибка", "Выберите категорию, а не продукт", Alert.AlertType.ERROR);
            return;
        }

        String categoryName = selectedItem.getValue();

        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Добавление продукта");
        dialog.setHeaderText("Добавление продукта в категорию: " + categoryName);

        ButtonType addButton = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Название продукта");
        TextField caloriesField = new TextField();
        caloriesField.setPromptText("Калории (на 100г)");
        TextField proteinField = new TextField();
        proteinField.setPromptText("Белки (на 100г)");
        TextField fatsField = new TextField();
        fatsField.setPromptText("Жиры (на 100г)");
        TextField carbsField = new TextField();
        carbsField.setPromptText("Углеводы (на 100г)");

        grid.add(new Label("Название:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Калории:"), 0, 1);
        grid.add(caloriesField, 1, 1);
        grid.add(new Label("Белки:"), 0, 2);
        grid.add(proteinField, 1, 2);
        grid.add(new Label("Жиры:"), 0, 3);
        grid.add(fatsField, 1, 3);
        grid.add(new Label("Углеводы:"), 0, 4);
        grid.add(carbsField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                try {
                    String name = nameField.getText();
                    if (name == null || name.trim().isEmpty()) {
                        showAlert("Ошибка", "Введите название продукта", Alert.AlertType.ERROR);
                        return null;
                    }
                    int calories = Integer.parseInt(caloriesField.getText());
                    double protein = Double.parseDouble(proteinField.getText());
                    double fats = Double.parseDouble(fatsField.getText());
                    double carbs = Double.parseDouble(carbsField.getText());

                    Product product = new Product();
                    product.setName(name);
                    product.setCalories(calories);
                    product.setProtein(protein);
                    product.setFats(fats);
                    product.setCarbs(carbs);
                    return product;
                } catch (NumberFormatException e) {
                    showAlert("Ошибка", "Введите корректные числа", Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        Optional<Product> result = dialog.showAndWait();
        result.ifPresent(product -> {
            // Добавляем продукт в TreeView
            TreeItem<String> productItem = new TreeItem<>(product.getName());
            productMap.put(productItem, product);
            selectedItem.getChildren().add(productItem);

            // Добавляем продукт в сервис
            Category category = catalogService.getCategoryByName(categoryName);
            if (category != null) {
                catalogService.addProduct(product, category);
            }

            showAlert("Успех", "Продукт \"" + product.getName() + "\" добавлен в категорию \"" + categoryName + "\"", Alert.AlertType.INFORMATION);
        });
    }

    @FXML
    private void handleDelete() {
        TreeItem<String> selected = catalogTreeView.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getParent() != null) {
            String itemName = selected.getValue();

            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Подтверждение удаления");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText("Вы уверены, что хотите удалить \"" + itemName + "\"?");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (productMap.containsKey(selected)) {
                    Product product = productMap.get(selected);
                    TreeItem<String> parentCategory = selected.getParent();
                    Category category = catalogService.getCategoryByName(parentCategory.getValue());
                    if (category != null) {
                        catalogService.removeProduct(product, category);
                    }
                    productMap.remove(selected);
                }
                else {
                    Category category = catalogService.getCategoryByName(itemName);
                    if (category != null) {
                        catalogService.removeCategory(category);
                    }
                    for (TreeItem<String> child : selected.getChildren()) {
                        productMap.remove(child);
                    }
                }

                selected.getParent().getChildren().remove(selected);
                showAlert("Успех", "\"" + itemName + "\" удален", Alert.AlertType.INFORMATION);
            }
        } else {
            showAlert("Ошибка", "Нельзя удалить корневой элемент", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleExport() {
        try {
            mealPlanService.createNewDailyRation();
            if (currentUser != null) {
                mealPlanService.setUser(currentUser);
            }

            syncTableToService(breakfastTableView, "Breakfast");
            syncTableToService(lunchTableView, "Lunch");
            syncTableToService(dinnerTableView, "Dinner");

            String fileName = "meal_plan_" + LocalDate.now() + ".txt";
            mealPlanService.saveDailyRation(fileName);

            showAlert("Экспорт", "План питания экспортирован в файл: " + fileName, Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось экспортировать: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
