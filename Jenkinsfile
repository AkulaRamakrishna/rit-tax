library 'shared-library@push-libraries'

pipeline_springBoot {
    jdk '11'
    repo 'rit-docker-virtual'
    jira_id '40205'
    reports 'https://qa1.rogers.com/TechHub_QE'
    in_sprint {
        plan 'DUMMY__ADD_LATER'
        credentials_id 'jira_serv_jiraxray'
    }
    agent {
        database 'cassandra', version: '3.11'
    }
    options {
        webhook = 'https://rcirogers.webhook.office.com/webhookb2/f318eabe-c702-4d0c-8378-2cf04677e3eb@0ab4cbbf-4bc7-4826-b52c-a14fed5286b9/JenkinsCI/8a577c7e52464a2b96db6ff3df7b95cc/b4413520-e153-4ab8-bf05-99df83db23ec'
    }
    stages {
        gradle_pacts { disable }
        gradle_validate_open_api { disable }
        gradle_insprint_units { disable }
        gradle_insprint_regressions { disable }
    }
}
