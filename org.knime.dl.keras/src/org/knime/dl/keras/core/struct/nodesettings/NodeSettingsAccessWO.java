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
package org.knime.dl.keras.core.struct.nodesettings;

import org.apache.commons.lang3.ClassUtils;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsWO;
import org.knime.dl.keras.core.struct.Member;
import org.knime.dl.keras.core.struct.Struct;
import org.knime.dl.keras.core.struct.access.AbstractStructAccess;
import org.knime.dl.keras.core.struct.access.DefaultMemberWriteAccess;
import org.knime.dl.keras.core.struct.access.MemberReadAccess;
import org.knime.dl.keras.core.struct.access.MemberWriteAccess;
import org.knime.dl.keras.core.struct.access.StructAccess;
import org.knime.dl.keras.core.struct.access.StructWriteAccess;
import org.knime.dl.keras.core.struct.access.ValueWriteAccess;

/**
 * @author Christian Dietz, KNIME GmbH, Konstanz, Germany
 */
class NodeSettingsAccessWO extends AbstractStructAccess<MemberWriteAccess<?, NodeSettingsWO>>
    implements StructWriteAccess<NodeSettingsWO, MemberWriteAccess<?, NodeSettingsWO>> {
    public NodeSettingsAccessWO(Struct struct) {
        super(struct);
        for (final Member<?> member : struct) {
            addMemberInstance(createMemberWriteAccess(member));
        }
    }

    private static <T> MemberWriteAccess<T, NodeSettingsWO> createMemberWriteAccess(Member<T> member) {
        NodeSettingsAccessFactoryRegistry registry = NodeSettingsAccessFactoryRegistry.getInstance();

        final Class<T> rawType = member.getRawType();
        ValueWriteAccess<T, NodeSettingsWO> writeAccess;
        if (ClassUtils.isPrimitiveOrWrapper(rawType) && !rawType.isArray()) {
            writeAccess = createPrimitiveAccessWO(member);
        } else if (rawType.isArray() && ClassUtils.isPrimitiveOrWrapper(rawType.getComponentType())) {
            writeAccess = createPrimitiveArrayAccessWO(member);
        } else if (rawType.equals(String.class)) {
            @SuppressWarnings("unchecked")
            final ValueWriteAccess<T, NodeSettingsWO> casted =
                (ValueWriteAccess<T, NodeSettingsWO>)new NodeSettingsStringAccessWO((Member<String>)member);
            writeAccess = casted;
        } else if (rawType.equals(String[].class)) {
            @SuppressWarnings("unchecked")
            final ValueWriteAccess<T, NodeSettingsWO> casted =
                (ValueWriteAccess<T, NodeSettingsWO>)createStringArrayAccessWO((Member<String[]>)member);
            writeAccess = casted;
        } else if (rawType.isEnum()) {
            writeAccess = createEnumAccessWO(member);
        } else if (registry.hasWriteAccessFactoryFor(rawType)) {
            writeAccess = registry.getWriteAccessFactoryFor(rawType).create(member);
        } else {
            writeAccess = new NodeSettingsObjectAccessWO<>(member);
        }

        return new DefaultMemberWriteAccess<>(member, writeAccess);
    }

    private static ValueWriteAccess<String[], NodeSettingsWO> createStringArrayAccessWO(Member<String[]> member) {
        return new NodeSettingsStringArrayAccessWO(member);
    }

    private static <T> ValueWriteAccess<T, NodeSettingsWO> createEnumAccessWO(Member<T> member) {
        return new AbstractNodeSettingsWriteAccess<T>(member) {

            @Override
            protected void setValue(NodeSettingsWO settings, T value) throws InvalidSettingsException {
                final Enum<?> casted = ((Enum<?>)value);
                settings.addString(member.getKey(), casted.name());
            }
        };
    }

    private static <T> ValueWriteAccess<T, NodeSettingsWO> createPrimitiveArrayAccessWO(Member<T> member) {
        return new NodeSettingsPrimitiveAccessWO<>(member);
    }

    private static <T> ValueWriteAccess<T, NodeSettingsWO> createPrimitiveAccessWO(Member<T> member) {
        return new NodeSettingsPrimitiveAccessWO<>(member);
    }
}