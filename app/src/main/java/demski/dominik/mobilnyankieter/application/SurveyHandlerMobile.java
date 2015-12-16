package demski.dominik.mobilnyankieter.application;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bohonos.demski.mieldzioc.mobilnyankieter.serialization.jsonserialization.JsonSurveySerializator;
import bohonos.demski.mieldzioc.mobilnyankieter.survey.Survey;
import bohonos.demski.mieldzioc.mobilnyankieter.survey.SurveyHandler;
import demski.dominik.mobilnyankieter.database.DataBaseAdapter;

/**
 * Created by Dominik on 2015-05-04.
 */
public class SurveyHandlerMobile extends SurveyHandler {
    private Context context;
    private DataBaseAdapter db;

    /**
     *
     * @param context context aplikacji
     * @param lastSurveysId ostatnio przyznane id grupy ankiet dla tego ankietera.
     */
    public SurveyHandlerMobile(Context context, int lastSurveysId) {
            super(lastSurveysId);

            this.context = context;
            db = new DataBaseAdapter(context);

            Map<Survey, Integer> surveys = db.getAllSurveyTemplates();

            for(Survey survey : surveys.keySet()){
                super.loadSurveyTemplate(survey, surveys.get(survey));
            }

            List<Survey> surveyList = getTestedSurveys();
            for(Survey testedSurvey : surveyList){
                if(db.getSurveyTemplate(testedSurvey.getIdOfSurveys()) == null){
                    loadSurveyTemplateTest(testedSurvey);
                }
            }
        }

    private void loadSurveyTemplateTest(Survey survey){
        super.loadSurveyTemplate(survey, SurveyHandler.ACTIVE);

        db.addSurveyTemplate(survey, SurveyHandler.ACTIVE, false);
    }
    private List<Survey> getTestedSurveys(){
        List<Survey> surveys = new ArrayList<>();

        JsonSurveySerializator jsonSurveySerializator = new JsonSurveySerializator();

        Survey surveyAboutApp = jsonSurveySerializator.deserializeSurvey("{\"description\":\"Bardzo proszę o wypełnienie tej ankiety i przesłanie wyników w formacie .csv (odpowiedni plik można wygenerować w menu wypełnionych ankiet) na adres: mobilnyankieter.android@interia.pl\",\"deviceId\":\"F4:09:D8:AE:12:FB--4535596914055203856\",\"idOfSurveys\":\"F4:09:D8:AE:12:FB--4535596914055203856000001\",\"questions\":[{\"type\":\"TextQuestion\",\"properties\":{\"hint\":\"\",\"question\":\"Proszę podać swoje imię i nazwisko.\",\"obligatory\":false}},{\"type\":\"TextQuestion\",\"properties\":{\"hint\":\"\",\"question\":\"Proszę podać markę i model swojego urządzenia, na którym testowana jest aplikacja. \",\"obligatory\":true}},{\"type\":\"TextQuestion\",\"properties\":{\"hint\":\"\",\"question\":\"Proszę podać wersję systemu Android na urządzeniu.\",\"obligatory\":false}},{\"type\":\"ScaleQuestion\",\"properties\":{\"maxLabel\":\"intuicyjny, bezawaryjny\",\"minLabel\":\"nieintuicyjny, awaryjny\",\"maxValue\":10,\"minValue\":0,\"userAnswer\":-2147483648,\"hint\":\"\",\"question\":\"Proszę ocenić moduł tworzenia nowej ankiety.\",\"obligatory\":false}},{\"type\":\"TextQuestion\",\"properties\":{\"hint\":\"Co można zmienić? Co jest dobre? Co jest złe? \",\"question\":\"Proszę ocenić moduł tworzenia nowej ankiety.\",\"obligatory\":false}},{\"type\":\"ScaleQuestion\",\"properties\":{\"maxLabel\":\"intuicyjny, bezawaryjny\",\"minLabel\":\"nieintuicyjny, awaryjny\",\"maxValue\":10,\"minValue\":0,\"userAnswer\":-2147483648,\"hint\":\"\",\"question\":\"Proszę ocenić moduł wypełniania ankiet.\",\"obligatory\":false}},{\"type\":\"TextQuestion\",\"properties\":{\"hint\":\"Co można zmienić? Co jest dobre? Co jest złe? \",\"question\":\"Proszę ocenić moduł wypełniania ankiet.\",\"obligatory\":false}},{\"type\":\"ScaleQuestion\",\"properties\":{\"maxLabel\":\"intuicyjny, bezawaryjny\",\"minLabel\":\"nieintuicyjny, awaryjny\",\"maxValue\":10,\"minValue\":0,\"userAnswer\":-2147483648,\"hint\":\"\",\"question\":\"Proszę ocenić moduł zarządzania szablonami ankiet. \",\"obligatory\":false}},{\"type\":\"TextQuestion\",\"properties\":{\"hint\":\"Co można zmienić? Co jest dobre? Co jest złe? \",\"question\":\"Proszę ocenić moduł zarządzania szablonami ankiet. \",\"obligatory\":false}},{\"type\":\"ScaleQuestion\",\"properties\":{\"maxLabel\":\"intuicyjny, bezawaryjny\",\"minLabel\":\"nieintuicyjny, awaryjny\",\"maxValue\":10,\"minValue\":0,\"userAnswer\":-2147483648,\"hint\":\"\",\"question\":\"Proszę ocenić moduł zarządzania wynikami ankiet. \",\"obligatory\":false}},{\"type\":\"TextQuestion\",\"properties\":{\"hint\":\"Co można zmienić? Co jest dobre? Co jest złe? \",\"question\":\"Proszę ocenić moduł zarządzania wynikami ankiet. \",\"obligatory\":false}},{\"type\":\"TextQuestion\",\"properties\":{\"hint\":\"\",\"question\":\"Co można zmienić w aplikacji, co jest fajne?\",\"obligatory\":false}},{\"type\":\"TextQuestion\",\"properties\":{\"hint\":\"\",\"question\":\"Czy w trakcie korzystania z aplikacji pojawiły się błędy? Jeśli tak to jakie i w jakiej sytuacji?\",\"obligatory\":false}},{\"type\":\"TextQuestion\",\"properties\":{\"hint\":\"\",\"question\":\"Jak oceniasz wygląd aplikacji? Co można w nim zmienić? \",\"obligatory\":false}}],\"summary\":\"Dziękuję za wypełnienie, przesłanie wyników w formacie .csv (odpowiedni plik można wygenerować w menu wypełnionych ankiet) na adres: mobilnyankieter.android@interia.pl\",\"title\":\"Ankieta oceniająca Mobilnego ankietera\",\"numberOfSurvey\":0}\n");
        surveys.add(surveyAboutApp);

        Survey exampleSurvey = jsonSurveySerializator.deserializeSurvey("{\"description\":\"to jest ankieta przykładowa\",\"deviceId\":\"F4:09:D8:AE:12:FB-5925391715879136549\",\"idOfSurveys\":\"F4:09:D8:AE:12:FB-592539171587913654900002\",\"questions\":[{\"type\":\"OneChoiceQuestion\",\"properties\":{\"answers\":[\"tak\",\"nie\",\"nie wiem\"],\"isDropDownList\":false,\"userAnswer\":-1,\"hint\":\"\",\"question\":\"Lubisz matematykę?\",\"obligatory\":false}},{\"type\":\"MultipleChoiceQuestion\",\"properties\":{\"answers\":[\"pies\",\"kot\",\"żółw \",\"wąż \"],\"userAnswers\":[],\"hint\":\"\",\"question\":\"Zaznacz zwierzęta,  które chciałbyś kiedyś mieć. \",\"obligatory\":true}},{\"type\":\"ScaleQuestion\",\"properties\":{\"maxLabel\":\"bardzo lubię \",\"minLabel\":\"nie lubię \",\"maxValue\":10,\"minValue\":0,\"userAnswer\":-2147483648,\"hint\":\"\",\"question\":\"Jak bardzo lubisz swoją uczelnię/pracę? \",\"obligatory\":false}},{\"type\":\"GridQuestion\",\"properties\":{\"columnLabels\":[\"1\",\"2\",\"3\",\"4\",\"5\"],\"rowLabels\":[\"Adam\",\"Andrzej\",\"Dominik\"],\"userAnswers\":[],\"hint\":\"\",\"question\":\"Jak bardzo lubisz dane imię? \",\"obligatory\":false}},{\"type\":\"TextQuestion\",\"properties\":{\"constraint\":{\"type\":\"TextConstraint\",\"properties\":{\"minLength\":3}},\"hint\":\"\",\"question\":\"Opowiedz coś o sobie. \",\"obligatory\":false}},{\"type\":\"OneChoiceQuestion\",\"properties\":{\"answers\":[\"żółty \",\"zielony \",\"niebieski \"],\"isDropDownList\":true,\"userAnswer\":-1,\"hint\":\"\",\"question\":\"Podaj swój ulubiony kolor.\",\"obligatory\":false}},{\"type\":\"DateTimeQuestion\",\"properties\":{\"onlyDate\":true,\"onlyTime\":false,\"hint\":\"\",\"question\":\"Który dzisiaj jest? \",\"obligatory\":true}},{\"type\":\"DateTimeQuestion\",\"properties\":{\"onlyDate\":false,\"onlyTime\":true,\"hint\":\"\",\"question\":\"O której wstales? \",\"obligatory\":false}}],\"summary\":\"Dziękuję za poświęcony czas! \",\"title\":\"Przykładowa ankieta \",\"numberOfSurvey\":0}\n");
        surveys.add(exampleSurvey);

        return surveys;
    }

    /**
     * Dodaje nowy szablon ankiety do klasy SurveyTemplate i do bazy danych.
     * @param survey ankieta do dodania.
     * @return id dodanego szablonu (id grupy ankiet), jeśli nie udało się dodać szablonu do bazy
     * danych, zwraca -1.
     */
    @Override
    public String addNewSurveyTemplate(Survey survey) {
        if(survey == null){
            throw new NullPointerException("Given survey mustn't be null.");
        }

        String id =  super.addNewSurveyTemplate(survey);
        super.setSurveyStatus(survey, SurveyHandler.ACTIVE);

        if(!db.addSurveyTemplate(survey, SurveyHandler.ACTIVE, false)){
            return null;
        }

        ApplicationState.getInstance(context).saveLastAddedSurveyTemplateNumber(super.getMaxSurveysId());

        return id;
    }

    @Override
    public void deleteSurveyTemplate(Survey survey) {
        super.deleteSurveyTemplate(survey);

        db.deleteSurveyTemplate(survey.getIdOfSurveys());
    }
}
