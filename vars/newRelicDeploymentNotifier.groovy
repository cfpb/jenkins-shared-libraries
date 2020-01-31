import groovy.json.JsonOutput

/**
 * Records a deployment in New Relic
 
 * Description, Changelog, User, and Revision parameters are recorded with New Relic and show up in their respective user interface sections in New Relic. 
 * Use the New Relic user interface as a guide for what kind of information you want to pass as these parameters
 * 
 * This library function will become unnecessary when the New Relic Deployment Notifier Plugin supports pipeline syntax (https://issues.jenkins-ci.org/browse/JENKINS-57706)
 *
 * @param newRelicApiKey New Relic API Key 
 * @param newRelicApplicationId APM Application ID under which the deployment will be recorded
 * @param description Description of this deployment
 * @param changelog List of changes included in this deployment
 * @param user The name of the user or process that triggered the deployment
 * @param revision SCM revision, such as git SHA 

 */
def call(newRelicApiKey, newRelicApplicationId, description, changelog, user, revision) {

    def APMPayload = [
        "deployment": [
            "description": description,
            "changelog": changelog,
            "user": user,
            "revision": revision
        ]
    ]

    httpRequest(
        consoleLogResponseBody: true,
        httpMode: 'POST',
        contentType: 'APPLICATION_JSON',
        customHeaders: [
            [
                maskValue: true,
                name: 'X-Api-Key',
                value: newRelicApiKey
            ]
        ],
        requestBody: JsonOutput.toJson(APMPayload),
        responseHandle: 'NONE',
        url: (
            "https://api.newrelic.com/v2/applications/${newRelicApplicationId}/deployments.json"
        )
    )
}
