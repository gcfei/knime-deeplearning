/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
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
package org.knime.dl.core.data.convert;

import java.util.OptionalLong;

import org.knime.core.data.DataType;
import org.knime.core.data.vector.bitvector.DenseBitVector;
import org.knime.core.data.vector.bitvector.DenseBitVectorCell;
import org.knime.core.data.vector.bitvector.DenseBitVectorCellFactory;
import org.knime.dl.core.DLTensorSpec;
import org.knime.dl.core.data.DLReadableBitBuffer;
import org.knime.dl.util.DLUtils;

/**
 * @author Benjamin Wilhelm, KNIME GmbH, Konstanz, Germany
 * @author Marcel Wiedenmann, KNIME GmbH, Konstanz, Germany
 * @author Christian Dietz, KNIME GmbH, Konstanz, Germany
 */
public class DLBitTensorToBitVectorCellConverterFactory
    implements DLTensorToDataCellConverterFactory<DLReadableBitBuffer, DenseBitVectorCell> {

    @Override
    public String getName() {
        return DataType.getType(DenseBitVectorCell.class).toPrettyString();
    }

    @Override
    public Class<DLReadableBitBuffer> getBufferType() {
        return DLReadableBitBuffer.class;
    }

    @Override
    public DataType getDestType() {
        return DataType.getType(DenseBitVectorCell.class);
    }

    @Override
    public OptionalLong getDestCount(final DLTensorSpec spec) {
        return OptionalLong.of(1);
    }

    @Override
    public DLTensorToDataCellConverter<DLReadableBitBuffer, DenseBitVectorCell> createConverter() {
        return (input, out, exec) -> {
            final long bufferSize = input.getBuffer().size();
            final long exampleSize = DLUtils.Shapes.getFixedSize(input.getSpec().getShape()).getAsLong();
            final DLReadableBitBuffer buf = input.getBuffer();
            for (int i = 0; i < bufferSize / exampleSize; i++) {
                final DenseBitVector vector = new DenseBitVector(exampleSize);
                for (long j = 0; j < exampleSize; j++) {
                    vector.set(j, buf.readNextBit());
                }
                out[i] = (new DenseBitVectorCellFactory(vector)).createDataCell();
            }
        };
    }
}
