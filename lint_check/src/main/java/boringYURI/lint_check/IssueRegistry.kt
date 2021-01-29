package boringYURI.lint_check

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.*
import com.android.tools.lint.detector.api.Category.Companion.CORRECTNESS
import java.util.*

@Suppress("UnstableApiUsage")
class IssueRegistry : IssueRegistry() {

    override val issues: List<Issue>
        get() = listOf(IssueDetector.ISSUE)

    override val api = CURRENT_API

}