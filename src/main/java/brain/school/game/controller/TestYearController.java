package brain.school.game.controller;


import brain.school.game.dto.TestYearDto;
import brain.school.game.model.TestYear;
import brain.school.game.service.BrainSchoolGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@ResponseBody
@RequestMapping("/api/test")
public class TestYearController {


    @Autowired
    private BrainSchoolGameService schoolGameService;

    @PostMapping
    public ResponseEntity<TestYear> createTest(@RequestBody TestYearDto testYearDto) {
        return new ResponseEntity<>(schoolGameService.create(testYearDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TestYear>> getTest(){
        return new ResponseEntity<>(schoolGameService.get(), HttpStatus.OK);
    }

    @GetMapping("/year/discipline")
    public ResponseEntity<TestYear> getByYearAndDiscipline(
            @RequestParam("year") String year ,
            @RequestParam("discipline") String discipline
    ){
        return new ResponseEntity<>(schoolGameService.getByYearAndDiscipline(year, discipline), HttpStatus.OK);
    }

    @GetMapping("/ano/{ano}")
    public ResponseEntity<TestYear> getAno(@PathVariable("ano") String ano) {
        return new ResponseEntity<>(schoolGameService.getYear(ano), HttpStatus.OK);
    }

    @GetMapping("/{disciplina}")
    public ResponseEntity<TestYear> getTest(@PathVariable("disciplina") String disciplina) {
        return new ResponseEntity<>(schoolGameService.getOne(disciplina), HttpStatus.OK);
    }



}
