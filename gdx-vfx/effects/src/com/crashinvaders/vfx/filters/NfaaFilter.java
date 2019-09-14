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
import com.badlogic.gdx.math.Vector2;
import com.crashinvaders.vfx.gl.VfxGLUtils;

/**
 * Normal filtered anti-aliasing filter.
 * @author Toni Sagrista
 * @author metaphore
 */
public final class NfaaFilter extends ShaderVfxFilter {

    private static final String U_TEXTURE0 = "u_texture0";
    private static final String U_VIEWPORT_INVERSE = "u_viewportInverse";

    private final Vector2 viewportInverse = new Vector2();

    public NfaaFilter(boolean supportAlpha) {
        super(VfxGLUtils.compileShader(
                Gdx.files.classpath("shaders/screenspace.vert"),
                Gdx.files.classpath("shaders/nfaa.frag"),
                supportAlpha ? "#define SUPPORT_ALPHA" : ""));
    }

    @Override
    public void rebind() {
        super.rebind();
        program.begin();
        program.setUniformi(U_TEXTURE0, TEXTURE_HANDLE0);
        program.setUniformf(U_VIEWPORT_INVERSE, viewportInverse);
        program.end();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        this.viewportInverse.set(1f / width, 1f / height);
        setUniform(U_VIEWPORT_INVERSE, this.viewportInverse);
    }
}
