import hudson.model.Result
import org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper


/**
 * Email Extension plugin helper for sending automatic build status emails under
 * two circumstances: (1) when a build fails or is unstable (2) when a
 * previously-unstable/failed build now succeeds (_ie_ is fixed).
 *
 * Example usage within a declarative pipeline:
 *
 *    steps {
 *        sendEmail currentBuild, ['example@example.com']
 *    }
 *
 * Note that `currentBuild` is a Jenkins-provided object that is always available.
 *
 * @param currentBuild a Jenkins RunWrapper object for the current build
 * @param emailList a list of recipients' email addresses
 */
def call(RunWrapper currentBuild, List<String> emailList) {
    if (!emailList) {
        return
    }

    // Unfortunately emailext does not expose the same triggers functionality in
    // its declarative pipeline interface that it does in its Job DSL interface.
    // As a result we have to implement the trigger logic in a more ad hoc
    // manner. NB: we avoided Result#fromString (even though it would be nicer)
    // because Jenkins's sandboxed-Groovy mode does not allow the method by default.

    def currentResult = currentBuild.currentResult
    def previousResult = currentBuild.getPreviousBuild()?.getResult()

    def buildIsFixed =
        currentResult == Result.SUCCESS.toString() &&
        currentResult != previousResult &&
        previousResult != null

    def badResult =
        currentResult in [Result.UNSTABLE.toString(), Result.FAILURE.toString()]

    if (buildIsFixed || badResult) {
        emailext (
            recipientProviders: [[$class: "RequesterRecipientProvider"]],
            to: emailList.join(", "),
            subject: "\$DEFAULT_SUBJECT",
            body: "\$DEFAULT_CONTENT"
        )
    }
}
