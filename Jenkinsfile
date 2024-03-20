@Library('op-pipelines@basyx-v3') _

genericPipeline {
    buildScript = """
        source /var/lib/jenkins/.sdkman/bin/sdkman-init.sh > /dev/null 2>&1
        sdk use java 17.0.8-amzn > /dev/null 2>&1
        if [[ \$(git rev-parse --abbrev-ref HEAD) == feature/* ]]; then
            echo "The current branch is a feature branch."
            if [[ \$(mvn help:evaluate -Dexpression=revision -q -DforceStdout) == *"OPFORK"* ]]; then
                exit 1
            fi
        fi
        mvn clean deploy \
            -DskipTests \
            -Ddocker.provenance=false \
            -Ddocker.namespace=registry.devinf.objective-partner.net/i40-forks/basyx-java-server-sdk
    """
}
