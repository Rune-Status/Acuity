package com.acuitybotting.website.dashboard.components.general.list_display;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import lombok.Getter;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Created by Zachary Herridge on 8/9/2018.
 */
@Getter
public class InteractiveListColumn<T, R extends Component> {

    private final String uid = UUID.randomUUID().toString();
    private final String header;
    private final String maxWidth;

    private boolean filterable = true;

    private final Function<T, R> constructMapping;
    private BiConsumer<T, R> updateMapping;

    public InteractiveListColumn(String header, String maxWidth, Function<T, R> constructMapping, BiConsumer<T, R> updateMapping) {
        this.header = header;
        this.maxWidth = maxWidth;
        this.updateMapping = updateMapping;
        this.constructMapping = constructMapping;
    }

    public Component getHeaderComponent(){
        Div div = new Div();
        div.setText(header);
        div.setWidth(maxWidth);
        return div;
    }
}
