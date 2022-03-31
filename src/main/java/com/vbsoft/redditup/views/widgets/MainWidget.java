package com.vbsoft.redditup.views.widgets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.listbox.dataview.ListBoxListDataView;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vbsoft.redditup.persistence.LogModel;
import com.vbsoft.redditup.service.Upvoter;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MainWidget extends HorizontalLayout {

    private final Upvoter upvoter;
    private ListBoxListDataView<String> urlsData;
    private Grid<LogModel> fLogGrid = new Grid<>(LogModel.class, true);

    @Autowired
    public MainWidget(Upvoter service) {
        this.upvoter = service;
        this.addActions();
        this.addGrid();
    }

    private void addActions() {
        VerticalLayout starter = new VerticalLayout();
        starter.setWidth("60%");
        MultiSelectListBox<String> urls = new MultiSelectListBox<>();
        urls.setWidthFull();
        urls.getStyle().set("border", "1px solid gray");
        this.urlsData = urls.setItems(new LinkedList<>());
        TextArea area = new TextArea();
        area.setWidth("30%");
        HorizontalLayout tools = new HorizontalLayout();
        tools.setWidth("30%");
        Button add = new Button(VaadinIcon.ADD_DOCK.create(), event -> {
            this.urlsData.addItem(area.getValue());
            this.urlsData.refreshAll();
        });
        Button clear = new Button(VaadinIcon.RECYCLE.create(), event -> {
            this.urlsData = urls.setItems(new LinkedList<>());
            this.urlsData.refreshAll();
        });

        Button start = new Button(VaadinIcon.START_COG.create(), event -> this.fireUpvote());
        Label urlLabel = new Label("URLs");
        tools.add(add, start, clear);
        tools.setJustifyContentMode(JustifyContentMode.EVENLY);
        start.setWidthFull();
        starter.add(area, tools, urlLabel, urls);
        add(starter);
    }

    private void addGrid() {
        VerticalLayout grid = new VerticalLayout();
        ComboBox<String> logsFiles = new ComboBox<>();
        logsFiles.setPlaceholder("Logs");
        logsFiles.setWidthFull();
        logsFiles.setItems(this.getLogs());
        logsFiles.addValueChangeListener(this::fillLog);
        grid.add(logsFiles, this.fLogGrid);
        grid.setWidth("40%");
        add(grid);
    }

    private void fillLog(AbstractField.ComponentValueChangeEvent changeEvent) {
       String val = (String) changeEvent.getValue();
       File file = new File(val);
        ObjectMapper mapper = new JsonMapper();
        try {
            List<LogModel> model = Arrays.asList(mapper.readValue(FileUtils.readFileToString(file, StandardCharsets.UTF_8), LogModel[].class));
            this.fLogGrid.setItems(model);
            this.fLogGrid.getDataProvider().refreshAll();
        } catch (IOException ex) {
            Notification.show("Can not read file");
        }

    }

    private List<String> getLogs() {
        List<String> result = new LinkedList<>();
        File logsDirectory = new File("reddit/logs");
        if(logsDirectory.exists()) {
            File[] files = logsDirectory.listFiles();
            Arrays.stream(files).parallel().forEach(f -> result.add(f.getAbsolutePath()));
        }
        return result;
    }

    private void fireUpvote() {
        System.out.println(Arrays.toString(this.urlsData.getItems().toArray()));
        this.upvoter.upvote(this.urlsData.getItems().collect(Collectors.toList()));
    }
}
