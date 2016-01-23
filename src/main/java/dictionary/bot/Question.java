package dictionary.bot;

/**
 * Created by harshit on 21/1/16.
 */
public class Question {

    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getQ_options_1() {
        return q_options_1;
    }

    public void setQ_options_1(String q_options_1) {
        this.q_options_1 = q_options_1;
    }

    public String getQ_options_2() {
        return q_options_2;
    }

    public void setQ_options_2(String q_options_2) {
        this.q_options_2 = q_options_2;
    }

    public String getQ_options_3() {
        return q_options_3;
    }

    public void setQ_options_3(String q_options_3) {
        this.q_options_3 = q_options_3;
    }

    public String getQ_options_4() {
        return q_options_4;
    }

    public void setQ_options_4(String q_options_4) {
        this.q_options_4 = q_options_4;
    }

    public int getQ_correct_option() {
        return q_correct_option;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", q_options_1='" + q_options_1 + '\'' +
                ", q_options_2='" + q_options_2 + '\'' +
                ", q_options_3='" + q_options_3 + '\'' +
                ", q_options_4='" + q_options_4 + '\'' +
                ", q_correct_option=" + q_correct_option +
                ", q_text='" + q_text + '\'' +
                '}';
    }

    public void setQ_correct_option(int q_correct_option) {
        this.q_correct_option = q_correct_option;
    }

    private String q_options_1;
    private String q_options_2;
    private String q_options_3;

    private String q_options_4;
    private int q_correct_option;

    public String getQ_text() {
        return q_text;
    }

    public void setQ_text(String q_text) {
        this.q_text = q_text;
    }

    private String q_text;

}
