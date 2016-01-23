package dictionary.bot;

import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Query;

import java.util.List;

/**
 * Created by harshit on 21/1/16.
 */
public interface TriviaInterface {

    @Headers({
            "Accept: application/json",
            "X-Mashape-Key: YgXFx8fyJemshaRWbzMMcZ21Vcpqp1CE4jAjsnAFa0RBW99WmV"
    })
    @GET("/v1/getAllQuizQuestions")
    List<Question> getAllQuestions(@Query("limit") int limit,
                                       @Query("page") int page);
}
