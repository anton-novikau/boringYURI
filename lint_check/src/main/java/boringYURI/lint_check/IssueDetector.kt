package boringYURI.lint_check

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UElement

@Suppress("UnstableApiUsage")
class IssueDetector : Detector(), SourceCodeScanner {

    override fun applicableAnnotations() = listOf("boringyuri.api.Path")

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
        val message = String.format(
            "TEST",
            getInternalMethodName(method ?: return)
        )
        val location = context.getLocation(annotation)
        context.report(ISSUE, annotation, location, message)
    }

    companion object {
        private val IMPLEMENTATION = Implementation(
            IssueDetector::class.java,
            Scope.JAVA_FILE_SCOPE
        )

        val ISSUE = Issue.create(
            id = "AndroidLogDetector",
            briefDescription = "The android Log should not be used",
            explanation = """
                For amazing showcasing purposes we should not use the Android Log. We should the
                AmazingLog instead.
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 9,
            severity = Severity.ERROR,
            androidSpecific = true,
            implementation = IMPLEMENTATION
        )
    }

}