package com.banking.account_cmd.api.command;

public interface CommandHandler {

    void handle(OpenAccountCommand command);
    void handle(WithDrawFundsCommand command);
    void handle(DepositFundsCommand command);
    void handle(CloseAccountCommand command);
}
