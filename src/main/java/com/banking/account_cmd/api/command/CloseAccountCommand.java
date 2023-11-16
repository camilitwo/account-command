package com.banking.account_cmd.api.command;

import com.banking.cqrs_core.commands.BaseCommand;

public class CloseAccountCommand extends BaseCommand {
    public CloseAccountCommand(String id) {
        super(id);
    }
}
