/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package ui.gui;

import javax.swing.*;
import java.awt.*;

// This class is a part of TextInputDemo
// from https://docs.oracle.com/javase/tutorial/uiswing/examples/components/index.html

// A 1.4 file that provides utility methods for
// creating form- or grid-style layouts with SpringLayout.
// These utilities are used by several programs, such as
// SpringBox and SpringCompactGrid.
public class SpringUtilities {

    // EFFECTS: adjust the x/y constraints of all the cells so that they
    //          are aligned in a grid.
    private static SpringLayout.Constraints
            alignCells(Container parent, int cols, SpringLayout layout, Spring xpadSpring, Spring ypadSpring,
                       Spring initialXSpring, Spring initialYSpring, int max, SpringLayout.Constraints lastCons,
                       SpringLayout.Constraints lastRowCons) {
        for (int i = 0; i < max; i++) {
            SpringLayout.Constraints cons = layout.getConstraints(
                    parent.getComponent(i));
            if (i % cols == 0) { //start of new row
                lastRowCons = lastCons;
                cons.setX(initialXSpring);
            } else { //x position depends on previous component
                cons.setX(Spring.sum(lastCons.getConstraint(SpringLayout.EAST),
                        xpadSpring));
            }

            if (i / cols == 0) { //first row
                cons.setY(initialYSpring);
            } else { //y position depends on previous row
                cons.setY(Spring.sum(lastRowCons.getConstraint(SpringLayout.SOUTH),
                        ypadSpring));
            }
            lastCons = cons;
        }
        return lastCons;
    }

    //EFFECTS: Apply the new width/height Spring. This forces all the
    //         components to have the same size.
    private static void applyNewSpring(Container parent, SpringLayout layout, int max,
                                       Spring maxWidthSpring, Spring maxHeightSpring) {
        for (int i = 0; i < max; i++) {
            SpringLayout.Constraints cons = layout.getConstraints(
                    parent.getComponent(i));

            cons.setWidth(maxWidthSpring);
            cons.setHeight(maxHeightSpring);
        }
    }

    //EFFECTS: Used by makeCompactGrid to get cell constraints
    private static SpringLayout.Constraints getConstraintsForCell(
            int row, int col,
            Container parent,
            int cols) {
        SpringLayout layout = (SpringLayout) parent.getLayout();
        Component c = parent.getComponent(row * cols + col);
        return layout.getConstraints(c);
    }

    // EFFECTS: Aligns the first <code>rows</code> * <code>cols</code>
    //          components of <code>parent</code> in
    //          a grid. Each component in a column is as wide as the maximum
    //          preferred width of the components in that column;
    //          height is similarly determined for each row.
    //          The parent is made just big enough to fit them all.
    public static void makeCompactGrid(Container parent,
                                       int rows, int cols,
                                       int initialX, int initialY,
                                       int xpad, int ypad) {
        SpringLayout layout;
        try {
            layout = (SpringLayout) parent.getLayout();
        } catch (ClassCastException exc) {
            System.err.println("The first argument to makeCompactGrid must use SpringLayout.");
            return;
        }

        //Align all cells in each column and make them the same width.
        Spring x = Spring.constant(initialX);
        x = alignCellsWidth(parent, rows, cols, xpad, x);

        //Align all cells in each row and make them the same height.
        Spring y = Spring.constant(initialY);
        y = alignCellsHeight(parent, rows, cols, ypad, y);

        setParentSize(parent, layout, x, y);
    }

    //EFFECTS: Align all cells in each row and make them the same height.
    private static Spring alignCellsHeight(Container parent, int rows, int cols, int ypad, Spring y) {
        for (int r = 0; r < rows; r++) {
            Spring height = Spring.constant(0);
            for (int c = 0; c < cols; c++) {
                height = Spring.max(height,
                        getConstraintsForCell(r, c, parent, cols).getHeight());
            }
            for (int c = 0; c < cols; c++) {
                SpringLayout.Constraints constraints =
                        getConstraintsForCell(r, c, parent, cols);
                constraints.setY(y);
                constraints.setHeight(height);
            }
            y = Spring.sum(y, Spring.sum(height, Spring.constant(ypad)));
        }
        return y;
    }

    // EFFECTS: set the parent's size
    private static void setParentSize(Container parent, SpringLayout layout, Spring x, Spring y) {
        SpringLayout.Constraints pcons = layout.getConstraints(parent);
        pcons.setConstraint(SpringLayout.SOUTH, y);
        pcons.setConstraint(SpringLayout.EAST, x);
    }

    //EFFECTS: Align all cells in each column and make them the same width.
    private static Spring alignCellsWidth(Container parent, int rows, int cols, int xpad, Spring x) {
        for (int c = 0; c < cols; c++) {
            Spring width = Spring.constant(0);
            for (int r = 0; r < rows; r++) {
                width = Spring.max(width,
                        getConstraintsForCell(r, c, parent, cols).getWidth());
            }
            for (int r = 0; r < rows; r++) {
                SpringLayout.Constraints constraints =
                        getConstraintsForCell(r, c, parent, cols);
                constraints.setX(x);
                constraints.setWidth(width);
            }
            x = Spring.sum(x, Spring.sum(width, Spring.constant(xpad)));
        }
        return x;
    }
}
