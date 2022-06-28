package com.fico.cbs.utils;

import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileHandleUtil {
    public FileHandleUtil() {
    }

    public static String loadTxt(String fileName) throws IllegalArgumentException, IOException {
        if (org.apache.commons.lang.StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException("Operate File's Name Argument Exception.");
        } else {
            String txtFileName = ResourceUtils.getResourceAsPath(fileName);
            File operFile = new File(txtFileName);
            if (!operFile.exists()) {
                throw new IOException("Operate File not exist.");
            } else if (operFile.isDirectory()) {
                throw new IOException("Operate File is folder.");
            } else {
                InputStreamReader isr = new InputStreamReader(new FileInputStream(operFile), "UTF-8");
                BufferedReader reader = new BufferedReader(isr);
                String line = "";
                StringBuffer fileResult = new StringBuffer();

                while((line = reader.readLine()) != null) {
                    if (!org.apache.commons.lang.StringUtils.isBlank(line)) {
                        fileResult.append(line.trim());
                    }
                }

                isr.close();
                reader.close();
                return fileResult.toString();
            }
        }
    }

    public static String loadXml(String fileName) throws IllegalArgumentException, IOException {
        if (org.apache.commons.lang.StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException("Operate File's Name Argument Exception.");
        } else {
            BufferedReader reader = null;
            InputStreamReader isr = null;
            File operFile = new File(fileName);
            if (!operFile.exists()) {
                throw new IOException("Operate File not exist.");
            } else if (operFile.isDirectory()) {
                throw new IOException("Operate File is folder.");
            } else {
                isr = new InputStreamReader(new FileInputStream(operFile), "UTF-8");
                reader = new BufferedReader(isr);
                String line = "";
                StringBuffer fileResult = new StringBuffer();

                while((line = reader.readLine()) != null) {
                    if (!org.apache.commons.lang.StringUtils.isBlank(line)) {
                        fileResult.append(line.trim());
                    }
                }

                isr.close();
                reader.close();
                return fileResult.toString();
            }
        }
    }

    public static boolean saveTxt(String fileName, String fileContent) throws IllegalArgumentException, IOException {
        if (org.apache.commons.lang.StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException("Operate File's Name Argument Exception.");
        } else {
            File operFile = new File(fileName);
            if (operFile.isDirectory()) {
                throw new IOException("Operate File is folder.");
            } else {
                File checkFolder = new File(operFile.getParent());
                if (!checkFolder.exists() || checkFolder.isFile()) {
                    checkFolder.mkdirs();
                }

                operFile.deleteOnExit();
                OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(operFile), "UTF-8");
                os.write(JsonFormatUtil.formatJson(fileContent));
                os.flush();
                os.close();
                return true;
            }
        }
    }

    public static void write(String fileName, String content, String encoding) throws IOException {
        if (org.apache.commons.lang.StringUtils.isBlank(fileName)) {
            throw new IOException("write fileName is Null Or Empty.");
        } else {
            File file = new File(fileName);
            if (file.exists()) {
                file.delete();
            } else {
                File checkFolder = new File(file.getParent());
                if (!checkFolder.exists() || checkFolder.isFile()) {
                    checkFolder.mkdirs();
                }
            }

            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos, encoding);
            BufferedWriter writer = new BufferedWriter(osw);
            if (StringUtils.isBlank(content)) {
                writer.write("");
            } else {
                writer.write(content);
            }

            writer.close();
        }
    }

    public static String handleFileName(String fileName, Map<String, String> nameParams) throws Exception {
        if (fileName != null && fileName.trim().length() != 0) {
            if (nameParams != null && nameParams.size() != 0) {
                String tmpFileName = fileName;

                String param;
                Matcher inputFileMatcher;
                for(Iterator var4 = nameParams.keySet().iterator(); var4.hasNext(); tmpFileName = inputFileMatcher.replaceAll((String)nameParams.get(param))) {
                    param = (String)var4.next();
                    String regex = "#%s#";
                    regex = String.format(regex, param);
                    Pattern pattern = Pattern.compile(regex);
                    inputFileMatcher = pattern.matcher(tmpFileName);
                }

                return tmpFileName;
            } else {
                return fileName;
            }
        } else {
            return "";
        }
    }

    public static String handlePathName(String pathName, Map<String, String> nameParams) throws Exception {
        if (pathName != null && pathName.trim().length() != 0) {
            if (nameParams != null && nameParams.size() != 0) {
                String tmpFileName = pathName;

                String param;
                Matcher inputFileMatcher;
                for(Iterator var4 = nameParams.keySet().iterator(); var4.hasNext(); tmpFileName = inputFileMatcher.replaceAll((String)nameParams.get(param))) {
                    param = (String)var4.next();
                    String regex = "#%s#";
                    regex = String.format(regex, param);
                    Pattern pattern = Pattern.compile(regex);
                    inputFileMatcher = pattern.matcher(tmpFileName);
                }

                return tmpFileName;
            } else {
                return pathName;
            }
        } else {
            return "";
        }
    }
}
