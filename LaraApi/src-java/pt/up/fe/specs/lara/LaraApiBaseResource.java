/**
 * Copyright 2013 SuikaSoft.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

package pt.up.fe.specs.lara;

import org.lara.interpreter.weaver.utils.LaraResourceProvider;

/**
 * @author Joao Bispo
 *
 */
public enum LaraApiBaseResource implements LaraResourceProvider {

    // Code
    ENERGY_BASE("code/EnergyBase.lara"),
    LOGGER_BASE("code/LoggerBase.lara"),
    TIMER_BASE("code/TimerBase.lara"),

    // Lara
    COMPILATION_BASE("CompilationBase.lara");

    private final String resource;

    private static final String BASE_PACKAGE = "lara/";

    /**
     * @param resource
     */
    private LaraApiBaseResource(String resource) {
        this.resource = BASE_PACKAGE + resource;
    }

    /* (non-Javadoc)
     * @see org.suikasoft.SharedLibrary.Interfaces.ResourceProvider#getResource()
     */
    @Override
    public String getOriginalResource() {
        return resource;
    }

}
