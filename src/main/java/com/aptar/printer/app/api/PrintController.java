package com.aptar.printer.app.api;

import com.aptar.printer.app.dto.PrintDto;
import com.aptar.printer.app.service.PrintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController()
@RequestMapping("print")
public class PrintController {

    @Autowired
    private PrintService printService;

    @PostMapping()
    public String print(@Valid @RequestBody PrintDto printDto){
        return printService.print(printDto);
    }
}
