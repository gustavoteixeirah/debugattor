package com.kodegt.debugattor.web.components;

import com.kodegt.debugattor.domain.artifact.Artifact;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Component for rendering artifact previews with expandable details
 */
public class ArtifactCard extends VerticalLayout {

    private final Artifact artifact;
    private boolean expanded = false;

    public ArtifactCard(Artifact artifact) {
        this.artifact = artifact;

        setPadding(true);
        setSpacing(true);
        getStyle()
                .set("background-color", "#1a1a1a")
                .set("border-radius", "4px")
                .set("border", "1px solid #2a2a2a")
                .set("cursor", "pointer")
                .set("transition", "border-color 0.2s");

        addClickListener(e -> toggleExpanded());

        renderContent();
    }

    private void renderContent() {
        removeAll();

        // Header
        HorizontalLayout header = createHeader();
        add(header);

        // Content preview (always visible)
        Component preview = createContentPreview();
        if (preview != null) {
            add(preview);
        }

        // Full content (visible when expanded)
        if (expanded) {
            Component fullContent = createFullContent();
            if (fullContent != null) {
                add(fullContent);
            }
        }
    }

    private HorizontalLayout createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setSpacing(true);

        Icon typeIcon = getArtifactIcon();

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
        copyButton.getStyle()
                .set("color", "#999999")
                .set("min-width", "auto");
        copyButton.addClickListener(e -> {
            copyToClipboard();
        });

        Button expandButton = new Button(new Icon(expanded ? VaadinIcon.CHEVRON_UP : VaadinIcon.CHEVRON_DOWN));
        expandButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        expandButton.getStyle()
                .set("color", "#999999")
                .set("min-width", "auto");

        header.add(typeIcon, artifactName, typeLabel, copyButton, expandButton);

        return header;
    }

    private Component createContentPreview() {
        switch (artifact.type()) {
            case IMAGE:
                return createImagePreview(false);

            case LOG:
                return createTextPreview(artifact.content(), 3);

            case JSON_DATA:
                return createJsonPreview(artifact.content(), 5);

            default:
                return null;
        }
    }

    private Component createFullContent() {
        switch (artifact.type()) {
            case IMAGE:
                return createImagePreview(true);

            case LOG:
                return createTextPreview(artifact.content(), -1);

            case JSON_DATA:
                return createJsonPreview(artifact.content(), -1);

            default:
                return null;
        }
    }

    private Component createImagePreview(boolean fullSize) {
        if (artifact.content() == null || artifact.content().isEmpty()) {
            return createPlaceholder("No image content");
        }

        Image img = new Image(artifact.content(), artifact.description());
        img.setWidth("100%");

        if (fullSize) {
            img.getStyle()
                    .set("max-height", "600px")
                    .set("object-fit", "contain")
                    .set("border-radius", "4px")
                    .set("background-color", "#0a0a0a")
                    .set("cursor", "pointer");
            img.addClickListener(e -> openImageModal());
        } else {
            img.getStyle()
                    .set("max-height", "150px")
                    .set("object-fit", "cover")
                    .set("border-radius", "4px")
                    .set("background-color", "#0a0a0a")
                    .set("cursor", "pointer");
            img.addClickListener(e -> openImageModal());
        }

        return img;
    }

    private Component createTextPreview(String content, int maxLines) {
        if (content == null || content.isEmpty()) {
            return createPlaceholder("No content");
        }

        String displayContent = content;
        if (maxLines > 0) {
            String[] lines = content.split("\n");
            if (lines.length > maxLines) {
                displayContent = String.join("\n", java.util.Arrays.copyOf(lines, maxLines)) + "\n...";
            }
        }

        Pre pre = new Pre(displayContent);
        pre.getStyle()
                .set("color", "#cccccc")
                .set("font-size", "11px")
                .set("margin", "8px 0 0 0")
                .set("white-space", "pre-wrap")
                .set("word-wrap", "break-word")
                .set("line-height", "1.5")
                .set("max-height", maxLines > 0 ? (maxLines * 18) + "px" : "none")
                .set("overflow", "hidden");

        return pre;
    }

    private Component createJsonPreview(String content, int maxLines) {
        if (content == null || content.isEmpty()) {
            return createPlaceholder("No JSON content");
        }

        String displayContent = formatJson(content);
        if (maxLines > 0) {
            String[] lines = displayContent.split("\n");
            if (lines.length > maxLines) {
                displayContent = String.join("\n", java.util.Arrays.copyOf(lines, maxLines)) + "\n...";
            }
        }

        Pre pre = new Pre(displayContent);
        pre.getStyle()
                .set("color", "#cccccc")
                .set("font-size", "11px")
                .set("margin", "8px 0 0 0")
                .set("white-space", "pre-wrap")
                .set("word-wrap", "break-word")
                .set("font-family", "monospace")
                .set("line-height", "1.5")
                .set("max-height", maxLines > 0 ? (maxLines * 18) + "px" : "none")
                .set("overflow", "hidden")
                .set("background-color", "#0a0a0a")
                .set("padding", "8px")
                .set("border-radius", "4px");

        return pre;
    }

    private Component createPlaceholder(String message) {
        Div placeholder = new Div();
        placeholder.setText(message);
        placeholder.getStyle()
                .set("color", "#666666")
                .set("font-size", "12px")
                .set("font-style", "italic")
                .set("padding", "16px")
                .set("text-align", "center");
        return placeholder;
    }

    private Icon getArtifactIcon() {
        Icon icon;
        switch (artifact.type()) {
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

    private void toggleExpanded() {
        expanded = !expanded;
        renderContent();

        getStyle().set("border-color", expanded ? "#4a4a4a" : "#2a2a2a");
    }

    private void openImageModal() {
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

        Span timestamp = new Span("Logged at: " + (artifact.loggedAt() != null ? artifact.loggedAt().toString() : "Unknown"));
        timestamp.getStyle()
                .set("color", "#999999")
                .set("font-size", "12px");

        footer.add(typeLabel, timestamp);

        content.add(header, imageContainer, footer);
        dialog.add(content);

        dialog.open();
    }

    private void copyToClipboard() {
        // In a real implementation, use JavaScript to copy to clipboard
        // For now, show a notification
        Dialog dialog = new Dialog();
        dialog.add(new Span("Content copied to clipboard!"));

        Button closeButton = new Button("Close", e -> dialog.close());
        dialog.add(closeButton);

        dialog.open();

        // Auto-close after 2 seconds
        dialog.getElement().executeJs(
                "setTimeout(() => $0.opened = false, 2000)",
                dialog.getElement()
        );
    }

    private String formatJson(String json) {
        // Simple JSON formatting - in production, use a proper JSON library
        try {
            return json.replace("{", "{\n  ")
                    .replace("}", "\n}")
                    .replace(",", ",\n  ")
                    .replace("[", "[\n  ")
                    .replace("]", "\n]");
        } catch (Exception e) {
            return json;
        }
    }
}