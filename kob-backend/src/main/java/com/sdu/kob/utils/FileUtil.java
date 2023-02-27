package com.sdu.kob.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;

public class FileUtil {
    /**
     * @param file          要上传的文件
     * @param targetDirPath 存放文件的文件夹路径
     * @return 文件路径
     */
    public static String saveMultipartFile(MultipartFile file, String targetDirPath, String username) {

        File toFile;
        if (file == null || file.getSize() <= 0) {
            return null;
        } else {
            /*获取文件原名称*/
            String originalFilename = file.getOriginalFilename();
            /*获取文件格式*/
            String fileFormat = originalFilename.substring(originalFilename.lastIndexOf("."));

            deleteSamePrefixFile(targetDirPath + File.separator + username + fileFormat);
            toFile = new File(targetDirPath + File.separator + username + fileFormat);

            String absolutePath = null;
            try {
                absolutePath = toFile.getCanonicalPath();

                /*判断路径中的文件夹是否存在，如果不存在，先创建文件夹*/
                String dirPath = absolutePath.substring(0, absolutePath.lastIndexOf(File.separator));
                File dir = new File(dirPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                InputStream ins = file.getInputStream();

                inputStreamToFile(ins, toFile);
                ins.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return absolutePath;
        }
    }

    // 获取流文件
    private static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = Files.newOutputStream(file.toPath());
            int bytesRead;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void deleteSamePrefixFile(String deepPath) {
        String subPath = deepPath.substring(0, deepPath.lastIndexOf("/"));
        File file = new File(subPath);
        File[] files = file.listFiles();
        if (null == files) return;
        String name = deepPath.substring(deepPath.lastIndexOf("/") + 1).split("/.")[0];
        for (File f : files) {
            String fName = f.getName().substring(f.getName().lastIndexOf("/") + 1).split("/.")[0];
            if (fName.equals(name)) {
                f.delete();
                break;
            }
        }
    }
}
