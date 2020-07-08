package com.aptar.printer.app.service;

import com.aptar.printer.app.dto.PrintDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class PrintService {
    @Value("${app.folders.path.data}")
    private String filePath;

    public String print(PrintDto printDto){
        String fileName =  filePath + "/" + printDto.getPrinterName() + ".json";
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(fileName);
        try {
            // Serialize Java object info JSON file.
            mapper.writeValue(file, printDto.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Data saved successfully";
    }
}
