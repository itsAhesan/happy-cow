package com.xworkz.happycow.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BankForm {


    private Integer agentId; // posted as hidden field

    private String bankName;


    private String branchName;


    private String accountHolderName;

    private String accountNumber;


    private String confirmAccountNumber;


    private String ifsc;

    private String accountType;


}
