SHELL := /bin/bash

path := ./

detekt:
	$(path)gradlew detektAll

autodetekt:
	$(path)gradlew detektAll --auto-correct --continue

buildApp:
	./gradlew :app:assemble

buildWear:
	./gradlew :wear:assemble


buildAndroid:
	./gradlew :cmp-app:assemble

testCommon:
	./gradlew :cmp-common:testDebugUnitTest

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

# Удаляет локальные ветки которые отсутствуют на remote
# from: https://stackoverflow.com/a/17029936/981330
unsafe_clear_branches:
	git fetch --prune && \
	git branch -r | awk '{print $$1}' | egrep -v -f /dev/fd/0 <(git branch -vv | grep origin) | \
	awk '{print $$1}' | xargs git branch -D

## Run on Desktop jvm
runDesktop:
	./gradlew run
