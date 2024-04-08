package cn.frkovo.rhymaker;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;


import java.io.*;
import java.nio.charset.StandardCharsets;

public class ZipCompress {
    private final String zipFileName;
    private final String sourceFileName;

    public ZipCompress(String zipFileName, String sourceFileName) {
        this.zipFileName = zipFileName;
        this.sourceFileName = sourceFileName;
    }

    public void zip() throws Exception {
        // 创建zip输出流
        ZipArchiveOutputStream out = new ZipArchiveOutputStream(new FileOutputStream(zipFileName));

        File sourceFile = new File(sourceFileName);

        compress(out, sourceFile, "");

        out.close();
    }

    public void compress(ZipArchiveOutputStream out, File sourceFile, String base) throws Exception {
        if (sourceFile.isDirectory()) {
            File[] flist = sourceFile.listFiles();

            for (int i = 0; i < flist.length; i++) {
                compress(out, flist[i], base + "/" + flist[i].getName());
            }
        } else {
            out.putArchiveEntry(new ZipArchiveEntry(base));

            // 判断文件是否为文本文件
            if (sourceFile.getName().endsWith(".mcmeta")) {
                // 使用字符流处理文本文件
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile), StandardCharsets.UTF_8));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));

                int tag;
                while ((tag = reader.read()) != -1) {
                    writer.write(tag);
                }

                writer.flush();
                reader.close();
            } else {
                // 使用字节流处理非文本文件
                FileInputStream fis = new FileInputStream(sourceFile);
                IOUtils.copy(fis, out);
                fis.close();
            }

            out.closeArchiveEntry();
        }
    }
}