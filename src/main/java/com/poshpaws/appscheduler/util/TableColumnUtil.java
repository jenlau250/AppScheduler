/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler.util;

import java.util.function.Function;
import javafx.beans.property.Property;
import javafx.scene.control.TableColumn;

/**
 *
 * @author Jen
 */
public class TableColumnUtil {
    
    private static <S,T> TableColumn<S,T> createColumn(String title, Function<S, Property<T>> property) {
    TableColumn<S,T> col = new TableColumn<>(title);
    col.setCellValueFactory(cellData -> property.apply(cellData.getValue()));
    col.setOnEditCommit(edit -> {
        S rowValue = edit.getRowValue();
        property.apply(rowValue).setValue(edit.getNewValue());
    });
    return col ;
}
    
}
