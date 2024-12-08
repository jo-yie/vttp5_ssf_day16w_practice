package nus.iss.vttp5_ssf_day16w_practice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nus.iss.vttp5_ssf_day16w_practice.exception.BoardGameNotFoundException;
import nus.iss.vttp5_ssf_day16w_practice.model.BoardGame;
import nus.iss.vttp5_ssf_day16w_practice.service.BoardGameService;

@RestController
@RequestMapping(path = "/api/boardgame")
public class BoardGameController {

    @Autowired
    BoardGameService boardGameService;

    // task 0 
    // insert from games.json into redis 
    @GetMapping("/insert")
    public ResponseEntity<String> insertBoardGames() throws Exception { 

        boardGameService.insertBoardGamesService();

        return ResponseEntity.ok().build();

    }

    // task 0
    // return all board games
    @GetMapping("/all")
    public ResponseEntity<List<BoardGame>> getAllBoardGames() {

        // try {
        //     // retrieve all board games 
        //     List<BoardGame> boardGames = boardGameService.getAllBoardGamesService();

        //     // check if list is empty 
        //     if (boardGames.isEmpty()) {
        //         // returns 204 no content
        //         return ResponseEntity.noContent().build();

        //     } else {
        //         // return the list of board games with 200 OK status
        //         return ResponseEntity.ok(boardGames);

        //     }

        // } 
        // catch (Exception e) {
        //     // return a 500 internal server error in case of an exception 
        //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        // }

        // retrieve all board games 
        List<BoardGame> boardGames = boardGameService.getAllBoardGamesService();

        // check if list is empty 
        if (boardGames.isEmpty()) {
            // returns 204 no content
            return ResponseEntity.noContent().build();

        } else {
            // return the list of board games with 200 OK status
            return ResponseEntity.ok(boardGames);

        }
    }

    // task 1 
    // insert one board game into redis 
    @PostMapping("")
    public ResponseEntity<String> newBoardGame(@RequestBody String boardGameRaw) {

        try { 
            String payload = boardGameService.insertBoardGame(boardGameRaw);
            return ResponseEntity.status(201).body(payload);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to create board game " + e.getMessage());

        }

    }

    // task 2
    // retrieve a given board game 
    // GET /api/boardgame/<boardgame id>
    @GetMapping("/{boardGameGID}")
    public ResponseEntity<Object> getBoardGame(@PathVariable String boardGameGID) { 

        try { 

            BoardGame boardGame = boardGameService.getBoardGameService(boardGameGID);
            return ResponseEntity.ok().body(boardGame);

        } catch (BoardGameNotFoundException e) {

            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        
        } catch (Exception e) {

            return ResponseEntity.status(500).body("Error: " + e.getMessage());

        }

    }

    // task 3 
    // update redis 
    // PUT /api/boardgame/<boardgame id>?upsert=true
    @PutMapping("/{boardGameGID}")
    public ResponseEntity<String> updateBoardGame(@PathVariable String boardGameGID, 
                                @RequestBody String boardGameRaw, 
                                @RequestParam(required = false, defaultValue = "false") Boolean upsert) { 

        try {

            String payload = boardGameService.updateBoardGameService(boardGameGID, boardGameRaw, upsert);
            return ResponseEntity.status(200).body(payload);

        } catch (BoardGameNotFoundException e) {

            return ResponseEntity.status(404).body("Error: " + e.getMessage());

        }

    }
    

}
