/*
 * Copyright 2013 SPeCS.
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
package larac.objects;

import pt.up.fe.specs.util.enums.EnumHelperWithValue;
import pt.up.fe.specs.util.lazy.Lazy;
import pt.up.fe.specs.util.providers.StringProvider;
import tdrc.utils.StringUtils;

public class Enums {
    // private static final String CODE_PARAM_REGEX = "(?!\\[)(\\s*\\S+?\\s*)"; // no spaces in between
    private static final String CODE_PARAM_REGEX = "(?!\\[)(.+?)";
    public static String SYMBOL_REGEX_BEGIN = "\\[\\[";
    public static String SYMBOL_REGEX_END = "\\]\\]";
    public static String SYMBOL_BEGIN = "[[";
    public static String SYMBOL_END = "]]";
    // public static String INSERT_SYMBOL_REGEX = Enums.SYMBOL_REGEX_BEGIN + "(?!\\[)(\\S+?)" + Enums.SYMBOL_REGEX_END;
    public static String INSERT_SYMBOL_REGEX = Enums.SYMBOL_REGEX_BEGIN + Enums.CODE_PARAM_REGEX
            + Enums.SYMBOL_REGEX_END;

    public static enum JoinOperator {
        UNION_JOIN("+"),
        NATURAL_JOIN("::"),
        BITAND_JOIN("&"),;
        private String op;

        JoinOperator(String op) {
            setOp(op);
        }

        public void setOp(String op) {
            this.op = op;
        }

        public String getOp() {
            return op;
        }

        public static JoinOperator getOpTag(String text) {
            if (text != null) {
                for (final JoinOperator b : JoinOperator.values()) {
                    if (text.equalsIgnoreCase(b.op)) {
                        return b;
                    }
                }
            }
            return null;
        }

        public static boolean contains(String text) {
            if (text != null) {
                for (final JoinOperator b : JoinOperator.values()) {
                    if (text.equalsIgnoreCase(b.op)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * Binary operators ordered by precedence: less ordinal means less precedence
     * 
     * @author Tiago
     *
     */
    public static enum BinaryOperator implements StringProvider {

        COMMA(",", 0), // 0
        OR("||", 1), // 1
        AND("&&", 2), // 2
        BitOR("|", 3), // 3
        BitXOR("^", 4), // 4
        BitAND("&", 5), // 5

        SEQ("===", 6), // 6
        NSEQ("!==", 6), // 6
        EQ("==", 6), // 6
        NEQ("!=", 6), // 6
        MATCH("~=", 6), // 6

        LT("<", 7), // 7
        LTE("<=", 7), // 7
        GT(">", 7), // 7
        GTE(">=", 7), // 7
        INSTANCEOF("instanceof", 7), // 7
        IN("in", 7), // 7

        SHL("<<", 8), // 8
        SHR(">>", 8), // 8
        USHR(">>>", 8), // 8

        ADD("+", 9), // 9
        SUB("-", 9), // 9

        MULT("*", 10), // 10
        DIV("/", 10), // 10
        MOD("%", 10), // 10

        ;

        private static final Lazy<EnumHelperWithValue<BinaryOperator>> ENUM_HELPER = EnumHelperWithValue
                .newLazyHelperWithValue(BinaryOperator.class);

        private String op;
        private final int precedence;

        BinaryOperator(String op, int precedence) {
            setOp(op);
            this.precedence = precedence;
        }

        public void setOp(String op) {
            this.op = op;
        }

        public String getOp() {
            return op;
        }

        public static BinaryOperator getOpTag(String text) {
            if (text != null) {
                for (final BinaryOperator b : BinaryOperator.values()) {
                    if (text.equalsIgnoreCase(b.op)) {
                        return b;
                    }
                }
            }
            return null;
        }

        public static boolean contains(String text) {
            if (text != null) {
                for (final BinaryOperator b : BinaryOperator.values()) {
                    if (text.equalsIgnoreCase(b.op)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public boolean hasPrecedence(BinaryOperator operator) {
            return precedence > operator.precedence;
        }

        @Override
        public String getString() {
            return name();
        }

        public static EnumHelperWithValue<BinaryOperator> getHelper() {
            return ENUM_HELPER.get();
        }
    }

    public static enum UnaryOperator implements StringProvider {
        POS("+"),
        NEG("-"),
        NOT("!"),
        INV("~"),
        INCP("++"),
        DECP("--"),
        TYPEOF("typeof"),
        DELETE("delete"),
        VOID("void");
        // NEW("new");

        private static final Lazy<EnumHelperWithValue<UnaryOperator>> ENUM_HELPER = EnumHelperWithValue
                .newLazyHelperWithValue(UnaryOperator.class);

        private String op;

        UnaryOperator(String op) {
            setOp(op);
        }

        public void setOp(String op) {
            this.op = op;
        }

        public String getOp() {
            return op;
        }

        public static UnaryOperator getOpTag(String text) {
            if (text != null) {
                for (final UnaryOperator b : UnaryOperator.values()) {
                    if (text.equalsIgnoreCase(b.op)) {
                        return b;
                    }
                }
            }
            return null;
        }

        public static boolean contains(String text) {
            if (text != null) {
                for (final UnaryOperator b : UnaryOperator.values()) {
                    if (text.equalsIgnoreCase(b.op)) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public String getString() {
            return name();
        }

        public static EnumHelperWithValue<UnaryOperator> getHelper() {
            return ENUM_HELPER.get();
        }
    }

    public static enum AssignOperator {
        ASSIGN("="),
        ASSIGN_ADD("+="),
        ASSIGN_SUB("-="),
        ASSIGN_MULT("*="),
        ASSIGN_DIV("/="),
        ASSIGN_MOD(
                "%="),
        ASSIGN_SHL("<<="),
        ASSIGN_SHR(">>="),
        ASSIGN_USHR(">>>="),
        ASSIGN_BitAND("&="),
        ASSIGN_BitXOR(
                "^="),
        ASSIGN_BitOR("|=")

        ;
        /*
         * "<<=" <op name='ASSIGN_SHL'/> ">>=" <op name='ASSIGN_SHR'/>
         * ">>>=" <op name='ASSIGN_USHR'/> "&=" "^=" <op
         * name='ASSIGN_BitAND'/> "^=" <op name='ASSIGN_BitXOR'/> "|="
         * <op name='ASSIGN_BitOR'/>
         * 
         * 
         */

        private String op;

        AssignOperator(String op) {
            setOp(op);
        }

        public void setOp(String op) {
            this.op = op;
        }

        public String getOp() {
            return op;
        }

        public static AssignOperator getOpTag(String text) {
            if (text != null) {
                for (final AssignOperator b : AssignOperator.values()) {
                    if (text.equalsIgnoreCase(b.op)) {
                        return b;
                    }
                }
            }
            return null;
        }

        public static boolean contains(String text) {
            if (text != null) {
                for (final AssignOperator b : AssignOperator.values()) {
                    if (text.equalsIgnoreCase(b.op)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public static enum Types {
        Null,
        Boolean,
        Int,
        Float,
        Double,
        String,
        RegEx,
        Code,
        Joinpoint,
        Aspect,
        Array,
        Map,
        Object,
        FN,
        FNDecl,
        Exception,
        Undefined,
        AspectSTATIC,
        Base64;
        public static Types getDefault() {
            return Undefined;
        }

        public static Types maxType(Types leftType, Types rightType) {
            return leftType.compareTo(rightType) >= 0 ? leftType : rightType;
        }

        public static Types value(String t) {
            if (t.equals("Integer")) {
                return Int;
            }
            t = StringUtils.firstCharToUpper(t);
            try {
                return valueOf(t);
            } catch (final Exception e) {
                return null;
            }
        }

        @Override
        public String toString() {
            return name().toLowerCase();
        }

    }

    public static enum LoopTypes {
        WHILE,
        DO,
        FOR,
        FORIN,
        FOREACH;
    }
}
