/*******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.crashinvaders.vfx.filters;

import com.badlogic.gdx.math.Vector2;
import com.crashinvaders.vfx.PostProcessorFilter;
import com.crashinvaders.vfx.utils.ShaderLoader;

/** Scattering Light effect.
 * @see <a href="https://medium.com/community-play-3d/god-rays-whats-that-5a67f26aeac2">https://medium.com/community-play-3d/god-
 *      rays-whats-that-5a67f26aeac2</a>
 * @author Toni Sagrista **/
public final class ScatteringFilter extends PostProcessorFilter<ScatteringFilter> {

    private final Vector2 viewport = new Vector2();

    private float[] lightPositions;
    private float[] lightViewAngles;
    private int lightAmount;

    private float decay = 0.96815f;
    private float density = 0.926f;
    private float weight = 0.58767f;

    private int numSamples = 100;

//    /// NUM_SAMPLES will describe the rays quality, you can play with
//    int NUM_SAMPLES = 100;

    public enum Param implements PostProcessorFilter.Parameter {
        // @formatter:off
        Texture("u_texture0", 0),
        LightPositions("u_lightPositions", 2),
        LightViewAngles("u_lightViewAngles", 1),
        Viewport("u_viewport", 2),
        NLights("u_nLights", 0),
        Decay("u_decay", 0),
        Density("u_density", 0),
        Weight("u_weight", 0),
        NumSamples("u_numSamples", 0);
        // @formatter:on

        private String mnemonic;
        private int elementSize;

        private Param(String mnemonic, int arrayElementSize) {
            this.mnemonic = mnemonic;
            this.elementSize = arrayElementSize;
        }

        @Override
        public String mnemonic() {
            return this.mnemonic;
        }

        @Override
        public int arrayElementSize() {
            return this.elementSize;
        }
    }

    public ScatteringFilter(float[] positions, float[] angles) {
        super(ShaderLoader.fromFile("screenspace", "lightscattering"));
        setLights(positions, angles);
        rebind();
    }

    public void setLights(float[] positions, float[] angles) {
        if (positions.length / 2 != angles.length) {
            throw new IllegalArgumentException("Position array size doesn't match to angles' doubled size.");
        }

        this.lightAmount = angles.length;
        this.lightPositions = positions;
        this.lightViewAngles = angles;
        setParam(Param.NLights, this.lightAmount);
        setParamv(Param.LightPositions, lightPositions, 0, lightPositions.length);
        setParamv(Param.LightViewAngles, lightViewAngles, 0, lightViewAngles.length);
    }

    public float getDecay() {
        return decay;
    }

    public void setDecay(float decay) {
        this.decay = decay;
        setParam(Param.Decay, decay);
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
        setParam(Param.Density, density);
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
        setParam(Param.Weight, weight);
    }

    public int getNumSamples() {
        return numSamples;
    }

    public void setNumSamples(int numSamples) {
        this.numSamples = numSamples;
        setParam(Param.NumSamples, numSamples);
    }

    @Override
    public void resize(int width, int height) {
        this.viewport.set(width, height);
        setParam(Param.Viewport, this.viewport);
    }

    @Override
    public void rebind() {
        // Re-implement super to batch every parameter
        setParams(Param.Texture, u_texture0);
        setParams(Param.NLights, this.lightAmount);
        setParams(Param.Viewport, viewport);
        setParamsv(Param.LightPositions, lightPositions, 0, lightPositions.length);
        setParamsv(Param.LightViewAngles, lightViewAngles, 0, lightViewAngles.length);
        setParams(Param.Decay, decay);
        setParams(Param.Density, density);
        setParams(Param.Weight, weight);
        setParams(Param.NumSamples, numSamples);
        endParams();
    }

    @Override
    protected void onBeforeRender() {
        inputTexture.bind(u_texture0);
    }
}
