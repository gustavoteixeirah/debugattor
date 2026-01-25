package com.kodegt.debugattor.web;

import com.kodegt.debugattor.application.input.DeleteExecutionUseCase;
import com.kodegt.debugattor.application.input.FetchExecutionsUseCase;
import com.kodegt.debugattor.domain.execution.Execution;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Route("")
@PageTitle("Executions")
public class ExecutionsView extends VerticalLayout {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS yyyy-MM-dd");

    private final FetchExecutionsUseCase fetchExecutionsUseCase;
    private final DeleteExecutionUseCase deleteExecutionUseCase;
    private final TextField searchField;
    private final Grid<Execution> grid;

    public ExecutionsView(FetchExecutionsUseCase fetchExecutionsUseCase,
                          DeleteExecutionUseCase deleteExecutionUseCase) {
        this.fetchExecutionsUseCase = fetchExecutionsUseCase;
        this.deleteExecutionUseCase = deleteExecutionUseCase;

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        // Apply dark theme
        getStyle()
                .set("background-color", "#1a1a1a")
                .set("color", "#ffffff");

        // Create container
        VerticalLayout container = new VerticalLayout();
        container.setSizeFull();
        container.setPadding(true);
        container.setSpacing(true);
        container.getStyle()
                .set("max-width", "1400px")
                .set("margin", "0 auto");

        // Header
        container.add(createHeader());

        // Search field
        searchField = createSearchField();
        container.add(searchField);

        // Grid
        grid = createGrid();
        container.add(grid);

        add(container);
    }

    private Component createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.getStyle().set("margin-bottom", "16px");

        H2 title = new H2("Executions");
        title.getStyle()
                .set("margin", "0")
                .set("color", "#ffffff");

        Button refreshButton = new Button("Refresh", new Icon(VaadinIcon.REFRESH));
        refreshButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        refreshButton.addClickListener(e -> grid.getDataProvider().refreshAll());
        refreshButton.getStyle()
                .set("color", "#999999");

        header.add(title, refreshButton);

        return header;
    }

    private TextField createSearchField() {
        TextField search = new TextField();
        search.setPlaceholder("Search by ID");
        search.setPrefixComponent(VaadinIcon.SEARCH.create());
        search.setWidthFull();
        search.setValueChangeMode(ValueChangeMode.LAZY);
        search.addValueChangeListener(e -> grid.getDataProvider().refreshAll());

        // Dark theme styling
        search.getStyle()
                .set("--lumo-contrast-10pct", "#2a2a2a")
                .set("--lumo-contrast-20pct", "#3a3a3a")
                .set("--lumo-base-color", "#252525")
                .set("--lumo-body-text-color", "#ffffff")
                .set("--lumo-secondary-text-color", "#999999");

        return search;
    }

    private Grid<Execution> createGrid() {
        Grid<Execution> grid = new Grid<>();
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);

        // Apply dark theme to grid
        grid.getStyle()
                .set("--lumo-contrast-5pct", "#1a1a1a")
                .set("--lumo-contrast-10pct", "#252525")
                .set("--lumo-base-color", "#1a1a1a")
                .set("--lumo-body-text-color", "#ffffff")
                .set("--lumo-secondary-text-color", "#999999")
                .set("--lumo-header-text-color", "#cccccc")
                .set("--lumo-primary-color", "#60a5fa")
                .set("border-radius", "8px")
                .set("overflow", "hidden");

        // ID column
        grid.addColumn(execution -> execution.id().toString().substring(0, 8) + "...")
                .setHeader("ID")
                .setAutoWidth(true)
                .setFlexGrow(0);

        // Status column with badge
        grid.addColumn(new ComponentRenderer<>(this::createStatusBadge))
                .setHeader("Status")
                .setSortable(true)
                .setComparator(Execution::status)
                .setAutoWidth(true)
                .setFlexGrow(0);

        // Started At column
        grid.addColumn(execution ->
                        execution.startedAt() != null ? execution.startedAt().format(TIME_FORMATTER) : "")
                .setHeader("Started At")
                .setSortable(true)
                .setComparator(Execution::startedAt)
                .setAutoWidth(true)
                .setFlexGrow(1);

        // Finished At column
        grid.addColumn(execution ->
                        execution.finishedAt() != null ? execution.finishedAt().format(TIME_FORMATTER) : "")
                .setHeader("Finished At")
                .setSortable(true)
                .setComparator(Execution::finishedAt)
                .setAutoWidth(true)
                .setFlexGrow(1);

        // Duration column
        grid.addColumn(Execution::duration)
                .setHeader("Duration")
                .setAutoWidth(true)
                .setFlexGrow(0);

        // Actions column
        grid.addColumn(new ComponentRenderer<>(this::createActionButtons))
                .setHeader("Actions")
                .setAutoWidth(true)
                .setFlexGrow(0);

        // Set data provider
        grid.setItemsPageable(pageable ->
                fetchExecutionsUseCase.fetch(searchField.getValue(), pageable));

        // Row click listener
        grid.addItemClickListener(e -> {
            if (!isActionButtonClick(e.getColumn())) {
                ExecutionView.showExecutionDetails(e.getItem().id().toString());
            }
        });

        // Row styling
        grid.setClassNameGenerator(execution -> {
            switch (execution.status()) {
                case FAILED:
                    return "execution-failed";
                case COMPLETED:
                    return "execution-completed";
                case RUNNING:
                default:
                    return "execution-running";
            }
        });

        return grid;
    }

    private Component createStatusBadge(Execution execution) {
        Span badge = new Span(formatStatus(execution.status()));

        String backgroundColor;
        String textColor;

        switch (execution.status()) {
            case COMPLETED:
                backgroundColor = "#064e3b";
                textColor = "#4ade80";
                break;
            case FAILED:
                backgroundColor = "#7f1d1d";
                textColor = "#f87171";
                break;
            case RUNNING:
            default:
                backgroundColor = "#1e3a8a";
                textColor = "#60a5fa";
                break;
        }

        badge.getStyle()
                .set("background-color", backgroundColor)
                .set("color", textColor)
                .set("padding", "4px 12px")
                .set("border-radius", "12px")
                .set("font-size", "12px")
                .set("font-weight", "600")
                .set("white-space", "nowrap");

        return badge;
    }

    private Component createActionButtons(Execution execution) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(true);
        actions.setPadding(false);

        Button viewButton = new Button(new Icon(VaadinIcon.EYE));
        viewButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        viewButton.getStyle()
                .set("color", "#60a5fa")
                .set("cursor", "pointer");
        viewButton.getElement().setAttribute("title", "View details");
        viewButton.addClickListener(e -> {
            e.getSource().getUI().ifPresent(ui ->
                    UI.getCurrent().navigate(ExecutionView.class, execution.id().toString()));
        });

        Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        deleteButton.getStyle()
                .set("color", "#f87171")
                .set("cursor", "pointer");
        deleteButton.getElement().setAttribute("title", "Delete execution");
        deleteButton.addClickListener(e -> confirmDelete(execution.id()));

        actions.add(viewButton, deleteButton);

        return actions;
    }

    private void confirmDelete(UUID executionId) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Delete Execution");
        dialog.setText("Are you sure you want to delete this execution? This action cannot be undone.");

        dialog.setCancelable(true);
        dialog.setCancelText("Cancel");

        dialog.setConfirmText("Delete");
        dialog.setConfirmButtonTheme("error primary");

        dialog.addConfirmListener(event -> {
            try {
                deleteExecutionUseCase.delete(executionId);
                grid.getDataProvider().refreshAll();
            } catch (Exception e) {
                // Show error notification
                showErrorNotification("Failed to delete execution: " + e.getMessage());
            }
        });

        // Dark theme for dialog
        dialog.getElement().getThemeList().add("dark");

        dialog.open();
    }

    private void showErrorNotification(String message) {
        // Simple error display - you can enhance this with Vaadin Notification component
        Div notification = new Div();
        notification.setText(message);
        notification.getStyle()
                .set("position", "fixed")
                .set("top", "16px")
                .set("right", "16px")
                .set("background-color", "#7f1d1d")
                .set("color", "#f87171")
                .set("padding", "12px 24px")
                .set("border-radius", "8px")
                .set("z-index", "1000");

        add(notification);

        // Auto-remove after 3 seconds
        notification.getElement().executeJs(
                "setTimeout(() => $0.remove(), 3000)",
                notification.getElement()
        );
    }

    private boolean isActionButtonClick(Grid.Column<Execution> column) {
        return column != null && "Actions".equals(column.getHeaderText());
    }

    private String formatStatus(Execution.Status status) {
        switch (status) {
            case COMPLETED:
                return "COMPLETED";
            case FAILED:
                return "FAILED";
            case RUNNING:
            default:
                return "RUNNING";
        }
    }

    public static void showExecutionsView() {
        UI.getCurrent().navigate(ExecutionsView.class);
    }
}