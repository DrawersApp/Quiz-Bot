package dictionary.bot;

import retrofit.RestAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by harshit on 21/1/16.
 */
public class TriviaRetrofitAdapter {

    private TriviaRetrofitAdapter() {
        createDictionaryInterface();
    }

    private static TriviaRetrofitAdapter triviaRetrofitAdapter;
    public synchronized static TriviaRetrofitAdapter getTriviaRetrofitAdapter() {
        if (triviaRetrofitAdapter == null) {
            triviaRetrofitAdapter = new TriviaRetrofitAdapter();
        }
        return triviaRetrofitAdapter;
    }

    private TriviaInterface  triviaInterface;


    public TriviaInterface getTriviaInterface() {
        return triviaInterface;
    }

    private void createDictionaryInterface() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://pareshchouhan-trivia-v1.p.mashape.com")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        triviaInterface = restAdapter.create(TriviaInterface.class);
    }

    public static String generateString(Question question) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(question.getQ_text());
        stringBuilder.append("\n\n");
        stringBuilder.append("(1) ");
        stringBuilder.append(question.getQ_options_1());
        stringBuilder.append("\n");
        stringBuilder.append("(2) ");
        stringBuilder.append(question.getQ_options_2());
        stringBuilder.append("\n");
        stringBuilder.append("(3) ");
        stringBuilder.append(question.getQ_options_3());
        stringBuilder.append("\n");
        stringBuilder.append("(4) ");
        stringBuilder.append(question.getQ_options_4());
        return stringBuilder.toString();
    }

    public static List<Question> questionsList = new ArrayList<>();

    public static Map<String, CurrentState> currentQuestionsMap = new HashMap<>();

    public static void populateQuestionsListIfRequired() {
        if (questionsList.isEmpty()) {
            questionsList = triviaRetrofitAdapter.getTriviaInterface().getAllQuestions(1000, 1);
        }
    }

    public Question getQuestion(String user) {
        populateQuestionsListIfRequired();
        if (!currentQuestionsMap.containsKey(user)) {
            currentQuestionsMap.put(user, new CurrentState(0, false));
        }
        if (currentQuestionsMap.get(user).currentQuestion > 1000) {
            return null;
        }
        currentQuestionsMap.get(user).enqued = true;
        return questionsList.get(currentQuestionsMap.get(user).currentQuestion);
    }

    public String validateAnswer(String user, int anwer) {
        currentQuestionsMap.get(user).enqued = false;
        currentQuestionsMap.get(user).currentQuestion = currentQuestionsMap.get(user).currentQuestion + 1;
        Question question = questionsList.get(currentQuestionsMap.get(user).currentQuestion);
        return anwer == question.getQ_correct_option() ? "Correct!!" : "Correct option is"
                + question.getQ_correct_option();
    }

    public static class CurrentState {
        int currentQuestion;

        public CurrentState(int currentQuestion, boolean enqued) {
            this.currentQuestion = currentQuestion;
            this.enqued = enqued;
        }

        boolean enqued;
    }

    public static Integer isValidOption(String s) {
        try {
            return Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return -1;
        } catch(NullPointerException e) {
            return -1;
        }
    }
}
