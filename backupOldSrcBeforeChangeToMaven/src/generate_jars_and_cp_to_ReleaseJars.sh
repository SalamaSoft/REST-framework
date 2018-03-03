ant -buildfile ./JSON/build.xml
\cp -f ./ReleaseJars/JSON.jar ./SalamaCloudDataService/lib/
\cp -f ./ReleaseJars/JSON.jar ./SalamaCloudDataServiceUtil/lib/

ant -buildfile ./SalamaUtil/build.xml
\cp -f ./ReleaseJars/SalamaUtil.jar ./SalamaCloudDataService/lib/
\cp -f ./ReleaseJars/SalamaUtil.jar ./SalamaHttpUtil/lib/
\cp -f ./ReleaseJars/SalamaUtil.jar ./SalamaInvokeService/lib/
\cp -f ./ReleaseJars/SalamaUtil.jar ./SalamaModelDrivenGenerator/lib/
\cp -f ./ReleaseJars/SalamaUtil.jar ./SalamaReflect/lib/
\cp -f ./ReleaseJars/SalamaUtil.jar ./SalamaServiceAuth/lib/
\cp -f ./ReleaseJars/SalamaUtil.jar ./SalamaServiceCore/lib/
\cp -f ./ReleaseJars/SalamaUtil.jar ./SalamaUtilTest/lib/


ant -buildfile ./SalamaServiceCore/build.xml
\cp -f ./ReleaseJars/SalamaServiceCore.jar ./SalamaCloudDataService/lib/
\cp -f ./ReleaseJars/SalamaServiceCore.jar ./SalamaCloudDataServiceCore/lib/
\cp -f ./ReleaseJars/SalamaServiceCore.jar ./SalamaHttpUtil/lib/
\cp -f ./ReleaseJars/SalamaServiceCore.jar ./SalamaInvokeService/lib/
\cp -f ./ReleaseJars/SalamaServiceCore.jar ./SalamaServerServlet/lib/
\cp -f ./ReleaseJars/SalamaServiceCore.jar ./SalamaServiceAuth/lib/
\cp -f ./ReleaseJars/SalamaServiceCore.jar ./SalamaServiceUtil/lib/
\cp -f ./ReleaseJars/SalamaServiceCore.jar ./SalamaUtilTest/lib/


ant -buildfile ./SalamaReflect/build.xml
\cp -f ./ReleaseJars/SalamaReflect.jar ./SalamaCloudDataService/lib/
\cp -f ./ReleaseJars/SalamaReflect.jar ./SalamaInvokeService/lib/
\cp -f ./ReleaseJars/SalamaReflect.jar ./SalamaServerServlet/lib/
\cp -f ./ReleaseJars/SalamaReflect.jar ./SalamaUtilTest/lib/

ant -buildfile ./SalamaHttpUtil/build.xml
\cp -f ./ReleaseJars/SalamaHttpUtil.jar ./SalamaInvokeService/lib/
\cp -f ./ReleaseJars/SalamaHttpUtil.jar ./SalamaServerServlet/lib/
\cp -f ./ReleaseJars/SalamaHttpUtil.jar ./SalamaUtilTest/lib/

ant -buildfile ./SalamaInvokeService/build.xml
\cp -f ./ReleaseJars/SalamaInvokeService.jar ./SalamaServerServlet/lib/
\cp -f ./ReleaseJars/SalamaInvokeService.jar ./SalamaUtilTest/lib/

ant -buildfile ./SalamaServiceAuth/build.xml

ant -buildfile ./SalamaCloudDataServiceCore/build.xml
\cp -f ./ReleaseJars/SalamaCloudDataServiceCore.jar ./SalamaCloudDataService/lib/
\cp -f ./ReleaseJars/SalamaCloudDataServiceCore.jar ./SalamaServerServlet/lib/

ant -buildfile ./SalamaCloudDataServiceUtil/build.xml
\cp -f ./ReleaseJars/SalamaCloudDataServiceUtil.jar ./SalamaCloudDataService/lib/
\cp -f ./ReleaseJars/SalamaCloudDataServiceUtil.jar ./SalamaServerServlet/lib/

ant -buildfile ./SalamaCloudDataService/build.xml
\cp -f ./ReleaseJars/SalamaCloudDataService.jar ./SalamaServerServlet/lib/

ant -buildfile ./SalamaServerServlet/build.xml

ant -buildfile ./SalamaModelDrivenGenerator/build.xml







