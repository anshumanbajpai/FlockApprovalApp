package co.flock.approval;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

public class Runner {

    private static final Logger _logger = LoggerFactory.getLogger(Runner.class);

    public static void main(String[] args) {

        _logger.debug("Starting..");
        port(9000);

        get("/", (req, res) -> {
            System.out.println("Req : " + req.body());
            return "hello get";
        });

        post("/", (req, res) -> {
            _logger.debug("Req received : {} {} ", req, res);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONObject jsonObject = new JSONObject(req.body());
            String type = (String) jsonObject.get("name");
            if ("app.install".equals(type)) {
                String userId = jsonObject.getString("userId");
                _logger.debug("Install event received {}", userId);
                System.out.println("Userid : " + userId);

            } else {

            }
            return "";
        });
    }
}
