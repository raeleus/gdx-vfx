
/*******************************************************************************
 * Copyright 2019 metaphore
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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.crashinvaders.vfx.VfxFilter;
import com.crashinvaders.vfx.gl.VfxGLUtils;

/** Motion blur filter that draws the last frame (motion filter included) with a lower opacity.
 * @author Toni Sagrista */
public class MotionBlurFilter extends VfxFilter<MotionBlurFilter> {

	private float blurOpacity = 0.5f;
	private Texture lastFrameTex;

	/** Defines which function will be used to mix the two frames to produce motion blur effect. */
	public enum BlurFunction {
		MAX("motionblur-max"),
		MIX("motionblur-mix");

		final String fragmentShaderName;

		BlurFunction(String fragmentShaderName) {
			this.fragmentShaderName = fragmentShaderName;
		}
	}

	public enum Param implements Parameter {
        Texture("u_texture0", 0),
        LastFrame("u_texture1", 0),
        BlurOpacity("u_blurOpacity", 0);

        private String mnemonic;
        final int elementSize;

        Param(String mnemonic, int arrayElementSize) {
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

	public MotionBlurFilter(BlurFunction blurFunction) {
		super(VfxGLUtils.compileShader(
				Gdx.files.classpath("shaders/screenspace.vert"),
				Gdx.files.classpath("shaders/" + blurFunction.fragmentShaderName + ".frag")));
		rebind();
	}

	public void setBlurOpacity (float blurOpacity) {
		this.blurOpacity = blurOpacity;
		setParam(Param.BlurOpacity, this.blurOpacity);
	}

	public void setLastFrameTexture (Texture tex) {
		this.lastFrameTex = tex;
		if (lastFrameTex != null) {
			setParam(Param.LastFrame, u_texture1);
		}
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void rebind () {
		setParams(Param.Texture, u_texture0);
		if (lastFrameTex != null) {
			setParams(Param.LastFrame, u_texture1);
		}
		setParams(Param.BlurOpacity, this.blurOpacity);
		endParams();
	}

	@Override
	protected void onBeforeRender () {
		inputTexture.bind(u_texture0);
		if (lastFrameTex != null) {
			lastFrameTex.bind(u_texture1);
		}
	}
}