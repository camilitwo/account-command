package com.banking.account_cmd.domain;

import com.banking.account_cmd.api.command.OpenAccountCommand;
import com.banking.account_command.events.AccountClosedEvent;
import com.banking.account_command.events.AccountOpenedEvent;
import com.banking.account_command.events.FundsDepositedEvent;
import com.banking.account_command.events.FundsWithdrawnEvent;
import com.banking.cqrs_core.domain.AggregateRoot;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
public class AccountAggregate extends AggregateRoot {
    private Boolean active;
    private double balance;

    public AccountAggregate(OpenAccountCommand command){
        raiseEvent(AccountOpenedEvent.builder()
                .id(command.getId())
                .accountHolder(command.getAccountHolder())
                .accountType(command.getAccountType())
                .createDate(new Date())
                .openingBalance(command.getOpeningBalance())
                .build());
    }

    public void depositFunds(double amount) {
        if(!this.active){
            throw new IllegalStateException("Account is not active");
        }
        if(amount <= 0){
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        raiseEvent(FundsDepositedEvent.builder()
                .id(this.id)
                .amount(amount)
                .build());
    }

    public void withdrawFunds(double amount) {
        if(!this.active){
            throw new IllegalStateException("Account is not active");
        }
        if(amount <= 0){
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if(amount > this.balance){
            throw new IllegalArgumentException("Amount must be less than or equal to balance");
        }
        raiseEvent(FundsWithdrawnEvent.builder()
                .id(this.id)
                .amount(amount)
                .build());
    }

    public void closeAccount(){
        if(!this.active){
            throw new IllegalStateException("Account is not active");
        }
        raiseEvent(AccountClosedEvent.builder()
                .id(this.id)
                .build());
    }

    public void apply(AccountOpenedEvent event){
        this.id = event.getId();
        this.active = true;
        this.balance = event.getOpeningBalance();
    }

    public void apply(FundsDepositedEvent event){
        this.id = event.getId();
        this.balance += event.getAmount();
    }

    public void apply(FundsWithdrawnEvent event){
        this.id = event.getId();
        this.balance -= event.getAmount();
    }

    public void apply(AccountClosedEvent event){
        this.id = event.getId();
        this.active = false;
    }
}

