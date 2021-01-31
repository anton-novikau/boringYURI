package boringYURI.lint_check

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UElement

@Suppress("UnstableApiUsage")
class IssueDetector : Detector(), Detector.UastScanner {

    override fun applicableAnnotations() = listOf(PATH_ANNOTATION_NAME, PARAM_ANNOTATION_NAME)

    override fun visitAnnotationUsage(
        context: JavaContext,
        usage: UElement,
        type: AnnotationUsageType,
        annotation: UAnnotation,
        qualifiedName: String,
        method: PsiMethod?,
        annotations: List<UAnnotation>,
        allMemberAnnotations: List<UAnnotation>,
        allClassAnnotations: List<UAnnotation>,
        allPackageAnnotations: List<UAnnotation>
    ) {
        val pathAnnotation =
            annotations.find { it.qualifiedName?.contains(PATH_ANNOTATION_NAME) == true }
        val paramAnnotation =
            annotations.find { it.qualifiedName?.contains(PARAM_ANNOTATION_NAME) == true }

        if (pathAnnotation != null && paramAnnotation != null) {
            pathAnnotation.also {
                val message = BRIEF_DESCRIPTION
                val location = context.getLocation(it)
                context.report(ISSUE, it, location, message)
            }
            paramAnnotation.also {
                val message = BRIEF_DESCRIPTION
                val location = context.getLocation(it)
                context.report(ISSUE, it, location, message)
            }
        }
    }

    companion object {
        private val IMPLEMENTATION = Implementation(
            IssueDetector::class.java,
            Scope.JAVA_FILE_SCOPE
        )

        private const val PATH_ANNOTATION_NAME = "boringyuri.api.Path"
        private const val PARAM_ANNOTATION_NAME = "boringyuri.api.Param"
        private const val DETECTOR_NAME = "BoringYURILintDetector"
        private const val BRIEF_DESCRIPTION =
            "You cannot use @Path an @Param together applying to one field"
        private const val EXPLANATION =
            "You cannot use @Path an @Param together applying to one field"

        val ISSUE = Issue.create(
            id = DETECTOR_NAME,
            briefDescription = BRIEF_DESCRIPTION,
            explanation = EXPLANATION,
            category = Category.CORRECTNESS,
            priority = 9,
            severity = Severity.ERROR,
            implementation = IMPLEMENTATION
        )
    }

}