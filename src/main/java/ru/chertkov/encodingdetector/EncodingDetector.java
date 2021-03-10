package ru.chertkov.encodingdetector;

import org.apache.log4j.Logger;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static ru.chertkov.encodingdetector.util.PropertiesUtils.readProperties;

public class EncodingDetector {

    private static final Logger logger = Logger.getLogger(EncodingDetector.class);

    private static final char tik = '\\';
    private static final char tok = '/';
    private static int ticTocCounter = 0;

    public static void main(String[] args) throws java.io.IOException {
        logger.info("start program");

        long timeStart = System.currentTimeMillis();

        readProperties();

        File[] files = new File(System.getProperty("inputDirectory")).listFiles(pathname -> !pathname.isDirectory());
        if(files != null && files.length > 0){
            logger.info("start handle files");
            int filesCount = files.length;
            double step = 100.0/filesCount;
            double counter = 0.0;
            for(File file: files){
                detect(file.getPath());

                if(ticTocCounter == 0){
                    ticTocCounter +=1;
                }else{
                    ticTocCounter -=1;
                }
                counter += step;
                System.out.print(String.format("%.2f", counter) + " % " + (ticTocCounter==0?tik:tok) + '\r');
            }
            logger.info("finish handle files");
        }else{
            logger.warn("directory is empty");
        }
        logger.info("finish program");
        logger.info("time -> " + ((System.currentTimeMillis() - timeStart)/1000) + " sec.");
    }

    private static void detect(String filename) throws IOException {
        byte[] buf = new byte[16384];
        FileInputStream fis = new FileInputStream(filename);
        UniversalDetector detector = new UniversalDetector(null);
        int nread;
        while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nread);
        }
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        if (encoding != null) {
            logger.info("encoding-> " + encoding + ", file-> " + filename);
        } else {
            logger.warn("No encoding detected, file-> " + filename);
        }
        detector.reset();
    }
}
