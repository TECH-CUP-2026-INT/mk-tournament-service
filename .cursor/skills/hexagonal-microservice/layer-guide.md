# Layer Examples

## domain/model — Pure domain class

```java
// No @Document, no @Entity, no Spring annotations
public class Tournament {
    private String id;
    private String name;
    private LocalDate startDate;
    private TournamentStatus status;

    // constructors, getters, business methods only
}
```

## domain/port/in — Use case interface

```java
public interface CreateTournamentUseCase {
    Tournament create(Tournament tournament);
}
```

## domain/port/out — Repository port interface

```java
public interface TournamentRepositoryPort {
    Tournament save(Tournament tournament);
    Optional<Tournament> findById(String id);
    List<Tournament> findAll();
}
```

## application/service — Use case implementation

```java
@Service // Only Spring annotation allowed here
@RequiredArgsConstructor
public class CreateTournamentService implements CreateTournamentUseCase {

    private final TournamentRepositoryPort repository; // depends on port, not implementation

    @Override
    public Tournament create(Tournament tournament) {
        // business logic here
        return repository.save(tournament);
    }
}
```

## infrastructure/rest — Controller (input adapter)

```java
@RestController
@RequestMapping("/api/v1/tournaments")
@RequiredArgsConstructor
public class TournamentController {

    private final CreateTournamentUseCase createTournamentUseCase;

    @PostMapping
    public ResponseEntity<TournamentResponse> create(@RequestBody @Valid CreateTournamentRequest request) {
        Tournament tournament = TournamentRestMapper.toDomain(request);
        Tournament created = createTournamentUseCase.create(tournament);
        return ResponseEntity.status(HttpStatus.CREATED).body(TournamentRestMapper.toResponse(created));
    }
}
```

## infrastructure/rest/dto — Request and Response DTOs

```java
// Request
public record CreateTournamentRequest(
    @NotBlank String name,
    @NotNull LocalDate startDate
) {}

// Response
public record TournamentResponse(
    String id,
    String name,
    LocalDate startDate,
    String status
) {}
```

## infrastructure/persistence — MongoDB Entity

```java
@Document(collection = "tournaments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TournamentEntity {

    @Id
    private String id;
    private String name;
    private LocalDate startDate;
    private String status;
}
```

## infrastructure/persistence — Spring Data Mongo repository

```java
public interface TournamentMongoRepository extends MongoRepository<TournamentEntity, String> {
}
```

## infrastructure/persistence — Repository adapter (output adapter)

```java
@Component
@RequiredArgsConstructor
public class TournamentRepositoryAdapter implements TournamentRepositoryPort {

    private final TournamentMongoRepository mongoRepository;

    @Override
    public Tournament save(Tournament tournament) {
        TournamentEntity entity = TournamentPersistenceMapper.toEntity(tournament);
        TournamentEntity saved = mongoRepository.save(entity);
        return TournamentPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Tournament> findById(String id) {
        return mongoRepository.findById(id).map(TournamentPersistenceMapper::toDomain);
    }

    @Override
    public List<Tournament> findAll() {
        return mongoRepository.findAll().stream()
            .map(TournamentPersistenceMapper::toDomain)
            .toList();
    }
}
```
