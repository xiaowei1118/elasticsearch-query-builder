package org.coryphaei.query;

import java.io.*;

/**
 * Created by twist on 2016/5/12.
 */
public class FileUtil {

    /**
     * 读文件
     *
     * @param fileName
     * @return
     */
    public static String readFile(String fileName) {
        return readFile(new File(fileName));
    }

    public static String readFile(File file) {
        StringBuffer message = new StringBuffer();
        try {
            String encoding = "utf-8";
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String textLine;
                while ((textLine = bufferedReader.readLine()) != null) {
                    message.append(textLine);
                }

                read.close();
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }

        return message.toString();
    }

    /**
     * 读取classpath目录下的资源文件
     *
     * @param fileName
     * @return
     */
//    public static String readResourcePackageFile(String fileName) {
//        return readFile(getClassPathFile(fileName));
//    }

    /**
     * 写classpath目录下的资源文件
     *
     * @param fileName
     * @return
     */
    public static boolean writeResourcePackageFile(String fileName, String data, Boolean ifAppend) {
        return writeFile(getClassPathFile(fileName), data, ifAppend);
    }

    /**
     * 写文件
     *
     * @param fileName 文件名称
     * @param data     数据
     * @param ifAppend 是否追加形式
     * @return boolean
     */
    public static boolean writeFile(String fileName, String data, Boolean ifAppend) {
        try {
            FileWriter writer = new FileWriter(fileName, ifAppend);
            writer.write(data);
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除文件
     *
     * @param fileName
     */
    public static void deleteFile(String fileName) {
        File file = new File(fileName);

        try {
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public static String writeFile(String filePath, String fileName, byte[] content) {
        String absolutePath = null;
        try {
            writeToFile(filePath, fileName, content);
            absolutePath = filePath + File.separator + fileName;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return absolutePath;
    }

    public static File writeToFile(String filePath, String fileName, byte[] content) throws IOException {
        File director = new File(filePath + File.separator);
        if (!director.exists()) {
            if (!director.mkdirs()) {
                throw new RuntimeException("创建目录失败：" + filePath);
            }
        }

        String absolutePath = filePath + File.separator + fileName;
        File file = new File(absolutePath);
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new RuntimeException("创建文件失败：" + fileName);
            }
        }

        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(
                    new FileOutputStream(file));
            out.write(content);
        } catch (IOException e) {
            file.delete();
            throw new RuntimeException("写文件内容失败：" + fileName);
        } finally {
            out.close();

            return file;
        }
    }

    public static File getResourcePackageFile(String fileName) {
        return new File(getClassPathFile(fileName));
    }

    public static String getClassPathFile(String fileName) {
        System.out.println(fileName);
        System.out.println(FileUtil.class.getResource("/").getPath());
        return FileUtil.class.getResource("/").getPath() + fileName;
    }

//    public static String readResourceRemoveComments(String fileName) {
//        String fileContents = readResourcePackageFile(fileName);
//
//        return fileContents.replaceAll("/\\*.*?\\*/", "").replaceAll("//.*?[\\n| ]", "\n");
//    }

    public static File[] listFile(String dir) {
        File[] paths = new File[]{};

        try {
            File file = new File(dir);
            paths = file.listFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return paths;
    }

    public static File[] listResourcePackageFile(String dir) {
        return listFile(getClassPathFile(dir));
    }

    public static String getResource(Class clazz, String fileName) {
        //返回读取指定资源的输入流
        InputStream is = clazz.getClassLoader().getResourceAsStream(fileName);

        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String result = "";
        String line;
        try {
            while (((line = br.readLine())) != null)
                result += line;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    public static String readResourceRemoveComments(Class clazz, String fileName) {
        String fileContents = getResource(clazz, fileName);

        return fileContents.replaceAll("/\\*.*?\\*/", "").replaceAll("//.*?[\\n| ]", "\n");
    }
}
