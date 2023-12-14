package com.banking.account_cmd.infrastructure;

import com.banking.account_cmd.domain.AccountAggregate;
import com.banking.account_cmd.domain.EventStoreRepository;
import com.banking.cqrs_core.events.BaseEvent;
import com.banking.cqrs_core.events.EventModel;
import com.banking.cqrs_core.exceptions.AggregateNotFoundException;
import com.banking.cqrs_core.exceptions.ConcurrencyException;
import com.banking.cqrs_core.infrastructure.EventStore;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
public class AccountEventStore implements EventStore {

    private final EventStoreRepository eventStoreRepository;

    public AccountEventStore(EventStoreRepository eventStoreRepository) {
        this.eventStoreRepository = eventStoreRepository;
    }

    @Override
    public void save(String aggregateId, Iterable<BaseEvent> events, int expectedVersion) {
        var eventStream = eventStoreRepository.findAllByAggregateIdentifier(aggregateId);
        if (eventStream.get(eventStream.size() -1).getVersion() != expectedVersion && expectedVersion != -1) {
            throw new ConcurrencyException("Concurrency exception");
        }

        var version = expectedVersion;
        for (var event : events) {
            version++;
            event.setVersion(version);
            var eventModel = EventModel.builder()
                            .timestamp(new Date())
                            .aggregateIdentifier(aggregateId)
                            .aggregateType(AccountAggregate.class.getTypeName())
                            .version(version)
                            .eventType(event.getClass().getTypeName())
                            .eventData(event)
                                    .build();
            var persistedEvent = eventStoreRepository.save(eventModel);
            if (persistedEvent != null) {
                // producir un event para kafka

            }

        }
    }

    @Override
    public List<BaseEvent> getEvents(String aggregateId) {
        var eventStream = eventStoreRepository.findAllByAggregateIdentifier(aggregateId);
        if (eventStream.isEmpty()) {
            throw new AggregateNotFoundException("Aggregate not found");
        }
        return eventStream.stream().map(EventModel::getEventData).toList();
    }
}
