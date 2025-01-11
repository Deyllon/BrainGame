package brain.school.game.controller;


import brain.school.game.dto.RankedUserDto;
import brain.school.game.model.User;
import brain.school.game.service.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@ResponseBody
@RequestMapping("/api/ranking")
public class RankingController {

    @Autowired
    private RankingService rankingService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<RankedUserDto>> getRanking(@PathVariable("userId") Long userId,
                                                          @RequestParam(value = "municipio", required = false) String municipio,
                                                          @RequestParam(value = "estado", required = false) String estado
    ){
        return new ResponseEntity<>(rankingService.ranking(userId, municipio, estado), HttpStatus.OK);
    }
}
