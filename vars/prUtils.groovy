import groovy.json.JsonSlurper

def findClosedPrs(prs) {
    def closedPRs = new JsonSlurper().parseText(prs).collect { pr ->
        return pr.state == 'closed'
    }
}
