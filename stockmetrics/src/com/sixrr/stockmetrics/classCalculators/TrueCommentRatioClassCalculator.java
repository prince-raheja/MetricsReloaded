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

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElementVisitor;
import com.sixrr.metrics.utils.ClassUtils;
import com.sixrr.stockmetrics.utils.LineUtil;

public class TrueCommentRatioClassCalculator extends ClassCalculator {
    private int commentLines = 0;

    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        public void visitClass(PsiClass aClass) {
            super.visitClass(aClass);
            int prevCommentLines = 0;
            if (!ClassUtils.isAnonymous(aClass)) {
                prevCommentLines = commentLines;
                commentLines = 0;
            }
            super.visitClass(aClass);

            if (!ClassUtils.isAnonymous(aClass)) {
                if (aClass.isInterface()) {
                    commentLines = prevCommentLines;
                } else {
                    int linesOfCode = LineUtil.countLines(aClass);
                    final PsiClass[] innerClasses = aClass.getInnerClasses();
                    for (PsiClass innerClass : innerClasses) {
                        linesOfCode -= LineUtil.countLines(innerClass);
                    }
                    postMetric(aClass, commentLines, linesOfCode - commentLines);
                    commentLines = prevCommentLines;
                }
            }
        }

        public void visitComment(PsiComment comment) {
            super.visitComment(comment);
            commentLines += LineUtil.countCommentOnlyLines(comment);
        }
    }
}