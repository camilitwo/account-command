package com.banking.account_cmd.infrastructure;

import com.banking.account_cmd.domain.AccountAggregate;
import com.banking.account_cmd.domain.EventStoreRepository;
import com.banking.cqrs_core.events.BaseEvent;
import com.banking.cqrs_core.events.EventModel;
import com.banking.cqrs_core.exceptions.AggregateNotFoundException;
import com.banking.cqrs_core.exceptions.ConcurrencyException;
import com.banking.cqrs_core.infrastructure.EventStore;
import com.banking.cqrs_core.producers.EventProducer;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
public class AccountEventStore implements EventStore {

    private final EventStoreRepository eventStoreRepository;
    private final EventProducer eventProducer;

    public AccountEventStore(EventStoreRepository eventStoreRepository, EventProducer eventProducer) {
        this.eventStoreRepository = eventStoreRepository;
        this.eventProducer = eventProducer;
    }

    @Override
    public void save(String aggregateId, Iterable<BaseEvent> events, int expectedVersion) {
        var eventStream = eventStoreRepository.findAllByAggregateIdentifier(aggregateId);
        if (!eventStream.isEmpty() && eventStream.get(eventStream.size() -1).getVersion() != expectedVersion && expectedVersion != -1) {
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
            if (!persistedEvent.getId().isEmpty()) {
                this.eventProducer.produce(event.getClass().getSimpleName(), event);
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
