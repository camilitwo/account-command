package com.banking.account_cmd.api.command;

import com.banking.account_cmd.domain.AccountAggregate;
import com.banking.cqrs_core.handlers.EventSourcingHandler;
import org.springframework.stereotype.Service;

@Service
public class AccountCommandHandler implements CommandHandler{

    private final EventSourcingHandler<AccountAggregate> eventSourcingHandler;

    public AccountCommandHandler(EventSourcingHandler<AccountAggregate> eventSourcingHandler) {
        this.eventSourcingHandler = eventSourcingHandler;
    }

    @Override
    public void handle(OpenAccountCommand command) {
        var account = new AccountAggregate(command);
        eventSourcingHandler.save(account);
    }

    @Override
    public void handle(WithDrawFundsCommand command) {
        var account = eventSourcingHandler.getById(command.getId());
        if(command.getAmount() > account.getBalance())
            throw new IllegalArgumentException("Insufficient funds");

        account.withdrawFunds(command.getAmount());
        eventSourcingHandler.save(account);
    }

    @Override
    public void handle(DepositFundsCommand command) {
        var account = eventSourcingHandler.getById(command.getId());
        account.depositFunds(command.getAmount());
        eventSourcingHandler.save(account);
    }

    @Override
    public void handle(CloseAccountCommand command) {
        var account = eventSourcingHandler.getById(command.getId());
        account.closeAccount();
        eventSourcingHandler.save(account);
    }
}
