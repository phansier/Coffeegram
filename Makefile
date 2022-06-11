path := ./

detekt:
	$(path)gradlew detektAll

buildApp:
	./gradlew :app:assemble --no-configuration-cache

buildWear:
	./gradlew :wear:assemble --no-configuration-cache

localCheck: detekt buildApp buildWear

compose_metrics:
	$(path)gradlew :app:assembleRelease \-Pmyapp.enableComposeCompilerReports=true --no-configuration-cache

# https://github.com/PatilShreyas/compose-report-to-html
compose_report:
	java -jar composeReport2Html.jar \
      -app Coffeegram \
      -overallStatsReport app/build/compose_metrics/app_release-module.json \
      -detailedStatsMetrics app/build/compose_metrics/app_release-composables.csv \
      -composableMetrics app/build/compose_metrics/app_release-composables.txt \
      -classMetrics app/build/compose_metrics/app_release-classes.txt \
      -o app/build/compose_report/
