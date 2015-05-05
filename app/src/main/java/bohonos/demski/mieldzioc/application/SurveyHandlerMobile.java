package bohonos.demski.mieldzioc.application;

import android.content.Context;

import bohonos.demski.mieldzioc.dataBase.DataBaseAdapter;
import bohonos.demski.mieldzioc.survey.Survey;
import bohonos.demski.mieldzioc.survey.SurveyHandler;

/**
 * Created by Dominik on 2015-05-04.
 */
public class SurveyHandlerMobile extends SurveyHandler {

    private Context context;
    private DataBaseAdapter db;

    /**
     *
     * @param context
     * @param lastSurveysId ostatnio przyznane id grupy ankiet dla tego ankietera.
     */
    public SurveyHandlerMobile(Context context, int lastSurveysId) {
        super(lastSurveysId);
        this.context = context;
        db = new DataBaseAdapter(context);
    }

    public void setContext(Context context) {
        this.context = context;
        db = new DataBaseAdapter(context);
    }

    /**
     * Dodaje nowy szablon ankiety do klasy SurveyTemplate i do bazy danych.
     * @param survey ankieta do dodania.
     * @return id dodanego szablonu (id grupy ankiet), jeœli nie uda³o siê dodaæ szablonu do bazy
     * danych, zwraca -1.
     */
    @Override
    public String addNewSurveyTemplate(Survey survey) {
        String id =  super.addNewSurveyTemplate(survey);
        super.setSurveyStatus(survey, SurveyHandler.ACTIVE);
        if(survey == null) throw new NullPointerException("Przekazana ankieta nie mo¿e byæ nullem " +
                "- próba dodania ankiety do bazy danych");
        if(!db.addSurveyTemplate(survey, super.getSurveyStatus(survey.getIdOfSurveys()))) return null;
        return id;
    }
}
