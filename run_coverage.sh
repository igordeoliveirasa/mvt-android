./gradlew clean assemble;
./gradlew jacocoTestReport;
./gradlew jacocoRootReport coveralls;
open ./app/build/reports/jacoco/jacocoTestReport/html/index.html
