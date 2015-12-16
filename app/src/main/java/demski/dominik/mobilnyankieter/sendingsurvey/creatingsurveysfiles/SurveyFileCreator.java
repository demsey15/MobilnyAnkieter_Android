package demski.dominik.mobilnyankieter.sendingsurvey.creatingsurveysfiles;

import android.os.Environment;
import android.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.List;

import bohonos.demski.mieldzioc.mobilnyankieter.serialization.jsonserialization.JsonSurveySerializator;
import bohonos.demski.mieldzioc.mobilnyankieter.survey.Survey;
import demski.dominik.mobilnyankieter.serialization.csv.CsvMaker;
import demski.dominik.mobilnyankieter.utilities.DateAndTimeService;

/**
 * Created by Dominik on 2015-10-20.
 */
public class SurveyFileCreator {
    private FileHandler fileHandler = new FileHandler();

    public Pair<Boolean, String> saveSurveyTemplate(Survey survey){
        if(fileHandler.isExternalStorageWritable()) {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                    getSurveyTemplateFileName(survey));

            file.getParentFile().mkdirs();

            try {
                JsonSurveySerializator jsonSurveySerializator = new JsonSurveySerializator();
                String jsonSurvey = jsonSurveySerializator.serializeSurvey(survey);

                fileHandler.saveToFile(file, jsonSurvey);

                String surveySavedTxt = "Szablon ankiet został zapisany (" + file.getAbsolutePath() + ")";

                return new Pair<>(true, surveySavedTxt);
            } catch (IOException e) {
                String errorTooLowMemoryTxt = "Nie można zapisać pliku - możliwy brak miejsca w pamięci urządzenia";

                e.printStackTrace();

                return new Pair<>(false, errorTooLowMemoryTxt);
            }
        }
        else{
            String errorLackOfMemoryTxt = "Brak załączonej pamięci zewnętrznej. Załącz pamięć i spróbuj ponownie.";

            return new Pair<>(false, errorLackOfMemoryTxt);
        }
    }

    private String getSurveyTemplateFileName(Survey survey){
        String now = DateAndTimeService.getToday();

        return "mobilnyankieter" + File.separator + "szablonyAnkiet" + File.separator +
                 now + " " + survey.getTitle() + ".json";
    }

    private Pair<Boolean, String> saveSurveyAnswersInFile(String stringToSave, String filePath){
        if(fileHandler.isExternalStorageWritable()) {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                        filePath);

                file.getParentFile().mkdirs();

                try {
                    fileHandler.saveToFile(file, stringToSave);

                    String surveySavedTxt = "Wyniki zostały zapisane (" + file.getAbsolutePath() + ")";

                    return new Pair<>(true, surveySavedTxt);
                } catch (IOException e) {
                    String errorTooLowMemoryTxt = "Nie można zapisać pliku - możliwy brak miejsca w pamięci urządzenia";

                    e.printStackTrace();

                    return new Pair<>(false, errorTooLowMemoryTxt);
                }
        }
        else{
            String errorLackOfMemoryTxt = "Brak załączonej pamięci zewnętrznej. Załącz pamięć i spróbuj ponownie.";

            return new Pair<>(false, errorLackOfMemoryTxt);
        }
    }

    public Pair<Boolean, String> saveSurveyAnswersInJson(List<Survey> surveys, String sendStatus){
        if (surveys != null && !surveys.isEmpty()) {
            String filePath = getSurveyAnswersFileNameJson(surveys.get(0), sendStatus);

            JsonSurveySerializator jsonSurveySerializator = new JsonSurveySerializator();
            String jsonSurvey = jsonSurveySerializator.serializeListOfSurveys(surveys);

            return saveSurveyAnswersInFile(jsonSurvey, filePath);
        }
        else{
            String emptySurveyListMessage = "Brak zapisanych odpowiedzi dla wybranej ankiety.";

            return new Pair<>(false, emptySurveyListMessage);
            }
        }

    public Pair<Boolean, String> saveSurveyAnswersInCsv(List<Survey> surveys, String sendStatus){
        if (surveys != null && !surveys.isEmpty()) {
            String filePath = getSurveyAnswersFileNameCsv(surveys.get(0), sendStatus);

            CsvMaker csvMaker = new CsvMaker(";");
            String csvSurvey = csvMaker.serializeListOfSurveys(surveys);

            return saveSurveyAnswersInFile(csvSurvey, filePath);
        }
        else{
            String emptySurveyListMessage = "Brak zapisanych odpowiedzi dla wybranej ankiety.";

            return new Pair<>(false, emptySurveyListMessage);
        }
    }

   private String getSurveyAnswersFileNameJson(Survey survey, String sendStatus){
       String now = DateAndTimeService.getToday();

       return "mobilnyankieter" + File.separator + "wynikiAnkiet" + File.separator +
                 "json" + File.separator + survey.getTitle() + "_" + sendStatus + "_" + now + "_answers.json";
   }

    private String getSurveyAnswersFileNameCsv(Survey survey, String sendStatus){
        String now = DateAndTimeService.getToday();

        return "mobilnyankieter" + File.separator + "wynikiAnkiet" + File.separator +
                "csv" + File.separator + survey.getTitle() + "_" + sendStatus + "_" + now + "_answers.csv";
    }
}
