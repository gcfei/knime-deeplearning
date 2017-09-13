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
 */
package org.knime.dl.keras.cntk.core;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.URL;

import org.knime.dl.core.DLInvalidContextException;
import org.knime.dl.core.DLInvalidSourceException;
import org.knime.dl.keras.core.DLKerasAbstractNetworkLoader;
import org.knime.dl.python.core.DLPythonContext;
import org.knime.dl.python.core.DLPythonNetworkHandle;
import org.knime.dl.python.core.DLPythonNumPyTypeMap;

/**
 * @author Marcel Wiedenmann, KNIME, Konstanz, Germany
 * @author Christian Dietz, KNIME, Konstanz, Germany
 */
public final class DLKerasCNTKNetworkLoader extends DLKerasAbstractNetworkLoader<DLKerasCNTKNetwork> {

	private static final long serialVersionUID = 1L;

	@Override
	public DLKerasCNTKNetwork fetch(final DLPythonNetworkHandle handle, final URL source, final DLPythonContext context)
			throws IllegalArgumentException, DLInvalidSourceException, DLInvalidContextException, IOException {
		validateSource(source);
		final DLKerasCNTKCommands commands = createCommands(checkNotNull(context), true);
		final DLKerasCNTKNetworkSpec spec =
				commands.extractNetworkSpec(checkNotNull(handle), DLPythonNumPyTypeMap.INSTANCE);
		return new DLKerasCNTKNetwork(spec, source);
	}

	@Override
	protected DLKerasCNTKCommands createCommands(final DLPythonContext context, final boolean initialize)
			throws DLInvalidContextException {
		final DLKerasCNTKCommands commands = new DLKerasCNTKCommands(context);
		if (initialize) {
			commands.setupEnvironment();
			commands.testInstallation();
			commands.registerBackends();
			commands.setupBackend();
		}
		return commands;
	}
}
