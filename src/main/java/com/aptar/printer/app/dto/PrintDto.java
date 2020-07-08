package com.aptar.printer.app.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PrintDto {

    @NotNull
    private String printerName;
    @NotNull
    private PrintData data;

}
