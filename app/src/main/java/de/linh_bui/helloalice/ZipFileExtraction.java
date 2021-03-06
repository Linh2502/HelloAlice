package de.linh_bui.helloalice;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
/**
 * Created by Linh on 23.07.15.
 *
 * Extract ZIP file containing all AIML files
 */
public class ZipFileExtraction
{
    public void unZipIt(InputStream zipFile, String outputFolder)
    {
        try
        {
            ZipInputStream zin = new ZipInputStream(zipFile);
            ZipEntry entry;
            int bytesRead;
            byte[] buffer = new byte[4096];

            while ((entry = zin.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    File dir = new File(outputFolder, entry.getName());
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    Log.d("+++++++++++Zip Extractor" , "[DIR] " + entry.getName());
                } else {
                    FileOutputStream fos = new FileOutputStream(outputFolder + entry.getName());
                    while ((bytesRead = zin.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                    fos.close();
                    Log.d("+++++++++++Zip Extractor" , "[FILE] " + entry.getName());
                }
            }
            zin.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}