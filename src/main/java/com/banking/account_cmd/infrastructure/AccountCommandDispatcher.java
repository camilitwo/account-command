package com.banking.account_cmd.infrastructure;

import com.banking.cqrs_core.commands.BaseCommand;
import com.banking.cqrs_core.commands.CommandHandlerMethod;
import com.banking.cqrs_core.infrastructure.CommandDispatcher;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class AccountCommandDispatcher implements CommandDispatcher {

    private final Map<Class<? extends BaseCommand>, List<CommandHandlerMethod>> routes = new HashMap<>();

    @Override
    public <T extends BaseCommand> void registerHandler(Class<T> commandClass, CommandHandlerMethod<T> handlerMethod) {
        var handlers = routes.computeIfAbsent(commandClass, c -> new LinkedList<>());
        handlers.add(handlerMethod);
    }

    @Override
    public void send(BaseCommand command) {
        var handlers = routes.get(command.getClass());
        if (handlers == null || handlers.isEmpty()) {
            throw new RuntimeException("No handler registered for " + command.getClass());
        }

        if(handlers.size() > 1) {
            throw new RuntimeException("More than one handler registered for " + command.getClass());
        }

        handlers.get(0).handle(command);
    }
}
