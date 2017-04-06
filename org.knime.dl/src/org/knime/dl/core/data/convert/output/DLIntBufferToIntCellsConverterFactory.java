/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 * History
 *   Jun 30, 2017 (marcel): created
 */
package org.knime.dl.core.data.convert.output;

import java.util.function.Consumer;

import org.knime.core.data.def.IntCell;
import org.knime.core.node.ExecutionContext;
import org.knime.dl.core.DLLayerData;
import org.knime.dl.core.DLLayerDataSpec;
import org.knime.dl.core.data.DLReadableIntBuffer;
import org.knime.dl.util.DLUtils;

/**
 *
 * @author Marcel Wiedenmann, KNIME, Konstanz, Germany
 * @author Christian Dietz, KNIME, Konstanz, Germany
 */
public class DLIntBufferToIntCellsConverterFactory
    implements DLLayerDataToDataCellConverterFactory<DLReadableIntBuffer, IntCell> {

    @Override
    public String getName() {
        return "To IntCell";
    }

    @Override
    public Class<DLReadableIntBuffer> getBufferType() {
        return DLReadableIntBuffer.class;
    }

    @Override
    public Class<IntCell> getDestType() {
        return IntCell.class;
    }

    @Override
    public DLLayerDataToDataCellConverter<DLReadableIntBuffer, IntCell> createConverter() {
        return new DLLayerDataToDataCellConverter<DLReadableIntBuffer, IntCell>() {

            @Override
            public void convert(final ExecutionContext exec, final DLLayerData<DLReadableIntBuffer> input,
                final Consumer<IntCell> out) {
                final DLReadableIntBuffer buf = input.getData();
                for (int i = 0; i < buf.size(); i++) {
                    out.accept(new IntCell(buf.readNextInt()));
                }
            }
        };
    }

    @Override
    public int getDestCount(final DLLayerDataSpec spec) {
        // TODO for now we assume int size ... as several frameworks only
        // support int sized inputs/outputs layers...
        return (int)DLUtils.Shapes.getSize(spec.getShape());
    }
}