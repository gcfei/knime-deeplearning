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
 *   Jun 28, 2017 (marcel): created
 */
package org.knime.dl.python.core.data;

import static com.google.common.base.Preconditions.checkArgument;

import org.knime.core.data.DataCell;

/**
 *
 * @author Marcel Wiedenmann, KNIME, Konstanz, Germany
 * @author Christian Dietz, KNIME, Konstanz, Germany
 */
public abstract class AbstractDLPythonDataDataBuffer<S> extends DataCell implements DLPythonDataBuffer<S> {

	private static final long serialVersionUID = 1L;

	protected S m_storage;

	// TODO: capacity should be 'long' sometime
	private final int m_capacity;

	protected AbstractDLPythonDataDataBuffer(long capacity) {
		checkArgument(capacity <= Integer.MAX_VALUE,
				"Input shape is too big. Buffer only supports capacities up to " + Integer.MAX_VALUE + ".");
		m_capacity = (int) capacity;
		m_storage = createStorage();
	}

	protected abstract S createStorage();

	@Override
	public S getStorage() {
		return m_storage;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCapacity() {
		return m_capacity;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Buffer with capacity: " + m_capacity;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws Exception {
		m_storage = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return m_storage.hashCode();
	}

	protected void setValidatedStorage(final S storage) {
		m_storage = storage;
	}
}