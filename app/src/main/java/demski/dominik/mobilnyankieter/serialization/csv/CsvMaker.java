package demski.dominik.mobilnyankieter.serialization.csv;

import java.util.ArrayList;
import java.util.List;

import bohonos.demski.mieldzioc.mobilnyankieter.common.Pair;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.DateTimeQuestion;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.GridQuestion;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.Question;
import bohonos.demski.mieldzioc.mobilnyankieter.serialization.ISerializator;
import bohonos.demski.mieldzioc.mobilnyankieter.survey.Survey;
import demski.dominik.mobilnyankieter.utilities.DateAndTimeService;

public class CsvMaker implements ISerializator {
	private final String separator;

	public CsvMaker(String separator) {
		this.separator = separator;
	}

	public String serializeListOfSurveys(List<Survey> surveys) {
		if (surveys == null) {
			throw new NullPointerException("Surveys must not be null.");
		}

		if (surveys.isEmpty()) {
			return "";
		}

		List<Pair<String, List<String>>> answers = new ArrayList<>();

		prepareHeaders(surveys, answers);

		for (Survey survey : surveys) {
			int indexInAnswersList = 0;

			answers.get(indexInAnswersList).getSecond()
					.add(DateAndTimeService.getDateAsDBString(survey.getStartTime()));
			indexInAnswersList++;

			answers.get(indexInAnswersList).getSecond()
					.add(DateAndTimeService.getDateAsDBString(survey.getFinishTime()));
			indexInAnswersList++;

			for (int i = 0; i < survey.questionListSize(); i++) {
				Question question = survey.getQuestion(i);

				int questionType = question.getQuestionType();

				if (questionType == Question.GRID_QUESTION) {
					indexInAnswersList = putGridAnswers(answers, indexInAnswersList, question);
				} else if (questionType == Question.DATE_QUESTION) {
					indexInAnswersList = putDateAnswer(answers, indexInAnswersList, question);
				} else if (questionType == Question.TIME_QUESTION) {
					indexInAnswersList = putTimeAnswer(answers, indexInAnswersList, question);
				} else {
					indexInAnswersList = putOtherAnswers(answers, indexInAnswersList, question);
				}
			}
		}

		StringBuilder csvStringBuilder = new StringBuilder();

		for (Pair<String, List<String>> pair : answers) {
			String header = pair.getFirst();

			if (header.contains(separator)) {
				header = "\"" + header + "\"";
			}

			csvStringBuilder.append(header + separator);
		}

		csvStringBuilder.append("\n");

		if (answers.size() > 0) {
			int listLength = answers.get(0).getSecond().size();

			for (int l = 0; l < listLength; l++) {
				for (int k = 0; k < answers.size(); k++) {
					String answer = answers.get(k).getSecond().get(l);

					if (answer.contains(separator)) {
						answer = "\"" + answer + "\"";
					}

					csvStringBuilder.append(answer + separator);
				}
				csvStringBuilder.append("\n");
			}
		}

		return csvStringBuilder.toString();
	}

	private int putOtherAnswers(List<Pair<String, List<String>>> answers, int indexInAnswersList, Question question) {
		List<String> userAnswers = question.getUserAnswersAsStringList();

		String joinedAnswers = "";

		for (String answer : userAnswers) {
			joinedAnswers += answer + ", ";
		}

		if (!joinedAnswers.isEmpty()) {
			joinedAnswers = joinedAnswers.substring(0, joinedAnswers.length() - 2);
		}

		answers.get(indexInAnswersList).getSecond().add(joinedAnswers);
		indexInAnswersList++;

		return indexInAnswersList;
	}

	private int putTimeAnswer(List<Pair<String, List<String>>> answers, int indexInAnswersList, Question question) {
		DateTimeQuestion dateTimeQuestion = (DateTimeQuestion) question;

		String userAnswer = "";

		List<String> answer = dateTimeQuestion.getUserAnswersAsStringList();

		if (!answer.isEmpty()) {
			userAnswer = answer.get(0) + ":" + answer.get(1);

			answers.get(indexInAnswersList).getSecond().add(userAnswer);
			indexInAnswersList++;
		}

		return indexInAnswersList;
	}

	private int putDateAnswer(List<Pair<String, List<String>>> answers, int indexInAnswersList, Question question) {
		DateTimeQuestion dateTimeQuestion = (DateTimeQuestion) question;

		String userAnswer = "";

		List<String> answer = dateTimeQuestion.getUserAnswersAsStringList();

		if (!answer.isEmpty()) {
			userAnswer = DateAndTimeService.addFirstZeros(Integer.parseInt(answer.get(0))) + "-"
					+ DateAndTimeService.addFirstZeros(Integer.parseInt(answer.get(1))) + "-" + answer.get(2);
			
			answers.get(indexInAnswersList).getSecond().add(userAnswer);
			indexInAnswersList++;
		}

		return indexInAnswersList;
	}

	private int putGridAnswers(List<Pair<String, List<String>>> answers, int indexInAnswersList, Question question) {
		GridQuestion gridQuestion = (GridQuestion) question;

		List<String> rowLabels = gridQuestion.getRowLabels();
		List<String> columnLabels = gridQuestion.getColumnLabels();

		for (int j = 0; j < rowLabels.size(); j++) {
			String totalAnswer = "";

			for (int k = 0; k < columnLabels.size(); k++) {
				if (gridQuestion.ifCheckedPair(new Pair<Integer, Integer>(j, k))) {
					String answer = gridQuestion.getAsnwerForCoordinates(new Pair<Integer, Integer>(j, k));
					answer = answer.replaceFirst(rowLabels.get(j), "");
					answer = answer.replace('^', ' ');
					answer = answer.replaceAll("#", "");
					answer = answer.trim();

					if (!totalAnswer.isEmpty()) {
						answer = ", " + answer;
					}

					totalAnswer += answer;
				}
			}

			answers.get(indexInAnswersList).getSecond().add(totalAnswer);
			indexInAnswersList++;
		}

		return indexInAnswersList;
	}

	private void prepareHeaders(List<Survey> surveys, List<Pair<String, List<String>>> answers) {
		Survey firstSurvey = surveys.get(0);

		answers.add(new Pair<String, List<String>>("Czas rozpocz�cia wype�niania", new ArrayList<String>()));
		answers.add(new Pair<String, List<String>>("Czas zako�czenia wype�niania", new ArrayList<String>()));

		for (int i = 0; i < firstSurvey.questionListSize(); i++) {
			Question question = firstSurvey.getQuestion(i);

			int questionType = question.getQuestionType();
			String questionText = question.getQuestion();

			if (questionType == Question.GRID_QUESTION) {
				GridQuestion gridQuestion = (GridQuestion) question;

				List<String> rowLabels = gridQuestion.getRowLabels();
				for (String rowLabel : rowLabels) {
					answers.add(new Pair<String, List<String>>(questionText + " [" + rowLabel + "]",
							new ArrayList<String>()));
				}
			} else {
				answers.add(new Pair<String, List<String>>(questionText, new ArrayList<String>()));
			}
		}
	}

	@Override
	public String serializeSurvey(Survey survey) {
		List<Survey> surveyList = new ArrayList<>(1);

		surveyList.add(survey);

		return serializeListOfSurveys(surveyList);
	}

	@Override
	public Survey deserializeSurvey(String surveyInJson) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Survey> deserializeListOfSurveys(String surveysInJson) {
		throw new UnsupportedOperationException();
	}

}
