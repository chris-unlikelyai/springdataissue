package springdataissue

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.event.EventListener
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

// Schema:
//
// CREATE TABLE IF NOT EXISTS parent_entity
// (
//     id                 UUID NOT NULL DEFAULT random_uuid() PRIMARY KEY
// );
//
// CREATE TABLE IF NOT EXISTS child_entity
// (
//     id                 UUID NOT NULL DEFAULT random_uuid() PRIMARY KEY,
//     parent_entity      UUID NOT NULL REFERENCES parent_entity (id) ON DELETE CASCADE,
//     parent_entity_key  INT  NOT NULL
// );

@Table
data class ParentEntity(
  @MappedCollection val children: List<ChildEntity>,
  @Id val id: UUID? = null,
)

@Table
data class ChildEntity(@Id val id: UUID? = null)

interface EntityRepository : CrudRepository<ParentEntity, UUID>

@SpringBootApplication class SpringDataIssue

@Service
class Startup(private val entityRepository: EntityRepository) {
  @EventListener(ApplicationReadyEvent::class)
  fun onStartup() {
    val entity1 = entityRepository.save(ParentEntity(listOf(ChildEntity())))
    println("After initial save:            $entity1")

    val entity2 = entity1.copy(children = entity1.children + ChildEntity())
    println("Updated in memory:             $entity2")

    val entity3 = entityRepository.save(entity2)
    println("After saving updated entity:   $entity3")

    val entityE = entityRepository.findByIdOrNull(entity3.id!!)!!
    println("Updated entity loaded from DB: $entityE")
  }
}

fun main(args: Array<String>) {
  runApplication<SpringDataIssue>(*args)
}

// Result:
//   After initial save:            ParentEntity(children=[ChildEntity(id=542938e4-1941-40bb-852b-6b11052c5a3b)], id=c93a8ba8-0c54-4979-af25-d08f422803d7)
//   Updated in memory:             ParentEntity(children=[ChildEntity(id=542938e4-1941-40bb-852b-6b11052c5a3b), ChildEntity(id=null)], id=c93a8ba8-0c54-4979-af25-d08f422803d7)
//   After saving updated entity:   ParentEntity(children=[ChildEntity(id=a9686057-a17d-4626-b649-ab512155bfd3)], id=c93a8ba8-0c54-4979-af25-d08f422803d7)
//   Updated entity loaded from DB: ParentEntity(children=[ChildEntity(id=542938e4-1941-40bb-852b-6b11052c5a3b), ChildEntity(id=a9686057-a17d-4626-b649-ab512155bfd3)], id=c93a8ba8-0c54-4979-af25-d08f422803d7)
