/*
 * Copyright 2005, Sixth and Red River Software
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.sixrr.stockmetrics.classCalculators;

import com.intellij.psi.*;

public class WeightedMethodComplexityCalculator extends ClassCalculator {
    private int complexity = 0;

    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        public void visitClass(PsiClass aClass) {
            int prevComplexity = 0;
            if (isConcreteClass(aClass)) {
                prevComplexity = complexity;
                complexity = 0;
            }
            super.visitClass(aClass);

            if (isConcreteClass(aClass)) {
                postMetric(aClass, complexity);
                complexity = prevComplexity;
            }
        }

        public void visitMethod(PsiMethod method) {
            super.visitMethod(method);
            if (!method.hasModifierProperty(PsiModifier.ABSTRACT)) {
                complexity++;
            }
        }

        public void visitForStatement(PsiForStatement statement) {
            super.visitForStatement(statement);
            complexity++;
        }

        public void visitForeachStatement(PsiForeachStatement statement) {
            super.visitForeachStatement(statement);
            complexity++;
        }

        public void visitIfStatement(PsiIfStatement statement) {
            super.visitIfStatement(statement);
            complexity++;
        }

        public void visitDoWhileStatement(PsiDoWhileStatement statement) {
            super.visitDoWhileStatement(statement);
            complexity++;
        }

        public void visitConditionalExpression(PsiConditionalExpression expression) {
            super.visitConditionalExpression(expression);
            complexity++;
        }

        public void visitSwitchStatement(PsiSwitchStatement statement) {
            super.visitSwitchStatement(statement);
            final PsiCodeBlock body = statement.getBody();
            if (body == null) {
                return;
            }
            final PsiStatement[] statements = body.getStatements();
            boolean pendingLabel = false;
            for (final PsiStatement child : statements) {
                if (child instanceof PsiSwitchLabelStatement) {
                    if (!pendingLabel) {
                        complexity++;
                    }
                    pendingLabel = true;
                } else {
                    pendingLabel = false;
                }
            }
        }

        public void visitWhileStatement(PsiWhileStatement statement) {
            super.visitWhileStatement(statement);
            complexity++;
        }
    }
}