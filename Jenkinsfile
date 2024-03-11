@Library('op-pipelines@basyx-v3') _

genericPipeline {
    buildScript = """
        source /var/lib/jenkins/.sdkman/bin/sdkman-init.sh > /dev/null 2>&1
        sdk use java 17.0.8-amzn > /dev/null 2>&1
        revision=\$(mvn help:evaluate -Dexpression=revision -q -DforceStdout)
        echo "revision=$revision"
        branch=\$(git rev-parse --abbrev-ref HEAD)
        echo "branch=$branch"
        if [[ $branch == feature/* ]]; then
            echo "The current branch is a feature branch."
            dash_count=\$(echo "$revision" | grep -o "-" | wc -l)
            if ! [ "$dash_count" -eq 2 ]; then
                exit 1
            fi
        fi
        mvn clean deploy \
            -DskipTests \
            -Ddocker.provenance=false \
            -Ddocker.namespace=registry.devinf.objective-partner.net/i40-forks/basyx-java-server-sdk
    """
}
