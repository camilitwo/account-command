package com.banking.account_cmd.infrastructure;

import com.banking.account_cmd.domain.AccountAggregate;
import com.banking.cqrs_core.domain.AggregateRoot;
import com.banking.cqrs_core.events.BaseEvent;
import com.banking.cqrs_core.handlers.EventSourcingHandler;
import com.banking.cqrs_core.infrastructure.EventStore;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
public class AccountEventSourcingHandler implements EventSourcingHandler<AccountAggregate> {

    private final EventStore eventStore;

    public AccountEventSourcingHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void save(AggregateRoot aggregateRoot) {
        eventStore.save(aggregateRoot.getId(),
                aggregateRoot.getUncommittedChanges(),
                aggregateRoot.getVersion());
        aggregateRoot.markChangesAsCommitted();
    }

    @Override
    public AccountAggregate getById(String id) {
        var aggregate = new AccountAggregate();
        var events = eventStore.getEvents(id);
        if(events != null && !events.isEmpty()) {
            aggregate.replayEvents(events);
            var lastVersion = events.stream()
                    .map(BaseEvent::getVersion)
                    .max(Comparator.naturalOrder())
                    .orElse(0);
            aggregate.setVersion(lastVersion);
        }
        return aggregate;
    }
}
