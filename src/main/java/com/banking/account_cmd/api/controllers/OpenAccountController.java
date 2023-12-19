package com.banking.account_cmd.api.controllers;

import com.banking.account_cmd.api.command.OpenAccountCommand;
import com.banking.account_cmd.api.dto.OpenAccountResponse;
import com.banking.account_command.dto.BaseResponse;
import com.banking.cqrs_core.infrastructure.CommandDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path="/api/v1/open-account")
@Slf4j
public class OpenAccountController {

    private final CommandDispatcher commandDispatcher;

    public OpenAccountController(CommandDispatcher commandDispatcher) {
        this.commandDispatcher = commandDispatcher;
    }

    @PostMapping
    public ResponseEntity<BaseResponse> openAccount(@RequestBody OpenAccountCommand command) {
        log.info("OpenAccountController.openAccount() - Start");
        String id = UUID.randomUUID().toString();
        command.setId(id);
        commandDispatcher.send(command);
        log.info("OpenAccountController.openAccount() - End");
        return new ResponseEntity<>(new OpenAccountResponse("Account opened successfully", id),
                HttpStatus.CREATED);

    }
}
