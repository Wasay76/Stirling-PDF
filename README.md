1. There are two folders in our repo. These are two separate projects.
2. One of them has the whole pdf application, the other is just the minimum amount of classes that we are working on. I deleted all the docker and unrelated files and redid the gradle.build file for this minimal folder.
3. FIRST: Test that you can open the folder with the whole application, open a terminal at the root directory of stirling-pdf-main and run it with ./gradlew clean build if it works then your java is probably set up right.
4. NEXT: Find a test file in the project with the whole pdf application and try running it. It should work. This confirms both the unit test works and that gradle works.

5. Now navigate the terminal to the directory of the minimal project. Run ./gradlew clean build in the minimal folder to compile everything and run every test.
6. Run ./gradlew pitest to run pitest (this will only work on the minimal project)
7. Check the build folder that is generated and populated for pitest and gradle reports.
