/**
 * Copyright 2019 SPeCS.
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

package pt.up.fe.specs.lara.loc;

import java.io.File;

import org.lara.language.specification.dsl.LanguageSpecificationV2;
import org.suikasoft.jOptions.DataStore.ADataClass;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;

import larac.LaraC;
import pt.up.fe.specs.lara.aspectir.Aspects;
import pt.up.fe.specs.lara.loc.visitors.CommentVisitor;
import pt.up.fe.specs.lara.loc.visitors.FunctionsVisitor;
import pt.up.fe.specs.lara.loc.visitors.StatementsVisitor;
import pt.up.fe.specs.util.SpecsLogs;

public class LaraStats extends ADataClass<LaraStats> {

    /// DATAKEYS BEGIN

    public static final DataKey<Integer> LARA_STMTS = KeyFactory.integer("laraStmts");
    public static final DataKey<Integer> ASPECTS = KeyFactory.integer("aspects");
    public static final DataKey<Integer> FUNCTIONS = KeyFactory.integer("functions");
    public static final DataKey<Integer> COMMENTS = KeyFactory.integer("comments");

    /// DATAKEYS END

    private final LanguageSpecificationV2 languageSpecification;

    public LaraStats(LanguageSpecificationV2 languageSpecification) {
        super();
        this.languageSpecification = languageSpecification;
    }

    /**
     * Parses the given files and adds its stats to this instance.
     * 
     * @param laraFile
     */
    public void addFileStats(File laraFile) {
        Aspects aspects = LaraC.parseLara(laraFile, languageSpecification).orElse(null);
        if (aspects == null) {
            SpecsLogs.info("LaraLoc: Could not parse file '" + laraFile.getAbsolutePath() + "'");
            return;
        }

        CommentVisitor commentVisitor = new CommentVisitor();
        aspects.visitDepthFirst(commentVisitor);

        StatementsVisitor stmtsVisitor = new StatementsVisitor();
        aspects.visitDepthFirst(stmtsVisitor);

        FunctionsVisitor functionsVisitor = new FunctionsVisitor();
        aspects.visitDepthFirst(functionsVisitor);

        inc(ASPECTS, aspects.aspects.size());
        inc(COMMENTS, commentVisitor.getCommentLines());
        inc(LARA_STMTS, stmtsVisitor.getNumStmts());
        inc(FUNCTIONS, functionsVisitor.getNumFunctions());

        // System.out.println("# stmts: " + stmtsVisitor.getNumStmts());
        // System.out.println("# aspects: " + aspects.aspects.size());
        // System.out.println("# comment lines: " + commentVisitor.getCommentLines());
    }

    public void addFileStats(LaraStats fileStats) {
        // Increment all keys that are Numbers
        inc(fileStats);
    }
}
