import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import com.ClassifierService.OpenNLPApproach.FileUtil;
import com.ClassifierService.OpenNLPApproach.OpenNLPDocumentCategorizer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenNLPTestClass {
    private static Logger log = LoggerFactory.getLogger(OpenNLPTestClass.class);
    static String modelFile = null;
    static String dataDirectory = null;
    static String testDataDir = null;

    public static void main(String args[]) throws IOException {

        modelFile = new File("src/main/resources/models/open_nlp_model.bin").getAbsolutePath();

        OpenNLPDocumentCategorizer detector = new OpenNLPDocumentCategorizer();

////      try training and testing on twitter data to classify good and bad reviews..
//        String inputFile = new File("src/test/resources/data/tweets.txt").getAbsolutePath();
//        detector.trainModelfromMarkedFile(inputFile, modelFile);
//        ArrayList<String> testData = new ArrayList<String>();
//        String testFile = new File("src/test/resources/data/testdata.txt").getAbsolutePath();
//        try (BufferedReader br = new BufferedReader(new FileReader(testFile))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                testData.add(line);
//            }
//        }
//        System.out.println("-------------------");
//        for (String line : testData){
//            System.out.println(detector.getCategory(line) + "\t" + line);
//        }

        dataDirectory = new File("src/main/resources/data/labeled").getAbsolutePath();
        detector.trainModelfromDocuments(dataDirectory, modelFile);

        detector.initModel(modelFile);

        testDataDir = new File("src/main/resources/data/unlabeled").getAbsolutePath();
        Collection<String> fileNames = FileUtil.getDirectories(testDataDir);
        HashMap<String, String> testDataMap = new HashMap<String, String>();
        for (String discipline : fileNames) {
            Collection<String> soureLines = FileUtil.readFilesAsList(testDataDir + "/" + discipline);
            testDataMap.put(discipline, StringUtils.join(" ", soureLines));
        }
        for (String key : testDataMap.keySet()) {
            log.info(key + "\t ==> " );
            HashMap<String,Double> results = detector.getWeights(testDataMap.get(key));
            results.forEach((k, v) -> log.info("\t" + k + ": " + v));
        }


    }
}
