SHELL := /bin/bash

path := ./

params := --console=plain

detekt:
	$(path)gradlew detektAll $(params)

autodetekt:
	$(path)gradlew detektAll --auto-correct --continue

buildApp:
	./gradlew :app:assembleDebug $(params)

buildWear:
	./gradlew :wear:assemble $(params)


buildAndroid:
	./gradlew :cmp-app:assemble $(params)

buildDesktop:
	./gradlew :cmp-app:assemble $(params)

testCommon:
	./gradlew :cmp-common:testDebugUnitTest $(params)

localCheck: detekt buildApp buildWear buildAndroid testCommon

compose_metrics:
	$(path)gradlew :app:assembleRelease \-Pmyapp.enableComposeCompilerReports=true

# https://github.com/PatilShreyas/compose-report-to-html
compose_report:
	java -jar composeReport2Html.jar \
      -app Coffeegram \
      -overallStatsReport app/build/compose_metrics/app_release-module.json \
      -detailedStatsMetrics app/build/compose_metrics/app_release-composables.csv \
      -composableMetrics app/build/compose_metrics/app_release-composables.txt \
      -classMetrics app/build/compose_metrics/app_release-classes.txt \
      -o app/build/compose_report/

# Removes local branches absent in remote
# from: https://stackoverflow.com/a/17029936/981330
unsafe_clear_branches:
	git fetch --prune && \
	git branch -r | awk '{print $$1}' | egrep -v -f /dev/fd/0 <(git branch -vv | grep origin) | \
	awk '{print $$1}' | xargs git branch -D

## Run on Desktop jvm
runDesktop:
	./gradlew run $(params)


runWasm:
	./gradlew :cmp-common:wasmJsBrowserDevelopmentRun --no-configuration-cache $(params)

buildWasm:
	./gradlew :cmp-common:wasmJsBrowserDistribution --no-configuration-cache $(params)

generateFramework:
	./gradlew :cmp-common:generateDummyFramework $(params)

buildHealth:
	./gradlew buildHealth $(params)
