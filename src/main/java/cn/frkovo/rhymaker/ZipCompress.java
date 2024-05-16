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
            for (File nestedFile : sourceFile.listFiles()) {
                compress(out, nestedFile, base + "/" + nestedFile.getName());
            }
        } else {
            String entryName = sourceFile.getPath().replace("\\", "/").substring(sourceFileName.length());
            if (entryName.startsWith("/")) {
                entryName = entryName.substring(1);
            }
            ZipArchiveEntry entry = new ZipArchiveEntry(entryName);
            out.putArchiveEntry(entry);

            try (FileInputStream fis = new FileInputStream(sourceFile)) {
                IOUtils.copy(fis, out);
            }

            out.closeArchiveEntry();
        }
    }
}