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
 *   Jun 28, 2017 (marcel): created
 */
package org.knime.dl.python.core.data;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;

import org.knime.core.data.DataType;
import org.knime.dl.core.data.DLDefaultLongBuffer;
import org.knime.dl.core.data.DLReadableLongBuffer;
import org.knime.dl.core.data.DLWritableLongBuffer;

/**
 * Long type implementation of {@link DLPythonAbstractDataBuffer}.
 *
 * @author Marcel Wiedenmann, KNIME GmbH, Konstanz, Germany
 * @author Christian Dietz, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("serial") // not intended for serialization
public class DLPythonLongBuffer extends DLPythonAbstractDataBuffer<DLDefaultLongBuffer, long[]>
		implements DLWritableLongBuffer, DLReadableLongBuffer {

	/**
	 * This buffer's {@link DataType}.
	 */
	public static final DataType TYPE = DataType.getType(DLPythonLongBuffer.class);

	/**
	 * Creates a new instance of this buffer.
	 *
	 * @param capacity the immutable capacity of the buffer
	 */
	public DLPythonLongBuffer(final long capacity) {
		super(new DLDefaultLongBuffer(capacity));
	}

	@Override
	public long readNextLong() throws BufferUnderflowException {
		return m_buffer.readNextLong();
	}

	@Override
	public long[] toLongArray() {
		return m_buffer.toLongArray();
	}

	@Override
	public void put(final boolean value) throws BufferOverflowException {
		m_buffer.put(value);
	}

	@Override
	public void putAll(final boolean[] values) throws BufferOverflowException {
		m_buffer.putAll(values);
	}

	@Override
	public void put(final byte value) throws BufferOverflowException {
		m_buffer.put(value);
	}

	@Override
	public void putAll(final byte[] values) throws BufferOverflowException {
		m_buffer.putAll(values);
	}

	@Override
	public void put(final int value) throws BufferOverflowException {
		m_buffer.put(value);
	}

	@Override
	public void putAll(final int[] values) throws BufferOverflowException {
		m_buffer.putAll(values);
	}

	@Override
	public void put(final long value) throws BufferOverflowException {
		m_buffer.put(value);
	}

	@Override
	public void putAll(final long[] values) throws BufferOverflowException {
		m_buffer.putAll(values);
	}

	@Override
	public void put(final short value) throws BufferOverflowException {
		m_buffer.put(value);
	}

	@Override
	public void putAll(final short[] values) throws BufferOverflowException {
		m_buffer.putAll(values);
	}

	@Override
	public void readToLongArray(long[] dest, int destPos, int length) {
		m_buffer.readToLongArray(dest, destPos, length);
	}
}
