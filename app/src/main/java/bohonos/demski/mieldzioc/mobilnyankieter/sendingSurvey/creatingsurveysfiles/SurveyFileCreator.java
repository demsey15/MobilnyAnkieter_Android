package bohonos.demski.mieldzioc.mobilnyankieter.sendingsurvey.creatingsurveysfiles;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

import bohonos.demski.mieldzioc.mobilnyankieter.application.DateAndTimeService;
import bohonos.demski.mieldzioc.mobilnyankieter.jsonserialization.JsonSurveySerializator;
import bohonos.demski.mieldzioc.mobilnyankieter.survey.Survey;

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
                 now + survey.getTitle() + ".json";
    }

    public void saveSurveyAnswers(List<Survey> surveys, Context context){
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                getSurveyAnswersFileName());

        file.getParentFile().mkdirs();

        try {
            JsonSurveySerializator jsonSurveySerializator = new JsonSurveySerializator();
            String jsonSurvey = jsonSurveySerializator.serializeListOfSurveys(surveys);

            fileHandler.saveToFile(file, jsonSurvey);

            Log.d("FILE_SURVEY_TEMP_PATH", file.getCanonicalPath());
        } catch (IOException e) {
            Toast.makeText(context, "Nie można zapisać pliku - możliwy brak miejsca w pamięci urządzenia", Toast.LENGTH_LONG);

            e.printStackTrace();
        }
    }

   private String getSurveyAnswersFileName(){
       String now = DateAndTimeService.getToday();

       return "mobilnyankieter" + File.separator + "wynikiAnkiet" + File.separator +
                 now + "_answers.json";
   }
}
