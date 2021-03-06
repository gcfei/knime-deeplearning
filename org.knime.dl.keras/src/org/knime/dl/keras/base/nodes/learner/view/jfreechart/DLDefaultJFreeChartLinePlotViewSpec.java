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
 */
package org.knime.dl.keras.base.nodes.learner.view.jfreechart;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * @author Christian Dietz, KNIME GmbH, Konstanz, Germany
 * @author Marcel Wiedenmann, KNIME GmbH, Konstanz, Germany
 * @author David Kolb, KNIME GmbH, Konstanz, Germany
 */
public class DLDefaultJFreeChartLinePlotViewSpec implements DLJFreeChartLinePlotViewSpec {

	private /* final */ String m_id;

	private /* final */ String m_title;

	private /* final */ String m_labelY;

	private /* final */ String m_labelX;

	private /* final */ String[] m_lineLabels;

	public DLDefaultJFreeChartLinePlotViewSpec(final String id, final String title, final String labelY,
			final String labelX, final String[] lineLabels) {
		m_id = id;
		m_title = title;
		m_labelY = labelY;
		m_labelX = labelX;
		m_lineLabels = lineLabels;
	}

	/**
	 * Empty deserialization constructor. Must not be called for other purposes.
	 */
	public DLDefaultJFreeChartLinePlotViewSpec() {
	}

	@Override
	public String id() {
		return m_id;
	}

	@Override
	public String title() {
		return m_title;
	}

	@Override
	public String labelY() {
		return m_labelY;
	}

	@Override
	public String labelX() {
		return m_labelX;
	}

	@Override
	public int numPlots() {
		return m_lineLabels.length;
	}

	@Override
	public String getLineLabel(final int i) {
		return m_lineLabels[i];
	}

	@Override
	public void writeExternal(final ObjectOutput objOut) throws IOException {
		objOut.writeUTF(m_id);
		objOut.writeUTF(m_title);
		objOut.writeUTF(m_labelX);
		objOut.writeUTF(m_labelY);
		objOut.writeObject(m_lineLabels);
	}

	@Override
	public void readExternal(final ObjectInput objIn) throws IOException, ClassNotFoundException {
		m_id = objIn.readUTF();
		m_title = objIn.readUTF();
		m_labelX = objIn.readUTF();
		m_labelY = objIn.readUTF();
		m_lineLabels = (String[]) objIn.readObject();
	}
}
