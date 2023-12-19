package com.banking.account_cmd.api.controllers;

import com.banking.account_cmd.api.command.WithDrawFundsCommand;
import com.banking.account_command.dto.BaseResponse;
import com.banking.cqrs_core.infrastructure.CommandDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/api/v1/withdraw-funds")
@Slf4j
public class WithdrawFundsController {

    private final CommandDispatcher commandDispatcher;

    public WithdrawFundsController(CommandDispatcher commandDispatcher) {
        this.commandDispatcher = commandDispatcher;
    }

    @PutMapping(path="/{id}")
    public ResponseEntity<BaseResponse> withdrawFunds(@PathVariable(value = "id") String id,
                                                      @RequestBody WithDrawFundsCommand command) {
        log.info("WithdrawFundsController.withdrawFunds() - Start");
        command.setId(id);
        commandDispatcher.send(command);
        log.info("WithdrawFundsController.withdrawFunds() - End");
        return new ResponseEntity<>(new BaseResponse("Funds withdrawn successfully"), HttpStatus.OK);


    }

}
