package com.banking.account_cmd.api.controllers;

import com.banking.account_cmd.api.command.DepositFundsCommand;
import com.banking.account_cmd.api.dto.DepositFundsResponse;
import com.banking.account_command.dto.BaseResponse;
import com.banking.cqrs_core.infrastructure.CommandDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/api/v1/deposit-funds")
@Slf4j
public class DepositFundsController {

    private final CommandDispatcher commandDispatcher;

    public DepositFundsController(CommandDispatcher commandDispatcher) {
        this.commandDispatcher = commandDispatcher;
    }

    @PutMapping(path="/{id}")
    public ResponseEntity<BaseResponse> depositFunds(@PathVariable(value = "id") String id,
    @RequestBody DepositFundsCommand command) {
        log.info("DepositFundsController.depositFunds() - Start");
        command.setId(id);
        commandDispatcher.send(command);
        log.info("DepositFundsController.depositFunds() - End");
        return new ResponseEntity<>(new DepositFundsResponse("Funds deposited successfully", id),
                HttpStatus.OK);

    }
}
