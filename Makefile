SHELL := /bin/bash

path := ./

detekt:
	$(path)gradlew detektAll

buildApp:
	./gradlew :app:assemble

buildWear:
	./gradlew :wear:assemble

localCheck: detekt buildApp buildWear

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
