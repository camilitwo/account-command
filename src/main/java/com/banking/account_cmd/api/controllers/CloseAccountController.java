package com.banking.account_cmd.api.controllers;

import com.banking.account_cmd.api.command.CloseAccountCommand;
import com.banking.account_command.dto.BaseResponse;
import com.banking.cqrs_core.infrastructure.CommandDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="/api/v1/close-account")
@Slf4j
public class CloseAccountController {

    private final CommandDispatcher commandDispatcher;

    public CloseAccountController(CommandDispatcher commandDispatcher) {
        this.commandDispatcher = commandDispatcher;
    }

    @DeleteMapping(path="/{id}")
    public ResponseEntity<BaseResponse> closeAccount(@PathVariable String id) {
        log.info("CloseAccountController.closeAccount() - Start");
        commandDispatcher.send(new CloseAccountCommand(id));
        log.info("CloseAccountController.closeAccount() - End");
        return new ResponseEntity<>(new BaseResponse("Account closed successfully"), HttpStatus.OK);
    }

}
