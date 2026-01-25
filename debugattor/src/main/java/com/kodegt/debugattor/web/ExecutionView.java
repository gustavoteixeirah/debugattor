package com.kodegt.debugattor.web;

import com.kodegt.debugattor.application.input.DeleteExecutionUseCase;
import com.kodegt.debugattor.application.input.GetExecutionByIdUseCase;
import com.kodegt.debugattor.domain.artifact.Artifact;
import com.kodegt.debugattor.domain.execution.Execution;
import com.kodegt.debugattor.domain.step.Step;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Route("execution")
@PageTitle("Execution Details")
public class ExecutionView extends VerticalLayout implements HasUrlParameter<String> {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss");

    private final GetExecutionByIdUseCase getExecutionByIdUseCase;
    private final DeleteExecutionUseCase deleteExecutionUseCase;

    private final Div mainContent = new Div();
    private String executionId;

    public ExecutionView(GetExecutionByIdUseCase getExecutionByIdUseCase,
                         DeleteExecutionUseCase deleteExecutionUseCase) {
        this.getExecutionByIdUseCase = getExecutionByIdUseCase;
        this.deleteExecutionUseCase = deleteExecutionUseCase;

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        mainContent.setSizeFull();
        mainContent.getStyle()
                .set("background-color", "#1a1a1a")
                .set("color", "#ffffff")
                .set("overflow", "auto");

        add(mainContent);
    }

    public static void showExecutionDetails(String executionId) {
        UI.getCurrent().navigate(ExecutionView.class, executionId);
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        this.executionId = parameter;
        loadExecution();
    }

    private void loadExecution() {
        try {
            Optional<Execution> executionOpt = getExecutionByIdUseCase.execute(UUID.fromString(executionId));

            if (executionOpt.isEmpty()) {
                showError("Execution not found");
                return;
            }

            Execution execution = executionOpt.get();
            renderExecution(execution);

        } catch (Exception e) {
            log.error("Error loading execution", e);
            showError("Error loading execution: " + e.getMessage());
        }
    }

    private void renderExecution(Execution execution) {
        mainContent.removeAll();

        VerticalLayout container = new VerticalLayout();
        container.setSizeFull();
        container.setPadding(true);
        container.setSpacing(true);
        container.getStyle()
                .set("max-width", "1400px")
                .set("margin", "0 auto");

        // Header
        container.add(createHeader(execution));

        // Steps section
        container.add(createStepsSection(execution));

        mainContent.add(container);
    }

    private Component createHeader(Execution execution) {
        VerticalLayout header = new VerticalLayout();
        header.setPadding(false);
        header.setSpacing(true);

        // Back button and title row
        HorizontalLayout topRow = new HorizontalLayout();
        topRow.setWidthFull();
        topRow.setAlignItems(FlexComponent.Alignment.CENTER);
        topRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        Button backButton = new Button("Back to Executions", new Icon(VaadinIcon.ARROW_LEFT));
        backButton.addClickListener(e -> UI.getCurrent().navigate(""));
        backButton.getStyle()
                .set("background-color", "#2a2a2a")
                .set("color", "#ffffff")
                .set("border", "none")
                .set("cursor", "pointer");

        topRow.add(backButton);

        // Execution title and metadata
        H2 title = new H2("Execution " + execution.id().toString().substring(0, 8));
        title.getStyle()
                .set("margin", "0")
                .set("color", "#ffffff");

        HorizontalLayout metadata = new HorizontalLayout();
        metadata.setSpacing(true);
        metadata.setAlignItems(FlexComponent.Alignment.CENTER);

        Span startedSpan = new Span("Started " + formatRelativeTime(execution.startedAt()));
        startedSpan.getStyle().set("color", "#999999");

        Span statusSpan = createStatusBadge(execution.status());

        Span stepsCount = new Span("Steps: " + execution.steps().size());
        stepsCount.getStyle().set("color", "#999999");

        // Count completed, running, failed steps
        long completed = execution.steps().stream().filter(s -> s.status() == Step.Status.COMPLETED).count();
        long running = execution.steps().stream().filter(s -> s.status() == Step.Status.RUNNING).count();
        long failed = execution.steps().stream().filter(s -> s.status() == Step.Status.FAILED).count();

        Span completedSpan = new Span("✓ " + completed);
        completedSpan.getStyle().set("color", "#4ade80");

        Span runningSpan = new Span("◷ " + running);
        runningSpan.getStyle().set("color", "#60a5fa");

        Span failedSpan = new Span("✕ " + failed);
        failedSpan.getStyle().set("color", "#f87171");

        metadata.add(startedSpan, statusSpan, stepsCount, completedSpan, runningSpan, failedSpan);

        header.add(topRow, title, metadata);

        return header;
    }

    private Component createStepsSection(Execution execution) {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(true);

        HorizontalLayout sectionHeader = new HorizontalLayout();
        sectionHeader.setWidthFull();
        sectionHeader.setAlignItems(FlexComponent.Alignment.CENTER);
        sectionHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        H3 title = new H3("Execution Steps (" + execution.steps().size() + ")");
        title.getStyle().set("margin", "0");

        long completed = execution.steps().stream().filter(s -> s.status() == Step.Status.COMPLETED).count();
        long running = execution.steps().stream().filter(s -> s.status() == Step.Status.RUNNING).count();
        long failed = execution.steps().stream().filter(s -> s.status() == Step.Status.FAILED).count();

        Span summary = new Span(completed + " completed, " + running + " running, " + failed + " failed");
        summary.getStyle().set("color", "#999999");

        sectionHeader.add(title, summary);
        section.add(sectionHeader);

        // Steps grid
        Div stepsGrid = new Div();
        stepsGrid.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(auto-fill, minmax(300px, 1fr))")
                .set("gap", "16px")
                .set("margin-top", "16px");

        int stepNumber = 1;
        for (Step step : execution.steps()) {
            stepsGrid.add(createStepCard(step, stepNumber++));
        }

        section.add(stepsGrid);

        return section;
    }

    private Component createStepCard(Step step, int stepNumber) {
        VerticalLayout card = new VerticalLayout();
        card.setPadding(true);
        card.setSpacing(true);
        card.getStyle()
                .set("background-color", "#252525")
                .set("border-radius", "8px")
                .set("border", "1px solid #3a3a3a");

        // Step header
        HorizontalLayout stepHeader = new HorizontalLayout();
        stepHeader.setWidthFull();
        stepHeader.setAlignItems(FlexComponent.Alignment.CENTER);
        stepHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        HorizontalLayout stepInfo = new HorizontalLayout();
        stepInfo.setSpacing(true);
        stepInfo.setAlignItems(FlexComponent.Alignment.CENTER);

        Span stepNum = new Span(String.valueOf(stepNumber));
        stepNum.getStyle()
                .set("background-color", "#3a3a3a")
                .set("border-radius", "50%")
                .set("width", "24px")
                .set("height", "24px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("font-size", "12px");

        Icon statusIcon = getStatusIcon(step.status());

        stepInfo.add(stepNum, statusIcon);

        Span artifactCount = new Span(step.artifacts().size() + " artifacts");
        artifactCount.getStyle()
                .set("color", "#999999")
                .set("font-size", "12px");

        stepHeader.add(stepInfo, artifactCount);

        // Step name
        H4 stepName = new H4(step.name());
        stepName.getStyle()
                .set("margin", "8px 0")
                .set("color", "#ffffff")
                .set("font-size", "14px");

        // Started time
        Span startedTime = new Span("Started " + formatRelativeTime(step.registeredAt()));
        startedTime.getStyle()
                .set("color", "#999999")
                .set("font-size", "12px");

        card.add(stepHeader, stepName, startedTime);

        // Artifacts section
        if (!step.artifacts().isEmpty()) {
            Div artifactsHeader = new Div();
            artifactsHeader.setText("Artifacts");
            artifactsHeader.getStyle()
                    .set("color", "#cccccc")
                    .set("font-size", "12px")
                    .set("font-weight", "bold")
                    .set("margin-top", "12px")
                    .set("margin-bottom", "8px");

            card.add(artifactsHeader);

            for (Artifact artifact : step.artifacts()) {
                card.add(createArtifactPreview(artifact));
            }
        }

        return card;
    }

    private Component createArtifactPreview(Artifact artifact) {
        VerticalLayout preview = new VerticalLayout();
        preview.setPadding(true);
        preview.setSpacing(true);
        preview.getStyle()
                .set("background-color", "#1a1a1a")
                .set("border-radius", "4px")
                .set("border", "1px solid #2a2a2a");

        // Artifact header
        HorizontalLayout artifactHeader = new HorizontalLayout();
        artifactHeader.setWidthFull();
        artifactHeader.setAlignItems(FlexComponent.Alignment.CENTER);
        artifactHeader.setSpacing(true);

        Icon typeIcon = getArtifactIcon(artifact.type());

        Span artifactName = new Span(artifact.description());
        artifactName.getStyle()
                .set("color", "#ffffff")
                .set("font-size", "12px")
                .set("flex", "1")
                .set("overflow", "hidden")
                .set("text-overflow", "ellipsis")
                .set("white-space", "nowrap");

        Span typeLabel = new Span(artifact.type().name());
        typeLabel.getStyle()
                .set("background-color", "#3a3a3a")
                .set("color", "#999999")
                .set("padding", "2px 8px")
                .set("border-radius", "4px")
                .set("font-size", "10px");

        Button copyButton = new Button(new Icon(VaadinIcon.COPY));
        copyButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        copyButton.getStyle().set("color", "#999999");

        artifactHeader.add(typeIcon, artifactName, typeLabel, copyButton);

        preview.add(artifactHeader);

        // Artifact content preview
        Component contentPreview = createArtifactContent(artifact);
        if (contentPreview != null) {
            preview.add(contentPreview);
        }

        return preview;
    }

    private Component createArtifactContent(Artifact artifact) {
        switch (artifact.type()) {
            case IMAGE:
                Image img = new Image(artifact.content(), artifact.description());
                img.setWidth("100%");
                img.getStyle()
                        .set("max-height", "200px")
                        .set("object-fit", "contain")
                        .set("border-radius", "4px")
                        .set("cursor", "pointer");
                img.addClickListener(e -> openImageModal(artifact));
                return img;

            case LOG:
                Pre logContent = new Pre(truncateText(artifact.content(), 200));
                logContent.getStyle()
                        .set("color", "#cccccc")
                        .set("font-size", "11px")
                        .set("margin", "0")
                        .set("white-space", "pre-wrap")
                        .set("word-wrap", "break-word");
                return logContent;

            case JSON_DATA:
                Pre jsonContent = new Pre(truncateText(artifact.content(), 200));
                jsonContent.getStyle()
                        .set("color", "#cccccc")
                        .set("font-size", "11px")
                        .set("margin", "0")
                        .set("white-space", "pre-wrap")
                        .set("word-wrap", "break-word")
                        .set("font-family", "monospace");
                return jsonContent;

            default:
                return null;
        }
    }

    private Icon getStatusIcon(Step.Status status) {
        Icon icon;
        switch (status) {
            case COMPLETED:
                icon = VaadinIcon.CHECK_CIRCLE.create();
                icon.setColor("#4ade80");
                break;
            case FAILED:
                icon = VaadinIcon.CLOSE_CIRCLE.create();
                icon.setColor("#f87171");
                break;
            case RUNNING:
            default:
                icon = VaadinIcon.CIRCLE.create();
                icon.setColor("#60a5fa");
                break;
        }
        icon.setSize("16px");
        return icon;
    }

    private Icon getArtifactIcon(Artifact.Type type) {
        Icon icon;
        switch (type) {
            case IMAGE:
                icon = VaadinIcon.PICTURE.create();
                break;
            case JSON_DATA:
                icon = VaadinIcon.CODE.create();
                break;
            case LOG:
            default:
                icon = VaadinIcon.FILE_TEXT.create();
                break;
        }
        icon.setColor("#999999");
        icon.setSize("16px");
        return icon;
    }

    private Span createStatusBadge(Execution.Status status) {
        Span badge = new Span(formatStatus(status));

        String backgroundColor;
        String textColor;

        switch (status) {
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
                .set("font-weight", "bold");

        return badge;
    }

    private String formatStatus(Execution.Status status) {
        switch (status) {
            case COMPLETED:
                return "Finished";
            case FAILED:
                return "Failed";
            case RUNNING:
            default:
                return "Running";
        }
    }

    private String formatRelativeTime(Object timestamp) {
        if (timestamp == null) {
            return "unknown time";
        }
        // Simple implementation - you can enhance this with relative time calculation
        return "a few seconds ago";
    }

    private String truncateText(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }

    private void openImageModal(Artifact artifact) {
        Dialog dialog = new Dialog();
        dialog.setModal(true);
        dialog.setDraggable(false);
        dialog.setResizable(false);
        dialog.setWidth("95vw");
        dialog.setHeight("95vh");

        // Create content layout
        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(false);
        content.setSizeFull();
        content.getStyle()
                .set("background-color", "#1a1a1a");

        // Header with title and close button
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.getStyle()
                .set("padding", "16px")
                .set("background-color", "#252525")
                .set("border-bottom", "1px solid #3a3a3a");

        Span title = new Span(artifact.description());
        title.getStyle()
                .set("color", "#ffffff")
                .set("font-weight", "600")
                .set("font-size", "16px");

        Button closeButton = new Button(new Icon(VaadinIcon.CLOSE));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        closeButton.getStyle()
                .set("color", "#999999")
                .set("cursor", "pointer");
        closeButton.addClickListener(e -> dialog.close());

        header.add(title, closeButton);

        // Image container
        Div imageContainer = new Div();
        imageContainer.setSizeFull();
        imageContainer.getStyle()
                .set("display", "flex")
                .set("justify-content", "center")
                .set("align-items", "center")
                .set("padding", "24px")
                .set("background-color", "#0a0a0a")
                .set("overflow", "auto");

        Image fullImage = new Image(artifact.content(), artifact.description());
        fullImage.getStyle()
                .set("max-width", "100%")
                .set("max-height", "100%")
                .set("width", "auto")
                .set("height", "auto")
                .set("object-fit", "contain")
                .set("border-radius", "4px");

        imageContainer.add(fullImage);

        // Footer with metadata
        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.setAlignItems(FlexComponent.Alignment.CENTER);
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        footer.getStyle()
                .set("padding", "12px 16px")
                .set("background-color", "#252525")
                .set("border-top", "1px solid #3a3a3a");

        Span typeLabel = new Span("IMAGE");
        typeLabel.getStyle()
                .set("background-color", "#3a3a3a")
                .set("color", "#999999")
                .set("padding", "4px 8px")
                .set("border-radius", "4px")
                .set("font-size", "11px");

        Span timestamp = new Span("Logged at: " + formatRelativeTime(artifact.loggedAt()));
        timestamp.getStyle()
                .set("color", "#999999")
                .set("font-size", "12px");

        footer.add(typeLabel, timestamp);

        content.add(header, imageContainer, footer);
        dialog.add(content);

        dialog.open();
    }

    private void showError(String message) {
        mainContent.removeAll();

        VerticalLayout errorLayout = new VerticalLayout();
        errorLayout.setSizeFull();
        errorLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        errorLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        H2 errorTitle = new H2("Error");
        errorTitle.getStyle().set("color", "#f87171");

        Span errorMessage = new Span(message);
        errorMessage.getStyle().set("color", "#999999");

        Button backButton = new Button("Back to Executions", e -> UI.getCurrent().navigate(""));
        backButton.getStyle()
                .set("background-color", "#2a2a2a")
                .set("color", "#ffffff")
                .set("border", "none")
                .set("cursor", "pointer");

        errorLayout.add(errorTitle, errorMessage, backButton);
        mainContent.add(errorLayout);
    }
}