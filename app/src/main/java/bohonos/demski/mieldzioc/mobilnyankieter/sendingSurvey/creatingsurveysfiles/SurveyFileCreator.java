package bohonos.demski.mieldzioc.mobilnyankieter.sendingsurvey.creatingsurveysfiles;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import bohonos.demski.mieldzioc.mobilnyankieter.application.DateAndTimeService;
import bohonos.demski.mieldzioc.mobilnyankieter.jsonserialization.JsonSurveySerializator;
import bohonos.demski.mieldzioc.mobilnyankieter.survey.Survey;

/**
 * Created by Dominik on 2015-10-20.
 */
public class SurveyFileCreator {
    public void saveSurveyTemplate(Survey survey, Context context){
        if(isExternalStorageWritable()) {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                    getSurveyTemplateFileName(survey));

            file.getParentFile().mkdirs();

            try {
                PrintWriter fileOutputStream = new PrintWriter(file);

                JsonSurveySerializator jsonSurveySerializator = new JsonSurveySerializator();
                String jsonSurvey = jsonSurveySerializator.serializeSurvey(survey);

                fileOutputStream.println(jsonSurvey);

                fileOutputStream.close();
                Log.d("FILE_SURVEY_TEMP_PATH", file.getCanonicalPath());
            } catch (IOException e) {
                Toast.makeText(context, "Nie można zapisać pliku - możliwy brak miejsca w pamięci urządzenia", Toast.LENGTH_LONG);
                e.printStackTrace();
            }
        }
    }

    private String getSurveyTemplateFileName(Survey survey){
        String now = DateAndTimeService.getToday();

        return "mobilnyankieter" + File.separator + "szablonyAnkiet" + File.separator +
                survey.getIdOfSurveys() + now + ".json";
    }

    public void saveSurveyAnswers(List<Survey> surveys, Context context){
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                getSurveyAnswersFileName());

        file.getParentFile().mkdirs();

        try {
            PrintWriter fileOutputStream = new PrintWriter(file);

            JsonSurveySerializator jsonSurveySerializator = new JsonSurveySerializator();
            String jsonSurvey = jsonSurveySerializator.serializeListOfSurveys(surveys);

            fileOutputStream.println(jsonSurvey);

            fileOutputStream.close();
            Log.d("FILE_SURVEY_TEMP_PATH", file.getCanonicalPath());
        } catch (IOException e) {
            Toast.makeText(context, "Nie można zapisać pliku - możliwy brak miejsca w pamięci urządzenia", Toast.LENGTH_LONG);

            e.printStackTrace();
        }
    }

   private String getSurveyAnswersFileName(){
       String now = DateAndTimeService.getToday();

       return "mobilnyankieter" + File.separator + "wynikiAnkiet" + File.separator +
                 now + ".json";
   }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();

        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
