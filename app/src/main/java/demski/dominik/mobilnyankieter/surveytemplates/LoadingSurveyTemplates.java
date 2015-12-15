package demski.dominik.mobilnyankieter.surveytemplates;

import android.content.Context;

import java.io.File;

import demski.dominik.mobilnyankieter.application.ApplicationState;
import demski.dominik.mobilnyankieter.database.DataBaseAdapter;
import demski.dominik.mobilnyankieter.sendingsurvey.creatingsurveysfiles.FileHandler;
import bohonos.demski.mieldzioc.mobilnyankieter.serialization.jsonserialization.JsonSurveySerializator;
import bohonos.demski.mieldzioc.mobilnyankieter.survey.Survey;
import bohonos.demski.mieldzioc.mobilnyankieter.survey.SurveyHandler;

/**
 * Created by Dominik on 2015-10-23.
 */
public class LoadingSurveyTemplates {
    public static final int CANNOT_READ_FILE = 0;
    public static final int WRONG_FILE_FORMAT = 1;
    public static final int SURVEY_ANSWER_INSTEAD_OF_SURVEY_TEMPLATE = 2;
    public static final int SURVEY_TEMPLATE_ALREADY_EXISTS = 3;
    public static final int ERROR_DURING_ADDING_TO_DB = 4;
    public static final int SURVEY_ADDED = 5;

    FileHandler fileHandler = new FileHandler();
    public int loadSurveyTemplate(File file, Context context){
        String fileContent = fileHandler.getFileContentAsString(file);

        if(fileContent == null){
            return CANNOT_READ_FILE;
        }
        else{
            JsonSurveySerializator jsonSurveySerializator = new JsonSurveySerializator();

            Survey survey = jsonSurveySerializator.deserializeSurvey(fileContent);

            if(survey == null){
                return WRONG_FILE_FORMAT;
            } else{
                if(survey.getNumberOfSurvey() != 0){
                    return SURVEY_ANSWER_INSTEAD_OF_SURVEY_TEMPLATE;
                } else {
                    DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
                    dataBaseAdapter.open();

                    if(dataBaseAdapter.ifSurveyTemplateInDB(survey.getIdOfSurveys())){
                        dataBaseAdapter.close();

                        return SURVEY_TEMPLATE_ALREADY_EXISTS;
                    }
                    else{

                        if(dataBaseAdapter.addSurveyTemplate(survey, SurveyHandler.ACTIVE, false)){
                            fileHandler.deleteFile(file);

                            SurveyHandler surveyHandler = ApplicationState.getInstance(context).getSurveyHandler();
                            surveyHandler.loadSurveyTemplate(survey, SurveyHandler.ACTIVE);

                            return SURVEY_ADDED;
                        }
                        else{
                            return ERROR_DURING_ADDING_TO_DB;
                        }
                    }
                }
            }
        }
    }
}
