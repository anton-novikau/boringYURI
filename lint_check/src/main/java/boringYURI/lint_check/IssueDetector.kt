package boringYURI.lint_check

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UElement

@Suppress("UnstableApiUsage")
class IssueDetector : Detector(), Detector.UastScanner {

    override fun applicableAnnotations() = listOf("boringyuri.api.Path", "boringyuri.api.Param")

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
        val pathAnnotation = annotations.find { it.qualifiedName?.contains("boringyuri.api.Path") == true }
        val paramAnnotation = annotations.find { it.qualifiedName?.contains("boringyuri.api.Param") == true }

        if (pathAnnotation != null && paramAnnotation != null) {
            pathAnnotation.also {
                val message = "You cannot use @Path an @Param together applying to one field"
                val location = context.getLocation(it)
                context.report(ISSUE, it, location, message)
            }
            paramAnnotation.also {
                val message = "You cannot use @Path an @Param together applying to one field"
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

        val ISSUE = Issue.create(
            id = "BoringYURILintDetector",
            briefDescription = "You cannot use @Path an @Param together applying to one field",
            explanation = """
                You cannot use @Path an @Param together applying to one field
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 9,
            severity = Severity.ERROR,
            implementation = IMPLEMENTATION
        )
    }

}