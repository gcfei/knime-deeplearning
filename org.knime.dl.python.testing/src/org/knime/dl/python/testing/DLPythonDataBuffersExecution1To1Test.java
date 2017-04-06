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
 * Jun 21, 2017 (marcel): created
 */
package org.knime.dl.python.testing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.knime.dl.core.DLAbstractLayerDataSpec;
import org.knime.dl.core.DLDefaultLayerData;
import org.knime.dl.core.DLDefaultLayerDataSpec;
import org.knime.dl.core.DLLayerData;
import org.knime.dl.core.DLLayerDataSpec;
import org.knime.dl.core.data.DLReadableDoubleBuffer;
import org.knime.dl.core.data.DLReadableFloatBuffer;
import org.knime.dl.core.data.DLReadableIntBuffer;
import org.knime.dl.core.data.DLReadableLongBuffer;
import org.knime.dl.python.core.data.DLPythonDoubleBuffer;
import org.knime.dl.python.core.data.DLPythonFloatBuffer;
import org.knime.dl.python.core.data.DLPythonIntBuffer;
import org.knime.dl.python.core.data.DLPythonLongBuffer;
import org.knime.dl.python.core.kernel.DLPythonKernel;
import org.knime.dl.python.core.kernel.DLPythonKernelConfig;
import org.knime.dl.util.DLUtils;

/**
 *
 * @author Marcel Wiedenmann,KNIME,Konstanz,Germany
 * @author Christian Dietz,KNIME,Konstanz,Germany
 */
public class DLPythonDataBuffersExecution1To1Test {

    private static final int IN_LAYER_DATA_NUM = 1;

    private static final String IN_LAYER_DATA_NAME = "test_in_data";

    private static final long[] IN_LAYER_DATA_SHAPE = new long[]{11, 10};

    private static final String OUT_LAYER_DATA_NAME = "test_out_data";

    private static final long[] OUT_LAYER_DATA_SHAPE = new long[]{11, 10};

    private static final String[] OUT_LAYER_DATA_SELECTED = {OUT_LAYER_DATA_NAME};

    private static final String BUNDLE_ID = "org.knime.dl.python.testing";

    private DLPythonKernel m_kernel;

    private Random m_rng;

    @Before
    public void setUp() throws IOException {
        m_kernel = new DLPythonKernel(new DLPythonKernelConfig());
        m_rng = new Random(543677);
    }

    @After
    public void tearDown() {
        m_kernel.close();
    }

    @Test
    public void testDouble() throws IOException {
        final ArrayList<DLLayerData<?>> layerData = new ArrayList<>(IN_LAYER_DATA_NUM);
        for (int i = 0; i < IN_LAYER_DATA_NUM; i++) {
            final DLLayerDataSpec spec =
                new DLAbstractLayerDataSpec(IN_LAYER_DATA_NAME, IN_LAYER_DATA_SHAPE, double.class) {
                };
            final DLPythonDoubleBuffer buff = new DLPythonDoubleBuffer(spec.getShape());
            for (int j = 0; j < buff.getCapacity(); j++) {
                final double val = m_rng.nextDouble() * m_rng.nextInt(Integer.MAX_VALUE / 5);
                buff.put(val);
            }
            layerData.add(new DLDefaultLayerData<>(spec, buff));
        }

        final HashMap<DLLayerDataSpec, DLLayerData<?>> networkInput = new HashMap<>();
        for (final DLLayerData<?> input : layerData) {
            networkInput.put(input.getSpec(), input);
        }

        m_kernel.putNetworkInput(networkInput, IN_LAYER_DATA_NUM);
        final String code = DLUtils.Files.readAllUTF8(
            DLUtils.Files.getFileFromBundle(BUNDLE_ID, "py/DLPythonDataBuffers1To1ExecutionTest_testDouble.py"));
        m_kernel.execute(code);

        final HashMap<DLLayerDataSpec, DLLayerData<?>> outputLayerDataSpecs = new HashMap<>();
        for (final String outputLayerDataName : OUT_LAYER_DATA_SELECTED) {
            final DLDefaultLayerDataSpec spec =
                new DLDefaultLayerDataSpec(outputLayerDataName, OUT_LAYER_DATA_SHAPE, double.class);
            outputLayerDataSpecs.put(spec, new DLDefaultLayerData<>(spec, new DLPythonDoubleBuffer(spec.getShape())));
        }
        m_kernel.getNetworkOutput(outputLayerDataSpecs);

        final DLLayerData<?> input = networkInput.values().iterator().next();
        final DLLayerData<?> output = outputLayerDataSpecs.values().iterator().next();
        Assert.assertArrayEquals(input.getSpec().getShape(), output.getSpec().getShape());
        Assert.assertEquals(input.getSpec().getElementType(), output.getSpec().getElementType());
        final DLReadableDoubleBuffer inputData = (DLReadableDoubleBuffer)input.getData();
        final DLReadableDoubleBuffer outputData = (DLReadableDoubleBuffer)output.getData();

        for (int i = 0; i < inputData.size(); i++) {
            Assert.assertEquals(inputData.readNextDouble() * 5, outputData.readNextDouble(), 0.0);
        }
    }

    @Test
    public void testFloat() throws IOException {
        final ArrayList<DLLayerData<?>> layerData = new ArrayList<>(IN_LAYER_DATA_NUM);
        for (int i = 0; i < IN_LAYER_DATA_NUM; i++) {
            final DLLayerDataSpec spec =
                new DLAbstractLayerDataSpec(IN_LAYER_DATA_NAME, IN_LAYER_DATA_SHAPE, float.class) {
                };
            final DLPythonFloatBuffer buff = new DLPythonFloatBuffer(spec.getShape());
            for (int j = 0; j < buff.getCapacity(); j++) {
                final float val = m_rng.nextFloat() * m_rng.nextInt(Short.MAX_VALUE / 5);
                buff.put(val);
            }
            layerData.add(new DLDefaultLayerData<>(spec, buff));
        }

        final HashMap<DLLayerDataSpec, DLLayerData<?>> networkInput = new HashMap<>();
        for (final DLLayerData<?> input : layerData) {
            networkInput.put(input.getSpec(), input);
        }

        m_kernel.putNetworkInput(networkInput, IN_LAYER_DATA_NUM);
        final String code = DLUtils.Files.readAllUTF8(
            DLUtils.Files.getFileFromBundle(BUNDLE_ID, "py/DLPythonDataBuffers1To1ExecutionTest_testFloat.py"));
        m_kernel.execute(code);

        final HashMap<DLLayerDataSpec, DLLayerData<?>> outputLayerDataSpecs = new HashMap<>();
        for (final String outputLayerDataName : OUT_LAYER_DATA_SELECTED) {
            final DLDefaultLayerDataSpec spec =
                new DLDefaultLayerDataSpec(outputLayerDataName, OUT_LAYER_DATA_SHAPE, float.class);
            outputLayerDataSpecs.put(spec, new DLDefaultLayerData<>(spec, new DLPythonFloatBuffer(spec.getShape())));
        }
        m_kernel.getNetworkOutput(outputLayerDataSpecs);

        final DLLayerData<?> input = networkInput.values().iterator().next();
        final DLLayerData<?> output = outputLayerDataSpecs.values().iterator().next();
        Assert.assertArrayEquals(input.getSpec().getShape(), output.getSpec().getShape());
        Assert.assertEquals(input.getSpec().getElementType(), output.getSpec().getElementType());
        final DLReadableFloatBuffer inputData = (DLReadableFloatBuffer)input.getData();
        final DLReadableFloatBuffer outputData = (DLReadableFloatBuffer)output.getData();

        for (int i = 0; i < inputData.size(); i++) {
            Assert.assertEquals(inputData.readNextFloat() * 5, outputData.readNextFloat(), 0.0f);
        }
    }

    @Test
    public void testInt() throws IOException {
        final ArrayList<DLLayerData<?>> layerData = new ArrayList<>(IN_LAYER_DATA_NUM);
        for (int i = 0; i < IN_LAYER_DATA_NUM; i++) {
            final DLLayerDataSpec spec =
                new DLAbstractLayerDataSpec(IN_LAYER_DATA_NAME, IN_LAYER_DATA_SHAPE, int.class) {
                };
            final DLPythonIntBuffer buff = new DLPythonIntBuffer(spec.getShape());
            for (int j = 0; j < buff.getCapacity(); j++) {
                final int val = m_rng.nextInt(Integer.MAX_VALUE / 5);
                buff.put(val);
            }
            layerData.add(new DLDefaultLayerData<>(spec, buff));
        }

        final HashMap<DLLayerDataSpec, DLLayerData<?>> networkInput = new HashMap<>();
        for (final DLLayerData<?> input : layerData) {
            networkInput.put(input.getSpec(), input);
        }

        m_kernel.putNetworkInput(networkInput, IN_LAYER_DATA_NUM);
        final String code = DLUtils.Files.readAllUTF8(
            DLUtils.Files.getFileFromBundle(BUNDLE_ID, "py/DLPythonDataBuffers1To1ExecutionTest_testInt.py"));
        m_kernel.execute(code);

        final HashMap<DLLayerDataSpec, DLLayerData<?>> outputLayerDataSpecs = new HashMap<>();
        for (final String outputLayerDataName : OUT_LAYER_DATA_SELECTED) {
            final DLDefaultLayerDataSpec spec =
                new DLDefaultLayerDataSpec(outputLayerDataName, OUT_LAYER_DATA_SHAPE, int.class);
            outputLayerDataSpecs.put(spec, new DLDefaultLayerData<>(spec, new DLPythonIntBuffer(spec.getShape())));
        }
        m_kernel.getNetworkOutput(outputLayerDataSpecs);

        final DLLayerData<?> input = networkInput.values().iterator().next();
        final DLLayerData<?> output = outputLayerDataSpecs.values().iterator().next();
        Assert.assertArrayEquals(input.getSpec().getShape(), output.getSpec().getShape());
        Assert.assertEquals(input.getSpec().getElementType(), output.getSpec().getElementType());
        final DLReadableIntBuffer inputData = (DLReadableIntBuffer)input.getData();
        final DLReadableIntBuffer outputData = (DLReadableIntBuffer)output.getData();

        for (int i = 0; i < inputData.size(); i++) {
            Assert.assertEquals(inputData.readNextInt() * 5, outputData.readNextInt());
        }
    }

    @Test
    public void testLong() throws IOException {
        final ArrayList<DLLayerData<?>> layerData = new ArrayList<>(IN_LAYER_DATA_NUM);
        for (int i = 0; i < IN_LAYER_DATA_NUM; i++) {
            final DLLayerDataSpec spec =
                new DLAbstractLayerDataSpec(IN_LAYER_DATA_NAME, IN_LAYER_DATA_SHAPE, long.class) {
                };
            final DLPythonLongBuffer buff = new DLPythonLongBuffer(spec.getShape());
            for (int j = 0; j < buff.getCapacity(); j++) {
                final long val = m_rng.nextLong() / 5;
                buff.put(val);
            }
            layerData.add(new DLDefaultLayerData<>(spec, buff));
        }

        final HashMap<DLLayerDataSpec, DLLayerData<?>> networkInput = new HashMap<>();
        for (final DLLayerData<?> input : layerData) {
            networkInput.put(input.getSpec(), input);
        }

        m_kernel.putNetworkInput(networkInput, IN_LAYER_DATA_NUM);
        final String code = DLUtils.Files.readAllUTF8(
            DLUtils.Files.getFileFromBundle(BUNDLE_ID, "py/DLPythonDataBuffers1To1ExecutionTest_testLong.py"));
        m_kernel.execute(code);

        final HashMap<DLLayerDataSpec, DLLayerData<?>> outputLayerDataSpecs = new HashMap<>();
        for (final String outputLayerDataName : OUT_LAYER_DATA_SELECTED) {
            final DLDefaultLayerDataSpec spec =
                new DLDefaultLayerDataSpec(outputLayerDataName, OUT_LAYER_DATA_SHAPE, long.class);
            outputLayerDataSpecs.put(spec, new DLDefaultLayerData<>(spec, new DLPythonLongBuffer(spec.getShape())));
        }
        m_kernel.getNetworkOutput(outputLayerDataSpecs);

        final DLLayerData<?> input = networkInput.values().iterator().next();
        final DLLayerData<?> output = outputLayerDataSpecs.values().iterator().next();
        Assert.assertArrayEquals(input.getSpec().getShape(), output.getSpec().getShape());
        Assert.assertEquals(input.getSpec().getElementType(), output.getSpec().getElementType());
        final DLReadableLongBuffer inputData = (DLReadableLongBuffer)input.getData();
        final DLReadableLongBuffer outputData = (DLReadableLongBuffer)output.getData();

        for (int i = 0; i < inputData.size(); i++) {
            Assert.assertEquals(inputData.readNextLong() * 5, outputData.readNextLong());
        }
    }
}