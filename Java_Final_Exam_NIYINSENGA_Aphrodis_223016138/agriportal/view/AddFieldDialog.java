package com.agriportal.view;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog wrapper around AddFarmView used by controllers.
 */
public class AddFieldDialog extends JDialog {

    private final AddFieldView fieldView;

    public AddFieldDialog(Frame owner) {
        super(owner, "Add Field", true);
        fieldView = new AddFieldView();
        getContentPane().add(fieldView);
        pack();
        setLocationRelativeTo(owner);
    }

    public AddFieldView getFieldView() { return fieldView; }
}
