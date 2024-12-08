package nus.iss.vttp5_ssf_day16w_practice.service;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import nus.iss.vttp5_ssf_day16w_practice.exception.BoardGameNotFoundException;
import nus.iss.vttp5_ssf_day16w_practice.model.BoardGame;
import nus.iss.vttp5_ssf_day16w_practice.repo.BoardGameRepo;

@Service
public class BoardGameService {

    @Autowired
    BoardGameRepo boardGameRepo;

    // insert games.json into redis 
    public void insertBoardGamesService() throws Exception { 

        // read "games.json" using stream 

        String path = "data/game.json";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);

        if (inputStream == null) {
            throw new FileNotFoundException("game.json not found");
        }

        // use JsonReader to read stream 
        JsonReader jReader = Json.createReader(inputStream);

        // read the JsonArray of objects 
        // [{}, {}, {}]
        JsonArray jArray = jReader.readArray();

        for (JsonValue jvBoardGame : jArray) {

            // cast JsonValue into JsonObject 
            JsonObject joBoardGame = (JsonObject) jvBoardGame;

            // extract information 
            Integer gid = joBoardGame.getInt("gid");
            String name = joBoardGame.getString("name");
            Integer year = joBoardGame.getInt("year");

            // create BoardGame POJO 
            BoardGame boardGame = new BoardGame(gid, name, year); 

            // BoardGame POJO --> JsonObject 
            JsonObjectBuilder builder = Json.createObjectBuilder()
                                            .add("gid", boardGame.getGid())
                                            .add("name", boardGame.getName())
                                            .add("year", boardGame.getYear());
            JsonObject jsonObject = builder.build(); 

            // JsonObject --> JsonString 
            String boardGameString = jsonObject.toString(); 

            // hash key for saving to redis 
            String hashKey = boardGame.getGid().toString();

            // save to Redis 
            boardGameRepo.saveBoardGame(hashKey, boardGameString);

        }

        System.out.println("Board games saved to Redis successfully!");
        
    }

    // retrieve all board games 
    // convert to POJOs 
    public List<BoardGame> getAllBoardGamesService() { 

        List<Object> boardGameValues = boardGameRepo.getAllBoardGameValues();

        // create a list to hold BoardGame POJOs 
        List<BoardGame> boardGames = new ArrayList<>(); 

        // convert each JSON string into a BoardGame POJO using a loop 
        for (Object joBoardGame : boardGameValues) {

            String jsBoardGame = joBoardGame.toString(); 
            BoardGame boardGame = jsonStringToBoardGame(jsBoardGame);
            boardGames.add(boardGame);

        }

        return boardGames; 

    }

    // helper method to parse JSON string into a BoardGame POJO 
    private BoardGame jsonStringToBoardGame(String jString) {

        StringReader sr = new StringReader(jString);
        JsonReader jr = Json.createReader(sr);
        JsonObject jo = jr.readObject();

        // map JSON fields to a BoardGame POJO 

        BoardGame boardGame = new BoardGame(
            jo.getInt("gid"),
            jo.getString("name"),
            jo.getInt("year"));

        return boardGame;

    }

    // helper method to convert BoardGame POJO into JSON formatted string (for saving to redis)
    private String POJOToJsonString(BoardGame boardGame) {
        JsonObjectBuilder builder = Json.createObjectBuilder()
                                        .add("gid", boardGame.getGid())
                                        .add("name", boardGame.getName())
                                        .add("year", boardGame.getYear());

        JsonObject jObject = builder.build(); 
        return jObject.toString(); 

    }

    // insert one board game into redis via POST request
    public String insertBoardGame(String data) { 

        // convert string to BoardGame POJO
        BoardGame boardGame = jsonStringToBoardGame(data);

        // get gid 
        String gid = boardGame.getGid().toString();

        // convert BoardGame POJO to json string
        String jString = POJOToJsonString(boardGame);

        boardGameRepo.saveBoardGame(gid, jString);

        // build confirmation response (payload)
        JsonObject payload = Json.createObjectBuilder()
                                .add("insert_count", 1)
                                .add("id", gid)
                                .build();

        return payload.toString();


    }

    // task 2
    // get board game from redis via GET request
    public BoardGame getBoardGameService(String gid) { 

        String jsonString = boardGameRepo.getBoardGameByGID(gid);

        if (jsonString == null || jsonString.isEmpty()) {
            throw new BoardGameNotFoundException(gid);
            
        }

        BoardGame boardGame = jsonStringToBoardGame(jsonString);

        return boardGame;

    }

    // task 3 
    // update board game from redis via PUT request 
    public String updateBoardGameService(String gid, String boardGameRaw, Boolean upsert) { 

        // if game key exists
        if (boardGameRepo.checkKey(gid)) {

            // JSON String --> BoardGame
            BoardGame boardGame = jsonStringToBoardGame(boardGameRaw);

            // get gid for hash key 
            String GIDHashKey = boardGame.getGid().toString();

            // BoardGame --> JSON String 
            String boardGameJSONString = POJOToJsonString(boardGame);

            // save new data to Redis 
            boardGameRepo.saveBoardGame(GIDHashKey, boardGameJSONString);

            // build confirmation response (payload) 
            JsonObject payload = Json.createObjectBuilder()
                                    .add("update_count", 1)
                                    .add("id", GIDHashKey)
                                    .build();

            return payload.toString();

        } 

        // upsert == true, create new BoardGame entry
        else if (upsert && upsert == true) { 

            // JSON String --> BoardGame
            BoardGame boardGame = jsonStringToBoardGame(boardGameRaw);

            // get gid for hash key 
            String GIDHashKey = boardGame.getGid().toString();

            // BoardGame --> JSON String 
            String boardGameJSONString = POJOToJsonString(boardGame);

            // save new data to Redis 
            boardGameRepo.saveBoardGame(GIDHashKey, boardGameJSONString);

            // build confirmation response (payload) 
            JsonObject payload = Json.createObjectBuilder()
                                    .add("update_count", 1)
                                    .add("id", GIDHashKey)
                                    .build();

            return payload.toString();

        }

        // ELSE
        // board game key not found
        else {

            throw new BoardGameNotFoundException(gid);

        }

    }

    
}
