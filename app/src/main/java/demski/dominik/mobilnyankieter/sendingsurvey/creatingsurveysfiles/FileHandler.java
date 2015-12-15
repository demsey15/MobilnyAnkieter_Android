package demski.dominik.mobilnyankieter.sendingsurvey.creatingsurveysfiles;

import android.os.Environment;
import android.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Created by Dominik on 2015-10-23.
 */
public class FileHandler {
    public void prepareLoadingDirectories(){
        if(isExternalStorageWritable()){
            File file = getLoadingDir();

            mkDirsIfThereHaveNotAlreadyExisted(file);
        }
    }

    public File getLoadingDir(){
        if(isExternalStorageWritable()){
            return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                    getLoadingDirectoryName());
        }
        else{
            return null;
        }
    }

    private String getLoadingDirectoryName(){
        return "mobilnyankieter" + File.separator + "wczytywanieSzablonowAnkiet";
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();

        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();

        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public boolean mkDirsIfThereHaveNotAlreadyExisted(File dirFile){
        return dirFile.mkdirs();
    }

    public void saveToFile(File file, String toSave) throws IOException{
        PrintWriter fileOutputStream = new PrintWriter(file);

        fileOutputStream.println(toSave);

        fileOutputStream.close();
    }

    public Pair<File[], String> getFilesFromDirectory(File directory){
        if(isExternalStorageReadable()){
            if(directory.isDirectory()){
                return new Pair<>(directory.listFiles(), "");
            }
            else{
                return new Pair<>(null, "Wystąpił błąd: brak folderu: " + directory.getAbsolutePath());
            }
        }
        else{
            return new Pair<>(null, "Nie można odczytać zewnętrznej pamięci lub jest ona niedostępna. Spróbuj ponownie później.");
        }
    }

    public String getFileContentAsString(File file){
        String fileContent = null;

        if(isExternalStorageReadable()){
            if(file.isFile()){
                try {
                    fileContent = "";

                    Scanner in = new Scanner(file);

                    while (in.hasNextLine()){
                        fileContent += in.nextLine();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        return fileContent;
    }

    public boolean deleteFile(File file){
        return file.delete();
    }
}
